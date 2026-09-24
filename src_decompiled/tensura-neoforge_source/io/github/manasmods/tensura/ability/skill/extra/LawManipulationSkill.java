package io.github.manasmods.tensura.ability.skill.extra;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.manascore.skill.api.Skills;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.ability.skill.unique.CookSkill;
import io.github.manasmods.tensura.config.ability.skill.ExtraSkillConfig;
import io.github.manasmods.tensura.data.TensuraSkillTags;
import io.github.manasmods.tensura.data.TensuraTags;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.entity.TensuraProjectile;
import io.github.manasmods.tensura.entity.magic.misc.WarpPortalEntity;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.TensuraStats;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.effect.EffectStorage;
import io.github.manasmods.tensura.util.AttributeHelper;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;

public class LawManipulationSkill extends Skill {
   public static final ExtraSkillConfig.LawManipulation CONFIG = ((ExtraSkillConfig)ConfigRegistry.getConfig(ExtraSkillConfig.class)).LawManipulation;
   public static final ResourceLocation LAW_MANIPULATION = ResourceLocation.fromNamespaceAndPath("tensura", "law_manipulation");

   public LawManipulationSkill() {
      super(Skill.SkillType.EXTRA);
   }

   public int getModes(ManasSkillInstance instance) {
      return 2;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      return mode == 0 ? (instance.isMastered(entity) ? 1 : -1) : 0;
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> "law_manipulation.cleanse";
         case 1 -> "law_manipulation.takeover";
         default -> super.getModeId(instance, mode);
      };
   }

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      if (newEP < CONFIG.epAcquirement) {
         return false;
      } else if (!SkillUtils.isSkillMastered(entity, (ManasSkill)ExtraSkills.MANA_MANIPULATION.get())) {
         return false;
      } else if (entity instanceof ServerPlayer player) {
         int magics = player.getStats().getValue(Stats.CUSTOM.get(TensuraStats.MAGIC_MASTERED));
         return magics >= CONFIG.magicMastered;
      } else {
         return false;
      }
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
      return instance.getMastery() >= 0.0;
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.addPermanentAttribute(entity, TensuraAttributes.LAW_DEGRADATION, LAW_MANIPULATION, 1.0, Operation.ADD_VALUE);
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.removeAttribute(entity, TensuraAttributes.LAW_DEGRADATION, LAW_MANIPULATION);
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (mode != 0) {
         TensuraProjectile projectile = ObjectSelectionHelper.getTargetingEntity(TensuraProjectile.class, entity, CONFIG.takeoverRange, 0.5, true, false, false);
         if (projectile != null && projectile.isAlive()) {
            ManasSkillInstance targetInstance = projectile.getSkill();
            if (targetInstance != null && targetInstance.is(TensuraSkillTags.MAGIC)) {
               if (projectile.getOwner() instanceof LivingEntity owner) {
                  if (owner.getAttributeValue(TensuraAttributes.LAW_DEGRADATION) > 0.0
                     && EnergyHelper.getMaxEP(owner) >= EnergyHelper.getMaxEP(entity) * CONFIG.takeoverMultiplier) {
                     entity.sendSystemMessage(Component.translatable("tensura.targeting.not_allowed").withStyle(ChatFormatting.RED));
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
                     return;
                  }

                  if (projectile.getType().equals(MiscEntityTypes.MAGIC_CIRCLE.get())) {
                     Skills skills = SkillAPI.getSkillsFrom(owner);
                     Optional<ManasSkillInstance> targetOptional = skills.getSkill(targetInstance.getSkill());
                     if (targetOptional.isPresent()) {
                        ManasSkillInstance targetSkill = targetOptional.get();
                        int cooldown = instance.isMastered(entity) ? CONFIG.brokenMagicCooldownMastered : CONFIG.brokenMagicCooldown;
                        targetSkill.setCoolDown(Math.max(targetSkill.getCoolDown(projectile.getMode()), cooldown), projectile.getMode());
                        skills.markDirty();
                     }

                     entity.level()
                        .playSound(
                           null,
                           projectile.getX(),
                           projectile.getY(),
                           projectile.getZ(),
                           (SoundEvent)TensuraSoundEvents.BARRIER_BREAK.get(),
                           TensuraSkill.ABILITY_SOUND,
                           0.75F,
                           1.0F
                        );
                     projectile.discard();
                  } else if (projectile instanceof WarpPortalEntity portal) {
                     entity.level()
                        .playSound(
                           null,
                           projectile.getX(),
                           projectile.getY(),
                           projectile.getZ(),
                           (SoundEvent)TensuraSoundEvents.CAST_SPACE.get(),
                           TensuraSkill.ABILITY_SOUND,
                           0.75F,
                           1.0F
                        );
                     portal.setRemoveIn(55);
                     portal.closeDestinationPortal();
                  } else {
                     projectile.setOwner(entity);
                     entity.level()
                        .playSound(
                           null,
                           projectile.getX(),
                           projectile.getY(),
                           projectile.getZ(),
                           (SoundEvent)TensuraSoundEvents.DEBUFF_ACTIVATE.get(),
                           TensuraSkill.ABILITY_SOUND,
                           0.75F,
                           1.0F
                        );
                     if (entity instanceof Player player) {
                        player.displayClientMessage(
                           Component.translatable("tensura.skill.mode.law_manipulation.takeover.success", new Object[]{projectile.getName()})
                              .withStyle(ChatFormatting.RED),
                           true
                        );
                     }
                  }
               }

               entity.swing(InteractionHand.MAIN_HAND, true);
               entity.level()
                  .playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
            } else {
               entity.sendSystemMessage(Component.translatable("tensura.ability.activation_failed").withStyle(ChatFormatting.RED));
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
            }
         } else {
            entity.sendSystemMessage(Component.translatable("tensura.ability.activation_failed").withStyle(ChatFormatting.RED));
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
         }
      } else {
         LivingEntity target = ObjectSelectionHelper.getTargetingEntity(entity, CONFIG.cleanseRange, false);
         boolean success;
         if (target != null && entity.isShiftKeyDown()) {
            success = EffectStorage.clearSeverance(target);
            success = CookSkill.removeCookedHP(target) || success;
            success = TensuraMobEffect.removePredicateEffect(target, getCleansingEffect()) || success;
            if (success) {
               entity.level()
                  .playSound(
                     null,
                     target.getX(),
                     target.getY(),
                     target.getZ(),
                     (SoundEvent)TensuraSoundEvents.DEBUFF_DEACTIVATE.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
               TensuraParticleHelper.spawnServerParticles(
                  target.level(),
                  TensuraParticleUtils.getPurpleWave(0.9F, target.getBbWidth() * 3.0F, -0.5F, true),
                  target.getX(),
                  target.getY() + target.getBbHeight() * 0.33,
                  target.getZ()
               );
               TensuraParticleHelper.spawnServerParticles(
                  target.level(),
                  TensuraParticleUtils.getBlackWave(0.9F, target.getBbWidth() * 3.0F, -0.5F, true),
                  target.getX(),
                  target.getY() + target.getBbHeight() * 0.5,
                  target.getZ()
               );
               TensuraParticleHelper.spawnServerParticles(
                  target.level(),
                  TensuraParticleUtils.getPurpleWave(0.9F, target.getBbWidth() * 3.0F, -0.5F, true),
                  target.getX(),
                  target.getY() + target.getBbHeight() * 0.66,
                  target.getZ()
               );
            }
         } else {
            success = EffectStorage.clearSeverance(entity);
            success = CookSkill.removeCookedHP(entity) || success;
            success = TensuraMobEffect.removePredicateEffect(entity, getCleansingEffect()) || success;
            if (success) {
               entity.level()
                  .playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.DEBUFF_DEACTIVATE.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
               TensuraParticleHelper.spawnServerParticles(
                  entity.level(),
                  TensuraParticleUtils.getPurpleWave(0.9F, entity.getBbWidth() * 3.0F, -0.5F, true),
                  entity.getX(),
                  entity.getY() + entity.getBbHeight() * 0.33,
                  entity.getZ()
               );
               TensuraParticleHelper.spawnServerParticles(
                  entity.level(),
                  TensuraParticleUtils.getBlackWave(0.9F, entity.getBbWidth() * 3.0F, -0.5F, true),
                  entity.getX(),
                  entity.getY() + entity.getBbHeight() * 0.5,
                  entity.getZ()
               );
               TensuraParticleHelper.spawnServerParticles(
                  entity.level(),
                  TensuraParticleUtils.getPurpleWave(0.9F, entity.getBbWidth() * 3.0F, -0.5F, true),
                  entity.getX(),
                  entity.getY() + entity.getBbHeight() * 0.66,
                  entity.getZ()
               );
            }
         }

         if (success) {
            instance.addMasteryPoint(entity);
            entity.swing(InteractionHand.MAIN_HAND, true);
            entity.level()
               .playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
         } else {
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
         }
      }
   }

   public static Predicate<Holder<MobEffect>> getCleansingEffect() {
      return effect -> effect.equals(TensuraMobEffects.getReference(TensuraMobEffects.MAGIC_INTERFERENCE))
         ? true
         : effect.is(TensuraTags.MobEffects.AFFECTED_BY_LAW_MANIPULATION);
   }
}
