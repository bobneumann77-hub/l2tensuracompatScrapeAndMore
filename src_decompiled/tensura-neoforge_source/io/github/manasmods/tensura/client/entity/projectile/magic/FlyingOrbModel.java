package io.github.manasmods.tensura.client.entity.projectile.magic;

import io.github.manasmods.tensura.entity.projectile.TensuraFlyingProjectile;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class FlyingOrbModel<T extends TensuraFlyingProjectile & GeoEntity> extends DefaultedEntityGeoModel<T> {
   public FlyingOrbModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "flying_orb"), false);
   }

   protected String subtype() {
      return "entity/misc";
   }

   public RenderType getRenderType(T animatable, ResourceLocation texture) {
      return RenderType.entityTranslucent(texture);
   }

   public ResourceLocation getTextureResource(T instance) {
      ResourceLocation[] resourceLocations = instance.getTextureLocation();
      if (resourceLocations == null) {
         return ResourceLocation.fromNamespaceAndPath("tensura", "textures/blank_texture.png");
      }

      List<ResourceLocation> list = Arrays.stream(resourceLocations).toList();
      return list.isEmpty() ? ResourceLocation.fromNamespaceAndPath("tensura", "textures/blank_texture.png") : list.getFirst();
   }

   public void setCustomAnimations(T animatable, long instanceId, AnimationState<T> animationState) {
      GeoBone head = this.getAnimationProcessor().getBone("All");
      if (head != null) {
         Entity entityData = (Entity)animationState.getData(DataTickets.ENTITY);
         Vec3 motion = entityData.getDeltaMovement();
         float xRot = -((float)(Mth.atan2(motion.horizontalDistance(), motion.y) * 180.0F / (float)Math.PI) - 90.0F);
         float yRot = -((float)(Mth.atan2(motion.z, motion.x) * 180.0F / (float)Math.PI) - 90.0F);
         head.setRotX(xRot * (float) (Math.PI / 180.0));
         head.setRotY(yRot * (float) (Math.PI / 180.0));
      }
   }
}
