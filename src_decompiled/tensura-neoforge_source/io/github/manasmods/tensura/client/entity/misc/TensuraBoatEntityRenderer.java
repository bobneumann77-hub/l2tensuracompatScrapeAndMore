package io.github.manasmods.tensura.client.entity.misc;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import io.github.manasmods.tensura.entity.template.TensuraBoatEntity;
import io.github.manasmods.tensura.entity.template.TensuraChestBoatEntity;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.Boat;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

public class TensuraBoatEntityRenderer extends EntityRenderer<Boat> {
   private final Map<TensuraBoatEntity.Type, Pair<ResourceLocation, ListModel<Boat>>> boatResources;

   public TensuraBoatEntityRenderer(Context context, boolean chest) {
      super(context);
      this.shadowRadius = 0.8F;
      this.boatResources = Stream.of(TensuraBoatEntity.Type.values())
         .collect(
            ImmutableMap.toImmutableMap(
               type -> type,
               type -> new Pair(ResourceLocation.fromNamespaceAndPath("tensura", getTextureLocation(type, chest)), this.createBoatModel(context, type, chest))
            )
         );
   }

   private static ModelLayerLocation createLocation(String path, String model) {
      return new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("tensura", path), model);
   }

   public static ModelLayerLocation createBoatModelName(TensuraBoatEntity.Type pType) {
      return createLocation("boat/" + pType.getName(), "main");
   }

   public static ModelLayerLocation createChestBoatModelName(TensuraBoatEntity.Type type) {
      return createLocation("chest_boat/" + type.getName(), "main");
   }

   private ListModel<Boat> createBoatModel(Context context, TensuraBoatEntity.Type type, boolean chest) {
      ModelLayerLocation modellayerlocation = chest ? createChestBoatModelName(type) : createBoatModelName(type);
      return (ListModel<Boat>)(chest ? new ChestBoatModel(context.bakeLayer(modellayerlocation)) : new BoatModel(context.bakeLayer(modellayerlocation)));
   }

   private static String getTextureLocation(TensuraBoatEntity.Type type, boolean chest) {
      return chest ? "textures/entity/chest_boat/" + type.getName() + ".png" : "textures/entity/boat/" + type.getName() + ".png";
   }

   public void render(Boat boat, float boatYaw, float partialTicks, PoseStack stack, MultiBufferSource buffer, int light) {
      stack.pushPose();
      stack.translate(0.0F, 0.375F, 0.0F);
      stack.mulPose(Axis.YP.rotationDegrees(180.0F - boatYaw));
      float f = boat.getHurtTime() - partialTicks;
      float f1 = boat.getDamage() - partialTicks;
      if (f1 < 0.0F) {
         f1 = 0.0F;
      }

      if (f > 0.0F) {
         stack.mulPose(Axis.XP.rotationDegrees(Mth.sin(f) * f * f1 / 10.0F * boat.getHurtDir()));
      }

      float f2 = boat.getBubbleAngle(partialTicks);
      if (!Mth.equal(f2, 0.0F)) {
         stack.mulPose(new Quaternionf().setAngleAxis(boat.getBubbleAngle(partialTicks) * (float) (Math.PI / 180.0), 1.0F, 0.0F, 1.0F));
      }

      Pair<ResourceLocation, ListModel<Boat>> pair = this.getModelWithLocation(boat);
      ResourceLocation resourcelocation = (ResourceLocation)pair.getFirst();
      ListModel<Boat> model = (ListModel<Boat>)pair.getSecond();
      stack.scale(-1.0F, -1.0F, 1.0F);
      stack.mulPose(Axis.YP.rotationDegrees(90.0F));
      model.setupAnim(boat, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F);
      VertexConsumer vertexconsumer = buffer.getBuffer(model.renderType(resourcelocation));
      model.renderToBuffer(stack, vertexconsumer, light, OverlayTexture.NO_OVERLAY);
      if (model instanceof BoatModel boatModel && !boat.isUnderWater()) {
         VertexConsumer consumer = buffer.getBuffer(RenderType.waterMask());
         boatModel.waterPatch().render(stack, consumer, light, OverlayTexture.NO_OVERLAY);
      }

      stack.popPose();
      super.render(boat, boatYaw, partialTicks, stack, buffer, light);
   }

   @NotNull
   public ResourceLocation getTextureLocation(Boat entity) {
      if (entity instanceof TensuraBoatEntity boat) {
         return (ResourceLocation)this.boatResources.get(boat.getTensuraBoatType()).getFirst();
      } else {
         return entity instanceof TensuraChestBoatEntity boat
            ? (ResourceLocation)this.boatResources.get(boat.getTensuraBoatType()).getFirst()
            : ResourceLocation.withDefaultNamespace("boat/oak");
      }
   }

   public Pair<ResourceLocation, ListModel<Boat>> getModelWithLocation(Boat boat) {
      if (boat instanceof TensuraBoatEntity entity) {
         return this.boatResources.get(entity.getTensuraBoatType());
      } else {
         return boat instanceof TensuraChestBoatEntity entity ? this.boatResources.get(entity.getTensuraBoatType()) : null;
      }
   }
}
