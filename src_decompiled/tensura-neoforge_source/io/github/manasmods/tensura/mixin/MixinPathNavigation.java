package io.github.manasmods.tensura.mixin;

import io.github.manasmods.tensura.data.chunk.BlockMagiculeModifier;
import io.github.manasmods.tensura.storage.AreaMagiculeHelper;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.chunk.ChunkStorage;
import io.github.manasmods.tensura.util.MagicEngineHelper;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PathNavigation.class)
public abstract class MixinPathNavigation {
   @Shadow
   @Nullable
   protected Path path;
   @Shadow
   @Final
   protected Level level;
   @Shadow
   @Final
   protected Mob mob;
   @Unique
   private static double tensura$threshold = Double.NaN;

   @Unique
   private static double tensura$threshold() {
      double t = tensura$threshold;
      if (Double.isNaN(t)) {
         t = ChunkStorage.CONFIG.minimalMagiculeSpawn;
         tensura$threshold = t;
      }

      return t;
   }

   @Inject(method = "trimPath()V", at = @At("TAIL"))
   private void trimPath(CallbackInfo ci) {
      if (this.path != null) {
         if (MagicEngineHelper.hasActive()) {
            double threshold = tensura$threshold();
            double mobMagicule = AreaMagiculeHelper.getMagicule(this.mob, true);
            if (!(mobMagicule < threshold)) {
               LivingEntity target = this.mob.getTarget();
               if (target == null
                  || target != this.mob.getLastHurtByMob() && target != this.mob.getLastHurtMob() && target != this.mob.getLastAttacker()
                  || !(AreaMagiculeHelper.getMagicule(target, true) < threshold)) {
                  if (AreaMagiculeHelper.getMagicule(this.level, this.path.getTarget(), true) < threshold) {
                     this.path.truncateNodes(0);
                  } else {
                     int lastChunkX = Integer.MIN_VALUE;
                     int lastChunkZ = Integer.MIN_VALUE;
                     double cachedBaseMagicule = 0.0;
                     List<BlockMagiculeModifier> cachedModifiers = null;

                     for (int i = 0; i < this.path.getNodeCount(); i++) {
                        Node node = this.path.getNode(i);
                        BlockPos nodePos = node.asBlockPos();
                        int cx = nodePos.getX() >> 4;
                        int cz = nodePos.getZ() >> 4;
                        if (cx != lastChunkX || cz != lastChunkZ) {
                           ChunkStorage storage = TensuraStorages.getChunkFrom(this.level.getChunkAt(nodePos));
                           cachedBaseMagicule = storage.getMagicule();
                           cachedModifiers = storage.getBlockModifiers();
                           lastChunkX = cx;
                           lastChunkZ = cz;
                        }

                        double areaMP = cachedBaseMagicule;
                        if (!cachedModifiers.isEmpty()) {
                           double ncx = nodePos.getX() + 0.5;
                           double ncy = nodePos.getY() + 0.5;
                           double ncz = nodePos.getZ() + 0.5;

                           for (BlockMagiculeModifier modifier : cachedModifiers) {
                              BlockPos mp = modifier.pos();
                              double r = modifier.effectDistance();
                              if (!(Math.abs(mp.getX() + 0.5 - ncx) > r) && !(Math.abs(mp.getY() + 0.5 - ncy) > r) && !(Math.abs(mp.getZ() + 0.5 - ncz) > r)) {
                                 areaMP = modifier.getMagicule(areaMP);
                              }
                           }
                        }

                        if (Math.max(areaMP, 0.0) < threshold) {
                           this.path.truncateNodes(i);
                           return;
                        }
                     }
                  }
               }
            }
         }
      }
   }
}
