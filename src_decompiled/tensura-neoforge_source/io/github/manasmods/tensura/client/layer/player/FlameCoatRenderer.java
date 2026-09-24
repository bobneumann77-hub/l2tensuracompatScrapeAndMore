package io.github.manasmods.tensura.client.layer.player;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.skill.extra.FlameDominationSkill;
import io.github.manasmods.tensura.ability.skill.extra.FlameManipulationSkill;
import io.github.manasmods.tensura.client.layer.template.DefaultedHumanoidLayerModel;
import io.github.manasmods.tensura.client.layer.template.GeoHumanoidLayerRenderer;
import io.github.manasmods.tensura.client.layer.template.HandCoatGeo;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.skill.UniqueSkills;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.texture.AnimatableTexture;
import software.bernie.geckolib.model.GeoModel;

public class FlameCoatRenderer extends GeoHumanoidLayerRenderer<HandCoatGeo> {
   protected boolean armored = false;
   protected final GeoModel<HandCoatGeo> armoredModel = new DefaultedHumanoidLayerModel<HandCoatGeo>(
      ResourceLocation.fromNamespaceAndPath("tensura", "flame_coat_armor")
   );

   public FlameCoatRenderer() {
      super(new DefaultedHumanoidLayerModel<HandCoatGeo>(ResourceLocation.fromNamespaceAndPath("tensura", "flame_coat")));
      AnimatableTexture.setAndUpdate(ResourceLocation.fromNamespaceAndPath("tensura", "textures/models/layer/geo/flame_coat.png"));
      AnimatableTexture.setAndUpdate(ResourceLocation.fromNamespaceAndPath("tensura", "textures/models/layer/geo/black_flame_coat.png"));
   }

   @Override
   public GeoModel<HandCoatGeo> getGeoModel() {
      return this.armored ? this.armoredModel : super.getGeoModel();
   }

   public ResourceLocation getTextureLocation(HandCoatGeo animatable) {
      return this.getCurrentEntity() != null && SkillUtils.isSkillToggled(this.getCurrentEntity(), (ManasSkill)ExtraSkills.BLACK_FLAME.get())
         ? ResourceLocation.fromNamespaceAndPath("tensura", "textures/models/layer/geo/black_flame_coat.png")
         : ResourceLocation.fromNamespaceAndPath("tensura", "textures/models/layer/geo/flame_coat.png");
   }

   @Override
   public boolean shouldRender(LivingEntity entity) {
      if (!SkillUtils.isSkillToggled(entity, (ManasSkill)ExtraSkills.BLACK_FLAME.get())
         && !SkillUtils.isSkillToggled(entity, (ManasSkill)UniqueSkills.BERSERK.get())) {
         return (
                  !((FlameManipulationSkill)ExtraSkills.FLAME_MANIPULATION.get()).isInSlot(entity)
                     || !SkillUtils.hasSkill(entity, (ManasSkill)ExtraSkills.FLAME_MANIPULATION.get())
               )
               && (
                  !((FlameDominationSkill)ExtraSkills.FLAME_DOMINATION.get()).isInSlot(entity)
                     || !SkillUtils.hasSkill(entity, (ManasSkill)ExtraSkills.FLAME_DOMINATION.get())
               )
            ? false
            : FlameManipulationSkill.canUseFire(entity);
      } else {
         return true;
      }
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
