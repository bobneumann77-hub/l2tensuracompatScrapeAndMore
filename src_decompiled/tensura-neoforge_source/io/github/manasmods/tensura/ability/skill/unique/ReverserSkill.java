package io.github.manasmods.tensura.ability.skill.unique;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.data.TensuraTags;
import io.github.manasmods.tensura.enchantment.TensuraEnchantmentHelper;
import io.github.manasmods.tensura.enchantment.TensuraEnchantments;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.Alignment;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments.Mutable;
import org.jetbrains.annotations.Nullable;

public class ReverserSkill extends Skill {
   private static final UniqueSkillConfig.Reverser CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).Reverser;

   public ReverserSkill() {
      super(Skill.SkillType.UNIQUE);
   }

   @Override
   public double getDefaultAcquiringMagiculeCost() {
      return CONFIG.mpAcquirement;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
      return instance.isMastered(entity);
   }

   public int getModes(ManasSkillInstance instance) {
      return 3;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      if (reverse) {
         return mode == 0 ? 2 : mode - 1;
      } else {
         return mode == 2 ? 0 : mode + 1;
      }
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> "reverser.reverse";
         case 1 -> "reverser.buff";
         case 2 -> "reverser.debuff";
         default -> super.getModeId(instance, mode);
      };
   }

   private void turnChaos(LivingEntity entity) {
      IExistence existence = TensuraStorages.getExistenceFrom(entity);
      existence.setAlignment(Alignment.CHAOS);
      existence.markDirty();
   }

   private void resetChaos(LivingEntity entity) {
      IExistence existence = TensuraStorages.getExistenceFrom(entity);
      if (existence.getAlignment().equals(Alignment.CHAOS)) {
         existence.setAlignment(existence.getOriginalAlignment());
         existence.markDirty();
      }
   }

   @Override
   public void onForgetSkill(ManasSkillInstance instance, LivingEntity entity) {
      super.onForgetSkill(instance, entity);
      this.resetChaos(entity);
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (instance.isMastered(entity)) {
         ItemStack stack = entity.getMainHandItem();
         if (!stack.isEmpty()) {
            if (mode == 1) {
               boolean success = false;
               PotionContents contents = (PotionContents)stack.get(DataComponents.POTION_CONTENTS);
               if (contents != null) {
                  List<MobEffectInstance> list = new ArrayList<>();
                  contents.forEachEffect(
                     effect -> {
                        if (((MobEffect)effect.getEffect().value()).getCategory() == MobEffectCategory.HARMFUL) {
                           Pair<Holder<MobEffect>, Float> reversed = this.getReversedDebuff(effect.getEffect());
                           if (reversed != null) {
                              int duration = stack.is(Items.LINGERING_POTION) && contents.potion().isPresent()
                                 ? effect.getDuration() / 4
                                 : effect.getDuration();
                              MobEffectInstance reversedInstance = new MobEffectInstance(
                                 (Holder)reversed.getFirst(),
                                 duration,
                                 Math.round((effect.getAmplifier() + 1) * (Float)reversed.getSecond()) - 1,
                                 effect.isAmbient(),
                                 effect.isVisible(),
                                 effect.showIcon()
                              );
                              reversedInstance.tensura$setTag(effect.tensura$getOrCreateTag());
                              reversedInstance.tensura$setSource(effect.tensura$getSource());
                              reversedInstance.tensura$setSourceAbility(effect.tensura$getSourceAbility());
                              list.add(reversedInstance);
                           }
                        }
                     }
                  );
                  if (!list.isEmpty()) {
                     stack.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.empty(), contents.customColor(), list));
                     success = true;
                  }
               }

               DataComponentType<ItemEnchantments> type = stack.is(Items.ENCHANTED_BOOK) ? DataComponents.STORED_ENCHANTMENTS : DataComponents.ENCHANTMENTS;
               ItemEnchantments itemEnchantments = (ItemEnchantments)stack.getOrDefault(type, ItemEnchantments.EMPTY);
               Mutable mutable = new Mutable(itemEnchantments);

               for (Holder<Enchantment> holder : itemEnchantments.keySet()) {
                  if (holder.is(EnchantmentTags.CURSE)
                     && !holder.is(TensuraTags.Enchantments.INHERITANCE_ENGRAVING)
                     && mutable.getLevel(holder) <= ((Enchantment)holder.value()).getMaxLevel()) {
                     boolean removal = this.isRemovalCurse(holder);
                     ResourceKey<Enchantment> blessing = removal ? null : this.getReversedCurse(holder);
                     if (removal || blessing != null) {
                        if (EnergyHelper.isOutOfEnergy(entity, 0.0, CONFIG.magiculeCostCurse)) {
                           break;
                        }

                        mutable.removeIf(enchantment -> enchantment.equals(holder));
                        if (!removal) {
                           Holder<Enchantment> blessingHolder = TensuraEnchantmentHelper.getEnchantment(entity.level(), blessing);
                           mutable.upgrade(blessingHolder, itemEnchantments.getLevel(holder));
                        }

                        success = true;
                     }
                  }
               }

               if (success) {
                  stack.set(type, mutable.toImmutable());
                  entity.swing(InteractionHand.MAIN_HAND, true);
                  TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.ENCHANT, 2.0);
                  entity.level()
                     .playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENCHANTMENT_TABLE_USE, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
                  return;
               }
            } else if (mode == 2) {
               PotionContents contents = (PotionContents)stack.get(DataComponents.POTION_CONTENTS);
               if (contents != null) {
                  List<MobEffectInstance> list = new ArrayList<>();
                  contents.forEachEffect(
                     effect -> {
                        if (((MobEffect)effect.getEffect().value()).isBeneficial()) {
                           Pair<Holder<MobEffect>, Float> reversed = this.getReversedBuff(effect.getEffect());
                           if (reversed != null) {
                              int duration = stack.is(Items.LINGERING_POTION) && contents.potion().isPresent()
                                 ? effect.getDuration() / 4
                                 : effect.getDuration();
                              MobEffectInstance reversedInstance = new MobEffectInstance(
                                 (Holder)reversed.getFirst(),
                                 duration,
                                 Math.round((effect.getAmplifier() + 1) * (Float)reversed.getSecond()) - 1,
                                 effect.isAmbient(),
                                 effect.isVisible(),
                                 effect.showIcon()
                              );
                              reversedInstance.tensura$setTag(effect.tensura$getOrCreateTag());
                              reversedInstance.tensura$setSource(effect.tensura$getSource());
                              reversedInstance.tensura$setSourceAbility(effect.tensura$getSourceAbility());
                              list.add(reversedInstance);
                           }
                        }
                     }
                  );
                  if (!list.isEmpty()) {
                     stack.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.empty(), contents.customColor(), list));
                     entity.swing(InteractionHand.MAIN_HAND, true);
                     TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.ENCHANT, 2.0);
                     entity.level()
                        .playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENCHANTMENT_TABLE_USE, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
                     return;
                  }
               }
            }
         }
      }

      LivingEntity target = ObjectSelectionHelper.getTargetingEntity(entity, 3.0, false);
      if (target != null) {
         if (target instanceof Player player && player.getAbilities().invulnerable) {
            return;
         }

         if (mode == 0) {
            if (!instance.isMastered(entity)) {
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
               return;
            }

            IExistence existence = TensuraStorages.getExistenceFrom(target);
            if (!existence.getAlignment().equals(Alignment.DEFAULT)) {
               entity.swing(InteractionHand.MAIN_HAND, true);
               if (existence.getAlignment().equals(Alignment.CHAOS)) {
                  this.resetChaos(target);
                  TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.SCULK_SOUL, 2.0);
                  entity.level()
                     .playSound(
                        null,
                        target.getX(),
                        target.getY(),
                        target.getZ(),
                        (SoundEvent)TensuraSoundEvents.BUFF_ACTIVATE.get(),
                        TensuraSkill.ABILITY_SOUND,
                        1.0F,
                        1.0F
                     );
               } else {
                  this.turnChaos(target);
                  TensuraParticleHelper.addServerParticlesAroundSelf(target, (ParticleOptions)TensuraParticleTypes.SOUL.get(), 2.0);
                  entity.level()
                     .playSound(
                        null,
                        target.getX(),
                        target.getY(),
                        target.getZ(),
                        (SoundEvent)TensuraSoundEvents.BUFF_DEACTIVATE.get(),
                        TensuraSkill.ABILITY_SOUND,
                        1.0F,
                        1.0F
                     );
               }
            } else {
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
            }
         } else if (mode == 1 ? !this.applyDebuffReverse(target) : !this.applyBuffReverse(target)) {
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
         } else {
            instance.addMasteryPoint(entity);
            entity.swing(InteractionHand.MAIN_HAND, true);
            TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.COMPOSTER, 2.0);
            entity.level()
               .playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
         }
      } else if (mode == 1) {
         if (this.applyDebuffReverse(entity)) {
            instance.addMasteryPoint(entity);
            entity.swing(InteractionHand.MAIN_HAND, true);
            TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.COMPOSTER, 2.0);
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
      } else if (mode == 0) {
         IExistence existence = TensuraStorages.getExistenceFrom(entity);
         if (existence.getAlignment().equals(Alignment.DEFAULT)) {
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
            return;
         }

         entity.swing(InteractionHand.MAIN_HAND, true);
         if (existence.getAlignment().equals(Alignment.CHAOS)) {
            this.resetChaos(entity);
            TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.SCULK_SOUL, 2.0);
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
         } else {
            this.turnChaos(entity);
            TensuraParticleHelper.addServerParticlesAroundSelf(entity, (ParticleOptions)TensuraParticleTypes.SOUL.get(), 2.0);
            entity.level()
               .playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.BUFF_ACTIVATE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
         }
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

   public void onToggleOn(ManasSkillInstance skillInstance, LivingEntity living) {
      this.applyDebuffReverse(living);
   }

   public boolean onEffectAdded(ManasSkillInstance instance, LivingEntity entity, @Nullable Entity source, Changeable<MobEffectInstance> effectInstance) {
      if (!instance.isToggled()) {
         return true;
      }

      MobEffectInstance effect = (MobEffectInstance)effectInstance.get();
      if (effect == null) {
         return true;
      }

      if (((MobEffect)effect.getEffect().value()).getCategory() != MobEffectCategory.HARMFUL) {
         return true;
      }

      Pair<Holder<MobEffect>, Float> reversed = this.getReversedDebuff(effect.getEffect());
      if (reversed == null) {
         return true;
      }

      MobEffectInstance reversedInstance = new MobEffectInstance(
         (Holder)reversed.getFirst(),
         effect.getDuration(),
         Math.round((effect.getAmplifier() + 1) * (Float)reversed.getSecond()) - 1,
         effect.isAmbient(),
         effect.isVisible(),
         effect.showIcon()
      );
      reversedInstance.tensura$setTag(effect.tensura$getOrCreateTag());
      reversedInstance.tensura$setSource(effect.tensura$getSource());
      reversedInstance.tensura$setSourceAbility(effect.tensura$getSourceAbility());
      effectInstance.set(reversedInstance);
      return true;
   }

   private boolean applyDebuffReverse(LivingEntity entity) {
      boolean success = false;

      for (MobEffectInstance instance : List.copyOf(entity.getActiveEffects())) {
         if (((MobEffect)instance.getEffect().value()).getCategory() == MobEffectCategory.HARMFUL
            && !instance.getEffect().is(TensuraTags.MobEffects.SKILL_DEBUFF)
            && !instance.is(TensuraMobEffects.getReference(TensuraMobEffects.MAGICULE_POISON))) {
            Pair<Holder<MobEffect>, Float> reversed = this.getReversedDebuff(instance.getEffect());
            if (reversed != null) {
               MobEffectInstance reversedInstance = new MobEffectInstance(
                  (Holder)reversed.getFirst(),
                  instance.getDuration(),
                  Math.round((instance.getAmplifier() + 1) * (Float)reversed.getSecond()) - 1,
                  instance.isAmbient(),
                  instance.isVisible(),
                  instance.showIcon()
               );
               reversedInstance.tensura$setTag(instance.tensura$getOrCreateTag());
               reversedInstance.tensura$setSource(instance.tensura$getSource());
               reversedInstance.tensura$setSourceAbility(instance.tensura$getSourceAbility());
               entity.addEffect(reversedInstance);
               success = true;
            }

            entity.removeEffect(instance.getEffect());
         }
      }

      return success;
   }

   private boolean applyBuffReverse(LivingEntity entity) {
      boolean success = false;

      for (MobEffectInstance instance : List.copyOf(entity.getActiveEffects())) {
         if (((MobEffect)instance.getEffect().value()).isBeneficial()) {
            Pair<Holder<MobEffect>, Float> reversed = this.getReversedBuff(instance.getEffect());
            if (reversed != null) {
               MobEffectInstance reversedInstance = new MobEffectInstance(
                  (Holder)reversed.getFirst(),
                  instance.getDuration(),
                  Math.round((instance.getAmplifier() + 1) * (Float)reversed.getSecond()) - 1,
                  instance.isAmbient(),
                  instance.isVisible(),
                  instance.showIcon()
               );
               reversedInstance.tensura$setTag(instance.tensura$getOrCreateTag());
               reversedInstance.tensura$setSource(instance.tensura$getSource());
               reversedInstance.tensura$setSourceAbility(instance.tensura$getSourceAbility());
               entity.addEffect(reversedInstance);
               entity.removeEffect(instance.getEffect());
               success = true;
            }
         }
      }

      return success;
   }

   private Pair<Holder<MobEffect>, Float> getReversedDebuff(Holder<MobEffect> mobEffect) {
      if (mobEffect.equals(MobEffects.MOVEMENT_SLOWDOWN)) {
         return Pair.of(MobEffects.MOVEMENT_SPEED, 1.0F);
      } else if (mobEffect.equals(TensuraMobEffects.getReference(TensuraMobEffects.PARALYSIS))) {
         return Pair.of(MobEffects.MOVEMENT_SPEED, 1.0F);
      } else if (mobEffect.equals(MobEffects.WITHER)) {
         return Pair.of(MobEffects.REGENERATION, 1.0F);
      } else if (mobEffect.equals(MobEffects.POISON)) {
         return Pair.of(MobEffects.REGENERATION, 1.0F);
      } else if (mobEffect.equals(TensuraMobEffects.getReference(TensuraMobEffects.CORROSION))) {
         return Pair.of(MobEffects.REGENERATION, 2.0F);
      } else if (mobEffect.equals(TensuraMobEffects.getReference(TensuraMobEffects.FATAL_POISON))) {
         return Pair.of(MobEffects.REGENERATION, 2.0F);
      } else if (mobEffect.equals(TensuraMobEffects.getReference(TensuraMobEffects.BURDEN))) {
         return Pair.of(MobEffects.SLOW_FALLING, 1.0F);
      } else if (mobEffect.equals(TensuraMobEffects.getReference(TensuraMobEffects.FRAGILITY))) {
         return Pair.of(MobEffects.DAMAGE_RESISTANCE, 0.5F);
      } else if (mobEffect.equals(TensuraMobEffects.getReference(TensuraMobEffects.HYPNOSIS))) {
         return Pair.of(TensuraMobEffects.getReference(TensuraMobEffects.ILLUSION_BOOST), 1.0F);
      } else if (mobEffect.equals(MobEffects.DIG_SLOWDOWN)) {
         return Pair.of(MobEffects.DIG_SPEED, 1.0F);
      } else if (mobEffect.equals(MobEffects.HUNGER)) {
         return Pair.of(MobEffects.SATURATION, 1.0F);
      } else if (mobEffect.equals(MobEffects.UNLUCK)) {
         return Pair.of(MobEffects.LUCK, 1.0F);
      } else if (mobEffect.equals(MobEffects.WEAKNESS)) {
         return Pair.of(MobEffects.DAMAGE_BOOST, 1.0F);
      } else if (mobEffect.equals(MobEffects.BLINDNESS)) {
         return Pair.of(MobEffects.NIGHT_VISION, 1.0F);
      } else if (mobEffect.equals(MobEffects.DARKNESS)) {
         return Pair.of(MobEffects.NIGHT_VISION, 1.0F);
      } else {
         return mobEffect.equals(MobEffects.GLOWING) ? Pair.of(MobEffects.INVISIBILITY, 1.0F) : null;
      }
   }

   private Pair<Holder<MobEffect>, Float> getReversedBuff(Holder<MobEffect> mobEffect) {
      if (mobEffect.equals(MobEffects.MOVEMENT_SPEED)) {
         return Pair.of(MobEffects.MOVEMENT_SLOWDOWN, 1.0F);
      } else if (mobEffect.equals(MobEffects.REGENERATION)) {
         return Pair.of(MobEffects.WITHER, 1.0F);
      } else if (mobEffect.equals(MobEffects.SLOW_FALLING)) {
         return Pair.of(TensuraMobEffects.getReference(TensuraMobEffects.BURDEN), 1.0F);
      } else if (mobEffect.equals(MobEffects.JUMP)) {
         return Pair.of(TensuraMobEffects.getReference(TensuraMobEffects.BURDEN), 2.0F);
      } else if (mobEffect.equals(MobEffects.DAMAGE_RESISTANCE)) {
         return Pair.of(TensuraMobEffects.getReference(TensuraMobEffects.FRAGILITY), 2.0F);
      } else if (mobEffect.equals(TensuraMobEffects.getReference(TensuraMobEffects.ILLUSION_BOOST))) {
         return Pair.of(TensuraMobEffects.getReference(TensuraMobEffects.HYPNOSIS), 1.0F);
      } else if (mobEffect.equals(MobEffects.DIG_SPEED)) {
         return Pair.of(MobEffects.DIG_SLOWDOWN, 1.0F);
      } else if (mobEffect.equals(MobEffects.SATURATION)) {
         return Pair.of(MobEffects.HUNGER, 1.0F);
      } else if (mobEffect.equals(MobEffects.LUCK)) {
         return Pair.of(MobEffects.UNLUCK, 1.0F);
      } else if (mobEffect.equals(MobEffects.DAMAGE_BOOST)) {
         return Pair.of(MobEffects.WEAKNESS, 1.0F);
      } else if (mobEffect.equals(MobEffects.INVISIBILITY)) {
         return Pair.of(MobEffects.GLOWING, 1.0F);
      } else {
         return mobEffect.equals(MobEffects.NIGHT_VISION) ? Pair.of(MobEffects.DARKNESS, 1.0F) : null;
      }
   }

   private ResourceKey<Enchantment> getReversedCurse(Holder<Enchantment> curse) {
      if (curse.is(TensuraEnchantments.ENERVATION)) {
         return TensuraEnchantments.VITALITY;
      } else if (curse.is(TensuraEnchantments.LETHARGY)) {
         return TensuraEnchantments.VIGOR;
      } else if (curse.is(TensuraEnchantments.SEALING)) {
         return TensuraEnchantments.TRANSCENDENCE;
      } else if (curse.is(TensuraEnchantments.STAGNATION)) {
         return TensuraEnchantments.GROWTH;
      } else {
         return curse.is(TensuraEnchantments.RUINATION) ? TensuraEnchantments.RESTORATION : null;
      }
   }

   private boolean isRemovalCurse(Holder<Enchantment> curse) {
      return curse.is(Enchantments.BINDING_CURSE) || curse.is(Enchantments.VANISHING_CURSE);
   }
}
