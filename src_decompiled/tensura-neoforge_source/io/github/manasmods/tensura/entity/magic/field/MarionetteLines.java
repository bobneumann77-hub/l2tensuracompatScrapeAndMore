package io.github.manasmods.tensura.entity.magic.field;

import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MarionetteLines extends AreaField implements GeoEntity {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public MarionetteLines(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public MarionetteLines(Level level, Entity entity) {
      this((EntityType<? extends Projectile>)MiscEntityTypes.MARIONETTE_LINES.get(), level);
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

   @NotNull
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
         if (this.getTarget() != null) {
            this.setPos(this.getTarget().position());
            if (!this.getTarget().isAlive() && this.getLife() - this.getAge() > 10) {
               this.setAge(this.getLife() - 10);
            }
         }
      }
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController(
            this,
            "controller",
            0,
            event -> {
               if (this.getAge() < 10) {
                  return event.setAndContinue(RawAnimation.begin().thenPlayAndHold("animation.marionette_lines.start"));
               } else {
                  return this.getLife() - this.getAge() < 10
                     ? event.setAndContinue(RawAnimation.begin().thenPlayAndHold("animation.marionette_lines.stop"))
                     : event.setAndContinue(RawAnimation.begin().thenLoop("animation.marionette_lines.loop"));
               }
            }
         )
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
