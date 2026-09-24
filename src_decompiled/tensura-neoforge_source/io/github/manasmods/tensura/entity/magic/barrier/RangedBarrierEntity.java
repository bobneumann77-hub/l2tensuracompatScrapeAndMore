package io.github.manasmods.tensura.entity.magic.barrier;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.skill.resist.PhysicalAttackNullification;
import io.github.manasmods.tensura.ability.skill.resist.PhysicalAttackResistance;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.enchantment.TensuraEnchantmentHelper;
import io.github.manasmods.tensura.enchantment.TensuraEnchantments;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.registry.skill.ResistanceSkills;
import io.github.manasmods.tensura.registry.skill.UniqueSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class RangedBarrierEntity extends BarrierEntity implements GeoEntity {
   public static final ResourceLocation[] RANGED_BARRIER = new ResourceLocation[]{
      ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/barrier/ranged_barrier_start_0.png"),
      ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/barrier/ranged_barrier_start_1.png"),
      ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/barrier/ranged_barrier_start_2.png"),
      ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/barrier/ranged_barrier_start_3.png"),
      ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/barrier/ranged_barrier_start_4.png"),
      ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/barrier/ranged_barrier_start_5.png"),
      ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/barrier/ranged_barrier_start_6.png"),
      ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/barrier/ranged_barrier_start_7.png"),
      ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/barrier/ranged_barrier_start_8.png"),
      ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/barrier/ranged_barrier_start_9.png")
   };
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public RangedBarrierEntity(Level level, LivingEntity entity) {
      this((EntityType<? extends RangedBarrierEntity>)MiscEntityTypes.RANGED_BARRIER.get(), level);
      this.setOwner(entity);
   }

   public RangedBarrierEntity(EntityType<? extends RangedBarrierEntity> entityType, Level level) {
      super(entityType, level);
   }

   @Override
   protected void updateVisualSize() {
   }

   @Override
   public boolean shouldRenderAtSqrDistance(double pDistance) {
      return true;
   }

   private float getPhysicalResistances(DamageSource source) {
      if (!TensuraDamageHelper.isPhysicalAttack(source)) {
         return 1.0F;
      }

      if (((PhysicalAttackNullification)ResistanceSkills.PHYSICAL_ATTACK_NULLIFICATION.get()).isNullificationBypass(source)) {
         return 1.0F;
      }

      if (this.getOwner() instanceof LivingEntity owner) {
         if (SkillUtils.isSkillToggled(owner, (ManasSkill)ResistanceSkills.PHYSICAL_ATTACK_NULLIFICATION.get())) {
            return ((PhysicalAttackNullification)ResistanceSkills.PHYSICAL_ATTACK_NULLIFICATION.get()).isResistanceBypass(source) ? 0.5F : 0.0F;
         } else if (SkillUtils.isSkillToggled(owner, (ManasSkill)ResistanceSkills.PHYSICAL_ATTACK_RESISTANCE.get())) {
            return ((PhysicalAttackResistance)ResistanceSkills.PHYSICAL_ATTACK_RESISTANCE.get()).isResistanceBypass(source) ? 1.0F : 0.5F;
         } else {
            return 1.0F;
         }
      } else {
         return 1.0F;
      }
   }

   @Override
   public boolean hurt(DamageSource pSource, float pAmount) {
      if (this.isInvulnerableTo(pSource)) {
         return false;
      }

      if (!this.level().isClientSide() && !this.isRemoved()) {
         if (pSource.getDirectEntity() instanceof LivingEntity attacker && shouldInstaBreak(attacker, this.getOwner())) {
            pAmount = this.getHealth();
         } else {
            pAmount *= this.getPhysicalResistances(pSource);
         }

         this.setHealth(this.getHealth() - pAmount);
         this.markHurt();
         this.gameEvent(GameEvent.ENTITY_DAMAGE, pSource.getEntity());
         if (this.getHealth() <= 0.0F) {
            this.discard();
            this.level()
               .playSound(null, this.getX(), this.getY(), this.getZ(), (SoundEvent)TensuraSoundEvents.BARRIER_BREAK.get(), this.getSoundSource(), 2.0F, 1.0F);
         } else {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.SHIELD_BLOCK, this.getSoundSource(), 2.0F, 1.0F);
         }
      }

      return true;
   }

   public static boolean shouldInstaBreak(LivingEntity attacker, @Nullable Entity target) {
      if (TensuraStorages.getAbilityFrom(attacker).isAbilityInActivePreset((ManasSkill)UniqueSkills.ANTI_SKILL.get())
         || SkillUtils.isSkillToggled(attacker, (ManasSkill)UniqueSkills.ANTI_SKILL.get())) {
         return true;
      } else if (SkillUtils.isSkillToggled(attacker, (ManasSkill)UniqueSkills.COOK.get())) {
         return true;
      } else {
         return TensuraEnchantmentHelper.getEnchantmentLevel(attacker.level(), TensuraEnchantments.MAGIC_INTERFERENCE, attacker.getMainHandItem()) > 0
               && target instanceof LivingEntity entity
            ? TensuraStorages.getExistenceFrom(entity).getMagicule() < TensuraStorages.getExistenceFrom(attacker).getMagicule() * 1.5
            : false;
      }
   }

   protected void markHurt() {
      this.hurtMarked = true;

      for (BarrierPart part : this.parts) {
         part.addServerParticlesAroundSelf(ParticleTypes.WHITE_ASH, 0.5);
         part.addServerParticlesAroundSelf(ParticleTypes.WHITE_ASH, 0.5);
         part.addServerParticlesAroundSelf(ParticleTypes.WHITE_ASH, 0.5);
      }
   }

   public void registerControllers(ControllerRegistrar controllers) {
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
