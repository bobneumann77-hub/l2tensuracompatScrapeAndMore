package io.github.manasmods.tensura.ability.skill.extra;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.ManasSkill.AttributeTemplate;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.ExtraSkillConfig;
import io.github.manasmods.tensura.entity.magic.field.haki.HakiField;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.SubordinateHelper;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.level.entity.EntityTypeTest;

public class MortalFearSkill extends Skill {
   private static final ExtraSkillConfig.MortalFear CONFIG = ((ExtraSkillConfig)ConfigRegistry.getConfig(ExtraSkillConfig.class)).MortalFear;
   private static final ResourceLocation HAKI = ResourceLocation.fromNamespaceAndPath("tensura", "mortal_fear");

   public MortalFearSkill() {
      super(Skill.SkillType.EXTRA);
      this.addHeldAttributeModifier(Attributes.MOVEMENT_SPEED, HAKI, HakiSkill.CONFIG.speedMultiplier - 1.0, Operation.ADD_MULTIPLIED_TOTAL);
   }

   public boolean canScroll(ManasSkillInstance instance, LivingEntity entity, int mode) {
      return instance.getMastery() >= 0.0;
   }

   @Override
   public boolean shouldTriggerReleaseOnHeldInterrupt(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      return true;
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return HakiSkill.CONFIG.magiculeCost;
   }

   public double getAttributeModifierAmplifier(ManasSkillInstance instance, LivingEntity entity, Holder<Attribute> holder, AttributeTemplate template, int mode) {
      return instance.isMastered(entity) ? (HakiSkill.CONFIG.speedMultiplierMastered - 1.0) / (HakiSkill.CONFIG.speedMultiplier - 1.0) : 1.0;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (instance.getTag() != null) {
         for (String id : instance.getTag().getAllKeys()) {
            if (id.contains("HakiID_")) {
               instance.getTag().putInt(id, 0);
            }
         }

         instance.markDirty();
      }
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (entity.level() instanceof ServerLevel serverLevel) {
         if (heldTicks % BASE_CONFIG.Mastery.masteryHoldTick == 0 && heldTicks > 0) {
            instance.addMasteryPoint(entity);
         }

         double scale = instance.getTag() != null && instance.getTag().contains("scale") ? instance.getTag().getDouble("scale") : 0.0;
         double multiplier = scale == 0.0 ? 1.0 : Math.min(scale, 1.0);
         double ownerEP = EnergyHelper.getMaxEP(entity) * multiplier;
         List<? extends LivingEntity> list = serverLevel.getEntities(
            EntityTypeTest.forClass(LivingEntity.class), living -> SubordinateHelper.isSubordinate(entity, living)
         );
         if (!list.isEmpty()) {
            if (heldTicks % 20 == 0 && EnergyHelper.isOutOfEnergy(entity, instance, mode, list.size())) {
               return false;
            }
         } else {
            if (heldTicks % 20 == 0 && EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
               return false;
            }

            list = List.of(entity);
         }

         this.mortalFear(list, entity, instance, mode, ownerEP, CONFIG.hakiRadius, instance.isMastered(entity));
         if (heldTicks == 1) {
            entity.level()
               .playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.HAKI_START.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
         } else if (heldTicks >= 40) {
            entity.level()
               .playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.HAKI_LOOP.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
         }

         return true;
      } else {
         return false;
      }
   }

   public void onRelease(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int keyNumber, int mode) {
      if (this.hasAttributeApplied(entity, Attributes.MOVEMENT_SPEED, HAKI)) {
         instance.setCoolDown(instance.isMastered(entity) ? HakiSkill.CONFIG.cooldownMastered : HakiSkill.CONFIG.cooldown, mode);
      }
   }

   private void mortalFear(
      List<? extends LivingEntity> list, LivingEntity entity, ManasSkillInstance instance, int mode, double EP, double radius, boolean mastered
   ) {
      for (LivingEntity subordinate : list) {
         if (subordinate != entity) {
            subordinate.addEffect(
               new MobEffectInstance(
                  MobEffects.DAMAGE_BOOST, CONFIG.strengthDuration, mastered ? CONFIG.strengthLevelMastered : CONFIG.strengthLevel, false, true
               )
            );
         }

         HakiField haki = HakiField.getHaki(
            (EntityType<? extends HakiField>)MiscEntityTypes.HAKI_FIELD.get(),
            "HakiID_" + subordinate.getId(),
            HakiField.HakiVariant.MORTAL_FEAR,
            radius * subordinate.getBbHeight(),
            CONFIG.epDifferenceMultiplier,
            HakiSkill.CONFIG.fearDuration,
            subordinate.getBbHeight() / 1.8F,
            subordinate.position().add(0.0, subordinate.getBbHeight() / 2.0F, 0.0),
            subordinate,
            instance,
            this,
            mode
         );
         if (haki != null) {
            if (subordinate != entity) {
               haki.setSubordinate(true);
            }

            haki.setEp(EP);
         }
      }
   }

   public void onScroll(ManasSkillInstance instance, LivingEntity entity, double delta, int mode) {
      HakiSkill.changeEPUsed(instance, entity, delta);
   }
}
