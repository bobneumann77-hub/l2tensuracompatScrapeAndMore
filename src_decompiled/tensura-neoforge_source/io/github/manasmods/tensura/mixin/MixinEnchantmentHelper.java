package io.github.manasmods.tensura.mixin;

import io.github.manasmods.tensura.enchantment.effect.WeaponBaseAttributeMultiplier;
import io.github.manasmods.tensura.registry.item.misc.TensuraEnchantmentEffectComponents;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnchantmentHelper.class)
public abstract class MixinEnchantmentHelper {
   @Inject(
      method = "forEachModifier(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EquipmentSlot;Ljava/util/function/BiConsumer;)V",
      at = @At("HEAD")
   )
   private static void forEachModifier(
      ItemStack itemStack, EquipmentSlot equipmentSlot, BiConsumer<Holder<Attribute>, AttributeModifier> biConsumer, CallbackInfo ci
   ) {
      if (itemStack.isEnchanted()) {
         ItemEnchantments enchants = (ItemEnchantments)itemStack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
         if (!enchants.isEmpty()) {
            for (Entry<Holder<Enchantment>> entry : enchants.entrySet()) {
               Holder<Enchantment> holder = (Holder<Enchantment>)entry.getKey();
               Enchantment enchant = (Enchantment)holder.value();
               if (enchant.matchingSlot(equipmentSlot)) {
                  List<WeaponBaseAttributeMultiplier> effects = enchant.getEffects(
                     (DataComponentType)TensuraEnchantmentEffectComponents.WEAPON_MULTIPLIER.get()
                  );
                  if (!effects.isEmpty()) {
                     int level = entry.getIntValue();

                     for (WeaponBaseAttributeMultiplier effect : effects) {
                        if (effect.matches(itemStack) && effect.group().test(equipmentSlot)) {
                           biConsumer.accept(effect.attribute(), effect.getModifier(level, itemStack, equipmentSlot));
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @Inject(
      method = "forEachModifier(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EquipmentSlotGroup;Ljava/util/function/BiConsumer;)V",
      at = @At("HEAD")
   )
   private static void forEachModifierGroup(
      ItemStack itemStack, EquipmentSlotGroup equipmentSlotGroup, BiConsumer<Holder<Attribute>, AttributeModifier> biConsumer, CallbackInfo ci
   ) {
      if (itemStack.isEnchanted()) {
         ItemEnchantments enchants = (ItemEnchantments)itemStack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
         if (!enchants.isEmpty()) {
            for (Entry<Holder<Enchantment>> entry : enchants.entrySet()) {
               Holder<Enchantment> holder = (Holder<Enchantment>)entry.getKey();
               Enchantment enchant = (Enchantment)holder.value();
               if (enchant.definition().slots().contains(equipmentSlotGroup)) {
                  List<WeaponBaseAttributeMultiplier> effects = enchant.getEffects(
                     (DataComponentType)TensuraEnchantmentEffectComponents.WEAPON_MULTIPLIER.get()
                  );
                  if (!effects.isEmpty()) {
                     int level = entry.getIntValue();

                     for (WeaponBaseAttributeMultiplier effect : effects) {
                        if (effect.matches(itemStack) && WeaponBaseAttributeMultiplier.isSameGroup(effect.group(), equipmentSlotGroup)) {
                           biConsumer.accept(effect.attribute(), effect.getModifier(level, itemStack, equipmentSlotGroup));
                        }
                     }
                  }
               }
            }
         }
      }
   }
}
