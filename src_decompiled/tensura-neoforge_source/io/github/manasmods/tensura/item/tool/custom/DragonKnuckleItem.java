package io.github.manasmods.tensura.item.tool.custom;

import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.item.TensuraToolTiers;
import io.github.manasmods.tensura.item.tool.MultitoolItem;
import io.github.manasmods.tensura.item.weapon.TensuraSwordItem;
import io.github.manasmods.tensura.registry.item.misc.TensuraCreativeTabs;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import org.jetbrains.annotations.NotNull;

public class DragonKnuckleItem extends MultitoolItem {
   public static final ResourceLocation MULTIPLIER = ResourceLocation.fromNamespaceAndPath("tensura", "damage_multiplier");

   public DragonKnuckleItem() {
      super(
         TensuraToolTiers.HIGH_MAGISTEEL,
         TensuraBlockTags.MINEABLE_WITH_MULTITOOL,
         new Properties().durability(4000).arch$tab(TensuraCreativeTabs.GEARS).fireResistant(),
         createAttributes(-0.9F, -3.0F, -1.0)
      );
   }

   public static ItemAttributeModifiers createAttributes(double damage, float speed, double ratio) {
      return ItemAttributeModifiers.builder()
         .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(MULTIPLIER, damage, Operation.ADD_MULTIPLIED_TOTAL), EquipmentSlotGroup.MAINHAND)
         .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, speed, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
         .add(
            Attributes.SWEEPING_DAMAGE_RATIO,
            new AttributeModifier(TensuraSwordItem.BASE_SWEEP_RATIO_ID, ratio, Operation.ADD_VALUE),
            EquipmentSlotGroup.MAINHAND
         )
         .build();
   }

   public float getDestroySpeed(ItemStack pStack, BlockState pState) {
      return 20.0F;
   }

   @NotNull
   public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
      if (!(entity instanceof Shearable target && target.readyForShearing())) {
         return InteractionResult.PASS;
      } else {
         if (entity.level().isClientSide) {
            return InteractionResult.SUCCESS;
         }

         target.shear(SoundSource.PLAYERS);
         entity.gameEvent(GameEvent.SHEAR, player);
         stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
         return InteractionResult.SUCCESS;
      }
   }

   @NotNull
   public InteractionResult useOn(UseOnContext useOnContext) {
      Level level = useOnContext.getLevel();
      BlockPos blockPos = useOnContext.getClickedPos();
      BlockState blockState = level.getBlockState(blockPos);
      if (blockState.getBlock() instanceof GrowingPlantHeadBlock growingPlantHeadBlock && !growingPlantHeadBlock.isMaxAge(blockState)) {
         Player player = useOnContext.getPlayer();
         ItemStack itemStack = useOnContext.getItemInHand();
         if (player instanceof ServerPlayer) {
            CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)player, blockPos, itemStack);
         }

         level.playSound(player, blockPos, SoundEvents.GROWING_PLANT_CROP, SoundSource.BLOCKS, 1.0F, 1.0F);
         BlockState blockState2 = growingPlantHeadBlock.getMaxAgeState(blockState);
         level.setBlockAndUpdate(blockPos, blockState2);
         level.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, Context.of(useOnContext.getPlayer(), blockState2));
         if (player != null) {
            itemStack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(useOnContext.getHand()));
         }

         return InteractionResult.sidedSuccess(level.isClientSide);
      } else {
         return super.useOn(useOnContext);
      }
   }
}
