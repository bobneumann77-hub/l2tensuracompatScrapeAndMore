package io.github.manasmods.tensura.client.layer.player;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.manasmods.tensura.client.layer.template.DefaultedHumanoidLayerModel;
import io.github.manasmods.tensura.client.layer.template.GeoHumanoidLayerRenderer;
import io.github.manasmods.tensura.client.layer.template.HandCoatGeo;
import io.github.manasmods.tensura.effect.ability.HakiCoatEffect;
import io.github.manasmods.tensura.effect.ability.MagicAuraEffect;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.texture.AnimatableTexture;

public class AuraCoatRenderer extends GeoHumanoidLayerRenderer<HandCoatGeo> {
   public AuraCoatRenderer() {
      super(new DefaultedHumanoidLayerModel<HandCoatGeo>(ResourceLocation.fromNamespaceAndPath("tensura", "aura_coat")));
      AnimatableTexture.setAndUpdate(ResourceLocation.fromNamespaceAndPath("tensura", "textures/models/layer/geo/aura_coat.png"));
      AnimatableTexture.setAndUpdate(ResourceLocation.fromNamespaceAndPath("tensura", "textures/models/layer/geo/haki_coat.png"));
   }

   public ResourceLocation getTextureLocation(HandCoatGeo animatable) {
      if (this.getCurrentEntity() != null) {
         AttributeInstance instance = this.getCurrentEntity().getAttribute(TensuraAttributes.PHYSICAL_RESIST_DEGRADATION);
         if (instance != null && instance.hasModifier(HakiCoatEffect.HAKI_COAT)) {
            return ResourceLocation.fromNamespaceAndPath("tensura", "textures/models/layer/geo/haki_coat.png");
         }
      }

      return super.getTextureLocation(animatable);
   }

   @Override
   public boolean shouldRender(LivingEntity entity) {
      AttributeInstance instance = entity.getAttribute(TensuraAttributes.PHYSICAL_RESIST_DEGRADATION);
      return instance == null ? false : instance.hasModifier(MagicAuraEffect.MAGIC_AURA) || instance.hasModifier(HakiCoatEffect.HAKI_COAT);
   }

   @Override
   public void render(
      PoseStack pMatrixStack,
      LivingEntity entity,
      GeoAnimatable animatable,
      HumanoidModel<?> baseModel,
      MultiBufferSource pBuffer,
      int pPackedLight,
      float pLimbSwing,
      float pLimbSwingAmount,
      float pPartialTicks,
      float pNetHeadYaw,
      float pHeadPitch
   ) {
      if (this.shouldRender(entity)) {
         this.prepForRender(entity, animatable, baseModel, pBuffer, pPartialTicks, pLimbSwing, pLimbSwingAmount, pNetHeadYaw, pHeadPitch);
         this.renderToBuffer(pMatrixStack, pBuffer, null, pPackedLight);
      }
   }
}
