package io.github.manasmods.tensura.enchantment.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.enchantment.template.EnchantmentPostDamageEffect;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.item.misc.TensuraEnchantmentEffectComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record DeadEndRainbowEntity(int attackNumber, LevelBasedValue bonusPercentage, LevelBasedValue minAmount, LevelBasedValue maxAmount, int cooldown)
   implements EnchantmentPostDamageEffect {
   public static final MapCodec<DeadEndRainbowEntity> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            Codec.INT.fieldOf("attack_number").forGetter(DeadEndRainbowEntity::attackNumber),
            LevelBasedValue.CODEC.fieldOf("bonus_percentage").forGetter(DeadEndRainbowEntity::bonusPercentage),
            LevelBasedValue.CODEC.fieldOf("min_amount").forGetter(DeadEndRainbowEntity::minAmount),
            LevelBasedValue.CODEC.fieldOf("max_amount").forGetter(DeadEndRainbowEntity::maxAmount),
            Codec.INT.fieldOf("cooldown").forGetter(DeadEndRainbowEntity::cooldown)
         )
         .apply(instance, DeadEndRainbowEntity::new)
   );

   @Override
   public void apply(ServerLevel serverLevel, int i, EnchantedItemInUse enchantedItemInUse, Entity entity, Vec3 vec3, float originalDamage) {
      this.applyDeadEndRainbow(i, enchantedItemInUse, entity);
   }

   public void apply(ServerLevel serverLevel, int i, EnchantedItemInUse enchantedItemInUse, Entity entity, Vec3 vec3) {
      this.applyDeadEndRainbow(i, enchantedItemInUse, entity);
   }

   public void applyDeadEndRainbow(int i, EnchantedItemInUse enchantedItemInUse, Entity entity) {
      if (entity.invulnerableTime < 40) {
         if (enchantedItemInUse.owner() instanceof Player player) {
            if (player.getCooldowns().isOnCooldown(enchantedItemInUse.itemStack().getItem())) {
               return;
            }

            player.getCooldowns().addCooldown(enchantedItemInUse.itemStack().getItem(), this.cooldown);
         }

         if (entity instanceof LivingEntity target) {
            LivingEntity attacker = enchantedItemInUse.owner();
            double SHP = target.getAttributeValue(TensuraAttributes.MAX_SPIRITUAL_HEALTH);
            float shpDamage = (float)Mth.clamp(
               SHP / this.attackNumber + SHP * this.bonusPercentage.calculate(i), this.minAmount.calculate(i), this.maxAmount.calculate(i)
            );
            float strength = attacker instanceof Player player ? player.getAttackStrengthScale(0.5F) : 1.0F;
            shpDamage *= 0.2F + strength * strength * 0.8F;
            TensuraDamageHelper.directSpiritualHurt(target, attacker, shpDamage);
         }
      }
   }

   @NotNull
   public MapCodec<DeadEndRainbowEntity> codec() {
      return (MapCodec<DeadEndRainbowEntity>)TensuraEnchantmentEffectComponents.DEAD_END_RAINBOW.get();
   }
}
