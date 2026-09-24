package dev.xkmc.l2hostility.content.config;

import java.util.Optional;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public record TraitConfig(int cost, int weight, int max_rank, int min_level) {
   public static final TraitConfig DEFAULT = new TraitConfig(10, 100, 1, 10);

   public TagKey<EntityType<?>> getBlacklistTag(ResourceLocation id) {
      return TagKey.create(Registries.ENTITY_TYPE, id.withSuffix("_blacklist"));
   }

   public TagKey<EntityType<?>> getWhitelistTag(ResourceLocation id) {
      return TagKey.create(Registries.ENTITY_TYPE, id.withSuffix("_whitelist"));
   }

   public boolean allows(ResourceLocation self, EntityType<?> type) {
      TagKey<EntityType<?>> blacklist = this.getBlacklistTag(self);
      TagKey<EntityType<?>> whitelist = this.getWhitelistTag(self);
      boolean def = true;
      Optional<Named<EntityType<?>>> bt = BuiltInRegistries.ENTITY_TYPE.getTag(blacklist);
      Optional<Named<EntityType<?>>> wt = BuiltInRegistries.ENTITY_TYPE.getTag(whitelist);
      if (bt.isPresent() && bt.get().size() > 0 && type.is(blacklist)) {
         return false;
      }

      if (wt.isPresent() && wt.get().size() > 0) {
         if (type.is(whitelist)) {
            return true;
         }

         def = false;
      }

      return def;
   }
}
