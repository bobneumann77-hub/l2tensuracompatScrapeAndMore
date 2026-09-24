package io.github.manasmods.tensura.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.entity.template.TensuraPartEntity;
import io.github.manasmods.tensura.entity.template.subclass.IMultipart;
import io.github.manasmods.tensura.item.weapon.spell.SimpleSpellCastItem;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import io.github.manasmods.tensura.registry.skill.UniqueSkills;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.SubordinateHelper;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {
   @Inject(method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;)V", at = @At("TAIL"))
   private void init(EntityType<? extends LivingEntity> entityType, Level level, CallbackInfo ci) {
      LivingEntity entity = (LivingEntity)this;
      entity.getAttributes().tensura$setOwner(entity);
   }

   @Inject(method = "recreateFromPacket(Lnet/minecraft/network/protocol/game/ClientboundAddEntityPacket;)V", at = @At("TAIL"))
   private void setPartId(ClientboundAddEntityPacket packet, CallbackInfo ci) {
      if (this instanceof IMultipart multipart) {
         for (int i = 0; i < multipart.getParts().length; i++) {
            TensuraPartEntity part = multipart.getParts()[i];
            part.setId(part.getParent().getId() + i);
         }
      }
   }

   @Inject(
      method = "readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/attributes/AttributeMap;load(Lnet/minecraft/nbt/ListTag;)V", shift = Shift.AFTER)
   )
   private void readAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
      LivingEntity entity = (LivingEntity)this;
      entity.getAttributes().tensura$setOwner(entity);
   }

   @Inject(method = "getAttributeValue(Lnet/minecraft/core/Holder;)D", at = @At("HEAD"), cancellable = true)
   private void getAttributeValue(Holder<Attribute> holder, CallbackInfoReturnable<Double> cir) {
      LivingEntity entity = (LivingEntity)this;
      if (!entity.getAttributes().hasAttribute(holder)) {
         cir.setReturnValue(0.0);
      }
   }

   @ModifyReturnValue(method = "getDimensions(Lnet/minecraft/world/entity/Pose;)Lnet/minecraft/world/entity/EntityDimensions;", at = @At("RETURN"))
   public EntityDimensions getDimensions(EntityDimensions original, Pose pose) {
      LivingEntity entity = (LivingEntity)this;
      float width = (float)entity.getAttributeValue(TensuraAttributes.WIDTH_MULTIPLIER);
      float height = (float)entity.getAttributeValue(TensuraAttributes.HEIGHT_MULTIPLIER);
      return width == 1.0F && height == 1.0F ? original : original.scale(width, height);
   }

   @Inject(
      method = "eat(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/LivingEntity;eat(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/food/FoodProperties;)Lnet/minecraft/world/item/ItemStack;"
      )
   )
   public void addEatEffect(Level level, ItemStack itemStack, CallbackInfoReturnable<ItemStack> cir) {
      FoodProperties food = (FoodProperties)itemStack.get(DataComponents.FOOD);
      LivingEntity entity = (LivingEntity)this;
      if (itemStack.is(TensuraItemTags.MONSTER_CONSUMABLES)) {
         double magicule = food.nutrition() * 100.0;
         if (itemStack.is(TensuraItemTags.COOKED_MONSTER_CONSUMABLES)) {
            magicule /= 2.0;
         }

         EnergyHelper.gainMagicule(entity, magicule, EnergyHelper.GainType.NORMAL);
      }
   }

   @ModifyReturnValue(method = "getArmorCoverPercentage()F", at = @At("RETURN"))
   public float getArmorCoverPercentage(float original) {
      LivingEntity entity = (LivingEntity)this;
      return entity.getAttributeValue(TensuraAttributes.PRESENCE_CONCEALMENT) >= 1.0 ? 0.0F : original;
   }

   @Inject(method = "playHurtSound(Lnet/minecraft/world/damagesource/DamageSource;)V", at = @At("HEAD"), cancellable = true)
   protected void getHurtSound(DamageSource pSource, CallbackInfo ci) {
      LivingEntity living = (LivingEntity)this;
      if (SkillUtils.shouldCancelPain(living)) {
         ci.cancel();
      } else {
         if (SkillUtils.isSkillToggled(living, (ManasSkill)UniqueSkills.MURDERER.get())) {
            if (living instanceof Player player) {
               player.playNotifySound(SoundEvents.GENERIC_HURT, SoundSource.PLAYERS, 1.0F, 1.0F);
            }

            ci.cancel();
         }
      }
   }

   @ModifyReturnValue(method = "canAttack(Lnet/minecraft/world/entity/LivingEntity;)Z", at = @At("RETURN"))
   public boolean canAttack(boolean original, LivingEntity target) {
      if (!original) {
         return false;
      }

      UUID owner = SubordinateHelper.getSubordinateOwnerUUID((LivingEntity)this);
      return owner == null || !Objects.equals(owner, target.getUUID()) && !Objects.equals(owner, SubordinateHelper.getSubordinateOwnerUUID(target))
         ? original
         : false;
   }

   @Inject(
      method = "onEffectRemoved(Lnet/minecraft/world/effect/MobEffectInstance;)V",
      at = @At(
         value = "INVOKE",
         shift = Shift.AFTER,
         target = "Lnet/minecraft/world/effect/MobEffect;removeAttributeModifiers(Lnet/minecraft/world/entity/ai/attributes/AttributeMap;)V"
      )
   )
   private void onEffectRemoved(MobEffectInstance instance, CallbackInfo ci) {
      if (instance.getEffect().value() instanceof TensuraMobEffect effect) {
         effect.onAttributeRemoved((LivingEntity)this, instance);
      }
   }

   @WrapOperation(
      method = "tickEffects()V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;onEffectRemoved(Lnet/minecraft/world/effect/MobEffectInstance;)V")
   )
   private void tickEffects(LivingEntity entity, MobEffectInstance instance, Operation<Void> original) {
      original.call(new Object[]{entity, instance});
      if (instance.getEffect().value() instanceof TensuraMobEffect effect) {
         effect.onEffectRemoved((LivingEntity)this, instance);
      }
   }

   @Inject(method = "stopUsingItem()V", at = @At("HEAD"))
   private void stopUsingItem(CallbackInfo ci) {
      LivingEntity entity = (LivingEntity)this;
      if (!entity.level().isClientSide() && entity.isUsingItem() && entity.getUseItem().getItem() instanceof SimpleSpellCastItem castItem) {
         castItem.onCastRelease(entity.getUseItem(), entity.level(), entity);
      }
   }

   @WrapOperation(
      method = "removeEffect(Lnet/minecraft/core/Holder;)Z",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;onEffectRemoved(Lnet/minecraft/world/effect/MobEffectInstance;)V")
   )
   private void removeEffect(LivingEntity entity, MobEffectInstance instance, Operation<Void> original) {
      original.call(new Object[]{entity, instance});
      if (instance.getEffect().value() instanceof TensuraMobEffect effect) {
         effect.onEffectRemoved((LivingEntity)this, instance);
      }
   }

   @Inject(method = "setPosToBed(Lnet/minecraft/core/BlockPos;)V", at = @At("HEAD"), cancellable = true)
   public void setPosToBed(BlockPos blockPos, CallbackInfo ci) {
      LivingEntity entity = (LivingEntity)this;
      if (entity.level().getBlockState(blockPos).is((Block)TensuraBlocks.THATCH_BED.get())) {
         entity.setPos(blockPos.getX() + 0.5, blockPos.getY() + 0.15, blockPos.getZ() + 0.5);
         ci.cancel();
      }
   }

   @ModifyReturnValue(method = "shouldDropLoot()Z", at = @At("RETURN"))
   public boolean shouldDropLoot(boolean original) {
      if (!original) {
         return false;
      }

      LivingEntity entity = (LivingEntity)this;
      return TensuraStorages.getExistenceFrom(entity).getSummoner() != null ? false : original;
   }

   @ModifyReturnValue(method = "shouldDropExperience()Z", at = @At("RETURN"))
   public boolean shouldDropExperience(boolean original) {
      if (!original) {
         return false;
      }

      LivingEntity entity = (LivingEntity)this;
      return TensuraStorages.getExistenceFrom(entity).getSummoner() != null ? false : original;
   }
}
