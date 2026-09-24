package io.github.manasmods.tensura.client.entity.circle;

import io.github.manasmods.tensura.client.TensuraRenderTypes;
import io.github.manasmods.tensura.entity.magic.ExplosionCircle;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class ExplosionCircleModel extends DefaultedEntityGeoModel<ExplosionCircle> {
   public ExplosionCircleModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "explosion_circle"), false);
   }

   protected String subtype() {
      return "entity/misc";
   }

   public ResourceLocation getTextureResource(ExplosionCircle instance) {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/misc/magic_explosion.png");
   }

   public RenderType getRenderType(ExplosionCircle animatable, ResourceLocation texture) {
      return TensuraRenderTypes.getUnlitTranslucent(texture);
   }

   public void setCustomAnimations(ExplosionCircle animatable, long instanceId, AnimationState<ExplosionCircle> animationState) {
      GeoBone first = this.getAnimationProcessor().getBone("first");
      if (first.isHidden() != animatable.getExplosionLevel() < 0.5) {
         first.setHidden(animatable.getExplosionLevel() < 0.5);
      }

      GeoBone second = this.getAnimationProcessor().getBone("second");
      if (second.isHidden() != animatable.getExplosionLevel() < 1.5) {
         second.setHidden(animatable.getExplosionLevel() < 1.5);
      }

      GeoBone third = this.getAnimationProcessor().getBone("third");
      if (third.isHidden() != animatable.getExplosionLevel() < 2.5) {
         third.setHidden(animatable.getExplosionLevel() < 2.5);
      }

      GeoBone forth = this.getAnimationProcessor().getBone("forth");
      if (forth.isHidden() != animatable.getExplosionLevel() < 3.5) {
         forth.setHidden(animatable.getExplosionLevel() < 3.5);
      }

      GeoBone fifth = this.getAnimationProcessor().getBone("fifth");
      if (fifth.isHidden() != animatable.getExplosionLevel() < 4.5) {
         fifth.setHidden(animatable.getExplosionLevel() < 4.5);
      }

      GeoBone sixth = this.getAnimationProcessor().getBone("sixth");
      if (sixth.isHidden() != animatable.getExplosionLevel() < 5.5) {
         sixth.setHidden(animatable.getExplosionLevel() < 5.5);
      }

      GeoBone seventh = this.getAnimationProcessor().getBone("seventh");
      if (seventh.isHidden() != animatable.getExplosionLevel() < 6.5) {
         seventh.setHidden(animatable.getExplosionLevel() < 6.5);
      }

      GeoBone eighth = this.getAnimationProcessor().getBone("eighth");
      if (eighth.isHidden() != animatable.getExplosionLevel() < 7.5) {
         eighth.setHidden(animatable.getExplosionLevel() < 7.5);
      }

      GeoBone ninth = this.getAnimationProcessor().getBone("ninth");
      if (ninth.isHidden() != animatable.getExplosionLevel() < 8.5) {
         ninth.setHidden(animatable.getExplosionLevel() < 8.5);
      }

      GeoBone tenth = this.getAnimationProcessor().getBone("tenth");
      if (tenth.isHidden() != animatable.getExplosionLevel() < 9.5) {
         tenth.setHidden(animatable.getExplosionLevel() < 9.5);
      }

      GeoBone eleventh = this.getAnimationProcessor().getBone("eleventh");
      if (eleventh.isHidden() != animatable.getExplosionLevel() < 9.5) {
         eleventh.setHidden(animatable.getExplosionLevel() < 9.5);
      }

      GeoBone main = this.getAnimationProcessor().getBone("main");
      if (main != null) {
         Entity entityData = (Entity)animationState.getData(DataTickets.ENTITY);
         main.setRotX((entityData.getXRot() + 90.0F) * (float) (-Math.PI / 180.0));
         main.setRotY(entityData.getYRot() * (float) (-Math.PI / 180.0));
      }
   }
}
