package io.github.manasmods.tensura.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.manasmods.tensura.ability.SkillClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.At.Shift;

@Mixin(LevelRenderer.class)
public abstract class MixinLevelRenderer {
   @ModifyVariable(
      method = "renderLevel(Lnet/minecraft/client/DeltaTracker;ZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V",
      at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/entity/Entity;getTeamColor()I", shift = Shift.AFTER)
   )
   private int additional_enchantments$getTypeColor(int color, @Local Entity entity) {
      Player player = Minecraft.getInstance().player;
      if (color == 16777215 && player != null) {
         int glowColor = SkillClientUtils.getGlowColor(player, entity);
         if (glowColor != -1) {
            return glowColor;
         }
      }

      return color;
   }
}
