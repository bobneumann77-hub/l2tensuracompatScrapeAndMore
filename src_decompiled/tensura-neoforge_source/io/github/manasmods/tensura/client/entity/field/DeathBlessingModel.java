package io.github.manasmods.tensura.client.entity.field;

import io.github.manasmods.tensura.entity.magic.field.DeathBlessingField;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class DeathBlessingModel extends DefaultedEntityGeoModel<DeathBlessingField> {
   public DeathBlessingModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "death_blessing"), false);
   }

   protected String subtype() {
      return "entity/misc";
   }

   public ResourceLocation getTextureResource(DeathBlessingField instance) {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/field/death_blessing.png");
   }

   public RenderType getRenderType(DeathBlessingField animatable, ResourceLocation texture) {
      return RenderType.entityTranslucent(texture);
   }

   public void setCustomAnimations(DeathBlessingField bless, long instanceId, AnimationState<DeathBlessingField> animationState) {
      GeoBone yellowRay = this.getAnimationProcessor().getBone("yellow_ray_root");
      if (yellowRay.isHidden() != bless.isCastedTooEarly()) {
         yellowRay.setHidden(bless.isCastedTooEarly());
      }

      GeoBone soul = this.getAnimationProcessor().getBone("soul_root");
      if (soul.isHidden() != bless.isCastedTooEarly()) {
         soul.setHidden(bless.isCastedTooEarly());
      }

      GeoBone all = this.getAnimationProcessor().getBone("all");
      if (all != null) {
         Entity entityData = (Entity)animationState.getData(DataTickets.ENTITY);
         all.setRotX(entityData.getXRot() * (float) (-Math.PI / 180.0));
         all.setRotY(entityData.getYRot() * (float) (-Math.PI / 180.0));
      }
   }
}
