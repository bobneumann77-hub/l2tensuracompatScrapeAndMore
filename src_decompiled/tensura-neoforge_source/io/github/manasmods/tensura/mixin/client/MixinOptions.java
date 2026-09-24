package io.github.manasmods.tensura.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.player.ITensuraPlayer;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Options.class)
public abstract class MixinOptions {
   @Shadow
   @Final
   private Map<SoundSource, OptionInstance<Double>> soundSourceVolumes;
   @Unique
   private final Map<SoundSource, OptionInstance<Double>> tensura$extraSoundVolumes = new HashMap<>();

   @Shadow
   protected abstract OptionInstance<Double> createSoundSliderOptionInstance(String var1, SoundSource var2);

   @ModifyReturnValue(method = "getCameraType()Lnet/minecraft/client/CameraType;", at = @At("RETURN"))
   public CameraType getCameraType(CameraType original) {
      Player player = Minecraft.getInstance().player;
      if (player == null) {
         return original;
      }

      ITensuraPlayer data = TensuraStorages.getPlayerDataFrom(player);
      return data.isForcedThirdPerson() ? CameraType.THIRD_PERSON_BACK : original;
   }

   @Shadow
   public abstract OptionInstance<Double> getSoundSourceOptionInstance(SoundSource var1);

   @Redirect(
      method = "processOptions(Lnet/minecraft/client/Options$FieldAccess;)V",
      at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"),
      require = 0
   )
   private Object tensura$processSoundSourceLookup(Map<SoundSource, OptionInstance<Double>> map, Object key) {
      return key instanceof SoundSource source ? this.getSoundSourceOptionInstance(source) : map.get(key);
   }

   @Inject(
      method = "getSoundSourceOptionInstance(Lnet/minecraft/sounds/SoundSource;)Lnet/minecraft/client/OptionInstance;",
      at = @At("HEAD"),
      cancellable = true
   )
   private void tensura$lazyAddSoundSource(SoundSource source, CallbackInfoReturnable<OptionInstance<Double>> cir) {
      OptionInstance<Double> existing;
      try {
         existing = this.soundSourceVolumes.get(source);
      } catch (Throwable ignored) {
         existing = null;
      }

      if (existing != null) {
         cir.setReturnValue(existing);
      } else {
         OptionInstance<Double> cached = this.tensura$extraSoundVolumes.get(source);
         if (cached != null) {
            cir.setReturnValue(cached);
         } else {
            OptionInstance<Double> created = this.createSoundSliderOptionInstance("soundCategory." + source.getName(), source);
            this.tensura$extraSoundVolumes.put(source, created);

            try {
               this.soundSourceVolumes.put(source, created);
            } catch (Throwable var7) {
            }

            cir.setReturnValue(created);
         }
      }
   }
}
