package io.github.manasmods.tensura.mixin.client;

import io.github.manasmods.tensura.client.TensuraClient;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelBakery.class)
public abstract class MixinModelBakery {
   @Shadow
   protected abstract void loadItemModelAndDependencies(ResourceLocation var1);

   @Inject(
      method = "<init>(Lnet/minecraft/client/color/block/BlockColors;Lnet/minecraft/util/profiling/ProfilerFiller;Ljava/util/Map;Ljava/util/Map;)V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/resources/model/ModelBakery;loadSpecialItemModelAndDependencies(Lnet/minecraft/client/resources/model/ModelResourceLocation;)V",
         ordinal = 1
      )
   )
   private void onInit(CallbackInfo ci) {
      for (String string : TensuraClient.CUSTOM_GUI_MODEL_ITEMS) {
         ResourceLocation location = ResourceLocation.tryParse(string);
         if (location != null) {
            this.loadItemModelAndDependencies(ResourceLocation.fromNamespaceAndPath(location.getNamespace(), location.getPath() + "_gui"));
         }
      }
   }
}
