package io.github.manasmods.tensura.client.entity.misc;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.entity.magic.misc.NonPlayerFishingHook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NonPlayerFishingHookRenderer extends EntityRenderer<NonPlayerFishingHook> {
   private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/fishing_hook.png");
   private static final RenderType RENDER_TYPE = RenderType.entityCutout(TEXTURE_LOCATION);
   private static final double VIEW_BOBBING_SCALE = 960.0;

   public NonPlayerFishingHookRenderer(Context context) {
      super(context);
   }

   public void render(NonPlayerFishingHook NonPlayerFishingHook, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
      if (NonPlayerFishingHook.getOwner() instanceof LivingEntity living) {
         poseStack.pushPose();
         poseStack.pushPose();
         poseStack.scale(0.5F, 0.5F, 0.5F);
         poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
         Pose pose = poseStack.last();
         VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RENDER_TYPE);
         vertex(vertexConsumer, pose, i, 0.0F, 0, 0, 1);
         vertex(vertexConsumer, pose, i, 1.0F, 0, 1, 1);
         vertex(vertexConsumer, pose, i, 1.0F, 1, 1, 0);
         vertex(vertexConsumer, pose, i, 0.0F, 1, 0, 0);
         poseStack.popPose();
         float h = living.getAttackAnim(g);
         float j = Mth.sin(Mth.sqrt(h) * (float) Math.PI);
         Vec3 vec3 = this.getPlayerHandPos(living, j, g);
         Vec3 vec32 = NonPlayerFishingHook.getPosition(g).add(0.0, 0.25, 0.0);
         float k = (float)(vec3.x - vec32.x);
         float l = (float)(vec3.y - vec32.y);
         float m = (float)(vec3.z - vec32.z);
         VertexConsumer vertexConsumer2 = multiBufferSource.getBuffer(RenderType.lineStrip());
         Pose pose2 = poseStack.last();

         for (int o = 0; o <= 16; o++) {
            stringVertex(k, l, m, vertexConsumer2, pose2, fraction(o, 16), fraction(o + 1, 16));
         }

         poseStack.popPose();
         super.render(NonPlayerFishingHook, f, g, poseStack, multiBufferSource, i);
      }
   }

   private Vec3 getPlayerHandPos(LivingEntity player, float f, float g) {
      int i = player.getMainArm() == HumanoidArm.RIGHT ? 1 : -1;
      ItemStack itemStack = player.getMainHandItem();
      if (!itemStack.is(TensuraItemTags.FISHING_RODS)) {
         i = -i;
      }

      if (this.entityRenderDispatcher.options.getCameraType().isFirstPerson() && player == Minecraft.getInstance().player) {
         double n = 960.0 / ((Integer)this.entityRenderDispatcher.options.fov().get()).intValue();
         Vec3 vec3 = this.entityRenderDispatcher.camera.getNearPlane().getPointOnPlane(i * 0.525F, -0.1F).scale(n).yRot(f * 0.5F).xRot(-f * 0.7F);
         return player.getEyePosition(g).add(vec3);
      } else {
         float h = Mth.lerp(g, player.yBodyRotO, player.yBodyRot) * (float) (Math.PI / 180.0);
         double d = Mth.sin(h);
         double e = Mth.cos(h);
         float j = player.getScale();
         double k = i * 0.35 * j;
         double l = 0.8 * j;
         float m = player.isCrouching() ? -0.1875F : 0.0F;
         return player.getEyePosition(g).add(-e * k - d * l, m - 0.45 * j, -d * k + e * l);
      }
   }

   private static float fraction(int i, int j) {
      return (float)i / j;
   }

   private static void vertex(VertexConsumer vertexConsumer, Pose pose, int i, float f, int j, int k, int l) {
      vertexConsumer.addVertex(pose, f - 0.5F, j - 0.5F, 0.0F)
         .setColor(-1)
         .setUv(k, l)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(i)
         .setNormal(pose, 0.0F, 1.0F, 0.0F);
   }

   private static void stringVertex(float f, float g, float h, VertexConsumer vertexConsumer, Pose pose, float i, float j) {
      float k = f * i;
      float l = g * (i * i + i) * 0.5F + 0.25F;
      float m = h * i;
      float n = f * j - k;
      float o = g * (j * j + j) * 0.5F + 0.25F - l;
      float p = h * j - m;
      float q = Mth.sqrt(n * n + o * o + p * p);
      n /= q;
      o /= q;
      p /= q;
      vertexConsumer.addVertex(pose, k, l, m).setColor(-16777216).setNormal(pose, n, o, p);
   }

   public ResourceLocation getTextureLocation(NonPlayerFishingHook NonPlayerFishingHook) {
      return TEXTURE_LOCATION;
   }
}
