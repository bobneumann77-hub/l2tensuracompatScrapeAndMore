package io.github.manasmods.tensura.entity.magic.field;

import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class HellFlare extends AreaField implements GeoEntity {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public HellFlare(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setElementalAttack(true);
   }

   public HellFlare(EntityType<? extends HellFlare> flare, Level level, Entity entity) {
      this(flare, level);
      this.setOwner(entity);
      this.setElementalAttack(true);
   }

   public boolean shouldRenderAtSqrDistance(double pDistance) {
      return pDistance < 4096.0 || super.shouldRenderAtSqrDistance(pDistance);
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
      return TensuraDamageTypes.BLACK_FLAME;
   }

   @Override
   public boolean isInstant() {
      return true;
   }

   @Override
   public void tick() {
      super.tick();
      if (this.getAge() >= this.getTickEachHit()) {
         this.level()
            .playSound(
               null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.CAST_FIRE.get(), TensuraSkill.ABILITY_SOUND, this.getSize(), 1.0F
            );
      }
   }

   @Override
   public void applyEffect(LivingEntity target, boolean instant) {
      if (instant) {
         super.applyEffect(target, true);
      } else {
         target.setRemainingFireTicks(Math.max(target.getRemainingFireTicks(), 200));
         MobEffectInstance instance = new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.BLACK_BURN), 200, 0, false, false, false);
         TensuraMobEffect.addEffect(target, instance, this.getOwner(), this.getSkill() != null ? this.getSkill().getSkill() : null, this.getMode());
      }
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController(this, "controller", 0, event -> event.setAndContinue(RawAnimation.begin().thenPlayAndHold("animation.hell_flare.start")))
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
