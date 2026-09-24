package io.github.manasmods.tensura.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.registry.item.TensuraArmorItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class WingsLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
   private static final ResourceLocation WINGS_LOCATION = ResourceLocation.fromNamespaceAndPath("tensura", "textures/models/armor/bat_glider.png");
   private final WingsModel<T> model = new WingsModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(WingsModel.WINGS_LAYER));

   public WingsLayer(RenderLayerParent<T, M> pRenderer) {
      super(pRenderer);
   }

   public void render(
      PoseStack pMatrixStack,
      MultiBufferSource pBuffer,
      int pPackedLight,
      T entity,
      float pLimbSwing,
      float pLimbSwingAmount,
      float pPartialTicks,
      float pAgeInTicks,
      float pNetHeadYaw,
      float pHeadPitch
   ) {
      ItemStack chest = entity.getItemBySlot(EquipmentSlot.CHEST);
      if (this.shouldRender(chest, entity)) {
         ResourceLocation resourcelocation = this.getWingsTexture(entity);
         pMatrixStack.pushPose();
         pMatrixStack.translate(0.0, 0.0, 0.125);
         this.getParentModel().copyPropertiesTo(this.model);
         this.model.setupAnim(entity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
         VertexConsumer vertexconsumer = ItemRenderer.getArmorFoilBuffer(pBuffer, RenderType.armorCutoutNoCull(resourcelocation), chest.hasFoil());
         this.model.renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY);
         pMatrixStack.popPose();
      }
   }

   public boolean shouldRender(ItemStack chest, T entity) {
      return chest.is((Item)TensuraArmorItems.BAT_GLIDER.get());
   }

   public ResourceLocation getWingsTexture(T entity) {
      return WINGS_LOCATION;
   }
}
