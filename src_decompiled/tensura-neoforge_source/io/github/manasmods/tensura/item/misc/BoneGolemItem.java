package io.github.manasmods.tensura.item.misc;

import io.github.manasmods.tensura.entity.human.golem.BoneGolemEntity;
import io.github.manasmods.tensura.entity.variant.BoneGolemVariant;
import io.github.manasmods.tensura.registry.entity.HumanEntityTypes;
import io.github.manasmods.tensura.registry.item.misc.TensuraCreativeTabs;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import lombok.Generated;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BoneGolemItem extends Item {
   private final BoneGolemVariant variant;

   public BoneGolemItem(BoneGolemVariant variant, Properties properties) {
      super(properties);
      this.variant = variant;
   }

   public BoneGolemItem(BoneGolemVariant variant) {
      this(variant, new Properties().arch$tab(TensuraCreativeTabs.MISCELLANEOUS).fireResistant().rarity(Rarity.EPIC));
   }

   @NotNull
   public InteractionResult useOn(UseOnContext useOnContext) {
      Direction face = useOnContext.getClickedFace();
      Level level = useOnContext.getLevel();
      if (level.isClientSide()) {
         return InteractionResult.SUCCESS;
      }

      BlockPos pos = useOnContext.getClickedPos();
      Player player = useOnContext.getPlayer();
      EntityDimensions dimensions = ((EntityType)HumanEntityTypes.BONE_GOLEM.get()).getDimensions();
      Vec3 location = useOnContext.getClickLocation();
      double planeLocal = getFacePlane(level, pos, face, player);
      double x = location.x;
      double y = pos.getY() + (face == Direction.UP ? planeLocal : (face == Direction.DOWN ? dimensions.height() * -1.0F : 0.0));
      double z = location.z;
      double halfWidth = dimensions.width() / 2.0;
      if (face.getAxis() == Axis.X) {
         x = pos.getX() + planeLocal + face.getStepX() * (halfWidth + 0.001);
      } else if (face.getAxis() == Axis.Z) {
         z = pos.getZ() + planeLocal + face.getStepZ() * (halfWidth + 0.001);
      }

      Vec3 vec3 = new Vec3(x, y, z);
      ItemStack itemStack = useOnContext.getItemInHand();
      AABB aABB = dimensions.makeBoundingBox(vec3.x(), vec3.y(), vec3.z());
      if (level.noCollision(null, aABB) && level.getEntities(null, aABB).isEmpty()) {
         BoneGolemEntity golem = new BoneGolemEntity((EntityType<? extends BoneGolemEntity>)HumanEntityTypes.BONE_GOLEM.get(), level);
         golem.setVariant(this.getVariant());
         golem.setPos(vec3);
         Component component = (Component)itemStack.get(DataComponents.CUSTOM_NAME);
         if (component != null) {
            golem.setCustomName(component);
         }

         if (player != null && player.isSecondaryUseActive()) {
            golem.setNoGravity(true);
         }

         float rot = Mth.floor((Mth.wrapDegrees(useOnContext.getRotation() - 180.0F) + 22.5F) / 45.0F) * 45.0F;
         golem.setYRot(rot);
         golem.yBodyRot = rot;
         golem.yBodyRotO = rot;
         golem.yHeadRot = rot;
         golem.yHeadRotO = rot;
         level.addFreshEntity(golem);
         double ep = this.getVariant().getEP();
         EnergyHelper.setBaseMaxEP(golem, ep);
         IExistence existence = TensuraStorages.getExistenceFrom(golem);
         existence.setSpiritualHealth(60.0);
         existence.setMagicule(ep / 2.0);
         existence.setAura(ep / 2.0);
         existence.markDirty();
         golem.getAttributes().assignBaseValues(this.getVariant().getAttributes());
         golem.setHealth(golem.getMaxHealth());
         golem.gameEvent(GameEvent.ENTITY_PLACE, useOnContext.getPlayer());
         level.playSound(
            null,
            golem.getX(),
            golem.getY(),
            golem.getZ(),
            this.getVariant().getBlock().defaultBlockState().getSoundType().getPlaceSound(),
            SoundSource.BLOCKS,
            0.75F,
            0.8F
         );
         itemStack.shrink(1);
         return InteractionResult.sidedSuccess(level.isClientSide);
      } else {
         if (player != null) {
            player.displayClientMessage(Component.translatable("tensura.message.position.occupied").withStyle(ChatFormatting.RED), true);
         }

         return InteractionResult.FAIL;
      }
   }

   private static double getFacePlane(Level level, BlockPos pos, Direction face, @Nullable Player player) {
      BlockState state = level.getBlockState(pos);
      VoxelShape shape = state.getCollisionShape(level, pos, player != null ? CollisionContext.of(player) : CollisionContext.empty());
      if (shape.isEmpty()) {
         return switch (face) {
            case WEST, DOWN, NORTH -> 0.0;
            case EAST, UP, SOUTH -> 1.0;
            default -> throw new MatchException(null, null);
         };
      } else {
         double max = switch (face) {
            case WEST, DOWN, NORTH -> Double.POSITIVE_INFINITY;
            case EAST, UP, SOUTH -> Double.NEGATIVE_INFINITY;
            default -> throw new MatchException(null, null);
         };

         for (AABB bb : shape.toAabbs()) {
            double v = switch (face) {
               case WEST -> bb.minX;
               case DOWN -> bb.minY;
               case NORTH -> bb.minZ;
               case EAST -> bb.maxX;
               case UP -> bb.maxY;
               case SOUTH -> bb.maxZ;
               default -> throw new MatchException(null, null);
            };
            if (face != Direction.WEST && face != Direction.DOWN && face != Direction.NORTH) {
               max = Math.max(max, v);
            } else {
               max = Math.min(max, v);
            }
         }

         return Mth.clamp(max, 0.0, 1.0);
      }
   }

   @Generated
   public BoneGolemVariant getVariant() {
      return this.variant;
   }
}
