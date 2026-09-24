package io.github.manasmods.tensura.entity.magic.breath;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.EntityEvents.ProjectileHitResult;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.entity.TensuraProjectile;
import io.github.manasmods.tensura.entity.template.TensuraPartEntity;
import io.github.manasmods.tensura.entity.template.subclass.IMultipart;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.NotNull;

public class BreathEntity extends TensuraProjectile implements IMultipart {
   private static final EntityDataAccessor<Float> LENGTH = SynchedEntityData.defineId(BreathEntity.class, EntityDataSerializers.FLOAT);
   protected float prevLength = 0.0F;
   public BreathPart[] parts = new BreathPart[]{
      new BreathPart(this, "Breath 1", 1.0F, 1.0F),
      new BreathPart(this, "Breath 2", 2.0F, 1.5F),
      new BreathPart(this, "Breath 3", 3.0F, 2.0F),
      new BreathPart(this, "Breath 4", 4.0F, 2.5F)
   };

   public BreathEntity(EntityType<? extends BreathEntity> entityType, Level level, LivingEntity entity) {
      this(entityType, level);
      this.setOwner(entity);
   }

   public BreathEntity(EntityType<? extends BreathEntity> entityType, Level level) {
      super(entityType, level);
      this.setNoGravity(true);
      this.blocksBuilding = false;
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(LENGTH, 10.0F);
   }

