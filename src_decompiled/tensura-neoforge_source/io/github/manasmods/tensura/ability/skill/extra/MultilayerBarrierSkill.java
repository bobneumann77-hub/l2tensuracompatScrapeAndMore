package io.github.manasmods.tensura.ability.skill.extra;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.ExtraSkillConfig;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import io.github.manasmods.tensura.util.SubordinateHelper;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class MultilayerBarrierSkill extends Skill {
   public static final ExtraSkillConfig.MultilayerBarrier CONFIG = ((ExtraSkillConfig)ConfigRegistry.getConfig(ExtraSkillConfig.class)).MultilayerBarrier;
   protected static final ResourceLocation MULTILAYER = ResourceLocation.fromNamespaceAndPath("tensura", "multilayer_barrier");
   protected static final ResourceLocation ALLY_MULTILAYER = ResourceLocation.fromNamespaceAndPath("tensura", "ally_multilayer_barrier");

   public MultilayerBarrierSkill() {
      super(Skill.SkillType.EXTRA);
   }

   @Override
   public void onForgetSkill(ManasSkillInstance instance, LivingEntity entity) {
      super.onForgetSkill(instance, entity);
      AttributeInstance attribute = entity.getAttribute(TensuraAttributes.MULTILAYER_BARRIER);
      if (attribute != null) {
         if (attribute.getModifier(MULTILAYER) != null) {
            attribute.removeModifier(MULTILAYER);
         }
      }
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      LivingEntity target = entity.isShiftKeyDown() ? ObjectSelectionHelper.getTargetingEntity(entity, 5.0, false) : null;
      if (target != null) {
         AttributeInstance attributeInstance = Objects.requireNonNull(target.getAttribute(TensuraAttributes.MULTILAYER_BARRIER));
         if (!attributeInstance.hasModifier(MULTILAYER) && SubordinateHelper.isAlly(entity, target)) {
            if (attributeInstance.getModifier(ALLY_MULTILAYER) != null) {
               attributeInstance.removeModifier(ALLY_MULTILAYER);
               if (attributeInstance.getValue() <= 0.0) {
                  attributeInstance.removeModifiers();
               }

               target.level()
                  .playSound(
                     null,
                     target.getX(),
                     target.getY(),
                     target.getZ(),
                     (SoundEvent)TensuraSoundEvents.BARRIER_BREAK.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
               TensuraParticleHelper.spawnServerParticles(
                  target.level(),
                  TensuraParticleUtils.getBlueWave(0.8F, target.getBbWidth() * 2.0F, 0.0F, false),
                  target.getX(),
                  target.getY() + target.getBbHeight() * 0.75,
                  target.getZ()
               );
               TensuraParticleHelper.spawnServerParticles(
                  target.level(),
                  TensuraParticleUtils.getPurpleWave(0.8F, target.getBbWidth() * 2.5F, 0.0F, false),
                  target.getX(),
                  target.getY() + target.getBbHeight() * 0.5,
                  target.getZ()
               );
               TensuraParticleHelper.spawnServerParticles(
                  target.level(),
                  TensuraParticleUtils.getBlueWave(0.8F, target.getBbWidth() * 2.0F, 0.0F, false),
                  target.getX(),
                  target.getY() + target.getBbHeight() * 0.25,
                  target.getZ()
               );
            } else {
               instance.addMasteryPoint(entity);
               instance.setCoolDown(CONFIG.cooldown, mode);
               double barrierPoints = target.getMaxHealth() * CONFIG.allyPointMultiplier;
               attributeInstance.addOrReplacePermanentModifier(new AttributeModifier(ALLY_MULTILAYER, barrierPoints, Operation.ADD_VALUE));
               target.level()
                  .playSound(
                     null,
                     target.getX(),
                     target.getY(),
                     target.getZ(),
                     (SoundEvent)TensuraSoundEvents.DEFENCE_ACTIVATE.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
               TensuraParticleHelper.spawnServerParticles(
                  target.level(),
                  TensuraParticleUtils.getBlueWave(0.8F, target.getBbWidth() * 2.5F, -0.2F, true),
                  target.getX(),
                  target.getY() + target.getBbHeight() * 0.75,
                  target.getZ()
               );
               TensuraParticleHelper.spawnServerParticles(
                  target.level(),
                  TensuraParticleUtils.getColorlessWave(0.8F, target.getBbWidth() * 3.5F, -0.2F, true),
                  target.getX(),
                  target.getY() + target.getBbHeight() * 0.75,
                  target.getZ()
               );
               TensuraParticleHelper.spawnServerParticles(
                  target.level(),
                  TensuraParticleUtils.getPurpleWave(0.8F, target.getBbWidth() * 3.0F, -0.2F, true),
                  target.getX(),
                  target.getY() + target.getBbHeight() * 0.5,
                  target.getZ()
               );
               TensuraParticleHelper.spawnServerParticles(
                  target.level(),
                  TensuraParticleUtils.getBlueWave(0.8F, target.getBbWidth() * 4.0F, -0.2F, true),
                  target.getX(),
                  target.getY() + target.getBbHeight() * 0.5,
                  target.getZ()
               );
               TensuraParticleHelper.spawnServerParticles(
                  target.level(),
                  TensuraParticleUtils.getBlueWave(0.8F, target.getBbWidth() * 2.5F, -0.2F, true),
                  target.getX(),
                  target.getY() + target.getBbHeight() * 0.25,
                  target.getZ()
               );
               TensuraParticleHelper.spawnServerParticles(
                  target.level(),
                  TensuraParticleUtils.getColorlessWave(0.8F, target.getBbWidth() * 3.5F, -0.2F, true),
                  target.getX(),
                  target.getY() + target.getBbHeight() * 0.25,
                  target.getZ()
               );
            }
         } else {
            entity.sendSystemMessage(Component.translatable("tensura.ability.activation_failed").withStyle(ChatFormatting.RED));
            entity.level()
               .playSound(
                  null,
                  target.getX(),
                  target.getY(),
                  target.getZ(),
                  (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                  TensuraSkill.ABILITY_SOUND,
                  1.0F,
                  1.0F
               );
         }
      } else {
         AttributeInstance attributeInstance = Objects.requireNonNull(entity.getAttribute(TensuraAttributes.MULTILAYER_BARRIER));
         attributeInstance.removeModifier(ALLY_MULTILAYER);
         if (attributeInstance.getModifier(MULTILAYER) != null) {
            attributeInstance.removeModifier(MULTILAYER);
            if (attributeInstance.getValue() <= 0.0) {
               attributeInstance.removeModifiers();
            }

            entity.level()
               .playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.BARRIER_BREAK.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
            TensuraParticleHelper.spawnServerParticles(
               entity.level(),
               TensuraParticleUtils.getBlueWave(0.8F, entity.getBbWidth() * 2.0F, 0.0F, false),
               entity.getX(),
               entity.getY() + entity.getBbHeight() * 0.75,
               entity.getZ()
            );
            TensuraParticleHelper.spawnServerParticles(
               entity.level(),
               TensuraParticleUtils.getPurpleWave(0.8F, entity.getBbWidth() * 2.5F, 0.0F, false),
               entity.getX(),
               entity.getY() + entity.getBbHeight() * 0.5,
               entity.getZ()
            );
            TensuraParticleHelper.spawnServerParticles(
               entity.level(),
               TensuraParticleUtils.getBlueWave(0.8F, entity.getBbWidth() * 2.0F, 0.0F, false),
               entity.getX(),
               entity.getY() + entity.getBbHeight() * 0.25,
               entity.getZ()
            );
         } else {
            instance.addMasteryPoint(entity);
            instance.setCoolDown(CONFIG.cooldown, mode);
            double barrierPoints = entity.getMaxHealth() * CONFIG.pointMultiplier;
            attributeInstance.addOrReplacePermanentModifier(new AttributeModifier(MULTILAYER, barrierPoints, Operation.ADD_VALUE));
            entity.level()
               .playSound(
                  null,
                  entity.getX(),
                  entity.getY(),
                  entity.getZ(),
                  (SoundEvent)TensuraSoundEvents.DEFENCE_ACTIVATE.get(),
                  TensuraSkill.ABILITY_SOUND,
                  1.0F,
                  1.0F
               );
            TensuraParticleHelper.spawnServerParticles(
               entity.level(),
               TensuraParticleUtils.getBlueWave(0.8F, entity.getBbWidth() * 2.5F, -0.2F, true),
               entity.getX(),
               entity.getY() + entity.getBbHeight() * 0.75,
               entity.getZ()
            );
            TensuraParticleHelper.spawnServerParticles(
               entity.level(),
               TensuraParticleUtils.getColorlessWave(0.8F, entity.getBbWidth() * 3.5F, -0.2F, true),
               entity.getX(),
               entity.getY() + entity.getBbHeight() * 0.75,
               entity.getZ()
            );
            TensuraParticleHelper.spawnServerParticles(
               entity.level(),
               TensuraParticleUtils.getPurpleWave(0.8F, entity.getBbWidth() * 3.0F, -0.2F, true),
               entity.getX(),
               entity.getY() + entity.getBbHeight() * 0.5,
               entity.getZ()
            );
            TensuraParticleHelper.spawnServerParticles(
               entity.level(),
               TensuraParticleUtils.getBlueWave(0.8F, entity.getBbWidth() * 4.0F, -0.2F, true),
               entity.getX(),
               entity.getY() + entity.getBbHeight() * 0.5,
               entity.getZ()
            );
            TensuraParticleHelper.spawnServerParticles(
               entity.level(),
               TensuraParticleUtils.getBlueWave(0.8F, entity.getBbWidth() * 2.5F, -0.2F, true),
               entity.getX(),
               entity.getY() + entity.getBbHeight() * 0.25,
               entity.getZ()
            );
            TensuraParticleHelper.spawnServerParticles(
               entity.level(),
               TensuraParticleUtils.getColorlessWave(0.8F, entity.getBbWidth() * 3.5F, -0.2F, true),
               entity.getX(),
               entity.getY() + entity.getBbHeight() * 0.25,
               entity.getZ()
            );
         }
      }
   }
}
