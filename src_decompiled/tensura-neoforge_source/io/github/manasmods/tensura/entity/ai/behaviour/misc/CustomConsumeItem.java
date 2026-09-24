package io.github.manasmods.tensura.entity.ai.behaviour.misc;

import com.mojang.datafixers.util.Pair;
import io.github.manasmods.tensura.entity.ai.behaviour.CustomTimeDelayedBehaviour;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.registry.SBLMemoryTypes;
import net.tslat.smartbrainlib.util.BrainUtils;

public class CustomConsumeItem<E extends LivingEntity> extends CustomTimeDelayedBehaviour<E> {
   private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(1).noMemory((MemoryModuleType)SBLMemoryTypes.SPECIAL_ATTACK_COOLDOWN.get());
   protected Function<E, Integer> consumeIntervalSupplier = entity -> 1;
   private ItemStack consumingStack = ItemStack.EMPTY;
   protected BiFunction<E, ItemStack, Boolean> canConsumeFood = (entity, stack) -> {
      FoodProperties foodProperties = (FoodProperties)stack.get(DataComponents.FOOD);
      return foodProperties != null;
   };
   private long nextEatCheck = 0L;

   public CustomConsumeItem() {
      this.delayFor(entity -> 0);
   }

   public CustomConsumeItem<E> consumeInterval(Function<E, Integer> supplier) {
      this.consumeIntervalSupplier = supplier;
      return this;
   }

   public CustomConsumeItem<E> canConsumeFood(BiFunction<E, ItemStack, Boolean> supplier) {
      this.canConsumeFood = supplier;
      return this;
   }

   protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
      return MEMORY_REQUIREMENTS;
   }

   protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
      long now = level.getGameTime();
      if (now < this.nextEatCheck) {
         return false;
      } else if (!entity.isAlive()) {
         this.nextEatCheck = now + 20L;
         return false;
      } else {
         return true;
      }
   }

   @Override
   protected int getDelayTime(E entity) {
      if (this.consumingStack.isEmpty()) {
         ItemStack food;
         if (this.canConsumeFood.apply(entity, entity.getMainHandItem())) {
            food = entity.getMainHandItem();
            entity.startUsingItem(InteractionHand.MAIN_HAND);
         } else if (this.canConsumeFood.apply(entity, entity.getOffhandItem())) {
            food = entity.getOffhandItem();
            entity.startUsingItem(InteractionHand.OFF_HAND);
         } else {
            food = ItemStack.EMPTY;
         }

         if (food.isEmpty()) {
            return super.getDelayTime(entity);
         }

         this.consumingStack = food.copy();
      }

      if (!this.consumingStack.isEmpty()) {
         FoodProperties foodProperties = (FoodProperties)this.consumingStack.get(DataComponents.FOOD);
         if (foodProperties != null) {
            return super.getDelayTime(entity) + foodProperties.eatDurationTicks();
         }
      }

      return super.getDelayTime(entity);
   }

   @Override
   protected void doDelayedAction(E entity) {
      if (!this.consumingStack.isEmpty()) {
         this.spawnItemParticles(entity, this.consumingStack, 16);
         entity.playSound(
            this.consumingStack.getUseAnimation() == UseAnim.EAT ? SoundEvents.GENERIC_EAT : SoundEvents.GENERIC_DRINK,
            1.0F,
            entity.getRandom().nextFloat() * 0.2F + 0.9F
         );
         this.consumingStack = ItemStack.EMPTY;
      }

      BrainUtils.setForgettableMemory(entity, (MemoryModuleType)SBLMemoryTypes.SPECIAL_ATTACK_COOLDOWN.get(), true, this.consumeIntervalSupplier.apply(entity));
   }

   protected void spawnItemParticles(E mob, ItemStack pStack, int pAmount) {
      for (int i = 0; i < pAmount; i++) {
         Vec3 vec3 = new Vec3((mob.getRandom().nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
         vec3 = vec3.xRot(-mob.getXRot() * (float) Math.PI / 180.0F);
         vec3 = vec3.yRot(-mob.getYRot() * (float) Math.PI / 180.0F);
         double d0 = -mob.getRandom().nextFloat() * 0.6 - 0.3;
         Vec3 vec31 = new Vec3((mob.getRandom().nextFloat() - 0.5) * 0.3, d0, 0.6);
         vec31 = vec31.xRot(-mob.getXRot() * (float) Math.PI / 180.0F);
         vec31 = vec31.yRot(-mob.getYRot() * (float) Math.PI / 180.0F);
         vec31 = vec31.add(mob.getX(), mob.getEyeY(), mob.getZ());
         ((ServerLevel)mob.level())
            .sendParticles(new ItemParticleOption(ParticleTypes.ITEM, pStack), vec31.x, vec31.y, vec31.z, 1, vec3.x, vec3.y + 0.05, vec3.z, 0.0);
      }
   }
}
