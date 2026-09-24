package io.github.manasmods.tensura.ability.battlewill.utility;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.battlewill.Battlewill;
import io.github.manasmods.tensura.config.ability.BattlewillConfig;
import io.github.manasmods.tensura.entity.magic.shield.ShieldEntity;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

public class AuraShieldArt extends Battlewill {
   private static final BattlewillConfig.AuraShield CONFIG = ((BattlewillConfig)ConfigRegistry.getConfig(BattlewillConfig.class)).AuraShield;

   @Override
   public double getAuraCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.auraCost;
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (heldTicks == 0) {
         if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            return false;
         }

         instance.addMasteryPoint(entity);
         entity.level()
            .playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.DEFENCE_ACTIVATE.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 1.0F
            );
      }

      ShieldEntity shield = ShieldEntity.createShield(
         (EntityType<? extends ShieldEntity>)MiscEntityTypes.AURA_SHIELD.get(), entity, instance, this, mode, 40, 3.0F, CONFIG.health, CONFIG.size
      );
      if (shield != null && shield.getHealth() <= 0.0F) {
         instance.setCoolDown(instance.isMastered(entity) ? CONFIG.cooldownMastered : CONFIG.cooldown, mode);
         return false;
      } else {
         return true;
      }
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      CompoundTag tag = instance.getOrCreateTag();
      if (instance.onCoolDown(mode)) {
         tag.putInt("ShieldEntity", 0);
      } else {
         ShieldEntity shield = ShieldEntity.createShield(
            (EntityType<? extends ShieldEntity>)MiscEntityTypes.AURA_SHIELD.get(), entity, instance, this, mode, 0, 3.0F, CONFIG.health, CONFIG.size
         );
         if (shield != null) {
            shield.setDistanceFromOwner(0.0F);
            instance.setCoolDown(instance.isMastered(entity) ? CONFIG.cooldownMastered : CONFIG.cooldown, mode);
         }

         tag.putInt("ShieldEntity", 0);
      }
   }
}
