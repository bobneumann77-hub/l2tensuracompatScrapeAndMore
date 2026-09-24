package io.github.manasmods.tensura.entity.magic.barrier;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.spiritual.earth.EarthStormMagic;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.projectile.magic.StoneShotProjectile;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class EarthStormEntity extends BarrierEntity implements GeoEntity {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public EarthStormEntity(EntityType<? extends EarthStormEntity> entityType, Level level) {
      super(entityType, level);
      this.setElementalAttack(true);
      this.setFollowOwner(true);
      this.noCulling = false;
   }

   @Override
   public boolean shouldCreateParts() {
      return false;
   }

   @Override
   public boolean hurt(DamageSource pSource, float pAmount) {
      return false;
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.EARTH_ELEMENTAL;
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
   public void tick() {
      super.tick();
      if (this.tickCount % 5 == 0) {
         this.level()
            .playSound(
               null,
               this.getX(),
               this.getY() - this.getSize(),
               this.getZ(),
               (SoundEvent)TensuraSoundEvents.WIND_BLOW.get(),
               TensuraSkill.ABILITY_SOUND,
               2.0F,
               1.0F
            );
      }
   }

   @Override
   public void applyEffect(LivingEntity target) {
      if (target.getRandom().nextFloat() <= EarthStormMagic.CONFIG.levitationChance) {
         target.addEffect(
            new MobEffectInstance(
               MobEffects.LEVITATION, EarthStormMagic.CONFIG.levitationDuration, EarthStormMagic.CONFIG.levitationLevel - 1, false, false, false
            )
         );
      }

      this.spawnStoneSpike(this.getOwner(), target);
   }

   private void spawnStoneSpike(@Nullable Entity owner, LivingEntity target) {
      StoneShotProjectile stoneShot = new StoneShotProjectile(target.level(), owner);
      stoneShot.setSpeed(1.0F);
      stoneShot.setDamage(EarthStormMagic.CONFIG.stormDamage);
      stoneShot.setSkill(this.getSkill());
      stoneShot.setMode(this.getMode());
      stoneShot.setApCost(this.getApCost());
      stoneShot.setMpCost(this.getMpCost());
      stoneShot.setElementalAttack(true);
      stoneShot.setPos(target.getX(), target.getEyeY() + 4.0, target.getZ());
      target.level().addFreshEntity(stoneShot);
      target.level()
         .playSound(
            null, stoneShot.getX(), stoneShot.getY(), stoneShot.getZ(), (SoundEvent)TensuraSoundEvents.CAST_EARTH.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
         );
      if (owner instanceof LivingEntity shooter) {
         MagicCircle.castMagicCircle(
            1.0F,
            25,
            stoneShot.position().add(0.0, 0.5, 0.0),
            MagicCircleVariant.EARTH,
            shooter,
            new CompoundTag(),
            this.getSkill(),
            this.getMode(),
            Pair.of(this.getApCost(), this.getMpCost())
         );
      }
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(new AnimationController(this, "controller", 0, event -> event.setAndContinue(RawAnimation.begin().thenLoop("animation.storm.loop"))));
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
