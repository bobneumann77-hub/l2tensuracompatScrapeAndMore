package io.github.manasmods.tensura.client.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.client.TensuraColors;
import io.github.manasmods.tensura.entity.monster.SlimeEntity;
import io.github.manasmods.tensura.util.client.ClientHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.Color;

public class SlimeSantaHatLayer extends GeoRenderLayer<SlimeEntity> {
   public SlimeSantaHatLayer(GeoRenderer<SlimeEntity> renderer) {
      super(renderer);
   }

   public ResourceLocation getTextureResource() {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/slime/santa_hat.png");
   }

   public void render(
      PoseStack poseStack,
      SlimeEntity slime,
      BakedGeoModel bakedModel,
      RenderType renderType,
      MultiBufferSource bufferSource,
      VertexConsumer buffer,
      float partialTick,
      int packedLight,
      int packedOverlay
   ) {
      if (shouldShowSantaHat(slime)) {
         int color = Color.WHITE.argbInt();
         if (slime.isInvisible()) {
            Player player = Minecraft.getInstance().player;
            if (player == null || slime.isInvisibleTo(player)) {
               return;
            }

            color = TensuraColors.getARGBWithAlpha(color, 0.1F);
         }

         RenderType type = RenderType.entityCutout(this.getTextureResource());
         this.getRenderer()
            .reRender(
               this.getDefaultBakedModel(slime),
               poseStack,
               bufferSource,
               slime,
               type,
               bufferSource.getBuffer(type),
               partialTick,
               packedLight,
               OverlayTexture.NO_OVERLAY,
               color
            );
      }
   }

   public static boolean shouldShowSantaHat(SlimeEntity slime) {
      ItemStack itemStack = slime.getItemBySlot(EquipmentSlot.HEAD);
      return itemStack.isEmpty() && !slime.isSaddled() && ClientHelper.XMAS;
   }
}
