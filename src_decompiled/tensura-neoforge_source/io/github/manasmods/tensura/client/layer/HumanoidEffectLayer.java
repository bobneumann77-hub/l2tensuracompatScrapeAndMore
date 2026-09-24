package io.github.manasmods.tensura.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.manasmods.tensura.client.layer.player.AuraCoatRenderer;
import io.github.manasmods.tensura.client.layer.player.FlameCoatRenderer;
import io.github.manasmods.tensura.client.layer.player.LightningCoatRenderer;
import io.github.manasmods.tensura.client.layer.template.HandCoatGeo;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;

public class HumanoidEffectLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
   private final AuraCoatRenderer auraCoat;
   private final FlameCoatRenderer flameCoat;
   private final LightningCoatRenderer lightningCoat;
   private final HandCoatGeo handCoat = new HandCoatGeo();

   public HumanoidEffectLayer(RenderLayerParent<T, M> pRenderer) {
      super(pRenderer);
      this.auraCoat = new AuraCoatRenderer();
      this.flameCoat = new FlameCoatRenderer();
      this.lightningCoat = new LightningCoatRenderer();
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
      if (this.getParentModel() instanceof HumanoidModel<?> baseModel) {
         this.auraCoat
            .render(pMatrixStack, entity, this.handCoat, baseModel, pBuffer, pPackedLight, pLimbSwing, pLimbSwingAmount, pPartialTicks, pNetHeadYaw, pHeadPitch);
         this.flameCoat
            .render(pMatrixStack, entity, this.handCoat, baseModel, pBuffer, pPackedLight, pLimbSwing, pLimbSwingAmount, pPartialTicks, pNetHeadYaw, pHeadPitch);
         this.lightningCoat
            .render(pMatrixStack, entity, this.handCoat, baseModel, pBuffer, pPackedLight, pLimbSwing, pLimbSwingAmount, pPartialTicks, pNetHeadYaw, pHeadPitch);
      }
   }
}
