package io.github.manasmods.tensura.client.entity.projectile;

import io.github.manasmods.tensura.entity.projectile.SpearedFinArrow;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;

public class SpearedFinArrowRenderer extends ArrowRenderer<SpearedFinArrow> {
   public SpearedFinArrowRenderer(Context pContext) {
      super(pContext);
   }

   public ResourceLocation getTextureLocation(SpearedFinArrow pEntity) {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/projectiles/speared_fin_arrow.png");
   }
}
