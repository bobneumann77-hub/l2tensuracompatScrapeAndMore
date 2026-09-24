package io.github.manasmods.tensura.util.client;

import io.github.manasmods.tensura.client.TensuraRenderTypes;
import java.util.Calendar;
import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ClientHelper {
   public static final boolean HALLOWEEN = Calendar.getInstance().get(2) + 1 == 10 && Calendar.getInstance().get(5) >= 30;
   public static final boolean XMAS = Calendar.getInstance().get(2) + 1 == 12 && Calendar.getInstance().get(5) >= 24 && Calendar.getInstance().get(5) <= 26;

   public static boolean isInFirstViewDistance(Entity target, double distance) {
      if (!Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
         return false;
      }

      Entity entity = Minecraft.getInstance().getCameraEntity();
      return entity == null ? false : entity.getEyePosition().distanceTo(target.position()) < distance;
   }

   public static RenderType getFirstViewRenderType(Entity target, double distance, ResourceLocation texture) {
      return isInFirstViewDistance(target, distance) ? RenderType.entityNoOutline(texture) : RenderType.entityTranslucent(texture);
   }

   public static RenderType getFirstViewRenderTypeBright(Entity target, double distance, ResourceLocation texture) {
      return isInFirstViewDistance(target, distance) ? RenderType.entityNoOutline(texture) : TensuraRenderTypes.getUnlitTranslucent(texture);
   }

   public static boolean isRiderFirstViewDistance(Entity target, double distance) {
      if (!Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
         return false;
      } else {
         Entity entity = Minecraft.getInstance().getCameraEntity();
         if (entity == null) {
            return false;
         } else if (target.getControllingPassenger() == null) {
            return false;
         } else {
            return !target.getControllingPassenger().equals(entity)
               ? false
               : entity.getEyePosition().distanceTo(target.getBoundingBox().getCenter()) < distance;
         }
      }
   }

   public static RenderType getRiderFirstViewRenderType(RenderType type, Entity target, double distance, ResourceLocation texture) {
      return isRiderFirstViewDistance(target, distance) ? RenderType.entityNoOutline(texture) : type;
   }

   public static void spawnMarkerParticle(Level pLevel, BlockState pState, BlockPos pos, Predicate<Item> predicate) {
      if (pLevel.isClientSide()) {
         Player player = Minecraft.getInstance().player;
         if (player != null) {
            if (predicate.test(player.getMainHandItem().getItem()) || predicate.test(player.getOffhandItem().getItem())) {
               pLevel.addParticle(
                  new BlockParticleOption(ParticleTypes.BLOCK_MARKER, pState), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0.0, 0.0, 0.0
               );
            }
         }
      }
   }

   public static void spawnMarkerParticle(Level pLevel, BlockState pState, BlockPos pos, Item item) {
      spawnMarkerParticle(pLevel, pState, pos, pItem -> pItem == item);
   }
}
