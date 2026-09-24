package io.github.manasmods.tensura.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.data.TensuraTags;
import io.github.manasmods.tensura.entity.magic.barrier.BarrierPart;
import io.github.manasmods.tensura.entity.template.TensuraPartEntity;
import io.github.manasmods.tensura.entity.template.subclass.ILivingPartEntity;
import io.github.manasmods.tensura.entity.template.subclass.IMultipart;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.util.SubordinateHelper;
import io.github.manasmods.tensura.world.subclass.IMultipartLevel;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MixinEntity {
   @Inject(method = "recreateFromPacket(Lnet/minecraft/network/protocol/game/ClientboundAddEntityPacket;)V", at = @At("TAIL"))
   private void setPartId(ClientboundAddEntityPacket packet, CallbackInfo ci) {
      if (this instanceof IMultipart multipart) {
         for (int i = 0; i < multipart.getParts().length; i++) {
            TensuraPartEntity part = multipart.getParts()[i];
            part.setId(part.getParent().getId() + i);
         }
      }
   }

   @ModifyReturnValue(method = "isInvulnerableTo(Lnet/minecraft/world/damagesource/DamageSource;)Z", at = @At("RETURN"))
   public boolean isInvulnerableTo(boolean original, DamageSource pSource) {
      if (!original) {
         return false;
      } else {
         Entity entity = (Entity)this;
         if (!entity.fireImmune()) {
            return original;
         } else {
            return pSource.is(TensuraTags.DamageTypes.IS_MAGIC_FIRE) ? false : original;
         }
      }
   }

   @Inject(method = "setSharedFlagOnFire(Z)V", at = @At("RETURN"), cancellable = true)
   public void setSharedFlagOnFire(boolean pIsOnFire, CallbackInfo ci) {
      if (!pIsOnFire) {
         Entity entity = (Entity)this;
         if (entity instanceof LivingEntity living && TensuraStorages.getEffectFrom(living).isOnBlackFlame()) {
            entity.setSharedFlagOnFire(true);
            ci.cancel();
         }
      }
   }

   @ModifyReturnValue(method = "isOnFire()Z", at = @At("RETURN"))
   public boolean isOnFire(boolean original) {
      Entity entity = (Entity)this;
      if (entity instanceof LivingEntity living) {
         return original ? !SkillUtils.shouldCancelInteraction(living) : TensuraStorages.getEffectFrom(living).isOnBlackFlame();
      } else {
         return original;
      }
   }

   @ModifyReturnValue(method = "canCollideWith(Lnet/minecraft/world/entity/Entity;)Z", at = @At("RETURN"))
   public boolean canCollideWith(boolean original, Entity pEntity) {
      if (!original) {
         return false;
      } else {
         return pEntity instanceof BarrierPart part ? part.canCollideWith((Entity)this) : original;
      }
   }

   @ModifyReturnValue(method = "isInvisible()Z", at = @At("RETURN"))
   public boolean isInvisible(boolean original) {
      Entity entity = (Entity)this;
      if (entity instanceof LivingEntity living) {
         if (living.getAttributeValue(TensuraAttributes.PRESENCE_CONCEALMENT) >= 1.0) {
            return true;
         } else {
            return living.getType().equals(EntityType.PLAYER)
                  && TensuraStorages.getExistenceFrom(living).isSpiritualForm()
                  && !SkillUtils.inSpiritualWorld(entity.level().dimension())
               ? true
               : original;
         }
      } else {
         return original;
      }
   }

   @ModifyReturnValue(method = "isInvisibleTo(Lnet/minecraft/world/entity/player/Player;)Z", at = @At("RETURN"))
   public boolean shouldBeInvisible(boolean original, Player pPlayer) {
      Entity entity = (Entity)this;
      if (entity.getType().is(TensuraEntityTags.CAN_STAY_INVISIBLE)) {
         return original;
      } else if (SkillUtils.shouldCancelInvisibility(pPlayer, entity)) {
         return false;
      } else {
         return entity instanceof LivingEntity target && TensuraStorages.getExistenceFrom(target).isSpiritualForm() ? false : original;
      }
   }

   @Inject(method = "isAlliedTo(Lnet/minecraft/world/entity/Entity;)Z", at = @At("HEAD"), cancellable = true)
   public void isAlliedTo(Entity pEntity, CallbackInfoReturnable<Boolean> cir) {
      Entity target = (Entity)this;
      if (target instanceof LivingEntity sub) {
         if (pEntity instanceof LivingEntity targetx) {
            if (SubordinateHelper.isAlly(sub, ILivingPartEntity.checkForHead(targetx))) {
               cir.setReturnValue(true);
            }
         }
      }
   }

   @Inject(method = "setRemoved(Lnet/minecraft/world/entity/Entity$RemovalReason;)V", at = @At("HEAD"))
   private void unregisterParts(RemovalReason reason, CallbackInfo ci) {
      Entity self = (Entity)this;
      if (self instanceof IMultipart parent) {
         if (self.level() instanceof IMultipartLevel mp) {
            for (TensuraPartEntity part : parent.getParts()) {
               if (part != null) {
                  mp.tensura$unregisterPart(part);
               }
            }
         }
      }
   }
}
