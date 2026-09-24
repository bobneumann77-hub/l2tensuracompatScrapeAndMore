package io.github.manasmods.tensura.ability.skill.extra;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.ExtraSkillConfig;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.entity.magic.breath.BreathEntity;
import io.github.manasmods.tensura.entity.projectile.magic.BlackFlameBallProjectile;
import io.github.manasmods.tensura.entity.projectile.magic.HellFlareProjectile;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.battlewill.ProjectileArts;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import io.github.manasmods.tensura.registry.skill.CommonSkills;
import io.github.manasmods.tensura.registry.skill.ExtraSkills;
import io.github.manasmods.tensura.registry.skill.IntrinsicSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;

public class BlackFlameSkill extends Skill {
   public static final ExtraSkillConfig.BlackFlame CONFIG = ((ExtraSkillConfig)ConfigRegistry.getConfig(ExtraSkillConfig.class)).BlackFlame;

   public BlackFlameSkill() {
      super(Skill.SkillType.EXTRA);
   }

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      if (!SkillUtils.hasSkill(entity, (ManasSkill)ExtraSkills.BLACK_LIGHTNING.get())) {
         return false;
      } else {
         return !SkillUtils.hasSkill(entity, (ManasSkill)ExtraSkills.MOLECULAR_MANIPULATION.get())
            ? false
            : SkillUtils.isSkillMastered(entity, (ManasSkill)IntrinsicSkills.FLAME_TRANSFORM.get());
      }
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
      return instance.getMastery() >= 0.0;
   }

   public int getModes(ManasSkillInstance instance) {
      return 4;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      if (reverse) {
         return switch (mode) {
            case 0 -> this.isModeLearnt(instance, mode) ? 3 : (this.canEquipHellFlare(entity) ? 2 : 1);
            case 1 -> 0;
            case 2 -> 1;
            case 3 -> this.canEquipHellFlare(entity) ? 2 : 1;
            default -> -1;
         };
      } else {
         return switch (mode) {
            case 0 -> 1;
            case 1 -> this.canEquipHellFlare(entity) ? 2 : 0;
            case 2 -> this.isModeLearnt(instance, mode) ? 3 : 0;
            default -> 0;
         };
      }
   }

   private boolean isModeLearnt(ManasSkillInstance instance, int mode) {
      return instance.getTag() == null
         ? false
         : instance.getTag().getDouble(this.getModeId(instance, mode)) >= BASE_CONFIG.Learning.learningPointRequirement * 2;
   }

   @Override
   public List<Integer> getModeLearningList(ManasSkillInstance instance) {
      return List.of(2);
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> "black_flame.breath";
         case 1 -> "black_flame.ball";
         case 2 -> "black_flame.hell_flare";
         case 3 -> "black_flame.limited_hell_flare";
         default -> super.getModeId(instance, mode);
      };
   }

   private boolean canEquipHellFlare(LivingEntity entity) {
      if (!SkillUtils.hasSkill(entity, (ManasSkill)ProjectileArts.OGRE_FLAME.get())) {
         return false;
      } else {
         return !SkillUtils.hasSkill(entity, (ManasSkill)CommonSkills.RANGED_BARRIER.get())
            ? false
            : SkillUtils.hasSkill(entity, (ManasSkill)ExtraSkills.FLAME_MANIPULATION.get())
               || SkillUtils.hasSkill(entity, (ManasSkill)ExtraSkills.FLAME_DOMINATION.get());
      }
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> CONFIG.magiculeCostBreath;
         case 1 -> CONFIG.magiculeCostBall;
         default -> CONFIG.magiculeCostFlare;
      };
   }

   public boolean onTouchEntity(ManasSkillInstance instance, LivingEntity attacker, LivingEntity target, DamageSource source, Changeable<Float> amount) {
      if (!instance.isToggled()) {
         return true;
      }

      if (EnergyHelper.isOutOfEnergy(attacker, 0.0, CONFIG.magiculeCostCoating)) {
         return true;
      }

      if (!TensuraDamageHelper.isPhysicalAttack(source) && !(source.getEntity() instanceof AbstractArrow)) {
         return true;
      }

      int level = this.isMastered(instance, attacker) ? 1 : 0;
      MobEffectInstance burn = new MobEffectInstance(
         TensuraMobEffects.getReference(TensuraMobEffects.BLACK_BURN), CONFIG.blackBurnDuration, level, false, false, false
      );
      target.setRemainingFireTicks(Math.max(target.getRemainingFireTicks(), CONFIG.blackBurnDuration));
      TensuraMobEffect.addEffect(target, burn, attacker, this, 0);
      attacker.level()
         .playSound(null, target.getX(), target.getY(), target.getZ(), (SoundEvent)TensuraSoundEvents.CAST_FIRE.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 1.0F);
      TensuraParticleHelper.addServerParticlesAroundSelf(target, (ParticleOptions)TensuraParticleTypes.BLACK_FIRE.get(), 1.0);
      return true;
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (mode != 0) {
         return false;
      }

      if (heldTicks % 20 == 0 && EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
         return false;
      }

      if (heldTicks % BASE_CONFIG.Mastery.masteryHoldTick == 0 && heldTicks > 0) {
         instance.addMasteryPoint(entity);
      }

      float damage = instance.isMastered(entity) ? CONFIG.breathFlameDamageMastered : CONFIG.breathFlameDamage;
      float magicDamage = instance.isMastered(entity) ? CONFIG.breathMagicDamageMastered : CONFIG.breathMagicDamage;
      BreathEntity breath = BreathEntity.spawnBreathEntity(
         (EntityType<? extends BreathEntity>)MiscEntityTypes.BLACK_FLAME_BREATH.get(), entity, instance, damage, this, mode
      );
      if (breath != null) {
         breath.setSecondaryDamage(magicDamage);
      }

      entity.level()
         .playSound(null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.BREATH_FIRE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      return true;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      Level level = entity.level();
      CompoundTag tag = instance.getOrCreateTag();
      switch (mode) {
         case 0:
            instance.getOrCreateTag().putInt("BreathEntity", 0);
            instance.markDirty();
            break;
         case 1:
            if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
               return;
            }

            instance.addMasteryPoint(entity);
            instance.setCoolDown(CONFIG.ballCooldown, mode);
            BlackFlameBallProjectile ball = new BlackFlameBallProjectile(entity.level(), entity);
            ball.setDamage(CONFIG.ballFlameDamage);
            ball.setSecondaryDamage(CONFIG.ballMagicDamage);
            ball.setSpeed(1.5F);
            ball.setExplosionRadius(1.0F);
            ball.setSkill(entity, instance, this, mode);
            ball.setPosAndShoot(entity);
            level.addFreshEntity(ball);
            level.playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_FIRE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
            );
            break;
         case 2:
            if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
               return;
            }

            if (this.learnMode(instance, entity, mode)) {
               return;
            }

            instance.setCoolDown(CONFIG.flareCooldown, mode);
            double masteryPoint = tag.getDouble(this.getModeId(instance, mode));
            if (masteryPoint < BASE_CONFIG.Learning.learningPointRequirement * 2) {
               tag.putDouble(this.getModeId(instance, mode), masteryPoint + this.getDefaultMasteryIncrement(instance, entity, mode));
               if (tag.getDouble(this.getModeId(instance, mode)) >= BASE_CONFIG.Learning.learningPointRequirement * 2 && entity instanceof Player player) {
                  player.displayClientMessage(
                     Component.translatable("tensura.skill.mastery", new Object[]{this.getModeName(instance, mode)})
                        .setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)),
                     false
                  );
               }

               instance.markDirty();
            }

            HellFlareProjectile projectile = new HellFlareProjectile(entity.level(), entity);
            projectile.setDamage(CONFIG.flareFlameDamage);
            projectile.setSecondaryDamage(CONFIG.flareMagicDamage);
            projectile.setSpeed(1.5F);
            projectile.setAreaLife(80);
            projectile.setAreaRadius(CONFIG.flareRadius);
            projectile.setSkill(entity, instance, this, mode);
            projectile.setPosAndShoot(entity);
            entity.level().addFreshEntity(projectile);
            entity.level()
               .playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_FIRE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
            break;
         case 3:
            if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
               return;
            }

            instance.setCoolDown(CONFIG.flareCooldownLimited, mode);
            HellFlareProjectile projectile = new HellFlareProjectile(entity.level(), entity);
            projectile.setDamage(CONFIG.flareFlameDamageLimited);
            projectile.setSecondaryDamage(CONFIG.flareMagicDamageLimited);
            projectile.setSpeed(1.5F);
            projectile.setLimited(true);
            projectile.setAreaLife(80);
            projectile.setAreaRadius(CONFIG.flareRadiusLimited);
            projectile.setSkill(entity, instance, this, mode);
            projectile.setPosAndShoot(entity);
            entity.level().addFreshEntity(projectile);
            entity.level()
               .playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_FIRE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
      }
   }
}
