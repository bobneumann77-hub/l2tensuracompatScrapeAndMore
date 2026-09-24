package io.github.manasmods.tensura.neoforge.mixin.client;

import java.util.Map;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Options.class)
public abstract class MixinOptionsForge {
   @Shadow
   public abstract OptionInstance<Double> getSoundSourceOptionInstance(SoundSource var1);

   @Redirect(
      method = "processOptionsForge(Lnet/minecraft/client/Options$FieldAccess;)V",
      at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"),
      require = 0
   )
   private Object tensura$processForgeSoundSourceLookup(Map<SoundSource, OptionInstance<Double>> map, Object key) {
      return key instanceof SoundSource source ? this.getSoundSourceOptionInstance(source) : map.get(key);
   }
}
