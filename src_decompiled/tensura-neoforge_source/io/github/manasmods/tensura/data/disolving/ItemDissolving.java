package io.github.manasmods.tensura.data.disolving;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public record ItemDissolving(ResourceLocation item, double aura, double magicule, double health, double spiritualHealth) {
   public static final Codec<ItemDissolving> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("item").forGetter(ItemDissolving::item),
            Codec.DOUBLE.optionalFieldOf("aura", 0.0).forGetter(ItemDissolving::aura),
            Codec.DOUBLE.optionalFieldOf("magicule", 0.0).forGetter(ItemDissolving::magicule),
            Codec.DOUBLE.optionalFieldOf("heath", 0.0).forGetter(ItemDissolving::health),
            Codec.DOUBLE.optionalFieldOf("spiritualHeath", 0.0).forGetter(ItemDissolving::spiritualHealth)
         )
         .apply(instance, ItemDissolving::new)
   );

   public static ItemDissolving getDefault(ResourceLocation item, double magicule) {
      return new ItemDissolving(item, 0.0, magicule, 0.0, 0.0);
   }

   public static ItemDissolving getDefault(ResourceLocation item, double magicule, double health) {
      return new ItemDissolving(item, 0.0, magicule, health, 0.0);
   }
}
