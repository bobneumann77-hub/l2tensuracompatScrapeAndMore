package io.github.manasmods.tensura.item;

import com.google.common.base.Suppliers;
import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import java.util.function.Supplier;
import lombok.Generated;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public enum TensuraToolTiers implements Tier {
   SILVER(BlockTags.INCORRECT_FOR_IRON_TOOL, 150, 10.0F, 2.0F, 20, () -> Ingredient.of(new ItemLike[]{(ItemLike)TensuraMaterialItems.SILVER_INGOT.get()})),
   LOW_MAGISTEEL(
      BlockTags.INCORRECT_FOR_DIAMOND_TOOL,
      1800,
      8.0F,
      8.0F,
      25,
      () -> Ingredient.of(
         new ItemLike[]{
            (ItemLike)TensuraMaterialItems.LOW_MAGISTEEL_INGOT.get(),
            (ItemLike)TensuraMaterialItems.HIGH_MAGISTEEL_INGOT.get(),
            (ItemLike)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get()
         }
      )
   ),
   HIGH_MAGISTEEL(
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      2500,
      12.0F,
      16.0F,
      30,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)TensuraMaterialItems.HIGH_MAGISTEEL_INGOT.get(), (ItemLike)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get()})
   ),
   MITHRIL(
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      2700,
      14.0F,
      22.0F,
      35,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)TensuraMaterialItems.MITHRIL_INGOT.get(), (ItemLike)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get()})
   ),
   ORICHALCUM(
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      2800,
      16.0F,
      26.0F,
      40,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)TensuraMaterialItems.ORICHALCUM_INGOT.get(), (ItemLike)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get()})
   ),
   PURE_MAGISTEEL(
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      3000,
      20.0F,
      30.0F,
      40,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get()})
   ),
   ADAMANTITE(
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      3200,
      24.0F,
      46.0F,
      45,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)TensuraMaterialItems.ADAMANTITE_INGOT.get()})
   ),
   HIHIIROKANE(
      BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
      3600,
      27.0F,
      76.0F,
      50,
      () -> Ingredient.of(new ItemLike[]{(ItemLike)TensuraMaterialItems.HIHIIROKANE_INGOT.get()})
   );

   private final TagKey<Block> incorrectBlocksForDrops;
   private final int uses;
   private final float speed;
   private final float damage;
   private final int enchantmentValue;
   private final Supplier<Ingredient> repairIngredient;

   TensuraToolTiers(TagKey<Block> incorrect, int uses, float speed, float damage, int enchantment, Supplier<Ingredient> repair) {
      this.incorrectBlocksForDrops = incorrect;
      this.uses = uses;
      this.speed = speed;
      this.damage = damage;
      this.enchantmentValue = enchantment;
      this.repairIngredient = Suppliers.memoize(repair::get);
   }

   public float getAttackDamageBonus() {
      return this.damage;
   }

   @NotNull
   public TagKey<Block> getIncorrectBlocksForDrops() {
      return this.incorrectBlocksForDrops;
   }

   @NotNull
   public Ingredient getRepairIngredient() {
      return this.repairIngredient.get();
   }

   @Generated
   public int getUses() {
      return this.uses;
   }

   @Generated
   public float getSpeed() {
      return this.speed;
   }

   @Generated
   public int getEnchantmentValue() {
      return this.enchantmentValue;
   }
}
