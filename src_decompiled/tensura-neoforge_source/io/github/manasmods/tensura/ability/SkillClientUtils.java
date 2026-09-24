package io.github.manasmods.tensura.ability;

import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.tensura.ability.skill.extra.SenseSoundwaveSkill;
import io.github.manasmods.tensura.ability.skill.unique.ReaperSkill;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.data.TensuraRaceTags;
import io.github.manasmods.tensura.effect.ability.EnemySearchEffect;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.skill.ResistanceSkills;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.player.ITensuraPlayer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

public class SkillClientUtils {
   public static final List<Function<Player, SkillClientUtils.XrayType>> XRAY_TYPES = new ArrayList<>();

   public static int getGlowColor(Player player, Entity entity) {
      if (player == entity || entity.getType().is(TensuraEntityTags.NO_HIGHLIGHT)) {
         return -1;
      } else if (entity.getType().is(TensuraEntityTags.CAN_STAY_INVISIBLE) && entity.isInvisibleTo(player)) {
         return -1;
      } else if (entity instanceof LivingEntity living && living.getAttributeValue(TensuraAttributes.PRESENCE_CONCEALMENT) > 0.0) {
         return -1;
      } else {
         double distance = player.distanceTo(entity);
         AttributeInstance radius = player.getAttribute(TensuraAttributes.PRESENCE_SENSE_RADIUS);
         if (radius == null) {
            return -1;
         }

         double senseDistance = radius.getValue();
         if (!(distance <= senseDistance)) {
            return -1;
         }

         int color = -1;
         double sense = player.getAttributeValue(TensuraAttributes.PRESENCE_SENSE);
         ITensuraPlayer playerData = TensuraStorages.getPlayerDataFrom(player);
         switch (playerData.getPresenceSenseMode()) {
            case 0:
               if (sense >= 4.0) {
                  color = 5636095;
               } else if (entity instanceof LivingEntity living && living.isAlive()) {
                  if (sense == 3.0) {
                     color = 5416173;
                  } else if (sense == 2.0) {
                     color = 2208948;
                  } else if (sense > 0.5) {
                     color = 10278389;
                  }
               }
               break;
            case 1:
               boolean enemy = entity.getType().is(TensuraEntityTags.HOSTILE_MONSTER)
                  || entity instanceof Enemy
                  || entity instanceof Mob mob && mob.isAggressive();
               if (enemy) {
                  color = 16213349;
               } else if (entity.getType().is(TensuraEntityTags.NEUTRAL_MOB) || entity instanceof NeutralMob) {
                  color = 15788046;
               }
               break;
            case 2:
               if (entity.getType().is(TensuraEntityTags.TRAP_ENTITY)) {
                  color = 16733525;
               }
               break;
            case 3:
               if (entity.getType().is(TensuraEntityTags.TREASURE_ENTITY)) {
                  color = 16766720;
               }
         }

         AttributeInstance scale = player.getAttribute(Attributes.SCALE);
         if (scale != null && scale.hasModifier(ReaperSkill.REAPER)) {
            color = 11184810;
         }

         if (radius.hasModifier(EnemySearchEffect.ENEMY_SEARCH)) {
            boolean enemy = entity.getType().is(TensuraEntityTags.HOSTILE_MONSTER)
               || entity instanceof Enemy
               || entity instanceof Mob mob && mob.isAggressive();
            if (enemy) {
               color = 16733525;
            }
         }

         if (radius.hasModifier(SenseSoundwaveSkill.SOUND_SENSE) && !entity.isSilent() && !SkillUtils.canBlockSoundDetect(entity)) {
            color = 5635925;
         }

         AttributeInstance heatSense = player.getAttribute(TensuraAttributes.HEAT_SENSE_RADIUS);
         if (heatSense != null && distance <= heatSense.getValue() && !player.level().dimensionType().ultraWarm()) {
            if (entity.getType().is(TensuraEntityTags.HOT_SOURCE)) {
               color = 16089632;
            } else if (entity.getType().is(TensuraEntityTags.COLD_SOURCE)) {
               color = 10278389;
            } else if (entity instanceof LivingEntity living
               && !entity.getType().is(TensuraEntityTags.COLD_BLOODED)
               && !entity.getType().is(TensuraEntityTags.NO_BLOOD)) {
               Optional<ManasRaceInstance> optional = RaceAPI.getRaceFrom(living).getRace();
               if (optional.isEmpty() || !optional.get().is(TensuraRaceTags.COLD_BLOODED)) {
                  color = 16490018;
               }
            }
         }

         return color;
      }
   }

   public static boolean shouldCancelFireOverlay(LivingEntity entity) {
      if (entity.getAttributeValue(TensuraAttributes.PRESENCE_CONCEALMENT) >= 1.0) {
         return true;
      } else if (SkillUtils.isSkillToggled(entity, (ManasSkill)ResistanceSkills.FLAME_ATTACK_NULLIFICATION.get())) {
         return true;
      } else {
         return SkillUtils.isSkillToggled(entity, (ManasSkill)ResistanceSkills.HEAT_NULLIFICATION.get())
            ? true
            : SkillUtils.isSkillToggled(entity, (ManasSkill)ResistanceSkills.THERMAL_FLUCTUATION_NULLIFICATION.get());
      }
   }

   public record XrayType(Predicate<BlockState> predicate, int radius, float red, float green, float blue, float alpha) {
   }
}
