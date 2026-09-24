package io.github.manasmods.tensura.ability.skill.intrinsic;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.IntrinsicSkillConfig;
import io.github.manasmods.tensura.entity.magic.breath.BreathEntity;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

public class IceBreathSkill extends Skill {
   private static final IntrinsicSkillConfig.IceBreath CONFIG = ((IntrinsicSkillConfig)ConfigRegistry.getConfig(IntrinsicSkillConfig.class)).IceBreath;

   public IceBreathSkill() {
      super(Skill.SkillType.INTRINSIC);
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      instance.getOrCreateTag().putInt("BreathEntity", 0);
      instance.markDirty();
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (heldTicks % 20 == 0 && EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
         return false;
      }

      if (heldTicks % BASE_CONFIG.Mastery.masteryHoldTick == 0 && heldTicks > 0) {
         instance.addMasteryPoint(entity);
      }

      float damage = instance.isMastered(entity) ? CONFIG.damageMastered : CONFIG.damage;
      BreathEntity.spawnBreathEntity((EntityType<? extends BreathEntity>)MiscEntityTypes.ICE_BREATH.get(), entity, instance, damage, this, mode);
      if (heldTicks % 2 == 0) {
         entity.level()
            .playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_ICE.get(), TensuraSkill.ABILITY_SOUND, 0.75F, 1.0F
            );
      }

      return true;
   }
}
