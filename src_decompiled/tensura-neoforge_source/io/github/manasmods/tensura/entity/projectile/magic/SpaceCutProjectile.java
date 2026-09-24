package io.github.manasmods.tensura.entity.projectile.magic;

import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.entity.projectile.TensuraFlyingProjectile;
import io.github.manasmods.tensura.registry.entity.ProjectileEntityTypes;
import java.util.Optional;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class SpaceCutProjectile extends TensuraFlyingProjectile implements GeoEntity {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public SpaceCutProjectile(EntityType<? extends SpaceCutProjectile> type, Level level) {
      super(type, level);
      this.setElementalAttack(true);
      this.setSize(1.5F);
      this.setElement(Element.SPACE);
   }

   public SpaceCutProjectile(Level worldIn, LivingEntity shooter) {
      this((EntityType<? extends SpaceCutProjectile>)ProjectileEntityTypes.SPACE_CUT.get(), worldIn);
      this.setOwner(shooter);
      this.setVisible(false);
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.SPACE_ELEMENTAL;
   }

   @Override
   public boolean shouldDiscardInLava() {
      return false;
   }

   @Override
   public boolean shouldDiscardInWater() {
      return false;
   }

   @Override
   public ResourceLocation getTexture() {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/projectiles/slash/dimension_cut.png");
   }

   @Override
   public Optional<SoundEvent> hitSound() {
      return Optional.empty();
   }

   @Override
   public void flyingParticles() {
   }

   @Override
   public void hitParticles(double x, double y, double z) {
   }

   public void registerControllers(ControllerRegistrar controllers) {
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
