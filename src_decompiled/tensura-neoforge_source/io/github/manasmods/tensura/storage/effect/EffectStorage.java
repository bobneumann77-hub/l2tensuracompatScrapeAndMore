package io.github.manasmods.tensura.storage.effect;

import io.github.manasmods.manascore.skill.api.EntityEvents;
import io.github.manasmods.manascore.skill.api.EntityEvents.LivingTickEvent;
import io.github.manasmods.manascore.storage.api.Storage;
import io.github.manasmods.manascore.storage.api.StorageEvents;
import io.github.manasmods.manascore.storage.api.StorageKey;
import io.github.manasmods.manascore.storage.api.StorageEvents.RegisterStorage;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.enchantment.TensuraEnchantmentHelper;
import io.github.manasmods.tensura.enchantment.TensuraEnchantments;
import io.github.manasmods.tensura.storage.TensuraStorages;
import java.util.function.Predicate;
import lombok.Generated;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EffectStorage extends Storage implements IEffect {
   @Generated
   private static final Logger log = LogManager.getLogger(EffectStorage.class);
   private static StorageKey<EffectStorage> key = null;
   public static final int INSTANCE_UPDATE = 20;
   private float severanceAmount;
   private int severanceRemoveTime;
   private int cameraShakeDuration;
   private boolean meatShield;
   private boolean onBlackFlame;
   private boolean ignorePainNull;
   private boolean sleptAtThunderNight;
   private float lockedXRot;
   private float lockedYRot;
   private float cameraShakeLevel;

   public static void init() {
      StorageEvents.REGISTER_ENTITY_STORAGE
         .register(
            (RegisterStorage)registry -> key = registry.register(
               ResourceLocation.fromNamespaceAndPath("tensura", "effect_storage"),
               EffectStorage.class,
               LivingEntity.class::isInstance,
               target -> new EffectStorage((LivingEntity)target)
            )
         );
      EntityEvents.LIVING_POST_TICK.register((LivingTickEvent)entity -> {
         Level level = entity.level();
         if (!level.isClientSide()) {
            MinecraftServer server = level.getServer();
            if (server != null) {
               boolean isPlayer = entity instanceof Player;
               boolean updateTick = server.getTickCount() % 20 == 0;
               if (isPlayer || updateTick) {
                  IEffect storage = TensuraStorages.getEffectFrom(entity);
                  if (isPlayer) {
                     int shakeDuration = storage.getCameraShakeDuration();
                     if (shakeDuration > 0) {
                        storage.setCameraShakeDuration(shakeDuration - 1);
                        if (shakeDuration == 1) {
                           storage.setCameraShakeLevel(0.0F);
                        }

                        storage.markDirty();
                     }
                  }

                  if (updateTick) {
                     int removeTime = storage.getSeveranceRemoveTime();
                     if (removeTime > 0) {
                        storage.setSeveranceRemoveTime(removeTime - 1);
                        if (removeTime == 1) {
                           storage.setSeveranceAmount(0.0F);
                        }

                        storage.markDirty();
                     }
                  }
               }
            }
         }
      });
   }

   protected EffectStorage(LivingEntity holder) {
      super(holder);
   }

   @Override
   public void setSeveranceAmount(float amount) {
      this.severanceAmount = amount;
      LivingEntity entity = this.getOwner();
      float maxHP = entity.getMaxHealth() - this.severanceAmount;
      if (entity.getHealth() > maxHP) {
         entity.hurt(TensuraDamageTypes.getDamageSource(entity.level(), TensuraDamageTypes.SEVERANCE), entity.getHealth() - maxHP);
      }
   }

   @Override
   public void increaseSeveranceAmount(float amount) {
      int protection = TensuraEnchantmentHelper.getEnchantmentLevel(this.getOwner().level(), TensuraEnchantments.SEVERANCE_PROTECTION, this.getOwner());
      if (protection > 0) {
         amount *= 1.0F - protection * 0.1F;
      }

      this.setSeveranceAmount(this.getSeveranceAmount() + amount);
   }

   public void save(CompoundTag data) {
      data.putFloat("severance", this.severanceAmount);
      data.putInt("severanceRemove", this.severanceRemoveTime);
      data.putInt("cameraShakeDuration", this.cameraShakeDuration);
      data.putBoolean("onBlackFlame", this.onBlackFlame);
      data.putBoolean("ignorePainNull", this.ignorePainNull);
      data.putBoolean("meatShield", this.meatShield);
      data.putFloat("lockedXRot", this.lockedXRot);
      data.putFloat("lockedYRot", this.lockedYRot);
      data.putFloat("cameraShakeLevel", this.cameraShakeLevel);
   }

   public void load(CompoundTag data) {
      this.severanceAmount = data.getFloat("severance");
      this.severanceRemoveTime = data.getInt("severanceRemove");
      this.cameraShakeDuration = data.getInt("cameraShakeDuration");
      this.onBlackFlame = data.getBoolean("onBlackFlame");
      this.ignorePainNull = data.getBoolean("ignorePainNull");
      this.meatShield = data.getBoolean("meatShield");
      this.lockedXRot = data.getFloat("lockedXRot");
      this.lockedYRot = data.getFloat("lockedYRot");
      this.cameraShakeLevel = data.getFloat("cameraShakeLevel");
   }

   protected LivingEntity getOwner() {
      return (LivingEntity)this.holder;
   }

   public static float getSeveranceMaxHealth(LivingEntity entity) {
      IEffect effect = TensuraStorages.getEffectFrom(entity);
      return entity.getMaxHealth() - effect.getSeveranceAmount();
   }

   public static boolean clearSeverance(LivingEntity entity) {
      IEffect effect = TensuraStorages.getEffectFrom(entity);
      if (effect.getSeveranceAmount() > 0.0F) {
         effect.setSeveranceAmount(0.0F);
         effect.setSeveranceRemoveTime(0);
         effect.markDirty();
         return true;
      } else {
         return false;
      }
   }

   public static void setCameraShake(LivingEntity entity, float level, int duration) {
      IEffect effect = TensuraStorages.getEffectFrom(entity);
      effect.setCameraShakeLevel(Math.max(effect.getCameraShakeLevel(), level));
      effect.setCameraShakeDuration(Math.max(duration, effect.getCameraShakeDuration()));
      effect.markDirty();
   }

   public static void setCameraShake(Level level, Vec3 center, double radius, float multiplier, int duration, Predicate<Player> predicate) {
      if (!level.isClientSide()) {
         double distance = radius * radius;

         for (ServerPlayer player : ((ServerLevel)level).players()) {
            if (!(player.distanceToSqr(center) > distance) && (predicate == null || predicate.test(player))) {
               setCameraShake(player, multiplier, duration);
            }
         }
      }
   }

   public static void setCameraShake(Entity entity, double radius, float multiplier, int duration) {
      setCameraShake(entity.level(), entity.position(), radius, multiplier, duration, null);
   }

   @Generated
   public static StorageKey<EffectStorage> getKey() {
      return key;
   }

   @Generated
   @Override
   public float getSeveranceAmount() {
      return this.severanceAmount;
   }

   @Generated
   @Override
   public int getSeveranceRemoveTime() {
      return this.severanceRemoveTime;
   }

   @Generated
   @Override
   public int getCameraShakeDuration() {
      return this.cameraShakeDuration;
   }

   @Generated
   @Override
   public void setSeveranceRemoveTime(int severanceRemoveTime) {
      this.severanceRemoveTime = severanceRemoveTime;
   }

   @Generated
   @Override
   public void setCameraShakeDuration(int cameraShakeDuration) {
      this.cameraShakeDuration = cameraShakeDuration;
   }

   @Generated
   @Override
   public boolean isMeatShield() {
      return this.meatShield;
   }

   @Generated
   @Override
   public boolean isOnBlackFlame() {
      return this.onBlackFlame;
   }

   @Generated
   @Override
   public boolean isIgnorePainNull() {
      return this.ignorePainNull;
   }

   @Generated
   @Override
   public boolean isSleptAtThunderNight() {
      return this.sleptAtThunderNight;
   }

   @Generated
   @Override
   public void setMeatShield(boolean meatShield) {
      this.meatShield = meatShield;
   }

   @Generated
   @Override
   public void setOnBlackFlame(boolean onBlackFlame) {
      this.onBlackFlame = onBlackFlame;
   }

   @Generated
   @Override
   public void setIgnorePainNull(boolean ignorePainNull) {
      this.ignorePainNull = ignorePainNull;
   }

   @Generated
   @Override
   public void setSleptAtThunderNight(boolean sleptAtThunderNight) {
      this.sleptAtThunderNight = sleptAtThunderNight;
   }

   @Generated
   @Override
   public float getLockedXRot() {
      return this.lockedXRot;
   }

   @Generated
   @Override
   public float getLockedYRot() {
      return this.lockedYRot;
   }

   @Generated
   @Override
   public float getCameraShakeLevel() {
      return this.cameraShakeLevel;
   }

   @Generated
   @Override
   public void setLockedXRot(float lockedXRot) {
      this.lockedXRot = lockedXRot;
   }

   @Generated
   @Override
   public void setLockedYRot(float lockedYRot) {
      this.lockedYRot = lockedYRot;
   }

   @Generated
   @Override
   public void setCameraShakeLevel(float cameraShakeLevel) {
      this.cameraShakeLevel = cameraShakeLevel;
   }
}
