package io.github.manasmods.tensura.enchantment.effect;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.enchantment.template.EnchantmentPostDamageEffect;
import io.github.manasmods.tensura.entity.magic.barrier.BarrierEntity;
import io.github.manasmods.tensura.entity.magic.barrier.BarrierPart;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.item.misc.TensuraEnchantmentEffectComponents;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record BarrierPiercingEntity(LevelBasedValue percentage) implements EnchantmentPostDamageEffect {
   public static final MapCodec<BarrierPiercingEntity> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(LevelBasedValue.CODEC.fieldOf("percentage").forGetter(BarrierPiercingEntity::percentage))
         .apply(instance, BarrierPiercingEntity::new)
   );
   private static final ResourceLocation PIERCING = ResourceLocation.fromNamespaceAndPath("tensura", "barrier_piercing");

   @Override
   public void apply(ServerLevel serverLevel, int i, EnchantedItemInUse enchantedItemInUse, Entity entity, Vec3 vec3, float originalDamage) {
      this.applyBarrierPiercing(i, enchantedItemInUse, entity);
   }

   public void apply(ServerLevel serverLevel, int i, EnchantedItemInUse enchantedItemInUse, Entity entity, Vec3 vec3) {
      this.applyBarrierPiercing(i, enchantedItemInUse, entity);
   }

   public void applyBarrierPiercing(int i, EnchantedItemInUse enchantedItemInUse, Entity entity) {
      if (entity instanceof BarrierPart part) {
         BarrierEntity barrier = part.getBarrier();
         barrier.hurt(entity.damageSources().genericKill(), barrier.getHealth() * this.getPiercingAmount(i, enchantedItemInUse));
         entity.level().playSound(null, entity.blockPosition(), (SoundEvent)TensuraSoundEvents.BARRIER_BREAK.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 1.0F);
      } else if (entity instanceof LivingEntity target) {
         if (!target.hasInfiniteMaterials() && !target.isSpectator()) {
            AttributeInstance instance = target.getAttribute(TensuraAttributes.MULTILAYER_BARRIER);
            if (instance != null && !(instance.getValue() <= 0.0)) {
               double amount = this.getPiercingAmount(i, enchantedItemInUse) * -1.0F;
               AttributeModifier modifier = instance.getModifier(PIERCING);
               if (modifier == null) {
                  instance.addOrReplacePermanentModifier(new AttributeModifier(PIERCING, amount, Operation.ADD_MULTIPLIED_TOTAL));
               } else {
                  amount += modifier.amount();
                  instance.addOrReplacePermanentModifier(new AttributeModifier(PIERCING, amount, Operation.ADD_MULTIPLIED_TOTAL));
               }

               if (instance.getValue() <= 0.0) {
                  instance.removeModifiers();
               }

               entity.level()
                  .playSound(null, entity.blockPosition(), (SoundEvent)TensuraSoundEvents.BARRIER_BREAK.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 1.0F);
            }
         }
      }
   }

   private float getPiercingAmount(int i, EnchantedItemInUse enchantedItemInUse) {
      float amount = this.percentage.calculate(i);
      float strength = enchantedItemInUse.owner() instanceof Player player ? player.getAttackStrengthScale(0.5F) : 1.0F;
      return amount * (0.2F + strength * strength * 0.8F);
   }

   @NotNull
   public MapCodec<BarrierPiercingEntity> codec() {
      return (MapCodec<BarrierPiercingEntity>)TensuraEnchantmentEffectComponents.BARRIER_PIERCING.get();
   }
}
