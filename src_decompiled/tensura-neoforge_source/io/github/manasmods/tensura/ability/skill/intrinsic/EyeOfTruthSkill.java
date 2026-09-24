package io.github.manasmods.tensura.ability.skill.intrinsic;

import com.google.common.collect.ImmutableList;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.IntrinsicSkillConfig;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.AttributeHelper;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class EyeOfTruthSkill extends Skill {
   private static final IntrinsicSkillConfig.EyeOfTruth CONFIG = ((IntrinsicSkillConfig)ConfigRegistry.getConfig(IntrinsicSkillConfig.class)).EyeOfTruth;
   public static final ImmutableList<Holder<MobEffect>> VISION = ImmutableList.of(MobEffects.BLINDNESS, MobEffects.CONFUSION, MobEffects.DARKNESS);

   public EyeOfTruthSkill() {
      super(Skill.SkillType.INTRINSIC);
   }

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      IExistence existence = TensuraStorages.getExistenceFrom(entity);
      return existence.isHeroEgg() || existence.isTrueHero();
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
      if (instance.getMastery() < 0.0) {
         return false;
      }

      IExistence existence = TensuraStorages.getExistenceFrom(entity);
      return existence.isHeroEgg() || existence.isTrueHero();
   }

   @Override
   public boolean canBeSlotted(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return instance.getMastery() < 0.0;
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

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.addPresenceSense(entity, CONFIG.senseLevel);
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      AttributeHelper.removePresenceSense(entity, CONFIG.senseLevel);
   }

   public boolean onEffectAdded(ManasSkillInstance instance, LivingEntity entity, @Nullable Entity source, Changeable<MobEffectInstance> effect) {
      if (!instance.isToggled()) {
         return true;
      }

      List<Holder<MobEffect>> list = VISION;
      return !list.isEmpty() && !effect.isEmpty() ? !list.contains(((MobEffectInstance)effect.get()).getEffect()) : true;
   }
}
