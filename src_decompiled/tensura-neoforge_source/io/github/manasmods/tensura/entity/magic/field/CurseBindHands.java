package io.github.manasmods.tensura.entity.magic.field;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.race.RaceUtils;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import lombok.Generated;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.Animation.LoopType;
import software.bernie.geckolib.util.GeckoLibUtil;

public class CurseBindHands extends AreaField implements GeoEntity {
   protected int curseInterval = 100;
   protected int curseDuration = 100;
   protected int curseLevel = 0;
   @Nullable
   private MobEffectInstance additionalCurseEffect = null;
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public CurseBindHands(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setElementalAttack(true);
   }

   public CurseBindHands(Level level, Entity entity) {
      this((EntityType<? extends Projectile>)MiscEntityTypes.CURSE_BIND_HANDS.get(), level);
      this.setOwner(entity);
   }

   @Override
   protected boolean canHitEntity(Entity pTarget) {
      if (pTarget == this.getOwner()) {
         return false;
      } else {
         return super.canHitEntity(pTarget) && !RaceUtils.isUndead(pTarget) ? this.getOwner() == null || !pTarget.isAlliedTo(this.getOwner()) : false;
      }
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      pCompound.putInt("CurseInterval", this.getCurseInterval());
      pCompound.putInt("CurseDuration", this.getCurseDuration());
      pCompound.putInt("CurseLevel", this.getCurseLevel());
      if (this.getAdditionalCurseEffect() != null) {
         pCompound.put("CurseEffect", this.getAdditionalCurseEffect().save());
      }
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      this.setCurseInterval(pCompound.getInt("CurseInterval"));
      this.setCurseDuration(pCompound.getInt("CurseDuration"));
      this.setCurseLevel(pCompound.getInt("CurseLevel"));
      if (pCompound.contains("CurseEffect")) {
         this.setAdditionalCurseEffect(MobEffectInstance.load(pCompound.getCompound("CurseEffect")));
      }
   }

   @NotNull
   @Override
   public EntityDimensions getDimensions(Pose pose) {
      return this.getType().getDimensions().scale(this.getSize(), this.getVisualSize());
   }

   @Override
   protected void updateVisualSize() {
   }

   @Override
   public void tick() {
      super.tick();
      if (!this.level().isClientSide()) {
         if (this.tickCount % 10 == 0 && this.getTarget() != null) {
            this.setPos(this.getTarget().position());
            if (!this.getTarget().isAlive() && this.getLife() - this.getAge() > 10) {
               this.setAge(this.getLife() - 10);
            }
         }

         if (this.getCurseInterval() > 0 && this.getLife() - this.getAge() >= 10) {
            if (this.getAge() == 1 || this.getAge() % this.getCurseInterval() == 0) {
               for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox())) {
                  if (this.canHitEntity(target)) {
                     ManasSkill skill = this.getSkill() != null ? this.getSkill().getSkill() : null;
                     int curse = 0;
                     if (this.isEffectStack()) {
                        MobEffectInstance insanity = target.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.CURSE));
                        if (insanity != null) {
                           curse = insanity.getAmplifier() + this.getCurseLevel();
                        }
                     }

                     MobEffectInstance curseInstance = new MobEffectInstance(
                        TensuraMobEffects.getReference(TensuraMobEffects.CURSE), this.getCurseDuration(), curse, true, false, true
                     );
                     TensuraMobEffect.addEffect(target, curseInstance, this.getOwner(), skill, this.getMode());
                     MobEffectInstance additional = this.getAdditionalCurseEffect();
                     if (additional != null) {
                        int level = additional.getAmplifier();
                        if (this.isEffectStack()) {
                           MobEffectInstance effect = target.getEffect(additional.getEffect());
                           if (effect != null) {
                              level = effect.getAmplifier() + additional.getAmplifier();
                           }
                        }

                        MobEffectInstance instance = new MobEffectInstance(
                           additional.getEffect(), additional.getDuration(), level, additional.isAmbient(), additional.isVisible(), additional.showIcon()
                        );
                        TensuraMobEffect.addEffect(target, instance, this.getOwner(), skill, this.getMode());
                     }
                  }
               }
            }
         }
      }
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController[]{
            new AnimationController(
               this,
               "loopController",
               0,
               event -> this.getLife() - this.getAge() < 10
                  ? event.setAndContinue(RawAnimation.begin().thenPlayAndHold("animation.shadow_bind.stop"))
                  : event.setAndContinue(RawAnimation.begin().thenLoop("animation.shadow_bind.loop"))
            ),
            new AnimationController(this, "controller", 0, event -> PlayState.STOP)
               .triggerableAnim("start", RawAnimation.begin().then("animation.shadow_bind.start", LoopType.PLAY_ONCE))
         }
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   @Generated
   public int getCurseInterval() {
      return this.curseInterval;
   }

   @Generated
   public void setCurseInterval(int curseInterval) {
      this.curseInterval = curseInterval;
   }

   @Generated
   public int getCurseDuration() {
      return this.curseDuration;
   }

   @Generated
   public void setCurseDuration(int curseDuration) {
      this.curseDuration = curseDuration;
   }

   @Generated
   public int getCurseLevel() {
      return this.curseLevel;
   }

   @Generated
   public void setCurseLevel(int curseLevel) {
      this.curseLevel = curseLevel;
   }

   @Nullable
   @Generated
   public MobEffectInstance getAdditionalCurseEffect() {
      return this.additionalCurseEffect;
   }

   @Generated
   public void setAdditionalCurseEffect(@Nullable MobEffectInstance additionalCurseEffect) {
      this.additionalCurseEffect = additionalCurseEffect;
   }
}
