package io.github.manasmods.tensura.ability.battlewill.projectile;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.battlewill.Battlewill;
import io.github.manasmods.tensura.config.ability.BattlewillConfig;
import io.github.manasmods.tensura.entity.projectile.magic.AuraBulletProjectile;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.battlewill.ProjectileArts;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class MagicBulletArt extends Battlewill {
   private static final BattlewillConfig.MagicBullet CONFIG = ((BattlewillConfig)ConfigRegistry.getConfig(BattlewillConfig.class)).MagicBullet;

   @Override
   public double getAuraCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.auraCost;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      this.shootBullet(instance, entity, mode);
      AuraBulletProjectile bullet = new AuraBulletProjectile(entity.level(), entity);
      bullet.noPhysics = true;
      bullet.setSpeed(1.0F);
      bullet.setSize(0.4F);
      bullet.setColor(15788046);
      bullet.setSkill(entity, instance, this, mode);
      bullet.setNoGravity(true);
      bullet.setPos(this.getBulletPosition(entity, bullet));
      entity.level().addFreshEntity(bullet);
      CompoundTag tag = instance.getOrCreateTag();
      tag.putInt("BulletID", bullet.getId());
      tag.putInt("PowerScale", 0);
      instance.markDirty();
   }

   private Vec3 getBulletPosition(LivingEntity entity, AuraBulletProjectile bullet) {
      Vec3 look = entity.getLookAngle().scale(entity.getBbWidth() / 2.0F + bullet.getBbWidth());
      return entity.getEyePosition().add(look);
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      Level level = entity.level();
      CompoundTag tag = instance.getOrCreateTag();
      int id = tag.getInt("BulletID");
      if (level.getEntity(id) instanceof AuraBulletProjectile bullet) {
         bullet.setAge(0);
         bullet.setPos(this.getBulletPosition(entity, bullet));
         double max = instance.isMastered(entity) ? CONFIG.maxMultiplierMastered : CONFIG.maxMultiplier;
         int holdTime = instance.isMastered(entity) ? CONFIG.holdTimeMastered / 10 : CONFIG.holdTime / 10;
         if (heldTicks > 0 && heldTicks % holdTime == 0 && tag.getInt("PowerScale") < max * 10.0) {
            tag.putInt("PowerScale", tag.getInt("PowerScale") + 1);
            instance.markDirty();
            float power = tag.getInt("PowerScale") / 10.0F;
            int powerInt = (int)power;
            if (powerInt == 1) {
               bullet.setSkill(entity, instance, this, mode, power);
            }

            if (powerInt >= 1) {
               bullet.setSpeed(Math.min(0.4F * powerInt, 2.0F));
               bullet.setDamage(CONFIG.baseDamage * powerInt);
               if (powerInt > 4) {
                  bullet.setExplosionRadius(Math.max(powerInt - 3, 8));
               }
            }

            bullet.setSize(0.4F + 0.1F * power);
            bullet.setColor(this.getBulletColor(power));
         }

         TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.ELECTRIC_SPARK, 1.0);
         if (entity instanceof Player player) {
            player.displayClientMessage(
               Component.translatable("tensura.skill.power_scale", new Object[]{tag.getInt("PowerScale") / 10.0})
                  .setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GREEN)),
               true
            );
         }

         return true;
      } else {
         tag.putInt("BulletID", 0);
         instance.markDirty();
         return false;
      }
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      this.shootBullet(instance, entity, mode);
   }

   private void shootBullet(ManasSkillInstance instance, LivingEntity entity, int mode) {
      Level level = entity.level();
      CompoundTag tag = instance.getOrCreateTag();
      int id = tag.getInt("BulletID");
      if (level.getEntity(id) instanceof AuraBulletProjectile bullet) {
         int power = tag.getInt("PowerScale") / 10;
         tag.putInt("PowerScale", 0);
         instance.markDirty();
         if (power < 1) {
            bullet.discard();
            entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.EVOKER_CAST_SPELL, TensuraSkill.ABILITY_SOUND, 3.0F, 1.0F);
         } else if (EnergyHelper.isOutOfEnergy(entity, instance, mode, power)) {
            bullet.discard();
            entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.EVOKER_CAST_SPELL, TensuraSkill.ABILITY_SOUND, 3.0F, 1.0F);
         } else {
            entity.swing(InteractionHand.MAIN_HAND, true);
            if (power >= 2) {
               instance.addMasteryPoint(entity);
            }

            bullet.setSkill(entity, instance, this, mode, power);
            bullet.noPhysics = false;
            tag.putInt("BulletID", 0);
            bullet.shootFromRot(entity.getLookAngle());
            entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.BLAZE_SHOOT, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
         }
      } else {
         tag.putInt("BulletID", 0);
         instance.markDirty();
      }
   }

   public void onSkillMastered(ManasSkillInstance instance, LivingEntity entity) {
      if (!instance.isSubInstance()) {
         SkillHelper.learnSkill(entity, ((MaximumMagicBulletArt)ProjectileArts.MAXIMUM_MAGIC_BULLET.get()).createLearningInstance(entity));
      }
   }

   private int getBulletColor(float power) {
      if (power < 5.0F) {
         return AuraBulletProjectile.getColorBySize(power, 0.0F, 5.0F, 15788046, 16766720);
      } else if (power < 10.0F) {
         return AuraBulletProjectile.getColorBySize(power, 5.0F, 10.0F, 16766720, 5636095);
      } else {
         return power < 15.0F
            ? AuraBulletProjectile.getColorBySize(power, 10.0F, 15.0F, 5636095, 5416173)
            : AuraBulletProjectile.getColorBySize(power, 15.0F, 20.0F, 5416173, 7747016);
      }
   }
}
