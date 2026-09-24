package io.github.manasmods.tensura.handler;

import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.event.events.common.PlayerEvent.ChangeDimension;
import dev.architectury.event.events.common.TickEvent.Player;
import dev.architectury.networking.NetworkManager;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.manascore.skill.api.EntityEvents;
import io.github.manasmods.manascore.skill.api.EntityEvents.ProjectileHitEvent;
import io.github.manasmods.manascore.skill.api.EntityEvents.ProjectileHitResult;
import io.github.manasmods.tensura.Tensura;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.block.LabyrinthPortal;
import io.github.manasmods.tensura.data.TensuraRaceTags;
import io.github.manasmods.tensura.entity.monster.ElementalColossusEntity;
import io.github.manasmods.tensura.event.TensuraLevelEvents;
import io.github.manasmods.tensura.network.s2c.SendBooleanGameruleUpdatePayload;
import io.github.manasmods.tensura.network.s2c.SendIntegerGameruleUpdatePayload;
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import io.github.manasmods.tensura.registry.dimension.TensuraDimensions;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.boss.template.IBossFightHolder;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.storage.labyrinth.ILabyrinth;
import io.github.manasmods.tensura.storage.labyrinth.LabyrinthStorage;
import io.github.manasmods.tensura.storage.spirit.SpiritStorage;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import io.github.manasmods.tensura.world.TensuraGameRules;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class LabyrinthHandler {
   public static void init() {
      TensuraLevelEvents.LEVEL_PREPARED.register((TensuraLevelEvents.LevelPreparedEvent)(eventLevel, dimensionType) -> {
         if (eventLevel instanceof ServerLevel level) {
            if (dimensionType.is(TensuraDimensions.LABYRINTH_TYPE)) {
               ILabyrinth data = TensuraStorages.getLabyrinthFrom(level);
               if (data != null && !data.isLoaded()) {
                  LabyrinthStorage.generateStructures(level, data, false);
               }
            } else if (dimensionType.is(TensuraDimensions.BOSS_AREA_TYPE)) {
               IBossFightHolder data = TensuraStorages.getBossFightHolder(level.getServer().overworld());
               if (data != null && !data.isLoaded()) {
                  placeGazelArena(level);
               }
            }
         }
      });
      PlayerEvent.CHANGE_DIMENSION
         .register(
            (ChangeDimension)(player, oldDimension, newDimension) -> {
               if (!player.level().isClientSide()) {
                  if (oldDimension.equals(TensuraDimensions.LABYRINTH)) {
                     LabyrinthPortal.switchGameMode(player, true);
                  } else if (newDimension.equals(TensuraDimensions.LABYRINTH)) {
                     LabyrinthPortal.switchGameMode(player, false);
                  }

                  NetworkManager.sendToPlayer(
                     player,
                     new SendIntegerGameruleUpdatePayload(
                        SendIntegerGameruleUpdatePayload.GameruleKey.AWAKEN_SOUL, player.level().getGameRules().getInt(TensuraGameRules.DEMON_LORD_AWAKEN)
                     )
                  );
                  NetworkManager.sendToPlayer(
                     player,
                     new SendIntegerGameruleUpdatePayload(
                        SendIntegerGameruleUpdatePayload.GameruleKey.RESET_PER_SKILL_LOCK,
                        player.level().getGameRules().getInt(TensuraGameRules.RESET_PER_SKILL_LOCK)
                     )
                  );
                  NetworkManager.sendToPlayer(
                     player,
                     new SendIntegerGameruleUpdatePayload(
                        SendIntegerGameruleUpdatePayload.GameruleKey.RESET_INCOMPLETE_PENALTY,
                        player.level().getGameRules().getInt(TensuraGameRules.RESET_INCOMPLETE_PENALTY)
                     )
                  );
                  NetworkManager.sendToPlayer(
                     player,
                     new SendBooleanGameruleUpdatePayload(
                        SendBooleanGameruleUpdatePayload.GameruleKey.PLAYER_MANUAL_DODGING,
                        player.level().getGameRules().getBoolean(TensuraGameRules.PLAYER_MANUAL_DODGING)
                     )
                  );
                  NetworkManager.sendToPlayer(
                     player,
                     new SendBooleanGameruleUpdatePayload(
                        SendBooleanGameruleUpdatePayload.GameruleKey.TENSURA_NAME,
                        player.level().getGameRules().getBoolean(TensuraGameRules.TENSURA_DISPLAY_NAME)
                     )
                  );
                  NetworkManager.sendToPlayer(
                     player,
                     new SendBooleanGameruleUpdatePayload(
                        SendBooleanGameruleUpdatePayload.GameruleKey.DISABLE_NULLIFICATION,
                        player.level().getGameRules().getBoolean(TensuraGameRules.DISABLE_NULLIFICATION)
                     )
                  );
                  Optional<ManasRaceInstance> optional = RaceAPI.getRaceFrom(player).getRace();
                  if (!optional.isEmpty() && optional.get().is(TensuraRaceTags.LIMITED_EP_IN_CENTRAL)) {
                     if (SkillUtils.inSpiritualWorld(oldDimension) && !SkillUtils.inSpiritualWorld(newDimension)) {
                        IExistence existence = TensuraStorages.getExistenceFrom(player);
                        if (existence.getName() != null || existence.isTrueDemonLord() || existence.isTrueHero()) {
                           return;
                        }

                        EnergyHelper.applySpiritualEPLimit(player, existence);
                     } else if (!SkillUtils.inSpiritualWorld(oldDimension) && SkillUtils.inSpiritualWorld(newDimension)) {
                        EnergyHelper.removeSpiritualEPLimit(player);
                     }
                  }
               }
            }
         );
      TickEvent.PLAYER_POST.register((Player)eventPlayer -> {
         if (eventPlayer instanceof ServerPlayer player) {
            if (player.tickCount % 20 == 0) {
               if (player.level().dimension().equals(TensuraDimensions.LABYRINTH)) {
                  ILabyrinth data = TensuraStorages.getLabyrinthFrom(player.level());
                  if (data != null) {
                     playerTick(player, data);
                  }
               }
            }
         }
      });
      EntityEvents.PROJECTILE_HIT
         .register(
            (ProjectileHitEvent)(hitResult, projectile, deflection, result) -> {
               if (hitResult.getType() == Type.BLOCK) {
                  Level level = projectile.level();
                  BlockPos blockPos = ((BlockHitResult)hitResult).getBlockPos();
                  if (level.getBlockState(blockPos).getBlock() == TensuraBlocks.LABYRINTH_BARRIER_BLOCK.get()) {
                     projectile.setOwner(null);
                     projectile.setPos(hitResult.getLocation());
                     projectile.setDeltaMovement(projectile.getDeltaMovement().reverse());
                     result.set(ProjectileHitResult.PASS);
                     deflection.set(ProjectileDeflection.REVERSE);
                     projectile.level()
                        .playSound(
                           null,
                           projectile.getX(),
                           projectile.getY(),
                           projectile.getZ(),
                           (SoundEvent)TensuraSoundEvents.REFLECTION.get(),
                           TensuraSkill.ABILITY_SOUND,
                           1.0F,
                           1.0F
                        );
                  }
               }
            }
         );
   }

   private static void playerTick(ServerPlayer player, ILabyrinth labyrinth) {
      Level level = player.level();
      if (labyrinth.isColossusSpawned()) {
         if (player.tickCount % 600 != 0) {
            return;
         }

         if (!level.getGameRules().getBoolean(TensuraGameRules.COLOSSUS_RESPAWN)) {
            return;
         }

         Vec3 colossusPos = labyrinth.getColossusPos();
         double radius = labyrinth.getAreaRadius();
         if (player.distanceToSqr(colossusPos) > radius * radius) {
            return;
         }

         AABB aabb = new AABB(ObjectSelectionHelper.getBlockPos(colossusPos)).inflate(radius);
         List<ElementalColossusEntity> list = level.getEntitiesOfClass(ElementalColossusEntity.class, aabb, entity -> entity.isAlive() && !entity.isTame());
         if (list.isEmpty()) {
            labyrinth.setColossusSpawned(false);
         }
      } else {
         if (labyrinth.isColossusFirstSpawn() && !level.getGameRules().getBoolean(TensuraGameRules.COLOSSUS_RESPAWN)) {
            return;
         }

         if (TensuraStorages.getSpiritFrom(player).isColossusWon()) {
            return;
         }

         Vec3 colossusPos = labyrinth.getColossusPos();
         double radius = labyrinth.getAreaRadius();
         if (player.distanceToSqr(colossusPos) > radius * radius) {
            return;
         }

         if (!labyrinth.isColossusFirstSpawn()) {
            if (SpiritStorage.respawnColossus(labyrinth, (ServerLevel)level, colossusPos, MobSpawnType.NATURAL).isAlive()) {
               labyrinth.setColossusFirstSpawned(true);
            }
         } else {
            SpiritStorage.respawnColossus(labyrinth, (ServerLevel)level, colossusPos, MobSpawnType.TRIGGERED);
         }
      }
   }

   public static void placeGazelArena(ServerLevel level) {
      StructureTemplateManager manager = level.getStructureManager();
      Optional<StructureTemplate> optional = manager.get(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf_village/building/dwarf_arena"));
      if (!optional.isEmpty()) {
         BlockPos pos = new BlockPos(0, 80, 0);
         if (optional.get().placeInWorld(level, pos, pos, new StructurePlaceSettings(), StructureBlockEntity.createRandom(0L), 2)) {
            Tensura.LOG.info("Placed Dwarf King arena in {}", level.dimension().location());
         }
      }
   }
}
