package io.github.manasmods.tensura.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.manasmods.tensura.client.TensuraClient;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = ItemRenderer.class, priority = 2000)
public abstract class MixinItemRenderer {
   @Shadow
   @Final
   private ItemModelShaper itemModelShaper;

   @ModifyVariable(
      method = "render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V",
      at = @At("HEAD"),
      argsOnly = true,
      index = 8
   )
   public BakedModel renderItem(BakedModel model, @Local(argsOnly = true) ItemStack itemStack, @Local(argsOnly = true) ItemDisplayContext context) {
      if (context == ItemDisplayContext.GUI || context == ItemDisplayContext.GROUND) {
         ResourceLocation location = itemStack.getItem().arch$registryName();
         if (location != null && TensuraClient.CUSTOM_GUI_MODEL_ITEMS.contains(location.toString())) {
            return this.itemModelShaper
               .getModelManager()
               .getModel(ModelResourceLocation.inventory(ResourceLocation.fromNamespaceAndPath(location.getNamespace(), location.getPath() + "_gui")));
         }
      }

      return model;
   }
}
