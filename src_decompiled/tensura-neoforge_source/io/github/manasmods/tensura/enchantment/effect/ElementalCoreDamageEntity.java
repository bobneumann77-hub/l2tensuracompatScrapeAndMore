package io.github.manasmods.tensura.enchantment.effect;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.Magic;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.data.slotting.SlottingCombination;
import io.github.manasmods.tensura.effect.ability.MagicElementalEffect;
import io.github.manasmods.tensura.enchantment.SlottingHelper;
import io.github.manasmods.tensura.enchantment.template.EnchantmentPostDamageWithTypeEffect;
import io.github.manasmods.tensura.registry.data.TensuraCustomData;
import io.github.manasmods.tensura.registry.item.misc.TensuraEnchantmentEffectComponents;
import java.util.Arrays;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import org.jetbrains.annotations.NotNull;

public record ElementalCoreDamageEntity(LevelBasedValue amount, EnchantmentPostDamageWithTypeEffect.DamageCalculationType calculationType)
   implements EnchantmentPostDamageWithTypeEffect {
   public static final MapCodec<ElementalCoreDamageEntity> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
            LevelBasedValue.CODEC.fieldOf("amount").forGetter(ElementalCoreDamageEntity::amount),
            EnchantmentPostDamageWithTypeEffect.DamageCalculationType.CODEC.fieldOf("calculation_type").forGetter(ElementalCoreDamageEntity::calculationType)
         )
         .apply(instance, ElementalCoreDamageEntity::new)
   );

   @Override
   public Holder<DamageType> damageType() {
      return null;
   }

   @Override
   public void postDamage(int i, EnchantedItemInUse enchantedItemInUse, Entity target, float originalDamage) {
      if (target.invulnerableTime < 40) {
         ItemStack stack = enchantedItemInUse.itemStack();
         if (SlottingHelper.getContentSize(stack) > 0) {
            LivingEntity attacker = enchantedItemInUse.owner();
            float strength = attacker instanceof Player player ? player.getAttackStrengthScale(0.5F) : 1.0F;
            float damage = originalDamage * this.amount.calculate(i) * (0.2F + strength * strength * 0.8F);
            int[] cores = new int[5];

            for (ItemStack core : SlottingHelper.getContents(stack)) {
               Integer index = SlottingHelper.ELEMENT_INDEX_MAP.get(core.getItem());
               if (index != null) {
                  cores[index]++;
               }
            }

            Registry<SlottingCombination> registry = target.level().registryAccess().registryOrThrow(TensuraCustomData.SLOTTING);
            registry.stream().filter(combination -> Arrays.equals(combination.getCores(), cores)).findFirst().ifPresent(data -> {
               double[] multipliers = data.getElementMultiplier();

               for (int indexx = 0; indexx < multipliers.length; indexx++) {
                  double multiplier = multipliers[indexx];
                  if (!(multiplier <= 0.0)) {
                     Element element = Element.getCommonElemental().get(indexx);
                     if (element != null) {
                        this.applyElementalDamage(target, attacker, element, damage * (float)multiplier);
                     }
                  }
               }

               SlottingHelper.hurtAllCores(attacker, stack, data.durabilityCost(), enchantedItemInUse.inSlot());
            });
         }
      }
   }

   private void applyElementalDamage(Entity pTarget, LivingEntity pAttacker, Element element, float pAmount) {
      if (!(pAmount <= 0.0F)) {
         pTarget.invulnerableTime = 0;
         DamageSource magicDamage = TensuraDamageTypes.getEntityDamageSource(pTarget.level(), element.getDefaultDamage(), pAttacker)
            .tensura$setElement(element)
            .tensura$setMagicType(Magic.MagicType.SPIRITUAL)
            .tensura$setSlotting();
         if (TensuraDamageHelper.hurtSplitElemental(pTarget, magicDamage, 0.9F, pAmount) && pTarget instanceof LivingEntity target) {
            MagicElementalEffect.doVisualEffect(target, element);
         }
      }
   }

   @NotNull
   public MapCodec<ElementalCoreDamageEntity> codec() {
      return (MapCodec<ElementalCoreDamageEntity>)TensuraEnchantmentEffectComponents.ELEMENTAL_CORE_DAMAGE.get();
   }
}
