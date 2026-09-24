package io.github.manasmods.tensura.enchantment.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemAttributeModifiers.Entry;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentLocationBasedEffect;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record WeaponBaseAttributeMultiplier(
   ResourceLocation id, Holder<Attribute> attribute, LevelBasedValue amount, Optional<ItemPredicate> predicate, EquipmentSlotGroup group, boolean flatValue
) implements EnchantmentLocationBasedEffect {
   public static final MapCodec<WeaponBaseAttributeMultiplier> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(WeaponBaseAttributeMultiplier::id),
            Attribute.CODEC.fieldOf("attribute").forGetter(WeaponBaseAttributeMultiplier::attribute),
            LevelBasedValue.CODEC.fieldOf("amount").forGetter(WeaponBaseAttributeMultiplier::amount),
            ItemPredicate.CODEC.optionalFieldOf("predicate").forGetter(WeaponBaseAttributeMultiplier::predicate),
            EquipmentSlotGroup.CODEC.optionalFieldOf("group", EquipmentSlotGroup.ANY).forGetter(WeaponBaseAttributeMultiplier::group),
            Codec.BOOL.optionalFieldOf("flatValue", false).forGetter(WeaponBaseAttributeMultiplier::flatValue)
         )
         .apply(instance, WeaponBaseAttributeMultiplier::new)
   );

   private ResourceLocation idForSlot(StringRepresentable stringRepresentable) {
      return this.id.withSuffix("/" + stringRepresentable.getSerializedName());
   }

   public boolean matches(ItemStack stack) {
      return this.predicate.<Boolean>map(itemPredicate -> itemPredicate.test(stack)).orElse(true);
   }

   public AttributeModifier getModifier(int i, ItemStack stack, EquipmentSlot slot) {
      double amount = 1.0;
      if (!this.flatValue()) {
         ItemAttributeModifiers modifiers = (ItemAttributeModifiers)stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
         if (modifiers != null) {
            amount = this.getBaseAmount(amount, modifiers, slot);
         }

         if (stack.getItem() instanceof ArmorItem armor) {
            amount = this.getBaseAmount(amount, armor.getDefaultAttributeModifiers(), slot);
         }
      }

      return new AttributeModifier(this.idForSlot(slot), amount * this.amount().calculate(i), Operation.ADD_VALUE);
   }

   public AttributeModifier getModifier(int i, ItemStack stack, EquipmentSlotGroup slot) {
      double amount = 1.0;
      if (!this.flatValue()) {
         ItemAttributeModifiers modifiers = (ItemAttributeModifiers)stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
         if (modifiers != null) {
            amount = this.getBaseAmount(amount, modifiers, slot);
         }

         if (stack.getItem() instanceof ArmorItem armor) {
            amount = this.getBaseAmount(amount, armor.getDefaultAttributeModifiers(), slot);
         }
      }

      return new AttributeModifier(this.idForSlot(slot), amount * this.amount().calculate(i), Operation.ADD_VALUE);
   }

   private double getBaseAmount(double amount, ItemAttributeModifiers modifiers, EquipmentSlotGroup slot) {
      for (Entry entry : modifiers.modifiers()) {
         if (entry.attribute() == this.attribute && entry.slot() == slot) {
            AttributeModifier modifier = entry.modifier();
            if (modifier.operation().equals(Operation.ADD_VALUE)) {
               amount += entry.modifier().amount();
            } else {
               amount *= entry.modifier().amount();
            }
         }
      }

      return amount;
   }

   private double getBaseAmount(double amount, ItemAttributeModifiers modifiers, EquipmentSlot slot) {
      for (Entry entry : modifiers.modifiers()) {
         if (entry.attribute() == this.attribute && entry.slot().test(slot)) {
            AttributeModifier modifier = entry.modifier();
            if (modifier.operation().equals(Operation.ADD_VALUE)) {
               amount += entry.modifier().amount();
            } else {
               amount *= entry.modifier().amount();
            }
         }
      }

      return amount;
   }

   public void onChangedBlock(ServerLevel serverLevel, int i, EnchantedItemInUse enchantedItemInUse, Entity entity, Vec3 vec3, boolean bl) {
      if (bl && entity instanceof LivingEntity livingEntity) {
         if (!this.group.test(enchantedItemInUse.inSlot())) {
            return;
         }

         if (!this.matches(enchantedItemInUse.itemStack())) {
            return;
         }

         AttributeInstance instance = livingEntity.getAttribute(this.attribute);
         if (instance == null) {
            return;
         }

         instance.addOrUpdateTransientModifier(this.getModifier(i, enchantedItemInUse.itemStack(), enchantedItemInUse.inSlot()));
      }
   }

   public void onDeactivated(EnchantedItemInUse enchantedItemInUse, Entity entity, Vec3 vec3, int i) {
      if (entity instanceof LivingEntity livingEntity) {
         AttributeInstance instance = livingEntity.getAttribute(this.attribute);
         if (instance == null) {
            return;
         }

         instance.removeModifier(this.idForSlot(enchantedItemInUse.inSlot()));
      }
   }

   @NotNull
   public MapCodec<WeaponBaseAttributeMultiplier> codec() {
      return CODEC;
   }

   public static boolean isSameGroup(EquipmentSlotGroup first, EquipmentSlotGroup second) {
      if (first == second) {
         return true;
      } else if (first.equals(EquipmentSlotGroup.ANY) || second.equals(EquipmentSlotGroup.ANY)) {
         return true;
      } else if (first.equals(EquipmentSlotGroup.HAND)) {
         return second.equals(EquipmentSlotGroup.MAINHAND) || second.equals(EquipmentSlotGroup.OFFHAND);
      } else if (second.equals(EquipmentSlotGroup.HAND)) {
         return first.equals(EquipmentSlotGroup.MAINHAND) || first.equals(EquipmentSlotGroup.OFFHAND);
      } else if (first.equals(EquipmentSlotGroup.ARMOR)) {
         return second.equals(EquipmentSlotGroup.HEAD)
            || second.equals(EquipmentSlotGroup.CHEST)
            || second.equals(EquipmentSlotGroup.LEGS)
            || second.equals(EquipmentSlotGroup.FEET);
      } else {
         return !second.equals(EquipmentSlotGroup.ARMOR)
            ? false
            : first.equals(EquipmentSlotGroup.HEAD)
               || first.equals(EquipmentSlotGroup.CHEST)
               || first.equals(EquipmentSlotGroup.LEGS)
               || first.equals(EquipmentSlotGroup.FEET);
      }
   }
}
