package io.github.manasmods.tensura.entity.magic.barrier;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.event.TensuraSkillEvents;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BlizzardEntity extends BarrierEntity implements GeoEntity {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public BlizzardEntity(EntityType<? extends BlizzardEntity> entityType, Level level) {
      super(entityType, level);
      this.setElementalAttack(true);
      this.noCulling = false;
   }

   public BlizzardEntity(Level level, LivingEntity entity) {
      this((EntityType<? extends BlizzardEntity>)MiscEntityTypes.BLIZZARD.get(), level);
      this.setOwner(entity);
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
      return TensuraDamageTypes.ICE_ELEMENTAL;
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
               this.getY() + this.getSize(),
               this.getZ(),
               (SoundEvent)TensuraSoundEvents.WIND_BLOW.get(),
               TensuraSkill.ABILITY_SOUND,
               1.0F,
               1.0F
            );
      }

      if (!this.level().isClientSide()) {
         if (this.getAge() % 10 == 0 && this.shouldGrief()) {
            if (!this.level().isLoaded(this.blockPosition())) {
               return;
            }

            BlockPos.betweenClosedStream(new AABB(this.blockPosition()).inflate(this.getSize()))
               .forEach(
                  pos -> {
                     if (!(this.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) > this.getSize() * this.getSize())) {
                        if (this.level().getBlockState(pos).is(Blocks.WATER) || this.level().getBlockState(pos).is(Blocks.BUBBLE_COLUMN)) {
                           if (this.level().getFluidState(pos).isSource()) {
                              if (!this.level().getBlockState(pos.above()).is(Blocks.WATER)) {
                                 if (!(this.random.nextFloat() > 0.2F)) {
                                    if (!((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
                                       .grief(this.getSkill(), this.level(), this.getOwner(), pos.getX(), pos.getY(), pos.getZ())
                                       .isFalse()) {
                                       this.level().setBlockAndUpdate(pos, Blocks.FROSTED_ICE.defaultBlockState());
                                       this.level().scheduleTick(pos, Blocks.FROSTED_ICE, Mth.nextInt(this.random, 200, 400));
                                       ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker())
                                          .grief(this.getSkill(), this.level(), this.getOwner(), pos.getX(), pos.getY(), pos.getZ());
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               );
         }
      }
   }

   @Override
   public void applyEffect(LivingEntity entity) {
      if (this.dealDamage(entity, this.getDamage(), 0.1F) && this.getTickEachHit() != 0 && this.getAge() % (this.getTickEachHit() * 5) == 0) {
         MobEffectInstance effect = this.getMobEffect();
         if (effect != null) {
            int chillLevel = effect.getAmplifier();
            if (this.isEffectStack()) {
               MobEffectInstance chill = entity.getEffect(effect.getEffect());
               if (chill != null) {
                  chillLevel = chill.getAmplifier() + effect.getAmplifier();
               }
            }

            MobEffectInstance instance = new MobEffectInstance(effect.getEffect(), effect.getDuration(), chillLevel, true, false, true);
            ManasSkill skill = this.getSkill() != null ? this.getSkill().getSkill() : null;
            TensuraMobEffect.addEffect(entity, instance, this.getOwner(), skill, this.getMode());
         }
      }
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(new AnimationController(this, "controller", 0, event -> event.setAndContinue(RawAnimation.begin().thenLoop("animation.storm.loop"))));
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
