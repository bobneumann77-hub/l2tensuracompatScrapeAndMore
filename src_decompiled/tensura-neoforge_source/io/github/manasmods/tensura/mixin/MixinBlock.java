package io.github.manasmods.tensura.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.manasmods.tensura.ability.SkillUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Block.class)
public class MixinBlock {
   @ModifyReturnValue(
      method = "getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;",
      at = @At("RETURN")
   )
   private static List<ItemStack> smeltDroppedStacks(
      List<ItemStack> original, BlockState state, ServerLevel level, BlockPos pos, BlockEntity blockEntity, Entity entity, ItemStack tool
   ) {
      if (entity instanceof LivingEntity living) {
         if (!SkillUtils.canAutoSmelt(living)) {
            return original;
         }

         float xp = 0.0F;
         List<ItemStack> drops = new ArrayList<>(original.size());
         boolean anySmelted = false;

         for (ItemStack drop : original) {
            Optional<RecipeHolder<SmeltingRecipe>> recipe = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(drop), level);
            if (recipe.isPresent()) {
               anySmelted = true;
               ItemStack smelted = ((SmeltingRecipe)recipe.get().value()).getResultItem(level.registryAccess()).copy();
               smelted.setCount(drop.getCount());
               drops.add(smelted);
               float exp = ((SmeltingRecipe)recipe.get().value()).getExperience() * drop.getCount();
               xp += Mth.floor(exp);
               float frac = Mth.frac(exp);
               if (frac != 0.0F && Math.random() < frac) {
                  xp++;
               }
            } else {
               drops.add(drop);
            }
         }

         if (!anySmelted) {
            return original;
         }

         if (xp > 0.0F) {
            ExperienceOrb.award(level, Vec3.atCenterOf(pos), (int)xp);
         }

         level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.GENERIC_BURN, SoundSource.PLAYERS, 0.5F, 1.0F);
         level.sendParticles(ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 5, 0.04, 0.06, 0.04, 0.05);
         return drops;
      } else {
         return original;
      }
   }
}
