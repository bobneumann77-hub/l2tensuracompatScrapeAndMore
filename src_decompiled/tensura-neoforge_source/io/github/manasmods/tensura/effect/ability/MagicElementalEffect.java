package io.github.manasmods.tensura.effect.ability;

import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.skill.MagicElementalTransformSkill;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.effect.template.DamageAction;
import io.github.manasmods.tensura.effect.template.ITransformation;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.particle.option.SimpleAuraParticleOptions;
import io.github.manasmods.tensura.race.RaceUtils;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import java.awt.Color;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.block.Blocks;

public class MagicElementalEffect extends TensuraMobEffect implements DamageAction, ITransformation {
   private static final SimpleAuraParticleOptions EARTH_AURA_DEFAULT = new SimpleAuraParticleOptions(0.66F, 0.47F, 0.32F, 1.0F, 4.0F, -0.3F);
   private static final SimpleAuraParticleOptions SPACE_AURA_DEFAULT = new SimpleAuraParticleOptions(0.65F, 0.37F, 0.65F, 1.0F, 4.0F, -0.3F);
   private static final SimpleAuraParticleOptions WATER_AURA_DEFAULT = new SimpleAuraParticleOptions(0.64F, 0.92F, 0.95F, 1.0F, 4.0F, -0.3F);
   private static final SimpleAuraParticleOptions WIND_AURA_DEFAULT = new SimpleAuraParticleOptions(0.77F, 0.95F, 0.8F, 1.0F, 4.0F, -0.3F);

   public MagicElementalEffect() {
      super(MobEffectCategory.BENEFICIAL, new Color(255, 192, 34).getRGB());
   }

   public boolean applyEffectTick(LivingEntity entity, int pAmplifier) {
      if (this.failedToActivate(entity, TensuraMobEffects.getReference(TensuraMobEffects.MAGIC_ELEMENTAL_TRANSFORMATION))) {
         return false;
      }

      doVisualEffect(entity, this.getMagicElement(entity));
      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int pDuration, int amplifier) {
      return pDuration % 10 == 0;
   }

   @Override
   public void onEffectRemoved(LivingEntity entity, MobEffectInstance instance) {
      this.applyDebuff(entity);
   }

   @Override
   public boolean onBeingDamaged(LivingEntity entity, DamageSource source, Changeable<Float> amount) {
      float damageMultiplier = RaceUtils.getPhysicalAttackInputMultiplier(source);
      if (damageMultiplier != 1.0F) {
         amount.set(damageMultiplier);
      }

      return true;
   }

