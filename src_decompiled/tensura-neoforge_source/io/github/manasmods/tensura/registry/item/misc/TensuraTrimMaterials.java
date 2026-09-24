package io.github.manasmods.tensura.registry.item.misc;

import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import java.util.List;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.armortrim.TrimMaterial;

public class TensuraTrimMaterials {
   public static final ResourceKey<TrimMaterial> SILVER = create("silver");
   public static final ResourceKey<TrimMaterial> LOW_MAGISTEEL = create("low_magisteel");
   public static final ResourceKey<TrimMaterial> HIGH_MAGISTEEL = create("high_magisteel");
   public static final ResourceKey<TrimMaterial> MITHRIL = create("mithril");
   public static final ResourceKey<TrimMaterial> ORICHALCUM = create("orichalcum");
   public static final ResourceKey<TrimMaterial> PURE_MAGISTEEL = create("pure_magisteel");
   public static final ResourceKey<TrimMaterial> ADAMANTITE = create("adamantite");
   public static final ResourceKey<TrimMaterial> HIHIIROKANE = create("hihiirokane");
   public static final List<ResourceKey<TrimMaterial>> TRIM_MATERIALS = List.of(
      SILVER, LOW_MAGISTEEL, HIGH_MAGISTEEL, MITHRIL, ORICHALCUM, PURE_MAGISTEEL, ADAMANTITE, HIHIIROKANE
   );

   public static void bootstrap(BootstrapContext<TrimMaterial> context) {
      register(context, SILVER, ((Item)TensuraMaterialItems.SILVER_INGOT.get()).arch$holder(), 16777215, 0.2F);
      register(context, LOW_MAGISTEEL, ((Item)TensuraMaterialItems.LOW_MAGISTEEL_INGOT.get()).arch$holder(), 11184810, 0.2F);
      register(context, HIGH_MAGISTEEL, ((Item)TensuraMaterialItems.HIGH_MAGISTEEL_INGOT.get()).arch$holder(), 5636095, 0.8F);
      register(context, MITHRIL, ((Item)TensuraMaterialItems.MITHRIL_INGOT.get()).arch$holder(), 16733695, 1.0F);
      register(context, ORICHALCUM, ((Item)TensuraMaterialItems.ORICHALCUM_INGOT.get()).arch$holder(), 16766720, 0.6F);
      register(context, PURE_MAGISTEEL, ((Item)TensuraMaterialItems.PURE_MAGISTEEL_INGOT.get()).arch$holder(), 5636095, 0.8F);
      register(context, ADAMANTITE, ((Item)TensuraMaterialItems.ADAMANTITE_INGOT.get()).arch$holder(), 5635925, 0.7F);
      register(context, HIHIIROKANE, ((Item)TensuraMaterialItems.HIHIIROKANE_INGOT.get()).arch$holder(), 16733525, 0.8F);
   }

   private static void register(BootstrapContext<TrimMaterial> context, ResourceKey<TrimMaterial> key, Holder<Item> trimItem, int color, float itemModelIndex) {
      TrimMaterial material = new TrimMaterial(
         key.location().getPath(),
         trimItem,
         itemModelIndex,
         Map.of(),
         Component.translatable(Util.makeDescriptionId("trim_material", key.location())).withStyle(Style.EMPTY.withColor(color))
      );
      context.register(key, material);
   }

   private static ResourceKey<TrimMaterial> create(String name) {
      return ResourceKey.create(Registries.TRIM_MATERIAL, ResourceLocation.fromNamespaceAndPath("tensura", name));
   }
}
