package io.github.manasmods.tensura.entity.projectile.magic;

import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.entity.projectile.TensuraFlyingProjectile;
import io.github.manasmods.tensura.registry.entity.ProjectileEntityTypes;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class AuraSlashProjectile extends TensuraFlyingProjectile implements GeoEntity {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public AuraSlashProjectile(EntityType<? extends AuraSlashProjectile> entityType, Level level) {
      super(entityType, level);
   }

   public AuraSlashProjectile(Level levelIn, LivingEntity shooter) {
      super((EntityType<? extends Projectile>)ProjectileEntityTypes.AURA_SLASH.get(), levelIn);
      this.setOwner(shooter);
   }

   @Override
   public ResourceLocation getTexture() {
      return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/projectiles/slash/aura_slash.png");
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.AURA_SLASH;
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
   protected boolean canHitEntity(Entity pTarget) {
      return !super.canHitEntity(pTarget) ? false : pTarget instanceof LivingEntity;
   }

   @Override
   protected void onHitBlock(BlockHitResult pResult) {
      super.onHitBlock(pResult);
      if (this.getSize() >= 4.0F && this.shouldGrief()) {
         this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.WARDEN_ATTACK_IMPACT, TensuraSkill.ABILITY_SOUND, 3.0F, 1.0F);
         Predicate<BlockPos> posPredicate = this.getOwner() != null ? pos -> !Objects.equals(pos, this.getOwner().getOnPos()) : pos -> true;
         SkillHelper.launchBlock(
            this,
            this.getOwner(),
            pResult.getLocation(),
            (int)(0.25 * this.getSize()),
            (int)(0.5 * this.getSize()),
            0.3F,
            0.2F,
            blockState -> this.random.nextInt(2) != 1 ? false : blockState.is(TensuraBlockTags.EARTH_SKILL_BREAKABLE),
            posPredicate,
            this.getSkill()
         );
      }
   }

   @Override
   public Optional<SoundEvent> hitSound() {
      return Optional.of(SoundEvents.PLAYER_ATTACK_SWEEP);
   }

   @Override
   public void flyingParticles() {
   }

   public void registerControllers(ControllerRegistrar controllers) {
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
