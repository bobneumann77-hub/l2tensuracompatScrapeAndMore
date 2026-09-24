package io.github.manasmods.tensura.entity.ai.behaviour.misc;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.GameRules;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.BreedWithPartner;
import net.tslat.smartbrainlib.util.BrainUtils;

public class CustomBreedWithPartner<E extends Animal> extends BreedWithPartner<E> {
   protected BiConsumer<E, Animal> breedSupplier = (entity, partner) -> entity.spawnChildFromBreeding((ServerLevel)entity.level(), partner);

   public CustomBreedWithPartner() {
      this.partnerPredicate = Animal::canMate;
   }

   public CustomBreedWithPartner<E> shouldPartner(BiPredicate<E, Animal> predicate) {
      this.partnerPredicate = predicate;
      return this;
   }

   public CustomBreedWithPartner<E> performBreed(BiConsumer<E, Animal> callback) {
      this.breedSupplier = callback;
      return this;
   }

   protected void tick(E entity) {
      BehaviorUtils.lockGazeAndWalkToEachOther(
         entity, this.partner, (Float)this.speedMod.apply(entity, this.partner), this.closeEnoughDist.applyAsInt(entity, this.partner)
      );
      if (entity.closerThan(this.partner, 3.0) && entity.tickCount == this.childBreedTick) {
         this.breedSupplier.accept(entity, this.partner);
         BrainUtils.clearMemory(entity, MemoryModuleType.BREED_TARGET);
         BrainUtils.clearMemory(this.partner, MemoryModuleType.BREED_TARGET);
      }
   }

   public static void applyBreedingReward(Animal entity, Animal partner) {
      ServerLevel serverLevel = (ServerLevel)entity.level();
      Optional.ofNullable(entity.getLoveCause()).or(() -> Optional.ofNullable(partner.getLoveCause())).ifPresent(serverPlayer -> {
         serverPlayer.awardStat(Stats.ANIMALS_BRED);
         CriteriaTriggers.BRED_ANIMALS.trigger(serverPlayer, entity, partner, null);
      });
      serverLevel.broadcastEntityEvent(entity, (byte)18);
      if (serverLevel.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
         serverLevel.addFreshEntity(new ExperienceOrb(serverLevel, entity.getX(), entity.getY(), entity.getZ(), entity.getRandom().nextInt(7) + 1));
      }
   }

   public static void setBreedCooldown(Animal entity, Animal partner, int cooldown) {
      entity.setAge(cooldown);
      entity.resetLove();
      partner.setAge(cooldown);
      partner.resetLove();
   }
}
