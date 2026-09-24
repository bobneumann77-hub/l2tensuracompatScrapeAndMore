package io.github.manasmods.tensura.item.consumable;

import io.github.manasmods.tensura.registry.item.TensuraConsumableItems;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BottleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;

public class MagicBottleItem extends BottleItem {
   public MagicBottleItem(Properties properties) {
      super(properties);
   }

   public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
      List<AreaEffectCloud> list = pLevel.getEntitiesOfClass(
         AreaEffectCloud.class, pPlayer.getBoundingBox().inflate(2.0), cloud -> cloud != null && cloud.isAlive() && cloud.getOwner() instanceof EnderDragon
      );
      ItemStack itemstack = pPlayer.getItemInHand(pHand);
      if (!list.isEmpty()) {
         AreaEffectCloud areaeffectcloud = list.get(0);
         areaeffectcloud.setRadius(areaeffectcloud.getRadius() - 0.5F);
         pLevel.playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.BOTTLE_FILL_DRAGONBREATH, SoundSource.NEUTRAL, 1.0F, 1.0F);
         pLevel.gameEvent(pPlayer, GameEvent.FLUID_PICKUP, pPlayer.position());
         return InteractionResultHolder.sidedSuccess(this.turnBottleIntoItem(itemstack, pPlayer, new ItemStack(Items.DRAGON_BREATH)), pLevel.isClientSide());
      }

      BlockHitResult result = getPlayerPOVHitResult(pLevel, pPlayer, Fluid.SOURCE_ONLY);
      if (result.getType() == Type.BLOCK) {
         BlockPos blockpos = result.getBlockPos();
         if (!pLevel.mayInteract(pPlayer, blockpos)) {
            return InteractionResultHolder.pass(itemstack);
         }

         if (pLevel.getFluidState(blockpos).is(FluidTags.WATER)) {
            pLevel.playSound(pPlayer, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
            pLevel.gameEvent(pPlayer, GameEvent.FLUID_PICKUP, blockpos);
            return InteractionResultHolder.sidedSuccess(
               this.turnBottleIntoItem(itemstack, pPlayer, ((Item)TensuraConsumableItems.WATER_MAGIC_BOTTLE.get()).getDefaultInstance()), pLevel.isClientSide()
            );
         }
      }

      return InteractionResultHolder.pass(itemstack);
   }
}
