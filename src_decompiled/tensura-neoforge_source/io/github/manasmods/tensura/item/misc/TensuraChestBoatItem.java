package io.github.manasmods.tensura.item.misc;

import io.github.manasmods.tensura.entity.template.TensuraBoatEntity;
import io.github.manasmods.tensura.entity.template.TensuraChestBoatEntity;
import io.github.manasmods.tensura.item.dispensor.TensuraBoatDispenseItemBehavior;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class TensuraChestBoatItem extends Item {
   private static final Predicate<Entity> ENTITY_PREDICATE = EntitySelector.NO_SPECTATORS.and(Entity::canBeCollidedWith);
   private final TensuraBoatEntity.Type type;

   public TensuraChestBoatItem(Properties properties, TensuraBoatEntity.Type typeIn) {
      super(properties);
      this.type = typeIn;
      DispenserBlock.registerBehavior(this, new TensuraBoatDispenseItemBehavior(TensuraBoatEntity.Type.PALM, true));
   }

   public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
      ItemStack itemstack = playerIn.getItemInHand(handIn);
      HitResult rayTraceResult = getPlayerPOVHitResult(worldIn, playerIn, Fluid.ANY);
      if (rayTraceResult.getType() == Type.MISS) {
         return InteractionResultHolder.pass(itemstack);
      }

      Vec3 vec3d = playerIn.getEyePosition(1.0F);
      double d0 = 5.0;
      List<Entity> list = worldIn.getEntities(playerIn, playerIn.getBoundingBox().expandTowards(vec3d.scale(d0)).inflate(1.0), ENTITY_PREDICATE);
      if (!list.isEmpty()) {
         Vec3 vec3d1 = playerIn.getEyePosition(1.0F);

         for (Entity entity : list) {
            AABB axisAlignedBb = entity.getBoundingBox().inflate(entity.getPickRadius());
            if (axisAlignedBb.contains(vec3d1)) {
               return InteractionResultHolder.pass(itemstack);
            }
         }
      }

      if (rayTraceResult.getType() == Type.BLOCK) {
         TensuraChestBoatEntity boatEntity = new TensuraChestBoatEntity(
            worldIn, rayTraceResult.getLocation().x, rayTraceResult.getLocation().y, rayTraceResult.getLocation().z
         );
         boatEntity.setBoatType(this.type);
         boatEntity.setYRot(playerIn.getYRot());
         if (!worldIn.noCollision(boatEntity, boatEntity.getBoundingBox().inflate(-0.1))) {
            return InteractionResultHolder.fail(itemstack);
         }

         if (!worldIn.isClientSide) {
            worldIn.addFreshEntity(boatEntity);
         }

         if (!playerIn.hasInfiniteMaterials()) {
            itemstack.shrink(1);
         }

         playerIn.awardStat(Stats.ITEM_USED.get(this));
         return InteractionResultHolder.success(itemstack);
      } else {
         return InteractionResultHolder.pass(itemstack);
      }
   }
}