   @Override
   public boolean onDamagingEntity(LivingEntity attacker, LivingEntity target, DamageSource source, Changeable<Float> amount) {
      Element element = this.getMagicElement(attacker);
      if (element == null) {
         return true;
      }

      switch (element) {
         case DARKNESS:
            target.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 200, 0, true, false, true));
            if (TensuraDamageHelper.isDarkDamage(source)) {
               amount.set((Float)amount.get() * MagicElementalTransformSkill.CONFIG.transformBoost);
            }
            break;
         case EARTH:
            target.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.BURDEN), 200, 0, true, false, true));
            if (TensuraDamageHelper.isEarthDamage(source)) {
               amount.set((Float)amount.get() * MagicElementalTransformSkill.CONFIG.transformBoost);
            }
            break;
         case FLAME:
            target.setRemainingFireTicks(200);
            if (TensuraDamageHelper.isFireDamage(source)) {
               amount.set((Float)amount.get() * MagicElementalTransformSkill.CONFIG.transformBoost);
            }
            break;
         case LIGHT:
            target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0, true, false, true));
            if (TensuraDamageHelper.isLightDamage(source)) {
               amount.set((Float)amount.get() * MagicElementalTransformSkill.CONFIG.transformBoost);
            }
            break;
         case SPACE:
            if (TensuraDamageHelper.isSpatialDamage(source)) {
               amount.set((Float)amount.get() * MagicElementalTransformSkill.CONFIG.transformBoost);
            }
            break;
         case WATER:
            target.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.FATAL_POISON), 200, 0, true, false, true));
            if (TensuraDamageHelper.isWaterDamage(source)) {
               amount.set((Float)amount.get() * MagicElementalTransformSkill.CONFIG.transformBoost);
            }
            break;
         case WIND:
            target.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.PARALYSIS), 200, 0, true, false, true));
            if (TensuraDamageHelper.isWindDamage(source)) {
               amount.set((Float)amount.get() * MagicElementalTransformSkill.CONFIG.transformBoost);
            }
      }

      return true;
   }

   private Element getMagicElement(LivingEntity entity) {
      MobEffectInstance instance = entity.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.MAGIC_ELEMENTAL_TRANSFORMATION));
      return instance == null ? null : Element.byId(instance.tensura$getOrCreateTag().getInt("elemental"));
   }

   public static void doVisualEffect(LivingEntity entity, Element element) {
      switch (element) {
         case null:
            break;
         case EARTH: {
            TensuraParticleHelper.addServerParticlesAroundSelf(entity, new BlockParticleOption(ParticleTypes.BLOCK, Blocks.MUD_BRICKS.defaultBlockState()));
            TensuraParticleHelper.addServerParticlesAroundSelf(entity, TensuraParticleUtils.getBogEffect());
            double size = entity.getAttributeValue(Attributes.SCALE) * 4.0;
            SimpleAuraParticleOptions opt = size == 4.0 ? EARTH_AURA_DEFAULT : new SimpleAuraParticleOptions(0.66F, 0.47F, 0.32F, 1.0F, (float)size, -0.3F);
            TensuraParticleHelper.addServerAuraParticles(entity, opt, 5, 0.01);
            break;
         }
         case FLAME: {
            double size = entity.getAttributeValue(Attributes.SCALE) * 4.0;
            TensuraParticleHelper.addServerAuraParticles(entity, TensuraParticleUtils.getFlameOrangeAura(0.8F, (float)size, -0.3F), 5, 0.01);
            TensuraParticleHelper.addServerParticlesAroundSelf(entity, (ParticleOptions)TensuraParticleTypes.RED_FIRE.get());
            break;
         }
         case LIGHT: {
            TensuraParticleHelper.addServerParticlesAroundSelf(entity, TensuraParticleUtils.getYellowGust());
            double size = entity.getAttributeValue(Attributes.SCALE) * 4.0;
            TensuraParticleHelper.addServerAuraParticles(entity, TensuraParticleUtils.getGoldAura(0.8F, (float)size, -0.3F), 5, 0.01);
            break;
         }
         case SPACE: {
            TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.REVERSE_PORTAL);
            double size = entity.getAttributeValue(Attributes.SCALE) * 4.0;
            SimpleAuraParticleOptions opt = size == 4.0 ? SPACE_AURA_DEFAULT : new SimpleAuraParticleOptions(0.65F, 0.37F, 0.65F, 1.0F, (float)size, -0.3F);
            TensuraParticleHelper.addServerAuraParticles(entity, opt, 5, 0.01);
            break;
         }
         case WATER: {
            TensuraParticleHelper.addServerParticlesAroundSelf(entity, TensuraParticleUtils.getWaterEffect());
            double size = entity.getAttributeValue(Attributes.SCALE) * 4.0;
            SimpleAuraParticleOptions opt = size == 4.0 ? WATER_AURA_DEFAULT : new SimpleAuraParticleOptions(0.64F, 0.92F, 0.95F, 1.0F, (float)size, -0.3F);
            TensuraParticleHelper.addServerAuraParticles(entity, opt, 5, 0.01);
            break;
         }
         case WIND: {
            TensuraParticleHelper.addServerParticlesAroundSelf(entity, TensuraParticleUtils.getGreenGust());
            double size = entity.getAttributeValue(Attributes.SCALE) * 4.0;
            SimpleAuraParticleOptions opt = size == 4.0 ? WIND_AURA_DEFAULT : new SimpleAuraParticleOptions(0.77F, 0.95F, 0.8F, 1.0F, (float)size, -0.3F);
            TensuraParticleHelper.addServerAuraParticles(entity, opt, 5, 0.01);
            break;
         }
         default: {
            TensuraParticleHelper.addServerParticlesAroundSelf(entity, (ParticleOptions)TensuraParticleTypes.DARK_RED_LIGHTNING_SPARK.get());
            double size = entity.getAttributeValue(Attributes.SCALE) * 4.0;
            TensuraParticleHelper.addServerAuraParticles(entity, TensuraParticleUtils.getBlackAura(0.5F, (float)size, -0.3F), 5, 0.01);
         }
      }
   }
}
