package io.github.manasmods.tensura.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.manasmods.tensura.entity.template.subclass.ISubordinate;
import io.github.manasmods.tensura.event.TensuraEntityEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import java.util.UUID;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TamableAnimal.class)
public class MixinTamableAnimal implements ISubordinate {
   @ModifyReturnValue(method = "getOwnerUUID()Ljava/util/UUID;", at = @At("RETURN"))
   public UUID getOwnerUUID(UUID original) {
      TamableAnimal animal = (TamableAnimal)this;
      IExistence existence = TensuraStorages.getExistenceFrom(animal);
      UUID temporary = existence.getTemporaryOwner();
      if (temporary != null) {
         return temporary;
      }

      UUID summoner = existence.getSummoner();
      if (summoner != null) {
         return summoner;
      }

      UUID permanent = existence.getPermanentOwner();
      return permanent != null ? permanent : original;
   }

   @ModifyReturnValue(method = "isTame()Z", at = @At("RETURN"))
   public boolean isTame(boolean original) {
      if (original) {
         return true;
      } else {
         TamableAnimal animal = (TamableAnimal)this;
         IExistence existence = TensuraStorages.getExistenceFrom(animal);
         if (existence.getTemporaryOwner() != null) {
            return true;
         } else if (existence.getSummoner() != null) {
            return true;
         } else {
            return existence.getPermanentOwner() != null ? true : original;
         }
      }
   }

   @Inject(method = "tame(Lnet/minecraft/world/entity/player/Player;)V", at = @At("TAIL"))
   public void tame(Player player, CallbackInfo ci) {
      ((TensuraEntityEvents.PostTameEvent)TensuraEntityEvents.POST_TAME_EVENT.invoker()).tame((TamableAnimal)this, player);
   }
}
