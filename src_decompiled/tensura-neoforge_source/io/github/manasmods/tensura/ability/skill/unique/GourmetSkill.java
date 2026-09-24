package io.github.manasmods.tensura.ability.skill.unique;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.ability.subclass.ISpatialStorage;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.entity.magic.beam.PredatorMistProjectile;
import io.github.manasmods.tensura.event.TensuraSkillEvents;
import io.github.manasmods.tensura.menu.container.SpatialStorageContainer;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ability.IAbility;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.MenuHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import io.github.manasmods.tensura.util.SubordinateHelper;
import io.github.manasmods.tensura.world.TensuraGameRules;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class GourmetSkill extends Skill implements ISpatialStorage {
   private static final UniqueSkillConfig.Gourmet CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).Gourmet;
   private static final ResourceLocation CORROSION = ResourceLocation.fromNamespaceAndPath("tensura", "gourmet");

   public GourmetSkill() {
      super(Skill.SkillType.UNIQUE);
      this.addHeldAttributeModifier(Attributes.MOVEMENT_SPEED, CORROSION, CONFIG.corrosionSpeedMultiplier - 1.0, Operation.ADD_MULTIPLIED_TOTAL);
   }

   @Override
   public double getDefaultAcquiringMagiculeCost() {
      return CONFIG.mpAcquirement;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
      return instance.getMastery() >= 0.0;
   }

   public int getModes(ManasSkillInstance instance) {
      return 5;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      if (reverse) {
         return mode == 0 ? 4 : mode - 1;
      } else {
         return mode == 4 ? 0 : mode + 1;
      }
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> "predator.predation";
         case 1 -> "starved.corrosion";
         case 2 -> "predator.stomach";
         case 3 -> "starved.receive";
         case 4 -> "starved.provide";
         default -> super.getModeId(instance, mode);
      };
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 1 -> CONFIG.magiculeCostCorrosion;
         default -> 0.0;
      };
   }

   public boolean canIgnoreCoolDown(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return mode == 0 && entity.isShiftKeyDown();
   }

   public void addHeldAttributeModifiers(ManasSkillInstance instance, LivingEntity entity, int mode) {
      if (mode == 1) {
         super.addHeldAttributeModifiers(instance, entity, mode);
      }
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      return mode == 1 && !instance.onCoolDown(mode) ? this.corrosion(instance, entity, heldTicks, mode) : false;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      Level level = entity.level();
      CompoundTag tag = instance.getOrCreateTag();
      switch (mode) {
         case 0:
            if (entity.isShiftKeyDown() && entity instanceof Player player) {
               int newMode;
               switch (tag.getInt("blockMode")) {
                  case 1:
                     newMode = 2;
                     player.displayClientMessage(
                        Component.translatable("tensura.skill.predator.block_mode.blocks", new Object[]{this.getName()})
                           .setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_AQUA)),
                        true
                     );
                     break;
                  case 2:
                     newMode = 3;
                     player.displayClientMessage(
                        Component.translatable("tensura.skill.predator.block_mode.fluid", new Object[]{this.getName()})
                           .setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_AQUA)),
                        true
                     );
                     break;
                  case 3:
                     newMode = 4;
                     player.displayClientMessage(
                        Component.translatable("tensura.skill.predator.block_mode.all", new Object[]{this.getName()})
                           .setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_AQUA)),
                        true
                     );
                     break;
                  default:
                     newMode = 1;
                     player.displayClientMessage(
                        Component.translatable("tensura.skill.predator.block_mode.none", new Object[]{this.getName()})
                           .setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_AQUA)),
                        true
                     );
               }

               tag.putInt("blockMode", newMode);
               instance.markDirty();
            } else {
               PredatorMistProjectile mist = new PredatorMistProjectile(
                  (EntityType<? extends PredatorMistProjectile>)MiscEntityTypes.GOURMET_MIST.get(), entity.level(), entity
               );
               mist.setDamage(CONFIG.predationDamage);
               mist.setAttackingRange(CONFIG.predationRange);
               mist.setLife(60);
               mist.setBlockMode(instance.getOrCreateTag().getInt("blockMode"));
               if (instance.isMastered(entity)) {
                  mist.setConsumeProjectile(true);
               }

               mist.setSkill(entity, instance, this, mode);
               mist.setPos(entity.position().add(0.0, entity.getEyeHeight() * 0.7, 0.0));
               entity.level().addFreshEntity(mist);
               mist.triggerAnim("controller", "start");
               entity.level()
                  .playSound(
                     null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.PREDATION.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
                  );
               entity.swing(InteractionHand.MAIN_HAND, true);
               instance.addMasteryPoint(entity);
               instance.setCoolDown(instance.isMastered(entity) ? CONFIG.predationCooldownMastered : CONFIG.predationCooldown, mode);
            }
         case 1:
         case 4:
         default:
            break;
         case 2:
            this.openSpatialStorage(entity, instance);
            break;
         case 3:
            LivingEntity target = ObjectSelectionHelper.getTargetingEntity(entity, 5.0, false);
            if (target == null || !target.isAlive()) {
               return;
            }

            if (!SubordinateHelper.isSubordinate(entity, target)) {
               return;
            }

            if (target.getType().is(TensuraEntityTags.NO_SKILL_PLUNDER)) {
               return;
            }

            for (ManasSkillInstance targetInstance : SkillAPI.getSkillsFrom(target).getLearnedSkills().stream().filter(StarvedSkill::canGain).toList()) {
               if (!targetInstance.isTemporarySkill() && !(targetInstance.getMastery() < 0.0) && targetInstance.getSkill() != this) {
                  Changeable<ManasSkill> changeable = Changeable.of(targetInstance.getSkill());
                  if (!((TensuraSkillEvents.SkillPlunderEvent)TensuraSkillEvents.SKILL_PLUNDER.invoker()).plunder(target, entity, false, changeable).isFalse()
                     && SkillHelper.learnSkill(entity, (ManasSkill)changeable.get(), instance.getRemoveTime())) {
                     level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_LEVELUP, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
                     ((ServerLevel)level)
                        .sendParticles(
                           ParticleTypes.WAX_ON, entity.getX(), entity.getY() + entity.getBbHeight() / 2.0, entity.getZ(), 20, 0.08, 0.08, 0.08, 0.15
                        );
                  }
               }
            }
            break;
         case 5:
            MenuHelper.sendComingSoonMessage(entity);
      }
   }

   private boolean corrosion(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (heldTicks % 20 == 0 && EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
         return false;
      }

      if (heldTicks % BASE_CONFIG.Mastery.masteryHoldTick == 0 && heldTicks > 0) {
         instance.addMasteryPoint(entity);
      }

      Level level = entity.level();
      level.playSound(
         null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.ACID_SIZZLE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
      );
      double size = entity.getAttributeValue(Attributes.SCALE) * 4.0;
      TensuraParticleHelper.addServerAuraParticles(entity, TensuraParticleUtils.getChaosEaterAura(1.0F, (float)size, -0.3F), 3, 0.01);
      if (heldTicks % 10 == 0) {
         List<LivingEntity> list = level.getEntitiesOfClass(
            LivingEntity.class,
            entity.getBoundingBox().inflate(CONFIG.corrosionRadius),
            living -> !living.is(entity) && living.isAlive() && !living.isAlliedTo(entity)
         );
         if (!list.isEmpty()) {
            DamageSource source = this.createSource(instance, entity, TensuraDamageTypes.CORROSION, mode).tensura$setDodgeBypass();

            for (LivingEntity target : list) {
               if (target.hurt(source, CONFIG.corrosionDamage) && target.isDeadOrDying()) {
                  if (!target.getType().is(TensuraEntityTags.NO_SKILL_PLUNDER)) {
                     for (ManasSkillInstance targetInstance : SkillAPI.getSkillsFrom(target).getLearnedSkills().stream().filter(StarvedSkill::canGain).toList()) {
                        if (!targetInstance.isTemporarySkill() && !(targetInstance.getMastery() < 0.0) && targetInstance.getSkill() != this) {
                           Changeable<ManasSkill> changeable = Changeable.of(targetInstance.getSkill());
                           if (!((TensuraSkillEvents.SkillPlunderEvent)TensuraSkillEvents.SKILL_PLUNDER.invoker())
                              .plunder(target, entity, false, changeable)
                              .isFalse()) {
                              SkillHelper.learnSkill(entity, (ManasSkill)changeable.get(), instance.getRemoveTime());
                           }
                        }
                     }
                  }

                  if (!instance.isTemporarySkill()
                     && !target.getType().is(TensuraEntityTags.NO_EP_PLUNDER)
                     && (TensuraGameRules.canEpSteal(level) || !(target instanceof Player))) {
                     CompoundTag tag = instance.getOrCreateTag();
                     if (tag.contains("predationList")) {
                        CompoundTag predationList = (CompoundTag)tag.get("predationList");
                        if (predationList == null) {
                           continue;
                        }

                        String targetID = EntityType.getKey(target.getType()).toString();
                        if (predationList.contains(targetID)) {
                           continue;
                        }

                        predationList.putBoolean(targetID, true);
                     } else {
                        CompoundTag predationList = new CompoundTag();
                        predationList.putBoolean(EntityType.getKey(target.getType()).toString(), true);
                        tag.put("predationList", predationList);
                     }

                     double EP = Math.min(EnergyHelper.getEPGain(target, entity), EnergyHelper.CONFIG.maximumEPSteal);
                     if (EnergyHelper.drainEnergy(target, entity, EP, false, EnergyHelper.DrainType.MAX_EP, EnergyHelper.GainType.NONE)) {
                        double amount = CONFIG.corrosionEPSteal;
                        EnergyHelper.gainAura(target, EP * amount, EnergyHelper.GainType.MAX);
                        EnergyHelper.gainAura(target, EP * amount, EnergyHelper.GainType.NORMAL);
                        EnergyHelper.gainMagicule(entity, EP * amount, EnergyHelper.GainType.MAX);
                        EnergyHelper.gainMagicule(entity, EP * amount, EnergyHelper.GainType.NORMAL);
                        IExistence existence = TensuraStorages.getExistenceFrom(target);
                        existence.setSkippingEPDrop(true);
                        existence.markDirty();
                        tag.putDouble("storedMP", tag.getDouble("storedMP") + EP * (0.5 - amount));
                        tag.putDouble("storedAP", tag.getDouble("storedAP") + EP * (0.5 - amount));
                        instance.markDirty();
                     }
                  }
               }
            }
         }
      }

      return true;
   }

   @NotNull
   @Override
   public SpatialStorageContainer getSpatialStorage(ManasSkillInstance instance, Provider provide) {
      SpatialStorageContainer container = new SpatialStorageContainer(63, 200);
      container.fromTag(instance.getOrCreateTag().getList("SpatialStorage", 10), provide);
      return container;
   }

   @Override
   public void onLearnSkill(ManasSkillInstance instance, LivingEntity entity) {
      super.onLearnSkill(instance, entity);
      if (!(instance.getMastery() < 0.0)) {
         AttributeInstance water = entity.getAttribute(TensuraAttributes.WATER_CAPACITY);
         if (water != null) {
            water.setBaseValue(water.getValue() + CONFIG.waterCapacity);
         }

         AttributeInstance lava = entity.getAttribute(TensuraAttributes.LAVA_CAPACITY);
         if (lava != null) {
            lava.setBaseValue(lava.getValue() + CONFIG.lavaCapacity);
         }
      }
   }

   @Override
   public void onForgetSkill(ManasSkillInstance instance, LivingEntity entity) {
      super.onForgetSkill(instance, entity);
      if (!(instance.getMastery() < 0.0)) {
         IAbility ability = TensuraStorages.getAbilityFrom(entity);
         AttributeInstance water = entity.getAttribute(TensuraAttributes.WATER_CAPACITY);
         if (water != null) {
            water.setBaseValue(water.getValue() - CONFIG.waterCapacity);
            ability.setWaterPoint(Math.min(water.getValue(), ability.getWaterPoint()));
            ability.markDirty();
         }

         AttributeInstance lava = entity.getAttribute(TensuraAttributes.LAVA_CAPACITY);
         if (lava != null) {
            lava.setBaseValue(lava.getValue() - CONFIG.lavaCapacity);
            ability.setLavaPoint(Math.min(lava.getValue(), ability.getLavaPoint()));
            ability.markDirty();
         }
      }
   }
}
