package io.github.manasmods.tensura.entity.template.subclass;

import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.magic.Magic;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.data.TensuraRaceTags;
import io.github.manasmods.tensura.race.RaceUtils;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import java.util.Optional;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import org.jetbrains.annotations.Nullable;

public interface IDaemon extends ISpiritual {
   default boolean canCastMagics(LivingEntity entity) {
      return !entity.hasEffect(TensuraMobEffects.getReference(TensuraMobEffects.ANTI_MAGIC));
   }

   @Nullable
   default ManasSkillInstance getMagic(LivingEntity entity, Magic magic) {
      Optional<ManasSkillInstance> skill = SkillAPI.getSkillsFrom(entity).getSkill(magic);
      if (skill.isEmpty() || skill.get().onCoolDown(0)) {
         return null;
      } else {
         return !skill.get().canInteractSkill(entity) ? null : skill.get();
      }
   }

   default boolean shouldAttack(TamableAnimal mob, LivingEntity target) {
      if (target == this) {
         return false;
      }

      if (RaceUtils.isNonLiving(target)) {
         return false;
      }

      if (mob.isAlliedTo(target)) {
         return false;
      }

      if (mob.getOwner() != null) {
         if (target.isAlliedTo(mob.getOwner())) {
            return false;
         } else if (mob.getTarget() == target) {
            return true;
         } else {
            return target instanceof Mob targetMob
               ? targetMob.getTarget() == mob.getOwner()
               : mob.getOwner().getLastHurtMob() == target || mob.getOwner().getLastHurtByMob() == target;
         }
      } else if (target.hasInfiniteMaterials()) {
         return false;
      } else if (mob.getTarget() == target) {
         return true;
      } else if (target.getType().is(TensuraEntityTags.HELL_NEUTRAL)) {
         return false;
      } else {
         Optional<ManasRaceInstance> race = RaceAPI.getRaceFrom(target).getRace();
         if (race.isPresent()) {
            return race.get().is(TensuraRaceTags.DAEMON) ? target.distanceToSqr(mob) <= 256.0 : !race.get().is(TensuraRaceTags.SPIRITUAL);
         } else {
            return true;
         }
      }
   }
}
