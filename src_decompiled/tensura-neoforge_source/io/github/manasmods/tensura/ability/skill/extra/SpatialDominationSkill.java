package io.github.manasmods.tensura.ability.skill.extra;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.ManasSkill.AttributeTemplate;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.ExtraSkillConfig;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.data.TensuraTags;
import io.github.manasmods.tensura.entity.human.HinataSakaguchiEntity;
import io.github.manasmods.tensura.entity.magic.beam.BeamProjectile;
import io.github.manasmods.tensura.entity.magic.beam.SpatialRayProjectile;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.effect.IEffect;
import io.github.manasmods.tensura.util.AttributeHelper;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class SpatialDominationSkill extends Skill {
   public static final ExtraSkillConfig.SpatialManipulation CONFIG = ((ExtraSkillConfig)ConfigRegistry.getConfig(ExtraSkillConfig.class)).SpatialManipulation;
   public static final ResourceLocation SPATIAL_DOMINATION = ResourceLocation.fromNamespaceAndPath("tensura", "spatial_domination");
   private static final ResourceLocation RAY = ResourceLocation.fromNamespaceAndPath("tensura", "dimension_ray");
   private static final ResourceLocation FAULT = ResourceLocation.fromNamespaceAndPath("tensura", "fault_field");

   public SpatialDominationSkill() {
      super(Skill.SkillType.EXTRA);
      this.addHeldAttributeModifier(Attributes.MOVEMENT_SPEED, FAULT, CONFIG.faultFieldSpeed - 1.0, Operation.ADD_MULTIPLIED_TOTAL);
   }

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      return !SkillUtils.isSkillMastered(entity, (ManasSkill)ExtraSkills.SPATIAL_MANIPULATION.get()) ? false : newEP > CONFIG.dominationEpAcquirement;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
      return instance.getMastery() >= 0.0;
   }

   @Override
   public boolean shouldTriggerReleaseOnHeldInterrupt(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      return mode == 2;
   }

   public int getModes(ManasSkillInstance instance) {
      return 5;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      if (reverse) {
         return switch (mode) {
            case 1 -> 0;
            case 2 -> 1;
            case 3 -> 2;
            case 4 -> this.isModeLearnt(instance, mode) ? 3 : 2;
            default -> this.isModeLearnt(instance, mode) ? 3 : 4;
         };
      } else {
         return switch (mode) {
            case 0 -> 1;
            case 1 -> 2;
            case 2 -> this.isModeLearnt(instance, mode) ? 3 : 4;
            case 3 -> 4;
            default -> 0;
         };
      }
   }

   private boolean isModeLearnt(ManasSkillInstance instance, int mode) {
      return instance.getOrCreateTag().getDouble(this.getModeId(instance, mode)) >= BASE_CONFIG.Learning.learningPointRequirement;
   }

   @Override
   public List<Integer> getModeLearningList(ManasSkillInstance instance) {
      return List.of(1, 2, 3, 4);
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> "spatial_domination.warp_shot";
         case 1 -> "spatial_domination.spatial_cleanse";
         case 2 -> "spatial_domination.ray";
         case 3 -> "spatial_domination.storm";
         case 4 -> "spatial_domination.fault_field";
         default -> super.getModeId(instance, mode);
      };
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> CONFIG.magiculeCostWarpShot;
         case 1 -> CONFIG.magiculeCostCleanse;
         case 2 -> CONFIG.magiculeCostRay;
         case 3 -> CONFIG.magiculeCostStorm;
         case 4 -> CONFIG.magiculeCostField;
         default -> 0.0;
      };
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.multiplyElementalBoost(entity, TensuraAttributes.SPACE_BOOST, CONFIG.dominationBoost);
      if (instance.isMastered(entity)) {
         AttributeHelper.applyDominationDegradation(entity, TensuraAttributes.SPACE_RESIST_DEGRADATION, CONFIG.resistDegradationAcquirement);
      }
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.removeElementalMultiplier(entity, TensuraAttributes.SPACE_BOOST, CONFIG.dominationBoost);
      AttributeHelper.removeDominationDegradation(entity, TensuraAttributes.SPACE_RESIST_DEGRADATION);
   }

   public boolean onTakenDamage(ManasSkillInstance instance, LivingEntity entity, DamageSource source, Changeable<Float> amount) {
      if (!hasFaultField(entity)) {
         return true;
      } else if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
         return true;
      } else if (source.is(TensuraTags.DamageTypes.BYPASS_DIMENSION_FAULT)) {
         return true;
      } else if (TensuraDamageHelper.isSeveranceDamage(source, entity, true)) {
         return true;
      } else {
         float damageCanceled = (Float)amount.get();
         double lackedMagicule = EnergyHelper.isOutOfMagiculeConsuming(entity, (int)damageCanceled * CONFIG.magiculeCostFieldDamage);
         if (lackedMagicule > 0.0) {
            damageCanceled = (float)(damageCanceled - lackedMagicule / CONFIG.magiculeCostFieldDamage);
            amount.set((Float)amount.get() - damageCanceled);
            return true;
         } else {
            return false;
         }
      }
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      Level level = entity.level();
      switch (mode) {
         case 0:
            AttributeInstance warpShot = entity.getAttribute(TensuraAttributes.WARP_SHOT);
            if (warpShot == null) {
               return;
            }

            AttributeModifier modifier = warpShot.getModifier(SPATIAL_DOMINATION);
            if (modifier != null) {
               AttributeHelper.removeAttributeIfCorrect(entity, TensuraAttributes.WARP_SHOT, SPATIAL_DOMINATION, CONFIG.warpShotDomination);
               entity.level()
                  .playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.GENERIC_UNCAST.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
            } else if (AttributeHelper.addPermanentAttributeIfHigher(
               entity, TensuraAttributes.WARP_SHOT, SPATIAL_DOMINATION, CONFIG.warpShotDomination, Operation.ADD_VALUE
            )) {
               entity.level()
                  .playSound(
                     null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_SPACE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
                  );
            }
            break;
         case 1:
            if (this.learnMode(instance, entity, mode)) {
               return;
            }

            boolean success = false;
            LivingEntity target = ObjectSelectionHelper.getTargetingEntity(entity, 5.0, false);
            if (target != null && entity.isShiftKeyDown()) {
               IEffect effect = TensuraStorages.getEffectFrom(target);
               if (effect.getSeveranceAmount() > 0.0F) {
                  double cost = CONFIG.magiculeCostCleanse;
                  float severance = effect.getSeveranceAmount();
                  double lackedMagicule = EnergyHelper.isOutOfMagiculeConsuming(entity, (int)(severance * cost));
                  if (lackedMagicule > 0.0) {
                     severance = (float)(severance - lackedMagicule / cost);
                  }

                  if (severance > 0.0F) {
                     effect.setSeveranceAmount(Math.max(effect.getSeveranceAmount() - severance, 0.0F));
                     effect.markDirty();
                     success = true;
                  }
               }
            } else {
               IEffect effect = TensuraStorages.getEffectFrom(entity);
               if (effect.getSeveranceAmount() > 0.0F) {
                  double cost = CONFIG.magiculeCostCleanse;
                  float severance = effect.getSeveranceAmount();
                  double lackedMagicule = EnergyHelper.isOutOfMagiculeConsuming(entity, (int)(severance * cost));
                  if (lackedMagicule > 0.0) {
                     severance = (float)(severance - lackedMagicule / cost);
                  }

                  if (severance > 0.0F) {
                     effect.setSeveranceAmount(Math.max(effect.getSeveranceAmount() - severance, 0.0F));
                     effect.markDirty();
                     success = true;
                  }
               }
            }

            if (success) {
               instance.addMasteryPoint(entity);
               instance.setCoolDown(CONFIG.cleanseCooldown, mode);
               entity.swing(InteractionHand.MAIN_HAND, true);
               entity.level()
                  .playSound(
                     null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_SPACE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
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
                  TensuraParticleUtils.getPurpleWave(0.9F, entity.getBbWidth() * 3.0F, -0.5F, true),
                  entity.getX(),
                  entity.getY() + entity.getBbHeight() * 0.66,
                  entity.getZ()
               );
            }
            break;
         case 2:
            this.learnMode(instance, entity, mode);
            break;
         case 3:
            if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
               return;
            }

            if (this.learnMode(instance, entity, mode)) {
               return;
            }

            Entity target = ObjectSelectionHelper.getTargetingEntity(entity, CONFIG.stormRange, false, false);
            Vec3 pos;
            if (target != null) {
               pos = target.getEyePosition();
            } else {
               BlockHitResult result = ObjectSelectionHelper.getPlayerPOVHitResult(level, entity, Fluid.NONE, 30.0);
               pos = result.getLocation();
            }

            instance.setCoolDown(instance.isMastered(entity) ? CONFIG.stormCooldownMastered : CONFIG.stormCooldown, mode);
            level.playSound(null, pos.x(), pos.y(), pos.z(), (SoundEvent)TensuraSoundEvents.CAST_SPACE.get(), TensuraSkill.ABILITY_SOUND, 5.0F, 0.5F);
            ((ServerLevel)level).sendParticles(ParticleTypes.FLASH, pos.x(), pos.y(), pos.z(), 1, 0.0, 0.0, 0.0, 0.0);
            int rayAmount = instance.isMastered(entity) ? CONFIG.stormAmountMastered : CONFIG.stormAmount;
            RandomSource stormRand = entity.getRandom();

            for (int i = 0; i < rayAmount; i++) {
               Vec3 startOffset = new Vec3(0.0, 1.0 - stormRand.nextDouble() * 2.0, 0.6)
                  .normalize()
                  .scale(20.0)
                  .yRot(360.0F * i * (float) (Math.PI / 180.0) / rayAmount);
               SpatialRayProjectile ray = new SpatialRayProjectile(entity.level(), entity);
               ray.setFollowingOwner(false);
               ray.setLife(20);
               ray.setSize(0.75F);
               ray.setRange(10.0F);
               Vec3 rayPos = pos.add(startOffset);
               ray.setPos(rayPos.add(pos.subtract(rayPos).normalize().scale(10.0)));
               ray.setTargetPos(pos.x(), pos.y(), pos.z());
               ray.setDamage(instance.isMastered(entity) ? CONFIG.stormDamageMastered : CONFIG.stormDamage);
               ray.setSkill(entity, instance, this, mode);
               entity.level().addFreshEntity(ray);
               TensuraParticleHelper.addServerParticlesAroundSelf(ray, ParticleTypes.FLASH);
            }
            break;
         case 4:
            if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
               return;
            }

            this.learnMode(instance, entity, mode);
      }
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (mode == 2 && !instance.onCoolDown(mode)) {
         if (!this.isModeLearnt(instance, mode)) {
            return;
         }

         if (!this.hasAttributeApplied(entity, Attributes.MOVEMENT_SPEED, RAY)) {
            return;
         }

         instance.setCoolDown(instance.isMastered(entity) ? CONFIG.rayCooldown : CONFIG.rayCooldownMastered, mode);
      }
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (mode == 2) {
         if (!this.isModeLearnt(instance, mode)) {
            return false;
         }

         if (heldTicks % BASE_CONFIG.Mastery.masteryHoldTick == 0 && heldTicks > 0) {
            instance.addMasteryPoint(entity);
         }

         Pair<Double, Double> cost = Pair.of(this.getAuraCost(entity, instance, mode), this.getMagiculeCost(entity, instance, mode));
         BeamProjectile.spawnLastingBeam(
            (EntityType<? extends BeamProjectile>)MiscEntityTypes.SPATIAL_RAY.get(),
            instance.isMastered(entity) ? CONFIG.rayDamageMastered : CONFIG.rayDamage,
            0.5F,
            CONFIG.rayRange,
            entity,
            instance,
            mode,
            cost,
            cost,
            heldTicks
         );
         entity.level()
            .playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_SPACE.get(), TensuraSkill.ABILITY_SOUND, 0.8F, 0.5F
            );
         if (heldTicks > CONFIG.rayDuration) {
            instance.setCoolDown(instance.isMastered(entity) ? CONFIG.rayCooldown : CONFIG.rayCooldownMastered, mode);
            this.removeAttributeModifiers(instance, entity, mode);
            return false;
         } else {
            return true;
         }
      } else {
         if (mode != 4) {
            return false;
         }

         if (!this.isModeLearnt(instance, mode)) {
            return false;
         }

         if (heldTicks % 20 == 0 && EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            return false;
         }

         if (heldTicks % BASE_CONFIG.Mastery.masteryHoldTick == 0 && heldTicks > 0) {
            instance.addMasteryPoint(entity);
         }

         return true;
      }
   }

   public void addHeldAttributeModifiers(ManasSkillInstance instance, LivingEntity entity, int mode) {
      if ((mode == 4 || mode == 2) && this.isModeLearnt(instance, mode)) {
         if (mode == 4) {
            super.addHeldAttributeModifiers(instance, entity, mode);
         } else {
            AttributeMap attributeMap = entity.getAttributes();

            for (Entry<Holder<Attribute>, AttributeTemplate> entry : this.attributeModifiers.entrySet()) {
               AttributeInstance attributeInstance = attributeMap.getInstance(entry.getKey());
               if (attributeInstance != null) {
                  attributeInstance.removeModifier(entry.getValue().id());
                  attributeInstance.addOrUpdateTransientModifier(
                     entry.getValue().create(RAY, instance.getAttributeModifierAmplifier(entity, entry.getKey(), entry.getValue(), mode))
                  );
               }
            }
         }
      }
   }

   public void removeAttributeModifiers(ManasSkillInstance instance, LivingEntity entity, int mode) {
      if (mode == 4) {
         super.removeAttributeModifiers(instance, entity, mode);
      } else if (!this.attributeModifiers.isEmpty()) {
         AttributeMap map = entity.getAttributes();
         List<AttributeInstance> dirtyInstances = new ArrayList<>();

         for (Entry<Holder<Attribute>, AttributeTemplate> entry : this.attributeModifiers.entrySet()) {
            AttributeInstance attributeInstance = map.getInstance(entry.getKey());
            if (attributeInstance != null) {
               attributeInstance.removeModifier(RAY);
               dirtyInstances.add(attributeInstance);
            }
         }

         if (!dirtyInstances.isEmpty() && entity instanceof ServerPlayer player) {
            ClientboundUpdateAttributesPacket packet = new ClientboundUpdateAttributesPacket(player.getId(), dirtyInstances);
            player.connection.send(packet);
         }
      }
   }

   @Override
   public void onForgetSkill(ManasSkillInstance instance, LivingEntity entity) {
      super.onForgetSkill(instance, entity);
      AttributeInstance attribute = entity.getAttribute(TensuraAttributes.WARP_SHOT);
      if (attribute != null) {
         if (attribute.getModifier(SPATIAL_DOMINATION) != null) {
            attribute.removeModifier(SPATIAL_DOMINATION);
         }
      }
   }

   public static boolean hasFaultField(LivingEntity entity) {
      if (entity instanceof HinataSakaguchiEntity hinata && hinata.getPhase() == 1) {
         return true;
      } else {
         AttributeInstance attributeInstance = entity.getAttribute(Attributes.MOVEMENT_SPEED);
         return attributeInstance == null ? false : attributeInstance.getModifier(FAULT) != null;
      }
   }
}
