package io.github.manasmods.tensura.client.entity.misc;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.entity.magic.lightning.TensuraLightningBolt;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import org.joml.Matrix4f;

public class LightningBoltRenderer<T extends TensuraLightningBolt> extends EntityRenderer<T> {
   public LightningBoltRenderer(Context pContext) {
      super(pContext);
   }

   protected int getBlockLightLevel(T entity, BlockPos blockPos) {
      return 15;
   }

   public void render(T pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
      VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.lightning());
      Matrix4f matrix4f = pMatrixStack.last().pose();

      for (int j = 0; j < 4; j++) {
         for (int k = 0; k < 3; k++) {
            int l = 7;
            int i1 = 0;
            if (k > 0) {
               l = 7 - k;
            }

            if (k > 0) {
               i1 = l - 2;
            }

            for (int y = l; y >= i1; y--) {
               float f10 = 0.1F + j * 0.2F;
               if (k == 0) {
                  f10 *= y * 0.1F + 1.0F;
               }

               float f11 = 0.1F + j * 0.2F;
               if (k == 0) {
                  f11 *= (y - 1.0F) * 0.1F + 1.0F;
               }

               float red = 0.45F;
               float green = 0.45F;
               float blue = 0.5F;
               quad(matrix4f, vertexconsumer, 0.0F, 0.0F, y, 0.0F, 0.0F, red, green, blue, f10, f11, false, false, true, false);
               quad(matrix4f, vertexconsumer, 0.0F, 0.0F, y, 0.0F, 0.0F, red, green, blue, f10, f11, true, false, true, true);
               quad(matrix4f, vertexconsumer, 0.0F, 0.0F, y, 0.0F, 0.0F, red, green, blue, f10, f11, true, true, false, true);
               quad(matrix4f, vertexconsumer, 0.0F, 0.0F, y, 0.0F, 0.0F, red, green, blue, f10, f11, false, true, false, false);
            }
         }
      }

      for (int m = 0; m < pEntity.getAdditionalVisual(); m++) {
         renderAdditionalBolt(pMatrixStack, pBuffer, pEntity.level().getRandom().nextLong());
      }
   }

   protected static void renderAdditionalBolt(PoseStack pMatrixStack, MultiBufferSource pBuffer, long seed) {
      float[] afloat = new float[8];
      float[] afloat1 = new float[8];
      float f = 0.0F;
      float f1 = 0.0F;
      RandomSource source = RandomSource.create(seed);

      for (int i = 7; i >= 0; i--) {
         afloat[i] = f;
         afloat1[i] = f1;
         f += source.nextInt(11) - 5;
         f1 += source.nextInt(11) - 5;
      }

      VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.lightning());
      Matrix4f matrix4f = pMatrixStack.last().pose();

      for (int j = 0; j < 4; j++) {
         RandomSource randomSource = RandomSource.create(seed);

         for (int k = 0; k < 3; k++) {
            int l = 7;
            int i1 = 0;
            if (k > 0) {
               l = 7 - k;
            }

            if (k > 0) {
               i1 = l - 2;
            }

            float z = afloat[l] - f;
            float x = afloat1[l] - f1;

            for (int y = l; y >= i1; y--) {
               float z2 = z;
               float x2 = x;
               if (k == 0) {
                  z += randomSource.nextInt(11) - 5;
                  x += randomSource.nextInt(11) - 5;
               } else {
                  z += randomSource.nextInt(31) - 15;
                  x += randomSource.nextInt(31) - 15;
               }

               float f10 = 0.1F + j * 0.2F;
               if (k == 0) {
                  f10 *= y * 0.1F + 1.0F;
               }

               float f11 = 0.1F + j * 0.2F;
               if (k == 0) {
                  f11 *= (y - 1.0F) * 0.1F + 1.0F;
               }

               float red = 0.45F;
               float green = 0.45F;
               float blue = 0.5F;
               quad(matrix4f, vertexconsumer, z, x, y, z2, x2, red, green, blue, f10, f11, false, false, true, false);
               quad(matrix4f, vertexconsumer, z, x, y, z2, x2, red, green, blue, f10, f11, true, false, true, true);
               quad(matrix4f, vertexconsumer, z, x, y, z2, x2, red, green, blue, f10, f11, true, true, false, true);
               quad(matrix4f, vertexconsumer, z, x, y, z2, x2, red, green, blue, f10, f11, false, true, false, false);
            }
         }
      }
   }

   private static void quad(
      Matrix4f matrix4f,
      VertexConsumer vertexConsumer,
      float f,
      float g,
      int i,
      float h,
      float j,
      float k,
      float l,
      float m,
      float n,
      float o,
      boolean bl,
      boolean bl2,
      boolean bl3,
      boolean bl4
   ) {
      vertexConsumer.addVertex(matrix4f, f + (bl ? o : -o), i * 16, g + (bl2 ? o : -o)).setColor(k, l, m, 0.3F);
      vertexConsumer.addVertex(matrix4f, h + (bl ? n : -n), (i + 1) * 16, j + (bl2 ? n : -n)).setColor(k, l, m, 0.3F);
      vertexConsumer.addVertex(matrix4f, h + (bl3 ? n : -n), (i + 1) * 16, j + (bl4 ? n : -n)).setColor(k, l, m, 0.3F);
      vertexConsumer.addVertex(matrix4f, f + (bl3 ? o : -o), i * 16, g + (bl4 ? o : -o)).setColor(k, l, m, 0.3F);
   }

   public ResourceLocation getTextureLocation(T pEntity) {
      return TextureAtlas.LOCATION_BLOCKS;
   }
}
