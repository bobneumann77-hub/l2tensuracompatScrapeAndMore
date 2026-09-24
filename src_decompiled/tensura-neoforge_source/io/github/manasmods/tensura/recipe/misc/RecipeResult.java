package io.github.manasmods.tensura.recipe.misc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.architectury.registry.registries.RegistrySupplier;
import java.util.Objects;
import java.util.function.Supplier;
import lombok.Generated;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

public class RecipeResult {
   public static final Codec<RecipeResult> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            Codec.INT.fieldOf("minAmount").forGetter(RecipeResult::getMin),
            Codec.INT.fieldOf("maxAmount").forGetter(RecipeResult::getMax),
            Codec.FLOAT.fieldOf("chance").forGetter(RecipeResult::getChance),
            ItemStack.CODEC.fieldOf("item").forGetter(RecipeResult::getItem)
         )
         .apply(instance, RecipeResult::new)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, RecipeResult> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.VAR_INT,
      RecipeResult::getMin,
      ByteBufCodecs.VAR_INT,
      RecipeResult::getMax,
      ByteBufCodecs.FLOAT,
      RecipeResult::getChance,
      ItemStack.STREAM_CODEC,
      RecipeResult::getItem,
      RecipeResult::new
   );
   private final int min;
   private final int max;
   private final float chance;
   private final ItemStack item;

   private RecipeResult(int min, int max, float chance, ItemLike item) {
      this(min, max, chance, item.asItem().getDefaultInstance());
   }

   private RecipeResult(int min, int max, float chance, Supplier<ItemStack> item) {
      this(min, max, chance, item.get());
   }

   private RecipeResult(int min, int max, float chance, ItemStack item) {
      this.min = min;
      this.max = max;
      this.chance = chance;
      this.item = item;
   }

   public static RecipeResult fixed(ItemLike item) {
      return fixed(item, 1, 1.0F);
   }

   public static RecipeResult fixed(ItemLike item, int amount) {
      return fixed(item, amount, 1.0F);
   }

   public static RecipeResult fixed(ItemLike item, float chance) {
      return fixed(item, 1, chance);
   }

   public static RecipeResult fixed(ItemLike item, int amount, float chance) {
      return new RecipeResult(amount, amount, chance, item);
   }

   public static RecipeResult fixed(ItemStack item) {
      return fixed(item, 1, 1.0F);
   }

   public static RecipeResult fixed(ItemStack item, int amount) {
      return fixed(item, amount, 1.0F);
   }

   public static RecipeResult fixed(ItemStack item, float chance) {
      return fixed(item, 1, chance);
   }

   public static RecipeResult fixed(ItemStack item, int amount, float chance) {
      return new RecipeResult(amount, amount, chance, item);
   }

   public static RecipeResult fixed(Supplier<ItemStack> item) {
      return fixed(item, 1, 1.0F);
   }

   public static RecipeResult fixed(Supplier<ItemStack> item, int amount) {
      return fixed(item, amount, 1.0F);
   }

   public static RecipeResult fixed(Supplier<ItemStack> item, float chance) {
      return fixed(item, 1, chance);
   }

   public static RecipeResult fixed(Supplier<ItemStack> item, int amount, float chance) {
      return new RecipeResult(amount, amount, chance, item);
   }

   public static RecipeResult fixed(RegistrySupplier<Item> item) {
      return fixed((ItemLike)item.get());
   }

   public static RecipeResult fixed(RegistrySupplier<Item> item, int amount) {
      return fixed((ItemLike)item.get(), amount);
   }

   public static RecipeResult fixed(RegistrySupplier<Item> item, float chance) {
      return fixed((ItemLike)item.get(), chance);
   }

   public static RecipeResult fixed(RegistrySupplier<Item> item, int amount, float chance) {
      return fixed((ItemLike)item.get(), amount, chance);
   }

   public static RecipeResult inclusiveRange(ItemLike item, int min, int max) {
      return inclusiveRange(item, min, max, 1.0F);
   }

   public static RecipeResult inclusiveRange(ItemLike item, int min, int max, float chance) {
      return new RecipeResult(min, max, chance, item);
   }

   public static RecipeResult inclusiveRange(ItemStack item, int min, int max) {
      return inclusiveRange(item, min, max, 1.0F);
   }

   public static RecipeResult inclusiveRange(ItemStack item, int min, int max, float chance) {
      return new RecipeResult(min, max, chance, item);
   }

   public static RecipeResult inclusiveRange(Supplier<ItemStack> item, int min, int max) {
      return inclusiveRange(item, min, max, 1.0F);
   }

   public static RecipeResult inclusiveRange(Supplier<ItemStack> item, int min, int max, float chance) {
      return new RecipeResult(min, max, chance, item);
   }

   public static RecipeResult inclusiveRange(RegistrySupplier<Item> item, int min, int max) {
      return inclusiveRange((ItemLike)item.get(), min, max);
   }

   public static RecipeResult inclusiveRange(RegistrySupplier<Item> item, int min, int max, float chance) {
      return inclusiveRange((ItemLike)item.get(), min, max, chance);
   }

   public int getAmount(@Nullable RandomSource random) {
      int min = this.getMin();
      if (random == null) {
         return min;
      } else {
         int max = this.getMax();
         int amount = min == max ? min : random.nextIntBetweenInclusive(min, max);
         float chance = this.getChance();
         if (chance == 1.0F) {
            return amount;
         } else {
            return random.nextFloat() <= chance ? amount : 0;
         }
      }
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof RecipeResult that)) {
         return false;
      } else {
         boolean sameItem = ItemStack.isSameItemSameComponents(this.getItem(), that.getItem());
         boolean sameQuantity = this.getMin() == that.getMin() && this.getMax() == that.getMax();
         boolean sameChance = this.getChance() == that.getChance();
         return sameItem && sameQuantity && sameChance;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(ItemStack.hashItemAndComponents(this.getItem()), this.getMin(), this.getMax(), this.getChance());
   }

   @Generated
   public int getMin() {
      return this.min;
   }

   @Generated
   public int getMax() {
      return this.max;
   }

   @Generated
   public float getChance() {
      return this.chance;
   }

   @Generated
   public ItemStack getItem() {
      return this.item;
   }
}
