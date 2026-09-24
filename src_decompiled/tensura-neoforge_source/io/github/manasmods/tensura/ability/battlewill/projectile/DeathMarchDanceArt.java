package io.github.manasmods.tensura.ability.battlewill.projectile;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.battlewill.Battlewill;
import io.github.manasmods.tensura.config.ability.BattlewillConfig;
import io.github.manasmods.tensura.entity.projectile.magic.AuraBulletProjectile;
import io.github.manasmods.tensura.registry.battlewill.ProjectileArts;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class DeathMarchDanceArt extends Battlewill {
   private static final BattlewillConfig.DeathMarchDance CONFIG = ((BattlewillConfig)ConfigRegistry.getConfig(BattlewillConfig.class)).DeathMarchDance;

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      return SkillUtils.isSkillMastered(entity, (ManasSkill)ProjectileArts.MAXIMUM_MAGIC_BULLET.get());
   }

   @Override
   public double getAuraCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.auraCost;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      AuraBulletProjectile bullet = new AuraBulletProjectile(entity.level(), entity);
      bullet.setColor(16733695);
      bullet.setSize(0.75F);
      bullet.setSkill(entity, instance, this, mode);
      bullet.setLife(1200);
      bullet.setNoGravity(true);
      bullet.noPhysics = true;
      bullet.setPos(entity.position().add(0.0, entity.getBbHeight() + bullet.getBbHeight(), 0.0));
      entity.level().addFreshEntity(bullet);
      CompoundTag tag = instance.getOrCreateTag();
      tag.putInt("BulletID", bullet.getId());
      instance.markDirty();
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      Level level = entity.level();
      CompoundTag tag = instance.getOrCreateTag();
      int id = tag.getInt("BulletID");
      if (level.getEntity(id) instanceof AuraBulletProjectile bullet) {
         float oldSize = bullet.getSize();
         int time = instance.isMastered(entity) ? CONFIG.holdTimeMastered / 10 : CONFIG.holdTime / 10;
         double maxSize = (instance.isMastered(entity) ? CONFIG.maxMultiplierMastered : CONFIG.maxMultiplier) / 2.5;
         bullet.setPos(entity.position().add(0.0, entity.getBbHeight() + bullet.getBbHeight(), 0.0));
         bullet.setAge(0);
         double timer;
         if (oldSize < maxSize) {
            if (heldTicks > 0 && heldTicks % time == 0) {
               if (EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
                  this.spawnAuraBullets(instance, entity, (int)bullet.getSize() * 20, bullet.position(), mode);
                  bullet.discard();
                  entity.level()
                     .playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.EVOKER_CAST_SPELL, TensuraSkill.ABILITY_SOUND, 3.0F, 1.0F);
                  return false;
               }

               bullet.setSize(oldSize + 0.025F);
               bullet.setColor(16733695);
            }

            timer = Math.min(heldTicks / 10.0 / time, maxSize * 2.5);
         } else {
            timer = maxSize * 2.5;
         }

         if (entity instanceof Player player) {
            player.displayClientMessage(
               Component.translatable("tensura.skill.power_scale", new Object[]{SkillUtils.ROUND_DOUBLE.format(timer)})
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
      Level level = entity.level();
      CompoundTag tag = instance.getOrCreateTag();
      int id = tag.getInt("BulletID");
      if (level.getEntity(id) instanceof AuraBulletProjectile bullet) {
         this.spawnAuraBullets(instance, entity, (int)bullet.getSize() * 20, bullet.position(), mode);
         bullet.discard();
         entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.EVOKER_CAST_SPELL, TensuraSkill.ABILITY_SOUND, 3.0F, 1.0F);
      } else {
         tag.putInt("BulletID", 0);
         instance.markDirty();
      }
   }

   private void spawnAuraBullets(ManasSkillInstance instance, LivingEntity entity, int amount, Vec3 pos, int mode) {
      if (amount > 0) {
         if (amount >= 3) {
            instance.addMasteryPoint(entity);
         }

         Entity target = ObjectSelectionHelper.getTargetingEntity(entity, CONFIG.range, 0.5, true, true);
         float offsetY = 1.0F;

         for (int i = 0; i < amount; i++) {
            float angle = (float)((Math.PI * 2) * i / amount);
            float randomRadius = 1.75F + (entity.getRandom().nextFloat() - 0.5F) * 0.5F;
            float circleX = (float)Math.cos(angle) * randomRadius;
            float circleZ = (float)Math.sin(angle) * randomRadius;
            Vec3 bulletPos = pos.add(new Vec3(circleX, offsetY * entity.getRandom().nextFloat(), circleZ));
            AuraBulletProjectile bullet = new AuraBulletProjectile(entity.level(), entity);
            bullet.setColor(16733695);
            bullet.setSize(0.25F);
            bullet.setSpeed(0.5F);
            bullet.setNoGravity(true);
            bullet.setPos(bulletPos);
            bullet.shootFromRot(bulletPos.subtract(pos).normalize().scale(0.1F));
            bullet.setDelayTick(5 + entity.getRandom().nextInt(-1, 10));
            bullet.setLookDistance(30.0F);
            bullet.setDamage(CONFIG.baseDamage);
            bullet.setIgnoreInvulnerabilityOnHit(true);
            bullet.setExplosionRadius(4.0F);
            bullet.setSkill(entity, instance, this, mode);
            bullet.setHomingTarget(target);
            entity.level().addFreshEntity(bullet);
            entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.BLAZE_SHOOT, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
         }
      }
   }
}
