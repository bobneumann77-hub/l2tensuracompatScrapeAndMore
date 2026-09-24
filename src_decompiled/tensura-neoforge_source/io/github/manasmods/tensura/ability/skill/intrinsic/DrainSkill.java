package io.github.manasmods.tensura.ability.skill.intrinsic;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.IntrinsicSkillConfig;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.data.TensuraSkillTags;
import io.github.manasmods.tensura.event.TensuraSkillEvents;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.race.RaceUtils;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;

public class DrainSkill extends Skill {
   private static final IntrinsicSkillConfig.Drain CONFIG = ((IntrinsicSkillConfig)ConfigRegistry.getConfig(IntrinsicSkillConfig.class)).Drain;

   public DrainSkill() {
      super(Skill.SkillType.INTRINSIC);
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      LivingEntity target = ObjectSelectionHelper.getTargetingEntity(entity, CONFIG.range, false);
      if (target != null && target.isAlive()) {
         if (RaceUtils.isBloodless(target)) {
            entity.sendSystemMessage(Component.translatable("tensura.targeting.not_allowed").withStyle(ChatFormatting.RED));
         } else {
            if (target.hurt(this.createSource(instance, entity, TensuraDamageTypes.BLOOD_DRAIN, mode), CONFIG.drainAmount)) {
               entity.heal(CONFIG.drainAmount);
               instance.addMasteryPoint(entity);
               TensuraParticleHelper.addServerParticlesAroundSelf(target, DustParticleOptions.REDSTONE);
               entity.level()
                  .playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.ENERGY_DRAIN.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
               List<ManasSkillInstance> collection = SkillAPI.getSkillsFrom(target).getLearnedSkills().stream().filter(this::isDrainable).toList();
               if (collection.isEmpty()) {
                  return;
               }

               ManasSkill skill = collection.get(target.getRandom().nextInt(collection.size())).getSkill();
               if (!SkillUtils.hasSkillFully(entity, skill)) {
                  Changeable<ManasSkill> changeable = Changeable.of(skill);
                  if (!((TensuraSkillEvents.SkillPlunderEvent)TensuraSkillEvents.SKILL_PLUNDER.invoker()).plunder(target, entity, false, changeable).isFalse()
                     && SkillHelper.learnSkill(entity, (ManasSkill)changeable.get(), CONFIG.temporaryDuration)) {
                     instance.setCoolDown(CONFIG.temporaryDuration, mode);
                     entity.level()
                        .playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_LEVELUP, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
                  }
               } else {
                  entity.sendSystemMessage(
                     Component.translatable("tensura.skill.temporary.already_have", new Object[]{skill.getName()}).withStyle(ChatFormatting.RED)
                  );
               }
            }
         }
      }
   }

   public boolean isDrainable(ManasSkillInstance instance) {
      if (instance.is(TensuraSkillTags.NO_PLUNDERING)) {
         return false;
      } else if (instance.is(TensuraSkillTags.INTRINSIC_SKILLS)) {
         return true;
      } else {
         return instance.is(TensuraSkillTags.COMMON_SKILLS) ? true : instance.is(TensuraSkillTags.EXTRA_SKILLS);
      }
   }
}
