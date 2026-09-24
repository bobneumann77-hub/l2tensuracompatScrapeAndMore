package io.github.manasmods.tensura.client.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import io.github.manasmods.tensura.block.entity.ToolRackBlockEntity;
import io.github.manasmods.tensura.data.TensuraItemTags;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.phys.Vec3;

public class ToolRackBlockEntityRenderer implements BlockEntityRenderer<ToolRackBlockEntity> {
   private final ItemRenderer itemRenderer;

   public ToolRackBlockEntityRenderer(Context context) {
      this.itemRenderer = context.getItemRenderer();
   }

   public void render(ToolRackBlockEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
      Direction facing = (Direction)entity.getBlockState().getValue(HorizontalDirectionalBlock.FACING);
      float[] slotU = new float[]{0.75F, 0.5F, 0.25F};

      for (int slot = 0; slot < entity.getItems().size(); slot++) {
         ItemStack stack = (ItemStack)entity.getItems().get(slot);
         if (!stack.isEmpty()) {
            poseStack.pushPose();
            Pair<Vec3, Float> offset = this.getItemOffset(stack);
            double xOffset = ((Vec3)offset.getFirst()).x;
            double zOffset = ((Vec3)offset.getFirst()).z;
            float fronOffset = -0.075F;
            float sideOffset = slotU[Math.min(slot, 2)] - 0.5F;
            double x = xOffset;
            double z = zOffset;
            switch (facing) {
               case NORTH:
                  x = xOffset + sideOffset;
                  z = zOffset - fronOffset;
                  break;
               case SOUTH:
                  x = xOffset - sideOffset;
                  z = zOffset + fronOffset;
                  break;
               case WEST:
                  x = xOffset - fronOffset;
                  z = zOffset - sideOffset;
                  break;
               case EAST:
                  x = xOffset + fronOffset;
                  z = zOffset + sideOffset;
            }

            poseStack.translate(x, ((Vec3)offset.getFirst()).y, z);
            poseStack.mulPose(Axis.YN.rotationDegrees(facing.toYRot()));
            poseStack.mulPose(Axis.ZN.rotationDegrees((Float)offset.getSecond()));
            poseStack.mulPose(Axis.XN.rotationDegrees(-10.0F));
            float size = 0.75F;
            poseStack.scale(size, size, size);
            this.itemRenderer
               .renderStatic(stack, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, entity.getLevel(), 1);
            poseStack.popPose();
         }
      }
   }

   private Pair<Vec3, Float> getItemOffset(ItemStack stack) {
      if (stack.is(TensuraItemTags.DAGGERS)) {
         return Pair.of(new Vec3(0.5, 1.0, 0.5), 180.0F);
      } else if (stack.is(TensuraItemTags.SHORT_SWORDS)) {
         return Pair.of(new Vec3(0.5, 1.0, 0.5), 180.0F);
      } else if (stack.is(TensuraItemTags.LONG_SWORDS)) {
         return Pair.of(new Vec3(0.5, 1.15F, 0.5), 180.0F);
      } else if (stack.is(TensuraItemTags.GREAT_SWORDS)) {
         return Pair.of(new Vec3(0.5, 1.85F, 0.5), 180.0F);
      } else if (stack.is(TensuraItemTags.KATANAS)) {
         return Pair.of(new Vec3(0.5, 1.05F, 0.5), 180.0F);
      } else if (stack.is(TensuraItemTags.KODACHIS)) {
         return Pair.of(new Vec3(0.5, 1.0, 0.5), 180.0F);
      } else if (stack.is(TensuraItemTags.TACHIS)) {
         return Pair.of(new Vec3(0.5, 1.1F, 0.5), 180.0F);
      } else if (stack.is(TensuraItemTags.ODACHIS)) {
         return Pair.of(new Vec3(0.5, 1.95F, 0.5), 180.0F);
      } else if (stack.is(TensuraItemTags.SPEARS)) {
         return Pair.of(new Vec3(0.5, 0.75, 0.5), 0.0F);
      } else if (stack.is(TensuraItemTags.SCYTHES)) {
         return Pair.of(new Vec3(0.5, 0.65F, 0.5), 0.0F);
      } else if (stack.is(TensuraItemTags.TRIDENTS)) {
         return Pair.of(new Vec3(0.5, 0.9F, 0.5), 0.0F);
      } else if (stack.is(TensuraItemTags.MAGIC_STAVES)) {
         return Pair.of(new Vec3(0.5, 0.65F, 0.5), 0.0F);
      } else if (stack.is(ItemTags.AXES) || stack.is(ItemTags.PICKAXES) || stack.is(ItemTags.MACE_ENCHANTABLE)) {
         return Pair.of(new Vec3(0.5, 0.8F, 0.5), 0.0F);
      } else if (stack.is(ItemTags.SHOVELS) || stack.is(ItemTags.HOES)) {
         return Pair.of(new Vec3(0.5, 0.75, 0.5), 0.0F);
      } else {
         return stack.is(TensuraItemTags.SICKLES) ? Pair.of(new Vec3(0.5, 0.7F, 0.5), 0.0F) : Pair.of(new Vec3(0.5, 1.1F, 0.5), 180.0F);
      }
   }
}
