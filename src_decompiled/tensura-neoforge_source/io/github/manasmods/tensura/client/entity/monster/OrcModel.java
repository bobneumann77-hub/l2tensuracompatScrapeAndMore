package io.github.manasmods.tensura.client.entity.monster;

import io.github.manasmods.tensura.entity.monster.OrcEntity;
import io.github.manasmods.tensura.entity.variant.OrcVariant;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class OrcModel extends DefaultedEntityGeoModel<OrcEntity> {
   public OrcModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "orc"), true);
   }

   public ResourceLocation getTextureResource(OrcEntity instance) {
      return OrcVariant.LOCATION_BY_VARIANT.get(instance.getVariant());
   }

   @Nullable
   public RenderType getRenderType(OrcEntity animatable, ResourceLocation texture) {
      return RenderType.entityCutoutNoCull(texture, false);
   }

   public void setCustomAnimations(OrcEntity orc, long instanceId, AnimationState<OrcEntity> animationState) {
      boolean hasChestplate = !orc.getItemBySlot(EquipmentSlot.CHEST).isEmpty();
      GeoBone belly = this.getAnimationProcessor().getBone("Belly");
      if (hasChestplate != belly.isHidden()) {
         belly.setHidden(hasChestplate);
      }

      GeoBone armorBelly = this.getAnimationProcessor().getBone("ArmorBelly");
      if (hasChestplate == armorBelly.isHidden()) {
         armorBelly.setHidden(!hasChestplate);
      }

      if (!orc.isSleeping() && orc.isAlive()) {
         super.setCustomAnimations(orc, instanceId, animationState);
      }
   }
}
