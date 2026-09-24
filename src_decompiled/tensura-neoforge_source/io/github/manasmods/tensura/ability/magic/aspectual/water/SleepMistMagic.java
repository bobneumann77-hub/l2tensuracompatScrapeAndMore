package io.github.manasmods.tensura.ability.magic.aspectual.water;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.magic.field.cloud.SleepMistCloud;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class SleepMistMagic extends AspectualMagic {
   public static final AspectualMagicConfig.SleepMist CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).SleepMist;

   public SleepMistMagic() {
      super(AspectualMagic.AspectualType.WATER);
   }

   @Override
   public int getDefaultCastTime() {
      return CONFIG.castTime;
   }

   public int getMaxMastery() {
      return MAGIC_CONFIG.AspectualMagic.masteryMedium;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   @Override
   protected void applyCastingVisual(ManasSkillInstance instance, Player entity, int heldTicks, int mode, int castTime) {
      super.applyCastingVisual(instance, entity, heldTicks, mode, castTime);
      if (castTime > 1) {
         MagicCircle.castMagicCircle(
            3.0F,
            25,
            MagicCircleVariant.WATER,
            entity,
            instance.getOrCreateTag(),
            0.0F,
            new Vec3(0.0, 0.1F, 0.0),
            instance,
            mode,
            Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
         );
      }
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (heldTicks >= this.getCastingTime(instance, entity)) {
         if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            SleepMistCloud mist = new SleepMistCloud(entity.level(), entity);
            int duration = instance.isMastered(entity) ? CONFIG.mistDurationMastered : CONFIG.mistDuration;
            mist.setLife(duration);
            mist.setMobEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.PARALYSIS), duration, CONFIG.mistLevel, true, false, true));
            mist.setSize(CONFIG.mistRadius);
            mist.setHeight(1.0F);
            mist.setSkill(entity, instance, this, mode);
            mist.setPos(entity.getX(), entity.getY() + 1.0, entity.getZ());
            entity.level().addFreshEntity(mist);
            instance.addMasteryPoint(entity);
            entity.swing(InteractionHand.MAIN_HAND, true);
            entity.level()
               .playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.MIST_RELEASE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
         }
      }
   }
}
