package io.github.manasmods.tensura.enchantment.template;

import com.mojang.serialization.Codec;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public interface EnchantmentPostDamageWithTypeEffect extends EnchantmentPostDamageEffect {
   @Nullable
   Holder<DamageType> damageType();

   EnchantmentPostDamageWithTypeEffect.DamageCalculationType calculationType();

   void postDamage(int var1, EnchantedItemInUse var2, Entity var3, float var4);

   default DamageSource getDamageSource(LivingEntity attacker) {
      return this.damageType() == null ? null : new DamageSource(this.damageType(), attacker);
   }

   default float getWeaponDamage(LivingEntity attacker, Entity entity, EquipmentSlot slot, boolean enchanted) {
      DamageSource source = enchanted ? this.getDamageSource(attacker) : null;
      return TensuraDamageHelper.getWeaponDamage(attacker, entity, slot == EquipmentSlot.OFFHAND, source);
   }

   @Override
   default void apply(ServerLevel serverLevel, int i, EnchantedItemInUse itemInUse, Entity entity, Vec3 vec3, float originalDamage) {
      switch (this.calculationType()) {
         case STATIC_VALUE:
            this.postDamage(i, itemInUse, entity, 1.0F);
            break;
         case WEAPON_DAMAGE_MULTIPLY:
            this.postDamage(i, itemInUse, entity, this.getWeaponDamage(itemInUse.owner(), entity, itemInUse.inSlot(), false));
            break;
         case ENCHANTED_WEAPON_DAMAGE_MULTIPLY:
            this.postDamage(i, itemInUse, entity, this.getWeaponDamage(itemInUse.owner(), entity, itemInUse.inSlot(), true));
            break;
         case TOTAL_ATTACK_MULTIPLY:
            this.postDamage(i, itemInUse, entity, originalDamage);
      }
   }

   default void apply(ServerLevel serverLevel, int i, EnchantedItemInUse itemInUse, Entity entity, Vec3 vec3) {
      switch (this.calculationType()) {
         case STATIC_VALUE:
            this.postDamage(i, itemInUse, entity, 1.0F);
            break;
         case WEAPON_DAMAGE_MULTIPLY:
            this.postDamage(i, itemInUse, entity, this.getWeaponDamage(itemInUse.owner(), entity, itemInUse.inSlot(), false));
            break;
         case ENCHANTED_WEAPON_DAMAGE_MULTIPLY:
            this.postDamage(i, itemInUse, entity, this.getWeaponDamage(itemInUse.owner(), entity, itemInUse.inSlot(), true));
         case TOTAL_ATTACK_MULTIPLY:
      }
   }

   enum DamageCalculationType {
      STATIC_VALUE,
      WEAPON_DAMAGE_MULTIPLY,
      ENCHANTED_WEAPON_DAMAGE_MULTIPLY,
      TOTAL_ATTACK_MULTIPLY;

      public static final Codec<EnchantmentPostDamageWithTypeEffect.DamageCalculationType> CODEC = Codec.STRING
         .xmap(name -> valueOf(name.toUpperCase()), Enum::name);
   }
}