   @Override
   protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      pCompound.putFloat("Length", this.getLength());
   }

   @Override
   protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.entityData.set(LENGTH, pCompound.getFloat("Length"));
   }

   public float getLength() {
      return (Float)this.entityData.get(LENGTH);
   }

   public void setLength(float length) {
      this.entityData.set(LENGTH, length);
      this.buildParts();
   }

   @Override
   public void increaseLife(int life) {
      this.setLife(Math.min(this.getLife() + life, 100));
   }

   @Override
   public boolean shouldDiscardInLava() {
      return false;
   }

   @Override
   public boolean shouldDiscardInWater() {
      return false;
   }

   public boolean hasBlockInteraction() {
      return false;
   }

   @Override
   public TensuraPartEntity[] getParts() {
      return this.parts;
   }

   public void buildParts() {
      if (!(this.getLength() <= 0.0F)) {
         float f = this.getLength();

         int l;
         for (l = 1; f > 0.0F; l++) {
            f -= l;
         }

         BreathPart[] temp = new BreathPart[l - 1];
         float length = this.getLength();

         for (int i = 1; length > 0.0F; i++) {
            float partLength = i;
            if (length < i) {
               partLength = Math.max(length, 2.0F);
            }

            length -= i;
            temp[i - 1] = new BreathPart(this, "part" + i, partLength, 0.5F + i * 0.5F);
         }

         this.parts = temp;
      }
   }

   protected static boolean hasLineOfSight(Entity source, Entity target) {
      Vec3 sourceEye = new Vec3(source.getX(), source.getY() + source.getBbHeight() / 2.0F, source.getZ());
      Vec3 targetEye = new Vec3(target.getX(), target.getY() + target.getBbHeight() / 2.0F, target.getZ());
      ClipContext clipContext = new ClipContext(sourceEye, targetEye, Block.COLLIDER, Fluid.NONE, source);
      HitResult result = source.level().clip(clipContext);
      return result.getType() == Type.MISS;
   }

   protected Set<Entity> getPartCollision() {
      Set<Entity> result = new HashSet<>();

      for (Entity part : this.parts) {
         for (Entity entity : this.level().getEntities(part, part.getBoundingBox())) {
            if (this.canCollide(entity) && this.canHitEntity(entity)) {
               result.add(entity);
            }
         }
      }

      return result;
   }

   protected boolean canCollide(Entity entity) {
      Entity owner = this.getOwner();
      if (owner != null && entity == owner) {
         return false;
      }

      if (!hasLineOfSight(this, entity)) {
         return false;
      }

      if (owner != null) {
         Entity vehicle = entity.getVehicle();
         if (entity == vehicle) {
            return vehicle instanceof Mob mob && mob.getTarget() == owner;
         }

         if (owner.getPassengers().contains(entity)) {
            return entity instanceof Mob mob && mob.getTarget() == owner;
         }
      }

      return true;
   }

   @Override
   protected boolean canHitEntity(Entity pTarget) {
      return pTarget != this && !(pTarget instanceof LivingEntity living && living.hasInfiniteMaterials())
         ? this.getOwner() == null || !this.getOwner().isPassengerOfSameVehicle(pTarget)
         : false;
   }

   public void setId(int id) {
      super.setId(id);

      for (int i = 0; i < this.parts.length; i++) {
         if (this.parts[i] != null) {
            this.parts[i].setId(id + i + 1);
         }
      }
   }

   @Override
   public void tick() {
      super.tick();
      if (this.prevLength != this.getLength()) {
         this.prevLength = this.getLength();
         this.buildParts();
      }

      Entity owner = this.getOwner();
      if (owner != null) {
         Vec3 eye = owner.getEyePosition(1.0F).subtract(0.0, 0.8, 0.0);
         this.setPos(eye);
         this.setXRot(owner.getXRot());
         this.setYRot(owner.getYRot());
         this.yRotO = this.getYRot();
         this.xRotO = this.getXRot();
         double distance = 1.0;

         for (BreathPart part : this.parts) {
            if (part != null) {
               distance += part.getBbWidth() * 4.0F / 5.0F;
               Vec3 newVector = eye.add(owner.getViewVector(0.5F).multiply(distance, distance, distance));
               part.setPos(newVector);
               part.setDeltaMovement(eye.add(owner.getViewVector(1.0F).multiply(distance, distance, distance)));
               Vec3 vec3 = new Vec3(part.getX(), part.getY(), part.getZ());
               part.xo = vec3.x;
               part.yo = vec3.y;
               part.zo = vec3.z;
               part.xOld = vec3.x;
               part.yOld = vec3.y;
               part.zOld = vec3.z;
            }
         }
      }

      if (!this.level().isClientSide()) {
         for (Entity entity : this.getPartCollision()) {
            this.hitEntity(entity, ProjectileHitResult.DEFAULT);
         }

         if (!this.hasBlockInteraction()) {
            return;
         }

         if (!this.shouldGrief()) {
            return;
         }

         if (!(this.getOwner() instanceof Player player)) {
            return;
         }

         float var15 = (float) (Math.PI / 12);

         for (int i = 0; i < this.getLength(); i++) {
            Vec3 lookAngle = this.getOwner()
               .getLookAngle()
               .normalize()
               .xRot(this.getRandom().nextFloat() * var15 * 2.0F - var15)
               .yRot(this.getRandom().nextFloat() * var15 * 2.0F - var15)
               .zRot(this.getRandom().nextFloat() * var15 * 2.0F - var15);
            this.handleBlockInteraction(player, lookAngle);
         }
      } else {
         this.spawnParticle();
      }
   }

   protected void handleBlockInteraction(Player player, Vec3 lookAngle) {
   }

   public void spawnParticle() {
   }

   public static BreathEntity spawnBreathEntity(
      EntityType<? extends BreathEntity> entityType, LivingEntity owner, ManasSkillInstance instance, float damage, TensuraSkill skill, int mode
   ) {
      Pair<Double, Double> cost = new Pair(skill.getAuraCost(owner, instance, mode), skill.getMagiculeCost(owner, instance, mode));
      return spawnBreathEntity(entityType, owner, instance, mode, damage, cost);
   }

   public static BreathEntity spawnBreathEntity(
      EntityType<? extends BreathEntity> entityType, LivingEntity owner, ManasSkillInstance instance, int mode, float damage, Pair<Double, Double> cost
   ) {
      CompoundTag tag = instance.getOrCreateTag();
      Level level = owner.level();
      if (tag.getInt("BreathEntity") == 0) {
         BreathEntity breath = (BreathEntity)entityType.create(level);
         if (breath == null) {
            return null;
         }

         breath.setLife(5);
         breath.setDamage(damage);
         breath.setOwner(owner);
         breath.setPos(owner.position().add(0.0, owner.getBbHeight() / 2.0F, 0.0));
         breath.setApCost((Double)cost.getFirst());
         breath.setMpCost((Double)cost.getSecond());
         breath.setSkill(instance);
         breath.setMode(mode);
         owner.level().addFreshEntity(breath);
         tag.putInt("BreathEntity", breath.getId());
         instance.markDirty();
         return breath;
      } else if (owner.level().getEntity(tag.getInt("BreathEntity")) instanceof BreathEntity breath) {
         breath.increaseLife(1);
         return breath;
      } else {
         tag.putInt("BreathEntity", 0);
         instance.markDirty();
         return null;
      }
   }
}
