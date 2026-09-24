package io.github.manasmods.tensura.ability.skill.unique;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.manascore.skill.api.Skills;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.TensuraSkillInstance;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.ability.skill.intrinsic.AbsorbDissolveSkill;
import io.github.manasmods.tensura.ability.subclass.IRefining;
import io.github.manasmods.tensura.ability.subclass.ISpatialStorage;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.data.disolving.ItemDissolving;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.entity.magic.beam.PredatorMistProjectile;
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
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.MenuHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class PredatorSkill extends Skill implements IRefining<PredatorSkill> {
   public static final UniqueSkillConfig.Predator CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).Predator;

   public PredatorSkill() {
      super(Skill.SkillType.UNIQUE);
   }

   @Override
   public double getDefaultAcquiringMagiculeCost() {
      return CONFIG.mpAcquirement;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
      return instance.getMastery() >= 0.0;
   }

   public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
      CompoundTag tag = instance.getOrCreateTag();
      return tag != null && tag.getBoolean("Brewing");
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
         case 1 -> "predator.analysis";
         case 2 -> "predator.stomach";
         case 3 -> "predator.mimicry";
         case 4 -> "predator.isolation";
         default -> super.getModeId(instance, mode);
      };
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 4 -> CONFIG.magiculeCostIsolation;
         default -> 0.0;
      };
   }

   public boolean canIgnoreCoolDown(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return mode == 0 && entity.isShiftKeyDown();
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

   public void onSkillMastered(ManasSkillInstance instance, LivingEntity entity) {
      if (!instance.isSubInstance()) {
         if (SkillUtils.hasSkill(entity, (ManasSkill)UniqueSkills.STARVED.get())) {
            TensuraSkillInstance gluttony = ((GluttonySkill)UniqueSkills.GLUTTONY.get()).createDefaultInstance();
            gluttony.setMastery(((GluttonySkill)UniqueSkills.GLUTTONY.get()).getAcquirementMastery(entity));
            SkillHelper.learnSkill(entity, gluttony);
         }
      }
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
                  (EntityType<? extends PredatorMistProjectile>)MiscEntityTypes.PREDATOR_MIST.get(), level, entity
               );
               mist.setDamage(CONFIG.predationDamage);
               mist.setAttackingRange(CONFIG.predationRange);
               mist.setLife(60);
               mist.setBlockMode(instance.getOrCreateTag().getInt("blockMode"));
               if (instance.isMastered(entity)) {
                  mist.setConsumeProjectile(true);
               }

               ManasSkillInstance gluttony = this.getGluttony(entity);
               if (gluttony != null) {
                  mist.setSkill(entity, gluttony, (TensuraSkill)UniqueSkills.GLUTTONY.get(), mode);
               } else {
                  mist.setSkill(entity, instance, this, mode);
               }

               mist.setPos(entity.position().add(0.0, entity.getEyeHeight() * 0.7, 0.0));
               entity.level().addFreshEntity(mist);
               mist.triggerAnim("controller", "start");
               entity.level()
                  .playSound(
                     null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.PREDATION.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
                  );
               entity.swing(InteractionHand.MAIN_HAND, true);
               instance.addMasteryPoint(entity);
               instance.setCoolDown(instance.isMastered(entity) ? 3 : 5, mode);
            }
            break;
         case 1:
            this.openRefiningMenu(entity, instance);
            break;
         case 2:
            this.openSpatialStorage(entity, instance);
            break;
         case 3:
            MenuHelper.sendComingSoonMessage(entity);
            break;
         case 4:
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
                     TensuraParticleHelper.addServerAuraParticles(
                        target, TensuraParticleUtils.getBlackAura(0.5F, (float)target.getAttributeValue(Attributes.SCALE) * 4.0F, -0.3F), 5, 0.01
                     );
                     TensuraParticleHelper.spawnServerParticles(
                        entity.level(),
                        TensuraParticleUtils.getBlackWave(0.9F, target.getBbWidth() * 2.5F, -0.5F, true),
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
                     TensuraParticleHelper.addServerAuraParticles(
                        entity, TensuraParticleUtils.getBlackAura(0.5F, (float)entity.getAttributeValue(Attributes.SCALE) * 4.0F, -0.3F), 5, 0.01
                     );
                     TensuraParticleHelper.spawnServerParticles(
                        entity.level(),
                        TensuraParticleUtils.getBlackWave(0.9F, entity.getBbWidth() * 2.5F, -0.5F, true),
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
      }
   }

   public void onTick(ManasSkillInstance instance, LivingEntity entity) {
      if (entity instanceof Player player) {
         this.onRefiningTick(instance, player);
      }
   }

   private ManasSkillInstance getGluttony(LivingEntity entity) {
      Skills storage = SkillAPI.getSkillsFrom(entity);
      Optional<ManasSkillInstance> optional = storage.getSkill((ManasSkill)UniqueSkills.GLUTTONY.get());
      return optional.isPresent() && optional.get().getMastery() >= 0.0 ? optional.get() : null;
   }

   @Override
   public void openSpatialStorage(LivingEntity entity, ManasSkillInstance instance) {
      ManasSkillInstance gluttony = this.getGluttony(entity);
      if (gluttony != null) {
         this.moveItemsToSpatialStorage(instance, gluttony, entity, true);
      } else {
         IRefining.super.openSpatialStorage(entity, instance);
      }
   }

   @Override
   public boolean addItemToSpatialStorage(ManasSkillInstance instance, LivingEntity entity, ItemStack stack) {
      ManasSkillInstance gluttony = this.getGluttony(entity);
      return gluttony != null
         ? ((ISpatialStorage)gluttony.getSkill()).addItemToSpatialStorage(gluttony, entity, stack)
         : IRefining.super.addItemToSpatialStorage(instance, entity, stack);
   }

   @NotNull
   @Override
   public SpatialStorageContainer getSpatialStorage(ManasSkillInstance instance, Provider provide) {
      SpatialStorageContainer container = new SpatialStorageContainer(63, 128);
      container.fromTag(instance.getOrCreateTag().getList("SpatialStorage", 10), provide);
      return container;
   }
}
