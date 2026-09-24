package io.github.manasmods.tensura.entity.magic.barrier;

import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.extra.HeatWaveSkill;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.event.TensuraSkillEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class HeatStormEntity extends BarrierEntity implements GeoEntity {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public HeatStormEntity(EntityType<? extends HeatStormEntity> entityType, Level level) {
      super(entityType, level);
      this.noCulling = false;
      this.setFollowOwner(true);
      this.setBurnTicks(HeatWaveSkill.CONFIG.stormBurnTick);
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
      return TensuraDamageTypes.HEAT_WAVE;
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
   public void tick() {
      super.tick();
      if (this.tickCount % 2 == 0) {
         this.level().playSound(null, this.getX(), this.getY() - this.getSize(), this.getZ(), SoundEvents.FIRE_AMBIENT, TensuraSkill.ABILITY_SOUND, 2.0F, 1.0F);
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
                        if ((state.is(Blocks.WATER) || state.is(Blocks.BUBBLE_COLUMN) || state.is(BlockTags.SNOW))
                           && !((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
                              .grief(this.getSkill(), this.level(), this.getOwner(), pos.getX(), pos.getY(), pos.getZ())
                              .isFalse()) {
                           this.level().setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                           if (this.random.nextFloat() < 0.05F) {
                              this.level()
                                 .playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.GENERIC_BURN, TensuraSkill.ABILITY_SOUND, 0.1F, 1.0F);
                              ((ServerLevel)this.level())
                                 .sendParticles(ParticleTypes.CLOUD, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 5, 0.04, 0.06, 0.04, 0.05);
                           }

                           ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker())
                              .grief(this.getSkill(), this.level(), this.getOwner(), pos.getX(), pos.getY(), pos.getZ());
                        }

                        if (state.is(Blocks.SEAGRASS) || state.is(Blocks.TALL_SEAGRASS) || state.is(Blocks.KELP) || state.is(Blocks.KELP_PLANT)) {
                           if (!((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
                              .grief(this.getSkill(), this.level(), this.getOwner(), pos.getX(), pos.getY(), pos.getZ())
                              .isFalse()) {
                              this.level().destroyBlock(pos, true);
                              this.level().setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                              if (this.random.nextFloat() < 0.05F) {
                                 this.level()
                                    .playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.GENERIC_BURN, TensuraSkill.ABILITY_SOUND, 0.1F, 1.0F);
                                 ((ServerLevel)this.level())
                                    .sendParticles(ParticleTypes.CLOUD, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 5, 0.04, 0.06, 0.04, 0.05);
                              }

                              ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker())
                                 .grief(this.getSkill(), this.level(), this.getOwner(), pos.getX(), pos.getY(), pos.getZ());
                           }
                        } else if (state.hasProperty(BlockStateProperties.WATERLOGGED)
                           && (Boolean)state.getValue(BlockStateProperties.WATERLOGGED)
                           && !((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
                              .grief(this.getSkill(), this.level(), this.getOwner(), pos.getX(), pos.getY(), pos.getZ())
                              .isFalse()) {
                           this.level().setBlockAndUpdate(pos, (BlockState)state.setValue(BlockStateProperties.WATERLOGGED, false));
                           if (this.random.nextFloat() < 0.05F) {
                              this.level()
                                 .playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.GENERIC_BURN, TensuraSkill.ABILITY_SOUND, 0.1F, 1.0F);
                              ((ServerLevel)this.level())
                                 .sendParticles(ParticleTypes.CLOUD, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 5, 0.04, 0.06, 0.04, 0.05);
                           }

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
      controllers.add(new AnimationController(this, "controller", 0, event -> event.setAndContinue(RawAnimation.begin().thenLoop("animation.storm.loop"))));
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
