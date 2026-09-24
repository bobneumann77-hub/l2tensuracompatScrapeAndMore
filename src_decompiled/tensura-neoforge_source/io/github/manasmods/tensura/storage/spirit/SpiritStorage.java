package io.github.manasmods.tensura.storage.spirit;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.PlayerEvent.DropItem;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.EntityEvents;
import io.github.manasmods.manascore.skill.api.EntityEvents.LivingTickEvent;
import io.github.manasmods.manascore.storage.api.Storage;
import io.github.manasmods.manascore.storage.api.StorageEvents;
import io.github.manasmods.manascore.storage.api.StorageKey;
import io.github.manasmods.manascore.storage.api.StorageEvents.RegisterStorage;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.entity.monster.ElementalColossusEntity;
import io.github.manasmods.tensura.event.TensuraSpiritEvents;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import io.github.manasmods.tensura.registry.dimension.TensuraDimensions;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.labyrinth.ILabyrinth;
import io.github.manasmods.tensura.storage.labyrinth.LabyrinthStorage;
import java.util.Arrays;
import lombok.Generated;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class SpiritStorage extends Storage implements ISpiritWielder {
   @Generated
   private static final Logger log = LogManager.getLogger(SpiritStorage.class);
   private static StorageKey<SpiritStorage> key = null;
   private int spiritCooldown;
   private boolean colossusStarted;
   private boolean colossusPassed;
   private boolean colossusWon;
   private final int[] spiritLevels = new int[Element.values().length];

   public static void init() {
      StorageEvents.REGISTER_ENTITY_STORAGE
         .register(
            (RegisterStorage)registry -> key = registry.register(
               ResourceLocation.fromNamespaceAndPath("tensura", "spirit_storage"),
               SpiritStorage.class,
               LivingEntity.class::isInstance,
               target -> new SpiritStorage((LivingEntity)target)
            )
         );
      EntityEvents.LIVING_POST_TICK
         .register(
            (LivingTickEvent)entity -> {
               Level level = entity.level();
               if (!level.isClientSide()) {
                  MinecraftServer server = level.getServer();
                  if (server != null) {
                     if (server.getTickCount() % 20 == 0) {
                        if (entity instanceof Player) {
                           ISpiritWielder spirit = TensuraStorages.getSpiritFrom(entity);
                           if (spirit.getSpiritCooldown() > 0) {
                              spirit.setSpiritCooldown(spirit.getSpiritCooldown() - 1);
                              spirit.markDirty();
                           }
                        }

                        if (level.dimension().equals(TensuraDimensions.LABYRINTH)) {
                           ILabyrinth data = TensuraStorages.getLabyrinthFrom(level);
                           if (data != null
                              && entity.tickCount % 10 != 0
                              && !entity.isSpectator()
                              && !entity.hasInfiniteMaterials()
                              && entity.getY() <= data.getVoidHeight()) {
                              Vec3 pos = LabyrinthStorage.isEntityPassedColossus(entity) ? data.getVoidSavePosPassed() : data.getVoidSavePos();
                              entity.unRide();
                              entity.fallDistance = 0.0F;
                              entity.teleportTo(pos.x(), pos.y(), pos.z());
                              entity.hurtMarked = true;
                              level.playSound(
                                 null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_TELEPORT, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
                              );
                           }
                        }
                     }
                  }
               }
            }
         );
      PlayerEvent.DROP_ITEM.register((DropItem)(player, item) -> {
         if (player.level() instanceof ServerLevel level) {
            if (!level.dimension().equals(TensuraDimensions.LABYRINTH)) {
               return EventResult.pass();
            }

            ILabyrinth labyrinth = TensuraStorages.getLabyrinthFrom(level);
            Vec3 pos = labyrinth.getColossusPos();
            if (labyrinth.isColossusSpawned()) {
               return EventResult.pass();
            }

            if (player.distanceToSqr(pos) >= 100.0) {
               return EventResult.pass();
            }

            ItemStack stack = item.getItem();
            if (!stack.is((Item)TensuraBlocks.Items.PURE_MAGISTEEL_BLOCK.get())) {
               return EventResult.pass();
            }

            if (stack.getCount() == 1) {
               item.discard();
            } else {
               stack.shrink(1);
            }

            ElementalColossusEntity colossus = respawnColossus(labyrinth, level, pos, MobSpawnType.MOB_SUMMONED);
            SkillHelper.knockBack(colossus, player, 5.0F);
            return EventResult.pass();
         } else {
            return EventResult.pass();
         }
      });
   }

   public static ElementalColossusEntity respawnColossus(ILabyrinth labyrinth, ServerLevel level, Vec3 pos, MobSpawnType spawnType) {
      ElementalColossusEntity colossus = new ElementalColossusEntity(level, pos, spawnType);
      if (level.addFreshEntity(colossus) && colossus.isAlive()) {
         colossus.setSleeping(true);
         labyrinth.setColossusSpawned(true);
      }

      TensuraParticleHelper.addServerParticlesAroundSelf(colossus, ParticleTypes.EXPLOSION_EMITTER);
      TensuraParticleHelper.addServerParticlesAroundSelf(colossus, ParticleTypes.TOTEM_OF_UNDYING);
      TensuraParticleHelper.addServerParticlesAroundSelf(colossus, ParticleTypes.TOTEM_OF_UNDYING, 2.0);
      colossus.level().playSound(null, colossus.getX(), colossus.getY(), colossus.getZ(), SoundEvents.TOTEM_USE, TensuraSkill.ABILITY_SOUND, 3.0F, 1.0F);
      return colossus;
   }

   protected SpiritStorage(LivingEntity holder) {
      super(holder);
      this.colossusPassed = false;
      this.colossusWon = false;
      this.colossusStarted = false;
   }

   @Override
   public int getSpiritLevelId(Element spirit) {
      return this.spiritLevels[spirit.getId()];
   }

   @Nullable
   @Override
   public SpiritualMagic.SpiritLevel getSpiritLevel(Element spirit) {
      int id = this.spiritLevels[spirit.getId()];
      return id == 0 ? null : SpiritualMagic.SpiritLevel.byId(id);
   }

   @Override
   public boolean setSpiritLevel(Element spirit, SpiritualMagic.SpiritLevel level) {
      Changeable<Element> element = Changeable.of(spirit);
      Changeable<SpiritualMagic.SpiritLevel> levelChangeable = Changeable.of(level);
      if (!((TensuraSpiritEvents.SpiritLevelUpdate)TensuraSpiritEvents.SPIRIT_UPDATE.invoker()).update(this.getOwner(), element, levelChangeable).isFalse()) {
         this.spiritLevels[((Element)element.get()).getId()] = ((SpiritualMagic.SpiritLevel)levelChangeable.get()).getId();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void clearSpiritLevel() {
      Arrays.fill(this.spiritLevels, 0);
   }

   public void save(CompoundTag tag) {
      ListTag spiritList = new ListTag();

      for (int i = 0; i < this.spiritLevels.length; i++) {
         CompoundTag spirit = new CompoundTag();
         spirit.putInt("spirit" + i, this.spiritLevels[i]);
         spiritList.add(spirit);
      }

      tag.put("spiritList", spiritList);
      tag.putInt("cooldown", this.spiritCooldown);
      tag.putBoolean("colossusStarted", this.colossusStarted);
      tag.putBoolean("colossusPassed", this.colossusPassed);
      tag.putBoolean("colossusWon", this.colossusWon);
   }

   public void load(CompoundTag tag) {
      ListTag spiritList = (ListTag)tag.get("spiritList");
      if (spiritList != null) {
         Arrays.fill(this.spiritLevels, 0);
         int count = Math.min(spiritList.size(), this.spiritLevels.length);

         for (int i = 0; i < count; i++) {
            CompoundTag spirit = (CompoundTag)spiritList.get(i);
            this.spiritLevels[i] = spirit.getInt("spirit" + i);
         }
      }

      this.spiritCooldown = tag.getInt("cooldown");
      this.colossusStarted = tag.getBoolean("colossusStarted");
      this.colossusPassed = tag.getBoolean("colossusPassed");
      this.colossusWon = tag.getBoolean("colossusWon");
   }

   protected LivingEntity getOwner() {
      return (LivingEntity)this.holder;
   }

   public static int getSpiritCooldown(LivingEntity entity) {
      return TensuraStorages.getSpiritFrom(entity).getSpiritCooldown();
   }

   public static void setSpiritCooldown(LivingEntity entity, int cooldown) {
      ISpiritWielder spiritWielder = TensuraStorages.getSpiritFrom(entity);
      spiritWielder.setSpiritCooldown(cooldown);
      spiritWielder.markDirty();
   }

   public static SpiritualMagic.SpiritLevel getSpiritLevel(LivingEntity entity, Element element) {
      return TensuraStorages.getSpiritFrom(entity).getSpiritLevel(element);
   }

   @Generated
   public static StorageKey<SpiritStorage> getKey() {
      return key;
   }

   @Generated
   @Override
   public int getSpiritCooldown() {
      return this.spiritCooldown;
   }

   @Generated
   @Override
   public void setSpiritCooldown(int spiritCooldown) {
      this.spiritCooldown = spiritCooldown;
   }

   @Generated
   @Override
   public boolean isColossusStarted() {
      return this.colossusStarted;
   }

   @Generated
   @Override
   public boolean isColossusPassed() {
      return this.colossusPassed;
   }

   @Generated
   @Override
   public boolean isColossusWon() {
      return this.colossusWon;
   }

   @Generated
   @Override
   public void setColossusStarted(boolean colossusStarted) {
      this.colossusStarted = colossusStarted;
   }

   @Generated
   @Override
   public void setColossusPassed(boolean colossusPassed) {
      this.colossusPassed = colossusPassed;
   }

   @Generated
   @Override
   public void setColossusWon(boolean colossusWon) {
      this.colossusWon = colossusWon;
   }
}
