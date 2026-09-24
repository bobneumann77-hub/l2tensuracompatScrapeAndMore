package io.github.manasmods.tensura.ability.magic.spiritual.necromancy;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.config.ability.magic.SpiritualMagicConfig;
import io.github.manasmods.tensura.data.TensuraRaceTags;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.magic.field.cloud.MiasmicMistCloud;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.magic.SpiritualMagics;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import java.util.Optional;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class CurseMagic extends NecromancyMagic {
   public static final SpiritualMagicConfig.Curse CONFIG = ((SpiritualMagicConfig)ConfigRegistry.getConfig(SpiritualMagicConfig.class)).Curse;

   public CurseMagic() {
      super(SpiritualMagic.SpiritLevel.MEDIUM);
   }

   @Override
   public int getDefaultCastTime() {
      return CONFIG.castTime;
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
            MagicCircleVariant.NECROMANCY,
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
         MiasmicMistCloud mist = new MiasmicMistCloud(entity.level(), entity);
         mist.setLife(CONFIG.mistDuration);
         mist.setTickEachHit(CONFIG.mistInterval);
         mist.setDamage(instance.isMastered(entity) ? CONFIG.mistDamageMastered : CONFIG.mistDamage);
         int level = instance.isMastered(entity) ? CONFIG.curseLevelMastered : CONFIG.curseLevel;
         mist.setMobEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.CURSE), CONFIG.curseDuration, level - 1, true, false, true));
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

   public void onSkillMastered(ManasSkillInstance instance, LivingEntity entity) {
      if (!instance.isSubInstance()) {
         Optional<ManasRaceInstance> race = RaceAPI.getRaceFrom(entity).getRace();
         if (!race.isEmpty() && race.get().is(TensuraRaceTags.NECROMANCER)) {
            SkillHelper.learnSkill(entity, ((CurseBindMagic)SpiritualMagics.CURSE_BIND.get()).createLearningInstance(entity));
         }
      }
   }
}
