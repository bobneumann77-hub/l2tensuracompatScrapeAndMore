package io.github.manasmods.tensura.entity.template.subclass;

import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.EntityEvents.ProjectileHitResult;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.data.TensuraEntityTags;
import io.github.manasmods.tensura.data.TensuraRaceTags;
import io.github.manasmods.tensura.entity.ai.behaviour.TensuraBehaviourHelper;
import io.github.manasmods.tensura.race.RaceUtils;
import io.github.manasmods.tensura.storage.Alignment;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.util.EnergyHelper;
import java.util.List;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.Nullable;

public interface IOtherworlder extends ISubordinate {
   ResourceLocation getTextureLocation();

   List<ManasSkill> getUniqueSkills();

   @Override
   default boolean shouldTarget(LivingEntity target) {
      if (target == this) {
         return false;
      }

      if (RaceUtils.isNonLiving(target)) {
         return false;
      }

      if (EnergyHelper.getMaxEP(target) < 5000.0) {
         return false;
      }

      Optional<ManasRaceInstance> race = RaceAPI.getRaceFrom(target).getRace();
      if (race.isPresent()) {
         if (race.get().is(TensuraRaceTags.SPIRITUAL)) {
            return false;
         } else {
            return TensuraStorages.getExistenceFrom(target).getAlignment().equals(Alignment.MAJIN) ? true : !race.get().is(TensuraRaceTags.HUMAN_LIKE);
         }
      } else {
         return target.getType().is(TensuraEntityTags.OTHERWORLDER_PREY)
            ? true
            : TensuraStorages.getExistenceFrom(target).getAlignment().equals(Alignment.MAJIN);
      }
   }

   default void onProjectileImpact(
      EntityHitResult hitResult, Projectile projectile, Changeable<ProjectileDeflection> deflection, Changeable<ProjectileHitResult> result
   ) {
   }

   default void dropSkills(@Nullable Entity attacker) {
      if (attacker instanceof Player player) {
         for (ManasSkill skill : this.getUniqueSkills()) {
            double chance = TensuraBehaviourHelper.CONFIG.MobSpecific.otherworlderSkillDrop;
            if (!(chance <= player.getRandom().nextFloat() * 100.0F) && SkillHelper.learnSkill(player, skill)) {
               player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
         }
      }
   }
}
