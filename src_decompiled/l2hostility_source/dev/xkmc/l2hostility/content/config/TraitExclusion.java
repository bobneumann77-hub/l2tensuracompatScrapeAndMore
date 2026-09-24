package dev.xkmc.l2hostility.content.config;

import dev.xkmc.l2hostility.content.traits.base.MobTrait;
import java.util.LinkedHashMap;
import net.minecraft.core.Holder;

public record TraitExclusion(LinkedHashMap<Holder<MobTrait>, Double> excluded) {
   public static final TraitExclusion DEFAULT = new TraitExclusion(new LinkedHashMap<>());

   public static TraitExclusion.Builder builder() {
      return new TraitExclusion.Builder();
   }

   public static class Builder {
      private final LinkedHashMap<Holder<MobTrait>, Double> excluded = new LinkedHashMap<>();

      public TraitExclusion.Builder of(Holder<MobTrait> trait, double v) {
         this.excluded.put(trait, v);
         return this;
      }

      public TraitExclusion build() {
         return new TraitExclusion(this.excluded);
      }
   }
}
