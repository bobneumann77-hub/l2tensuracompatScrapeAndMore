package io.github.manasmods.tensura.ability.skill.unique;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.skill.UniqueSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.SubordinateHelper;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class GuardianSkill extends Skill {
   public static final UniqueSkillConfig.Guardian CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).Guardian;
   protected static final ResourceLocation GUARDIAN = ResourceLocation.fromNamespaceAndPath("tensura", "guardian");

   public GuardianSkill() {
      super(Skill.SkillType.UNIQUE);
   }

   @Override
   public double getDefaultAcquiringMagiculeCost() {
      return CONFIG.mpAcquirement;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
      return instance.getMastery() >= 0.0;
   }

   public int getModes(ManasSkillInstance instance) {
      return 2;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      return mode == 0 ? 1 : 0;
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> "guardian.grant";
         case 1 -> "guardian.iron_wall";
         default -> super.getModeId(instance, mode);
      };
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
         Level level = entity.level();
         switch (mode) {
            case 0:
               entity.swing(InteractionHand.MAIN_HAND, true);
               level.playSound(
                  null,
                  entity.getX(),
                  entity.getY(),
                  entity.getZ(),
                  (SoundEvent)TensuraSoundEvents.DEFENCE_ACTIVATE.get(),
                  TensuraSkill.ABILITY_SOUND,
                  1.0F,
                  1.0F
               );
               List<LivingEntity> list = level.getEntitiesOfClass(
                  LivingEntity.class,
                  entity.getBoundingBox().inflate(CONFIG.protectionRadius),
                  living -> !entity.is(living) && living.isAlive() && living.isAlliedTo(entity)
               );
               if (list.isEmpty()) {
                  return;
               }

               instance.addMasteryPoint(entity);

               for (LivingEntity target : list) {
                  target.addEffect(
                     new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.GUARDED), CONFIG.protectionDuration, 0, false, false, false),
                     entity
                  );
                  TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.WAX_ON, 1.0);
               }
               break;
            case 1:
               entity.swing(InteractionHand.MAIN_HAND, true);
               AttributeInstance armor = entity.getAttribute(Attributes.ARMOR);
               if (armor != null && armor.getModifier(GUARDIAN) != null) {
                  armor.removeModifier(GUARDIAN);
                  AttributeInstance attributeInstance = entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
                  if (attributeInstance != null) {
                     attributeInstance.removeModifier(GUARDIAN);
                  }

                  level.playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.DEFENCE_DEACTIVATE.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
               } else {
                  if (armor != null) {
                     double amount = instance.isMastered(entity) ? CONFIG.wallArmorMastered : CONFIG.wallArmor;
                     AttributeModifier modifier = new AttributeModifier(GUARDIAN, amount, Operation.ADD_VALUE);
                     armor.addOrReplacePermanentModifier(modifier);
                  }

                  AttributeInstance knockBack = entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
                  double knockAmount = instance.isMastered(entity) ? CONFIG.wallKnockResistMastered : CONFIG.wallKnockResist;
                  if (knockBack != null && !knockBack.hasModifier(GUARDIAN)) {
                     knockBack.addOrReplacePermanentModifier(new AttributeModifier(GUARDIAN, knockAmount, Operation.ADD_VALUE));
                  }

                  level.playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.DEFENCE_ACTIVATE.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
               }
         }
      }
   }

   @Override
   public void onForgetSkill(ManasSkillInstance instance, LivingEntity entity) {
      super.onForgetSkill(instance, entity);
      AttributeInstance armor = entity.getAttribute(Attributes.ARMOR);
      if (armor != null) {
         armor.removeModifier(GUARDIAN);
      }

      AttributeInstance attributeInstance = entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
      if (attributeInstance != null) {
         attributeInstance.removeModifier(GUARDIAN);
      }

      entity.level()
         .playSound(
            null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.DEFENCE_DEACTIVATE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
         );
   }

   public static boolean onSubordinateHurt(LivingEntity subordinate, DamageSource source, Changeable<Float> amount) {
      if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
         return true;
      }

      if (source.tensura$getBarrierBypassLevel() >= 1.5) {
         return true;
      }

      if (SubordinateHelper.getSubordinateOwner(subordinate) instanceof Player player) {
         if (source.getEntity() == player) {
            return true;
         } else {
            Optional<ManasSkillInstance> guardian = SkillAPI.getSkillsFrom(player).getSkill((ManasSkill)UniqueSkills.GUARDIAN.get());
            if (guardian.isEmpty()) {
               return true;
            } else if (guardian.get().isToggled()) {
               player.hurt(source.tensura$setDodgeBypass(), (Float)amount.get());
               player.displayClientMessage(
                  Component.translatable("tensura.skill.guardian.substitution_notification", new Object[]{amount.get(), subordinate.getName()})
                     .setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)),
                  true
               );
               return false;
            } else {
               return true;
            }
         }
      } else {
         return true;
      }
   }
}
