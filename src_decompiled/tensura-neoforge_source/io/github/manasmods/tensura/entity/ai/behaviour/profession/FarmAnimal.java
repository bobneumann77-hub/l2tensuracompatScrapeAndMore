package io.github.manasmods.tensura.entity.ai.behaviour.profession;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.tensura.entity.template.PlayerLikeEntity;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.Nullable;

public class FarmAnimal<E extends PlayerLikeEntity> extends ExtendedBehaviour<E> {
   private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(2)
      .usesMemories(new MemoryModuleType[]{MemoryModuleType.WALK_TARGET, MemoryModuleType.ATTACK_TARGET});
   private Function<E, Double> searchRange = entity -> 12.0;
   private Function<E, Integer> animalCount = entity -> 6;
   private Function<E, Integer> closeEnough = entity -> 1;
   private Function<E, Integer> interactRange = entity -> 2;
   private Function<E, Float> speed = entity -> 1.0F;
   private Predicate<E> canFarm = PlayerLikeEntity::shouldDoButchering;
   private Predicate<E> shouldButcher = entity -> true;
   private Predicate<E> shouldBreed = entity -> true;
   private Predicate<E> shouldHarvest = entity -> false;
   private BiPredicate<E, Animal> canHarvest = (entity, animal) -> animal instanceof Sheep sheep
      && sheep.readyForShearing()
      && entity.getMainHandItem().is(Items.SHEARS);
   private BiConsumer<E, Animal> doHarvest = (entity, animal) -> {
      if (animal instanceof Sheep sheep && sheep.readyForShearing() && entity.getMainHandItem().is(Items.SHEARS)) {
         sheep.shear(SoundSource.NEUTRAL);
         entity.swing(InteractionHand.MAIN_HAND);
         entity.getMainHandItem().hurtAndBreak(1, entity, EquipmentSlot.MAINHAND);
      }
   };
   private Class<? extends Animal>[] animalTypes;
   @Nullable
   private Animal targetAnimal = null;
   @Nullable
   private Animal partnerAnimal = null;
   private boolean harvesting = false;

   public FarmAnimal() {
      this.runFor(entity -> 300);
      this.types(Cow.class, Pig.class, Chicken.class, Rabbit.class);
   }

   @SafeVarargs
   public final FarmAnimal<E> types(Class<? extends Animal>... types) {
      this.animalTypes = types;
      return this;
   }

   public FarmAnimal<E> searchRange(Function<E, Double> range) {
      this.searchRange = range;
      return this;
   }

   public FarmAnimal<E> animalCount(Function<E, Integer> count) {
      this.animalCount = count;
      return this;
   }

   public FarmAnimal<E> closeEnough(Function<E, Integer> count) {
      this.closeEnough = count;
      return this;
   }

   public FarmAnimal<E> interactRange(Function<E, Integer> count) {
      this.interactRange = count;
      return this;
   }

   public FarmAnimal<E> speed(Function<E, Float> speed) {
      this.speed = speed;
      return this;
   }

   public FarmAnimal<E> canFarm(Predicate<E> predicate) {
      this.canFarm = predicate;
      return this;
   }

   public FarmAnimal<E> shouldButcher(Predicate<E> predicate) {
      this.shouldButcher = predicate;
      return this;
   }

   public FarmAnimal<E> shouldBreed(Predicate<E> predicate) {
      this.shouldBreed = predicate;
      return this;
   }

   public FarmAnimal<E> shouldHarvest(Predicate<E> predicate) {
      this.shouldHarvest = predicate;
      return this;
   }

   public FarmAnimal<E> canHarvest(BiPredicate<E, Animal> predicate) {
      this.canHarvest = predicate;
      return this;
   }

   public FarmAnimal<E> doHarvest(BiConsumer<E, Animal> consumer) {
      this.doHarvest = consumer;
      return this;
   }

   protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
      return MEMORY_REQUIREMENTS;
   }

   protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
      if (!this.canFarm.test(entity)) {
         return false;
      }

      List<Animal> animals = this.getNearbyAnimals(level, entity);
      if (animals.isEmpty()) {
         return false;
      }

      int count = animals.size();
      int desiredCount = this.animalCount.apply(entity);
      if (count > desiredCount) {
         if (this.shouldButcher.test(entity)) {
            if (BrainUtils.getMemory(entity, MemoryModuleType.ATTACK_TARGET) != null) {
               return false;
            }

            Animal target = animals.stream().min(Comparator.comparingDouble(LivingEntity::getHealth)).orElse(null);
            if (target != null) {
               this.targetAnimal = target;
               this.partnerAnimal = null;
               return true;
            }
         }

         return false;
      } else {
         if (this.shouldBreed.test(entity)) {
            Animal target = this.getBreedable(animals);
            Animal partner = target == null ? null : this.getPartner(animals, target);
            if (target != null && partner != null) {
               this.targetAnimal = target;
               this.partnerAnimal = partner;
               return true;
            }
         }

         if (this.shouldHarvest.test(entity)) {
            Animal target = this.getHarvestable(animals, entity);
            if (target != null) {
               this.targetAnimal = target;
               this.partnerAnimal = null;
               this.harvesting = true;
               return true;
            }
         }

         return false;
      }
   }

   protected void start(ServerLevel level, E entity, long gameTime) {
      if (this.targetAnimal != null) {
         if (this.partnerAnimal == null && !this.harvesting) {
            BrainUtils.setTargetOfEntity(entity, this.targetAnimal);
            BrainUtils.clearMemory(entity, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
         } else {
            BrainUtils.setMemory(
               entity, MemoryModuleType.WALK_TARGET, new WalkTarget(this.targetAnimal, this.speed.apply(entity), this.closeEnough.apply(entity))
            );
            BrainUtils.setMemory(entity, MemoryModuleType.LOOK_TARGET, new EntityTracker(this.targetAnimal, true));
         }
      }
   }

   protected boolean canStillUse(ServerLevel level, E entity, long now) {
      if (this.harvesting) {
         return this.targetAnimal != null && this.targetAnimal.isAlive() && this.canHarvest.test(entity, this.targetAnimal);
      } else {
         return this.partnerAnimal != null && this.partnerAnimal.isAlive()
            ? this.targetAnimal != null && this.targetAnimal.isAlive() && this.hasEnoughFood(this.targetAnimal, entity, 1)
            : false;
      }
   }

   protected void tick(ServerLevel level, E entity, long gameTime) {
      if (this.targetAnimal != null && this.targetAnimal.isAlive()) {
         if (this.partnerAnimal == null) {
            if (this.shouldHarvest.test(entity)
               && this.canHarvest.test(entity, this.targetAnimal)
               && entity.distanceTo(this.targetAnimal) <= this.interactRange.apply(entity).intValue()) {
               this.doHarvest.accept(entity, this.targetAnimal);
               Animal target = this.getHarvestable(this.getNearbyAnimals(level, entity), entity);
               if (target != null) {
                  this.targetAnimal = target;
                  this.start(level, entity, gameTime);
               }
            }
         } else if (this.shouldBreed.test(entity) && this.partnerAnimal.isAlive()) {
            if (this.targetAnimal.getAge() == 0 && this.targetAnimal.canFallInLove()) {
               if (entity.distanceTo(this.targetAnimal) <= this.interactRange.apply(entity).intValue()) {
                  this.feedAnimal(entity, this.targetAnimal);
               }
            } else if (this.partnerAnimal.getAge() == 0 && this.partnerAnimal.canFallInLove()) {
               BrainUtils.setMemory(
                  entity, MemoryModuleType.WALK_TARGET, new WalkTarget(this.partnerAnimal, this.speed.apply(entity), this.closeEnough.apply(entity))
               );
               if (entity.distanceTo(this.partnerAnimal) <= this.interactRange.apply(entity).intValue()) {
                  this.feedAnimal(entity, this.partnerAnimal);
               }
            }
         }
      }
   }

   private void feedAnimal(E entity, Animal animal) {
      animal.setInLove(null);
      entity.swing(InteractionHand.OFF_HAND);

      for (int j = 0; j < entity.inventory.getContainerSize(); j++) {
         ItemStack itemStack = entity.inventory.getItem(j);
         if (animal.isFood(itemStack)) {
            itemStack.shrink(1);
            break;
         }
      }
   }

   protected void stop(ServerLevel level, E entity, long gameTime) {
      this.targetAnimal = null;
      this.partnerAnimal = null;
      this.harvesting = false;
      BrainUtils.clearMemory(entity, MemoryModuleType.WALK_TARGET);
   }

   private List<Animal> getNearbyAnimals(ServerLevel level, E entity) {
      AABB area = entity.getBoundingBox().inflate(this.searchRange.apply(entity));
      return level.getEntitiesOfClass(Animal.class, area, animal -> animal.isAlive() && !animal.isBaby() && this.isAllowedAnimal(animal));
   }

   private boolean isAllowedAnimal(Animal animal) {
      if (this.animalTypes == null) {
         return animal instanceof Animal;
      }

      for (Class<? extends Animal> type : this.animalTypes) {
         if (type.isInstance(animal)) {
            return true;
         }
      }

      return false;
   }

   private Animal getBreedable(List<Animal> animals) {
      for (Animal animal : animals) {
         if (animal.getAge() == 0 && animal.canFallInLove()) {
            return animal;
         }
      }

      return null;
   }

   private Animal getPartner(List<Animal> animals, Animal first) {
      for (Animal animal : animals) {
         if (animal != first && animal.getType() == first.getType() && animal.getAge() == 0 && animal.canFallInLove()) {
            return animal;
         }
      }

      return null;
   }

   private Animal getHarvestable(List<Animal> animals, E entity) {
      for (Animal animal : animals) {
         if (this.canHarvest.test(entity, animal)) {
            return animal;
         }
      }

      return null;
   }

   private boolean hasEnoughFood(Animal animal, E entity, int amount) {
      int i = 0;

      for (int j = 0; j < entity.inventory.getContainerSize(); j++) {
         ItemStack itemStack = entity.inventory.getItem(j);
         if (animal.isFood(itemStack)) {
            i += itemStack.getCount();
         }

         if (i >= amount) {
            return true;
         }
      }

      return false;
   }
}
