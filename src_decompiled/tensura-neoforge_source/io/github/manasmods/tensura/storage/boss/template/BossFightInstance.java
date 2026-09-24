package io.github.manasmods.tensura.storage.boss.template;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import io.github.manasmods.manascore.race.api.SpawnPointHelper;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.entity.human.CloneEntity;
import io.github.manasmods.tensura.entity.magic.barrier.BossBarrierEntity;
import io.github.manasmods.tensura.entity.template.subclass.IArenaBoss;
import io.github.manasmods.tensura.mixin.accessor.AccessorServerPlayerGameMode;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.boss.exit.IForceExitHandler;
import io.github.manasmods.tensura.storage.boss.exit.SpawnPointForceExit;
import io.github.manasmods.tensura.storage.player.ITensuraPlayer;
import io.github.manasmods.tensura.util.SubordinateHelper;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.Generated;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BossFightInstance {
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   private BlockPos center;
   private ResourceKey<Level> dimension;
   private double radius;
   @Nullable
   private BlockPos entrance;
   private ResourceLocation bossType = ResourceLocation.withDefaultNamespace("none");
   @Nullable
   private BlockPos bossPosition;
   private List<String> startCommands = new ArrayList<>();
   private List<String> successCommands = new ArrayList<>();
   private List<String> failCommands = new ArrayList<>();
   private int timer = 0;
   private int startDelay = 0;
   private int tickCount = 0;
   private int forceExitTick = 0;
   private int forceExitTimer = 0;
   private IForceExitHandler forceExitHandler = new SpawnPointForceExit();
   private boolean forceExitOnLeave = false;
   private int maxPlayer = 1;
   private Set<UUID> joinedPlayers = new LinkedHashSet<>();
   private BossFightInstance.PlayerCountHandler playerCountHandler = BossFightInstance.PlayerCountHandler.NORMAL;
   private GameType forcedGameMode = null;
   private String nextBossFight = null;
   private Set<ManasSkill> bannedAbilities = new HashSet<>();
   private boolean banTeleportation = false;
   private boolean barrierSealed = false;
   private boolean resetBoss = true;
   private boolean started = false;
   private boolean onHold = false;
   private boolean succeeded = false;
   private boolean builtin = false;

   public BossFightInstance(BlockPos center, ResourceKey<Level> dimension, double radius) {
      this.center = center;
      this.dimension = dimension;
      this.radius = radius;
   }

   public boolean hasRuntimeState() {
      return this.started || this.onHold || this.succeeded || this.builtin || !this.joinedPlayers.isEmpty() || this.tickCount > 0 || this.forceExitTick > 0;
   }

   public CompoundTag saveRuntimeStateToNBT() {
      CompoundTag tag = new CompoundTag();
      tag.putBoolean("started", this.started);
      tag.putBoolean("onHold", this.onHold);
      tag.putBoolean("succeeded", this.succeeded);
      tag.putBoolean("builtin", this.builtin);
      tag.putInt("tickCount", this.tickCount);
      tag.putInt("forceExitTick", this.forceExitTick);
      ListTag playerList = new ListTag();
      int i = 0;

      for (UUID uuid : this.joinedPlayers) {
         CompoundTag playerTag = new CompoundTag();
         playerTag.putString("player_" + i, uuid.toString());
         playerList.add(playerTag);
         i++;
      }

      tag.put("joinedPlayers", playerList);
      return tag;
   }

   public void loadRuntimeStateFromNBT(CompoundTag tag) {
      this.started = tag.getBoolean("started");
      this.onHold = tag.getBoolean("onHold");
      this.succeeded = tag.getBoolean("succeeded");
      this.builtin = tag.getBoolean("builtin");
      this.tickCount = tag.getInt("tickCount");
      this.forceExitTick = tag.getInt("forceExitTick");
      this.joinedPlayers.clear();
      if (tag.contains("joinedPlayers")) {
         ListTag playerList = tag.getList("joinedPlayers", 10);

         for (int i = 0; i < playerList.size(); i++) {
            this.joinedPlayers.add(UUID.fromString(playerList.getCompound(i).getString("player_" + i)));
         }
      }
   }

   public int getTotalTime() {
      return this.getTimer() + this.getStartDelay();
   }

   public void addStartCommand(String command) {
      this.startCommands.add(command);
   }

   public void removeStartCommand(boolean last) {
      if (last) {
         this.startCommands.removeLast();
      } else {
         this.startCommands.removeFirst();
      }
   }

   public void clearStartCommands() {
      this.startCommands.clear();
   }

   public void addSuccessCommand(String command) {
      this.successCommands.add(command);
   }

   public void removeSuccessCommand(boolean last) {
      if (last) {
         this.successCommands.removeLast();
      } else {
         this.successCommands.removeFirst();
      }
   }

   public void clearSuccessCommands() {
      this.successCommands.clear();
   }

   public void addFailCommand(String command) {
      this.failCommands.add(command);
   }

   public void removeFailCommand(boolean last) {
      if (last) {
         this.failCommands.removeLast();
      } else {
         this.failCommands.removeFirst();
      }
   }

   public void clearFailCommands() {
      this.failCommands.clear();
   }

   public void addBannedAbilities(ManasSkill... skill) {
      Collections.addAll(this.bannedAbilities, skill);
   }

   public void removeBannedAbilities(ManasSkill... skill) {
      this.bannedAbilities.removeAll(List.of(skill));
   }

   public void clearBannedAbilities() {
      this.bannedAbilities.clear();
   }

   public Component getDataMessage() {
      Component status = this.isStarted()
         ? Component.translatable("tensura.boss_fight.get.status.started").withStyle(ChatFormatting.GREEN)
         : (
            this.isOnHold()
               ? Component.translatable("tensura.boss_fight.get.status.on_hold").withStyle(ChatFormatting.YELLOW)
               : Component.translatable("tensura.boss_fight.get.status.not_started").withStyle(ChatFormatting.RED)
         );
      MutableComponent component = Component.translatable("tensura.boss_fight.get.status", new Object[]{status});
      component.append("\n");
      String center = "[" + this.getCenter().getX() + ", " + this.getCenter().getY() + ", " + this.getCenter().getZ() + "]";
      component.append(Component.translatable("tensura.boss_fight.get.position", new Object[]{Component.literal(center).withStyle(ChatFormatting.AQUA)}));
      component.append("\n");
      component.append(
         Component.translatable(
            "tensura.boss_fight.get.dimension", new Object[]{Component.literal(this.getDimension().location().toString()).withStyle(ChatFormatting.AQUA)}
         )
      );
      component.append("\n");
      component.append(
         Component.translatable(
            "tensura.boss_fight.get.radius", new Object[]{Component.literal(String.valueOf(this.getRadius())).withStyle(ChatFormatting.AQUA)}
         )
      );
      if (this.getEntrance() != null) {
         component.append("\n");
         String bossPos = "[" + this.getEntrance().getX() + ", " + this.getEntrance().getY() + ", " + this.getEntrance().getZ() + "]";
         component.append(Component.translatable("tensura.boss_fight.get.entrance", new Object[]{Component.literal(bossPos).withStyle(ChatFormatting.AQUA)}));
      }

      component.append("\n");
      component.append(
         Component.translatable(
            "tensura.boss_fight.get.max_player",
            new Object[]{
               Component.literal(String.valueOf(this.getJoinedPlayers().size())).withStyle(ChatFormatting.AQUA),
               Component.literal(String.valueOf(this.getMaxPlayer())).withStyle(ChatFormatting.RED)
            }
         )
      );
      component.append("\n");
      component.append(
         Component.translatable(
            "tensura.boss_fight.get.max_player.handler",
            new Object[]{Component.literal(this.getPlayerCountHandler().getSerializedName()).withStyle(ChatFormatting.AQUA)}
         )
      );
      if (this.getForcedGameMode() != null) {
         component.append("\n");
         component.append(
            Component.translatable(
               "tensura.boss_fight.get.game_mode",
               new Object[]{MutableComponent.create(this.getForcedGameMode().getShortDisplayName().getContents()).withStyle(ChatFormatting.AQUA)}
            )
         );
      }

      component.append("\n");
      Component ban = Component.translatable(this.isBanTeleportation() ? "tensura.message.enabled" : "tensura.message.disabled").withStyle(ChatFormatting.AQUA);
      component.append(Component.translatable("tensura.boss_fight.get.ban_teleportation", new Object[]{ban}));
      component.append("\n");
      Component barrier = Component.translatable(this.isBarrierSealed() ? "tensura.message.enabled" : "tensura.message.disabled")
         .withStyle(ChatFormatting.AQUA);
      component.append(Component.translatable("tensura.boss_fight.get.barrier_sealed", new Object[]{barrier}));
      component.append("\n");
      component.append(
         Component.translatable(
            "tensura.boss_fight.get.timer",
            new Object[]{
               Component.literal(String.valueOf(Math.max(this.tickCount - this.getStartDelay(), 0))).withStyle(ChatFormatting.AQUA),
               Component.literal(String.valueOf(this.getTimer())).withStyle(ChatFormatting.RED)
            }
         )
      );
      component.append("\n");
      component.append(
         Component.translatable(
            "tensura.boss_fight.get.start_delay",
            new Object[]{
               Component.literal(String.valueOf(this.tickCount)).withStyle(ChatFormatting.AQUA),
               Component.literal(String.valueOf(this.getStartDelay())).withStyle(ChatFormatting.RED)
            }
         )
      );
      component.append("\n");
      component.append(
         Component.translatable(
            "tensura.boss_fight.get.force_exit.timer",
            new Object[]{
               Component.literal(String.valueOf(this.forceExitTick)).withStyle(ChatFormatting.AQUA),
               Component.literal(String.valueOf(this.getForceExitTimer())).withStyle(ChatFormatting.RED)
            }
         )
      );
      component.append("\n");
      component.append(
         Component.translatable(
            "tensura.boss_fight.get.force_exit.handler", new Object[]{this.getForceExitHandler().getDataMessage().withStyle(ChatFormatting.AQUA)}
         )
      );
      component.append("\n");
      Component exitOnLeave = Component.translatable(this.isForceExitOnLeave() ? "tensura.message.enabled" : "tensura.message.disabled")
         .withStyle(ChatFormatting.AQUA);
      component.append(Component.translatable("tensura.boss_fight.get.force_exit.on_leave", new Object[]{exitOnLeave}));
      if (!this.getBossType().equals(ResourceLocation.withDefaultNamespace("none"))) {
         EntityType<?> entity = (EntityType<?>)BuiltInRegistries.ENTITY_TYPE.get(this.getBossType());
         component.append("\n");
         component.append(
            Component.translatable(
               "tensura.boss_fight.get.boss_type", new Object[]{MutableComponent.create(entity.getDescription().getContents()).withStyle(ChatFormatting.AQUA)}
            )
         );
      }

      if (this.getBossPosition() != null) {
         component.append("\n");
         String bossPos = "[" + this.getBossPosition().getX() + ", " + this.getBossPosition().getY() + ", " + this.getBossPosition().getZ() + "]";
         component.append(
            Component.translatable("tensura.boss_fight.get.boss_position", new Object[]{Component.literal(bossPos).withStyle(ChatFormatting.AQUA)})
         );
      }

      if (this.getNextBossFight() != null) {
         component.append("\n");
         component.append(
            Component.translatable("tensura.boss_fight.get.next_boss", new Object[]{Component.literal(this.getNextBossFight()).withStyle(ChatFormatting.AQUA)})
         );
      }

      component.append("\n");
      Component reset = Component.translatable(this.isResetBoss() ? "tensura.message.enabled" : "tensura.message.disabled").withStyle(ChatFormatting.AQUA);
      component.append(Component.translatable("tensura.boss_fight.get.reset_boss", new Object[]{reset}));
      if (!this.getStartCommands().isEmpty()) {
         component.append("\n");
         component.append(Component.translatable("tensura.boss_fight.get.start_commands"));

         for (String command : this.getStartCommands()) {
            component.append("\n");
            component.append(Component.translatable("tensura.boss_fight.get.start_line", new Object[]{command}).withStyle(ChatFormatting.AQUA));
         }
      }

      if (!this.getSuccessCommands().isEmpty()) {
         component.append("\n");
         component.append(Component.translatable("tensura.boss_fight.get.success_commands"));

         for (String command : this.getSuccessCommands()) {
            component.append("\n");
            component.append(Component.translatable("tensura.boss_fight.get.start_line", new Object[]{command}).withStyle(ChatFormatting.AQUA));
         }
      }

      if (!this.getFailCommands().isEmpty()) {
         component.append("\n");
         component.append(Component.translatable("tensura.boss_fight.get.fail_commands"));

         for (String command : this.getFailCommands()) {
            component.append("\n");
            component.append(Component.translatable("tensura.boss_fight.get.start_line", new Object[]{command}).withStyle(ChatFormatting.AQUA));
         }
      }

      if (!this.getBannedAbilities().isEmpty()) {
         MutableComponent skillComponent = null;

         for (ManasSkill manasSkill : this.getBannedAbilities()) {
            MutableComponent name = manasSkill.getName();
            if (manasSkill instanceof TensuraSkill skill) {
               name = skill.getColoredName();
            }

            if (name != null) {
               if (skillComponent == null) {
                  skillComponent = name;
               } else {
                  skillComponent = skillComponent.append(Component.literal(", ").withStyle(ChatFormatting.WHITE)).append(name);
               }
            }
         }

         if (skillComponent != null) {
            component.append("\n");
            component.append(Component.translatable("tensura.boss_fight.get.banned_abilities", new Object[]{skillComponent}));
         }
      }

      return component.withStyle(ChatFormatting.WHITE);
   }

   public static void teleportToEntrance(Entity entity, BossFightInstance instance) {
      BlockPos entrance = instance.getEntrance();
      if (entrance != null) {
         SpawnPointHelper.teleportToAcrossDimensions(
            entity, instance.getDimension(), entrance.getX(), entrance.getY(), entrance.getZ(), entity.getYRot(), entity.getXRot()
         );
         entity.lookAt(Anchor.EYES, instance.getCenter().getCenter());
      }
   }

   private void toggleBannedAbilities(LivingEntity entity) {
      for (ManasSkill manasSkill : this.getBannedAbilities()) {
         Optional<ManasSkillInstance> optional = SkillAPI.getSkillsFrom(entity).getSkill(manasSkill);
         if (!optional.isEmpty()) {
            ManasSkillInstance instance = optional.get();
            if (instance.isToggled()) {
               instance.setToggled(false);
               instance.onToggleOff(entity);
            }
         }
      }
   }

   public void startBossFight(ServerLevel level, String bossName, boolean nextFight) {
      if (!this.isStarted()) {
         this.started = true;
         this.forceLoadArea(level, true);
         if (this.getStartDelay() <= 0) {
            this.spawnBoss(level, bossName);
         } else {
            int timer = this.getStartDelay();

            for (UUID uuid : this.getJoinedPlayers()) {
               ServerPlayer player = (ServerPlayer)level.getPlayerByUUID(uuid);
               if (player != null) {
                  if (nextFight) {
                     player.displayClientMessage(
                        Component.translatable("tensura.boss_fight.next.start_delay", new Object[]{timer / 20.0F}).withStyle(ChatFormatting.GOLD), false
                     );
                  } else {
                     player.displayClientMessage(
                        Component.translatable("tensura.boss_fight.start_delay", new Object[]{timer / 20.0F}).withStyle(ChatFormatting.GOLD), false
                     );
                  }

                  this.saveBossName(player, bossName);
               }
            }
         }
      }
   }

   private void spawnBoss(ServerLevel level, String bossName) {
      float radius = (float)this.getRadius();
      float distance = Mth.sqrt(radius * radius * 2.0F);
      distance = Mth.sqrt(distance * distance + radius * radius);
      List<LivingEntity> entities = level.getEntitiesOfClass(
         LivingEntity.class,
         new AABB(this.getCenter()).inflate(distance),
         entity -> !entity.getType().equals(EntityType.PLAYER) && !(SubordinateHelper.getSubordinateOwner(entity) instanceof Player)
      );
      if (entities.isEmpty()) {
         if (!Objects.equals(this.getBossType(), ResourceLocation.withDefaultNamespace("none"))) {
            Entity entity = ((EntityType)BuiltInRegistries.ENTITY_TYPE.get(this.getBossType())).create(level);
            if (entity == null) {
               return;
            }

            BlockPos pos = this.getBossPosition() != null ? this.getBossPosition() : this.getCenter();
            entity.setPos(pos.getCenter());
            entity.lookAt(Anchor.EYES, this.getCenter().getCenter());
            if (entity instanceof Mob mob) {
               mob.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), MobSpawnType.EVENT, null);
            }

            level.addFreshEntity(entity);
            if (entity instanceof IArenaBoss boss) {
               boss.onSpawned(level, bossName, this);
            }
         }

         if (!this.getStartCommands().isEmpty()) {
            BlockPos pos = this.getBossPosition() != null ? this.getBossPosition() : this.getCenter();
            CommandSourceStack stack = level.getServer().createCommandSourceStack().withLevel(level).withPosition(pos.getCenter());

            for (String command : this.getStartCommands()) {
               level.getServer().getCommands().performPrefixedCommand(stack, command);
            }
         }
      }

      int timer = this.getTimer();

      for (UUID uuid : this.getJoinedPlayers()) {
         ServerPlayer player = (ServerPlayer)level.getPlayerByUUID(uuid);
         if (player != null) {
            if (timer > 0) {
               if (timer > 1200) {
                  player.displayClientMessage(
                     Component.translatable("tensura.boss_fight.started_timer.minute", new Object[]{timer / 1200.0F}).withStyle(ChatFormatting.GOLD), false
                  );
               } else {
                  player.displayClientMessage(
                     Component.translatable("tensura.boss_fight.started_timer", new Object[]{timer / 20.0F}).withStyle(ChatFormatting.GOLD), false
                  );
               }
            } else if (timer == 0) {
               player.displayClientMessage(Component.translatable("tensura.boss_fight.started").withStyle(ChatFormatting.GOLD), false);
            }

            this.toggleBannedAbilities(player);
            this.saveBossName(player, bossName);
         }
      }
   }

   public void stopBossFight(ServerLevel level, boolean success) {
      if (this.isStarted()) {
         this.started = false;
         this.tickCount = 0;
         this.forceExitTick = 0;
         int timer = this.getForceExitTimer();
         if (!success) {
            this.succeeded = false;
            float radius = (float)this.getRadius();
            float distance = Mth.sqrt(radius * radius * 2.0F);
            distance = Mth.sqrt(distance * distance + radius * radius);

            for (Projectile entity : level.getEntitiesOfClass(Projectile.class, new AABB(this.getCenter()).inflate(distance))) {
               entity.discard();
            }

            for (CloneEntity entity : level.getEntitiesOfClass(CloneEntity.class, new AABB(this.getCenter()).inflate(distance))) {
               entity.remove();
            }

            if (this.isResetBoss()) {
               for (LivingEntity entity : level.getEntitiesOfClass(
                  LivingEntity.class,
                  new AABB(this.getCenter()).inflate(distance),
                  entityx -> !entityx.getType().equals(EntityType.PLAYER) && !(SubordinateHelper.getSubordinateOwner(entityx) instanceof Player)
               )) {
                  entity.discard();
               }
            }

            if (this.getFailCommands().isEmpty()) {
               for (UUID uuid : this.getJoinedPlayers()) {
                  ServerPlayer player = level.getServer().getPlayerList().getPlayer(uuid);
                  if (player != null) {
                     if (timer > 0) {
                        if (this.getTimer() != -1) {
                           player.displayClientMessage(
                              Component.translatable("tensura.boss_fight.ended_timer", new Object[]{timer / 20.0F}).withStyle(ChatFormatting.GOLD), true
                           );
                        }
                     } else {
                        if (timer == 0) {
                           player.displayClientMessage(Component.translatable("tensura.boss_fight.ended").withStyle(ChatFormatting.GOLD), true);
                        }

                        this.resetGameMode(player);
                        TensuraStorages.getPlayerDataFrom(player).setPreviouslyInBossFight(null);
                     }
                  }
               }
            } else {
               for (UUID uuid : List.copyOf(this.getJoinedPlayers())) {
                  ServerPlayer player = level.getServer().getPlayerList().getPlayer(uuid);
                  if (player != null) {
                     if (timer > 0) {
                        if (this.getTimer() != -1) {
                           player.displayClientMessage(
                              Component.translatable("tensura.boss_fight.ended_timer", new Object[]{timer / 20.0F}).withStyle(ChatFormatting.GOLD), true
                           );
                        }
                     } else {
                        if (timer == 0) {
                           player.displayClientMessage(Component.translatable("tensura.boss_fight.ended").withStyle(ChatFormatting.GOLD), true);
                        }

                        this.resetGameMode(player);
                        TensuraStorages.getPlayerDataFrom(player).setPreviouslyInBossFight(null);
                     }

                     CommandSourceStack stack = level.getServer()
                        .createCommandSourceStack()
                        .withLevel(level)
                        .withPosition(player.position())
                        .withEntity(player)
                        .withPermission(4);

                     for (String command : this.getFailCommands()) {
                        level.getServer().getCommands().performPrefixedCommand(stack, command);
                     }
                  }
               }
            }

            if (timer > 0) {
               this.onHold = true;
            } else {
               this.getJoinedPlayers().clear();
               this.forceLoadArea(level, false);
            }
         } else {
            this.succeeded = true;
            float radius = (float)this.getRadius();
            float distance = Mth.sqrt(radius * radius * 2.0F);
            distance = Mth.sqrt(distance * distance + radius * radius);

            for (Projectile entity : level.getEntitiesOfClass(Projectile.class, new AABB(this.getCenter()).inflate(distance))) {
               entity.discard();
            }

            for (CloneEntity entity : level.getEntitiesOfClass(CloneEntity.class, new AABB(this.getCenter()).inflate(distance))) {
               entity.remove();
            }

            if (this.getSuccessCommands().isEmpty()) {
               for (UUID uuid : List.copyOf(this.getJoinedPlayers())) {
                  ServerPlayer player = level.getServer().getPlayerList().getPlayer(uuid);
                  if (player != null) {
                     if (timer > 0) {
                        if (this.getTimer() != -1) {
                           player.displayClientMessage(
                              Component.translatable("tensura.boss_fight.ended_timer", new Object[]{timer / 20.0F}).withStyle(ChatFormatting.GOLD), true
                           );
                        }
                     } else {
                        if (timer == 0 && this.getNextBossFight() == null) {
                           player.displayClientMessage(Component.translatable("tensura.boss_fight.ended").withStyle(ChatFormatting.GOLD), true);
                        }

                        this.resetGameMode(player);
                        TensuraStorages.getPlayerDataFrom(player).setPreviouslyInBossFight(null);
                     }
                  }
               }
            } else {
               for (UUID uuid : List.copyOf(this.getJoinedPlayers())) {
                  ServerPlayer player = level.getServer().getPlayerList().getPlayer(uuid);
                  if (player != null) {
                     if (timer > 0) {
                        if (this.getTimer() != -1) {
                           player.displayClientMessage(
                              Component.translatable("tensura.boss_fight.ended_timer", new Object[]{timer / 20.0F}).withStyle(ChatFormatting.GOLD), true
                           );
                        }
                     } else {
                        if (timer == 0 && this.getNextBossFight() == null) {
                           player.displayClientMessage(Component.translatable("tensura.boss_fight.ended").withStyle(ChatFormatting.GOLD), true);
                        }

                        this.resetGameMode(player);
                        TensuraStorages.getPlayerDataFrom(player).setPreviouslyInBossFight(null);
                     }

                     CommandSourceStack stack = level.getServer()
                        .createCommandSourceStack()
                        .withLevel(level)
                        .withPosition(player.position())
                        .withEntity(player)
                        .withPermission(4);

                     for (String command : this.getSuccessCommands()) {
                        level.getServer().getCommands().performPrefixedCommand(stack, command);
                     }
                  }
               }
            }

            if (timer > 0) {
               this.onHold = true;
            } else if (this.getNextBossFight() != null) {
               List<UUID> players = new ArrayList<>(this.getJoinedPlayers());
               this.getJoinedPlayers().clear();
               this.forceLoadArea(level, false);
               this.joinNextBossFight(level, players);
            } else {
               this.getJoinedPlayers().clear();
               this.forceLoadArea(level, false);
            }
         }
      }
   }

   private void joinNextBossFight(ServerLevel level, List<UUID> players) {
      if (this.getNextBossFight() != null) {
         ServerLevel overworld = level.getServer().overworld();
         IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
         if (bossFightHolder.getBossFights().containsKey(this.getNextBossFight())) {
            BossFightInstance instance = bossFightHolder.getBossFights().get(this.getNextBossFight());
            ServerLevel dimension = level.getServer().getLevel(instance.getDimension());

            for (UUID uuid : players) {
               ServerPlayer player = level.getServer().getPlayerList().getPlayer(uuid);
               if (player != null) {
                  instance.joinBossFight(player, dimension, this.getNextBossFight(), true);
                  player.playNotifySound(SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.0F);
               }
            }
         }
      }
   }

   public boolean joinBossFight(ServerPlayer player, ServerLevel level, String bossName, boolean nextFight) {
      if (this.isOnHold()) {
         player.playNotifySound((SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
         player.displayClientMessage(Component.translatable("tensura.boss_fight.full").withStyle(ChatFormatting.RED), false);
         return false;
      }

      Set<UUID> joinedPlayers = this.getJoinedPlayers();
      switch (this.getPlayerCountHandler()) {
         case NORMAL:
            if (joinedPlayers.isEmpty()) {
               this.getJoinedPlayers().add(player.getUUID());
               teleportToEntrance(player, this);
               this.startBossFight(level, bossName, nextFight);
            } else {
               if (joinedPlayers.size() >= this.getMaxPlayer()) {
                  player.playNotifySound((SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
                  player.displayClientMessage(Component.translatable("tensura.boss_fight.full").withStyle(ChatFormatting.RED), false);
                  return false;
               }

               this.getJoinedPlayers().add(player.getUUID());
               teleportToEntrance(player, this);
               this.toggleBannedAbilities(player);
               this.saveBossName(player, bossName);
            }
            break;
         case START_WHEN_MET:
            if (joinedPlayers.isEmpty()) {
               this.getJoinedPlayers().add(player.getUUID());
               teleportToEntrance(player, this);
               if (this.getJoinedPlayers().size() >= this.getMaxPlayer()) {
                  this.startBossFight(level, bossName, nextFight);
               }
            } else {
               if (joinedPlayers.size() >= this.getMaxPlayer()) {
                  player.playNotifySound((SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
                  player.displayClientMessage(Component.translatable("tensura.boss_fight.full").withStyle(ChatFormatting.RED), false);
                  return false;
               }

               this.getJoinedPlayers().add(player.getUUID());
               teleportToEntrance(player, this);
               if (this.getJoinedPlayers().size() >= this.getMaxPlayer()) {
                  this.startBossFight(level, bossName, nextFight);
               }
            }
      }

      return true;
   }

   public void leaveBossFight(ServerPlayer player, ServerLevel level) {
      this.getJoinedPlayers().remove(player.getUUID());
      if (this.getJoinedPlayers().isEmpty()) {
         this.stopBossFight(level, false);
      }

      this.resetGameMode(player);
      TensuraStorages.getPlayerDataFrom(player).setPreviouslyInBossFight(null);
   }

   public void tick(MinecraftServer server, String bossName) {
      if (!this.isStarted()) {
         if (this.isOnHold()) {
            ServerLevel fightLevel = server.getLevel(this.getDimension());
            if (fightLevel == null) {
               return;
            }

            this.forceExitTick++;
            if (this.forceExitTick >= this.getForceExitTimer()) {
               this.onHold = false;
               if (this.succeeded && this.getNextBossFight() != null) {
                  List<UUID> players = new ArrayList<>(this.getJoinedPlayers());

                  for (UUID uuid : players) {
                     ServerPlayer player = server.getPlayerList().getPlayer(uuid);
                     if (player != null) {
                        this.resetGameMode(player);
                        TensuraStorages.getPlayerDataFrom(player).setPreviouslyInBossFight(null);
                        this.getJoinedPlayers().remove(uuid);
                     }
                  }

                  this.forceLoadArea(fightLevel, false);
                  this.joinNextBossFight(fightLevel, players);
               } else {
                  for (UUID uuid : List.copyOf(this.getJoinedPlayers())) {
                     ServerPlayer player = server.getPlayerList().getPlayer(uuid);
                     if (player != null) {
                        this.getJoinedPlayers().remove(uuid);
                        this.getForceExitHandler().apply(player, 0.0);
                        player.displayClientMessage(
                           Component.translatable("tensura.boss_fight.teleport_away").setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GREEN)), true
                        );
                        this.resetGameMode(player);
                        TensuraStorages.getPlayerDataFrom(player).setPreviouslyInBossFight(null);
                     }
                  }

                  this.forceLoadArea(fightLevel, false);
               }

               return;
            }

            if (this.getForceExitTimer() % 20 != 0) {
               return;
            }

            float radius = (float)this.getRadius();
            float distance = Mth.sqrt(radius * radius * 2.0F);
            distance = Mth.sqrt(distance * distance + radius * radius);

            for (UUID uuid : List.copyOf(this.getJoinedPlayers())) {
               ServerPlayer player = server.getPlayerList().getPlayer(uuid);
               if (player == null
                  || player.level() != fightLevel
                  || !player.isAlive()
                  || this.getCenter().distToCenterSqr(player.position()) > distance * distance) {
                  this.getJoinedPlayers().remove(uuid);
                  if (player != null) {
                     this.resetGameMode(player);
                     if (this.isForceExitOnLeave() && this.getForceExitHandler() != null) {
                        this.getForceExitHandler().apply(player, 0.0);
                     }

                     TensuraStorages.getPlayerDataFrom(player).setPreviouslyInBossFight(null);
                  }
               } else if (this.forceExitTick > 30) {
                  int time = this.getForceExitTimer() + 20 - this.forceExitTick;
                  if (time < 1200) {
                     if (time == 600) {
                        player.displayClientMessage(
                           Component.translatable("tensura.boss_fight.ended_timer.count", new Object[]{30})
                              .setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)),
                           true
                        );
                     } else if (time <= 220) {
                        player.displayClientMessage(
                           Component.translatable("tensura.boss_fight.timer.count", new Object[]{time / 20}).withStyle(ChatFormatting.RED), true
                        );
                     }
                  }
               }
            }

            if (this.getJoinedPlayers().isEmpty()) {
               this.onHold = false;
               this.forceLoadArea(fightLevel, false);
            }
         }
      } else {
         ServerLevel fightLevel = server.getLevel(this.getDimension());
         if (fightLevel != null) {
            boolean tickCheck = server.getTickCount() % 20 == 0;
            if (this.getStartDelay() > 0 && this.tickCount < this.getStartDelay()) {
               this.tickCount++;
               tickCheck = this.tickCount % 20 == 0;
               if (this.tickCount >= this.getStartDelay()) {
                  this.spawnBoss(fightLevel, bossName);
                  return;
               }
            } else if (this.getTimer() > 0) {
               this.tickCount++;
               tickCheck = this.tickCount % 20 == 0;
               if (this.tickCount >= this.getTotalTime()) {
                  this.stopBossFight(fightLevel, false);
                  return;
               }
            }

            if (tickCheck) {
               float radius = (float)this.getRadius();
               float distance = Mth.sqrt(radius * radius * 2.0F);
               distance = Mth.sqrt(distance * distance + radius * radius);

               for (UUID uuid : List.copyOf(this.getJoinedPlayers())) {
                  ServerPlayer player = server.getPlayerList().getPlayer(uuid);
                  if (player != null
                     && player.level() == fightLevel
                     && player.isAlive()
                     && !(this.getCenter().distToCenterSqr(player.position()) > distance * distance)) {
                     if (this.isBanTeleportation()) {
                        player.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.SPATIAL_BLOCKADE), 40, 9, true, false, true));
                     }

                     if (this.getStartDelay() > 0 && this.tickCount < this.getStartDelay()) {
                        int time = this.getStartDelay() - this.tickCount;
                        player.displayClientMessage(
                           Component.translatable("tensura.boss_fight.timer.count", new Object[]{time / 20})
                              .setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)),
                           true
                        );
                     } else if (this.getTimer() > 0) {
                        int time = this.getTotalTime() - this.tickCount;
                        if (time < 1200) {
                           if (time == 600) {
                              player.displayClientMessage(
                                 Component.translatable("tensura.boss_fight.started_timer.count", new Object[]{30}).withStyle(ChatFormatting.RED), true
                              );
                           } else if (time <= 220) {
                              player.displayClientMessage(
                                 Component.translatable("tensura.boss_fight.timer.count", new Object[]{time / 20})
                                    .setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)),
                                 true
                              );
                           }
                        } else if (time <= 3600) {
                           if (time % 1200 == 0) {
                              player.displayClientMessage(
                                 Component.translatable("tensura.boss_fight.started_timer.minute.count", new Object[]{time / 1200})
                                    .setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)),
                                 true
                              );
                           }
                        } else if (time % 6000 == 0) {
                           player.displayClientMessage(
                              Component.translatable("tensura.boss_fight.started_timer.minute.count", new Object[]{time / 1200})
                                 .setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_AQUA)),
                              true
                           );
                        }
                     }
                  } else {
                     this.getJoinedPlayers().remove(uuid);
                     if (player != null) {
                        this.resetGameMode(player);
                        if (this.isForceExitOnLeave() && this.getForceExitHandler() != null) {
                           this.getForceExitHandler().apply(player, 0.0);
                        }

                        TensuraStorages.getPlayerDataFrom(player).setPreviouslyInBossFight(null);
                     }
                  }
               }

               if (this.getJoinedPlayers().isEmpty()) {
                  this.stopBossFight(fightLevel, false);
               } else if (this.getStartDelay() <= 0 || this.tickCount >= this.getStartDelay()) {
                  List<LivingEntity> entities = fightLevel.getEntitiesOfClass(
                     LivingEntity.class,
                     new AABB(this.getCenter()).inflate(distance),
                     entity -> !entity.getType().equals(EntityType.PLAYER) && !(SubordinateHelper.getSubordinateOwner(entity) instanceof Player)
                  );
                  if (entities.isEmpty()) {
                     this.stopBossFight(fightLevel, true);
                  }
               }
            }
         }
      }
   }

   private void forceLoadArea(ServerLevel level, boolean force) {
      if (this.isBarrierSealed()) {
         int time = -1;
         if (this.getTimer() > 0) {
            time = this.getTotalTime() + this.getForceExitTimer();
         }

         BossBarrierEntity barrier = new BossBarrierEntity(level);
         barrier.setLife(time);
         barrier.setInstance(this);
         barrier.setSize((float)this.getRadius() - 1.0F);
         barrier.setPos(this.getCenter().getCenter().add(0.0, -barrier.getSize(), 0.0));
         level.addFreshEntity(barrier);
      }

      double radius = this.getRadius();
      BlockPos center = this.getCenter();
      int minX = (int)(center.getX() - radius);
      int maxX = (int)(center.getX() + radius);
      int minZ = (int)(center.getZ() - radius);
      int maxZ = (int)(center.getZ() + radius);
      int minChunkX = minX >> 4;
      int maxChunkX = maxX >> 4;
      int minChunkZ = minZ >> 4;
      int maxChunkZ = maxZ >> 4;

      for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
         for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
            level.setChunkForced(chunkX, chunkZ, force);
         }
      }
   }

   private void saveBossName(ServerPlayer player, String bossName) {
      if (this.getForcedGameMode() != null && !player.isCreative() && !player.isSpectator()) {
         if (player.gameMode.getGameModeForPlayer() != this.getForcedGameMode()) {
            ((AccessorServerPlayerGameMode)player.gameMode).setPreviousGameModeForPlayer(player.gameMode.getGameModeForPlayer());
            player.setGameMode(this.getForcedGameMode());
         }
      } else if (this.getForceExitHandler() == null) {
         return;
      }

      ITensuraPlayer data = TensuraStorages.getPlayerDataFrom(player);
      data.setPreviouslyInBossFight(bossName);
      data.markDirty();
   }

   public void resetGameMode(ServerPlayer player) {
      if (this.getForcedGameMode() != null) {
         if (player.gameMode.getGameModeForPlayer() == this.getForcedGameMode()) {
            if (player.gameMode.getPreviousGameModeForPlayer() == null) {
               player.setGameMode(GameType.SURVIVAL);
            } else {
               player.setGameMode(player.gameMode.getPreviousGameModeForPlayer());
            }

            ((AccessorServerPlayerGameMode)player.gameMode).setPreviousGameModeForPlayer(null);
         }
      }
   }

   public JsonObject toJson() {
      JsonObject json = new JsonObject();
      JsonObject centerObj = new JsonObject();
      centerObj.addProperty("x", this.center.getX());
      centerObj.addProperty("y", this.center.getY());
      centerObj.addProperty("z", this.center.getZ());
      json.add("center", centerObj);
      json.addProperty("dimension", this.dimension.location().toString());
      json.addProperty("radius", this.radius);
      if (this.entrance != null) {
         JsonObject entranceObj = new JsonObject();
         entranceObj.addProperty("x", this.entrance.getX());
         entranceObj.addProperty("y", this.entrance.getY());
         entranceObj.addProperty("z", this.entrance.getZ());
         json.add("entrance", entranceObj);
      }

      json.addProperty("bossType", this.bossType.toString());
      if (this.bossPosition != null) {
         JsonObject bossPos = new JsonObject();
         bossPos.addProperty("x", this.bossPosition.getX());
         bossPos.addProperty("y", this.bossPosition.getY());
         bossPos.addProperty("z", this.bossPosition.getZ());
         json.add("bossPosition", bossPos);
      }

      json.addProperty("timer", this.timer);
      json.addProperty("startDelay", this.startDelay);
      json.addProperty("forceExitTimer", this.forceExitTimer);
      json.add("forceExitHandler", this.forceExitHandler.toJson());
      json.addProperty("forceExitOnLeave", this.forceExitOnLeave);
      if (this.nextBossFight != null) {
         json.addProperty("nextBossFight", this.nextBossFight);
      }

      json.addProperty("maxPlayer", this.maxPlayer);
      json.addProperty("playerCountHandler", this.playerCountHandler.getSerializedName());
      if (this.forcedGameMode != null) {
         json.addProperty("forcedGameMode", this.forcedGameMode.getName());
      }

      if (!this.startCommands.isEmpty()) {
         JsonArray startCommands = new JsonArray();
         this.startCommands.forEach(startCommands::add);
         json.add("startCommands", startCommands);
      }

      if (!this.successCommands.isEmpty()) {
         JsonArray successCommands = new JsonArray();
         this.successCommands.forEach(successCommands::add);
         json.add("successCommands", successCommands);
      }

      if (!this.failCommands.isEmpty()) {
         JsonArray failCommands = new JsonArray();
         this.failCommands.forEach(failCommands::add);
         json.add("failCommands", failCommands);
      }

      if (!this.bannedAbilities.isEmpty()) {
         JsonArray bannedArray = new JsonArray();
         this.bannedAbilities.forEach(skill -> bannedArray.add(skill.getRegistryName().toString()));
         json.add("bannedAbilities", bannedArray);
      }

      json.addProperty("banTeleportation", this.banTeleportation);
      json.addProperty("barrierSealed", this.barrierSealed);
      json.addProperty("resetBoss", this.resetBoss);
      json.addProperty("started", this.started);
      json.addProperty("onHold", this.onHold);
      json.addProperty("tickCount", this.tickCount);
      json.addProperty("forceExitTick", this.forceExitTick);
      return json;
   }

   public static BossFightInstance fromJson(JsonObject json) {
      JsonObject centerObj = json.getAsJsonObject("center");
      BlockPos center = new BlockPos(centerObj.get("x").getAsInt(), centerObj.get("y").getAsInt(), centerObj.get("z").getAsInt());
      ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(json.get("dimension").getAsString()));
      BossFightInstance instance = new BossFightInstance(center, dimension, json.get("radius").getAsDouble());
      if (json.has("entrance")) {
         JsonObject entranceObj = json.getAsJsonObject("entrance");
         instance.entrance = new BlockPos(entranceObj.get("x").getAsInt(), entranceObj.get("y").getAsInt(), entranceObj.get("z").getAsInt());
      }

      instance.bossType = ResourceLocation.parse(json.get("bossType").getAsString());
      if (json.has("bossPosition")) {
         JsonObject bossPos = json.getAsJsonObject("bossPosition");
         instance.bossPosition = new BlockPos(bossPos.get("x").getAsInt(), bossPos.get("y").getAsInt(), bossPos.get("z").getAsInt());
      }

      instance.timer = json.get("timer").getAsInt();
      instance.startDelay = json.get("startDelay").getAsInt();
      instance.forceExitTimer = json.get("forceExitTimer").getAsInt();
      if (json.has("forceExitHandler")) {
         instance.forceExitHandler = IForceExitHandler.fromJson(json.getAsJsonObject("forceExitHandler"));
      }

      instance.forceExitOnLeave = json.get("forceExitOnLeave").getAsBoolean();
      if (json.has("nextBossFight")) {
         instance.nextBossFight = json.get("nextBossFight").getAsString();
      }

      instance.maxPlayer = json.get("maxPlayer").getAsInt();
      instance.playerCountHandler = BossFightInstance.PlayerCountHandler.CODEC
         .parse(JsonOps.INSTANCE, new JsonPrimitive(json.get("playerCountHandler").getAsString()))
         .result()
         .orElse(BossFightInstance.PlayerCountHandler.NORMAL);
      if (json.has("forcedGameMode")) {
         instance.forcedGameMode = GameType.byName(json.get("forcedGameMode").getAsString());
      }

      if (json.has("startCommands")) {
         JsonArray startCommands = json.getAsJsonArray("startCommands");
         startCommands.forEach(cmd -> instance.startCommands.add(cmd.getAsString()));
      }

      if (json.has("successCommands")) {
         JsonArray successCommands = json.getAsJsonArray("successCommands");
         successCommands.forEach(cmd -> instance.successCommands.add(cmd.getAsString()));
      }

      if (json.has("failCommands")) {
         JsonArray failCommands = json.getAsJsonArray("failCommands");
         failCommands.forEach(cmd -> instance.failCommands.add(cmd.getAsString()));
      }

      if (json.has("bannedAbilities")) {
         JsonArray bannedArray = json.getAsJsonArray("bannedAbilities");
         bannedArray.forEach(elem -> {
            ManasSkill skill = (ManasSkill)SkillAPI.getSkillRegistry().get(ResourceLocation.parse(elem.getAsString()));
            if (skill != null) {
               instance.bannedAbilities.add(skill);
            }
         });
      }

      instance.banTeleportation = json.get("banTeleportation").getAsBoolean();
      instance.barrierSealed = json.get("barrierSealed").getAsBoolean();
      instance.resetBoss = json.get("resetBoss").getAsBoolean();
      if (json.has("started")) {
         instance.started = json.get("started").getAsBoolean();
      }

      if (json.has("onHold")) {
         instance.onHold = json.get("onHold").getAsBoolean();
      }

      if (json.has("tickCount")) {
         instance.tickCount = json.get("tickCount").getAsInt();
      }

      if (json.has("forceExitTick")) {
         instance.forceExitTick = json.get("forceExitTick").getAsInt();
      }

      return instance;
   }

   public void saveToFile(Path path) throws IOException {
      Files.createDirectories(path.getParent());

      try (Writer writer = Files.newBufferedWriter(path)) {
         GSON.toJson(this.toJson(), writer);
      }
   }

   public static BossFightInstance loadFromFile(Path path) throws IOException {
      try (Reader reader = Files.newBufferedReader(path)) {
         JsonObject json = (JsonObject)GSON.fromJson(reader, JsonObject.class);
         return fromJson(json);
      }
   }

   @Generated
   public BlockPos getCenter() {
      return this.center;
   }

   @Generated
   public void setCenter(BlockPos center) {
      this.center = center;
   }

   @Generated
   public ResourceKey<Level> getDimension() {
      return this.dimension;
   }

   @Generated
   public void setDimension(ResourceKey<Level> dimension) {
      this.dimension = dimension;
   }

   @Generated
   public double getRadius() {
      return this.radius;
   }

   @Generated
   public void setRadius(double radius) {
      this.radius = radius;
   }

   @Nullable
   @Generated
   public BlockPos getEntrance() {
      return this.entrance;
   }

   @Generated
   public void setEntrance(@Nullable BlockPos entrance) {
      this.entrance = entrance;
   }

   @Generated
   public ResourceLocation getBossType() {
      return this.bossType;
   }

   @Generated
   public void setBossType(ResourceLocation bossType) {
      this.bossType = bossType;
   }

   @Nullable
   @Generated
   public BlockPos getBossPosition() {
      return this.bossPosition;
   }

   @Generated
   public void setBossPosition(@Nullable BlockPos bossPosition) {
      this.bossPosition = bossPosition;
   }

   @Generated
   public List<String> getStartCommands() {
      return this.startCommands;
   }

   @Generated
   public List<String> getSuccessCommands() {
      return this.successCommands;
   }

   @Generated
   public List<String> getFailCommands() {
      return this.failCommands;
   }

   @Generated
   public int getTimer() {
      return this.timer;
   }

   @Generated
   public void setTimer(int timer) {
      this.timer = timer;
   }

   @Generated
   public int getStartDelay() {
      return this.startDelay;
   }

   @Generated
   public void setStartDelay(int startDelay) {
      this.startDelay = startDelay;
   }

   @Generated
   public int getForceExitTimer() {
      return this.forceExitTimer;
   }

   @Generated
   public void setForceExitTimer(int forceExitTimer) {
      this.forceExitTimer = forceExitTimer;
   }

   @Generated
   public IForceExitHandler getForceExitHandler() {
      return this.forceExitHandler;
   }

   @Generated
   public void setForceExitHandler(IForceExitHandler forceExitHandler) {
      this.forceExitHandler = forceExitHandler;
   }

   @Generated
   public boolean isForceExitOnLeave() {
      return this.forceExitOnLeave;
   }

   @Generated
   public void setForceExitOnLeave(boolean forceExitOnLeave) {
      this.forceExitOnLeave = forceExitOnLeave;
   }

   @Generated
   public int getMaxPlayer() {
      return this.maxPlayer;
   }

   @Generated
   public void setMaxPlayer(int maxPlayer) {
      this.maxPlayer = maxPlayer;
   }

   @Generated
   public Set<UUID> getJoinedPlayers() {
      return this.joinedPlayers;
   }

   @Generated
   public BossFightInstance.PlayerCountHandler getPlayerCountHandler() {
      return this.playerCountHandler;
   }

   @Generated
   public void setPlayerCountHandler(BossFightInstance.PlayerCountHandler playerCountHandler) {
      this.playerCountHandler = playerCountHandler;
   }

   @Generated
   public GameType getForcedGameMode() {
      return this.forcedGameMode;
   }

   @Generated
   public void setForcedGameMode(GameType forcedGameMode) {
      this.forcedGameMode = forcedGameMode;
   }

   @Generated
   public String getNextBossFight() {
      return this.nextBossFight;
   }

   @Generated
   public void setNextBossFight(String nextBossFight) {
      this.nextBossFight = nextBossFight;
   }

   @Generated
   public Set<ManasSkill> getBannedAbilities() {
      return this.bannedAbilities;
   }

   @Generated
   public boolean isBanTeleportation() {
      return this.banTeleportation;
   }

   @Generated
   public void setBanTeleportation(boolean banTeleportation) {
      this.banTeleportation = banTeleportation;
   }

   @Generated
   public boolean isBarrierSealed() {
      return this.barrierSealed;
   }

   @Generated
   public void setBarrierSealed(boolean barrierSealed) {
      this.barrierSealed = barrierSealed;
   }

   @Generated
   public boolean isResetBoss() {
      return this.resetBoss;
   }

   @Generated
   public void setResetBoss(boolean resetBoss) {
      this.resetBoss = resetBoss;
   }

   @Generated
   public boolean isStarted() {
      return this.started;
   }

   @Generated
   public boolean isOnHold() {
      return this.onHold;
   }

   @Generated
   public boolean isSucceeded() {
      return this.succeeded;
   }

   @Generated
   public boolean isBuiltin() {
      return this.builtin;
   }

   @Generated
   public void setBuiltin(boolean builtin) {
      this.builtin = builtin;
   }

   public enum PlayerCountHandler implements StringRepresentable {
      NORMAL("normal"),
      START_WHEN_MET("start_when_met");

      public static final Codec<BossFightInstance.PlayerCountHandler> CODEC = StringRepresentable.fromEnum(BossFightInstance.PlayerCountHandler::values);
      private final String namespace;

      @NotNull
      public String getSerializedName() {
         return this.namespace;
      }

      @Generated
      PlayerCountHandler(final String namespace) {
         this.namespace = namespace;
      }
   }
}
