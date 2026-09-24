package io.github.manasmods.tensura.entity.magic.barrier;

import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.entity.monster.IfritEntity;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.storage.Alignment;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.Animation.LoopType;
import software.bernie.geckolib.util.GeckoLibUtil;

public class FlareCircleEntity extends BarrierEntity implements GeoEntity {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public FlareCircleEntity(EntityType<? extends FlareCircleEntity> entityType, Level level) {
      super(entityType, level);
      this.setBurnTicks(100);
      this.setElementalAttack(true);
   }

   public FlareCircleEntity(Level level, LivingEntity entity) {
      this((EntityType<? extends FlareCircleEntity>)MiscEntityTypes.FLARE_CIRCLE.get(), level);
      this.setOwner(entity);
   }

   @Override
   public boolean canWalkThrough() {
      return this.getOwner() instanceof IfritEntity;
   }

   @Override
   public boolean blockBuilding() {
      return false;
   }

   @Override
   public boolean hurt(DamageSource pSource, float pAmount) {
      return false;
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.FIRE_ELEMENTAL;
   }

   @Override
   public boolean canHitEntity(Entity pTarget) {
      if (pTarget == this.getOwner()) {
         return false;
      } else {
         return !super.canHitEntity(pTarget) ? false : !(this.getOwner() instanceof LivingEntity owner && owner.isAlliedTo(pTarget));
      }
   }

   @Override
   public void applyEffect(LivingEntity entity) {
      super.applyEffect(entity);
      if (Alignment.shouldConsumeAir(entity)) {
         entity.setAirSupply(entity.getAirSupply() - 30);
         if (entity.getAirSupply() == -20) {
            entity.setAirSupply(0);
            entity.hurt(TensuraDamageTypes.getDamageSource(this.level(), TensuraDamageTypes.SUFFOCATE), 2.0F);
         }
      }
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController[]{
            new AnimationController(
               this,
               "loopController",
               0,
               event -> {
                  if (this.getLife() - this.getAge() < 45) {
                     return event.setAndContinue(RawAnimation.begin().thenPlayAndHold("animation.flare_circle.stop"));
                  } else {
                     return this.getAge() < 60
                        ? event.setAndContinue(RawAnimation.begin().thenPlay("animation.flare_circle.start"))
                        : event.setAndContinue(RawAnimation.begin().thenLoop("animation.flare_circle.loop"));
                  }
               }
            ),
            new AnimationController(this, "controller", 0, event -> PlayState.STOP)
               .triggerableAnim("start", RawAnimation.begin().then("animation.flare_circle.start", LoopType.PLAY_ONCE))
         }
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
