package io.github.manasmods.tensura.client.entity.misc;

import io.github.manasmods.tensura.entity.magic.beam.PredatorMistProjectile;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class GluttonyMistModel extends DefaultedEntityGeoModel<PredatorMistProjectile> {
   private final ResourceLocation texture;

   public GluttonyMistModel(@Nullable ResourceLocation location) {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "gluttony_mist"), false);
      this.texture = location;
   }

   protected String subtype() {
      return "entity/misc";
   }

   public RenderType getRenderType(PredatorMistProjectile animatable, ResourceLocation texture) {
      return RenderType.entityTranslucent(texture);
   }

   public ResourceLocation getTextureResource(PredatorMistProjectile instance) {
      return this.texture == null ? super.getTextureResource(instance) : this.texture;
   }

   public void setCustomAnimations(PredatorMistProjectile animatable, long instanceId, AnimationState<PredatorMistProjectile> animationState) {
      GeoBone head = this.getAnimationProcessor().getBone("all");
      if (head != null) {
         Entity entityData = (Entity)animationState.getData(DataTickets.ENTITY);
         head.setRotX(entityData.getXRot() * (float) (-Math.PI / 180.0));
         head.setRotY(entityData.getYRot() * (float) (-Math.PI / 180.0));
      }
   }
}
