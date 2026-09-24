package io.github.manasmods.tensura.ability.skill.unique;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.effect.EffectStorage;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ItemStack;

public class BerserkerSkill extends Skill {
   private static final UniqueSkillConfig.Berserker CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).Berserker;
   public static final ResourceLocation BERSERKER = ResourceLocation.fromNamespaceAndPath("tensura", "berserker");

   public BerserkerSkill() {
      super(Skill.SkillType.UNIQUE);
   }

   @Override
   public double getDefaultAcquiringMagiculeCost() {
      return CONFIG.mpAcquirement;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
      return instance.getMastery() >= 0.0;
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      AttributeInstance aura = entity.getAttribute(TensuraAttributes.AURA_GAIN);
      if (aura != null && !aura.hasModifier(BERSERKER)) {
         aura.addOrReplacePermanentModifier(new AttributeModifier(BERSERKER, CONFIG.auraPercentage, Operation.ADD_VALUE));
      }

      AttributeInstance magicule = entity.getAttribute(TensuraAttributes.MAGICULE_GAIN);
      if (magicule != null && !magicule.hasModifier(BERSERKER)) {
         magicule.addOrReplacePermanentModifier(new AttributeModifier(BERSERKER, CONFIG.magiculePercentage, Operation.ADD_VALUE));
      }
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      AttributeInstance aura = entity.getAttribute(TensuraAttributes.AURA_GAIN);
      if (aura != null) {
         aura.removeModifier(BERSERKER);
      }

      AttributeInstance magicule = entity.getAttribute(TensuraAttributes.MAGICULE_GAIN);
      if (magicule != null) {
         magicule.removeModifier(BERSERKER);
      }
   }

   @Override
   public void onForgetSkill(ManasSkillInstance instance, LivingEntity entity) {
      super.onForgetSkill(instance, entity);
      this.deactivateBerserker(entity);
   }

   public boolean onTouchEntity(ManasSkillInstance instance, LivingEntity entity, LivingEntity target, DamageSource source, Changeable<Float> amount) {
      if (!this.isInSlot(entity, instance)) {
         return true;
      }

      if (source.getDirectEntity() == entity && TensuraDamageHelper.isPhysicalAttack(source)) {
         int durabilityBreak = (int)Math.max(1.0, ((Float)amount.get()).floatValue() * CONFIG.armorDurability);

         for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack slotStack = target.getItemBySlot(slot);
            slotStack.hurtAndBreak(durabilityBreak, target, slot);
         }
      }

      return true;
   }

   public boolean onTakenDamage(ManasSkillInstance instance, LivingEntity owner, DamageSource source, Changeable<Float> amount) {
      if (!this.isInSlot(owner, instance)) {
         return true;
      }

      if (source.getEntity() instanceof LivingEntity attacker) {
         attacker.getItemInHand(InteractionHand.MAIN_HAND).hurtAndBreak(CONFIG.weaponDurability, attacker, EquipmentSlot.MAINHAND);
      }

      return true;
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (this.deactivateBerserker(entity)) {
         return false;
      }

      if (heldTicks >= CONFIG.holdTime) {
         this.activateBerserker(instance, entity, mode);
         return false;
      }

      if (heldTicks % 2 == 0) {
         EffectStorage.setCameraShake(entity, 0.01F, 5);
         double size = entity.getAttributeValue(Attributes.SCALE) * 4.0;
         TensuraParticleHelper.addServerAuraParticles(entity, TensuraParticleUtils.getGoldAura(1.0F, (float)size, -0.3F), 1, 0.03);
         TensuraParticleHelper.spawnServerParticles(
            entity.level(),
            (ParticleOptions)TensuraParticleTypes.PURPLE_LIGHTNING_SPARK.get(),
            entity.getX(),
            entity.getY() + entity.getBbHeight() / 2.0F,
            entity.getZ(),
            3,
            0.08,
            0.08,
            0.08,
            0.2,
            true
         );
      }

      entity.level()
         .playSound(
            null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.TRANSFORM_BERSERKER.get(), TensuraSkill.ABILITY_SOUND, 0.2F, 1.0F
         );
      return true;
   }

   private void activateBerserker(ManasSkillInstance instance, LivingEntity entity, int mode) {
      double EP = EnergyHelper.getMaxEP(entity);
      AttributeInstance armor = entity.getAttribute(Attributes.ARMOR);
      if (armor != null) {
         AttributeModifier armorModifier = new AttributeModifier(BERSERKER, getArmor(EP), Operation.ADD_VALUE);
         armor.addOrReplacePermanentModifier(armorModifier);
         instance.setCoolDown(10, mode);
         EffectStorage.setCameraShake(entity, 10.0, 0.1F, 10);
         entity.level()
            .playSound(
               null,
               entity.getX(),
               entity.getY(),
               entity.getZ(),
               (SoundEvent)TensuraSoundEvents.TRANSFORM_BERSERKER.get(),
               TensuraSkill.ABILITY_SOUND,
               2.0F,
               1.0F
            );
         TensuraParticleHelper.addServerParticlesAroundSelf(entity, (ParticleOptions)TensuraParticleTypes.YELLOW_LIGHTNING_SPARK.get(), 3.0);
         TensuraParticleHelper.spawnServerParticles(
            entity.level(),
            (ParticleOptions)TensuraParticleTypes.PURPLE_LIGHTNING_SPARK.get(),
            entity.getX(),
            entity.getY() + entity.getBbHeight() / 2.0F,
            entity.getZ(),
            25,
            0.08,
            0.08,
            0.08,
            0.2,
            true
         );
         TensuraParticleHelper.spawnServerParticles(
            entity.level(),
            TensuraParticleUtils.getGoldWave(0.9F, entity.getBbWidth() * 6.0F, 0.1F, true),
            entity.getX(),
            entity.getY() + entity.getBbHeight() * 0.66F,
            entity.getZ()
         );
         TensuraParticleHelper.spawnServerParticles(
            entity.level(),
            TensuraParticleUtils.getPurpleWave(0.75F, entity.getBbWidth() * 7.0F, 0.1F, true),
            entity.getX(),
            entity.getY() + entity.getBbHeight() * 0.33F,
            entity.getZ()
         );
      }

      AttributeInstance damage = entity.getAttribute(Attributes.ATTACK_DAMAGE);
      if (damage != null) {
         damage.addOrReplacePermanentModifier(
            new AttributeModifier(BERSERKER, instance.isMastered(entity) ? getAttack(EP) * 2.0 : getAttack(EP), Operation.ADD_VALUE)
         );
      }

      AttributeInstance speed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
      if (speed != null) {
         speed.addOrReplacePermanentModifier(new AttributeModifier(BERSERKER, getSpeed(EP) / 100.0, Operation.ADD_VALUE));
      }
   }

   private boolean deactivateBerserker(LivingEntity entity) {
      AttributeInstance armor = entity.getAttribute(Attributes.ARMOR);
      if (armor != null && armor.removeModifier(BERSERKER)) {
         AttributeInstance damage = entity.getAttribute(Attributes.ATTACK_DAMAGE);
         if (damage != null) {
            damage.removeModifier(BERSERKER);
         }

         AttributeInstance speed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
         if (speed != null) {
            speed.removeModifier(BERSERKER);
         }

         entity.level()
            .playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.BUFF_DEACTIVATE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
            );
         return true;
      } else {
         return false;
      }
   }

   public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
      AttributeInstance attack = entity.getAttribute(Attributes.ATTACK_DAMAGE);
      return attack != null && attack.getModifier(BERSERKER) != null;
   }

   public void onTick(ManasSkillInstance instance, LivingEntity entity) {
      TensuraParticleHelper.addServerParticlesAroundSelf(entity, (ParticleOptions)TensuraParticleTypes.YELLOW_LIGHTNING_SPARK.get());
      if (!instance.isMastered(entity)) {
         CompoundTag tag = instance.getOrCreateTag();
         int time = tag.getInt("activatedTimes");
         if (time % BASE_CONFIG.Mastery.masteryActivateTime == 0) {
            instance.addMasteryPoint(entity);
         }

         tag.putInt("activatedTimes", time + 1);
      }
   }

   public static double getArmor(double EP) {
      return Math.min(CONFIG.armorMax, EP / CONFIG.armorEP);
   }

   public static double getAttack(double EP) {
      return EP <= CONFIG.attackEP ? CONFIG.attackBase : Math.min(CONFIG.attackMax, 5.0 + EP / CONFIG.attackEP);
   }

   public static double getSpeed(double EP) {
      return Math.min(CONFIG.speedMax, EP / CONFIG.speedEP);
   }
}
