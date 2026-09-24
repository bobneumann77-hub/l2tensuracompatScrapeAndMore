package io.github.manasmods.tensura.neoforge.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.network.c2s.RequestIllusionItemPacket;
import io.github.manasmods.tensura.registry.item.misc.TensuraTrimMaterials;
import io.github.manasmods.tensura.registry.skill.UniqueSkills;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Holder;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimPattern;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
public class MixinHumanoidArmorLayer<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> {
   @Shadow
   @Final
   private TextureAtlas armorTrimAtlas;

   @Inject(
      method = "renderTrim(Lnet/minecraft/core/Holder;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/item/armortrim/ArmorTrim;Lnet/minecraft/client/model/Model;Z)V",
      at = @At("HEAD"),
      cancellable = true
   )
   private void renderTrim(
      Holder<ArmorMaterial> holder,
      PoseStack poseStack,
      MultiBufferSource multiBufferSource,
      int i,
      ArmorTrim armorTrim,
      Model humanoidModel,
      boolean bl,
      CallbackInfo ci
   ) {
      if (armorTrim.material().is(TensuraTrimMaterials.HIHIIROKANE)) {
         TextureAtlasSprite textureAtlasSprite = this.armorTrimAtlas.getSprite(bl ? armorTrim.innerTexture(holder) : armorTrim.outerTexture(holder));
         VertexConsumer vertexConsumer = textureAtlasSprite.wrap(
            multiBufferSource.getBuffer(Sheets.armorTrimsSheet(((TrimPattern)armorTrim.pattern().value()).decal()))
         );
         Player player = Minecraft.getInstance().player;
         if (player == null) {
            return;
         }

         int tickCount = player.tickCount / 25 + player.getId();
         int preColor = tickCount % DyeColor.values().length;
         int nextColor = (tickCount + 1) % DyeColor.values().length;
         float tick = player.tickCount % 25.0F / 25.0F;
         int previous = Sheep.getColor(DyeColor.byId(preColor));
         int next = Sheep.getColor(DyeColor.byId(nextColor));
         humanoidModel.renderToBuffer(poseStack, vertexConsumer, i, OverlayTexture.NO_OVERLAY, ARGB32.lerp(tick, previous, next));
         ci.cancel();
      }
   }

   @WrapOperation(
      method = "renderArmorPiece(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;ILnet/minecraft/client/model/HumanoidModel;FFFFFF)V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/LivingEntity;getItemBySlot(Lnet/minecraft/world/entity/EquipmentSlot;)Lnet/minecraft/world/item/ItemStack;"
      )
   )
   public ItemStack renderMainArmWithItem(LivingEntity entity, EquipmentSlot slot, Operation<ItemStack> original) {
      return RequestIllusionItemPacket.getFalsifierItem(
         entity, (ManasSkill)UniqueSkills.FALSIFIER.get(), (ItemStack)original.call(new Object[]{entity, slot}), slot
      );
   }
}
