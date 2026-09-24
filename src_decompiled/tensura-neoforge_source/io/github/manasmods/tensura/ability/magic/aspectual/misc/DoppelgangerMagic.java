package io.github.manasmods.tensura.ability.magic.aspectual.misc;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.ability.skill.extra.BodyDoubleSkill;
import io.github.manasmods.tensura.ability.subclass.ICloning;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.entity.human.CloneEntity;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class DoppelgangerMagic extends AspectualMagic implements ICloning {
   public static final AspectualMagicConfig.Doppelganger CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).Doppelganger;

   public DoppelgangerMagic() {
      super(AspectualMagic.AspectualType.MISC);
   }

   @Override
   public int getDefaultCastTime() {
      return CONFIG.castTime;
   }

   @Override
   public int getMasteryCastTime() {
      return CONFIG.castTimeMastered;
   }

   public int getMaxMastery() {
      return MAGIC_CONFIG.AspectualMagic.masteryMedium;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return EnergyHelper.getBaseMaxMagicule(entity) * CONFIG.magiculeCost / 5.0;
   }

   @Override
   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (heldTicks <= 0 && entity.isShiftKeyDown()) {
         CloneEntity clone = ObjectSelectionHelper.getTargetingEntity(CloneEntity.class, entity, 30.0, 0.2, false);
         if (clone != null && clone.isOwnedBy(entity) && clone.isAlive() && clone.getSkill().getSkill() == this) {
            DamageSource source = TensuraDamageTypes.getDamageSource(entity.level(), TensuraDamageTypes.ENERGY_SOURCE_LOST).tensura$setBarrierBypassLevel(3.0F);
            clone.hurt(source, clone.getMaxHealth());
            entity.level()
               .playSound(
                  null,
                  entity.getX(),
                  entity.getY(),
                  entity.getZ(),
                  (SoundEvent)TensuraSoundEvents.BUFF_DEACTIVATE.get(),
                  TensuraSkill.ABILITY_SOUND,
                  1.0F,
                  1.0F
               );
            return false;
         }
      }

      return super.onHeld(instance, entity, heldTicks, mode);
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (heldTicks >= this.getCastingTime(instance, entity)) {
         IExistence existence = TensuraStorages.getExistenceFrom(entity);
         if (existence.isSpiritualForm()) {
            entity.sendSystemMessage(Component.translatable("tensura.ability.activation_failed").withStyle(ChatFormatting.RED));
         } else {
            Level level = entity.level();
            double cost = EnergyHelper.getBaseMaxMagicule(entity) * CONFIG.magiculeCost;
            if (!this.isOutOfEnergy(entity, instance, (double)0.0, (double)cost)) {
               int cloneNumber = instance.isMastered(entity) && entity.isShiftKeyDown() ? CONFIG.cloneNumberMastered : CONFIG.cloneNumber;
               double EP = cost / cloneNumber;
               double currentEP = existence.getMagicule() * CONFIG.magiculeCost / cloneNumber;
               Vec3 center = entity.position();
               double distance = entity.getBbWidth() / 2.0F * cloneNumber;

               for (int i = 0; i < cloneNumber; i++) {
                  double angle = (Math.PI * 2) / cloneNumber * i;
                  double dx = Math.cos(angle) * distance;
                  double dz = Math.sin(angle) * distance;
                  Vec3 pos = center.add(dx, 0.0, dz);
                  CloneEntity clone = BodyDoubleSkill.summonClone(instance, entity, level, EP, currentEP, pos);
                  CloneEntity.copyEffects(entity, clone);

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
   }

   @Override
   public void onSubordinateDeath(ManasSkillInstance instance, LivingEntity owner, LivingEntity subordinate, DamageSource source) {
      if (subordinate instanceof CloneEntity clone) {
         if (clone.getSkill().getSkill() == this) {
            if (!clone.isStatic()) {
               IExistence existence = TensuraStorages.getExistenceFrom(clone);
               EnergyHelper.gainMagicule(owner, existence.getMagicule(), EnergyHelper.GainType.NORMAL);
               existence.setSkippingEPDrop(true);
               existence.markDirty();
               double size = clone.getAttributeValue(Attributes.SCALE) * 4.0;
               TensuraParticleHelper.addServerAuraParticles(clone, TensuraParticleUtils.getBlackAura(0.5F, (float)size, -0.3F), 10, 0.01);
               if (owner instanceof Player player) {
                  player.displayClientMessage(
                     Component.translatable("tensura.ep.acquire_mp", new Object[]{existence.getMagicule()})
                        .setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)),
                     false
                  );
                  player.playNotifySound((SoundEvent)TensuraSoundEvents.ENERGY_DRAIN.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 1.0F);
               }
            }
         }
      }
   }

   @Override
   public void onCloneTick(CloneEntity clone, LivingEntity owner) {
      if (TensuraStorages.getExistenceFrom(clone).getSleepModeTime() > 0) {
         clone.remove();
      } else {
         clone.addEffect(
            new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.FRAGILITY), 200, CONFIG.cloneFragility - 1, false, false, false)
         );
         clone.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.ENERGY_BLOCKADE), 200, 4, false, false, false));
         if (clone.tickCount % 20 == 0 && clone.getHealth() < clone.getMaxHealth() && clone.isAlive()) {
            clone.heal(CONFIG.cloneHeal);
            EnergyHelper.drainEnergy(clone, null, CONFIG.cloneHealEnergy, false, EnergyHelper.DrainType.EP, EnergyHelper.GainType.NONE);
         }

         if (owner.distanceToSqr(clone) > 2500.0) {
            clone.getNavigation().stop();
         }
      }
   }
}
