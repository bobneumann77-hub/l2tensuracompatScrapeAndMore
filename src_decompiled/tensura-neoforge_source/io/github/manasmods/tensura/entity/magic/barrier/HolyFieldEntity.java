package io.github.manasmods.tensura.entity.magic.barrier;

import io.github.manasmods.tensura.entity.human.HinataSakaguchiEntity;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class HolyFieldEntity extends BarrierEntity {
   public HolyFieldEntity(Level level, LivingEntity entity) {
      this((EntityType<? extends HolyFieldEntity>)MiscEntityTypes.HOLY_FIELD.get(), level);
      this.setOwner(entity);
   }

   public HolyFieldEntity(EntityType<? extends HolyFieldEntity> entityType, Level level) {
      super(entityType, level);
   }

   @Override
   public boolean shouldCreateParts() {
      return false;
   }

   @Override
   public boolean shouldRenderAtSqrDistance(double pDistance) {
      return false;
   }

   @Override
   public boolean blockBuilding() {
      return false;
   }

   @Override
   public boolean canWalkThrough(Entity entity) {
      Entity owner = this.getOwner();
      return entity == owner
         ? true
         : entity instanceof LivingEntity target
            && (
               !target.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.ENERGY_BLOCKADE))
                  || !target.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.MAGIC_INTERFERENCE))
                  || !target.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.SPATIAL_BLOCKADE))
            );
   }

   @Override
   public boolean hurt(DamageSource pSource, float pAmount) {
      return false;
   }

   @Override
   protected boolean shouldRemove() {
      if (super.shouldRemove()) {
         return true;
      } else if (this.getOwner() == null) {
         return true;
      } else {
         return this.getOwner() instanceof HinataSakaguchiEntity hinata && hinata.holyFieldUUID != this.getUUID() ? true : !this.getOwner().isAlive();
      }
   }

   @Override
   public void applyEffect(LivingEntity entity) {
      Entity owner = this.getOwner();
      if (owner == null || !entity.isAlliedTo(owner) && entity != owner) {
         entity.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.ENERGY_BLOCKADE), 40, 4, false, false, false));
         entity.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.MAGIC_INTERFERENCE), 40, 0, false, false, false));
         entity.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.ANTI_MAGIC), 40, 0, false, false, false));
         entity.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.SPATIAL_BLOCKADE), 40, 0, false, false, false));
      }
   }
}
