package io.github.manasmods.tensura.entity.magic.field;

import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.Animation.LoopType;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ShadowBindHands extends AreaField implements GeoEntity {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public ShadowBindHands(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setElementalAttack(true);
   }

   public ShadowBindHands(Level level, Entity entity) {
      this((EntityType<? extends Projectile>)MiscEntityTypes.SHADOW_BIND_HANDS.get(), level);
      this.setOwner(entity);
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
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.DARKNESS_ELEMENTAL;
   }

   @Override
   public EntityDimensions getDimensions(Pose pose) {
      return this.getType().getDimensions().scale(this.getSize(), this.getVisualSize());
   }

   @Override
   protected void updateVisualSize() {
   }

   @Override
   public void tick() {
      super.tick();
      if (!this.level().isClientSide()) {
         if (this.tickCount % 10 == 0 && this.getTarget() != null) {
            this.setPos(this.getTarget().position());
            if (!this.getTarget().isAlive() && this.getLife() - this.getAge() > 10) {
               this.setAge(this.getLife() - 10);
            }
         }
      }
   }

   @Override
   protected boolean dealDamage(Entity target, float damage, float costMultiplier) {
      if (!(damage <= 0.0F) && target instanceof LivingEntity living) {
         DamageSource source = this.getDamageSource(costMultiplier);
         return TensuraDamageHelper.directSpiritualHurt(living, this.getOwner(), source, damage);
      } else {
         return false;
      }
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController[]{
            new AnimationController(
               this,
               "loopController",
               0,
               event -> this.getLife() - this.getAge() < 10
                  ? event.setAndContinue(RawAnimation.begin().thenPlayAndHold("animation.shadow_bind.stop"))
                  : event.setAndContinue(RawAnimation.begin().thenLoop("animation.shadow_bind.loop"))
            ),
            new AnimationController(this, "controller", 0, event -> PlayState.STOP)
               .triggerableAnim("start", RawAnimation.begin().then("animation.shadow_bind.start", LoopType.PLAY_ONCE))
         }
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
