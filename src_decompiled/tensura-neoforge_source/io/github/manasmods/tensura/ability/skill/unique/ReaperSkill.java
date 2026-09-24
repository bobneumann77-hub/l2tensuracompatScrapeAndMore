package io.github.manasmods.tensura.ability.skill.unique;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.impl.TickingSkill;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.ability.subclass.ICloning;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.entity.human.CloneEntity;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.entity.HumanEntityTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ReaperSkill extends Skill implements ICloning {
   private static final UniqueSkillConfig.Reaper CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).Reaper;
   public static final ResourceLocation REAPER = ResourceLocation.fromNamespaceAndPath("tensura", "reaper");

   public ReaperSkill() {
      super(Skill.SkillType.UNIQUE);
   }

   @Override
   public double getDefaultAcquiringMagiculeCost() {
      return CONFIG.mpAcquirement;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
      AttributeInstance recon = entity.getAttribute(TensuraAttributes.MAX_MAGICULE);
      return recon == null ? false : !recon.hasModifier(REAPER);
   }

   public boolean canScroll(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return mode == 0 && instance.getMastery() >= 0.0;
   }

   public int getModes(ManasSkillInstance instance) {
      return 2;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      return mode == 0 ? 1 : 0;
   }

   @Override
   public List<Integer> getModeLearningList(ManasSkillInstance instance) {
      return List.of(1);
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> "reaper.attack";
         case 1 -> "reaper.eater";
         default -> super.getModeId(instance, mode);
      };
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      AttributeInstance size = entity.getAttribute(Attributes.SCALE);
      if (size != null) {
         size.addOrReplacePermanentModifier(new AttributeModifier(REAPER, CONFIG.size - 1.0, Operation.ADD_MULTIPLIED_BASE));
         TensuraParticleHelper.addServerAuraParticles(entity, TensuraParticleUtils.getBlackAura(0.5F, (float)size.getValue() * 4.0F, -0.3F), 10, 0.01);
      }

      AttributeInstance sense = entity.getAttribute(TensuraAttributes.PRESENCE_SENSE);
      if (sense != null) {
         sense.addOrReplacePermanentModifier(new AttributeModifier(REAPER, CONFIG.bonusSenseLevel, Operation.ADD_VALUE));
      }

      AttributeInstance senseRadius = entity.getAttribute(TensuraAttributes.PRESENCE_SENSE_RADIUS);
      if (senseRadius != null) {
         senseRadius.addOrReplacePermanentModifier(
            new AttributeModifier(REAPER, instance.isMastered(entity) ? CONFIG.bonusSenseRadiusMastered : CONFIG.bonusSenseRadius, Operation.ADD_VALUE)
         );
      }

      double meleeAmount = instance.isMastered(entity) ? CONFIG.meleeDodgeMastered : CONFIG.meleeDodge;
      AttributeInstance melee = entity.getAttribute(TensuraAttributes.AUTO_MELEE_DODGE_CHANCE);
      if (melee != null) {
         melee.addOrReplacePermanentModifier(new AttributeModifier(REAPER, meleeAmount, Operation.ADD_VALUE));
      }

      double projectileAmount = instance.isMastered(entity) ? CONFIG.projectileDodgeMastered : CONFIG.projectileDodge;
      AttributeInstance projectile = entity.getAttribute(TensuraAttributes.AUTO_PROJECTILE_DODGE_CHANCE);
      if (projectile != null) {
         projectile.addOrReplacePermanentModifier(new AttributeModifier(REAPER, projectileAmount, Operation.ADD_VALUE));
      }
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      AttributeInstance size = entity.getAttribute(Attributes.SCALE);
      if (size != null) {
         size.removeModifier(REAPER);
         TensuraParticleHelper.addServerAuraParticles(entity, TensuraParticleUtils.getBlackAura(0.5F, (float)size.getValue() * 4.0F, -0.3F), 10, 0.01);
      }

      AttributeInstance sense = entity.getAttribute(TensuraAttributes.PRESENCE_SENSE);
      if (sense != null) {
         sense.removeModifier(REAPER);
      }

      AttributeInstance senseRadius = entity.getAttribute(TensuraAttributes.PRESENCE_SENSE_RADIUS);
      if (senseRadius != null) {
         senseRadius.removeModifier(REAPER);
      }

      AttributeInstance melee = entity.getAttribute(TensuraAttributes.AUTO_MELEE_DODGE_CHANCE);
      if (melee != null) {
         melee.removeModifier(REAPER);
      }

      AttributeInstance projectile = entity.getAttribute(TensuraAttributes.AUTO_PROJECTILE_DODGE_CHANCE);
      if (projectile != null) {
         projectile.removeModifier(REAPER);
      }
   }

   private boolean isUnableToActivateEater(ManasSkillInstance instance, LivingEntity entity) {
      if (instance.isToggled()) {
         return true;
      }

      AttributeInstance recon = entity.getAttribute(Attributes.SCALE);
      return recon != null && recon.hasModifier(REAPER);
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (mode == 1) {
         Level level = entity.level();
         if (this.isUnableToActivateEater(instance, entity)) {
            if (entity instanceof Player player) {
               String message = instance.isToggled() ? "tensura.skill.mode.need_toggle_off" : "tensura.ability.activation_failed";
               player.displayClientMessage(Component.translatable(message, new Object[]{this.getName()}).withStyle(ChatFormatting.RED), true);
            }
         } else if (!this.learnMode(instance, entity, mode)) {
            CompoundTag tag = instance.getOrCreateTag();
            double range = entity.getAttributeValue(Attributes.ENTITY_INTERACTION_RANGE);
            LivingEntity target = ObjectSelectionHelper.getTargetingEntity(entity, range + CONFIG.eaterBonusRange, false);
            if (target != null && target.isAlive()) {
               if (!(target instanceof Player player && player.getAbilities().invulnerable)) {
                  double targetEP = TensuraStorages.getExistenceFrom(target).getEP();
                  if (targetEP > TensuraStorages.getExistenceFrom(entity).getEP()) {
                     if (entity instanceof Player player) {
                        player.displayClientMessage(Component.translatable("tensura.targeting.ep_not_meet").withStyle(ChatFormatting.RED), false);
                     }

                     level.playSound(
                        null,
                        entity.getX(),
                        entity.getY(),
                        entity.getZ(),
                        (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                        TensuraSkill.ABILITY_SOUND,
                        1.0F,
                        1.0F
                     );
                  } else if (!EnergyHelper.isOutOfEnergy(entity, 0.0, targetEP)) {
                     DamageSource source = this.createSource(instance, entity, TensuraDamageTypes.INFINITE_EATER, mode);
                     if (target.hurt(source, target.getMaxHealth() * 10.0F)) {
                        entity.swing(InteractionHand.MAIN_HAND, true);
                        double amountToMax = CONFIG.eaterEPSteal;
                        double EP = Math.min(EnergyHelper.getEPGain(target, entity), EnergyHelper.CONFIG.maximumEPSteal / amountToMax);
                        EnergyHelper.gainMagicule(entity, EP * amountToMax, EnergyHelper.GainType.NORMAL);
                        instance.addMasteryPoint(entity);
                        instance.setCoolDown(instance.isMastered(entity) ? CONFIG.eaterCooldownMastered : CONFIG.eaterCooldown, mode);
                        level.playSound(
                           null,
                           entity.getX(),
                           entity.getY(),
                           entity.getZ(),
                           (SoundEvent)TensuraSoundEvents.EATER.get(),
                           TensuraSkill.ABILITY_SOUND,
                           1.0F,
                           1.0F
                        );
                        double size = target.getAttributeValue(Attributes.SCALE) * 4.0;
                        TensuraParticleHelper.addServerAuraParticles(target, TensuraParticleUtils.getBlackAura(0.5F, (float)size, -0.3F), 10, 0.01);
                        if (instance.isTemporarySkill()) {
                           return;
                        }

                        if (tag.contains("eatenList")) {
                           CompoundTag eatenList = (CompoundTag)tag.get("eatenList");
                           if (eatenList == null) {
                              return;
                           }

                           String targetID = EntityType.getKey(target.getType()).toString();
                           if (eatenList.contains(targetID)) {
                              return;
                           }

                           eatenList.putBoolean(targetID, true);
                           instance.markDirty();
                        } else {
                           CompoundTag eatenList = new CompoundTag();
                           eatenList.putBoolean(EntityType.getKey(target.getType()).toString(), true);
                           tag.put("eatenList", eatenList);
                           instance.markDirty();
                        }

                        if (target.getType().is(TensuraEntityTags.NO_EP_PLUNDER)) {
                           return;
                        }

                        if (EnergyHelper.drainEnergy(target, entity, EP * amountToMax, false, EnergyHelper.DrainType.MAX_EP, EnergyHelper.GainType.NONE)) {
                           EnergyHelper.gainMagicule(entity, EP * amountToMax, EnergyHelper.GainType.MAX);
                           TensuraStorages.getExistenceFrom(target).setSkippingEPDrop(true);
                        }
                     }
                  }
               }
            } else {
               entity.sendSystemMessage(Component.translatable("tensura.targeting.not_targeted").withStyle(ChatFormatting.RED));
               level.playSound(
                  null,
                  entity.getX(),
                  entity.getY(),
                  entity.getZ(),
                  (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                  TensuraSkill.ABILITY_SOUND,
                  1.0F,
                  1.0F
               );
               instance.setCoolDown(instance.isMastered(entity) ? CONFIG.eaterCooldownMastered : CONFIG.eaterCooldown, mode);
            }
         }
      }
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (mode == 0 && TickingSkill.isTickingSkill(entity, this, 0)) {
         if (instance.isToggled()) {
            if (entity instanceof Player player) {
               player.displayClientMessage(
                  Component.translatable("tensura.skill.mode.need_toggle_off", new Object[]{instance.getChatDisplayName(true)}).withStyle(ChatFormatting.RED),
                  true
               );
            }
         } else {
            Level level = entity.level();
            AttributeInstance recon = entity.getAttribute(Attributes.SCALE);
            if (recon != null) {
               AttributeInstance aura = entity.getAttribute(TensuraAttributes.MAX_AURA);
               AttributeInstance magicule = entity.getAttribute(TensuraAttributes.MAX_MAGICULE);
               if (recon.hasModifier(REAPER)) {
                  recon.removeModifier(REAPER);
                  if (aura != null) {
                     aura.removeModifier(REAPER);
                  }

                  if (magicule != null) {
                     magicule.removeModifier(REAPER);
                  }

                  this.updateCurrentEP(entity, 1.0F / CONFIG.attackMultiplier);
                  instance.setCoolDown(CONFIG.attackCooldown, mode);
                  level.playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.BUFF_DEACTIVATE.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
               } else {
                  CompoundTag tag = instance.getTag();
                  if (tag == null) {
                     this.summonClones(instance, entity, level, CONFIG.attackNumber);
                  } else {
                     this.summonClones(instance, entity, level, Math.max(tag.getInt("clones"), 1));
                  }

                  AttributeModifier reaper = new AttributeModifier(REAPER, CONFIG.attackMultiplier - 1.0F, Operation.ADD_MULTIPLIED_TOTAL);
                  recon.addOrReplacePermanentModifier(reaper);
                  instance.addMasteryPoint(entity);
                  if (aura != null) {
                     aura.addOrReplacePermanentModifier(reaper);
                  }

                  if (magicule != null) {
                     magicule.addOrReplacePermanentModifier(reaper);
                  }

                  this.updateCurrentEP(entity, CONFIG.attackMultiplier);
               }
            }
         }
      }
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (mode != 0) {
         return false;
      }

      if (this.isUnableToActivateEater(instance, entity)) {
         return true;
      }

      CompoundTag tag = instance.getTag();
      int clones = tag != null ? Math.max(tag.getInt("clones"), 1) : CONFIG.attackNumber;
      if (entity instanceof Player player) {
         player.displayClientMessage(
            Component.translatable("tensura.skill.output_number", new Object[]{clones}).setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_AQUA)), true
         );
      }

      return true;
   }

   public void onScroll(ManasSkillInstance instance, LivingEntity living, double delta, int mode) {
      CompoundTag tag = instance.getOrCreateTag();
      int newScale = tag.getInt("clones") + (int)delta;
      if (newScale > CONFIG.attackNumber) {
         newScale = 1;
      } else if (newScale < 1) {
         newScale = CONFIG.attackNumber;
      }

      if (tag.getInt("clones") != newScale) {
         tag.putInt("clones", newScale);
         instance.markDirty();
      }
   }

   public boolean onDeath(ManasSkillInstance instance, LivingEntity entity, DamageSource source) {
      AttributeInstance recon = entity.getAttribute(Attributes.SCALE);
      if (recon == null) {
         return true;
      }

      AttributeInstance aura = entity.getAttribute(TensuraAttributes.MAX_AURA);
      AttributeInstance magicule = entity.getAttribute(TensuraAttributes.MAX_MAGICULE);
      if (recon.getModifier(REAPER) != null) {
         recon.removeModifier(REAPER);
         if (aura != null) {
            aura.removeModifier(REAPER);
         }

         if (magicule != null) {
            magicule.removeModifier(REAPER);
         }

         this.updateCurrentEP(entity, 1.0F / CONFIG.attackMultiplier);
      }

      return true;
   }

   private void summonClones(ManasSkillInstance instance, LivingEntity entity, Level level, int number) {
      level.playSound(
         null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_SPLIT.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
      );
      TensuraParticleHelper.addServerAuraParticles(
         entity, TensuraParticleUtils.getBlackAura(0.5F, (float)entity.getAttributeValue(Attributes.SCALE) * 4.0F, -0.3F), 10, 0.01
      );
      double AP = TensuraStorages.getExistenceFrom(entity).getAura() * CONFIG.attackMultiplier / number;
      double MP = TensuraStorages.getExistenceFrom(entity).getMagicule() * CONFIG.attackMultiplier / number;

      float size = switch (number) {
         case 1 -> 0.5F;
         case 2 -> 0.42F;
         case 3 -> 0.37F;
         case 4 -> 0.33F;
         default -> 0.3F;
      };
      EntityType<CloneEntity> type = (EntityType<CloneEntity>)HumanEntityTypes.CLONE.get();

      for (int i = 0; i < number; i++) {
         CloneEntity clone = new CloneEntity(type, level);
         if (entity instanceof Player player) {
            clone.tame(player);
         }

         clone.setSkill(instance);
         clone.copyStatsAndSkills(entity, CloneEntity.CopySkill.ALL, true);
         CloneEntity.copyStatusEffect(entity, clone, CloneEntity.CopySkill.ALL, false);
         EnergyHelper.setMaxAura(clone, AP);
         EnergyHelper.setMaxMagicule(clone, MP);
         AttributeInstance scale = clone.getAttribute(Attributes.SCALE);
         if (scale != null) {
            AttributeModifier reaper = new AttributeModifier(
               ResourceLocation.fromNamespaceAndPath("tensura", "reaper_clone"), size - 1.0F, Operation.ADD_MULTIPLIED_TOTAL
            );
            scale.addOrReplacePermanentModifier(reaper);
         }

         IExistence existence = TensuraStorages.getExistenceFrom(clone);
         existence.setAura(AP);
         existence.setMagicule(MP);
         existence.markDirty();
         clone.setPos(entity.position());
         level.addFreshEntity(clone);
      }
   }

   private void updateCurrentEP(LivingEntity entity, float multiplier) {
      IExistence existence = TensuraStorages.getExistenceFrom(entity);
      existence.setAura(existence.getAura() * multiplier);
      existence.setMagicule(existence.getMagicule() * multiplier);
      existence.markDirty();
   }

   @Override
   public void onCloneTick(CloneEntity clone, LivingEntity owner) {
      if (TensuraStorages.getExistenceFrom(clone).getSleepModeTime() > 0) {
         clone.remove();
      } else {
         AttributeInstance recon = owner.getAttribute(Attributes.SCALE);
         if (recon == null) {
            clone.remove();
         } else if (recon.getModifier(REAPER) == null) {
            clone.remove();
         }
      }
   }
}
