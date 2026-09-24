package io.github.manasmods.tensura.ability.skill.intrinsic;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.IntrinsicSkillConfig;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.data.TensuraRaceTags;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.item.misc.TensuraDataComponents;
import java.util.Optional;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class DivineKiReleaseSkill extends Skill {
   private static final IntrinsicSkillConfig.DivineKiRelease CONFIG = ((IntrinsicSkillConfig)ConfigRegistry.getConfig(IntrinsicSkillConfig.class)).DivineKiRelease;
   public static final ResourceLocation DIVINE_KI = ResourceLocation.fromNamespaceAndPath("tensura", "divine_ki_release");

   public DivineKiReleaseSkill() {
      super(Skill.SkillType.INTRINSIC);
   }

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      Optional<ManasRaceInstance> race = RaceAPI.getRaceFrom(entity).getRace();
      return race.isPresent() && race.get().is(TensuraRaceTags.DIVINE);
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
      Optional<ManasRaceInstance> race = RaceAPI.getRaceFrom(entity).getRace();
      return race.isEmpty() || race.get().is(TensuraRaceTags.DIVINE);
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      AttributeInstance degrade = entity.getAttribute(TensuraAttributes.PHYSICAL_RESIST_DEGRADATION);
      if (degrade != null) {
         degrade.addOrReplacePermanentModifier(new AttributeModifier(DIVINE_KI, 1.0, Operation.ADD_VALUE));
      }

      TensuraParticleHelper.spawnServerParticles(
         entity.level(),
         TensuraParticleUtils.getGoldWave(0.9F, entity.getBbWidth() * 3.0F, -0.5F, true),
         entity.getX(),
         entity.getY() + entity.getBbHeight() * 0.33,
         entity.getZ()
      );
      TensuraParticleHelper.spawnServerParticles(
         entity.level(),
         TensuraParticleUtils.getGoldWave(0.9F, entity.getBbWidth() * 3.0F, -0.5F, true),
         entity.getX(),
         entity.getY() + entity.getBbHeight() * 0.66,
         entity.getZ()
      );
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      AttributeInstance degrade = entity.getAttribute(TensuraAttributes.PHYSICAL_RESIST_DEGRADATION);
      if (degrade != null) {
         degrade.removeModifier(DIVINE_KI);
      }

      TensuraParticleHelper.spawnServerParticles(
         entity.level(),
         TensuraParticleUtils.getGoldWave(0.9F, entity.getBbWidth() * 3.0F, 0.3F, true),
         entity.getX(),
         entity.getY() + entity.getBbHeight() * 0.33,
         entity.getZ()
      );
      TensuraParticleHelper.spawnServerParticles(
         entity.level(),
         TensuraParticleUtils.getGoldWave(0.9F, entity.getBbWidth() * 3.0F, 0.3F, true),
         entity.getX(),
         entity.getY() + entity.getBbHeight() * 0.66,
         entity.getZ()
      );
   }

   public boolean onDamageEntity(ManasSkillInstance instance, LivingEntity entity, LivingEntity target, DamageSource source, Changeable<Float> amount) {
      if (!instance.isToggled()) {
         return true;
      }

      if (!TensuraDamageHelper.isBattlewill(source, entity)) {
         return true;
      }

      float damage = instance.isMastered(entity) ? CONFIG.battlewillDamageMastered : CONFIG.battlewillDamage;
      amount.set((Float)amount.get() + damage);
      instance.addMasteryPoint(entity);
      return true;
   }

   public boolean onTouchEntity(ManasSkillInstance instance, LivingEntity entity, LivingEntity target, DamageSource source, Changeable<Float> amount) {
      if (!instance.isToggled()) {
         return true;
      }

      if (!TensuraDamageHelper.isPhysicalOrBattlewill(source, entity)) {
         return true;
      }

      int durabilityBreak = (int)Math.max(1.0F, (Float)amount.get() / 4.0F);
      durabilityBreak *= (int)(instance.isMastered(entity) ? CONFIG.durabilityBreakMastered - 1.0F : CONFIG.durabilityBreak - 1.0F);

      for (EquipmentSlot slot : EquipmentSlot.values()) {
         if (!slot.getType().equals(Type.HAND) || target.isBlocking() && slot == LivingEntity.getSlotForHand(target.getUsedItemHand())) {
            ItemStack slotStack = target.getItemBySlot(slot);
            if (!slotStack.has((DataComponentType)TensuraDataComponents.EP.get())
               || !((Double)slotStack.get((DataComponentType)TensuraDataComponents.EP.get()) >= 1000000.0)) {
               slotStack.hurtAndBreak(durabilityBreak, target, slot);
            }
         }
      }

      return true;
   }
}
