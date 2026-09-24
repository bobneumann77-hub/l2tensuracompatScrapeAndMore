package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.entity.monster.DirewolfEntity;
import io.github.manasmods.tensura.entity.variant.DirewolfVariant;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class DirewolfModel extends DefaultedEntityGeoModel<DirewolfEntity> {
   private static final ResourceLocation INDIE_TEXTURE = ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/direwolf/indie.png");
   private static final ResourceLocation GUITAR_WOLF_TEXTURE = ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/direwolf/guitar_wolf.png");
   private static final ResourceLocation GUITAR_STAR_WOLF_TEXTURE = ResourceLocation.fromNamespaceAndPath(
      "tensura", "textures/entity/direwolf/guitar_star_wolf.png"
   );
   private static final ResourceLocation MOMO_WOLF_TEXTURE = ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/direwolf/momo_wolf.png");

   public DirewolfModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "direwolf"), true);
   }

   public ResourceLocation getTextureResource(DirewolfEntity instance) {
      return this.getLocation(instance);
   }

   public void setCustomAnimations(DirewolfEntity entity, long instanceId, AnimationState<DirewolfEntity> animationState) {
      GeoBone chests = this.getAnimationProcessor().getBone("Chests");
      if (entity.isChested() == chests.isHidden()) {
         chests.setHidden(!entity.isChested());
      }

      GeoBone scarEye = this.getAnimationProcessor().getBone("ScarEye");
      if (entity.isAlpha() == scarEye.isHidden()) {
         scarEye.setHidden(!entity.isAlpha());
      }

      GeoBone upperHorn = this.getAnimationProcessor().getBone("UpperHorn");
      if (this.showUpperHorn(entity) == upperHorn.isHidden()) {
         upperHorn.setHidden(!this.showUpperHorn(entity));
      }

      GeoBone lowerHorn = this.getAnimationProcessor().getBone("LowerHorn");
      if (this.showLowerHorn(entity) == lowerHorn.isHidden()) {
         lowerHorn.setHidden(!this.showLowerHorn(entity));
      }

      if (!entity.isSleeping() && !entity.isInSittingPose()) {
         if (!animationState.isCurrentAnimation(DirewolfEntity.EAT)) {
            if (!animationState.isCurrentAnimation(DirewolfEntity.HOWL)) {
               if (!animationState.isCurrentAnimation(DirewolfEntity.SHADOW_MOTION)) {
                  super.setCustomAnimations(entity, instanceId, animationState);
               }
            }
         }
      }
   }

   public ResourceLocation getLocation(DirewolfEntity entity) {
      DirewolfVariant variant = entity.getVariant();
      if (entity.hasCustomName()) {
         String name = entity.getName().getString();
         if ("Indie".equalsIgnoreCase(name)) {
            if (variant == DirewolfVariant.BLUE_FANG || variant == DirewolfVariant.MYSTIC_WATER_WOLF) {
               return INDIE_TEXTURE;
            }
         } else if (entity.isStar()) {
            if ("Guitar".equalsIgnoreCase(name)) {
               if (variant == DirewolfVariant.DIREWOLF || variant == DirewolfVariant.STAR_WOLF) {
                  return GUITAR_WOLF_TEXTURE;
               }

               if (variant == DirewolfVariant.TEMPEST_STAR_WOLF) {
                  return GUITAR_STAR_WOLF_TEXTURE;
               }
            } else if (("Momo".equalsIgnoreCase(name) || "Memoires".equalsIgnoreCase(name))
               && (variant == DirewolfVariant.TEMPEST_STAR_WOLF || entity.isStar())) {
               return MOMO_WOLF_TEXTURE;
            }
         }
      }

      return entity.isStar() && variant.getBirthmarkTexture() != null ? variant.getBirthmarkTexture() : variant.getTexture();
   }

   private boolean showUpperHorn(DirewolfEntity entity) {
      return entity.getVariant() == DirewolfVariant.TEMPEST_STAR_WOLF ? true : entity.getVariant() == DirewolfVariant.STAR_WOLF;
   }

   private boolean showLowerHorn(DirewolfEntity entity) {
      return entity.getVariant() == DirewolfVariant.TEMPEST_STAR_WOLF;
   }
}
