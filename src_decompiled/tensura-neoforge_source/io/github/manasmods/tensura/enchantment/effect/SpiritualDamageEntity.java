package io.github.manasmods.tensura.enchantment.effect;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.enchantment.template.EnchantmentPostDamageWithTypeEffect;
import io.github.manasmods.tensura.registry.item.misc.TensuraEnchantmentEffectComponents;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import org.jetbrains.annotations.NotNull;

public record SpiritualDamageEntity(
   LevelBasedValue amount, EnchantmentPostDamageWithTypeEffect.DamageCalculationType calculationType, Holder<DamageType> damageType
) implements EnchantmentPostDamageWithTypeEffect {
   public static final MapCodec<SpiritualDamageEntity> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            LevelBasedValue.CODEC.fieldOf("amount").forGetter(SpiritualDamageEntity::amount),
            EnchantmentPostDamageWithTypeEffect.DamageCalculationType.CODEC.fieldOf("calculation_type").forGetter(SpiritualDamageEntity::calculationType),
            DamageType.CODEC.fieldOf("damage_type").forGetter(SpiritualDamageEntity::damageType)
         )
         .apply(instance, SpiritualDamageEntity::new)
   );

   @Override
   public void postDamage(int i, EnchantedItemInUse enchantedItemInUse, Entity entity, float originalDamage) {
      if (entity.invulnerableTime < 40) {
         if (entity instanceof LivingEntity target) {
            DamageSource source = new DamageSource(this.damageType, enchantedItemInUse.owner());
            TensuraDamageHelper.directSpiritualHurt(target, enchantedItemInUse.owner(), source, this.amount.calculate(i) * originalDamage);
         }
      }
   }

   @NotNull
   public MapCodec<SpiritualDamageEntity> codec() {
      return (MapCodec<SpiritualDamageEntity>)TensuraEnchantmentEffectComponents.SPIRITUAL_DAMAGE.get();
   }
}
