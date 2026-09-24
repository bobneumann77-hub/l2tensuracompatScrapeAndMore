package io.github.manasmods.tensura.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.manasmods.tensura.ability.SkillClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {
   @ModifyReturnValue(method = "shouldEntityAppearGlowing(Lnet/minecraft/world/entity/Entity;)Z", at = @At("RETURN"))
   public boolean setGlowing(boolean original, Entity pEntity) {
      if (original) {
         return true;
      }

      Player player = ((Minecraft)this).player;
      return player != null && SkillClientUtils.getGlowColor(player, pEntity) != -1 ? true : original;
   }
}
