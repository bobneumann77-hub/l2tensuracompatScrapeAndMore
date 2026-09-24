package io.github.manasmods.tensura.client.entity.human;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.manasmods.tensura.entity.human.CloneEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.component.ResolvableProfile;

public class CloneRenderer extends PlayerLikeRenderer<CloneEntity> {
   private final PlayerLikeModel<CloneEntity> defaultModel = (PlayerLikeModel<CloneEntity>)this.model;
   private final HumanoidArmorLayer<CloneEntity, PlayerLikeModel<CloneEntity>, HumanoidArmorModel<CloneEntity>> defaultArmor;
   private final PlayerLikeModel<CloneEntity> slimModel;
   private final HumanoidArmorLayer<CloneEntity, PlayerLikeModel<CloneEntity>, HumanoidArmorModel<CloneEntity>> slimArmor;

   public CloneRenderer(Context context) {
      super(context, new PlayerLikeModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
      this.slimModel = new PlayerLikeModel<>(context.bakeLayer(ModelLayers.PLAYER_SLIM), true);
      this.defaultArmor = new HumanoidArmorLayer(
         this,
         new HumanoidArmorModel(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
         new HumanoidArmorModel(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)),
         context.getModelManager()
      );
      this.slimArmor = new HumanoidArmorLayer(
         this,
         new HumanoidArmorModel(context.bakeLayer(ModelLayers.PLAYER_SLIM_INNER_ARMOR)),
         new HumanoidArmorModel(context.bakeLayer(ModelLayers.PLAYER_SLIM_OUTER_ARMOR)),
         context.getModelManager()
      );
   }

   protected boolean shouldShowName(CloneEntity pEntity) {
      return true;
   }

   protected void scale(CloneEntity clone, PoseStack pMatrixStack, float pPartialTickTime) {
      float scale = 0.9375F * clone.getHeight();
      pMatrixStack.scale(scale, scale, scale);
      this.shadowRadius = 0.5F * clone.getHeight();
   }

   public void render(CloneEntity clone, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
      ResolvableProfile profile = clone.getProfile();
      if (profile != null) {
         SkinManager skinManager = Minecraft.getInstance().getSkinManager();
         switch (skinManager.getInsecureSkin(profile.gameProfile()).model()) {
            case WIDE:
               this.model = this.defaultModel;
               if (!this.layers.contains(this.defaultArmor)) {
                  this.layers.remove(this.slimArmor);
                  this.layers.add(this.defaultArmor);
               }
               break;
            case SLIM:
               this.model = this.slimModel;
               if (!this.layers.contains(this.slimArmor)) {
                  this.layers.remove(this.defaultArmor);
                  this.layers.add(this.slimArmor);
               }
         }
      }

      super.render(clone, f, g, poseStack, multiBufferSource, i);
   }

   public ResourceLocation getTextureLocation(CloneEntity clone) {
      ResolvableProfile profile = clone.getProfile();
      if (profile == null) {
         return ResourceLocation.withDefaultNamespace("textures/entity/steve.png");
      }

      SkinManager skinManager = Minecraft.getInstance().getSkinManager();
      return skinManager.getInsecureSkin(profile.gameProfile()).texture();
   }

   protected double getSittingYOffset(CloneEntity clone) {
      return super.getSittingYOffset(clone) * clone.getHeight();
   }
}
