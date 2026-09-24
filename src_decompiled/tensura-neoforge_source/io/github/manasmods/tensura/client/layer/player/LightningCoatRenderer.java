package io.github.manasmods.tensura.client.layer.player;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.client.layer.template.DefaultedHumanoidLayerModel;
import io.github.manasmods.tensura.client.layer.template.GeoHumanoidLayerRenderer;
import io.github.manasmods.tensura.client.layer.template.HandCoatGeo;
import io.github.manasmods.tensura.registry.magic.AspectualMagics;
import java.util.Optional;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.texture.AnimatableTexture;
import software.bernie.geckolib.model.GeoModel;

public class LightningCoatRenderer extends GeoHumanoidLayerRenderer<HandCoatGeo> {
   protected boolean armored = false;
   protected final GeoModel<HandCoatGeo> armoredModel = new DefaultedHumanoidLayerModel<HandCoatGeo>(
      ResourceLocation.fromNamespaceAndPath("tensura", "flame_coat_armor")
   );

   public LightningCoatRenderer() {
      super(new DefaultedHumanoidLayerModel<HandCoatGeo>(ResourceLocation.fromNamespaceAndPath("tensura", "flame_coat")));
      AnimatableTexture.setAndUpdate(ResourceLocation.fromNamespaceAndPath("tensura", "textures/models/layer/geo/lightning_coat.png"));
   }

   @Override
   public GeoModel<HandCoatGeo> getGeoModel() {
      return this.armored ? this.armoredModel : super.getGeoModel();
   }

   public ResourceLocation getTextureLocation(HandCoatGeo animatable) {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/models/layer/geo/lightning_coat.png");
   }

   @Override
   public boolean shouldRender(LivingEntity entity) {
      Optional<ManasSkillInstance> instance = SkillAPI.getSkillsFrom(entity).getSkill((ManasSkill)AspectualMagics.THUNDER_RAIN.get());
      if (instance.isEmpty()) {
         return false;
      }

      CompoundTag tag = instance.get().getTag();
      return tag == null ? false : tag.getInt("CoatTime") > 0;
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
         this.armored = !entity.getItemBySlot(EquipmentSlot.CHEST).isEmpty();
         this.prepForRender(entity, animatable, baseModel, pBuffer, pPartialTicks, pLimbSwing, pLimbSwingAmount, pNetHeadYaw, pHeadPitch);
         this.renderToBuffer(pMatrixStack, pBuffer, null, pPackedLight);
      }
   }
}
