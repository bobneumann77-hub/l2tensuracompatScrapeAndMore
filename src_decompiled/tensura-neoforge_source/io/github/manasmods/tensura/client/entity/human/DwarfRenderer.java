package io.github.manasmods.tensura.client.entity.human;

import io.github.manasmods.tensura.client.entity.layer.DwarfLayer;
import io.github.manasmods.tensura.client.entity.layer.ProfessionClothesLayer;
import io.github.manasmods.tensura.client.entity.layer.RoyalGuardArmorLayer;
import io.github.manasmods.tensura.entity.human.DwarfEntity;
import io.github.manasmods.tensura.entity.variant.DwarfVariant;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;

public class DwarfRenderer extends PlayerLikeRenderer<DwarfEntity> {
   public DwarfRenderer(Context pContext) {
      super(pContext, new PlayerLikeModel<>(pContext.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
      this.addLayer(
         new HumanoidArmorLayer(
            this,
            new HumanoidModel(pContext.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
            new HumanoidModel(pContext.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)),
            pContext.getModelManager()
         )
      );
      this.addLayer(new DwarfLayer.Face(this));
      this.addLayer(new DwarfLayer.Hair(this));
      this.addLayer(new DwarfLayer.HairBody(this));
      this.addLayer(new DwarfLayer.FacialHair(this));
      this.addLayer(new DwarfLayer.Chest(this));
      this.addLayer(new DwarfLayer.Top(this));
      this.addLayer(new DwarfLayer.Bottom(this));
      this.addLayer(new DwarfLayer.Feet(this));
      this.addLayer(new RoyalGuardArmorLayer.Armor(this));
      this.addLayer(new RoyalGuardArmorLayer.Helmet(this));
      this.addLayer(new RoyalGuardArmorLayer.Chest(this));
      this.addLayer(new ProfessionClothesLayer(this));
      this.addLayer(new ProfessionClothesLayer.Chest(this));
   }

   public ResourceLocation getTextureLocation(DwarfEntity pEntity) {
      return DwarfVariant.Skin.getTextureLocation(pEntity);
   }

   protected boolean shouldSwim(DwarfEntity entity) {
      return entity.shouldSwim();
   }
}
