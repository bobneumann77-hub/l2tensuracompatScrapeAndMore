package io.github.manasmods.tensura.race.slime;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.ManasRace.Difficulty;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.config.race.RaceConfig;
import io.github.manasmods.tensura.config.race.SlimeConfig;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.race.template.DefaultRace;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.race.TensuraRaces;
import io.github.manasmods.tensura.registry.skill.CommonSkills;
import io.github.manasmods.tensura.registry.skill.IntrinsicSkills;
import io.github.manasmods.tensura.registry.skill.ResistanceSkills;
import io.github.manasmods.tensura.storage.Alignment;
import io.github.manasmods.tensura.world.TensuraGameRules;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class SlimeRace extends DefaultRace {
   public SlimeRace(Difficulty difficulty) {
      super(difficulty);
   }

   public SlimeRace() {
      this(Difficulty.EXTREME);
      this.applyDefaultAttributeModifiers();
      SlimeConfig.Slime config = ((SlimeConfig)ConfigRegistry.getConfig(SlimeConfig.class)).Slime;
      this.addAttributeModifier(Attributes.JUMP_STRENGTH, DEFAULT_RACE_ID, config.jumpStrength, Operation.ADD_VALUE);
      this.addAttributeModifier(Attributes.SAFE_FALL_DISTANCE, DEFAULT_RACE_ID, 2.0 * config.jumpStrength, Operation.ADD_MULTIPLIED_BASE);
      this.addAttributeModifier(Attributes.FALL_DAMAGE_MULTIPLIER, DEFAULT_RACE_ID, config.fallDamage, Operation.ADD_VALUE);
      this.addAttributeModifier(TensuraAttributes.WIDTH_MULTIPLIER, DEFAULT_RACE_ID, this.getCustomWidth() - 1.0, Operation.ADD_VALUE);
   }

   @Override
   public RaceConfig.Default getDefaultConfig() {
      return ((SlimeConfig)ConfigRegistry.getConfig(SlimeConfig.class)).Slime;
   }

   @Override
   public Alignment getAlignment() {
      return Alignment.MAJIN;
   }

   protected int getMaxJumpCharge() {
      return ((SlimeConfig)ConfigRegistry.getConfig(SlimeConfig.class)).Slime.maxChargeTick;
   }

   public double getCustomWidth() {
      return ((SlimeConfig)ConfigRegistry.getConfig(SlimeConfig.class)).Slime.width;
   }

   @Nullable
   public ManasRace getDefaultEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.DEMON_SLIME.get();
   }

   @Nullable
   @Override
   public ManasRace getAwakeningEvolution(ManasRaceInstance instance, LivingEntity entity) {
      return (ManasRace)TensuraRaces.DEMON_SLIME.get();
   }

   public List<ManasRace> getNextEvolutions(ManasRaceInstance instance, LivingEntity entity) {
      List<ManasRace> list = new ArrayList<>();
      list.add((ManasRace)TensuraRaces.DEMON_SLIME.get());
      list.add((ManasRace)TensuraRaces.METAL_SLIME.get());
      return list;
   }

   public List<ManasSkill> getIntrinsicSkills(ManasRaceInstance instance, LivingEntity entity) {
      List<ManasSkill> list = new ArrayList<>();
      list.add((ManasSkill)IntrinsicSkills.ABSORB_DISSOLVE.get());
      list.add((ManasSkill)CommonSkills.SELF_REGENERATION.get());
      return list;
   }

   public boolean canTick(ManasRaceInstance instance, LivingEntity entity) {
      if (!entity.level().getGameRules().getBoolean(TensuraGameRules.HARDCORE_RACE)) {
         return false;
      } else {
         return !entity.hasInfiniteMaterials() && !entity.isSpectator() ? entity.getAttributeValue(TensuraAttributes.PRESENCE_SENSE) <= 0.0 : false;
      }
   }

   public void onTick(ManasRaceInstance instance, LivingEntity entity) {
      entity.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.TRUE_BLINDNESS), 40, 0, false, false, false));
      entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 0, false, false, false));
   }

   public boolean onHurt(ManasRaceInstance instance, LivingEntity owner, DamageSource source, Changeable<Float> amount) {
      amount.set((Float)amount.get() * getSlimePhysicalInputMultiplier(source, owner));
      return true;
   }

   public void onActivateAbility(ManasRaceInstance instance, LivingEntity entity) {
      instance.getOrCreateTag().putInt("chargeTick", 0);
      instance.markDirty();
   }

   public boolean onHeldAbility(ManasRaceInstance instance, LivingEntity entity, int heldTicks) {
      if (entity.onGround() && !entity.isInLiquid()) {
         CompoundTag tag = instance.getOrCreateTag();
         int chargeTick = tag.getInt("chargeTick");
         if (chargeTick < this.getMaxJumpCharge()) {
            tag.putInt("chargeTick", Math.min(chargeTick + 1, this.getMaxJumpCharge()));
            instance.markDirty();
         }

         if (entity instanceof Player player) {
            player.displayClientMessage(
               Component.translatable(
                     "tensura.skill.power_scale", new Object[]{Math.round(tag.getInt("chargeTick") * 10 / 20.0) / 10.0 + "/" + this.getMaxJumpCharge() / 20.0}
                  )
                  .setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GREEN)),
               true
            );
         }

         return true;
      } else {
         return false;
      }
   }

   public void onReleaseAbility(ManasRaceInstance instance, LivingEntity entity, int heldTicks) {
      if (entity.onGround() && !entity.isInLiquid()) {
         int chargeTick = instance.getOrCreateTag().getInt("chargeTick");
         if (chargeTick > 0) {
            float jumpCharge = 1.0F / this.getMaxJumpCharge() * Math.min(chargeTick, this.getMaxJumpCharge());
            Vec3 look = entity.getLookAngle().normalize();
            float modifier = 3.3000002F;
            look = look.multiply(3.5000002F * jumpCharge, 3.3000002F * jumpCharge, 3.5000002F * jumpCharge);
            look = look.add(0.0, entity.getDeltaMovement().y, 0.0);
            entity.setDeltaMovement(look);
            entity.hurtMarked = true;
         }
      }
   }

   public static float getSlimePhysicalInputMultiplier(DamageSource source, LivingEntity entity) {
      if (!TensuraDamageHelper.isPhysicalAttack(source)) {
         return 1.0F;
      } else if (SkillUtils.isSkillToggled(entity, (ManasSkill)ResistanceSkills.PHYSICAL_ATTACK_RESISTANCE.get())) {
         return 1.0F;
      } else {
         return SkillUtils.isSkillToggled(entity, (ManasSkill)ResistanceSkills.PHYSICAL_ATTACK_NULLIFICATION.get())
            ? 1.0F
            : ((SlimeConfig)ConfigRegistry.getConfig(SlimeConfig.class)).physicalInput;
      }
   }
}
