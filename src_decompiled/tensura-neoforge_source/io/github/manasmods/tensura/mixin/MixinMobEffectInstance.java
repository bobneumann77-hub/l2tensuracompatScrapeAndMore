package io.github.manasmods.tensura.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.manasmods.tensura.Tensura;
import io.github.manasmods.tensura.effect.template.TensuraMobEffectInstance;
import io.github.manasmods.tensura.storage.ability.AbilitySlot;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectInstance.Details;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEffectInstance.class)
public class MixinMobEffectInstance implements TensuraMobEffectInstance {
   @Unique
   private static final Codec<MobEffectInstance> tensura$CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            MobEffect.CODEC.fieldOf("id").forGetter(MobEffectInstance::getEffect),
            UUIDUtil.CODEC.optionalFieldOf("source").forGetter(effect -> Optional.ofNullable(effect.tensura$getSource())),
            AbilitySlot.CODEC.optionalFieldOf("ability").forGetter(effect -> {
               AbilitySlot slot = effect.tensura$getSourceAbility();
               return slot != null && slot.getSkill() != null ? Optional.of(slot) : Optional.empty();
            }),
            CompoundTag.CODEC.optionalFieldOf("tag").forGetter(effect -> Optional.ofNullable(effect.tensura$getTag())),
            Codec.BOOL.optionalFieldOf("override", false).forGetter(TensuraMobEffectInstance::tensura$shouldOverride),
            Details.MAP_CODEC.forGetter(MobEffectInstance::asDetails)
         )
         .apply(instance, MixinMobEffectInstance::tensura$createMobEffectInstance)
   );
   @Unique
   @Nullable
   private UUID tensura$source;
   @Unique
   @Nullable
   private AbilitySlot tensura$ability;
   @Unique
   private CompoundTag tensura$tag;
   @Unique
   private boolean tensura$override = false;

   @Unique
   private static MobEffectInstance tensura$createMobEffectInstance(
      Holder<MobEffect> holder, Optional<UUID> source, Optional<AbilitySlot> ability, Optional<CompoundTag> tag, boolean override, Details details
   ) {
      MobEffectInstance instance = new MobEffectInstance(holder, details);
      instance.tensura$setSource(source.orElse(null));
      instance.tensura$setSourceAbility(ability.filter(slot -> slot.getSkill() != null).orElse(null));
      instance.tensura$setTag(tag.orElse(null));
      instance.tensura$setOverride(override);
      return instance;
   }

   @ModifyReturnValue(method = "save()Lnet/minecraft/nbt/Tag;", at = @At("RETURN"))
   private Tag save(Tag original) {
      try {
         return tensura$CODEC.encodeStart(NbtOps.INSTANCE, (MobEffectInstance)this)
            .resultOrPartial(error -> Tensura.LOG.warn("Failed to encode Tensura MobEffectInstance data: {}", error))
            .orElse(original);
      } catch (Exception e) {
         Tensura.LOG.warn("Exception while encoding Tensura MobEffectInstance data", e);
         return original;
      }
   }

   @ModifyReturnValue(method = "load(Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/world/effect/MobEffectInstance;", at = @At("RETURN"))
   private static MobEffectInstance load(MobEffectInstance original, CompoundTag compoundTag) {
      try {
         return tensura$CODEC.parse(NbtOps.INSTANCE, compoundTag)
            .resultOrPartial(error -> Tensura.LOG.warn("Failed to decode Tensura MobEffectInstance data: {}", error))
            .orElse(original);
      } catch (Exception e) {
         Tensura.LOG.warn("Exception while decoding Tensura MobEffectInstance data", e);
         return original;
      }
   }

   @Nullable
   @Override
   public UUID tensura$getSource() {
      return this.tensura$source;
   }

   @Override
   public void tensura$setSource(@Nullable UUID source) {
      this.tensura$source = source;
   }

   @Nullable
   @Override
   public AbilitySlot tensura$getSourceAbility() {
      return this.tensura$ability;
   }

   @Override
   public void tensura$setSourceAbility(@Nullable AbilitySlot ability) {
      this.tensura$ability = ability;
   }

   @Override
   public CompoundTag tensura$getTag() {
      return this.tensura$tag;
   }

   @Override
   public CompoundTag tensura$getOrCreateTag() {
      if (this.tensura$tag == null) {
         this.tensura$tag = new CompoundTag();
      }

      return this.tensura$tag;
   }

   @Override
   public void tensura$setTag(@Nullable CompoundTag tag) {
      this.tensura$tag = tag;
   }

   @Override
   public boolean tensura$shouldOverride() {
      return this.tensura$override;
   }

   @Override
   public void tensura$setOverride(boolean override) {
      this.tensura$override = override;
   }

   @Override
   public boolean tensura$hasSource() {
      return this.tensura$source != null;
   }

   @Override
   public boolean tensura$hasAbility() {
      return this.tensura$ability != null && this.tensura$ability.getSkill() != null;
   }

   @Override
   public boolean tensura$hasTag() {
      return this.tensura$tag != null;
   }

   @ModifyReturnValue(method = "update(Lnet/minecraft/world/effect/MobEffectInstance;)Z", at = @At("RETURN"))
   private boolean update(boolean success, MobEffectInstance other) {
      if ((other.tensura$hasSource() || other.tensura$shouldOverride()) && !Objects.equals(this.tensura$source, other.tensura$getSource())) {
         this.tensura$source = other.tensura$getSource();
         success = true;
      }

      if ((other.tensura$hasAbility() || other.tensura$shouldOverride()) && this.tensura$ability != other.tensura$getSourceAbility()) {
         this.tensura$ability = other.tensura$getSourceAbility();
         success = true;
      }

      if ((other.tensura$hasTag() || other.tensura$shouldOverride()) && !Objects.equals(this.tensura$tag, other.tensura$getTag())) {
         this.tensura$tag = other.tensura$getTag();
         success = true;
      }

      return success;
   }

   @Inject(method = "setDetailsFrom(Lnet/minecraft/world/effect/MobEffectInstance;)V", at = @At("TAIL"))
   private void setDetailsFrom(@NotNull MobEffectInstance other, CallbackInfo ci) {
      this.tensura$source = other.tensura$getSource();
      this.tensura$ability = other.tensura$getSourceAbility();
      this.tensura$tag = other.tensura$getTag();
      this.tensura$override = other.tensura$shouldOverride();
   }
}
