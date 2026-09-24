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

public class MagicSlashModel<T extends TensuraFlyingProjectile & GeoEntity> extends DefaultedEntityGeoModel<T> {
   private final boolean doubleBlade;

   public MagicSlashModel(boolean doubleBlade) {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "magic_slash"), false);
      this.doubleBlade = doubleBlade;
      AnimatableTexture.setAndUpdate(ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/projectiles/slash/wind_blade.png"));
   }

   protected String subtype() {
      return "entity/misc";
   }

   public RenderType getRenderType(T animatable, ResourceLocation texture) {
      return RenderType.entityTranslucentCull(texture);
   }

   public ResourceLocation getTextureResource(T instance) {
      ResourceLocation texture = instance.getTexture();
      return texture == null ? ResourceLocation.fromNamespaceAndPath("tensura", "textures/blank_texture.png") : texture;
   }

   public void setCustomAnimations(T animatable, long instanceId, AnimationState<T> animationState) {
      GeoBone slash2 = this.getAnimationProcessor().getBone("slash2");
      slash2.setHidden(!this.doubleBlade);
      GeoBone slash = this.getAnimationProcessor().getBone("slash");
      if (slash != null) {
         Entity entityData = (Entity)animationState.getData(DataTickets.ENTITY);
         slash.setRotX(entityData.getXRot() * (float) (Math.PI / 180.0));
         slash.setRotY(entityData.getYRot() * (float) (Math.PI / 180.0));
      }
   }
}
