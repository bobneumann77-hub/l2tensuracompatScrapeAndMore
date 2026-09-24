package dev.xkmc.l2hostility.content.traits.goals;

import dev.xkmc.l2core.capability.attachment.GeneralCapabilityHolder;
import dev.xkmc.l2damagetracker.contents.attack.DamageData.OffenceMax;
import dev.xkmc.l2hostility.content.capability.mob.CapStorageData;
import dev.xkmc.l2hostility.content.capability.mob.MobTraitCap;
import dev.xkmc.l2hostility.content.traits.base.MobTrait;
import dev.xkmc.l2hostility.init.data.LHConfig;
import dev.xkmc.l2hostility.init.registrate.LHMiscs;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

public class CounterStrikeTrait extends MobTrait {
   public CounterStrikeTrait(ChatFormatting format) {
      super(format);
   }

   @Override
   public void tick(LivingEntity le, int level) {
      if (!le.level().isClientSide()) {
         CounterStrikeTrait.Data data = ((MobTraitCap)((GeneralCapabilityHolder)LHMiscs.MOB.type()).getOrCreate(le))
            .getOrCreateData(this.getRegistryName(), CounterStrikeTrait.Data::new);
         if (data.cooldown > 0) {
            data.cooldown--;
         } else if (le instanceof Mob mob) {
            if (le.onGround()) {
               LivingEntity target = mob.getTarget();
               if (target != null && target.isAlive()) {
                  if (data.strikeId != null && data.strikeId.equals(target.getUUID())) {
                     Vec3 diff = target.position().subtract(le.position());
                     diff = diff.normalize().scale(3.0);
                     if (diff.y <= 0.2) {
                        diff = diff.add(0.0, 0.2, 0.0);
                     }

                     le.setDeltaMovement(diff);
                     le.hasImpulse = true;
                     data.duration = (Integer)LHConfig.SERVER.counterStrikeDuration.get();
                     data.strikeId = null;
                  }
               }
            }
         }
      }
   }

   @Override
   public void onHurtByMax(int level, LivingEntity le, OffenceMax event) {
      if (!le.level().isClientSide()) {
         Entity target = event.getSource().getEntity();
         CounterStrikeTrait.Data data = ((MobTraitCap)((GeneralCapabilityHolder)LHMiscs.MOB.type()).getOrCreate(le))
            .getOrCreateData(this.getRegistryName(), CounterStrikeTrait.Data::new);
         if (target instanceof LivingEntity && le instanceof Mob mob && mob.getTarget() == target) {
            data.strikeId = target.getUUID();
         }
      }
   }

   @SerialClass
   public static class Data extends CapStorageData {
      @SerialField
      public int cooldown;
      @SerialField
      public int duration;
      @SerialField
      public UUID strikeId;
   }
}
