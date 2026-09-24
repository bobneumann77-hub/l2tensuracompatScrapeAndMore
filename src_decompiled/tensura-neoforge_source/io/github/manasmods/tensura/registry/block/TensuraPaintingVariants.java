package io.github.manasmods.tensura.registry.block;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.PaintingVariant;

public class TensuraPaintingVariants {
   public static final ResourceKey<PaintingVariant> BLUMUND = create("blumund");
   public static final ResourceKey<PaintingVariant> FILTWOOD = create("filtwood");
   public static final ResourceKey<PaintingVariant> FULBROSIA = create("fulbrosia");
   public static final ResourceKey<PaintingVariant> INGRASSIA = create("ingrassia");
   public static final ResourceKey<PaintingVariant> LETTER_OF_CHALLENGE = create("letter_of_challenge");
   public static final ResourceKey<PaintingVariant> RAJA = create("raja");
   public static final ResourceKey<PaintingVariant> SCARLET_BOND = create("scarlet_bond");
   public static final ResourceKey<PaintingVariant> SUNFLOWER = create("sunflower");
   public static final ResourceKey<PaintingVariant> WORLD_MAP = create("world_map");
   public static final ResourceKey<PaintingVariant> TEMPEST = create("tempest");

   public static void bootstrap(BootstrapContext<PaintingVariant> context) {
      register(context, BLUMUND, 2, 1);
      register(context, FILTWOOD, 2, 1);
      register(context, FULBROSIA, 2, 1);
      register(context, INGRASSIA, 2, 1);
      register(context, LETTER_OF_CHALLENGE, 2, 1);
      register(context, RAJA, 1, 2);
      register(context, SCARLET_BOND, 2, 1);
      register(context, SUNFLOWER, 5, 3);
      register(context, WORLD_MAP, 4, 3);
      register(context, TEMPEST, 2, 1);
   }

   private static void register(BootstrapContext<PaintingVariant> context, ResourceKey<PaintingVariant> key, int xSize, int ySize) {
      context.register(key, new PaintingVariant(xSize, ySize, key.location()));
   }

   public static ResourceKey<PaintingVariant> create(String name) {
      return ResourceKey.create(Registries.PAINTING_VARIANT, ResourceLocation.fromNamespaceAndPath("tensura", name));
   }
}
