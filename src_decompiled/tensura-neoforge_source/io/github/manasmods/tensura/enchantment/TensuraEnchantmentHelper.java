package io.github.manasmods.tensura.enchantment;

import io.github.manasmods.tensura.enchantment.template.EnchantmentPostDamageEffect;
import io.github.manasmods.tensura.registry.item.misc.TensuraEnchantmentEffectComponents;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.enchantment.TargetedConditionalEffect;
import net.minecraft.world.item.enchantment.EnchantmentHelper.EnchantmentInSlotVisitor;
import net.minecraft.world.item.enchantment.EnchantmentHelper.EnchantmentVisitor;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.level.Level;

public class TensuraEnchantmentHelper {
   public static final List<String> ENGRAVEMENT_ID_LIST = List.of(
      "tensura:dead_end_rainbow",
      "tensura:holy_coat",
      "tensura:magic_interference",
      "tensura:tsukumogami",
      "tensura:barrier_piercing",
      "tensura:breathing_support",
      "tensura:crushing",
      "tensura:energy_steal",
      "tensura:elemental_boost",
      "tensura:elemental_resistance",
      "tensura:energy_protection",
      "tensura:holy_weapon",
      "tensura:intangibility",
      "tensura:magic_weapon",
      "tensura:magicule_absorption",
      "tensura:magic_capacity",
      "tensura:magic_protection",
      "tensura:severance",
      "tensura:slotting",
      "tensura:soul_eater",
      "tensura:spiritual_protection",
      "tensura:severance_protection",
      "tensura:sturdy",
      "tensura:swift",
      "tensura:enervation",
      "tensura:lethargy",
      "tensura:sealing",
      "tensura:stagnation",
      "tensura:ruination",
      "tensura:vitality",
      "tensura:vigor",
      "tensura:transcendence",
      "tensura:growth",
      "tensura:restoration"
   );

   public static Holder<Enchantment> getEnchantment(Level level, ResourceKey<Enchantment> enchantment) {
      return level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(enchantment);
   }

   public static int getEnchantmentLevel(Level level, ResourceKey<Enchantment> enchantment, ItemStack stack) {
      return EnchantmentHelper.getItemEnchantmentLevel(getEnchantment(level, enchantment), stack);
   }

   public static int getEnchantmentLevel(Level level, ResourceKey<Enchantment> enchantment, LivingEntity entity) {
      return EnchantmentHelper.getEnchantmentLevel(getEnchantment(level, enchantment), entity);
   }

   public static int getArmorEnchantmentLevel(ResourceKey<Enchantment> enchantment, LivingEntity entity) {
      Holder<Enchantment> holder = getEnchantment(entity.level(), enchantment);
      int i = 0;

      for (ItemStack slot : entity.getArmorSlots()) {
         i += EnchantmentHelper.getItemEnchantmentLevel(holder, slot);
      }

      return i;
   }

   public static boolean hasTag(ItemEnchantments itemEnchantments, TagKey<Enchantment> tagKey) {
      for (Entry<Holder<Enchantment>> entry : itemEnchantments.entrySet()) {
         Holder<Enchantment> holder = (Holder<Enchantment>)entry.getKey();
         if (holder.is(tagKey)) {
            return true;
         }
      }

      return false;
   }

   public static boolean hasNotTag(ItemStack itemStack, TagKey<Enchantment> tagKey) {
      ItemEnchantments itemEnchantments = (ItemEnchantments)itemStack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
      if (itemEnchantments.isEmpty()) {
         return false;
      }

      for (Entry<Holder<Enchantment>> entry : itemEnchantments.entrySet()) {
         Holder<Enchantment> holder = (Holder<Enchantment>)entry.getKey();
         if (!holder.is(tagKey)) {
            return true;
         }
      }

      return false;
   }

