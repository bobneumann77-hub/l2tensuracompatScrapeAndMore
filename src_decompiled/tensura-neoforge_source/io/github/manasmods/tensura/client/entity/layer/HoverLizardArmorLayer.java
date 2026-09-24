package io.github.manasmods.tensura.client.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.entity.monster.HoverLizardEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.AnimalArmorItem.BodyType;
import net.minecraft.world.item.component.DyedItemColor;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class HoverLizardArmorLayer extends GeoRenderLayer<HoverLizardEntity> {
   public HoverLizardArmorLayer(GeoRenderer<HoverLizardEntity> renderer) {
      super(renderer);
   }

   public ResourceLocation getTextureResource(ItemStack itemStack) {
      if (itemStack.is(Items.DIAMOND_HORSE_ARMOR)) {
         return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/hover_lizard/diamond_armor.png");
      } else if (itemStack.is(Items.GOLDEN_HORSE_ARMOR)) {
         return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/hover_lizard/golden_armor.png");
      } else {
         return itemStack.is(Items.IRON_HORSE_ARMOR)
            ? ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/hover_lizard/iron_armor.png")
            : ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/hover_lizard/leather_armor.png");
      }
   }

   public void render(
      PoseStack poseStack,
      HoverLizardEntity lizard,
      BakedGeoModel bakedModel,
      RenderType renderType,
      MultiBufferSource bufferSource,
      VertexConsumer buffer,
      float partialTick,
      int packedLight,
      int packedOverlay
   ) {
      ItemStack itemStack = lizard.getBodyArmorItem();
      if (itemStack.getItem() instanceof AnimalArmorItem animalArmorItem && animalArmorItem.getBodyType() == BodyType.EQUESTRIAN) {
         int color = -1;
         if (itemStack.is(ItemTags.DYEABLE)) {
            color = ARGB32.opaque(DyedItemColor.getOrDefault(itemStack, -6265536));
         }

         RenderType type = RenderType.entityTranslucent(this.getTextureResource(itemStack));
         this.getRenderer()
            .reRender(
               this.getDefaultBakedModel(lizard),
               poseStack,
               bufferSource,
               lizard,
               type,
               bufferSource.getBuffer(type),
               partialTick,
               packedLight,
               OverlayTexture.NO_OVERLAY,
               color
            );
      }
   }
}
