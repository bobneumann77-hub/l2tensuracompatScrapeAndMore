package io.github.manasmods.tensura.entity.magic.field;

import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.event.TensuraSkillEvents;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.storage.effect.EffectStorage;
import io.github.manasmods.tensura.world.TensuraGameRules;
import java.util.Stack;
import lombok.Generated;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MagicExplosion extends AreaField implements GeoEntity {
   protected boolean visualOnly = false;
   protected boolean limitedGriefing = false;
   private final Stack<BlockPos> chunksToDestroy = new Stack<>();
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public MagicExplosion(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setElementalAttack(true);
      this.setTickEachHit(10);
      this.noCulling = true;
      this.setLife(85);
   }

   public MagicExplosion(Level level, Entity entity) {
      this((EntityType<? extends Projectile>)MiscEntityTypes.MAGIC_EXPLOSION.get(), level);
      this.setOwner(entity);
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putBoolean("VisualOnly", this.isVisualOnly());
      compound.putBoolean("LimitedGriefing", this.isLimitedGriefing());
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setVisualOnly(compound.getBoolean("VisualOnly"));
      this.setLimitedGriefing(compound.getBoolean("LimitedGriefing"));
   }

   @NotNull
   @Override
   public EntityDimensions getDimensions(Pose pPose) {
      return this.getType().getDimensions();
   }

   public boolean shouldRenderAtSqrDistance(double d) {
      double e = 128.0 * getViewScale();
      return d < e * e;
   }

   @Override
   public boolean isInstant() {
      return true;
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return DamageTypes.EXPLOSION;
   }

   @Override
   protected boolean canHitEntity(Entity pTarget) {
      if (pTarget == this.getOwner()) {
         return false;
      } else {
         return !super.canHitEntity(pTarget) ? false : this.getOwner() == null || !pTarget.isAlliedTo(this.getOwner());
      }
   }

   @Override
   public void tick() {
      super.tick();
      if (this.getAge() >= this.getTickEachHit()) {
         this.level()
            .playSound(
               null, this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_EXPLODE, TensuraSkill.ABILITY_SOUND, Math.max(this.getSize(), 20.0F), 1.0F
            );
         if (this.isVisualOnly()) {
            return;
         }

         if (!this.level().isClientSide() && this.shouldGrief() && this.getAge() <= this.getTickEachHit() + 60) {
            float radius = Math.min(this.getSize(), this.level().getGameRules().getInt(TensuraGameRules.MAXIMUM_MAGIC_EXPLOSION));
            int chunkRadius = Math.max((int)radius / 15, 1);
            if (this.chunksToDestroy.isEmpty()) {
               BlockPos center = this.blockPosition().below((int)radius);

               for (int i = -chunkRadius; i <= chunkRadius; i++) {
                  for (int j = -chunkRadius; j <= chunkRadius; j++) {
                     for (int k = -chunkRadius; k <= chunkRadius; k++) {
                        this.chunksToDestroy.push(center.offset(i * 16, j * 16, k * 16));
                     }
                  }
               }

               this.chunksToDestroy
                  .sort((first, second) -> Double.compare(second.distManhattan(this.blockPosition()), first.distManhattan(this.blockPosition())));
            } else {
               int tickChunk = Math.min(this.chunksToDestroy.size(), 3);

               for (int i = 0; i < tickChunk; i++) {
                  this.removeChunk(radius);
               }
            }
         }
      }
   }

   @Override
   protected void hitTarget(boolean instant) {
      if (!this.level().isClientSide()) {
         if (instant) {
            EffectStorage.setCameraShake(this, this.getSize() * 1.5, 0.1F * this.getSize() / 50.0F, 20);
         } else {
            EffectStorage.setCameraShake(this, this.getSize() * 2.0F, 0.1F * this.getSize() / 50.0F, 20);
         }

         double distance = this.getSize() / 2.0F;
         distance *= distance;

         for (ServerPlayer player : ((ServerLevel)this.level()).players()) {
            if (!(player.distanceToSqr(this) > distance)) {
               player.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.FLASHED_BLINDNESS), 10, 0, false, false, false));
            }
         }

         for (Entity target : this.level().getEntitiesOfClass(Entity.class, this.getBoundingBox().inflate(this.getSize()))) {
            if (target instanceof LivingEntity living) {
               this.applyEffect(living, instant);
            } else {
               this.dealDamage(target);
            }
         }
      }
   }

   @Override
   public void applyEffect(LivingEntity target, boolean instant) {
      if (instant) {
         super.applyEffect(target, true);
      } else if (this.getAge() > 0) {
         this.dealDamage(target, this.getDamage() / 10.0F, 0.1F);
      }
   }

   @Override
   protected boolean dealDamage(Entity target, float damage, float costMultiplier) {
      return !this.canHitEntity(target) ? false : super.dealDamage(target, damage, costMultiplier);
   }

   private void removeChunk(float radius) {
      BlockPos chunkCorner = this.chunksToDestroy.pop();
      MutableBlockPos pos = new MutableBlockPos();
      pos.set(chunkCorner);
      float damage = this.getDamage() + this.getSecondaryDamage();

      for (int x = 0; x < 16; x++) {
         for (int z = 0; z < 16; z++) {
            for (int y = 16; y >= 0; y--) {
               pos.set(
                  chunkCorner.getX() + x,
                  Mth.clamp(chunkCorner.getY() + y, this.level().getMinBuildHeight(), this.level().getMaxBuildHeight()),
                  chunkCorner.getZ() + z
               );
               double yDist = this.smoothMin(0.6F - Math.abs(this.blockPosition().getY() - pos.getY()) / radius, 0.6F, 0.2F);
               double distToCenter = pos.distToLowCornerSqr(this.blockPosition().getX(), pos.getY() - 1, this.blockPosition().getZ());
               double targetRadius = yDist * radius * radius;
               if (distToCenter <= targetRadius) {
                  BlockState state = this.level().getBlockState(pos);
                  if (!state.isAir()
                     && !state.is(TensuraBlockTags.MAGIC_EXPLOSION_IMMUNE)
                     && (!this.isLimitedGriefing() || !(state.getBlock().getExplosionResistance() >= damage))) {
                     if (!state.is(Blocks.WATER) && !state.is(Blocks.LAVA)) {
                        if (state.getBlock().defaultDestroyTime() > -1.0F
                           && !((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
                              .grief(this.getSkill(), this.level(), this.getOwner(), pos.getX(), pos.getY(), pos.getZ())
                              .isFalse()) {
                           this.level().removeBlock(pos, false);
                           ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker())
                              .grief(this.getSkill(), this.level(), this.getOwner(), pos.getX(), pos.getY(), pos.getZ());
                        }
                     } else if (!((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
                        .grief(this.getSkill(), this.level(), this.getOwner(), pos.getX(), pos.getY(), pos.getZ())
                        .isFalse()) {
                        this.level().setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                        ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker())
                           .grief(this.getSkill(), this.level(), this.getOwner(), pos.getX(), pos.getY(), pos.getZ());
                     }
                  }
               }
            }
         }
      }
   }

   private float smoothMin(float a, float b, float k) {
      float h = Math.max(k - Math.abs(a - b), 0.0F) / k;
      return Math.min(a, b) - h * h * k * 0.25F;
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController(
            this,
            "controller",
            0,
            event -> this.getTickEachHit() <= 1
               ? event.setAndContinue(RawAnimation.begin().thenPlayAndHold("animation.magic_explosion.explode_quick"))
               : event.setAndContinue(RawAnimation.begin().thenPlayAndHold("animation.magic_explosion.explode"))
         )
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   @Generated
   public boolean isVisualOnly() {
      return this.visualOnly;
   }

   @Generated
   public void setVisualOnly(boolean visualOnly) {
      this.visualOnly = visualOnly;
   }

   @Generated
   public boolean isLimitedGriefing() {
      return this.limitedGriefing;
   }

   @Generated
   public void setLimitedGriefing(boolean limitedGriefing) {
      this.limitedGriefing = limitedGriefing;
   }
}
