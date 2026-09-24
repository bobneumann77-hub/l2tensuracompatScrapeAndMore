package io.github.manasmods.tensura.data.slotting;

import io.github.manasmods.tensura.registry.data.TensuraCustomData;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class TensuraSlottingCombinations {
   public static void register(BootstrapContext<SlottingCombination> context, SlottingCombination data) {
      ResourceKey<SlottingCombination> key = ResourceKey.create(
         TensuraCustomData.SLOTTING, ResourceLocation.fromNamespaceAndPath("tensura", "combination_" + data.id())
      );
      context.register(key, data);
   }

   public static void bootstrap(BootstrapContext<SlottingCombination> context) {
      register(context, SlottingCombination.of(1, 1, 0, 0, 0, 0, 1.0, 0.0, 0.0, 0.0, 0.0, "tensura:stone_shot", 3.0F, 1.0F, 1.5F, 0.0F, 0, false, 5, 10));
      register(context, SlottingCombination.of(2, 0, 1, 0, 0, 0, 0.0, 1.0, 0.0, 0.0, 0.0, "tensura:fire_bolt", 2.0F, 1.0F, 0.0F, 0.0F, 100, false, 5, 10));
      register(
         context, SlottingCombination.of(3, 0, 0, 1, 0, 0, 0.0, 0.0, 1.0, 0.0, 0.0, "tensura:space_cut_projectile", 1.75F, 1.0F, 0.0F, 0.0F, 0, false, 5, 10)
      );
      register(context, SlottingCombination.of(4, 0, 0, 0, 1, 0, 0.0, 0.0, 0.0, 1.0, 0.0, "tensura:water_ball", 1.25F, 1.0F, 1.0F, 0.0F, -1, false, 5, 10));
      register(context, SlottingCombination.of(5, 0, 0, 0, 0, 1, 0.0, 0.0, 0.0, 0.0, 1.0, "tensura:wind_sphere", 1.0F, 1.0F, 3.0F, 0.0F, -1, true, 5, 10));
      doubleCombinations(context);
      tripleCombinations(context);
   }

   private static void doubleCombinations(BootstrapContext<SlottingCombination> context) {
      register(context, SlottingCombination.of(6, 2, 0, 0, 0, 0, 1.5, 0.0, 0.0, 0.0, 0.0, "tensura:boulder_shot", 1.0F, 2.0F, 1.0F, 0.0F, 0, false, 4, 8));
      register(context, SlottingCombination.of(7, 1, 1, 0, 0, 0, 0.25, 0.25, 0.0, 0.0, 0.0));
      register(context, SlottingCombination.of(8, 1, 0, 1, 0, 0, 0.25, 0.0, 0.25, 0.0, 0.0));
      register(
         context,
         SlottingCombination.of(
            9, 1, 0, 0, 1, 0, 0.25, 0.0, 0.0, 1.5, 0.0, "tensura:mud_shot", 1.5F, 1.5F, 1.5F, 0.0F, -1, false, "minecraft:slowness", 1, 200, 3, 6
         )
      );
      register(context, SlottingCombination.of(10, 1, 0, 0, 0, 1, 1.0, 0.0, 0.0, 0.0, 1.0));
      register(context, SlottingCombination.of(11, 0, 2, 0, 0, 0, 0.0, 1.5, 0.0, 0.0, 0.0, "tensura:fire_bolt", 2.0F, 2.0F, 0.0F, 1.0F, 300, false, 4, 8));
      register(
         context,
         SlottingCombination.of(12, 0, 1, 1, 0, 0, 0.0, 0.75, 0.75, 0.0, 0.0, "tensura:space_cut_projectile", 1.75F, 1.25F, 0.0F, 0.0F, 100, false, 4, 8)
      );
      register(context, SlottingCombination.of(13, 0, 1, 0, 1, 0, 0.0, 0.25, 0.0, 0.25, 0.0));
      register(context, SlottingCombination.of(14, 0, 1, 0, 0, 1, 0.0, 1.5, 0.0, 0.0, 0.25, "tensura:fire_ball", 2.0F, 1.5F, 0.0F, 0.0F, 300, true, 3, 6));
      register(
         context, SlottingCombination.of(15, 0, 0, 2, 0, 0, 0.0, 0.0, 1.5, 0.0, 0.0, "tensura:space_cut_projectile", 1.75F, 2.0F, 0.0F, 0.0F, 0, false, 4, 8)
      );
      register(
         context,
         SlottingCombination.of(
            16, 0, 0, 1, 1, 0, 0.0, 0.0, 0.75, 0.75, 0.0, "tensura:space_cut_projectile", 1.75F, 1.25F, 0.0F, 0.0F, 0, false, "minecraft:poison", 0, 100, 4, 8
         )
      );
      register(context, SlottingCombination.of(17, 0, 0, 1, 0, 1, 0.0, 0.0, 0.25, 0.0, 0.25));
      register(
         context,
         SlottingCombination.of(
            18, 0, 0, 0, 2, 0, 0.0, 0.0, 0.0, 1.5, 0.0, "tensura:poison_ball", 1.25F, 2.0F, 1.0F, 0.0F, -1, false, "tensura:fatal_poison", 0, 200, 2.0F, 4, 8
         )
      );
      register(
         context,
         SlottingCombination.of(
            19, 0, 0, 0, 1, 1, 0.0, 0.0, 0.0, 0.75, 0.75, "tensura:frost_ball", 1.5F, 1.5F, 1.0F, 0.0F, -1, false, "tensura:chill", 1, 140, 1.0F, 4, 8
         )
      );
      register(context, SlottingCombination.of(20, 0, 0, 0, 0, 2, 0.0, 0.0, 0.0, 0.0, 1.5, "tensura:wind_blade", 1.5F, 2.0F, 1.0F, 0.0F, -1, true, 4, 8));
   }

   private static void tripleCombinations(BootstrapContext<SlottingCombination> context) {
      register(
         context,
         SlottingCombination.of(
            21, 3, 0, 0, 0, 0, 3.0, 0.0, 0.0, 0.0, 0.0, "tensura:gravity_sphere", 0.8F, 3.0F, 0.0F, 0.0F, 0, true, "tensura:burden", 1, 200, 4.0F, 3, 7
         )
      );
      register(context, SlottingCombination.of(22, 2, 1, 0, 0, 0, 3.0, 2.0, 0.0, 0.0, 0.0, "tensura:magma_shot", 1.5F, 1.5F, 0.0F, 2.0F, 200, false, 2, 6));
      register(context, SlottingCombination.of(23, 2, 0, 1, 0, 0, 2.0, 0.0, 0.0, 0.0, 0.0));
      register(
         context,
         SlottingCombination.of(
            24, 2, 0, 0, 1, 0, 2.0, 0.0, 0.0, 2.0, 0.0, "tensura:bog_shot", 1.85F, 2.5F, 1.5F, 0.0F, -1, false, "minecraft:slowness", 2, 150, 3, 7
         )
      );
      register(context, SlottingCombination.of(25, 2, 0, 0, 0, 1, 2.0, 0.0, 0.0, 0.0, 1.0, "tensura:boulder_shot", 1.5F, 2.0F, 1.5F, 0.0F, 0, false, 3, 7));
      register(context, SlottingCombination.of(26, 1, 2, 0, 0, 0, 0.0, 2.0, 0.0, 0.0, 0.0));
      register(
         context, SlottingCombination.of(27, 1, 1, 1, 0, 0, 0.5, 1.0, 0.5, 0.0, 0.0, "tensura:space_cut_projectile", 1.75F, 2.0F, 0.0F, 0.0F, 100, true, 4, 8)
      );
      register(context, SlottingCombination.of(28, 1, 1, 0, 1, 0, 3.0, 0.0, 0.0, 0.0, 0.0, "tensura:obsidian_shot", 2.5F, 2.25F, 1.5F, 0.0F, 0, false, 3, 7));
      register(context, SlottingCombination.of(29, 1, 1, 0, 0, 1, 3.0, 0.0, 0.0, 0.0, 0.0, "tensura:obsidian_shot", 2.5F, 2.25F, 1.5F, 0.0F, 0, false, 3, 7));
      register(context, SlottingCombination.of(30, 1, 0, 2, 0, 0, 0.0, 0.0, 3.0, 0.0, 0.0));
      register(
         context,
         SlottingCombination.of(
            31, 1, 0, 1, 1, 0, 0.5, 0.0, 0.5, 1.0, 0.0, "tensura:space_cut_projectile", 1.75F, 2.0F, 0.0F, 0.0F, 0, true, "minecraft:poison", 0, 100, 4, 8
         )
      );
      register(
         context, SlottingCombination.of(32, 1, 0, 1, 0, 1, 0.5, 0.0, 0.5, 0.0, 1.0, "tensura:space_cut_projectile", 1.75F, 2.0F, 3.0F, 0.0F, 0, true, 4, 8)
      );
      register(
         context,
         SlottingCombination.of(
            33, 1, 0, 0, 2, 0, 1.0, 0.0, 0.0, 2.0, 0.0, "tensura:bog_shot", 1.85F, 2.5F, 1.5F, 0.0F, -1, false, "minecraft:slowness", 2, 200, 3, 7
         )
      );
      register(
         context,
         SlottingCombination.of(
            34, 1, 0, 0, 1, 1, 1.0, 0.0, 0.0, 0.5, 0.5, "tensura:bog_shot", 2.2F, 2.5F, 1.5F, 0.0F, -1, false, "minecraft:slowness", 2, 100, 4, 8
         )
      );
      register(context, SlottingCombination.of(35, 1, 0, 0, 0, 2, 1.0, 0.0, 0.0, 0.0, 2.0));
      register(context, SlottingCombination.of(36, 0, 3, 0, 0, 0, 0.0, 3.0, 0.0, 0.0, 0.0, "tensura:plasmas_ball", 1.0F, 3.0F, 0.0F, 3.0F, 300, true, 3, 7));
      register(
         context, SlottingCombination.of(37, 0, 2, 1, 0, 0, 0.0, 2.0, 1.0, 0.0, 0.0, "tensura:space_cut_projectile", 1.75F, 2.0F, 3.0F, 0.0F, 300, false, 3, 7)
      );
      register(context, SlottingCombination.of(38, 0, 2, 0, 1, 0, 0.0, 1.0, 0.0, 0.0, 0.0));
      register(context, SlottingCombination.of(39, 0, 2, 0, 0, 1, 0.0, 4.0, 0.0, 0.0, 0.0, "tensura:fire_bolt", 2.0F, 2.0F, 0.0F, 3.0F, 300, false, 3, 7));
      register(
         context, SlottingCombination.of(40, 0, 1, 2, 0, 0, 0.0, 1.0, 2.0, 0.0, 0.0, "tensura:space_cut_projectile", 1.75F, 2.0F, 3.0F, 0.0F, 300, false, 3, 7)
      );
      register(
         context, SlottingCombination.of(41, 0, 1, 1, 1, 0, 0.0, 0.5, 1.0, 0.5, 0.0, "tensura:space_cut_projectile", 1.75F, 1.0F, 0.0F, 0.0F, 0, false, 4, 8)
      );
      register(
         context, SlottingCombination.of(42, 0, 1, 1, 0, 1, 0.0, 2.0, 1.0, 0.0, 0.0, "tensura:invisible_fire_bolt", 2.0F, 2.0F, 0.0F, 1.0F, 300, false, 3, 7)
      );
      register(context, SlottingCombination.of(43, 0, 1, 0, 2, 0, 0.0, 2.0, 0.0, 3.0, 0.0, "tensura:steam_ball", 2.75F, 2.25F, 1.0F, 0.0F, 0, true, 2, 6));
      register(context, SlottingCombination.of(44, 0, 1, 0, 1, 1, 0.0, 1.5, 0.0, 0.0, 0.0));
      register(context, SlottingCombination.of(45, 0, 1, 0, 0, 2, 0.0, 0.0, 0.0, 0.0, 1.5));
      register(
         context, SlottingCombination.of(46, 0, 0, 3, 0, 0, 0.0, 0.0, 3.0, 0.0, 0.0, "tensura:dimension_cut_projectile", 2.0F, 3.0F, 0.0F, 0.0F, 0, true, 3, 7)
      );
      register(
         context,
         SlottingCombination.of(
            47, 0, 0, 2, 1, 0, 0.0, 0.0, 2.0, 1.0, 0.0, "tensura:space_cut_projectile", 1.75F, 2.0F, 0.0F, 0.0F, 0, false, "tensura:fatal_poison", 3, 7
         )
      );
      register(context, SlottingCombination.of(48, 0, 0, 2, 0, 1, 0.0, 0.0, 1.5, 0.0, 0.0));
      register(
         context,
         SlottingCombination.of(
            49, 0, 0, 1, 2, 0, 0.0, 0.0, 1.0, 2.0, 0.0, "tensura:space_cut_projectile", 1.75F, 2.0F, 0.0F, 0.0F, 0, false, "tensura:fatal_poison", 3, 7
         )
      );
      register(
         context,
         SlottingCombination.of(
            50, 0, 0, 1, 1, 1, 0.0, 0.0, 1.0, 1.0, 0.0, "tensura:space_cut_projectile", 1.75F, 2.0F, 3.0F, 0.0F, 0, false, "tensura:fatal_poison", 4, 8
         )
      );
      register(context, SlottingCombination.of(51, 0, 0, 1, 0, 2, 0.0, 0.0, 0.0, 0.0, 1.5));
      register(
         context,
         SlottingCombination.of(
            52, 0, 0, 0, 3, 0, 0.0, 0.0, 0.0, 3.0, 0.0, "tensura:acid_ball", 1.25F, 3.0F, 1.0F, 0.0F, -1, false, "tensura:corrosion", 0, 100, 4.0F, 3, 7
         )
      );
      register(
         context,
         SlottingCombination.of(
            53, 0, 0, 0, 2, 1, 0.0, 0.0, 0.0, 2.0, 1.0, "tensura:poison_cutter", 1.5F, 2.5F, 1.0F, 0.0F, -1, false, "tensura:fatal_poison", 0, 200, 1.0F, 3, 7
         )
      );
      register(
         context,
         SlottingCombination.of(
            54, 0, 0, 0, 1, 2, 0.0, 0.0, 0.0, 2.0, 2.0, "tensura:ice_lance", 2.0F, 2.5F, 1.0F, 0.0F, -1, false, "tensura:chill", 2, 200, 1.0F, 3, 7
         )
      );
      register(
         context,
         SlottingCombination.of(
            55, 0, 0, 0, 0, 3, 0.0, 0.0, 0.0, 0.0, 3.0, "tensura:lightning_sphere", 1.0F, 3.0F, 0.0F, 2.0F, 100, true, "tensura:paralysis", 1, 100, 4.0F, 3, 7
         )
      );
      register(
         context,
         SlottingCombination.of(
            69, 1, 1, 1, 1, 1, 1.0, 1.0, 1.0, 1.0, 1.0, "tensura:reflector_echo", 2.0F, 5.0F, 0.0F, 4.0F, 0, true, "tensura:anti_skill", 0, 200, 3.0F, 0, 0
         )
      );
   }
}
