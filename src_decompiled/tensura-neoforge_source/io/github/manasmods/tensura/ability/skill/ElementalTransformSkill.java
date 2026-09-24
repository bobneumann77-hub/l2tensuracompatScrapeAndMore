package io.github.manasmods.tensura.ability.skill;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.spiritual.SpiritualMagic;
import io.github.manasmods.tensura.ability.skill.intrinsic.DarknessTransformSkill;
import io.github.manasmods.tensura.ability.skill.intrinsic.EarthTransformSkill;
import io.github.manasmods.tensura.ability.skill.intrinsic.FlameTransformSkill;
import io.github.manasmods.tensura.ability.skill.intrinsic.LightTransformSkill;
import io.github.manasmods.tensura.ability.skill.intrinsic.SpaceTransformSkill;
import io.github.manasmods.tensura.ability.skill.intrinsic.WaterTransformSkill;
import io.github.manasmods.tensura.ability.skill.intrinsic.WindTransformSkill;
import io.github.manasmods.tensura.config.ability.skill.IntrinsicSkillConfig;
import io.github.manasmods.tensura.effect.template.ITransformation;
import io.github.manasmods.tensura.registry.skill.IntrinsicSkills;
import io.github.manasmods.tensura.util.EnergyHelper;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import org.jetbrains.annotations.Nullable;

public abstract class ElementalTransformSkill extends Skill implements ITransformation {
   protected static final IntrinsicSkillConfig.ElementalTransform CONFIG = ((IntrinsicSkillConfig)ConfigRegistry.getConfig(IntrinsicSkillConfig.class)).ElementalTransform;
   private final Element element;

   public ElementalTransformSkill(Skill.SkillType type, Element element) {
      super(type);
      this.element = element;
      this.addHeldAttributeModifier(
         Attributes.MOVEMENT_SPEED,
         ResourceLocation.fromNamespaceAndPath("tensura", element.getNamespace() + "_transform"),
         CONFIG.speedMultiplier - 1.0,
         Operation.ADD_MULTIPLIED_TOTAL
      );
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.magiculeCost;
   }

   @Override
   public void onLearnSkill(ManasSkillInstance instance, LivingEntity living) {
      super.onLearnSkill(instance, living);
      if (!(instance.getMastery() < 0.0) && !instance.isTemporarySkill()) {
         for (ManasSkill manasSkill : SkillAPI.getSkillRegistry()) {
            if (manasSkill instanceof SpiritualMagic magic && magic.getElemental() == this.element && !(magic.getLevel().getId() > CONFIG.spiritLevel)) {
               SkillHelper.learnSkill(living, magic);
            }
         }
      }
   }

   protected abstract void applyVisualEffect(LivingEntity var1);

   protected abstract void onDamageEntity(ManasSkillInstance var1, LivingEntity var2, DamageSource var3);

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (this.failedToActivate(entity, null)) {
         return false;
      }

      if (heldTicks % 20 == 0 && EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
         return false;
      }

      if (heldTicks % BASE_CONFIG.Mastery.masteryHoldTick == 0 && heldTicks > 0) {
         instance.addMasteryPoint(entity);
      }

      this.applyVisualEffect(entity);
      List<LivingEntity> list = entity.level()
         .getEntitiesOfClass(
            LivingEntity.class,
            entity.getBoundingBox().inflate(CONFIG.radius),
            targetx -> !targetx.is(entity) && targetx.isAlive() && !targetx.isAlliedTo(entity)
         );
      if (!list.isEmpty()) {
         for (LivingEntity target : list) {
            DamageSource damageSource = this.createSource(instance, entity, this.element.getDefaultDamage(), mode)
               .tensura$setElement(this.element)
               .tensura$setDodgeBypass();
            this.onDamageEntity(instance, target, damageSource);
         }
      }

      return true;
   }

   @Nullable
   public static ManasSkill getTransformSkill(Element element) {
      return switch (element) {
         case EARTH -> (EarthTransformSkill)IntrinsicSkills.EARTH_TRANSFORM.get();
         case DARKNESS -> (DarknessTransformSkill)IntrinsicSkills.DARKNESS_TRANSFORM.get();
         case FLAME -> (FlameTransformSkill)IntrinsicSkills.FLAME_TRANSFORM.get();
         case LIGHT -> (LightTransformSkill)IntrinsicSkills.LIGHT_TRANSFORM.get();
         case SPACE -> (SpaceTransformSkill)IntrinsicSkills.SPACE_TRANSFORM.get();
         case WATER -> (WaterTransformSkill)IntrinsicSkills.WATER_TRANSFORM.get();
         case WIND -> (WindTransformSkill)IntrinsicSkills.WIND_TRANSFORM.get();
         default -> null;
      };
   }
}
