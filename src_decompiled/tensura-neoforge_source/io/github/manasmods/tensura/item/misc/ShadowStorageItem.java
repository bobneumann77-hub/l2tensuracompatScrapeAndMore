package io.github.manasmods.tensura.item.misc;

import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.entity.template.subclass.ITensuraMount;
import java.util.List;
import lombok.NonNull;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.Nullable;

public class ShadowStorageItem extends Item implements DispensibleContainerItem {
   public ShadowStorageItem(Properties properties) {
      super(properties);
      DispenserBlock.registerBehavior(this, new DefaultDispenseItemBehavior() {
         public ItemStack execute(BlockSource source, ItemStack stack) {
            DispensibleContainerItem dispensiblecontaineritem = (DispensibleContainerItem)stack.getItem();
            BlockPos blockpos = source.pos().relative((Direction)source.state().getValue(DispenserBlock.FACING));
            dispensiblecontaineritem.checkExtraContent(null, source.level(), stack, blockpos);
            return ItemStack.EMPTY;
         }
      });
   }

   public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
      if (itemStack.has(DataComponents.CUSTOM_DATA)) {
         CompoundTag tag = ((CustomData)itemStack.get(DataComponents.CUSTOM_DATA)).getUnsafe();
         list.add(Component.translatable("tensura.main_menu.existence_points", new Object[]{tag.getDouble("ShadowEP")}).withStyle(ChatFormatting.GRAY));
         list.add(Component.translatable("tensura.main_menu.health", new Object[]{tag.getDouble("ShadowHP")}).withStyle(ChatFormatting.GRAY));
         list.add(Component.translatable("tensura.main_menu.spiritual_health", new Object[]{tag.getDouble("ShadowSHP")}).withStyle(ChatFormatting.GRAY));
      }
   }

   public void checkExtraContent(@Nullable Player player, Level level, ItemStack stack, BlockPos pos) {
      if (level instanceof ServerLevel) {
         this.spawnShadow((ServerLevel)level, stack, pos);
         level.gameEvent(player, GameEvent.ENTITY_PLACE, pos);
      }
   }

   protected void playEmptySound(@Nullable Player player, LevelAccessor level, BlockPos blockPos) {
      level.playSound(player, blockPos, SoundEvents.EVOKER_CAST_SPELL, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
   }

   public boolean emptyContents(@Nullable Player pPlayer, Level pLevel, BlockPos pPos, @Nullable BlockHitResult pResult) {
      return false;
   }

   private void spawnShadow(ServerLevel serverLevel, ItemStack stack, BlockPos pos) {
      if (stack.has(DataComponents.CUSTOM_DATA)) {
         CompoundTag tag = ((CustomData)stack.get(DataComponents.CUSTOM_DATA)).copyTag();
         if (tag != null) {
            ResourceLocation typeID = ResourceLocation.tryParse(tag.getString("EntityType"));
            EntityType<?> type = (EntityType<?>)BuiltInRegistries.ENTITY_TYPE.get(typeID);
            Entity shadow = type.create(serverLevel, null, pos, MobSpawnType.BUCKET, true, false);
            if (shadow != null) {
               shadow.load(((CustomData)stack.getOrDefault(DataComponents.BUCKET_ENTITY_DATA, CustomData.EMPTY)).copyTag());
               shadow.moveTo(pos, shadow.getXRot(), shadow.getYRot());
               serverLevel.addFreshEntityWithPassengers(shadow);
               if (shadow instanceof ITensuraMount mount) {
                  mount.onShadowStorageSpawned();
               }
            }
         }
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
               this.playEmptySound(player, level, relative);
               return InteractionResultHolder.sidedSuccess(ItemStack.EMPTY, level.isClientSide());
            } else {
               return super.use(level, player, hand);
            }
         }
      }
   }
}
