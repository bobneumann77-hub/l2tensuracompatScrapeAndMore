package io.github.manasmods.tensura.client.entity.misc;

import io.github.manasmods.tensura.entity.projectile.magic.ChaosEaterProjectile;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.texture.AnimatableTexture;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class ChaosEaterModel extends DefaultedEntityGeoModel<ChaosEaterProjectile> {
   public ChaosEaterModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "chaos_eater"), false);
      AnimatableTexture.setAndUpdate(ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/misc/chaos_eater.png"));
   }

   protected String subtype() {
      return "entity/misc";
   }

   public ResourceLocation getTextureResource(ChaosEaterProjectile instance) {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/misc/chaos_eater.png");
   }

   public RenderType getRenderType(ChaosEaterProjectile animatable, ResourceLocation texture) {
      return RenderType.entityTranslucentCull(texture);
   }

   public void setCustomAnimations(ChaosEaterProjectile animatable, long instanceId, AnimationState<ChaosEaterProjectile> animationState) {
      GeoBone bone = this.getAnimationProcessor().getBone("Rotating");
      if (bone != null) {
         Entity entityData = (Entity)animationState.getData(DataTickets.ENTITY);
         Vec3 motion = entityData.getDeltaMovement();
         float xRot = -((float)(Mth.atan2(motion.horizontalDistance(), motion.y) * 180.0F / (float)Math.PI) - 90.0F);
         float yRot = -((float)(Mth.atan2(motion.z, motion.x) * 180.0F / (float)Math.PI) - 90.0F + (animatable.isReached() ? 180.0F : 0.0F));
         bone.setRotX(xRot * (float) (Math.PI / 180.0));
         bone.setRotY(yRot * (float) (Math.PI / 180.0));
      }
   }
}
