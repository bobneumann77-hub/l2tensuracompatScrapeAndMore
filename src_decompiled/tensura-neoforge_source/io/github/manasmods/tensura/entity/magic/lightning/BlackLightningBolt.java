package io.github.manasmods.tensura.entity.magic.lightning;

import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BlackLightningBolt extends TensuraLightningBolt implements GeoEntity {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public BlackLightningBolt(EntityType<? extends BlackLightningBolt> type, Level pLevel) {
      super(type, pLevel);
      this.setElementalAttack(true);
   }

   public BlackLightningBolt(Level pLevel, Entity owner) {
      super((EntityType<? extends net.minecraft.world.entity.LightningBolt>)MiscEntityTypes.BLACK_LIGHTNING_BOLT.get(), pLevel, owner);
   }

   @Override
   protected BlockState getFireBlock() {
      return ((Block)TensuraBlocks.BLACK_FIRE.get()).defaultBlockState();
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.BLACK_LIGHTNING;
   }

   @Override
   protected ParticleOptions getLightningParticle() {
      return (ParticleOptions)TensuraParticleTypes.BLACK_LIGHTNING_SPARK.get();
   }

   @Override
   protected int getDamageTick() {
      return 19;
   }

   @Override
   protected void spawnImpactParticles() {
      if (this.life % 3 == 0) {
         super.spawnImpactParticles();
      }

      if (this.life % 4 == 0) {
         TensuraParticleHelper.addServerParticlesAroundSelf(this, ParticleTypes.EXPLOSION_EMITTER, this.getRadius() * 4.0F);
      }
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController(this, "controller", 0, event -> event.setAndContinue(RawAnimation.begin().thenPlayAndHold("animation.black_lightning.start")))
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
