package io.github.manasmods.tensura.recipe.misc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.function.Supplier;
import lombok.Generated;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class RecipeIngredient {
   public static final Codec<RecipeIngredient> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(RecipeIngredient::getIngredient),
            Codec.INT.fieldOf("quantity").forGetter(RecipeIngredient::getQuantity)
         )
         .apply(instance, RecipeIngredient::new)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, RecipeIngredient> STREAM_CODEC = StreamCodec.composite(
      Ingredient.CONTENTS_STREAM_CODEC, RecipeIngredient::getIngredient, ByteBufCodecs.VAR_INT, RecipeIngredient::getQuantity, RecipeIngredient::new
   );
   public static final RecipeIngredient EMPTY = new RecipeIngredient(Ingredient.EMPTY, 1);
   private final Ingredient ingredient;
   private final Integer quantity;
   private final TagKey<Item> tagKey;

   private RecipeIngredient(Ingredient ingredient, int quantity) {
      this(ingredient, quantity, null);
   }

   private RecipeIngredient(Ingredient ingredient, int quantity, TagKey<Item> tagKey) {
      this.ingredient = ingredient;
      this.quantity = quantity;
      this.tagKey = tagKey;
   }

   public static RecipeIngredient create(ItemLike item) {
      return create(item, 1);
   }

   public static RecipeIngredient create(ItemLike item, int quantity) {
      return new RecipeIngredient(Ingredient.of(new ItemLike[]{item}), quantity);
   }

   public static RecipeIngredient create(TagKey<Item> item) {
      return create(item, 1);
   }

   public static RecipeIngredient create(TagKey<Item> item, int quantity) {
      return new RecipeIngredient(Ingredient.of(item), quantity, item);
   }

   public static RecipeIngredient create(ItemStack item) {
      return create(item, 1);
   }

   public static RecipeIngredient create(ItemStack item, int quantity) {
      return new RecipeIngredient(Ingredient.of(new ItemStack[]{item}), quantity);
   }

   public static RecipeIngredient create(Supplier<ItemStack> item) {
      return create(item, 1);
   }

   public static RecipeIngredient create(Supplier<ItemStack> item, int quantity) {
      return new RecipeIngredient(Ingredient.of(new ItemStack[]{item.get()}), quantity);
   }

   public static RecipeIngredient create(Ingredient ingredient) {
      return create(ingredient, 1);
   }

   public static RecipeIngredient create(Ingredient ingredient, int quantity) {
      return new RecipeIngredient(ingredient, quantity);
   }

   public ItemStack getFirstItem() {
      return this.ingredient.getItems()[0];
   }

   public boolean test(ItemStack other) {
      return this.ingredient.test(other) && this.ingredient.getItems()[0].getCount() <= other.getCount();
   }

   public boolean isEmpty() {
      return this == EMPTY || this.ingredient == Ingredient.EMPTY || this.getFirstItem().isEmpty();
   }

   public String getId() {
      return this.tagKey != null ? this.tagKey.location().getPath() : BuiltInRegistries.ITEM.getKey(this.getFirstItem().getItem()).getPath();
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof RecipeIngredient that)) {
         return false;
      } else {
         boolean sameIngredient = this.getIngredient().equals(that.getIngredient());
         boolean sameQuantity = this.getQuantity().equals(that.getQuantity());
         boolean sameTagKey = Objects.equals(this.getTagKey(), that.getTagKey());
         return sameIngredient && sameQuantity && sameTagKey;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.getIngredient(), this.getQuantity(), this.getTagKey());
   }

   @Generated
   public Ingredient getIngredient() {
      return this.ingredient;
   }

   @Generated
   public Integer getQuantity() {
      return this.quantity;
   }

   @Generated
   public TagKey<Item> getTagKey() {
      return this.tagKey;
   }
}
