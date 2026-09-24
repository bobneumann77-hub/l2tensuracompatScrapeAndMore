package io.github.manasmods.tensura.client.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Axis;
import io.github.manasmods.tensura.block.MagicEngineBlock;
import io.github.manasmods.tensura.block.entity.MagicEngineBlockEntity;
import io.github.manasmods.tensura.registry.item.TensuraMobDropItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class MagicEngineBlockEntityRenderer implements BlockEntityRenderer<MagicEngineBlockEntity> {
   private final ItemRenderer itemRenderer;

   public MagicEngineBlockEntityRenderer(Context context) {
      this.itemRenderer = context.getItemRenderer();
   }

   public void render(
      MagicEngineBlockEntity entity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay
   ) {
      BlockState state = entity.getBlockState();
      if ((Boolean)state.getValue(MagicEngineBlock.ENABLED)) {
         pPoseStack.pushPose();
         Direction direction = (Direction)state.getValue(MagicEngineBlock.FACING);
         Vec3 offset = this.getRotationOffset(direction);
         pPoseStack.translate(offset.x(), offset.y(), offset.z());
         pPoseStack.scale(0.5F, 0.5F, 0.5F);
         pPoseStack.mulPose(direction.getRotation());
         pPoseStack.mulPose(Axis.YP.rotation((entity.spin++ + pPartialTick) / 20.0F - 45.0F));
         this.itemRenderer
            .renderStatic(
               ((Item)TensuraMobDropItems.HIGH_QUALITY_MAGIC_CRYSTAL.get()).getDefaultInstance(),
               ItemDisplayContext.HEAD,
               pPackedLight,
               OverlayTexture.NO_OVERLAY,
               pPoseStack,
               pBufferSource,
               entity.getLevel(),
               1
            );
         pPoseStack.popPose();
         if (Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes() && state.getBlock() instanceof MagicEngineBlock magicEngine) {
            renderRadius(pPoseStack, pBufferSource, (float)magicEngine.getReductionRange());
         }
      }
   }

   public int getViewDistance() {
      return Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes() ? 512 : 64;
   }

   public boolean shouldRenderOffScreen(MagicEngineBlockEntity entity) {
      return true;
   }

   private static void renderRadius(PoseStack poseStack, MultiBufferSource bufferSource, float radius) {
      poseStack.pushPose();
      poseStack.translate(0.5, 0.5, 0.5);
      VertexConsumer consumer = bufferSource.getBuffer(RenderType.lines());
      Pose pose = poseStack.last();
      float r = 0.2F;
      float g = 0.9F;
      float b = 1.0F;
      float a = 0.85F;
      float h = radius + 0.5F;
      float nxNyNz_x = -h;
      float nxNyNz_y = -h;
      float nxNyNz_z = -h;
      float pxNyNz_x = h;
      float pxNyNz_y = -h;
      float pxNyNz_z = -h;
      float nxPyNz_x = -h;
      float nxPyNz_y = h;
      float nxPyNz_z = -h;
      float pxPyNz_x = h;
      float pxPyNz_y = h;
      float pxPyNz_z = -h;
      float nxNyPz_x = -h;
      float nxNyPz_y = -h;
      float nxNyPz_z = h;
      float pxNyPz_x = h;
      float pxNyPz_y = -h;
      float pxNyPz_z = h;
      float nxPyPz_x = -h;
      float nxPyPz_y = h;
      float nxPyPz_z = h;
      float pxPyPz_x = h;
      float pxPyPz_y = h;
      float pxPyPz_z = h;
      line(consumer, pose, nxNyNz_x, nxNyNz_y, nxNyNz_z, pxNyNz_x, pxNyNz_y, pxNyNz_z, 1.0F, 0.0F, 0.0F, r, g, b, a);
      line(consumer, pose, pxNyNz_x, pxNyNz_y, pxNyNz_z, pxNyPz_x, pxNyPz_y, pxNyPz_z, 0.0F, 0.0F, 1.0F, r, g, b, a);
      line(consumer, pose, pxNyPz_x, pxNyPz_y, pxNyPz_z, nxNyPz_x, nxNyPz_y, nxNyPz_z, 1.0F, 0.0F, 0.0F, r, g, b, a);
      line(consumer, pose, nxNyPz_x, nxNyPz_y, nxNyPz_z, nxNyNz_x, nxNyNz_y, nxNyNz_z, 0.0F, 0.0F, 1.0F, r, g, b, a);
      line(consumer, pose, nxPyNz_x, nxPyNz_y, nxPyNz_z, pxPyNz_x, pxPyNz_y, pxPyNz_z, 1.0F, 0.0F, 0.0F, r, g, b, a);
      line(consumer, pose, pxPyNz_x, pxPyNz_y, pxPyNz_z, pxPyPz_x, pxPyPz_y, pxPyPz_z, 0.0F, 0.0F, 1.0F, r, g, b, a);
      line(consumer, pose, pxPyPz_x, pxPyPz_y, pxPyPz_z, nxPyPz_x, nxPyPz_y, nxPyPz_z, 1.0F, 0.0F, 0.0F, r, g, b, a);
      line(consumer, pose, nxPyPz_x, nxPyPz_y, nxPyPz_z, nxPyNz_x, nxPyNz_y, nxPyNz_z, 0.0F, 0.0F, 1.0F, r, g, b, a);
      line(consumer, pose, nxNyNz_x, nxNyNz_y, nxNyNz_z, nxPyNz_x, nxPyNz_y, nxPyNz_z, 0.0F, 1.0F, 0.0F, r, g, b, a);
      line(consumer, pose, pxNyNz_x, pxNyNz_y, pxNyNz_z, pxPyNz_x, pxPyNz_y, pxPyNz_z, 0.0F, 1.0F, 0.0F, r, g, b, a);
      line(consumer, pose, pxNyPz_x, pxNyPz_y, pxNyPz_z, pxPyPz_x, pxPyPz_y, pxPyPz_z, 0.0F, 1.0F, 0.0F, r, g, b, a);
      line(consumer, pose, nxNyPz_x, nxNyPz_y, nxNyPz_z, nxPyPz_x, nxPyPz_y, nxPyPz_z, 0.0F, 1.0F, 0.0F, r, g, b, a);
      poseStack.popPose();
   }

   private static void line(
      VertexConsumer consumer,
      Pose pose,
      float x1,
      float y1,
      float z1,
      float x2,
      float y2,
      float z2,
      float nx,
      float ny,
      float nz,
      float r,
      float g,
      float b,
      float a
   ) {
      consumer.addVertex(pose, x1, y1, z1).setColor(r, g, b, a).setNormal(pose, nx, ny, nz);
      consumer.addVertex(pose, x2, y2, z2).setColor(r, g, b, a).setNormal(pose, nx, ny, nz);
   }

   private Vec3 getRotationOffset(Direction direction) {
      return switch (direction) {
         case DOWN -> new Vec3(0.5, -0.3, 0.5);
         case UP -> new Vec3(0.5, 1.3, 0.5);
         case EAST -> new Vec3(1.3, 0.5, 0.5);
         case WEST -> new Vec3(-0.3, 0.5, 0.5);
         case SOUTH -> new Vec3(0.5, 0.5, 1.3);
         case NORTH -> new Vec3(0.5, 0.5, -0.3);
         default -> throw new MatchException(null, null);
      };
   }
}
