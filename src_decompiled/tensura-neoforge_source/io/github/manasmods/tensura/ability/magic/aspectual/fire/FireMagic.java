package io.github.manasmods.tensura.ability.magic.aspectual.fire;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.projectile.magic.FireBoltProjectile;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class FireMagic extends AspectualMagic {
   private static final AspectualMagicConfig.Fire CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).Fire;

   public FireMagic() {
      super(AspectualMagic.AspectualType.FIRE);
   }

   @Override
   public int getDefaultCastTime() {
      return CONFIG.castTime;
   }

   @Override
   public int getMasteryCastTime() {
      return CONFIG.castTimeMastered;
   }

   public int getMaxMastery() {
      return MAGIC_CONFIG.AspectualMagic.masteryLow;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   @Override
   protected void applyCastingVisual(ManasSkillInstance instance, Player entity, int heldTicks, int mode, int castTime) {
      super.applyCastingVisual(instance, entity, heldTicks, mode, castTime);
      if (castTime > 1) {
         MagicCircle.castMagicCircle(
            0.75F,
            25,
            MagicCircleVariant.FLAME,
            entity,
            instance.getOrCreateTag(),
            0.75F,
            Vec3.ZERO,
            instance,
            mode,
            Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
         );
      }
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (heldTicks >= this.getCastingTime(instance, entity)) {
         if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            this.applyMultiShot(instance, entity, mode, instance.isMastered(entity) ? 3 : 1);
            entity.level()
               .playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_FIRE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
            entity.swing(InteractionHand.MAIN_HAND, true);
            instance.addMasteryPoint(entity);
         }
      }
   }

   protected void applyMultiShot(ManasSkillInstance instance, LivingEntity entity, int mode, int amount) {
      float h = 10.0F * ((amount - 1) / 2.0F);
      float i = amount == 1 ? 0.0F : 2.0F * h / (amount - 1);
      float j = (amount - 1) % 2 * i / 2.0F;
      float k = 1.0F;

      for (int l = 0; l < amount; l++) {
         float m = j + k * ((l + 1) / 2) * i;
         k = -k;
         FireBoltProjectile bolt = new FireBoltProjectile(entity.level(), entity);
         bolt.setDamage(CONFIG.fireDamage);
         bolt.setSecondaryDamage(CONFIG.magicDamage);
         bolt.setSkill(entity, instance, this, mode);
         bolt.setBurnTicks(100);
         bolt.setNoGravity(true);
         bolt.setPos(entity.getEyePosition().add(0.0, -0.25, 0.0).add(entity.getLookAngle().normalize()));
         this.shootProjectile(entity, bolt, 1.2F, m);
         entity.level().addFreshEntity(bolt);
      }
   }

   protected void shootProjectile(LivingEntity livingEntity, Projectile projectile, float speed, float multishot) {
      Vec3 vec3 = livingEntity.getUpVector(1.0F);
      Quaternionf quaternionf = new Quaternionf().setAngleAxis(multishot * 0.0174, vec3.x, vec3.y, vec3.z);
      Vec3 vec32 = livingEntity.getViewVector(1.0F);
      Vector3f vector3f = vec32.toVector3f().rotate(quaternionf);
      projectile.shoot(vector3f.x(), vector3f.y(), vector3f.z(), speed, 0.0F);
   }
}
