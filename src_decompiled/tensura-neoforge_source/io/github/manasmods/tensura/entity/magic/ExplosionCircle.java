package io.github.manasmods.tensura.entity.magic;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.Animation.LoopType;

public class ExplosionCircle extends MagicCircle {
   private static final EntityDataAccessor<Float> EXPLOSION_LEVEL = SynchedEntityData.defineId(ExplosionCircle.class, EntityDataSerializers.FLOAT);
   public static final RawAnimation START = RawAnimation.begin().then("animation.magic_explosion.spawn_circle", LoopType.HOLD_ON_LAST_FRAME);
   public static final RawAnimation STOP = RawAnimation.begin().then("animation.magic_explosion.remove_circle", LoopType.PLAY_ONCE);

   public ExplosionCircle(EntityType<? extends MagicCircle> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setRot(0.0F, -90.0F);
      this.noCulling = true;
   }

   public ExplosionCircle(Level level, LivingEntity entity) {
      this((EntityType<? extends MagicCircle>)MiscEntityTypes.EXPLOSION_CIRCLE.get(), level);
      this.setOwner(entity);
   }

   @Override
   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(EXPLOSION_LEVEL, 0.0F);
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      pCompound.putFloat("ExplosionLevel", this.getExplosionLevel());
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.setExplosionLevel(pCompound.getFloat("ExplosionLevel"));
   }

   public float getExplosionLevel() {
      return (Float)this.entityData.get(EXPLOSION_LEVEL);
   }

   public void setExplosionLevel(float level) {
      this.entityData.set(EXPLOSION_LEVEL, level);
   }

   public boolean shouldRenderAtSqrDistance(double d) {
      double e = 128.0 * getViewScale();
      return d < e * e;
   }

   @Override
   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
            new AnimationController(
               this, "loopController", 0, event -> event.setAndContinue(RawAnimation.begin().thenLoop("animation.magic_explosion.rotate_circle"))
            )
         )
         .add(
            new AnimationController(
                  this, "controller", 0, event -> event.setAndContinue(RawAnimation.begin().thenLoop("animation.magic_explosion.scale_circle"))
               )
               .triggerableAnim("start", START)
               .triggerableAnim("stop", STOP)
         );
   }

   public static void castMagicCircle(
      float radius,
      float explosionLevel,
      int life,
      Vec3 pos,
      LivingEntity owner,
      CompoundTag tag,
      @Nullable ManasSkillInstance instance,
      int mode,
      Pair<Double, Double> cost
   ) {
      castMagicCircle("MagicCircleID", radius, explosionLevel, life, pos, owner, tag, instance, mode, cost);
   }

   public static void castMagicCircle(
      String circleID,
      float radius,
      float explosionLevel,
      int life,
      Vec3 pos,
      LivingEntity owner,
      CompoundTag tag,
      @Nullable ManasSkillInstance instance,
      int mode,
      Pair<Double, Double> cost
   ) {
      Level level = owner.level();
      if (tag.getInt(circleID) == 0) {
         ExplosionCircle circle = new ExplosionCircle(level, owner);
         circle.setPos(pos);
         circle.setLife(life);
         circle.setSize(radius);
         circle.setExplosionLevel(explosionLevel);
         circle.setSkill(instance);
         circle.setMode(mode);
         circle.setApCost((Double)cost.getFirst());
         circle.setMpCost((Double)cost.getSecond());
         owner.level().addFreshEntity(circle);
         circle.reapplyPosition();
         circle.triggerAnim("controller", "start");
         owner.swing(InteractionHand.MAIN_HAND, true);
         tag.putInt(circleID, circle.getId());
      } else if (owner.level().getEntity(tag.getInt(circleID)) instanceof ExplosionCircle circle) {
         circle.increaseLife(1);
         circle.setSize(radius);
         circle.setExplosionLevel(explosionLevel);
         circle.setApCost((Double)cost.getFirst());
         circle.setMpCost((Double)cost.getSecond());
      } else {
         tag.putInt(circleID, 0);
      }
   }
}
