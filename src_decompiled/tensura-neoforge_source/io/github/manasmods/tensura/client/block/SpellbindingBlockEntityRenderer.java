package io.github.manasmods.tensura.client.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.manasmods.tensura.block.entity.SpellbindingBlockEntity;
import io.github.manasmods.tensura.data.TensuraItemTags;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class SpellbindingBlockEntityRenderer implements BlockEntityRenderer<SpellbindingBlockEntity> {
   private final ItemRenderer itemRenderer;

   public SpellbindingBlockEntityRenderer(Context context) {
      this.itemRenderer = context.getItemRenderer();
   }

   public void render(
      SpellbindingBlockEntity entity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay
   ) {
      ItemStack itemStack = entity.getItem(0);
      if (!itemStack.isEmpty()) {
         pPoseStack.pushPose();
         Vec3 offset = new Vec3(0.5, 1.0, 0.5);
         pPoseStack.translate(offset.x(), offset.y(), offset.z());
         pPoseStack.mulPose(Axis.YP.rotation((entity.spin++ + pPartialTick) / 80.0F));
         if (itemStack.is(TensuraItemTags.SPELL_CAST_WEAPONS)) {
            pPoseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            this.itemRenderer
               .renderStatic(itemStack, ItemDisplayContext.FIXED, pPackedLight, OverlayTexture.NO_OVERLAY, pPoseStack, pBufferSource, entity.getLevel(), 1);
         } else {
            this.itemRenderer
               .renderStatic(itemStack, ItemDisplayContext.GROUND, pPackedLight, OverlayTexture.NO_OVERLAY, pPoseStack, pBufferSource, entity.getLevel(), 1);
         }

         pPoseStack.popPose();
      }
   }
}
