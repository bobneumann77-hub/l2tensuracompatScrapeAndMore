package io.github.manasmods.tensura.ability.magic.aspectual.ice;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.config.ability.magic.AspectualMagicConfig;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.projectile.magic.IceLanceProjectile;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.registry.magic.AspectualMagics;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class IceBreakerMagic extends AspectualMagic {
   private static final AspectualMagicConfig.IceBreaker CONFIG = ((AspectualMagicConfig)ConfigRegistry.getConfig(AspectualMagicConfig.class)).IceBreaker;

   public IceBreakerMagic() {
      super(AspectualMagic.AspectualType.ICE);
   }

   @Override
   public int getDefaultCastTime() {
      return CONFIG.castTime;
   }

   public int getMaxMastery() {
      return MAGIC_CONFIG.AspectualMagic.masteryGreat;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   @Override
   public boolean canLearnSkill(ManasSkillInstance instance, LivingEntity entity) {
      if (!super.canLearnSkill(instance, entity)) {
         return false;
      }

      if (!SkillUtils.isSkillMastered(entity, (ManasSkill)AspectualMagics.ICICLE_LANCE.get())) {
         instance.setCoolDowns(TensuraSkill.BASE_CONFIG.Learning.learningFailCooldown);
         if (entity instanceof Player player) {
            player.playNotifySound((SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
            player.displayClientMessage(
               Component.translatable(
                     "tensura.skill.learn_points.failed_mastery",
                     new Object[]{instance.getChatDisplayName(false), ((IcicleLanceMagic)AspectualMagics.ICICLE_LANCE.get()).getChatDisplayName(false)}
                  )
                  .withStyle(ChatFormatting.RED),
               true
            );
         }

         return false;
      } else {
         return true;
      }
   }

   @Override
   protected void applyCastingVisual(ManasSkillInstance instance, Player entity, int heldTicks, int mode, int castTime) {
      super.applyCastingVisual(instance, entity, heldTicks, mode, castTime);
      MagicCircle.castMagicCircle(
         CONFIG.impactRadius / 2.0F,
         25,
         MagicCircleVariant.ICE,
         true,
         entity,
         instance.getOrCreateTag(),
         1.0F,
         20.0F,
         new Vec3(0.0, 1.0F + CONFIG.impactRadius, 0.0),
         instance,
         mode,
         Pair.of(0.0, this.getMagiculeCost(entity, instance, mode))
      );
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (!this.isCastingBlocked(instance, entity)) {
         Level level = entity.level();
         CompoundTag tag = instance.getOrCreateTag();
         int id = tag.getInt("LanceID");
         if (id != 0 && level.getEntity(id) instanceof IceLanceProjectile lance) {
            tag.putInt("LanceID", 0);
            instance.markDirty();
            lance.discard();
            entity.level()
               .playSound(
                  null,
                  entity.getX(),
                  entity.getY(),
                  entity.getZ(),
                  (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                  TensuraSkill.ABILITY_SOUND,
                  1.0F,
                  1.0F
               );
         } else {
            IceLanceProjectile lance = new IceLanceProjectile(entity.level(), entity);
            lance.noPhysics = true;
            lance.setSkill(entity, instance, this, mode);
            lance.setSize(1.0F);
            lance.setSpeed(1.75F);
            lance.setNoGravity(true);
            lance.setLookDistance(20.0F);
            lance.setDelayTick(10);
            lance.setOwnerOffset(new Vec3(0.0, 0.0, -1.0));
            this.applyCastingVisual(instance, entity, 0, mode);
            if (lance.getMagicCircle() == null && tag.contains("MagicCircleID")) {
               lance.setMagicCircle(entity.level().getEntity(tag.getInt("MagicCircleID")));
            }

            lance.updateDelayPosition();
            entity.level().addFreshEntity(lance);
            tag.putInt("LanceID", lance.getId());
            instance.markDirty();
         }
      }
   }

   @Override
   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (instance.onCoolDown(mode) && !instance.canIgnoreCoolDown(entity, mode)) {
         return false;
      } else {
         CompoundTag tag = instance.getOrCreateTag();
         if (heldTicks == 0 && this.isCastingBlocked(instance, entity)) {
            return false;
         } else {
            Level level = entity.level();
            int id = tag.getInt("LanceID");
            if (!(level.getEntity(id) instanceof IceLanceProjectile lance)) {
               tag.putInt("LanceID", 0);
               instance.markDirty();
               return false;
            } else {
               int cast = this.getCastingTime(instance, entity);
               if (cast <= 1 || heldTicks >= cast) {
                  lance.setSize(CONFIG.impactRadius);
               } else if (heldTicks > 0 && (int)(heldTicks % (cast / 20.0F)) == 0) {
                  lance.setSize(lance.getSize() + (CONFIG.impactRadius - 1.0F) / 20.0F);
               }

               lance.setAge(0);
               lance.setDelayTick(10);
               instance.markDirty();
               this.applyCastingVisual(instance, entity, heldTicks, mode);
               return true;
            }
         }
      }
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      Level level = entity.level();
      CompoundTag tag = instance.getOrCreateTag();
      int id = tag.getInt("LanceID");
      if (level.getEntity(id) instanceof IceLanceProjectile lance) {
         if (heldTicks >= this.getCastingTime(instance, entity) && !EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            lance.noPhysics = false;
            lance.setAge(0);
            lance.setDelayTick(5);
            lance.setHitRadius(CONFIG.impactRadius);
            lance.setIceBreaker(true);
            lance.setSecondaryDamage(CONFIG.magicDamage);
            lance.setFrostBonusDamage(CONFIG.frostDamage);
            lance.setChillBonusDamage(instance.isMastered(entity) ? CONFIG.chillDamageMastered : CONFIG.chillDamage);
            tag.putInt("LanceID", 0);
            entity.swing(InteractionHand.MAIN_HAND, true);
            instance.addMasteryPoint(entity);
            instance.setCoolDown(instance.isMastered(entity) ? CONFIG.cooldownMastered : CONFIG.cooldown, mode);
         } else {
            lance.discard();
            entity.level()
               .playSound(
                  null,
                  entity.getX(),
                  entity.getY(),
                  entity.getZ(),
                  (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                  TensuraSkill.ABILITY_SOUND,
                  1.0F,
                  1.0F
               );
            tag.putInt("LanceID", 0);
            instance.markDirty();
         }
      } else {
         tag.putInt("LanceID", 0);
         instance.markDirty();
      }
   }
}
