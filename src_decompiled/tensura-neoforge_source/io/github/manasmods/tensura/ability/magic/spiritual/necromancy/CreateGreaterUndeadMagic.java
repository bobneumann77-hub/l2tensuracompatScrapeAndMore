package io.github.manasmods.tensura.ability.magic.spiritual.necromancy;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.config.ability.magic.SpiritualMagicConfig;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.enchantment.TensuraEnchantmentHelper;
import io.github.manasmods.tensura.entity.human.undead.UndeadHumanoidEntity;
import io.github.manasmods.tensura.registry.entity.HumanEntityTypes;
import io.github.manasmods.tensura.registry.magic.SpiritualMagics;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.Nullable;

public class CreateGreaterUndeadMagic extends CreateUndeadMagic<UndeadHumanoidEntity> {
   public static final SpiritualMagicConfig.CreateGreaterUndead CONFIG = ((SpiritualMagicConfig)ConfigRegistry.getConfig(SpiritualMagicConfig.class)).CreateGreaterUndead;

   public CreateGreaterUndeadMagic() {
      super(SpiritualMagic.SpiritLevel.MEDIUM);
   }

   @Override
   public int getDefaultCastTime() {
      return CONFIG.castTime;
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

      if (!SkillUtils.isSkillMastered(entity, (ManasSkill)SpiritualMagics.CREATE_LESSER_UNDEAD.get())) {
         instance.setCoolDowns(TensuraSkill.BASE_CONFIG.Learning.learningFailCooldown);
         if (entity instanceof Player player) {
            player.playNotifySound((SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
            player.displayClientMessage(
               Component.translatable(
                     "tensura.skill.learn_points.failed_mastery",
                     new Object[]{
                        instance.getChatDisplayName(false), ((CreateLesserUndeadMagic)SpiritualMagics.CREATE_LESSER_UNDEAD.get()).getChatDisplayName(false)
                     }
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

   public int getModes(ManasSkillInstance instance) {
      return 3;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      if (reverse) {
         return mode == 0 ? 2 : mode - 1;
      } else {
         return mode == 2 ? 0 : mode + 1;
      }
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> "create_undead.zombie";
         case 1 -> "create_undead.skeleton";
         case 2 -> "create_undead.random";
         default -> super.getModeId(instance, mode);
      };
   }

   @Override
   public int getSuccessCooldown(ManasSkillInstance instance, LivingEntity entity) {
      return instance.isMastered(entity) ? CONFIG.cooldownMastered : CONFIG.cooldown;
   }

   @Override
   protected int getSpawnNumber(ManasSkillInstance instance, LivingEntity entity, int mode) {
      if (!entity.isShiftKeyDown() && !instance.getOrCreateTag().contains("SummonUUID_1")) {
         return CONFIG.undeadNumber;
      } else {
         return instance.isMastered(entity) ? CONFIG.undeadNumberSneakMastered : CONFIG.undeadNumberSneak;
      }
   }

   @Nullable
   @Override
   public EntityType<? extends UndeadHumanoidEntity> getSummonedType(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return switch (mode) {
         case 0 -> (EntityType)HumanEntityTypes.ZOMBIE.get();
         case 1 -> (EntityType)HumanEntityTypes.SKELETON.get();
         default -> entity.getRandom().nextBoolean() ? (EntityType)HumanEntityTypes.ZOMBIE.get() : (EntityType)HumanEntityTypes.SKELETON.get();
      };
   }

   @Override
   public void removeExistingSummon(ManasSkillInstance instance, LivingEntity entity, int mode) {
      TamableAnimal summon = ObjectSelectionHelper.getTargetingEntity(TamableAnimal.class, entity, 30.0, 0.2, false);
      if (summon != null) {
         if (summon.isOwnedBy(entity)) {
            IExistence existence = TensuraStorages.getExistenceFrom(summon);
            if (existence.getSummonedSecond() > 0) {
               if (Objects.equals(entity.getUUID(), existence.getSummoner())) {
                  DamageSource source = TensuraDamageTypes.getDamageSource(entity.level(), TensuraDamageTypes.ENERGY_SOURCE_LOST);
                  summon.hurt(source, summon.getMaxHealth());
                  instance.setCoolDowns(0);
               }
            }
         }
      }
   }

   public void addAdditionalSummonData(ManasSkillInstance instance, LivingEntity entity, UndeadHumanoidEntity summon, int mode) {
      if (entity instanceof Player player) {
         summon.tame(player);
      }

      summon.skipDropExperience();
      if (summon.getType().equals(HumanEntityTypes.ZOMBIE.get())) {
         ItemStack sword = Items.IRON_SWORD.getDefaultInstance();
         sword.enchant(TensuraEnchantmentHelper.getEnchantment(entity.level(), Enchantments.SHARPNESS), CONFIG.undeadSharpness);
         summon.addFakeItem(EquipmentSlot.MAINHAND, sword);
      } else if (summon.getType().equals(HumanEntityTypes.SKELETON.get())) {
         ItemStack sword = Items.BOW.getDefaultInstance();
         sword.enchant(TensuraEnchantmentHelper.getEnchantment(entity.level(), Enchantments.POWER), CONFIG.undeadPower);
         summon.addFakeItem(EquipmentSlot.MAINHAND, sword);
      }

      if (instance.isMastered(entity)) {
         summon.addFakeItem(EquipmentSlot.HEAD, Items.CHAINMAIL_HELMET.getDefaultInstance());
         summon.addFakeItem(EquipmentSlot.CHEST, Items.CHAINMAIL_CHESTPLATE.getDefaultInstance());
         summon.addFakeItem(EquipmentSlot.LEGS, Items.CHAINMAIL_LEGGINGS.getDefaultInstance());
         summon.addFakeItem(EquipmentSlot.FEET, Items.CHAINMAIL_BOOTS.getDefaultInstance());
      }

      IExistence existence = TensuraStorages.getExistenceFrom(summon);
      existence.setSummoner(entity.getUUID());
      existence.setSummonedSecond(CONFIG.undeadDuration);
      existence.setSummonedAbility(this, mode);
      existence.markDirty();
   }

   public void onPostSummon(ManasSkillInstance instance, LivingEntity entity, UndeadHumanoidEntity summon, int mode) {
      EnergyHelper.gainMagicule(summon, CONFIG.undeadEpBoost, EnergyHelper.GainType.MAX);
      AttributeInstance hp = summon.getAttribute(Attributes.MAX_HEALTH);
      if (hp != null) {
         hp.setBaseValue(CONFIG.undeadHP);
         summon.setHealth(CONFIG.undeadHP);
      }

      AttributeInstance attack = summon.getAttribute(Attributes.ATTACK_DAMAGE);
      if (attack != null) {
         attack.setBaseValue(CONFIG.undeadAttack);
      }
   }

   @Override
   public ParticleOptions getSummoningParticle(ManasSkillInstance instance, int mode) {
      return (ParticleOptions)TensuraParticleTypes.SOUL.get();
   }

   @Override
   public SoundEvent getSummoningSound(ManasSkillInstance instance, int mode) {
      return SoundEvents.SOUL_SAND_BREAK;
   }

   @Override
   public SoundEvent getFailSound(ManasSkillInstance instance, int mode) {
      return (SoundEvent)TensuraSoundEvents.DEBUFF_DEACTIVATE.get();
   }
}
