package io.github.manasmods.tensura.ability.battlewill.projectile;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.battlewill.Battlewill;
import io.github.manasmods.tensura.config.ability.BattlewillConfig;
import io.github.manasmods.tensura.entity.projectile.magic.AuraSlashProjectile;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.battlewill.MeleeArts;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public class OgreSwordCannonArt extends Battlewill {
   private static final BattlewillConfig.OgreSwordCannon CONFIG = ((BattlewillConfig)ConfigRegistry.getConfig(BattlewillConfig.class)).OgreSwordCannon;

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      return SkillUtils.isSkillMastered(entity, (ManasSkill)MeleeArts.OGRE_SWORD_GUILLOTINE.get());
   }

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
      double max = instance.isMastered(entity) ? CONFIG.maxMultiplierMastered : CONFIG.maxMultiplier;
      int holdTime = instance.isMastered(entity) ? CONFIG.holdTimeMastered / 10 : CONFIG.holdTime / 10;
      if (heldTicks > 0 && heldTicks % holdTime == 0 && power < max * 10.0) {
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
            if (power >= 2) {
               instance.addMasteryPoint(entity);
            }

            AuraSlashProjectile slash = new AuraSlashProjectile(entity.level(), entity);
            slash.setSpeed(0.5F);
            slash.setPiercingEntity(true);
            slash.setPiercingBlock(true);
            slash.setDamage((float)(entity.getAttributeValue(Attributes.ATTACK_DAMAGE) * (CONFIG.baseMultiplier + CONFIG.bonusMultiplier * power)));
            slash.setSize(power * 2);
            slash.setLife(20 * power);
            slash.setSkill(entity, instance, this, mode, power);
            slash.setNoGravity(true);
            slash.shootFromRot(entity.getLookAngle().multiply(1.0, 0.0, 1.0));
            slash.setPos(entity.position().add(0.0, entity.getBbHeight() / 2.0F - slash.getBbHeight() / 2.0F, 0.0));
            entity.level().addFreshEntity(slash);
            entity.level()
               .playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
         }
      }
   }
}
