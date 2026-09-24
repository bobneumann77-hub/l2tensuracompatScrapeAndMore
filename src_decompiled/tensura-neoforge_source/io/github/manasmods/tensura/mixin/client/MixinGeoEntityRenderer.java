package io.github.manasmods.tensura.mixin.client;

import io.github.manasmods.tensura.client.layer.template.LayerHolder;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@Mixin(GeoEntityRenderer.class)
public abstract class MixinGeoEntityRenderer<T extends LivingEntity & GeoEntity> {
   @Inject(
      method = "<init>(Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;Lsoftware/bernie/geckolib/model/GeoModel;)V",
      at = @At("RETURN")
   )
   private void onConstruct(Context renderManager, GeoModel<T> model, CallbackInfo ci) {
      GeoEntityRenderer<T> renderLayer = (GeoEntityRenderer<T>)this;
      renderLayer.addRenderLayer(new LayerHolder(renderLayer));
   }
}
