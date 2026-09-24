package io.github.manasmods.tensura.ability.battlewill.utility;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.battlewill.Battlewill;
import io.github.manasmods.tensura.config.ability.BattlewillConfig;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class InstantMoveArt extends Battlewill {
   private static final BattlewillConfig.InstantMove CONFIG = ((BattlewillConfig)ConfigRegistry.getConfig(BattlewillConfig.class)).InstantMove;
   private static final ResourceLocation INSTANT_MOVE = ResourceLocation.fromNamespaceAndPath("tensura", "instant_move");

   @Override
   public double getAuraCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.auraCost;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
      return instance.isMastered(living);
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (entity.onGround() || entity.isInLiquid()) {
         if (!EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
            Level level = entity.level();
            double distance = instance.isMastered(entity) ? CONFIG.distance * 2.0 : CONFIG.distance;
            BlockHitResult result = ObjectSelectionHelper.getPlayerPOVHitResult(level, entity, Fluid.NONE, distance);
            Vec3 vec3 = ObjectSelectionHelper.getFloorPos(result.getBlockPos().relative(result.getDirection()));
            if (!entity.level().getWorldBorder().isWithinBounds(ObjectSelectionHelper.getBlockPos(vec3))) {
               if (entity instanceof Player player) {
                  player.displayClientMessage(Component.translatable("tensura.skill.teleport.out_border").withStyle(ChatFormatting.RED), false);
               }
            } else {
               Vec3 source = entity.position().add(0.0, entity.getBbHeight() / 2.0F, 0.0);
               Vec3 offSetToTarget = vec3.subtract(source);

               for (int particleIndex = 1; particleIndex < Mth.floor(offSetToTarget.length()); particleIndex++) {
                  Vec3 particlePos = source.add(offSetToTarget.normalize().scale(particleIndex));
                  ((ServerLevel)level).sendParticles(ParticleTypes.CLOUD, particlePos.x, particlePos.y, particlePos.z, 1, 0.0, 0.0, 0.0, 0.0);
               }

               entity.resetFallDistance();
               entity.teleportTo(vec3.x(), vec3.y(), vec3.z());
               entity.hasImpulse = true;
               instance.addMasteryPoint(entity);
               level.playSound(
                  null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.INSTANT_MOVE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
               );
            }
         }
      }
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      AttributeInstance dodge = entity.getAttribute(TensuraAttributes.DODGE_STRENGTH);
      if (dodge != null) {
         dodge.addOrReplacePermanentModifier(new AttributeModifier(INSTANT_MOVE, CONFIG.dodgeStrength, Operation.ADD_VALUE));
      }

      AttributeInstance invulnerability = entity.getAttribute(TensuraAttributes.DODGE_INVULNERABILITY);
      if (invulnerability != null) {
         invulnerability.addOrReplacePermanentModifier(new AttributeModifier(INSTANT_MOVE, CONFIG.dodgeInvulnerability, Operation.ADD_VALUE));
      }
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      AttributeInstance dodge = entity.getAttribute(TensuraAttributes.DODGE_STRENGTH);
      if (dodge != null) {
         dodge.removeModifier(INSTANT_MOVE);
      }

      AttributeInstance invulnerability = entity.getAttribute(TensuraAttributes.DODGE_INVULNERABILITY);
      if (invulnerability != null) {
         invulnerability.removeModifier(INSTANT_MOVE);
      }
   }
}
