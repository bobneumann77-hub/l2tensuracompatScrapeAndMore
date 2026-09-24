package io.github.manasmods.tensura.ability.skill.unique;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.manascore.skill.api.Skills;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.TensuraSkillInstance;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.ability.skill.common.ThoughtCommunicationSkill;
import io.github.manasmods.tensura.ability.subclass.ISpatialStorage;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.data.TensuraSkillTags;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.event.TensuraSkillEvents;
import io.github.manasmods.tensura.menu.container.SpatialStorageContainer;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.skill.UniqueSkills;
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
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class StarvedSkill extends Skill implements ISpatialStorage {
   private static final UniqueSkillConfig.Starved CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).Starved;
   private static final ResourceLocation CORROSION = ResourceLocation.fromNamespaceAndPath("tensura", "starved");

   public StarvedSkill() {
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
         case 0 -> "starved.corrosion";
         case 1 -> "starved.stomach";
         case 2 -> "starved.receive";
         case 3 -> "starved.provide";
         case 4 -> "spiritual_domination";
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

   public boolean onTouchEntity(ManasSkillInstance instance, LivingEntity attacker, LivingEntity target, DamageSource source, Changeable<Float> amount) {
      if (!this.isInSlot(attacker, instance, 0)) {
         return true;
      }

      if (source.getDirectEntity() != attacker) {
         return true;
      }

      if (!TensuraDamageHelper.isPhysicalAttack(source)) {
         return true;
      }

      MobEffectInstance corrosion = new MobEffectInstance(
         TensuraMobEffects.getReference(TensuraMobEffects.CORROSION), CONFIG.corrosionDuration, CONFIG.corrosionLevel - 1, true, false, true
      );
      TensuraMobEffect.addEffect(target, corrosion, attacker, this, 0);
      attacker.level()
         .playSound(null, target.getX(), target.getY(), target.getZ(), (SoundEvent)TensuraSoundEvents.ACID_SIZZLE.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 1.0F);
      ((ServerLevel)attacker.level())
         .sendParticles(
            TensuraParticleUtils.getAcidBubble(),
            target.position().x,
            target.position().y + target.getBbHeight() / 2.0,
            target.position().z,
            20,
            0.08,
            0.08,
            0.08,
            0.15
         );
      return true;
   }

   public void addHeldAttributeModifiers(ManasSkillInstance instance, LivingEntity entity, int mode) {
      if (mode == 0) {
         super.addHeldAttributeModifiers(instance, entity, mode);
      }
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (mode != 0) {
         return false;
      }

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
      TensuraParticleHelper.addServerAuraParticles(entity, TensuraParticleUtils.getAcidAura(1.0F, (float)size, -0.3F), 3, 0.01);
      if (heldTicks % 10 == 0) {
         List<LivingEntity> list = level.getEntitiesOfClass(
            LivingEntity.class,
            entity.getBoundingBox().inflate(CONFIG.corrosionRadius),
            living -> !living.is(entity) && living.isAlive() && !living.isAlliedTo(entity)
         );
         if (!list.isEmpty()) {
            for (LivingEntity target : list) {
               DamageSource source = this.createSource(instance, entity, TensuraDamageTypes.CORROSION, mode).tensura$setDodgeBypass();
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

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      Level level = entity.level();
      CompoundTag tag = instance.getOrCreateTag();
      switch (mode) {
         case 1:
            this.openSpatialStorage(entity, instance);
            break;
         case 2:
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
         case 3:
            MenuHelper.sendComingSoonMessage(entity);
            break;
         case 4:
            if (!(entity instanceof Player player)) {
               return;
            }

            if (player.isSecondaryUseActive()) {
               List<Mob> list = player.level()
                  .getEntitiesOfClass(
                     Mob.class, player.getBoundingBox().inflate(CONFIG.dominationRadius), living -> SubordinateHelper.isSubordinate(player, living)
                  );
               if (list.isEmpty()) {
                  player.displayClientMessage(
                     Component.translatable("tensura.telepathy.subordinate_all.not_found").setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA)), true
                  );
                  return;
               }

               int command = tag.getInt("command");
               command = command == 4 ? 1 : command + 1;
               tag.putInt("command", command);

               for (Mob mob : list) {
                  MutableComponent message = switch (command) {
                     case 2 -> {
                        SubordinateHelper.setFollow(mob);
                        mob.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.RAMPAGE));
                        yield Component.translatable("tensura.telepathy.subordinate_all.follow");
                     }
                     case 3 -> {
                        SubordinateHelper.setWander(mob);
                        mob.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.RAMPAGE));
                        yield Component.translatable("tensura.telepathy.subordinate_all.wander");
                     }
                     case 4 -> {
                        SubordinateHelper.setWander(mob);
                        MobEffectInstance rampage = new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.RAMPAGE), 6000, 1, true, false, true);
                        TensuraMobEffect.addEffect(mob, rampage, entity, this, mode);
                        yield Component.translatable("tensura.telepathy.subordinate_all.rampage");
                     }
                     default -> {
                        SubordinateHelper.setStay(mob);
                        mob.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.RAMPAGE));
                        yield Component.translatable("tensura.telepathy.subordinate_all.stay");
                     }
                  };
                  player.swing(InteractionHand.MAIN_HAND, true);
                  player.displayClientMessage(message.setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA)), true);
                  player.level()
                     .playSound(
                        null,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(),
                        TensuraSkill.ABILITY_SOUND,
                        1.0F,
                        1.0F
                     );
               }
            } else {
               ThoughtCommunicationSkill.movementTelepathy(instance, entity);
            }
      }
   }

   public static boolean canGain(ManasSkillInstance instance) {
      if (instance.is(TensuraSkillTags.NO_PLUNDERING)) {
         return false;
      } else if (!instance.is(TensuraSkillTags.SKILLS)) {
         return false;
      } else {
         return instance.is(TensuraSkillTags.UNIQUE_SKILLS) ? false : !instance.is(TensuraSkillTags.ULTIMATE_SKILLS);
      }
   }

   @Override
   public void openSpatialStorage(LivingEntity entity, ManasSkillInstance instance) {
      ManasSkillInstance gluttony = this.getGluttony(entity);
      if (gluttony != null) {
         this.moveItemsToSpatialStorage(instance, gluttony, entity, true);
      } else {
         ISpatialStorage.super.openSpatialStorage(entity, instance);
      }
   }

   @Override
   public boolean addItemToSpatialStorage(ManasSkillInstance instance, LivingEntity entity, ItemStack stack) {
      ManasSkillInstance gluttony = this.getGluttony(entity);
      return gluttony != null
         ? ((ISpatialStorage)gluttony.getSkill()).addItemToSpatialStorage(gluttony, entity, stack)
         : ISpatialStorage.super.addItemToSpatialStorage(instance, entity, stack);
   }

   @NotNull
   @Override
   public SpatialStorageContainer getSpatialStorage(ManasSkillInstance instance, Provider provide) {
      SpatialStorageContainer container = new SpatialStorageContainer(54, 128);
      container.fromTag(instance.getOrCreateTag().getList("SpatialStorage", 10), provide);
      return container;
   }

   private ManasSkillInstance getGluttony(LivingEntity entity) {
      Skills storage = SkillAPI.getSkillsFrom(entity);
      Optional<ManasSkillInstance> optional = storage.getSkill((ManasSkill)UniqueSkills.GLUTTONY.get());
      return optional.isPresent() && optional.get().getMastery() >= 0.0 ? optional.get() : null;
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

         if (!instance.isTemporarySkill()) {
            if (SkillUtils.isSkillMastered(entity, (ManasSkill)UniqueSkills.PREDATOR.get())) {
               TensuraSkillInstance gluttony = ((GluttonySkill)UniqueSkills.GLUTTONY.get()).createDefaultInstance();
               gluttony.setMastery(((GluttonySkill)UniqueSkills.GLUTTONY.get()).getAcquirementMastery(entity));
               SkillHelper.learnSkill(entity, gluttony);
            }
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
