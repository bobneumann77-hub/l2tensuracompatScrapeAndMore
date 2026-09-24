package io.github.manasmods.tensura.client.entity.circle;

import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.util.client.ClientHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.texture.AnimatableTexture;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class MagicCircleModel extends DefaultedEntityGeoModel<MagicCircle> {
   public MagicCircleModel() {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "magic_circle"), false);

      for (MagicCircleVariant variant : MagicCircleVariant.values()) {
         AnimatableTexture.setAndUpdate(ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/misc/magic_circle/" + variant.getName() + ".png"));
      }
   }

   protected String subtype() {
      return "entity/misc";
   }

   public RenderType getRenderType(MagicCircle animatable, ResourceLocation texture) {
      return ClientHelper.getFirstViewRenderTypeBright(animatable, 2.5, texture);
   }

   public ResourceLocation getTextureResource(MagicCircle instance) {
      return instance.getVariant().getTexture();
   }

   public void setCustomAnimations(MagicCircle animatable, long instanceId, AnimationState<MagicCircle> animationState) {
      GeoBone head = this.getAnimationProcessor().getBone("all");
      if (head != null) {
         Entity entityData = (Entity)animationState.getData(DataTickets.ENTITY);
         head.setRotX((entityData.getXRot() + 90.0F) * (float) (-Math.PI / 180.0));
         head.setRotY(entityData.getYRot() * (float) (-Math.PI / 180.0));
      }
   }
}
