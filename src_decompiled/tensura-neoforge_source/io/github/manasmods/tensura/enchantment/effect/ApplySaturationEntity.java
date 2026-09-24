package io.github.manasmods.tensura.enchantment.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.manasmods.tensura.registry.item.misc.TensuraEnchantmentEffectComponents;
import java.util.Optional;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record ApplySaturationEntity(
   Optional<LevelBasedValue> handSaturation,
   Optional<LevelBasedValue> handExhaustion,
   Optional<Double> handRadius,
   Optional<LevelBasedValue> armorSaturation,
   Optional<LevelBasedValue> armorExhaustion,
   Optional<Double> armorRadius,
   Optional<EntityPredicate> aoePredicate
) implements EnchantmentEntityEffect {
   public static final MapCodec<ApplySaturationEntity> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            LevelBasedValue.CODEC.optionalFieldOf("handSaturation").forGetter(ApplySaturationEntity::handSaturation),
            LevelBasedValue.CODEC.optionalFieldOf("handExhaustion").forGetter(ApplySaturationEntity::handExhaustion),
            Codec.DOUBLE.optionalFieldOf("handRadius").forGetter(ApplySaturationEntity::handRadius),
            LevelBasedValue.CODEC.optionalFieldOf("armorSaturation").forGetter(ApplySaturationEntity::armorSaturation),
            LevelBasedValue.CODEC.optionalFieldOf("armorExhaustion").forGetter(ApplySaturationEntity::armorExhaustion),
            Codec.DOUBLE.optionalFieldOf("armorRadius").forGetter(ApplySaturationEntity::armorRadius),
            EntityPredicate.CODEC.optionalFieldOf("aoePredicate").forGetter(ApplySaturationEntity::aoePredicate)
         )
         .apply(instance, ApplySaturationEntity::new)
   );

   public void apply(ServerLevel serverLevel, int i, EnchantedItemInUse enchantedItemInUse, Entity entity, Vec3 vec3) {
      EquipmentSlot slot = enchantedItemInUse.inSlot();
      if (slot != null) {
         if (slot.isArmor()) {
            if (this.armorRadius.isEmpty()) {
               if (entity instanceof Player player && !player.getAbilities().invulnerable) {
                  FoodData data = player.getFoodData();
                  this.armorSaturation
                     .ifPresent(levelBasedValue -> data.setSaturation(Math.max(0.0F, data.getSaturationLevel() + levelBasedValue.calculate(i))));
                  this.armorExhaustion
                     .ifPresent(levelBasedValue -> data.setExhaustion(Math.max(0.0F, data.getExhaustionLevel() + levelBasedValue.calculate(i))));
               }
            } else {
               for (Player player : entity.level().players()) {
                  if (player != null
                     && !player.getAbilities().invulnerable
                     && !(player.distanceToSqr(entity) > Mth.square(this.armorRadius.get()))
                     && (this.aoePredicate.isEmpty() || this.aoePredicate().get().matches(serverLevel, player.position(), player))) {
                     FoodData data = player.getFoodData();
                     this.armorSaturation
                        .ifPresent(levelBasedValue -> data.setSaturation(Math.max(0.0F, data.getSaturationLevel() + levelBasedValue.calculate(i))));
                     this.armorExhaustion
                        .ifPresent(levelBasedValue -> data.setExhaustion(Math.max(0.0F, data.getExhaustionLevel() + levelBasedValue.calculate(i))));
                  }
               }
            }
         } else if (this.handRadius.isEmpty()) {
            if (entity instanceof Player player && !player.getAbilities().invulnerable) {
               FoodData data = player.getFoodData();
               this.handSaturation.ifPresent(levelBasedValue -> data.setSaturation(Math.max(0.0F, data.getSaturationLevel() + levelBasedValue.calculate(i))));
               this.handExhaustion.ifPresent(levelBasedValue -> data.setExhaustion(Math.max(0.0F, data.getExhaustionLevel() + levelBasedValue.calculate(i))));
            }
         } else {
            for (Player player : entity.level().players()) {
               if (player != null
                  && !player.getAbilities().invulnerable
                  && !(player.distanceToSqr(entity) > Mth.square(this.handRadius.get()))
                  && (this.aoePredicate.isEmpty() || this.aoePredicate().get().matches(serverLevel, player.position(), player))) {
                  FoodData data = player.getFoodData();
                  this.handSaturation
                     .ifPresent(levelBasedValue -> data.setSaturation(Math.max(0.0F, data.getSaturationLevel() + levelBasedValue.calculate(i))));
                  this.handExhaustion
                     .ifPresent(levelBasedValue -> data.setExhaustion(Math.max(0.0F, data.getExhaustionLevel() + levelBasedValue.calculate(i))));
               }
            }
         }
      }
   }

   @NotNull
   public MapCodec<ApplySaturationEntity> codec() {
      return (MapCodec<ApplySaturationEntity>)TensuraEnchantmentEffectComponents.APPLY_SATURATION.get();
   }
}
