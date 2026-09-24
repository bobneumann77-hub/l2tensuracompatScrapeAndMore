package io.github.manasmods.tensura.client.entity.projectile.magic;

import io.github.manasmods.tensura.entity.projectile.TensuraFlyingProjectile;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.texture.AnimatableTexture;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class MagicBallModel<T extends TensuraFlyingProjectile & GeoEntity> extends DefaultedEntityGeoModel<T> {
   public MagicBallModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "magic_ball"), false);
      AnimatableTexture.setAndUpdate(ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/projectiles/magic_ball/fire.png"));
      AnimatableTexture.setAndUpdate(ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/projectiles/magic_ball/plasma_fire.png"));
      AnimatableTexture.setAndUpdate(ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/projectiles/magic_ball/black_fire.png"));
      AnimatableTexture.setAndUpdate(ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/projectiles/magic_ball/water.png"));
      AnimatableTexture.setAndUpdate(ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/projectiles/magic_ball/poison.png"));
      AnimatableTexture.setAndUpdate(ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/projectiles/magic_ball/acid.png"));
      AnimatableTexture.setAndUpdate(ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/projectiles/magic_ball/mud.png"));
   }

   protected String subtype() {
      return "entity/misc";
   }

   public RenderType getRenderType(T animatable, ResourceLocation texture) {
      return RenderType.entityTranslucent(texture);
   }

   public ResourceLocation getTextureResource(T instance) {
      ResourceLocation texture = instance.getTexture();
      return texture == null ? ResourceLocation.fromNamespaceAndPath("tensura", "textures/blank_texture.png") : texture;
   }

   public void setCustomAnimations(T animatable, long instanceId, AnimationState<T> animationState) {
      GeoBone head = this.getAnimationProcessor().getBone("ball");
      if (head != null) {
         Entity entityData = (Entity)animationState.getData(DataTickets.ENTITY);
         head.setRotX(entityData.getXRot() * (float) (Math.PI / 180.0));
         head.setRotY(entityData.getYRot() * (float) (Math.PI / 180.0));
      }
   }
}
