package io.github.manasmods.tensura.item.weapon.custom;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.enchantment.TensuraEnchantmentHelper;
import io.github.manasmods.tensura.enchantment.TensuraEnchantments;
import io.github.manasmods.tensura.entity.projectile.magic.ChaosEaterProjectile;
import io.github.manasmods.tensura.item.TensuraToolTiers;
import io.github.manasmods.tensura.item.weapon.TwoHandedSwordItem;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.item.misc.TensuraCreativeTabs;
import io.github.manasmods.tensura.registry.skill.UniqueSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.effect.EffectStorage;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import io.github.manasmods.tensura.world.TensuraGameRules;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class MeatCrusherItem extends TwoHandedSwordItem {
   public MeatCrusherItem() {
      super(
         TensuraToolTiers.HIGH_MAGISTEEL,
         9,
         -3.0F,
         0.5,
         0.25,
         0.0,
         0.0,
         7,
         -3.2F,
         0.5,
         0.1,
         0.0,
         0.0,
         new Properties().arch$tab(TensuraCreativeTabs.GEARS).fireResistant()
      );
   }

   public UseAnim getUseAnimation(ItemStack pStack) {
      return UseAnim.SPEAR;
   }

   public int getUseDuration(ItemStack pStack, LivingEntity entity) {
      return 10000;
   }

   @NotNull
   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand pHand) {
      ItemStack stack = player.getItemInHand(pHand);
      if (player.getCooldowns().isOnCooldown(stack.getItem())) {
         return InteractionResultHolder.fail(stack);
      }

      if (TensuraEnchantmentHelper.getEnchantmentLevel(level, TensuraEnchantments.SOUL_EATER, player) > 0
         && SkillUtils.isSkillToggled(player, (ManasSkill)UniqueSkills.STARVED.get())) {
         this.summonChaosEater(player, 4, 1.0F);
         player.swing(player.getUsedItemHand(), true);
         if (this.isStarvedMastered(player, stack)) {
            player.getCooldowns().addCooldown(stack.getItem(), 100);
         } else {
            player.getCooldowns().addCooldown(stack.getItem(), 40);
         }

         level.playSound(
            null, player.getX(), player.getY(), player.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
         );
         return InteractionResultHolder.consume(stack);
      } else {
         player.startUsingItem(pHand);
         return InteractionResultHolder.consume(stack);
      }
   }

   public void releaseUsing(@NotNull ItemStack pStack, @NotNull Level level, @NotNull LivingEntity entity, int pTimeLeft) {
      int useTicks = this.getUseDuration(pStack, entity) - pTimeLeft;
      if (useTicks >= 7) {
         if (entity instanceof Player player) {
            player.getCooldowns().addCooldown(pStack.getItem(), 40);
            pStack.hurtAndBreak(10, player, LivingEntity.getSlotForHand(player.getUsedItemHand()));
         }

         entity.swing(entity.getUsedItemHand(), true);
         Vec3 target = entity.position().add(entity.getLookAngle().scale(2.0));
         Vec3 source = entity.position().add(0.0, entity.getEyeHeight(), 0.0);
         Vec3 sourceToTarget = target.subtract(source);
         Vec3 normalizes = sourceToTarget.normalize();
         EffectStorage.setCameraShake(entity, 7.0, 0.01F, 10);
         level.playSound(
            null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.EARTHSHATTER_KICK.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
         );
         int steps = Mth.floor(sourceToTarget.length());
         AABB pathAabb = new AABB(source, target).inflate(3.0);
         List<LivingEntity> hits = level.getEntitiesOfClass(LivingEntity.class, pathAabb, entityData -> !entityData.is(entity));
         Set<LivingEntity> alreadyHurt = new HashSet<>(hits.size());

         for (int particleIndex = 1; particleIndex < steps; particleIndex++) {
            Vec3 particlePos = source.add(normalizes.scale(particleIndex));
            TensuraParticleHelper.spawnParticlesLikeServer(
               level, ParticleTypes.EXPLOSION, particlePos.x, particlePos.y, particlePos.z, 1, 0.0, 0.0, 0.0, 0.0, false
            );
            if (TensuraGameRules.canSkillGrief(level)) {
               SkillHelper.launchBlock(
                  entity,
                  particlePos,
                  2,
                  1,
                  0.3F,
                  0.2F,
                  blockState -> entity.getRandom().nextInt(2) != 1 ? false : blockState.is(TensuraBlockTags.EARTH_SKILL_BREAKABLE),
                  pos -> !pos.equals(entity.getOnPos()) && !pos.equals(entity.getOnPos().below())
               );
            }

            double stepRangeSqr = 9.0;

            for (LivingEntity living : hits) {
               if (!alreadyHurt.contains(living)
                  && !(living.distanceToSqr(particlePos) > stepRangeSqr)
                  && living.hurt(entity.damageSources().mobAttack(entity), (float)entity.getAttributeValue(Attributes.ATTACK_DAMAGE))) {
                  TensuraMobEffect.addEffect(
                     living, TensuraMobEffects.getReference(TensuraMobEffects.CORROSION), 60, 1, true, false, true, entity.getUUID(), null, 0
                  );
                  TensuraParticleHelper.spawnServerGroundSlamParticle(living, 10, 2.0F);
                  living.getDeltaMovement().add(0.0, 0.3, 0.0);
                  alreadyHurt.add(living);
               }
            }
         }
      }
   }

   private boolean isStarvedMastered(Player player, ItemStack stack) {
      if (player.hasInfiniteMaterials()) {
         return true;
      }

      stack.hurtAndBreak(10, player, LivingEntity.getSlotForHand(player.getUsedItemHand()));
      return SkillUtils.isSkillMastered(player, (ManasSkill)UniqueSkills.STARVED.get());
   }

   private void summonChaosEater(LivingEntity entity, int amount, float distance) {
      int rot = 360 / amount;

      for (int i = 0; i < amount; i++) {
         Vec3 offset = new Vec3(0.0, distance, 0.0)
            .zRot((rot * i - rot / 2.0F) * (float) (Math.PI / 180.0))
            .xRot(-entity.getXRot() * (float) (Math.PI / 180.0))
            .yRot(-entity.getYRot() * (float) (Math.PI / 180.0));
         Vec3 offPos = entity.getEyePosition().add(offset);
         ChaosEaterProjectile chaosEater = new ChaosEaterProjectile(entity.level(), entity);
         chaosEater.setPos(offPos);
         chaosEater.setUpStartPos(amount, i, distance);
         LivingEntity target = ObjectSelectionHelper.getTargetingEntity(entity, 20.0, false);
         if (entity.isShiftKeyDown()) {
            List<LivingEntity> list = this.getTargetList(entity);
            if (!list.isEmpty()) {
               target = list.get(entity.getRandom().nextInt(list.size()));
            }
         }

         chaosEater.setTarget(target);
         chaosEater.shootFromRot(entity.getLookAngle());
         chaosEater.setLife(300);
         chaosEater.setDamage((float)entity.getAttributeValue(Attributes.ATTACK_DAMAGE));
         chaosEater.setMobEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.CORROSION), 200, 1, true, false, true));
         chaosEater.setEffectRange(1.5F);
         chaosEater.setMpCost(100.0);
         chaosEater.setSkill(SkillUtils.getSkillOrNull(entity, (ManasSkill)UniqueSkills.STARVED.get()));
         entity.level().addFreshEntity(chaosEater);
      }
   }

   private List<LivingEntity> getTargetList(LivingEntity owner) {
      AABB box = owner.getBoundingBox().inflate(20.0);
      return owner.level().getEntitiesOfClass(LivingEntity.class, box, entity -> this.shouldAttack(entity, owner));
   }

   protected boolean shouldAttack(LivingEntity entity, LivingEntity owner) {
      return !entity.isAlliedTo(owner) && !owner.isAlliedTo(entity) ? !entity.hasInfiniteMaterials() : false;
   }
}
