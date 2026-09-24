package io.github.manasmods.tensura.entity.magic.barrier;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.race.RaceUtils;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.registry.skill.ResistanceSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class DisintegrationEntity extends BarrierEntity implements GeoEntity {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private boolean playedSpawnSound = false;

   public DisintegrationEntity(Level level, LivingEntity entity) {
      this((EntityType<? extends DisintegrationEntity>)MiscEntityTypes.DISINTEGRATION.get(), level);
      this.setOwner(entity);
      this.setBurnTicks(100);
   }

   public DisintegrationEntity(EntityType<? extends DisintegrationEntity> entityType, Level level) {
      super(entityType, level);
      this.setBurnTicks(100);
      this.setElement(Element.HOLY);
   }

   @Override
   public boolean canWalkThrough() {
      return true;
   }

   @Override
   public boolean canWalkThrough(Entity entity) {
      Entity owner = this.getOwner();
      return owner != null && (entity.isAlliedTo(owner) || entity == owner);
   }

   @Override
   public boolean hurt(DamageSource pSource, float pAmount) {
      return false;
   }

   @Override
   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.DISINTEGRATION;
   }

   @Override
   public boolean canHitEntity(Entity pTarget) {
      return pTarget == this.getOwner() ? false : super.canHitEntity(pTarget);
   }

   @Override
   public void tick() {
      super.tick();
      if (this.level().isClientSide()) {
         if (!this.playedSpawnSound) {
            this.level().playLocalSound(this, (SoundEvent)TensuraSoundEvents.DISINTEGRATION.get(), TensuraSkill.ABILITY_SOUND, 5.0F, 1.0F);
            this.playedSpawnSound = true;
         }
      } else if (this.getOwner() == null) {
         this.remove();
      }
   }

   @Override
   protected void hitTarget() {
      super.hitTarget();

      for (ItemEntity target : this.level().getEntitiesOfClass(ItemEntity.class, this.getAffectedArea())) {
         target.hurt(this.getDamageSource(0.0F), 100.0F);
      }
   }

   @Override
   public void applyEffect(LivingEntity entity) {
      if (this.getAge() < this.getAstraBindTick(entity)) {
         entity.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.MOVEMENT_INTERFERENCE), 20, 9, false, false, false));
         entity.teleportTo(this.position().x(), this.position().y(), this.position().z());
         entity.setDeltaMovement(Vec3.ZERO);
         entity.hurtMarked = true;
      } else {
         entity.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.MOVEMENT_INTERFERENCE), 20, 4, false, false, false));
      }

      if (entity instanceof Player player && player.getAbilities().flying) {
         player.getAbilities().flying = false;
         player.onUpdateAbilities();
      }

      if (this.getAge() >= 215 && this.getAge() <= 275) {
         super.applyEffect(entity);
      }
   }

   @Override
   protected boolean dealDamage(Entity target) {
      return target instanceof LivingEntity living ? this.dealDamage(target, living.getMaxHealth() * 10.0F, 1.0F) : super.dealDamage(target);
   }

   @Override
   public DamageSource getDamageSource(ResourceKey<DamageType> type, float costMultiplier) {
      return super.getDamageSource(type, costMultiplier).tensura$setBarrierBypassLevel(3.0F).tensura$setResistanceBypassLevel(2.0F);
   }

   private int getAstraBindTick(LivingEntity entity) {
      double tick = 300.0;
      if (RaceUtils.isSpiritual(entity)) {
         tick -= tick * 0.25;
      }

      if (SkillUtils.isSkillToggled(entity, (ManasSkill)ResistanceSkills.SPIRITUAL_ATTACK_NULLIFICATION.get())) {
         tick -= tick * 0.5;
      } else if (SkillUtils.isSkillToggled(entity, (ManasSkill)ResistanceSkills.SPIRITUAL_ATTACK_RESISTANCE.get())) {
         tick -= tick * 0.25;
      }

      return Mth.ceil(tick);
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController(this, "loopController", 0, event -> event.setAndContinue(RawAnimation.begin().thenLoop("animation.disintegration.start")))
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
