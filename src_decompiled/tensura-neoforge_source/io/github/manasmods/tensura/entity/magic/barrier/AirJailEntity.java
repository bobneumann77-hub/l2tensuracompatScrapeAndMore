package io.github.manasmods.tensura.entity.magic.barrier;

import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.event.TensuraSkillEvents;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.Alignment;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.Animation.LoopType;
import software.bernie.geckolib.util.GeckoLibUtil;

public class AirJailEntity extends BarrierEntity implements GeoEntity {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public AirJailEntity(EntityType<? extends AirJailEntity> entityType, Level level) {
      super(entityType, level);
   }

   @Override
   public boolean shouldCreateParts() {
      return false;
   }

   @Override
   public boolean hurt(DamageSource pSource, float pAmount) {
      return false;
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.WIND_ELEMENTAL;
   }

   @Override
   public boolean canHitEntity(Entity pTarget) {
      if (!pTarget.isAlive() || !pTarget.isPickable()) {
         return false;
      } else {
         return pTarget.isSpectator() ? false : !(pTarget instanceof Player player && player.isCreative());
      }
   }

   @Override
   public int getDelayHitTime() {
      return 40;
   }

   @Override
   public void tick() {
      super.tick();
      if (this.tickCount % 2 == 0) {
         this.level()
            .playSound(null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.WIND_BLOW.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      }

      if (!this.level().isClientSide()) {
         for (LivingEntity target : this.getAffectedEntities()) {
            if (this.canHitEntity(target) && Alignment.shouldConsumeAir(target)) {
               int airSupply = target.getAirSupply();
               target.setAirSupply(airSupply - 5);
               if (airSupply - 5 <= -20) {
                  target.setAirSupply(0);
                  target.hurt(TensuraDamageTypes.getDamageSource(target.level(), TensuraDamageTypes.SUFFOCATE), 1.0F);
               }
            }
         }

         if (this.getAge() % 5 == 0 && this.shouldGrief()) {
            if (!this.level().isLoaded(this.blockPosition())) {
               return;
            }

            BlockPos.betweenClosedStream(new AABB(this.blockPosition()).inflate(this.getVisualSize()))
               .forEach(
                  pos -> {
                     if (!(this.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) > this.getVisualSize() * this.getVisualSize())) {
                        BlockState state = this.level().getBlockState(pos);
                        if (state.is(BlockTags.FIRE)
                           && !((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
                              .grief(this.getSkill(), this.level(), this.getOwner(), pos.getX(), pos.getY(), pos.getZ())
                              .isFalse()) {
                           this.level().removeBlock(pos, false);
                           ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker())
                              .grief(this.getSkill(), this.level(), this.getOwner(), pos.getX(), pos.getY(), pos.getZ());
                        }
                     }
                  }
               );
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
               event -> this.getLife() - this.getAge() < 60
                  ? event.setAndContinue(RawAnimation.begin().thenPlayAndHold("animation.jail_sphere.end"))
                  : event.setAndContinue(RawAnimation.begin().thenLoop("animation.jail_sphere.loop"))
            ),
            new AnimationController(this, "controller", 0, event -> PlayState.STOP)
               .triggerableAnim("start", RawAnimation.begin().then("animation.jail_sphere.start", LoopType.PLAY_ONCE))
         }
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