   public static void doAdditionalAfterAttack(
      ServerLevel serverLevel, Entity entity, LivingEntity attacker, DamageSource damageSource, ItemStack itemStack, float originalDamage
   ) {
      if (itemStack != null) {
         runIterationOnItem(
            itemStack,
            EquipmentSlot.MAINHAND,
            attacker,
            (holder, i, enchantedItemInUse) -> {
               for (TargetedConditionalEffect<EnchantmentEntityEffect> effect : ((Enchantment)holder.value())
                  .getEffects((DataComponentType)TensuraEnchantmentEffectComponents.AFTER_ATTACK.get())) {
                  if (EnchantmentTarget.VICTIM != effect.enchanted()
                     && effect.matches(Enchantment.damageContext(serverLevel, i, entity, damageSource))
                     && effect.effect() instanceof EnchantmentPostDamageEffect postDamage) {
                     Entity target = (Entity)(switch (effect.affected()) {
                        case ATTACKER -> damageSource.getEntity();
                        case DAMAGING_ENTITY -> attacker;
                        case VICTIM -> entity;
                        default -> throw new MatchException(null, null);
                     });
                     if (target != null) {
                        postDamage.apply(serverLevel, i, enchantedItemInUse, target, target.position(), originalDamage);
                     }
                  }
               }
            }
         );
      }
   }

   public static void doAdditionalAfterDamage(
      ServerLevel serverLevel, Entity entity, LivingEntity attacker, DamageSource damageSource, ItemStack itemStack, float originalDamage
   ) {
      if (itemStack != null) {
         runIterationOnItem(
            itemStack,
            EquipmentSlot.MAINHAND,
            attacker,
            (holder, i, enchantedItemInUse) -> {
               for (TargetedConditionalEffect<EnchantmentEntityEffect> effect : ((Enchantment)holder.value())
                  .getEffects((DataComponentType)TensuraEnchantmentEffectComponents.AFTER_DAMAGE.get())) {
                  if (EnchantmentTarget.VICTIM != effect.enchanted()
                     && effect.matches(Enchantment.damageContext(serverLevel, i, entity, damageSource))
                     && effect.effect() instanceof EnchantmentPostDamageEffect postDamage) {
                     Entity target = (Entity)(switch (effect.affected()) {
                        case ATTACKER -> damageSource.getEntity();
                        case DAMAGING_ENTITY -> attacker;
                        case VICTIM -> entity;
                        default -> throw new MatchException(null, null);
                     });
                     if (target != null) {
                        postDamage.apply(serverLevel, i, enchantedItemInUse, target, target.position(), originalDamage);
                     }
                  }
               }
            }
         );
      }
   }

   public static void runIterationOnItem(ItemStack itemStack, EnchantmentVisitor enchantmentVisitor) {
      ItemEnchantments itemEnchantments = (ItemEnchantments)itemStack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
      if (!itemEnchantments.isEmpty()) {
         for (Entry<Holder<Enchantment>> holderEntry : itemEnchantments.entrySet()) {
            enchantmentVisitor.accept((Holder)holderEntry.getKey(), holderEntry.getIntValue());
         }
      }
   }

   private static void runIterationOnItem(
      ItemStack itemStack, EquipmentSlot equipmentSlot, LivingEntity livingEntity, EnchantmentInSlotVisitor enchantmentInSlotVisitor
   ) {
      if (!itemStack.isEmpty()) {
         ItemEnchantments itemEnchantments = (ItemEnchantments)itemStack.get(DataComponents.ENCHANTMENTS);
         if (itemEnchantments != null && !itemEnchantments.isEmpty()) {
            EnchantedItemInUse enchantedItemInUse = new EnchantedItemInUse(itemStack, equipmentSlot, livingEntity);

            for (Entry<Holder<Enchantment>> holderEntry : itemEnchantments.entrySet()) {
               Holder<Enchantment> holder = (Holder<Enchantment>)holderEntry.getKey();
               if (((Enchantment)holder.value()).matchingSlot(equipmentSlot)) {
                  enchantmentInSlotVisitor.accept(holder, holderEntry.getIntValue(), enchantedItemInUse);
               }
            }
         }
      }
   }
}
