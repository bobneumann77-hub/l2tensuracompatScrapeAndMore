package io.github.manasmods.tensura.enchantment.effect;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.manasmods.tensura.enchantment.template.EnchantmentPostDamageWithTypeEffect;
import io.github.manasmods.tensura.registry.item.misc.TensuraEnchantmentEffectComponents;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import org.jetbrains.annotations.NotNull;

public record AdditionalDamageEntity(
   LevelBasedValue amount, EnchantmentPostDamageWithTypeEffect.DamageCalculationType calculationType, Holder<DamageType> damageType
) implements EnchantmentPostDamageWithTypeEffect {
   public static final MapCodec<AdditionalDamageEntity> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            LevelBasedValue.CODEC.fieldOf("amount").forGetter(AdditionalDamageEntity::amount),
            EnchantmentPostDamageWithTypeEffect.DamageCalculationType.CODEC.fieldOf("calculation_type").forGetter(AdditionalDamageEntity::calculationType),
            DamageType.CODEC.fieldOf("damage_type").forGetter(AdditionalDamageEntity::damageType)
         )
         .apply(instance, AdditionalDamageEntity::new)
   );

   @Override
   public void postDamage(int i, EnchantedItemInUse enchantedItemInUse, Entity entity, float originalDamage) {
      if (entity.invulnerableTime < 40) {
         entity.invulnerableTime = 0;
         entity.hurt(this.getDamageSource(enchantedItemInUse.owner()), this.amount.calculate(i) * originalDamage);
      }
   }

   @NotNull
   public MapCodec<AdditionalDamageEntity> codec() {
      return (MapCodec<AdditionalDamageEntity>)TensuraEnchantmentEffectComponents.ADDITIONAL_DAMAGE.get();
   }
}
