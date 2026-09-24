package io.github.manasmods.tensura.registry.item.misc;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BannerPattern;

public class TensuraBannerPatterns {
   public static final ResourceKey<BannerPattern> DWARGON = create("dwargon");

   public static void bootstrap(BootstrapContext<BannerPattern> context) {
      register(context, DWARGON);
   }

   private static void register(BootstrapContext<BannerPattern> context, ResourceKey<BannerPattern> key) {
      context.register(key, new BannerPattern(key.location(), "block.tensura.banner." + key.location().toShortLanguageKey()));
   }

   private static ResourceKey<BannerPattern> create(String name) {
      return ResourceKey.create(Registries.BANNER_PATTERN, ResourceLocation.fromNamespaceAndPath("tensura", name));
   }
}
