package io.github.manasmods.tensura.ability.skill.extra;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.ExtraSkillConfig;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.entity.projectile.WebBulletProjectile;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.item.TensuraToolItems;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class StickySteelThreadSkill extends Skill {
   private static final ExtraSkillConfig.StickySteelThread CONFIG = ((ExtraSkillConfig)ConfigRegistry.getConfig(ExtraSkillConfig.class)).StickySteelThread;

   public StickySteelThreadSkill() {
      super(Skill.SkillType.EXTRA);
   }

   public int getModes(ManasSkillInstance instance) {
      return 4;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      if (reverse) {
         return mode == 0 ? 3 : mode - 1;
      } else {
         return mode == 3 ? 0 : mode + 1;
      }
   }

   @Override
   public List<Integer> getModeLearningList(ManasSkillInstance instance) {
      return List.of(3);
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> "sticky_steel_thread.sticky";
         case 1 -> "sticky_steel_thread.steel";
         case 2 -> "sticky_steel_thread.slinger";
         case 3 -> "sticky_steel_thread.arcane_thread";
         default -> super.getModeId(instance, mode);
      };
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 1 -> CONFIG.magiculeCostSteel;
         case 2 -> CONFIG.magiculeCostSlinger;
         case 3 -> CONFIG.magiculeCostArcane;
         default -> CONFIG.magiculeCostSticky;
      };
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
         Level level = entity.level();
         CompoundTag tag = instance.getOrCreateTag();
         switch (mode) {
            case 0:
               instance.addMasteryPoint(entity);
               instance.setCoolDown(instance.isMastered(entity) ? CONFIG.threadCooldownMastered : CONFIG.threadCooldown, mode);
               this.shootWebBullet(entity, level, ((Item)TensuraToolItems.STICKY_WEB_CARTRIDGE.get()).getDefaultInstance());
               this.shootWebBullet(entity, level, Items.ARROW.getDefaultInstance());
               break;
            case 1:
               instance.addMasteryPoint(entity);
               instance.setCoolDown(instance.isMastered(entity) ? CONFIG.threadCooldownMastered : CONFIG.threadCooldown, mode);
               this.shootWebBullet(entity, level, ((Item)TensuraToolItems.STICKY_STEEL_WEB_CARTRIDGE.get()).getDefaultInstance());
               this.shootWebBullet(entity, level, Items.SPECTRAL_ARROW.getDefaultInstance());
               break;
            case 2:
               Entity oldBullet = entity.level().getEntity(tag.getInt("BulletID"));
               if (oldBullet instanceof WebBulletProjectile) {
                  oldBullet.discard();
               }

               if (entity.isShiftKeyDown()) {
                  return;
               }

               ItemStack stack = ((Item)TensuraToolItems.WEB_CARTRIDGE.get()).getDefaultInstance();
               WebBulletProjectile bullet = new WebBulletProjectile(level, entity, true, stack);
               bullet.setSlinger(true);
               Vec3 vector = entity.getViewVector(1.0F);
               bullet.shoot(vector.x(), vector.y(), vector.z(), 2.0F, 0.0F);
               entity.swing(entity.getUsedItemHand());
               level.addFreshEntity(bullet);
               tag.putInt("BulletID", bullet.getId());
               entity.swing(InteractionHand.MAIN_HAND, true);
               level.playSound(
                  null,
                  entity.getX(),
                  entity.getY(),
                  entity.getZ(),
                  (SoundEvent)TensuraSoundEvents.STICKY_STEEL_THREAD.get(),
                  TensuraSkill.ABILITY_SOUND,
                  1.0F,
                  1.0F
               );
               break;
            case 3:
               if (this.learnMode(instance, entity, mode)) {
                  return;
               }

               double distance = instance.isMastered(entity) ? CONFIG.arcaneRangeMastered : CONFIG.arcaneRange;
               LivingEntity target = ObjectSelectionHelper.getTargetingEntity(entity, distance, false, true);
               if (target == null) {
                  return;
               }

               entity.swing(InteractionHand.MAIN_HAND, true);
               TensuraParticleHelper.addServerParticlesAroundSelf(target, new BlockParticleOption(ParticleTypes.BLOCK, Blocks.COBWEB.defaultBlockState()), 1.0);
               TensuraParticleHelper.addServerParticlesAroundSelf(target, new BlockParticleOption(ParticleTypes.BLOCK, Blocks.COBWEB.defaultBlockState()), 2.0);
               int time = CONFIG.arcaneDuration - (instance.isMastered(entity) ? CONFIG.arcaneReactivateMastered : CONFIG.arcaneReactivate);
               if (this.getArcaneThreadSource(target, time) == entity.getUUID()) {
                  instance.addMasteryPoint(entity);
                  instance.setCoolDown(instance.isMastered(entity) ? CONFIG.arcaneCooldownMastered : CONFIG.arcaneCooldown, mode);
                  target.hurt(
                     this.createSource(instance, entity, TensuraDamageTypes.STEEL_THREAD, mode),
                     instance.isMastered(entity) ? CONFIG.arcaneReactivateDamageMastered : CONFIG.arcaneReactivateDamage
                  );
                  target.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.WEBBED));
                  target.removeEffect(TensuraMobEffects.getReference(TensuraMobEffects.SILENCE));
                  TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.SWEEP_ATTACK, 2.0);
                  level.playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.STICKY_STEEL_THREAD.get(),
                     TensuraSkill.ABILITY_SOUND,
                     2.0F,
                     1.5F
                  );
               } else if (!target.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.WEBBED))) {
                  level.playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.STICKY_STEEL_THREAD.get(),
                     TensuraSkill.ABILITY_SOUND,
                     2.0F,
                     1.0F
                  );
                  if (EnergyHelper.getMaxEP(target) <= EnergyHelper.getMaxEP(entity)) {
                     MobEffectInstance webbed = new MobEffectInstance(
                        TensuraMobEffects.getReference(TensuraMobEffects.WEBBED), CONFIG.arcaneDuration, 0, true, false, true
                     );
                     TensuraMobEffect.addEffect(target, webbed, entity, this, mode);
                  }

                  MobEffectInstance silence = new MobEffectInstance(
                     TensuraMobEffects.getReference(TensuraMobEffects.SILENCE), CONFIG.arcaneDuration, 0, true, false, true
                  );
                  TensuraMobEffect.addEffect(target, silence, entity, this, mode);
               }
         }
      }
   }

   @Nullable
   private UUID getArcaneThreadSource(LivingEntity target, int duration) {
      MobEffectInstance webbed = target.getEffect(TensuraMobEffects.getReference(TensuraMobEffects.WEBBED));
      if (webbed == null) {
         return null;
      } else {
         return webbed.getDuration() < duration ? null : webbed.tensura$getSource();
      }
   }

   private void shootWebBullet(LivingEntity entity, Level level, ItemStack ammo) {
      WebBulletProjectile bullet = new WebBulletProjectile(level, entity, true, ammo);
      Vec3 vector = entity.getViewVector(1.0F);
      bullet.shoot(vector.x(), vector.y(), vector.z(), 1.0F, 0.0F);
      entity.swing(entity.getUsedItemHand());
      level.addFreshEntity(bullet);
      entity.swing(InteractionHand.MAIN_HAND, true);
      level.playSound(
         null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.STICKY_STEEL_THREAD.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
      );
   }
}
