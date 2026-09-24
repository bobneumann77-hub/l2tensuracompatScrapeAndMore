package io.github.manasmods.tensura.client.entity.projectile.magic;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.entity.projectile.magic.AuraBulletProjectile;
import io.github.manasmods.tensura.util.client.ClientHelper;
import java.util.Objects;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.FastColor.ARGB32;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.texture.AnimatableTexture;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.Color;

public class AuraBulletRenderer<T extends AuraBulletProjectile> extends GeoEntityRenderer<T> {
   public AuraBulletRenderer(Context renderManager) {
      super(renderManager, new MagicSphereModel());
      AnimatableTexture.setAndUpdate(ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/projectiles/magic_sphere/aura_bullet.png"));
   }

   protected float getShadowRadius(T entity) {
      return 0.0F;
   }

   protected int getBlockLightLevel(T entity, BlockPos blockPos) {
      return 15;
   }

   public Color getRenderColor(T animatable, float partialTick, int packedLight) {
      Color color = super.getRenderColor(animatable, partialTick, packedLight);
      if (ClientHelper.isInFirstViewDistance(animatable, 2.0F + animatable.getSize())) {
         color = Color.ofARGB(Mth.ceil(color.getAlpha() * 0.1F), color.getRed(), color.getGreen(), color.getBlue());
      }

      return color;
   }

   public void renderCubesOfBone(PoseStack poseStack, GeoBone bone, VertexConsumer buffer, int packedLight, int packedOverlay, int colour) {
      if (Objects.equals(bone.getName(), "aura_inner")) {
         Color color = Color.ofOpaque(((AuraBulletProjectile)this.getAnimatable()).getColor()).brighter(0.8F);
         colour = ARGB32.multiply(colour, color.argbInt());
      } else if (Objects.equals(bone.getName(), "aura_3")) {
         Color color = Color.ofOpaque(((AuraBulletProjectile)this.getAnimatable()).getColor()).brighter(1.2F);
         colour = ARGB32.multiply(colour, color.argbInt());
      } else if (Objects.equals(bone.getName(), "aura_1")) {
         Color color = Color.ofOpaque(((AuraBulletProjectile)this.getAnimatable()).getColor());
         colour = ARGB32.multiply(colour, color.argbInt());
      }

      super.renderCubesOfBone(poseStack, bone, buffer, packedLight, packedOverlay, colour);
   }

   public void preRender(
      PoseStack poseStack,
      T animatable,
      BakedGeoModel model,
      @Nullable MultiBufferSource bufferSource,
      @Nullable VertexConsumer buffer,
      boolean isReRender,
      float partialTick,
      int packedLight,
      int packedOverlay,
      int colour
   ) {
      this.entityRenderTranslations.set(poseStack.last().pose());
      float scale = animatable.getVisualSize();
      this.scaleModelForRender(
         this.scaleWidth * scale, this.scaleHeight * scale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay
      );
   }
}
