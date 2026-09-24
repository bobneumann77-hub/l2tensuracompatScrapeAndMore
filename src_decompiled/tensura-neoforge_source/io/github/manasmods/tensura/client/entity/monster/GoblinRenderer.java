package io.github.manasmods.tensura.client.entity.monster;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.manasmods.tensura.client.entity.human.PlayerLikeModel;
import io.github.manasmods.tensura.client.entity.human.PlayerLikeRenderer;
import io.github.manasmods.tensura.client.entity.layer.GoblinLayer;
import io.github.manasmods.tensura.entity.monster.GoblinEntity;
import io.github.manasmods.tensura.entity.variant.GoblinVariant;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;

public class GoblinRenderer extends PlayerLikeRenderer<GoblinEntity> {
   public GoblinRenderer(Context pContext) {
      super(pContext, new PlayerLikeModel<>(pContext.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
      this.addLayer(
         new HumanoidArmorLayer(
            this,
            new HumanoidModel(pContext.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
            new HumanoidModel(pContext.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)),
            pContext.getModelManager()
         )
      );
      this.addLayer(new GoblinLayer.Face(this));
      this.addLayer(new GoblinLayer.Hair(this));
      this.addLayer(new GoblinLayer.HairBody(this));
      this.addLayer(new GoblinLayer.Clothing(this));
      this.addLayer(new GoblinLayer.Bandages(this));
      this.addLayer(new GoblinLayer.Head(this));
      this.addLayer(new GoblinLayer.Top(this));
      this.addLayer(new GoblinLayer.Bottom(this));
   }

   public ResourceLocation getTextureLocation(GoblinEntity pEntity) {
      return GoblinVariant.Skin.getTextureLocation(pEntity);
   }

   protected void scale(GoblinEntity pLivingEntity, PoseStack pMatrixStack, float pPartialTickTime) {
      float f = pLivingEntity.isHobgoblin() ? 0.9375F : 0.7F;
      pMatrixStack.scale(f, f, f);
   }

   protected boolean shouldSwim(GoblinEntity entity) {
      return entity.shouldSwim();
   }
}
