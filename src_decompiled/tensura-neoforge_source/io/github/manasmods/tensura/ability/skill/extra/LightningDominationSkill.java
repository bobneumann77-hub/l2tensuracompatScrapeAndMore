package io.github.manasmods.tensura.ability.skill.extra;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.entity.magic.lightning.LightningBolt;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.AttributeHelper;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class LightningDominationSkill extends Skill {
   public LightningDominationSkill() {
      super(Skill.SkillType.EXTRA);
   }

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      return !SkillUtils.isSkillMastered(entity, (ManasSkill)ExtraSkills.LIGHTNING_MANIPULATION.get())
         ? false
         : newEP > LightningManipulationSkill.CONFIG.dominationEpAcquirement;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return LightningManipulationSkill.CONFIG.magiculeCost;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
      return instance.getMastery() >= 0.0;
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.multiplyElementalBoost(entity, TensuraAttributes.LIGHTNING_BOOST, LightningManipulationSkill.CONFIG.dominationBoost);
      if (instance.isMastered(entity)) {
         AttributeHelper.applyDominationDegradation(
            entity, TensuraAttributes.LIGHTNING_RESIST_DEGRADATION, LightningManipulationSkill.CONFIG.resistDegradationAcquirement
         );
      }
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.removeElementalMultiplier(entity, TensuraAttributes.LIGHTNING_BOOST, LightningManipulationSkill.CONFIG.dominationBoost);
      AttributeHelper.removeDominationDegradation(entity, TensuraAttributes.LIGHTNING_RESIST_DEGRADATION);
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      Level level = entity.level();
      if (!level.isThundering()) {
         entity.sendSystemMessage(Component.translatable("tensura.ability.activation_failed.time").withStyle(ChatFormatting.RED));
         entity.level()
            .playSound(
               null,
               entity.getX(),
               entity.getY(),
               entity.getZ(),
               (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
               TensuraSkill.ABILITY_SOUND,
               1.0F,
               1.0F
            );
      } else if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
         instance.addMasteryPoint(entity);
         Entity target = ObjectSelectionHelper.getTargetingEntity(entity, 30.0, false, false);
         Vec3 pos;
         if (target != null) {
            pos = target.position();
         } else {
            BlockHitResult result = ObjectSelectionHelper.getPlayerPOVHitResult(level, entity, Fluid.NONE, 30.0);
            pos = result.getLocation();
         }

         LightningBolt bolt = new LightningBolt(level, entity);
         bolt.setCause(entity instanceof ServerPlayer serverPlayer ? serverPlayer : null);
         bolt.setSkill(entity, instance, this, mode);
         bolt.setTensuraDamage(LightningManipulationSkill.CONFIG.boltDamageDomination);
         bolt.setAdditionalVisual(8);
         bolt.setRadius(LightningManipulationSkill.CONFIG.boltRangeDomination);
         bolt.setSkill(instance);
         bolt.setPos(pos);
         level.addFreshEntity(bolt);
         entity.swing(InteractionHand.MAIN_HAND, true);
         entity.playSound((SoundEvent)TensuraSoundEvents.CAST_LIGHTNING.get());
      }
   }
}
