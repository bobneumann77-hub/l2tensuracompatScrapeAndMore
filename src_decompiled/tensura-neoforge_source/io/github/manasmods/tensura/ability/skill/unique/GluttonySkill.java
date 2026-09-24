package io.github.manasmods.tensura.ability.skill.unique;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.manascore.skill.api.Skills;
import io.github.manasmods.manascore.skill.api.ManasSkill.AttributeTemplate;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.ability.skill.intrinsic.AbsorbDissolveSkill;
import io.github.manasmods.tensura.ability.subclass.ISpatialStorage;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.data.disolving.ItemDissolving;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.entity.magic.beam.PredatorMistProjectile;
import io.github.manasmods.tensura.event.TensuraSkillEvents;
import io.github.manasmods.tensura.menu.container.SpatialStorageContainer;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.data.TensuraCustomData;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.registry.skill.UniqueSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ability.IAbility;
import io.github.manasmods.tensura.storage.effect.IEffect;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.MenuHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import io.github.manasmods.tensura.util.SubordinateHelper;
import io.github.manasmods.tensura.world.TensuraGameRules;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class GluttonySkill extends Skill implements ISpatialStorage {
   public static final UniqueSkillConfig.Gluttony CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).Gluttony;
   private static final ResourceLocation GLUTTONY = ResourceLocation.fromNamespaceAndPath("tensura", "gluttony");

   public GluttonySkill() {
      super(Skill.SkillType.UNIQUE);
      this.addHeldAttributeModifier(Attributes.MOVEMENT_SPEED, GLUTTONY, CONFIG.corrosionSpeedMultiplier - 1.0, Operation.ADD_MULTIPLIED_TOTAL);
   }

   @Override
   public double getDefaultAcquiringMagiculeCost() {
      return CONFIG.mpAcquirement;
   }

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      Skills storage = SkillAPI.getSkillsFrom(entity);
      return storage.getSkill((ManasSkill)UniqueSkills.STARVED.get()).isEmpty()
         ? false
         : storage.getSkill((ManasSkill)UniqueSkills.PREDATOR.get()).map(instance -> instance.isMastered(entity)).orElse(false);
   }

   @Override
   public int getAcquirementMastery(LivingEntity entity) {
      return -1;
   }

   @Override
   public int getMaxMastery() {
      return SKILL_CONFIG.Mastery.masteryUniqueSin;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
      return true;
   }

   public boolean canScroll(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return mode == 0 && instance.getMastery() >= 0.0;
   }

   public int getModes(ManasSkillInstance instance) {
      return 7;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      if (reverse) {
         return mode == 0 ? 6 : mode - 1;
      } else {
         return mode == 6 ? 0 : mode + 1;
      }
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> "predator.predation";
         case 1 -> "predator.stomach";
         case 2 -> "predator.mimicry";
         case 3 -> "predator.isolation";
         case 4 -> "starved.corrosion";
         case 5 -> "starved.receive";
         case 6 -> "starved.provide";
         default -> super.getModeId(instance, mode);
      };
   }

   public boolean canIgnoreCoolDown(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return mode == 0 && entity.isShiftKeyDown();
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 3 -> CONFIG.magiculeCostIsolation;
         case 4 -> CONFIG.magiculeCostCorrosion;
         default -> 0.0;
      };
   }

   public double getAttributeModifierAmplifier(ManasSkillInstance instance, LivingEntity entity, Holder<Attribute> holder, AttributeTemplate template, int mode) {
      return mode != 4 ? 0.0 : 1.0;
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (instance.onCoolDown(mode)) {
         return false;
      }

      switch (mode) {
         case 0:
            if (entity.isShiftKeyDown()) {
               return false;
            }

            CompoundTag tag = instance.getOrCreateTag();
            if (tag.getDouble("range") < 3.0) {
               tag.putDouble("range", CONFIG.predationRange);
            }

            if (heldTicks % BASE_CONFIG.Mastery.masteryHoldTick == 0 && heldTicks > 0) {
               instance.addMasteryPoint(entity);
            }

            Pair<Double, Double> cost = Pair.of(this.getAuraCost(entity, instance, mode), this.getMagiculeCost(entity, instance, mode));
            PredatorMistProjectile.spawnPredationMist(
               (EntityType<? extends PredatorMistProjectile>)MiscEntityTypes.GLUTTONY_MIST.get(),
               entity,
               instance,
               mode,
               CONFIG.predationDamage,
               cost,
               (float)tag.getDouble("range"),
               tag.getInt("blockMode"),
               true
            );
            if (heldTicks % 3 == 0) {
               entity.level()
                  .playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.PREDATION.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     0.8F + entity.getRandom().nextFloat() * 0.4F
                  );
            }

            return true;
         case 4:
            return this.corrosion(instance, entity, heldTicks, mode);
         default:
            return false;
      }
   }

   public void onScroll(ManasSkillInstance instance, LivingEntity entity, double delta, int mode) {
      CompoundTag tag = instance.getOrCreateTag();
      double newRange = tag.getDouble("range") + delta;
      double maxRange = instance.isMastered(entity) ? CONFIG.predationRangeMastered : CONFIG.predationRange;
      if (newRange > maxRange) {
         newRange = maxRange;
      } else if (newRange < 3.0) {
         newRange = 3.0;
      }

      if (tag.getDouble("range") != newRange) {
         tag.putDouble("range", newRange);
         if (entity instanceof Player player) {
            player.displayClientMessage(
               Component.translatable("tensura.skill.range", new Object[]{newRange}).setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_AQUA)), true
            );
         }

         instance.markDirty();
      }
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      Level level = entity.level();
      CompoundTag tag = instance.getOrCreateTag();
      switch (mode) {
         case 0:
            instance.getOrCreateTag().putInt("Mist", 0);
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
            }

            instance.markDirty();
            break;
         case 1:
            this.openSpatialStorage(entity, instance);
            break;
         case 2:
            MenuHelper.sendComingSoonMessage(entity);
            break;
         case 3:
            ItemStack itemStack = entity.getMainHandItem();
            if (itemStack.isEmpty()) {
               LivingEntity target = ObjectSelectionHelper.getTargetingEntity(entity, 5.0, false);
               boolean success;
               if (target != null) {
                  IEffect effect = TensuraStorages.getEffectFrom(target);
                  success = effect.getSeveranceAmount() > 0.0F;
                  effect.setSeveranceAmount(0.0F);
                  effect.markDirty();
                  Predicate<Holder<MobEffect>> predicate = holder -> ((MobEffect)holder.value()).getCategory() == MobEffectCategory.HARMFUL;
                  success = success || TensuraMobEffect.removePredicateEffect(target, predicate, this.getMagiculeCost(entity, instance, mode));
                  if (success) {
                     double size = target.getAttributeValue(Attributes.SCALE) * 4.0;
                     TensuraParticleHelper.addServerAuraParticles(target, TensuraParticleUtils.getBlackAura(0.5F, (float)size, -0.3F), 5, 0.01);
                     TensuraParticleHelper.spawnServerParticles(
                        entity.level(),
                        TensuraParticleUtils.getPurpleWave(0.9F, target.getBbWidth() * 2.5F, -0.5F, true),
                        target.getX(),
                        target.getY() + target.getBbHeight() * 0.66,
                        target.getZ()
                     );
                     TensuraParticleHelper.spawnServerParticles(
                        entity.level(),
                        TensuraParticleUtils.getBlackWave(1.0F, target.getBbWidth() * 3.0F, -0.5F, true),
                        target.getX(),
                        target.getY() + target.getBbHeight() * 0.33,
                        target.getZ()
                     );
                  }
               } else {
                  IEffect effect = TensuraStorages.getEffectFrom(entity);
                  success = effect.getSeveranceAmount() > 0.0F;
                  effect.setSeveranceAmount(0.0F);
                  effect.markDirty();
                  Predicate<Holder<MobEffect>> predicate = holder -> ((MobEffect)holder.value()).getCategory() == MobEffectCategory.HARMFUL;
                  success = success || TensuraMobEffect.removePredicateEffect(entity, predicate, this.getMagiculeCost(entity, instance, mode));
                  if (success) {
                     double size = entity.getAttributeValue(Attributes.SCALE) * 4.0;
                     TensuraParticleHelper.addServerAuraParticles(entity, TensuraParticleUtils.getBlackAura(0.5F, (float)size, -0.3F), 5, 0.01);
                     TensuraParticleHelper.spawnServerParticles(
                        entity.level(),
                        TensuraParticleUtils.getPurpleWave(0.9F, entity.getBbWidth() * 2.5F, -0.5F, true),
                        entity.getX(),
                        entity.getY() + entity.getBbHeight() * 0.66,
                        entity.getZ()
                     );
                     TensuraParticleHelper.spawnServerParticles(
                        entity.level(),
                        TensuraParticleUtils.getBlackWave(1.0F, entity.getBbWidth() * 3.0F, -0.5F, true),
                        entity.getX(),
                        entity.getY() + entity.getBbHeight() * 0.33,
                        entity.getZ()
                     );
                  }
               }

               if (success) {
                  entity.swing(InteractionHand.MAIN_HAND, true);
                  instance.setCoolDown(instance.isMastered(entity) ? CONFIG.isolationCooldownMastered : CONFIG.isolationCooldown, mode);
                  entity.level()
                     .playSound(
                        null,
                        entity.getX(),
                        entity.getY(),
                        entity.getZ(),
                        (SoundEvent)TensuraSoundEvents.GENERIC_HEAL.get(),
                        TensuraSkill.ABILITY_SOUND,
                        1.0F,
                        1.0F
                     );
               }
            } else {
               Registry<ItemDissolving> registry = entity.level().registryAccess().registryOrThrow(TensuraCustomData.ITEM_DISSOLVING);
               registry.stream()
                  .filter(dissolving -> dissolving.item().equals(itemStack.getItem().arch$registryName()))
                  .findFirst()
                  .ifPresentOrElse(
                     data -> {
                        EnergyHelper.gainMagicule(entity, data.magicule() * CONFIG.magiculeMultiplier, EnergyHelper.GainType.NORMAL);
                        if (data.health() > 0.0) {
                           entity.heal((float)data.health() * CONFIG.healthMultiplier);
                        }

                        AbsorbDissolveSkill.applySlimeCore(entity, itemStack);
                        instance.addMasteryPoint(entity);
                        if (entity instanceof ServerPlayer serverPlayer) {
                           serverPlayer.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
                           CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, itemStack);
                        }

                        itemStack.shrink(1);
                        entity.swing(InteractionHand.MAIN_HAND, true);
                        entity.level()
                           .playSound(
                              null,
                              entity.getX(),
                              entity.getY(),
                              entity.getZ(),
                              (SoundEvent)TensuraSoundEvents.EATER.get(),
                              TensuraSkill.ABILITY_SOUND,
                              1.0F,
                              1.0F
                           );
                     },
                     () -> entity.sendSystemMessage(Component.translatable("tensura.ability.activation_failed.item").withStyle(ChatFormatting.RED))
                  );
            }
         case 4:
         default:
            break;
         case 5:
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
         case 6:
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
      if (heldTicks % 5 == 0) {
         level.playSound(
            null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.ACID_SIZZLE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
         );
         double size = entity.getAttributeValue(Attributes.SCALE) * 4.0;
         TensuraParticleHelper.addServerAuraParticles(entity, TensuraParticleUtils.getPurpleAura(1.0F, (float)size, -0.3F), 3, 0.01);
      }

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
      SpatialStorageContainer container = new SpatialStorageContainer(81, 666);
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
