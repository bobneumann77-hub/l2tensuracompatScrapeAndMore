package io.github.manasmods.tensura.client.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.tensura.client.TensuraColors;
import io.github.manasmods.tensura.client.entity.human.PlayerLikeModel;
import io.github.manasmods.tensura.entity.template.TensuraMerchantEntity;
import io.github.manasmods.tensura.entity.template.subclass.IGender;
import io.github.manasmods.tensura.registry.entity.ai.TensuraVillagerProfessions;
import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib.util.Color;

public class ProfessionClothesLayer<T extends TensuraMerchantEntity> extends RenderLayer<T, HumanoidModel<T>> {
   public static final LayerDefinition CLOTHES_LAYER = LayerDefinition.create(PlayerModel.createMesh(new CubeDeformation(0.8F), false), 64, 64);
   public static ModelLayerLocation CLOTHES = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("tensura", "profession_clothes"), "main");
   private final PlayerModel<T> model = new PlayerModel(Minecraft.getInstance().getEntityModels().bakeLayer(CLOTHES), false);
   private static final Map<VillagerProfession, ResourceLocation> PROFESSION_TEXTURES = new IdentityHashMap<>();
   private static volatile boolean PROFESSION_TEXTURES_INITIALIZED = false;
   private static final ResourceLocation EMPTY = ResourceLocation.fromNamespaceAndPath("tensura", "empty");

   public ProfessionClothesLayer(RenderLayerParent<T, HumanoidModel<T>> pRenderer) {
      super(pRenderer);
   }

   public void render(
      PoseStack pMatrixStack,
      MultiBufferSource pBuffer,
      int pPackedLight,
      T entity,
      float pLimbSwing,
      float pLimbSwingAmount,
      float partialTicks,
      float pAgeInTicks,
      float pNetHeadYaw,
      float pHeadPitch
   ) {
      if (entity.wantsToTrade()) {
         ResourceLocation location = getTexture(entity.getProfession());
         if (location != null) {
            int color = Color.WHITE.argbInt();
            if (entity.isInvisible()) {
               Player player = Minecraft.getInstance().player;
               if (player == null || entity.isInvisibleTo(player)) {
                  return;
               }

               color = TensuraColors.getARGBWithAlpha(color, 0.1F);
            }

            HumanoidModel<T> model = this.model();
            model.prepareMobModel(entity, pLimbSwing, pLimbSwingAmount, partialTicks);
            ((HumanoidModel)this.getParentModel()).copyPropertiesTo(model);
            VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.entityTranslucent(location));
            model.setupAnim(entity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
            PlayerLikeModel.sittingPose(entity, model);
            pMatrixStack.pushPose();
            model.renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, color);
            pMatrixStack.popPose();
         }
      }
   }

   protected HumanoidModel<T> model() {
      return this.model;
   }

   private static void ensureTexturesInit() {
      if (!PROFESSION_TEXTURES_INITIALIZED) {
         synchronized (PROFESSION_TEXTURES) {
            if (!PROFESSION_TEXTURES_INITIALIZED) {
               PROFESSION_TEXTURES.put((VillagerProfession)TensuraVillagerProfessions.ROYAL_GUARD.get(), EMPTY);
               PROFESSION_TEXTURES.put(
                  VillagerProfession.ARMORER, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/dwarf/profession/armorer.png")
               );
               PROFESSION_TEXTURES.put(
                  (VillagerProfession)TensuraVillagerProfessions.BATTLEWILL_TRAINER.get(),
                  ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/dwarf/profession/battlewill_trainer.png")
               );
               PROFESSION_TEXTURES.put(
                  VillagerProfession.BUTCHER, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/dwarf/profession/butcher.png")
               );
               PROFESSION_TEXTURES.put(
                  VillagerProfession.CARTOGRAPHER, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/dwarf/profession/cartographer.png")
               );
               PROFESSION_TEXTURES.put(
                  VillagerProfession.CLERIC, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/dwarf/profession/alchemist.png")
               );
               PROFESSION_TEXTURES.put(
                  VillagerProfession.FARMER, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/dwarf/profession/farmer.png")
               );
               PROFESSION_TEXTURES.put(
                  VillagerProfession.FISHERMAN, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/dwarf/profession/fisherman.png")
               );
               PROFESSION_TEXTURES.put(
                  VillagerProfession.FLETCHER, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/dwarf/profession/fletcher.png")
               );
               PROFESSION_TEXTURES.put(
                  (VillagerProfession)TensuraVillagerProfessions.GUARD.get(),
                  ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/dwarf/profession/guard.png")
               );
               PROFESSION_TEXTURES.put(
                  VillagerProfession.LEATHERWORKER, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/dwarf/profession/leatherworker.png")
               );
               PROFESSION_TEXTURES.put(
                  VillagerProfession.LIBRARIAN, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/dwarf/profession/librarian.png")
               );
               PROFESSION_TEXTURES.put(
                  (VillagerProfession)TensuraVillagerProfessions.LUMBERJACK.get(),
                  ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/dwarf/profession/lumberjack.png")
               );
               PROFESSION_TEXTURES.put(
                  (VillagerProfession)TensuraVillagerProfessions.MAGIC_TRAINER.get(),
                  ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/dwarf/profession/magic_trainer.png")
               );
               PROFESSION_TEXTURES.put(VillagerProfession.MASON, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/dwarf/profession/mason.png"));
               PROFESSION_TEXTURES.put(
                  (VillagerProfession)TensuraVillagerProfessions.MERCHANT.get(),
                  ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/dwarf/profession/merchant.png")
               );
               PROFESSION_TEXTURES.put(
                  (VillagerProfession)TensuraVillagerProfessions.MINER.get(),
                  ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/dwarf/profession/miner.png")
               );
               PROFESSION_TEXTURES.put(
                  VillagerProfession.SHEPHERD, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/dwarf/profession/shepherd.png")
               );
               PROFESSION_TEXTURES.put(
                  VillagerProfession.TOOLSMITH, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/dwarf/profession/toolsmith.png")
               );
               PROFESSION_TEXTURES.put(
                  VillagerProfession.WEAPONSMITH, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/dwarf/profession/weaponsmith.png")
               );
               PROFESSION_TEXTURES_INITIALIZED = true;
            }
         }
      }
   }

   static ResourceLocation getTexture(VillagerProfession profession) {
      ensureTexturesInit();
      ResourceLocation result = PROFESSION_TEXTURES.get(profession);
      return result == EMPTY ? null : result;
   }

   public static class Chest<T extends TensuraMerchantEntity> extends RenderLayer<T, HumanoidModel<T>> {
      public static final LayerDefinition CHEST_LAYER = HumanoidChestModel.createBodyLayer(0.5F);
      public static ModelLayerLocation CHEST = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("tensura", "profession_clothes_chest"), "main");
      private final HumanoidChestModel<T> model = new HumanoidChestModel(Minecraft.getInstance().getEntityModels().bakeLayer(CHEST));

      public Chest(RenderLayerParent<T, HumanoidModel<T>> pRenderer) {
         super(pRenderer);
      }

      public void render(
         PoseStack pMatrixStack,
         MultiBufferSource pBuffer,
         int pPackedLight,
         T entity,
         float pLimbSwing,
         float pLimbSwingAmount,
         float partialTicks,
         float pAgeInTicks,
         float pNetHeadYaw,
         float pHeadPitch
      ) {
         if (!entity.isBaby()) {
            if (entity instanceof IGender gender && gender.isFemale()) {
               if (entity.wantsToTrade()) {
                  ResourceLocation location = ProfessionClothesLayer.getTexture(entity.getProfession());
                  if (location != null) {
                     int color = Color.WHITE.argbInt();
                     if (entity.isInvisible()) {
                        Player player = Minecraft.getInstance().player;
                        if (player == null || entity.isInvisibleTo(player)) {
                           return;
                        }

                        color = TensuraColors.getARGBWithAlpha(color, 0.1F);
                     }

                     ModelPart parentBody = ((HumanoidModel)this.getParentModel()).body;
                     this.model.body.xRot = parentBody.xRot;
                     this.model.body.yRot = parentBody.yRot;
                     this.model.body.zRot = parentBody.zRot;
                     VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.entityTranslucent(location));
                     pMatrixStack.pushPose();
                     this.model.renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, color);
                     pMatrixStack.popPose();
                  }
               }
            }
         }
      }
   }
}
