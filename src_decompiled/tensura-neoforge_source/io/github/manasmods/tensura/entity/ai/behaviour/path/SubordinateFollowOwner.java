package io.github.manasmods.tensura.entity.ai.behaviour.path;

import io.github.manasmods.tensura.entity.template.subclass.ISubordinate;
import io.github.manasmods.tensura.util.SubordinateHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.TamableAnimal;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.FollowEntity;

public class SubordinateFollowOwner<E extends PathfinderMob & ISubordinate> extends FollowEntity<E, LivingEntity> {
   protected LivingEntity owner = null;

   public SubordinateFollowOwner() {
      this.speedMod(1.5F);
      this.startCondition(entity -> !((ISubordinate)entity).isOrderedToSit() && !((ISubordinate)entity).isWandering());
      this.following(x$0 -> this.getOwner((E)x$0));
      this.teleportToTargetAfter((entity, owner) -> owner.getType().equals(EntityType.PLAYER) ? 15.0 : 64.0);
      this.stopFollowingWithin((entity, owner) -> Math.max(3.0 * entity.getBbWidth(), 4.0));
   }

   protected LivingEntity getOwner(E entity) {
      if (this.owner != null && (this.owner.isRemoved() || !this.owner.getUUID().equals(entity.getOwnerUUID()))) {
         this.owner = null;
      }

      if (this.owner == null) {
         this.owner = SubordinateHelper.getSubordinateOwner(entity);
      }

      return this.owner;
   }

   protected void teleportToTarget(E entity, LivingEntity target) {
      if (entity instanceof TamableAnimal tame) {
         if (tame.isOrderedToSit() || tame.isWandering()) {
            return;
         }

         tame.tryToTeleportToOwner();
      } else {
         super.teleportToTarget(entity, target);
      }
   }
}
