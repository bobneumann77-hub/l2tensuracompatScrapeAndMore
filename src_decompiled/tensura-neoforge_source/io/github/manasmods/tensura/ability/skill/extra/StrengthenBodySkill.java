package io.github.manasmods.tensura.ability.skill.extra;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.ExtraSkillConfig;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;

public class StrengthenBodySkill extends Skill {
   private static final ExtraSkillConfig.StrengthenBody CONFIG = ((ExtraSkillConfig)ConfigRegistry.getConfig(ExtraSkillConfig.class)).StrengthenBody;
   protected static final ResourceLocation STRENGTHEN = ResourceLocation.fromNamespaceAndPath("tensura", "strengthen_body");

   public StrengthenBodySkill() {
      super(Skill.SkillType.EXTRA);
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
      return instance.getMastery() >= 0.0;
   }

   @Override
   public boolean canBeSlotted(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return instance.getMastery() < 0.0;
   }

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      return newEP > CONFIG.epAcquirement;
   }

   public boolean onTakenDamage(ManasSkillInstance instance, LivingEntity owner, DamageSource source, Changeable<Float> amount) {
      if (!instance.isToggled()) {
         return true;
      }

      if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
         return true;
      }

      if (source.tensura$getBarrierBypassLevel() >= 2.0F) {
         return true;
      }

      amount.set((Float)amount.get() * CONFIG.inputMultiplier);
      return true;
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      AttributeInstance armor = entity.getAttribute(Attributes.ARMOR);
      if (armor != null) {
         if (!armor.hasModifier(STRENGTHEN)) {
            armor.addOrReplacePermanentModifier(new AttributeModifier(STRENGTHEN, CONFIG.bonusArmor, Operation.ADD_VALUE));
         }

         TensuraParticleHelper.spawnServerParticles(
            entity.level(),
            TensuraParticleUtils.getColorlessReversedWave(0.9F, entity.getBbWidth() * 3.0F),
            entity.getX(),
            entity.getY() + entity.getBbHeight() * 0.33,
            entity.getZ()
         );
         TensuraParticleHelper.spawnServerParticles(
            entity.level(),
            TensuraParticleUtils.getColorlessReversedWave(0.9F, entity.getBbWidth() * 3.0F),
            entity.getX(),
            entity.getY() + entity.getBbHeight() * 0.66,
            entity.getZ()
         );
      }
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      AttributeInstance armor = entity.getAttribute(Attributes.ARMOR);
      if (armor != null) {
         armor.removeModifier(STRENGTHEN);
      }
   }

   public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
      return instance.isToggled();
   }

   public void onTick(ManasSkillInstance instance, LivingEntity entity) {
      CompoundTag tag = instance.getOrCreateTag();
      int time = tag.getInt("activatedTimes");
      if (time % BASE_CONFIG.Mastery.masteryActivateTime == 0) {
         instance.addMasteryPoint(entity);
      }

      tag.putInt("activatedTimes", time + 1);
   }
}
