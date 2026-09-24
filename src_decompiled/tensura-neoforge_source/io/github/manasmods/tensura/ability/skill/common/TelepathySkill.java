package io.github.manasmods.tensura.ability.skill.common;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.CommonSkillConfig;
import io.github.manasmods.tensura.entity.template.subclass.ISubordinate;
import io.github.manasmods.tensura.registry.skill.CommonSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import io.github.manasmods.tensura.util.SubordinateHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

public class TelepathySkill extends Skill {
   private static final CommonSkillConfig.Telepathy CONFIG = ((CommonSkillConfig)ConfigRegistry.getConfig(CommonSkillConfig.class)).Telepathy;

   public TelepathySkill() {
      super(Skill.SkillType.COMMON);
   }

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      return newEP > CONFIG.epAcquirement;
   }

   @Override
   protected boolean canActivateInRaceLimit(ManasSkillInstance instance, int mode) {
      return true;
   }

   public void onSkillMastered(ManasSkillInstance instance, LivingEntity entity) {
      if (!instance.isSubInstance()) {
         SkillHelper.learnSkill(entity, ((ThoughtCommunicationSkill)CommonSkills.THOUGHT_COMMUNICATION.get()).createLearningInstance(entity));
      }
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      Mob mob = ObjectSelectionHelper.getTargetingEntity(Mob.class, entity, CONFIG.telepathyRadius, 0.2, false, false, false);
      if (mob != null && SubordinateHelper.isSubordinate(entity, mob)) {
         telepathy(instance, entity, mob);
      } else if (entity instanceof Player player) {
         player.displayClientMessage(Component.translatable("tensura.telepathy.subordinate.not_found").withStyle(ChatFormatting.RED), true);
      }
   }

   public static void telepathy(ManasSkillInstance instance, LivingEntity entity, Mob mob) {
      CompoundTag tag = instance.getOrCreateTag();
      if (tag.getInt("usedTimes") % BASE_CONFIG.Mastery.masteryActivateTime == 0) {
         instance.addMasteryPoint(entity);
      }

      tag.putInt("usedTimes", tag.getInt("usedTimes") + 1);
      if (entity instanceof Player player && mob instanceof ISubordinate tamable) {
         tamable.cycleCommands(mob, player);
      }

      entity.swing(InteractionHand.MAIN_HAND, true);
      entity.level()
         .playSound(
            null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 0.5F
         );
   }
}
