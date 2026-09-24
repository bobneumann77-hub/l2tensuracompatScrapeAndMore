package io.github.manasmods.tensura.enchantment.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.config.ability.AbilityConfig;
import io.github.manasmods.tensura.enchantment.template.EnchantmentPostDamageEffect;
import io.github.manasmods.tensura.registry.item.misc.TensuraEnchantmentEffectComponents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.effect.IEffect;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record SeveranceDamageEntity(LevelBasedValue amount, float severanceCap, boolean multiplyOriginalDamage, boolean ignoreDefence)
   implements EnchantmentPostDamageEffect {
   public static final MapCodec<SeveranceDamageEntity> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            LevelBasedValue.CODEC.fieldOf("amount").forGetter(SeveranceDamageEntity::amount),
            Codec.FLOAT.fieldOf("severanceCap").forGetter(SeveranceDamageEntity::severanceCap),
            Codec.BOOL.fieldOf("multiplyOriginalDamage").forGetter(SeveranceDamageEntity::multiplyOriginalDamage),
            Codec.BOOL.fieldOf("ignoreDefence").forGetter(SeveranceDamageEntity::ignoreDefence)
         )
         .apply(instance, SeveranceDamageEntity::new)
   );

   @Override
   public void apply(ServerLevel serverLevel, int i, EnchantedItemInUse enchantedItemInUse, Entity entity, Vec3 vec3, float originalDamage) {
      if (this.multiplyOriginalDamage) {
         this.postDamage(i, entity, originalDamage);
      }
   }

   public void apply(ServerLevel serverLevel, int i, EnchantedItemInUse enchantedItemInUse, Entity entity, Vec3 vec3) {
      if (!this.multiplyOriginalDamage) {
         this.postDamage(i, entity, 1.0F);
      }
   }

   public void postDamage(int i, Entity entity, float originalDamage) {
      if (entity instanceof LivingEntity target && !target.hasInfiniteMaterials()) {
         if (SkillUtils.shouldCancelSeverance(target, null)) {
            return;
         }

         IEffect effect = TensuraStorages.getEffectFrom(target);
         float damage = Math.min(this.amount.calculate(i), this.severanceCap) * originalDamage;
         if (!this.ignoreDefence) {
            damage = Math.min(damage, Math.max(0.5F, target.getMaxHealth() - target.getHealth() - effect.getSeveranceAmount()));
         }

         int removeSecond = ((AbilityConfig)ConfigRegistry.getConfig(AbilityConfig.class)).Misc.severanceRemoveSec;
         if (removeSecond != 0) {
            effect.setSeveranceRemoveTime(removeSecond);
            effect.increaseSeveranceAmount(damage);
            effect.markDirty();
         }
      }
   }

   @NotNull
   public MapCodec<SeveranceDamageEntity> codec() {
      return (MapCodec<SeveranceDamageEntity>)TensuraEnchantmentEffectComponents.SEVERANCE.get();
   }
}
