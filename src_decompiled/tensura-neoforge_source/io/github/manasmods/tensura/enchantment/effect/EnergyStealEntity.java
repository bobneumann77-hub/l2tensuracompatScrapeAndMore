package io.github.manasmods.tensura.enchantment.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.enchantment.template.EnchantmentPostDamageEffect;
import io.github.manasmods.tensura.registry.item.misc.TensuraEnchantmentEffectComponents;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record EnergyStealEntity(LevelBasedValue percentage, int cooldown) implements EnchantmentPostDamageEffect {
   public static final MapCodec<EnergyStealEntity> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            LevelBasedValue.CODEC.fieldOf("percentage").forGetter(EnergyStealEntity::percentage),
            Codec.INT.fieldOf("cooldown").forGetter(EnergyStealEntity::cooldown)
         )
         .apply(instance, EnergyStealEntity::new)
   );

   @Override
   public void apply(ServerLevel serverLevel, int i, EnchantedItemInUse enchantedItemInUse, Entity entity, Vec3 vec3, float originalDamage) {
      this.applyEnergySteal(i, enchantedItemInUse, entity);
   }

   public void apply(ServerLevel serverLevel, int i, EnchantedItemInUse enchantedItemInUse, Entity entity, Vec3 vec3) {
      this.applyEnergySteal(i, enchantedItemInUse, entity);
   }

   public void applyEnergySteal(int i, EnchantedItemInUse enchantedItemInUse, Entity entity) {
      if (entity.invulnerableTime < 60 && entity.isAlive()) {
         if (entity instanceof LivingEntity target) {
            ItemStack weapon = enchantedItemInUse.itemStack();
            LivingEntity owner = enchantedItemInUse.owner();
            if (owner instanceof Player player) {
               if (player.getCooldowns().isOnCooldown(weapon.getItem())) {
                  return;
               }

               player.getCooldowns().addCooldown(weapon.getItem(), this.cooldown);
               if (EnergyHelper.drainEnergy(target, owner, this.percentage.calculate(i), true, EnergyHelper.DrainType.EP, EnergyHelper.GainType.NORMAL)) {
                  player.playNotifySound((SoundEvent)TensuraSoundEvents.ENERGY_DRAIN.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
               }
            } else if (EnergyHelper.drainEnergy(target, owner, this.percentage.calculate(i), true, EnergyHelper.DrainType.EP, EnergyHelper.GainType.NORMAL)) {
               owner.playSound((SoundEvent)TensuraSoundEvents.ENERGY_DRAIN.get());
            }
         }
      }
   }

   @NotNull
   public MapCodec<EnergyStealEntity> codec() {
      return (MapCodec<EnergyStealEntity>)TensuraEnchantmentEffectComponents.ENERGY_STEAL.get();
   }
}
