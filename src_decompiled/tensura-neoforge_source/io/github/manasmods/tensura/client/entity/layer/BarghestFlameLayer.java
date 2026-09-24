package io.github.manasmods.tensura.client.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.client.TensuraColors;
import io.github.manasmods.tensura.entity.monster.BarghestEntity;
import io.github.manasmods.tensura.entity.variant.BarghestFlameVariant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.texture.AnimatableTexture;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.Color;

public class BarghestFlameLayer extends GeoRenderLayer<BarghestEntity> {
   public BarghestFlameLayer(GeoRenderer<BarghestEntity> entityRenderer) {
      super(entityRenderer);
   }

   protected ResourceLocation getTextureResource(BarghestEntity animatable) {
      ResourceLocation type;
      if (this.isRainbow(animatable)) {
         type = ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/barghest/barghest_flame_no_color.png");
      } else {
         type = BarghestFlameVariant.LOCATION_BY_VARIANT.get(animatable.getFlameType());
      }

      AnimatableTexture.setAndUpdate(type);
      return type;
   }

   public void render(
      PoseStack poseStack,
      BarghestEntity animatable,
      BakedGeoModel bakedModel,
      RenderType renderType,
      MultiBufferSource bufferSource,
      VertexConsumer buffer,
      float partialTick,
      int packedLight,
      int packedOverlay
   ) {
      int color = Color.WHITE.argbInt();
      if (this.isRainbow(animatable)) {
         int tickCount = animatable.tickCount / 25 + animatable.getId();
         int preColor = tickCount % DyeColor.values().length;
         int nextColor = (tickCount + 1) % DyeColor.values().length;
         float tick = (animatable.tickCount % 25 + partialTick) / 25.0F;
         int previous = Sheep.getColor(DyeColor.byId(preColor));
         int next = Sheep.getColor(DyeColor.byId(nextColor));
         color = ARGB32.lerp(tick, previous, next);
      }

      if (animatable.isInvisible()) {
         Player player = Minecraft.getInstance().player;
         if (player == null || animatable.isInvisibleTo(player)) {
            return;
         }

         color = TensuraColors.getARGBWithAlpha(color, 0.1F);
      }

      RenderType type = RenderType.entityTranslucentEmissive(this.getTextureResource(animatable));
      this.getRenderer()
         .reRender(
            this.getDefaultBakedModel(animatable),
            poseStack,
            bufferSource,
            animatable,
            type,
            bufferSource.getBuffer(type),
            partialTick,
            packedLight,
            OverlayTexture.NO_OVERLAY,
            color
         );
   }

   private boolean isRainbow(BarghestEntity animatable) {
      return animatable.hasCustomName() && "jeb_".equals(animatable.getName().getString()) && animatable.getFlameType().equals(BarghestFlameVariant.WHITE);
   }
}
