package io.github.manasmods.tensura.block;

import com.mojang.serialization.MapCodec;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.block.entity.CharybdisCoreBlockEntity;
import io.github.manasmods.tensura.entity.magic.misc.PrimedCharybdisCoreEntity;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.item.misc.TensuraDataComponents;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import java.util.List;
import java.util.Objects;
import java.util.function.ToIntFunction;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SculkSensorPhase;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CharybdisCoreBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
   public static final MapCodec<CharybdisCoreBlock> CODEC = simpleCodec(CharybdisCoreBlock::new);
   public static final DirectionProperty FACING = BlockStateProperties.FACING;
   public static final EnumProperty<SculkSensorPhase> MODE = BlockStateProperties.SCULK_SENSOR_PHASE;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   private static final VoxelShape SHAPE_NORTH = Shapes.or(Block.box(3.0, 3.0, 11.0, 13.0, 13.0, 16.0), Block.box(4.0, 4.0, 10.0, 12.0, 12.0, 11.0));
   private static final VoxelShape SHAPE_SOUTH = Shapes.or(Block.box(3.0, 3.0, 0.0, 13.0, 13.0, 5.0), Block.box(4.0, 4.0, 5.0, 12.0, 12.0, 6.0));
   private static final VoxelShape SHAPE_WEST = Shapes.or(Block.box(11.0, 3.0, 3.0, 16.0, 13.0, 13.0), Block.box(10.0, 4.0, 4.0, 11.0, 12.0, 12.0));
   private static final VoxelShape SHAPE_EAST = Shapes.or(Block.box(0.0, 3.0, 3.0, 5.0, 13.0, 13.0), Block.box(5.0, 4.0, 4.0, 6.0, 12.0, 12.0));
   private static final VoxelShape SHAPE_DOWN = Shapes.or(Block.box(3.0, 11.0, 3.0, 13.0, 16.0, 13.0), Block.box(4.0, 10.0, 4.0, 12.0, 11.0, 12.0));
   private static final VoxelShape SHAPE_UP = Shapes.or(Block.box(3.0, 0.0, 3.0, 13.0, 5.0, 13.0), Block.box(4.0, 5.0, 4.0, 12.0, 6.0, 12.0));

   public CharybdisCoreBlock(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.UP))
               .setValue(MODE, SculkSensorPhase.INACTIVE))
            .setValue(WATERLOGGED, Boolean.FALSE)
      );
   }

   public CharybdisCoreBlock() {
      this(
         Properties.of()
            .mapColor(MapColor.COLOR_RED)
            .strength(0.5F)
            .lightLevel(getLightEmission())
            .sound(SoundType.SHROOMLIGHT)
            .noOcclusion()
            .pushReaction(PushReaction.BLOCK)
      );
   }

   public void appendHoverText(ItemStack pStack, TooltipContext tooltipContext, List<Component> pTooltip, TooltipFlag pFlag) {
      super.appendHoverText(pStack, tooltipContext, pTooltip, pFlag);
      BlockItemStateProperties stateTag = (BlockItemStateProperties)pStack.get(DataComponents.BLOCK_STATE);
      if (stateTag != null) {
         SculkSensorPhase mode = (SculkSensorPhase)stateTag.get(MODE);
         if (mode != null) {
            if (mode.equals(SculkSensorPhase.INACTIVE)) {
               pTooltip.add(Component.translatable("tooltip.tensura.charybdis_core.inactive").withStyle(ChatFormatting.RED));
               if (pStack.has((DataComponentType)TensuraDataComponents.EP_DURABILITY.get())) {
                  double EP = (Double)pStack.get((DataComponentType)TensuraDataComponents.EP_DURABILITY.get());
                  if (EP > 0.0) {
                     pTooltip.add(Component.translatable("tensura.main_menu.existence_points", new Object[]{EP}).withStyle(ChatFormatting.GOLD));
                  }
               }
            } else if (mode.equals(SculkSensorPhase.ACTIVE)) {
               pTooltip.add(Component.translatable("tooltip.tensura.charybdis_core.active").withStyle(ChatFormatting.GOLD));
            } else if (mode.equals(SculkSensorPhase.COOLDOWN)) {
               pTooltip.add(Component.translatable("tooltip.tensura.charybdis_core.inert").withStyle(ChatFormatting.DARK_GREEN));
            }
         }
      }
   }

   @NotNull
   public MapCodec<CharybdisCoreBlock> codec() {
      return CODEC;
   }

   @NotNull
   public RenderShape getRenderShape(BlockState pState) {
      return RenderShape.MODEL;
   }

   public static ToIntFunction<BlockState> getLightEmission() {
      return state -> {
         return switch ((SculkSensorPhase)state.getValue(MODE)) {
            case COOLDOWN -> 8;
            case INACTIVE -> 2;
            default -> 12;
         };
      };
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
      pBuilder.add(new Property[]{FACING}).add(new Property[]{MODE}).add(new Property[]{WATERLOGGED});
   }

   @NotNull
   public BlockState rotate(BlockState pState, Rotation pRot) {
      return (BlockState)pState.setValue(FACING, pRot.rotate((Direction)pState.getValue(FACING)));
   }

   @NotNull
   public BlockState mirror(BlockState pState, Mirror pMirror) {
      return pState.rotate(pMirror.getRotation((Direction)pState.getValue(FACING)));
   }

   public BlockState getStateForPlacement(BlockPlaceContext pContext) {
      Direction direction = pContext.getClickedFace();
      FluidState fluidstate = pContext.getLevel().getFluidState(pContext.getClickedPos());
      return (BlockState)((BlockState)super.getStateForPlacement(pContext).setValue(FACING, direction))
         .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
   }

   @NotNull
   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return switch ((Direction)state.getValue(FACING)) {
         case NORTH -> SHAPE_NORTH;
         case SOUTH -> SHAPE_SOUTH;
         case WEST -> SHAPE_WEST;
         case EAST -> SHAPE_EAST;
         case DOWN -> SHAPE_DOWN;
         case UP -> SHAPE_UP;
         default -> throw new MatchException(null, null);
      };
   }

   public BlockState updateShape(
      BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos
   ) {
      if ((Boolean)pState.getValue(WATERLOGGED)) {
         pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
      }

      return super.updateShape(pState, pDirection, pNeighborState, pLevel, pCurrentPos, pNeighborPos);
   }

   public FluidState getFluidState(BlockState pState) {
      return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return new CharybdisCoreBlockEntity(pPos, pState);
   }

   @NotNull
   protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult blockHitResult) {
      if (pLevel.isClientSide()) {
         return InteractionResult.PASS;
      }

      if (pPlayer.isShiftKeyDown() && pLevel instanceof ServerLevel level) {
         for (ItemStack stack : Block.getDrops(pState, level, pPos, pLevel.getBlockEntity(pPos))) {
            pPlayer.addItem(stack);
         }

         pLevel.destroyBlock(pPos, false);
         pPlayer.swing(InteractionHand.MAIN_HAND, true);
         return InteractionResult.sidedSuccess(pLevel.isClientSide());
      } else {
         switch ((SculkSensorPhase)pState.getValue(MODE)) {
            case ACTIVE:
               PrimedCharybdisCoreEntity core = new PrimedCharybdisCoreEntity(pLevel, pPos.getX() + 0.5, pPos.getY(), pPos.getZ() + 0.5);
               pLevel.addFreshEntity(core);
               TensuraParticleHelper.addServerParticlesAroundSelf(core, ParticleTypes.SCULK_SOUL);
               TensuraParticleHelper.addServerParticlesAroundSelf(core, (ParticleOptions)TensuraParticleTypes.SOUL.get());
               pLevel.playSound(null, core.getX(), core.getY(), core.getZ(), SoundEvents.WITHER_SPAWN, SoundSource.BLOCKS, 1.0F, 1.0F);
               pLevel.removeBlock(pPos, false);
               pPlayer.swing(InteractionHand.MAIN_HAND, true);
               return InteractionResult.sidedSuccess(pLevel.isClientSide());
            case COOLDOWN:
               List<ManasSkill> list = ObjectSelectionHelper.CONFIG
                  .CharybdisCore
                  .charybdisCoreSkills
                  .stream()
                  .map(skillx -> (ManasSkill)SkillAPI.getSkillRegistry().get(ResourceLocation.tryParse(skillx)))
                  .filter(Objects::nonNull)
                  .toList();
               if (!list.isEmpty()) {
                  boolean success = false;

                  for (ManasSkill skill : list) {
                     if (SkillHelper.learnSkill(pPlayer, skill)) {
                        success = true;
                     }
                  }

                  if (!success) {
                     pPlayer.swing(InteractionHand.MAIN_HAND, true);
                     pLevel.playSound(
                        null, pPos.getX(), pPos.getY(), pPos.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), SoundSource.BLOCKS, 1.0F, 1.0F
                     );
                     return InteractionResult.PASS;
                  }
               }

               pLevel.destroyBlock(pPos, false);
               pLevel.playSound(null, pPos.getX(), pPos.getY(), pPos.getZ(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
               TensuraParticleHelper.addServerParticlesAroundPos(pLevel.getRandom(), pLevel, Vec3.atCenterOf(pPos), ParticleTypes.SCULK_SOUL, 1.0);
               TensuraParticleHelper.addServerParticlesAroundPos(pLevel.getRandom(), pLevel, Vec3.atCenterOf(pPos), ParticleTypes.SOUL, 1.0);
               TensuraParticleHelper.addServerParticlesAroundPos(
                  pLevel.getRandom(), pLevel, Vec3.atCenterOf(pPos), (ParticleOptions)TensuraParticleTypes.SOUL.get(), 1.0
               );
               pPlayer.swing(InteractionHand.MAIN_HAND, true);
               return InteractionResult.sidedSuccess(pLevel.isClientSide());
            default:
               return InteractionResult.PASS;
         }
      }
   }
}
