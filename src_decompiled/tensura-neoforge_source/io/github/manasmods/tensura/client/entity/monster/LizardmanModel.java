package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.client.entity.TensuraEntityGeoModel;
import io.github.manasmods.tensura.effect.ability.DragonModeEffect;
import io.github.manasmods.tensura.entity.monster.LizardmanEntity;
import io.github.manasmods.tensura.entity.variant.LizardmanVariant;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import java.util.List;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;

public class LizardmanModel extends TensuraEntityGeoModel<LizardmanEntity> {
   public LizardmanModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "lizardman"), "head");
   }

   public ResourceLocation getTextureResource(LizardmanEntity instance) {
      AttributeInstance magicule = instance.getAttribute(TensuraAttributes.MAX_MAGICULE);
      return magicule != null && magicule.hasModifier(DragonModeEffect.DRAGON_MODE)
         ? ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/lizardman/lizardman_dragon_body.png")
         : LizardmanVariant.LOCATION_BY_VARIANT.get(instance.getVariant());
   }

   @Nullable
   public RenderType getRenderType(LizardmanEntity animatable, ResourceLocation texture) {
      return RenderType.entityCutoutNoCull(texture, false);
   }

   public void setCustomAnimations(LizardmanEntity lizardman, long instanceId, AnimationState<LizardmanEntity> animationState) {
      GeoBone wings = this.getAnimationProcessor().getBone("wings");
      if (lizardman.isDragonewt() == wings.isHidden()) {
         wings.setHidden(!lizardman.isDragonewt());
      }

      boolean hood = lizardman.getHair() == LizardmanVariant.Hair.HOOD;
      GeoBone leftHorn = this.getAnimationProcessor().getBone("leftHorn");
      if (hood != leftHorn.isHidden()) {
         leftHorn.setHidden(hood);
      }

      GeoBone rightHorn = this.getAnimationProcessor().getBone("rightHorn");
      if (hood != rightHorn.isHidden()) {
         rightHorn.setHidden(hood);
      }

      if (!lizardman.isSleeping() && lizardman.isAlive()) {
         super.setCustomAnimations(lizardman, instanceId, animationState);
      }
   }

   @Override
   public List<String> getHiddenInLayerBones() {
      return List.of("wings", "hair");
   }
}
