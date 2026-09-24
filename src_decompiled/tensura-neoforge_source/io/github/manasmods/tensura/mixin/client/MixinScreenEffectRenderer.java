package io.github.manasmods.tensura.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.manasmods.tensura.ability.SkillClientUtils;
import io.github.manasmods.tensura.storage.TensuraStorages;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenEffectRenderer.class)
public abstract class MixinScreenEffectRenderer {
   @Unique
   private static final Material BLACK_FIRE_1 = new Material(
      TextureAtlas.LOCATION_BLOCKS, ResourceLocation.fromNamespaceAndPath("tensura", "block/black_fire_1")
   );

   @Inject(method = "renderFire(Lnet/minecraft/client/Minecraft;Lcom/mojang/blaze3d/vertex/PoseStack;)V", at = @At("HEAD"), cancellable = true)
   private static void renderFire(Minecraft minecraft, PoseStack poseStack, CallbackInfo ci) {
      if (minecraft.player != null && SkillClientUtils.shouldCancelFireOverlay(minecraft.player)) {
         ci.cancel();
      }
   }

   @ModifyVariable(
      method = "renderFire(Lnet/minecraft/client/Minecraft;Lcom/mojang/blaze3d/vertex/PoseStack;)V",
      at = @At(
         value = "INVOKE_ASSIGN",
         target = "Lnet/minecraft/client/resources/model/Material;sprite()Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;"
      )
   )
   private static TextureAtlasSprite onRenderFire(TextureAtlasSprite value, Minecraft minecraft, PoseStack poseStack) {
      return minecraft.player != null && TensuraStorages.getEffectFrom(minecraft.player).isOnBlackFlame() ? BLACK_FIRE_1.sprite() : value;
   }
}
