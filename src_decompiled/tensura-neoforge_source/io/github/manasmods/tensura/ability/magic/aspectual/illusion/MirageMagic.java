package io.github.manasmods.tensura.ability.magic.aspectual.illusion;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.ability.skill.extra.BodyDoubleSkill;
import io.github.manasmods.tensura.ability.subclass.ICloning;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.entity.human.CloneEntity;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class MirageMagic extends AspectualMagic implements ICloning {
   public static final AspectualMagicConfig.Mirage CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).Mirage;

   public MirageMagic() {
      super(AspectualMagic.AspectualType.ILLUSION);
   }

   @Override
   public int getDefaultCastTime() {
      return CONFIG.castTime;
   }

   @Override
   public int getCastingTime(ManasSkillInstance instance, LivingEntity entity, boolean mastered) {
      return this.getCastingTime(instance, entity, mastered && entity.isShiftKeyDown() ? CONFIG.castTimeMastered : this.getDefaultCastTime());
   }

   public int getMaxMastery() {
      return MAGIC_CONFIG.AspectualMagic.masteryMedium;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   @Override
   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (heldTicks <= 0 && entity.isShiftKeyDown()) {
         CloneEntity clone = ObjectSelectionHelper.getTargetingEntity(CloneEntity.class, entity, 30.0, 0.2, false);
         if (clone != null && clone.isOwnedBy(entity) && clone.isAlive() && clone.getSkill().getSkill() == this) {
            clone.remove();
            return false;
         }
      }

      return super.onHeld(instance, entity, heldTicks, mode);
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (heldTicks >= this.getCastingTime(instance, entity)) {
         if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            Level level = entity.level();
            int cloneNumber = instance.isMastered(entity) && entity.isShiftKeyDown() ? CONFIG.cloneNumberMastered : CONFIG.cloneNumber;
            double EP = CONFIG.cloneEP;
            int duration = instance.isMastered(entity) ? CONFIG.cloneDurationMastered : CONFIG.cloneDuration;
            AttributeInstance illusionBoost = entity.getAttribute(TensuraAttributes.ILLUSION_BOOST);
            if (illusionBoost != null) {
               duration = (int)(duration * illusionBoost.getValue());
            }

            Vec3 center = entity.position();
            double distance = entity.getBbWidth() / 2.0F * cloneNumber;

            for (int i = 0; i < cloneNumber; i++) {
               double angle = (Math.PI * 2) / cloneNumber * i;
               double dx = Math.cos(angle) * distance;
               double dz = Math.sin(angle) * distance;
               Vec3 pos = center.add(dx, 0.0, dz);
               CloneEntity clone = BodyDoubleSkill.summonClone(instance, entity, level, EP, EP, CloneEntity.CopySkill.NONE, pos);
               clone.setLife(duration);
               clone.setIllusion(true);

               for (EquipmentSlot slot : EquipmentSlot.values()) {
                  clone.addFakeItem(slot, entity.getItemBySlot(slot).copy());
               }
            }

            instance.addMasteryPoint(entity);
            instance.setCoolDown(instance.isMastered(entity) ? CONFIG.cooldownMastered : CONFIG.cooldown, mode);
            entity.swing(InteractionHand.MAIN_HAND, true);
            level.playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_SPLIT.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
            );
         }
      }
   }

   @Override
   public void onCloneTick(CloneEntity clone, LivingEntity owner) {
      IExistence existence = TensuraStorages.getExistenceFrom(clone);
      if (!(existence.getSpiritualHealth() < clone.getAttributeValue(TensuraAttributes.MAX_SPIRITUAL_HEALTH)) && existence.getSleepModeTime() <= 0) {
         clone.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.ENERGY_BLOCKADE), 200, 4, false, false, false));
         if (owner.distanceToSqr(clone) > 2500.0) {
            clone.getNavigation().stop();
         }
      } else {
         clone.remove();
      }
   }
}
