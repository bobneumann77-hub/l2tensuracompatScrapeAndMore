package io.github.manasmods.tensura.entity.magic.barrier;

import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.event.TensuraSkillEvents;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
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

public class WaterJailEntity extends BarrierEntity implements GeoEntity {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public WaterJailEntity(EntityType<? extends WaterJailEntity> entityType, Level level) {
      super(entityType, level);
   }

   public WaterJailEntity(Level pLevel, LivingEntity pOwner) {
      this((EntityType<? extends WaterJailEntity>)MiscEntityTypes.WATER_JAIL.get(), pLevel);
      this.setOwner(pOwner);
   }

   @Override
   public boolean hurt(DamageSource pSource, float pAmount) {
      return false;
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.WATER_BLADE;
   }

   @Override
   public ResourceKey<DamageType> getSecondaryDamageType() {
      return TensuraDamageTypes.WATER_ELEMENTAL;
   }

   @Override
   public boolean canHitEntity(Entity pTarget) {
      if (pTarget == this.getOwner()) {
         return false;
      } else {
         return !super.canHitEntity(pTarget) ? false : !(this.getOwner() instanceof LivingEntity owner && owner.isAlliedTo(pTarget));
      }
   }

   @Override
   public int getDelayHitTime() {
      return 40;
   }

   @Override
   public void tick() {
      super.tick();
      if (this.tickCount % 3 == 0) {
         this.level()
            .playSound(null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.CAST_WATER.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      }

      if (!this.level().isClientSide()) {
         if (this.getAge() % 5 == 0 && this.shouldGrief()) {
            if (!this.level().isLoaded(this.blockPosition())) {
               return;
            }

            BlockPos.betweenClosedStream(new AABB(this.blockPosition()).inflate(this.getVisualSize()))
               .forEach(
                  pos -> {
                     if (!(this.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) > this.getVisualSize() * this.getVisualSize())) {
                        BlockState state = this.level().getBlockState(pos);
                        if (state.is(BlockTags.FIRE)) {
                           if (!((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
                              .grief(this.getSkill(), this.level(), this.getOwner(), pos.getX(), pos.getY(), pos.getZ())
                              .isFalse()) {
                              this.level().removeBlock(pos, false);
                              ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker())
                                 .grief(this.getSkill(), this.level(), this.getOwner(), pos.getX(), pos.getY(), pos.getZ());
                           }
                        } else if (state.is(Blocks.LAVA)
                           && !((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
                              .grief(this.getSkill(), this.level(), this.getOwner(), pos.getX(), pos.getY(), pos.getZ())
                              .isFalse()) {
                           this.level()
                              .setBlockAndUpdate(
                                 pos, this.level().getFluidState(pos).isSource() ? Blocks.OBSIDIAN.defaultBlockState() : Blocks.COBBLESTONE.defaultBlockState()
                              );
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
