package io.github.manasmods.tensura.ability.magic.spiritual.necromancy;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.config.ability.magic.SpiritualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.magic.field.CurseBindHands;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.magic.SpiritualMagics;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class CurseBindMagic extends NecromancyMagic {
   public static final SpiritualMagicConfig.CurseBind CONFIG = ((SpiritualMagicConfig)ConfigRegistry.getConfig(SpiritualMagicConfig.class)).CurseBind;

   public CurseBindMagic() {
      super(SpiritualMagic.SpiritLevel.GREATER);
   }

   @Override
   public int getDefaultCastTime() {
      return CONFIG.castTime;
   }

   @Override
   public int getMasteryCastTime() {
      return CONFIG.castTimeMastered;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   @Override
   public boolean canLearnSkill(ManasSkillInstance instance, LivingEntity entity) {
      if (!super.canLearnSkill(instance, entity)) {
         return false;
      }

      if (!SkillUtils.isSkillMastered(entity, (ManasSkill)SpiritualMagics.CURSE.get())) {
         instance.setCoolDowns(TensuraSkill.BASE_CONFIG.Learning.learningFailCooldown);
         if (entity instanceof Player player) {
            player.playNotifySound((SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
            player.displayClientMessage(
               Component.translatable(
                     "tensura.skill.learn_points.failed_mastery",
                     new Object[]{instance.getChatDisplayName(false), ((CurseMagic)SpiritualMagics.CURSE.get()).getChatDisplayName(false)}
                  )
                  .withStyle(ChatFormatting.RED),
               true
            );
         }

         return false;
      } else {
         return true;
      }
   }

   @Override
   protected void applyCastingVisual(ManasSkillInstance instance, Player entity, int heldTicks, int mode, int castTime) {
      super.applyCastingVisual(instance, entity, heldTicks, mode, castTime);
      if (castTime > 1) {
         MagicCircle.castMagicCircle(
            0.5F,
            25,
            MagicCircleVariant.NECROMANCY,
            entity,
            instance.getOrCreateTag(),
            0.75F,
            Vec3.ZERO,
            instance,
            mode,
            Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
         );
      }
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (heldTicks >= this.getCastingTime(instance, entity)) {
         if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            LivingEntity target = ObjectSelectionHelper.getTargetingEntity(entity, CONFIG.range, false, true);
            if (target != null) {
               entity.level()
                  .playSound(
                     null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_DARK.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
                  );
               TensuraParticleHelper.addServerParticlesAroundSelf(target, (ParticleOptions)TensuraParticleTypes.SOUL.get());
               instance.addMasteryPoint(entity);
               instance.setCoolDown(instance.isMastered(entity) ? CONFIG.cooldownMastered : CONFIG.cooldown, mode);
               CurseBindHands hands = new CurseBindHands(entity.level(), entity);
               hands.setPos(target.position());
               hands.setSkill(entity, instance, this, mode);
               float damage = instance.isMastered(entity) ? CONFIG.bindDamageMastered : CONFIG.bindDamage;
               hands.setDamage(damage);
               hands.setTickEachHit(CONFIG.damageInterval);
               hands.setLife(CONFIG.bindDuration);
               hands.setSize(target.getBbWidth() / 0.6F);
               hands.setVisualSize(target.getBbHeight() / 1.8F);
               MobEffectInstance paralysis = new MobEffectInstance(
                  TensuraMobEffects.getReference(TensuraMobEffects.PARALYSIS), CONFIG.damageInterval + 5, CONFIG.paralysisLevel - 1, true, false, true
               );
               hands.setMobEffect(paralysis);
               MobEffectInstance corrosion = new MobEffectInstance(
                  TensuraMobEffects.getReference(TensuraMobEffects.CORROSION), CONFIG.curseDuration, CONFIG.corrosionLevel - 1, true, false, true
               );
               hands.setAdditionalCurseEffect(corrosion);
               hands.setCurseInterval(CONFIG.curseInterval);
               hands.setCurseDuration(CONFIG.curseDuration);
               hands.setCurseLevel(CONFIG.curseLevel);
               hands.setEffectStack(true);
               entity.level().addFreshEntity(hands);
               hands.triggerAnim("controller", "start");
               entity.swing(InteractionHand.MAIN_HAND, true);
            }
         }
      }
   }
}
