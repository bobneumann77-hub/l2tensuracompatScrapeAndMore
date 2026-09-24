package io.github.manasmods.tensura.entity.magic.misc;

import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.entity.TensuraProjectile;
import io.github.manasmods.tensura.event.TensuraEntityEvents;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import java.util.List;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.Animation.LoopType;
import software.bernie.geckolib.util.GeckoLibUtil;

public class DeathTornadoEntity extends TensuraProjectile implements GeoEntity {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public DeathTornadoEntity(Level pLevel) {
      super((EntityType<? extends Projectile>)MiscEntityTypes.DEATH_TORNADO.get(), pLevel);
      this.setElementalAttack(true);
      this.noCulling = true;
      this.setLife(200);
   }

   public DeathTornadoEntity(Level pLevel, Entity owner) {
      this(pLevel);
      this.setOwner(owner);
   }

   @Override
   public void setSize(float size) {
      this.entityData.set(SIZE, size);
   }

   public boolean shouldRenderAtSqrDistance(double d) {
      double e = 64.0 * getViewScale();
      return d < e * e;
   }

   @NotNull
   public SoundSource getSoundSource() {
      return TensuraSkill.ABILITY_SOUND;
   }

   @Override
   protected boolean canHitEntity(Entity pTarget) {
      if (pTarget == this.getOwner()) {
         return false;
      } else {
         return !super.canHitEntity(pTarget) ? false : !(this.getOwner() instanceof LivingEntity owner && owner.isAlliedTo(pTarget));
      }
   }

   @Override
   public void tick() {
      if (this.getAge() == 0) {
         this.triggerAnim("controller", "start");
      }

      super.tick();
      if (this.level().isClientSide() && this.getLife() % 5 == 0) {
         if (this.getLife() % 20 == 0) {
            this.level()
               .playLocalSound(
                  this.getX(),
                  this.getY(),
                  this.getZ(),
                  SoundEvents.LIGHTNING_BOLT_THUNDER,
                  TensuraSkill.ABILITY_SOUND,
                  10000.0F,
                  0.8F + this.random.nextFloat() * 0.2F,
                  false
               );
         }

         this.level()
            .playLocalSound(
               this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.WIND_BLOW.get(), TensuraSkill.ABILITY_SOUND, 3.0F, 1.0F, false
            );
      }

      if (this.getAge() >= 30) {
         if (this.getLife() - this.getAge() >= 30) {
            if (this.getAge() == 40) {
               this.launchBlocks((int)this.getSize());
            }

            AABB aabb = this.getBoundingBox().inflate(this.getSize(), 15.0F * this.getVisualSize(), this.getSize());
            List<Entity> list = this.level().getEntities(this, aabb, this::canHitEntity);
            if (!list.isEmpty()) {
               for (Entity entity : list) {
                  if (!entity.getType().is(TensuraEntityTags.NO_FORCED_MOVE)) {
                     Changeable<Vec3> changeable = Changeable.of(entity.getDeltaMovement().add(0.0, 0.1, 0.0));
                     if (!((TensuraEntityEvents.ForceMovementEvent)TensuraEntityEvents.FORCE_MOVEMENT_EVENT.invoker())
                        .move(entity, this.getOwner(), this.getSkill(), changeable)
                        .isFalse()) {
                        entity.setDeltaMovement((Vec3)changeable.get());
                        entity.hurtMarked = true;
                        entity.hasImpulse = true;
                     }
                  }

                  if (this.getAge() % 10 == 0) {
                     DamageSource source = TensuraDamageTypes.getIndirectEntityDamageSource(
                        this.level(), TensuraDamageTypes.DEATH_TORNADO, this.getOwner(), this
                     );
                     TensuraDamageHelper.hurtSplitElemental(entity, source, 0.9F, this.getDamage());
                  }
               }
            }
         }
      }
   }

   protected void launchBlocks(int radius) {
      if (this.shouldGrief()) {
         SkillHelper.launchBlock(
            this,
            this.getOwner(),
            this.position(),
            radius + 1,
            1,
            radius / 6.0F,
            radius / 9.0F,
            blockState -> this.random.nextInt(3) != 1 ? false : blockState.is(TensuraBlockTags.SKILL_BREAK_EASY),
            blockPos -> true,
            this.getSkill()
         );
      }
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController[]{
            new AnimationController(
               this,
               "loopController",
               3,
               event -> {
                  if (this.getAge() < 40) {
                     return event.setAndContinue(RawAnimation.begin().thenPlayAndHold("animation.death_tornado.start"));
                  }

                  int time = this.getLife() - this.getAge();
                  return time < 40
                     ? event.setAndContinue(RawAnimation.begin().thenPlayAndHold("animation.death_tornado.stop"))
                     : event.setAndContinue(RawAnimation.begin().thenLoop("animation.death_tornado.loop"));
               }
            ),
            new AnimationController(this, "controller", 3, event -> PlayState.STOP)
               .triggerableAnim("start", RawAnimation.begin().then("animation.death_tornado.start", LoopType.PLAY_ONCE))
               .triggerableAnim("stop", RawAnimation.begin().thenPlayAndHold("animation.death_tornado.stop"))
         }
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
