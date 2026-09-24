package io.github.manasmods.tensura.item.misc;

import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import lombok.NonNull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.Nullable;

public class SlimeBucketItem extends MobBucketItem {
   public SlimeBucketItem(Properties properties) {
      super((EntityType)MonsterEntityTypes.SLIME.get(), Fluids.EMPTY, SoundEvents.SLIME_JUMP, properties);
      DispenserBlock.registerBehavior(this, new DefaultDispenseItemBehavior() {
         public ItemStack execute(BlockSource source, ItemStack stack) {
            DispensibleContainerItem dispensiblecontaineritem = (DispensibleContainerItem)stack.getItem();
            BlockPos blockpos = source.pos().relative((Direction)source.state().getValue(DispenserBlock.FACING));
            dispensiblecontaineritem.checkExtraContent(null, source.level(), stack, blockpos);
            return Items.BUCKET.getDefaultInstance();
         }
      });
   }

   public void checkExtraContent(@Nullable Player player, Level level, ItemStack stack, BlockPos pos) {
      if (level instanceof ServerLevel serverLevel) {
         this.spawnSlime(serverLevel, stack, pos);
         level.gameEvent(player, GameEvent.ENTITY_PLACE, pos);
      }
   }

   private void spawnSlime(ServerLevel serverLevel, ItemStack stack, BlockPos pos) {
      EntityType<?> type = (EntityType<?>)MonsterEntityTypes.SLIME.get();
      if (stack.has(DataComponents.CUSTOM_DATA)) {
         CompoundTag tag = ((CustomData)stack.get(DataComponents.CUSTOM_DATA)).copyTag();
         if (tag.getBoolean("Metal")) {
            type = (EntityType<?>)MonsterEntityTypes.METAL_SLIME.get();
         } else if (tag.getBoolean("Supermassive")) {
            type = (EntityType<?>)MonsterEntityTypes.SUPERMASSIVE_SLIME.get();
         }
      }

      Entity slime = type.create(serverLevel, null, pos, MobSpawnType.BUCKET, true, false);
      if (slime instanceof Bucketable bucketable) {
         CustomData customData = (CustomData)stack.getOrDefault(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY);
         bucketable.loadFromBucketTag(customData.copyTag());
         bucketable.setFromBucket(true);
         slime.moveTo(pos, slime.getXRot(), slime.getYRot());
         serverLevel.addFreshEntityWithPassengers(slime);
      }
   }

   @NonNull
   public InteractionResultHolder<ItemStack> use(@NonNull Level level, Player player, @NonNull InteractionHand hand) {
      if (level == null) {
         throw new NullPointerException("level is marked non-null but is null");
      } else if (hand == null) {
         throw new NullPointerException("hand is marked non-null but is null");
      } else {
         ItemStack itemstack = player.getItemInHand(hand);
         BlockHitResult blockhitresult = getPlayerPOVHitResult(level, player, Fluid.NONE);
         if (blockhitresult.getType() == Type.MISS) {
            return InteractionResultHolder.pass(itemstack);
         } else if (blockhitresult.getType() != Type.BLOCK) {
            return InteractionResultHolder.pass(itemstack);
         } else {
            BlockPos blockpos = blockhitresult.getBlockPos();
            Direction direction = blockhitresult.getDirection();
            BlockPos relative = blockpos.relative(direction);
            if (level.mayInteract(player, blockpos) && player.mayUseItemAt(relative, direction, itemstack)) {
               this.checkExtraContent(player, level, itemstack, relative);
               player.awardStat(Stats.ITEM_USED.get(this));
               return InteractionResultHolder.sidedSuccess(getEmptySuccessItem(itemstack, player), level.isClientSide());
            } else {
               return super.use(level, player, hand);
            }
         }
      }
   }
}
