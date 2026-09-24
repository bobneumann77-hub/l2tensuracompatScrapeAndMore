package io.github.manasmods.tensura.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributeInstance;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AttributeMap.class)
public abstract class MixinAttributeMap implements TensuraAttributeInstance {
   @Unique
   @Nullable
   private LivingEntity tensura$owner;

   @Nullable
   @Override
   public LivingEntity tensura$getOwner() {
      return this.tensura$owner;
   }

   @Override
   public void tensura$setOwner(@Nullable LivingEntity owner) {
      this.tensura$owner = owner;
   }

   @ModifyReturnValue(method = "getInstance(Lnet/minecraft/core/Holder;)Lnet/minecraft/world/entity/ai/attributes/AttributeInstance;", at = @At("RETURN"))
   private AttributeInstance getInstance(AttributeInstance instance, Holder<Attribute> holder) {
      if (instance == null) {
         return null;
      }

      if (this.tensura$getOwner() != null) {
         instance.tensura$setOwner(this.tensura$getOwner());
      }

      return instance;
   }
}
