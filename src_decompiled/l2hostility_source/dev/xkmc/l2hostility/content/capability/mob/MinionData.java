package dev.xkmc.l2hostility.content.capability.mob;

import dev.xkmc.l2hostility.content.config.EntityConfig;
import dev.xkmc.l2serial.serialization.marker.SerialClass;
import dev.xkmc.l2serial.serialization.marker.SerialField;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

@SerialClass
public class MinionData {
   @SerialField
   public UUID uuid;
   @SerialField
   public int id;
   @SerialField
   public double linkDistance;
   @SerialField
   public boolean protectMaster;
   @SerialField
   public boolean discardOnUnlink;
   @Nullable
   public Mob master;

   public boolean tick(LivingEntity mob) {
      if (mob.level() instanceof ServerLevel sl) {
         if (this.master == null) {
            if (sl.getEntity(this.uuid) instanceof Mob mas) {
               this.master = mas;
            } else if (this.discardOnUnlink) {
               mob.discard();
            }
         }

         if (this.master != null && this.master.distanceTo(mob) > this.linkDistance) {
            BlockPos next = MasterData.getRandomPos(sl, mob.getType(), this.master, (int)(this.linkDistance * 0.5), 16);
            if (next != null) {
               mob.moveTo(Vec3.atCenterOf(next));
            } else if (this.discardOnUnlink) {
               mob.discard();
            }
         }
      } else {
         if (this.master == null && mob.level().getEntity(this.id) instanceof Mob mas && mas.getUUID().equals(this.uuid)) {
            this.master = mas;
         }

         if (this.master != null && this.id != this.master.getId()) {
            this.id = this.master.getId();
            return true;
         }
      }

      return false;
   }

   public MinionData init(Mob mob, EntityConfig.Minion config) {
      this.uuid = mob.getUUID();
      this.id = mob.getId();
      this.linkDistance = config.linkDistance();
      this.protectMaster = config.protectMaster();
      this.discardOnUnlink = config.discardOnUnlink();
      this.master = mob;
      return this;
   }
}
