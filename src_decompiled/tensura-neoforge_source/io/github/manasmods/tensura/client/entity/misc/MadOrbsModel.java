package io.github.manasmods.tensura.client.entity.misc;

import io.github.manasmods.tensura.entity.magic.misc.MadOrbsEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class MadOrbsModel extends DefaultedEntityGeoModel<MadOrbsEntity> {
   public MadOrbsModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "mad_orbs"), false);
   }

   protected String subtype() {
      return "entity/misc";
   }

   public RenderType getRenderType(MadOrbsEntity animatable, ResourceLocation texture) {
      return RenderType.entityTranslucent(texture);
   }

   public void setCustomAnimations(MadOrbsEntity animatable, long instanceId, AnimationState<MadOrbsEntity> animationState) {
      for (int i = 1; i <= 6; i++) {
         GeoBone orb = this.getAnimationProcessor().getBone("Orb" + i);
         if (orb.isHidden() == animatable.getSpheres() >= i) {
            orb.setHidden(animatable.getSpheres() < i);
         }
      }

      GeoBone all = this.getAnimationProcessor().getBone("All");
      if (all != null) {
         Entity entityData = (Entity)animationState.getData(DataTickets.ENTITY);
         all.setRotY(entityData.getYRot() * (float) (-Math.PI / 180.0));
      }
   }
}
