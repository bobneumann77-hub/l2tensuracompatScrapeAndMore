package io.github.manasmods.tensura.mixin;

import dev.architectury.event.EventResult;
import io.github.manasmods.tensura.event.TensuraEntityEvents;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributeInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AttributeInstance.class)
public abstract class MixinAttributeInstance implements TensuraAttributeInstance {
   @Shadow
   private double baseValue;
   @Unique
   @Nullable
   private LivingEntity tensura$owner;

   @Shadow
   protected abstract void setDirty();

   @Nullable
   @Override
   public LivingEntity tensura$getOwner() {
      return this.tensura$owner;
   }

   @Override
   public void tensura$setOwner(@Nullable LivingEntity owner) {
      this.tensura$owner = owner;
   }

   @Inject(method = "setBaseValue(D)V", at = @At("HEAD"), cancellable = true)
   private void setBaseValue(double newValue, CallbackInfo ci) {
      if (this.baseValue != newValue) {
         if (this.tensura$getOwner() != null) {
            EventResult event = ((TensuraEntityEvents.AttributeBaseValueChangedEvent)TensuraEntityEvents.ATTRIBUTE_BASE_CHANGE_EVENT.invoker())
               .change(this.tensura$getOwner(), (AttributeInstance)this, this.baseValue, newValue);
            if (event.isFalse()) {
               ci.cancel();
            } else if (event.isTrue() && event.interruptsFurtherEvaluation()) {
               ci.cancel();
               this.setDirty();
            }
         }
      }
   }
}
