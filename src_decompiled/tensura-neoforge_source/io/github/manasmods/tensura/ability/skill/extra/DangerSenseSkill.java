package io.github.manasmods.tensura.ability.skill.extra;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.ExtraSkillConfig;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class DangerSenseSkill extends Skill {
   private static final ExtraSkillConfig.DangerSense CONFIG = ((ExtraSkillConfig)ConfigRegistry.getConfig(ExtraSkillConfig.class)).DangerSense;

   public DangerSenseSkill() {
      super(Skill.SkillType.EXTRA);
   }

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      return newEP > CONFIG.epAcquirement;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
      return instance.getMastery() >= 0.0;
   }

   @Override
   public boolean canBeSlotted(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return instance.getMastery() < 0.0;
   }

   @Override
   public void onLearnSkill(ManasSkillInstance instance, LivingEntity entity) {
      super.onLearnSkill(instance, entity);
      if (!(instance.getMastery() < 0.0) && !instance.isTemporarySkill()) {
         SkillHelper.learnSkill(entity, ((MagicSenseSkill)ExtraSkills.MAGIC_SENSE.get()).createLearningInstance(entity));
      }
   }

   public void onSkillMastered(ManasSkillInstance instance, LivingEntity entity) {
      if (!instance.isSubInstance()) {
         if (SkillUtils.hasSkill(entity, (ManasSkill)ExtraSkills.MAGIC_SENSE.get())) {
            SkillHelper.learnSkill(entity, ((SenseSoundwaveSkill)ExtraSkills.SENSE_SOUNDWAVE.get()).createLearningInstance(entity));
         }
      }
   }

   public boolean onBeingTargeted(ManasSkillInstance instance, Changeable<LivingEntity> owner, LivingEntity attacker) {
      if (!instance.isToggled()) {
         return true;
      }

      if (owner.get() instanceof ServerPlayer player) {
         if (!(attacker instanceof Mob mob)) {
            return true;
         } else {
            if (mob.getTarget() == null || !player.is(mob.getTarget())) {
               if (player.getRandom().nextBoolean()) {
                  instance.addMasteryPoint(player);
               }

               this.sendSound(player, mob);
            }

            return true;
         }
      } else {
         return true;
      }
   }

   private void sendSound(ServerPlayer user, LivingEntity target) {
      Vec3 eyeVec = user.getEyePosition();
      Vec3 soundPos = eyeVec.add(target.getEyePosition().subtract(eyeVec).normalize().scale(5.0));
      user.connection
         .send(
            new ClientboundSoundPacket(
               BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.BELL_BLOCK),
               TensuraSkill.ABILITY_SOUND,
               soundPos.x(),
               eyeVec.y(),
               soundPos.z(),
               1.0F,
               1.0F,
               user.getRandom().nextLong()
            )
         );
   }
}
