package io.github.manasmods.tensura.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.entity.template.TensuraPartEntity;
import io.github.manasmods.tensura.entity.template.subclass.IMultipart;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.storage.TensuraStorages;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class MixinEntityRenderDispatcher {
   @Unique
   private static final Material BLACK_FIRE_0 = new Material(
      TextureAtlas.LOCATION_BLOCKS, ResourceLocation.fromNamespaceAndPath("tensura", "block/black_fire_0")
   );
   @Unique
   private static final Material BLACK_FIRE_1 = new Material(
      TextureAtlas.LOCATION_BLOCKS, ResourceLocation.fromNamespaceAndPath("tensura", "block/black_fire_1")
   );

   @Inject(
      method = "renderFlame(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/Entity;Lorg/joml/Quaternionf;)V",
      at = @At("HEAD"),
      cancellable = true
   )
   private void renderFlame(PoseStack poseStack, MultiBufferSource multiBufferSource, Entity entity, Quaternionf quaternionf, CallbackInfo ci) {
      if (entity instanceof LivingEntity living && living.getAttributeValue(TensuraAttributes.PRESENCE_CONCEALMENT) >= 1.0) {
         ci.cancel();
      }
   }

   @ModifyVariable(
      method = "renderFlame(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/Entity;Lorg/joml/Quaternionf;)V",
      at = @At(
         value = "INVOKE_ASSIGN",
         target = "Lnet/minecraft/client/resources/model/Material;sprite()Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;",
         ordinal = 0
      ),
      ordinal = 0
   )
   public TextureAtlasSprite onRenderFlameAtSprite0(TextureAtlasSprite value, PoseStack poseStack, MultiBufferSource multiBufferSource, Entity entity) {
      return entity instanceof LivingEntity living && TensuraStorages.getEffectFrom(living).isOnBlackFlame() ? BLACK_FIRE_0.sprite() : value;
   }

   @ModifyVariable(
      method = "renderFlame(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/Entity;Lorg/joml/Quaternionf;)V",
      at = @At(
         value = "INVOKE_ASSIGN",
         target = "Lnet/minecraft/client/resources/model/Material;sprite()Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;",
         ordinal = 1
      ),
      ordinal = 1
   )
   public TextureAtlasSprite onRenderFlameAtSprite1(TextureAtlasSprite value, PoseStack poseStack, MultiBufferSource multiBufferSource, Entity entity) {
      return entity instanceof LivingEntity living && TensuraStorages.getEffectFrom(living).isOnBlackFlame() ? BLACK_FIRE_1.sprite() : value;
   }

   @Inject(
      method = "renderHitbox(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/entity/Entity;FFFF)V",
      at = @At("HEAD")
   )
   private static void renderHitbox(
      PoseStack poseStack, VertexConsumer vertexConsumer, Entity entity, float partialTicks, float r, float g, float b, CallbackInfo ci
   ) {
      if (entity instanceof IMultipart multipart) {
         double x = -tensura$lerp(partialTicks, entity.xOld, entity.getX());
         double y = -tensura$lerp(partialTicks, entity.yOld, entity.getY());
         double z = -tensura$lerp(partialTicks, entity.zOld, entity.getZ());
         TensuraPartEntity[] parts = multipart.getParts();

         for (TensuraPartEntity part : parts) {
            poseStack.pushPose();
            double xPos = x + tensura$lerp(partialTicks, part.xOld, part.getX());
            double yPos = y + tensura$lerp(partialTicks, part.yOld, part.getY());
            double zPos = z + tensura$lerp(partialTicks, part.zOld, part.getZ());
            poseStack.translate(xPos, yPos, zPos);
            LevelRenderer.renderLineBox(
               poseStack, vertexConsumer, part.getBoundingBox().move(-part.getX(), -part.getY(), -part.getZ()), 0.25F, 1.0F, 0.0F, 1.0F
            );
            poseStack.popPose();
         }
      }
   }

   @Unique
   private static double tensura$lerp(double delta, double start, double end) {
      return start + delta * (end - start);
   }
}
