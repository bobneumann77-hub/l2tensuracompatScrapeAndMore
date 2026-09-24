package io.github.manasmods.tensura.ability.battlewill.projectile;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.battlewill.Battlewill;
import io.github.manasmods.tensura.config.ability.BattlewillConfig;
import io.github.manasmods.tensura.entity.magic.barrier.BarrierEntity;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.Vec3;

public class OgreFlameArt extends Battlewill {
   private static final BattlewillConfig.OgreFlame CONFIG = ((BattlewillConfig)ConfigRegistry.getConfig(BattlewillConfig.class)).OgreFlame;

   @Override
   public double getAuraCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return CONFIG.auraCost;
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      Level level = entity.level();
      int castTime = CONFIG.castTime;
      if (heldTicks >= castTime) {
         if (heldTicks == castTime + 1) {
            instance.addMasteryPoint(entity);
         }

         BlockPos pos = ObjectSelectionHelper.getPlayerPOVHitResult(level, entity, Fluid.NONE, CONFIG.maxDistance).getBlockPos();
         Pair<Double, Double> cost = Pair.of(this.getAuraCost(entity, instance, mode), this.getMagiculeCost(entity, instance, mode));
         BarrierEntity.spawnLastingBarrier(
            (EntityType<? extends BarrierEntity>)MiscEntityTypes.FLARE_CIRCLE.get(),
            CONFIG.flameDamage,
            CONFIG.flameRadius,
            7.0F,
            50,
            entity.getMaxHealth() / 2.0F,
            new Vec3(pos.getX(), pos.getY(), pos.getZ()),
            entity,
            instance,
            mode,
            cost,
            cost,
            heldTicks
         );
         level.playSound(
            null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.CAST_FIRE.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F
         );
         return heldTicks - castTime <= (instance.isMastered(entity) ? CONFIG.maxTime * 2 : CONFIG.maxTime);
      } else {
         if (entity instanceof Player player) {
            double sec = heldTicks / 20.0;
            player.displayClientMessage(
               Component.translatable(
                     "tensura.magic.cast_time.max", new Object[]{SkillUtils.ROUND_DOUBLE.format(sec), SkillUtils.ROUND_DOUBLE.format(castTime / 20.0)}
                  )
                  .setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)),
               true
            );
         }

         if (heldTicks % 5 == 0) {
            TensuraParticleHelper.addServerParticlesAroundSelf(entity, (ParticleOptions)TensuraParticleTypes.RED_FIRE.get(), 2.0);
         }

         return true;
      }
   }
}
