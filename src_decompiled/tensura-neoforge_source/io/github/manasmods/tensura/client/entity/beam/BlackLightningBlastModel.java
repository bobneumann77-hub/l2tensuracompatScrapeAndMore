package io.github.manasmods.tensura.client.entity.beam;

import io.github.manasmods.tensura.entity.magic.beam.BlackLightningBlastProjectile;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.texture.AnimatableTexture;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class BlackLightningBlastModel extends DefaultedEntityGeoModel<BlackLightningBlastProjectile> {
   public BlackLightningBlastModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "black_lightning_blast"), false);
      AnimatableTexture.setAndUpdate(ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/misc/black_lightning.png"));
   }

   protected String subtype() {
      return "entity/misc";
   }

   public ResourceLocation getTextureResource(BlackLightningBlastProjectile instance) {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/misc/black_lightning.png");
   }

   public void setCustomAnimations(BlackLightningBlastProjectile animatable, long instanceId, AnimationState<BlackLightningBlastProjectile> animationState) {
      GeoBone head = this.getAnimationProcessor().getBone("lightning");
      if (head != null) {
         Entity entityData = (Entity)animationState.getData(DataTickets.ENTITY);
         head.setRotX(entityData.getXRot() * (float) (-Math.PI / 180.0));
         head.setRotY(entityData.getYRot() * (float) (-Math.PI / 180.0));
      }
   }
}
