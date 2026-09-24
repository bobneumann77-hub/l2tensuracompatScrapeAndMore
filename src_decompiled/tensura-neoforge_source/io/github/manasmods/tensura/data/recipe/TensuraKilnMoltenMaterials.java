package io.github.manasmods.tensura.data.recipe;

import io.github.manasmods.tensura.registry.data.TensuraCustomData;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class TensuraKilnMoltenMaterials {
   public static final ResourceLocation MOLTEN_COPPER = ResourceLocation.fromNamespaceAndPath("tensura", "copper");
   public static final ResourceLocation MOLTEN_GOLD = ResourceLocation.fromNamespaceAndPath("tensura", "gold");
   public static final ResourceLocation MOLTEN_IRON = ResourceLocation.fromNamespaceAndPath("tensura", "iron");
   public static final ResourceLocation MOLTEN_SILVER = ResourceLocation.fromNamespaceAndPath("tensura", "silver");
   public static final ResourceLocation MOLTEN_MAGISTEEL = ResourceLocation.fromNamespaceAndPath("tensura", "magisteel");
   public static final ResourceLocation MOLTEN_NETHERITE = ResourceLocation.fromNamespaceAndPath("tensura", "netherite");

   public static void bootstrap(BootstrapContext<KilnMoltenMaterial> context) {
      register(context, KilnMoltenMaterial.getDefault(MOLTEN_COPPER, false, 229, 124, 86, 255));
      register(context, KilnMoltenMaterial.getDefault(MOLTEN_GOLD, false, 227, 167, 37, 255));
      register(context, KilnMoltenMaterial.getDefault(MOLTEN_IRON, false, 139, 128, 106, 255));
      register(context, KilnMoltenMaterial.getDefault(MOLTEN_SILVER, false, 206, 217, 217, 255));
      register(context, KilnMoltenMaterial.getDefault(MOLTEN_MAGISTEEL, true, 0, 233, 255, 255));
      register(context, KilnMoltenMaterial.getDefault(MOLTEN_NETHERITE, true, 57, 43, 43, 255));
   }

   public static void register(BootstrapContext<KilnMoltenMaterial> context, KilnMoltenMaterial data) {
      ResourceKey<KilnMoltenMaterial> key = ResourceKey.create(TensuraCustomData.KILN_MOLTEN, data.type());
      context.register(key, data);
   }
}
