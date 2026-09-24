package io.github.manasmods.tensura.client.entity;

import java.util.List;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class TensuraEntityGeoModel<T extends GeoAnimatable> extends DefaultedEntityGeoModel<T> {
   protected final String head;

   public TensuraEntityGeoModel(ResourceLocation location) {
      super(location);
      this.head = "head";
   }

   public TensuraEntityGeoModel(ResourceLocation location, String head) {
      super(location, true);
      this.head = head;
   }

   public void setCustomAnimations(T animatable, long instanceId, AnimationState<T> animationState) {
      if (this.turnsHead) {
         GeoBone head = this.getAnimationProcessor().getBone(this.head);
         if (head != null) {
            EntityModelData entityData = (EntityModelData)animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * (float) (Math.PI / 180.0));
            head.setRotY(entityData.netHeadYaw() * (float) (Math.PI / 180.0));
         }
      }
   }

   public List<String> getHiddenInLayerBones() {
      return List.of();
   }
}
