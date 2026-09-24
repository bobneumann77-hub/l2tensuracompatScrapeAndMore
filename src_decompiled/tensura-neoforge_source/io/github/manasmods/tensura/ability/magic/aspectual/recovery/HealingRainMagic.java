package io.github.manasmods.tensura.ability.magic.aspectual.recovery;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.magic.barrier.HealingRainEntity;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.registry.magic.AspectualMagics;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class HealingRainMagic extends AspectualMagic {
   private static final AspectualMagicConfig.HealingRain CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).HealingRain;

   public HealingRainMagic() {
      super(AspectualMagic.AspectualType.RECOVERY);
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
   public boolean canLearnSkill(ManasSkillInstance instance, LivingEntity entity) {
      if (!super.canLearnSkill(instance, entity)) {
         return false;
      }

      if (!SkillUtils.isSkillMastered(entity, (ManasSkill)AspectualMagics.HEALING.get())) {
         instance.setCoolDowns(TensuraSkill.BASE_CONFIG.Learning.learningFailCooldown);
         if (entity instanceof Player player) {
            player.playNotifySound((SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
            player.displayClientMessage(
               Component.translatable(
                     "tensura.skill.learn_points.failed_mastery",
                     new Object[]{instance.getChatDisplayName(false), ((HealingMagic)AspectualMagics.HEALING.get()).getChatDisplayName(false)}
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
            CONFIG.radius,
            25,
            MagicCircleVariant.RECOVERY,
            entity,
            instance.getOrCreateTag(),
            0.0F,
            new Vec3(0.0, CONFIG.radius * 2.0F + 2.0F, 0.0),
            instance,
            mode,
            Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
         );
      }
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (heldTicks >= this.getCastingTime(instance, entity)) {
         instance.addMasteryPoint(entity);
         HealingRainEntity rain = new HealingRainEntity(entity.level(), entity);
         rain.setFollowOwner(true);
         rain.setLife(CONFIG.duration);
         rain.setSize(CONFIG.radius);
         if (instance.isMastered(entity)) {
            rain.setDamage(CONFIG.hpHealMastered);
            rain.setPercentageHeal(CONFIG.hpHealPercentage);
         } else {
            rain.setDamage(CONFIG.hpHeal);
         }

         rain.setSkill(entity, instance, this, mode);
         rain.setPos(entity.getX(), entity.getY() + rain.getSize() * 2.0F, entity.getZ());
         entity.level().addFreshEntity(rain);
         instance.setCoolDown(instance.isMastered(entity) ? CONFIG.cooldownMastered : CONFIG.cooldown, mode);
         entity.level()
            .playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_HEAL.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
            );
         entity.swing(InteractionHand.MAIN_HAND, true);
      }
   }
}
