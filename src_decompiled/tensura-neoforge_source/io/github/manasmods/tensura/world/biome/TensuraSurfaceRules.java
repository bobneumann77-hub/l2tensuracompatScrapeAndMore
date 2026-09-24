package io.github.manasmods.tensura.world.biome;

import io.github.manasmods.tensura.registry.world.TensuraBiomes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.SurfaceRules.RuleSource;

public class TensuraSurfaceRules {
   private static final RuleSource MUD = makeStateRule(Blocks.MUD);
   private static final RuleSource SAND = makeStateRule(Blocks.SAND);
   private static final RuleSource SANDSTONE = makeStateRule(Blocks.SANDSTONE);

   public static RuleSource makeRules() {
      return SurfaceRules.sequence(
         new RuleSource[]{
            SurfaceRules.ifTrue(
               SurfaceRules.isBiome(new ResourceKey[]{TensuraBiomes.MIASMIC_PLAINS}),
               SurfaceRules.sequence(
                  new RuleSource[]{
                     SurfaceRules.ifTrue(
                        SurfaceRules.UNDER_FLOOR,
                        SurfaceRules.ifTrue(
                           SurfaceRules.yStartCheck(VerticalAnchor.absolute(50), 0),
                           SurfaceRules.sequence(
                              new RuleSource[]{
                                 SurfaceRules.ifTrue(SurfaceRules.not(SurfaceRules.waterStartCheck(0, 0)), MUD),
                                 SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PATCH, -1.0, -0.2), MUD)
                              }
                           )
                        )
                     ),
                     SurfaceRules.ifTrue(
                        SurfaceRules.ON_FLOOR,
                        SurfaceRules.ifTrue(
                           SurfaceRules.yStartCheck(VerticalAnchor.absolute(50), 0),
                           SurfaceRules.sequence(
                              new RuleSource[]{
                                 SurfaceRules.ifTrue(SurfaceRules.not(SurfaceRules.waterStartCheck(0, 0)), MUD),
                                 SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PATCH, -1.0, -0.2), MUD)
                              }
                           )
                        )
                     )
                  }
               )
            ),
            SurfaceRules.ifTrue(
               SurfaceRules.isBiome(new ResourceKey[]{TensuraBiomes.DESERT_OF_DEATH}),
               SurfaceRules.sequence(
                  new RuleSource[]{
                     SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, SurfaceRules.ifTrue(SurfaceRules.yStartCheck(VerticalAnchor.absolute(50), 0), SAND)),
                     SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.ifTrue(SurfaceRules.yStartCheck(VerticalAnchor.absolute(50), 0), SAND)),
                     SurfaceRules.ifTrue(
                        SurfaceRules.DEEP_UNDER_FLOOR, SurfaceRules.ifTrue(SurfaceRules.yStartCheck(VerticalAnchor.absolute(50), 0), SANDSTONE)
                     )
                  }
               )
            ),
            SurfaceRules.ifTrue(
               SurfaceRules.isBiome(new ResourceKey[]{TensuraBiomes.BARREN_LAND}),
               SurfaceRules.sequence(
                  new RuleSource[]{
                     SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, SurfaceRules.ifTrue(SurfaceRules.yStartCheck(VerticalAnchor.absolute(50), 0), SAND)),
                     SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.ifTrue(SurfaceRules.yStartCheck(VerticalAnchor.absolute(50), 0), SAND)),
                     SurfaceRules.ifTrue(
                        SurfaceRules.DEEP_UNDER_FLOOR, SurfaceRules.ifTrue(SurfaceRules.yStartCheck(VerticalAnchor.absolute(50), 0), SANDSTONE)
                     )
                  }
               )
            )
         }
      );
   }

   private static RuleSource makeStateRule(Block block) {
      return SurfaceRules.state(block.defaultBlockState());
   }
}
