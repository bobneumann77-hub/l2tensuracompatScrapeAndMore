package io.github.manasmods.tensura.ability.battlewill.projectile;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.battlewill.Battlewill;
import io.github.manasmods.tensura.config.ability.BattlewillConfig;
import io.github.manasmods.tensura.entity.projectile.magic.AuraBulletProjectile;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class DarkEightPalmsArt extends Battlewill {
   private static final BattlewillConfig.DarkEightPalms CONFIG = ((BattlewillConfig)ConfigRegistry.getConfig(BattlewillConfig.class)).DarkEightPalms;

   @Override
   public double getAuraCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.auraCost;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      instance.getOrCreateTag().putInt("PowerScale", 0);
      instance.markDirty();
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      CompoundTag tag = instance.getOrCreateTag();
      int power = tag.getInt("PowerScale");
      int time = instance.isMastered(entity) ? CONFIG.holdTimeMastered / 10 : CONFIG.holdTime / 10;
      if (heldTicks > 0 && heldTicks % time == 0 && power < CONFIG.maxMultiplier * 10.0) {
         tag.putInt("PowerScale", power + 1);
         instance.markDirty();
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
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      CompoundTag tag = instance.getOrCreateTag();
      int power = tag.getInt("PowerScale") / 10;
      tag.putInt("PowerScale", 0);
      instance.markDirty();
      if (power >= 1) {
         if (!EnergyHelper.isOutOfEnergy(entity, instance, mode, power)) {
            entity.swing(InteractionHand.MAIN_HAND, true);
            if (power >= 4) {
               instance.addMasteryPoint(entity);
            }

            Entity target = ObjectSelectionHelper.getTargetingEntity(entity, 40.0, false, true);
            Vec3 pos;
            if (target != null) {
               pos = target.getEyePosition();
            } else {
               BlockHitResult result = ObjectSelectionHelper.getPlayerPOVHitResult(entity.level(), entity, Fluid.NONE, 40.0);
               pos = result.getLocation().add(0.0, 0.5, 0.0);
            }

            this.spawnAuraBullets(instance, entity, pos, power, mode);
         }
      }
   }

   private void spawnAuraBullets(ManasSkillInstance instance, LivingEntity entity, Vec3 pos, int amount, int mode) {
      int rot = 360 / amount;

      for (int i = 0; i < amount; i++) {
         Vec3 bulletPos = entity.getEyePosition()
            .add(0.0, 2.0, 0.0)
            .add(
               new Vec3(0.0, 2.0, 0.0)
                  .zRot((rot * i - rot / 2.0F) * (float) (Math.PI / 180.0))
                  .xRot(-entity.getXRot() * (float) (Math.PI / 180.0))
                  .yRot(-entity.getYRot() * (float) (Math.PI / 180.0))
            );
         AuraBulletProjectile bullet = new AuraBulletProjectile(entity.level(), entity);
         bullet.setSpeed(1.5F);
         bullet.setPos(bulletPos);
         bullet.shootFromRot(pos.subtract(bulletPos).normalize());
         bullet.setLife(50);
         bullet.setDamage(CONFIG.baseDamage);
         bullet.setIgnoreInvulnerabilityOnHit(true);
         bullet.setExplosionRadius(3.0F);
         bullet.setSize(0.5F);
         bullet.setNoGravity(true);
         bullet.setSkill(entity, instance, this, mode);
         bullet.setColor(16766720);
         entity.level().addFreshEntity(bullet);
         entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.BLAZE_SHOOT, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      }
   }
}
