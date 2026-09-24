package io.github.manasmods.tensura.util.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.tensura.ability.SkillClientUtils;
import io.github.manasmods.tensura.config.ability.AbilityConfig;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.RenderStateShard.LineStateShard;
import net.minecraft.client.renderer.RenderType.CompositeState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class XrayHelper {
   private static final Map<SkillClientUtils.XrayType, List<XrayHelper.Edge>> XRAY_CACHE = new HashMap<>();
   private static int lastCacheTick = -1;
   public static final RenderType XRAY_LINES = RenderType.create(
      "xray_lines",
      DefaultVertexFormat.POSITION_COLOR_NORMAL,
      Mode.LINES,
      256,
      false,
      false,
      CompositeState.builder()
         .setShaderState(RenderStateShard.RENDERTYPE_LINES_SHADER)
         .setLineState(new LineStateShard(OptionalDouble.of(3.0)))
         .setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
         .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
         .setOutputState(RenderStateShard.ITEM_ENTITY_TARGET)
         .setCullState(RenderStateShard.NO_CULL)
         .setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
         .setWriteMaskState(RenderStateShard.COLOR_WRITE)
         .createCompositeState(false)
   );

   public static void onXrayRendering(PoseStack poseStack) {
      Minecraft minecraft = Minecraft.getInstance();
      Player player = minecraft.player;
      if (player != null && minecraft.level != null) {
         if (((AbilityConfig)ConfigRegistry.getConfig(AbilityConfig.class)).Misc.blockXrayVision) {
            if (player.tickCount != lastCacheTick && player.tickCount % 20 == 0) {
               rebuildCache(player);
               lastCacheTick = player.tickCount;
            }

            if (!XRAY_CACHE.isEmpty()) {
               Camera camera = minecraft.gameRenderer.getMainCamera();
               Vec3 camPos = camera.getPosition();
               poseStack.pushPose();
               poseStack.translate(-camPos.x, -camPos.y, -camPos.z);
               BufferSource buffer = minecraft.renderBuffers().bufferSource();
               RenderSystem.disableDepthTest();
               VertexConsumer consumer = buffer.getBuffer(XRAY_LINES);
               Pose pose = poseStack.last();

               for (Entry<SkillClientUtils.XrayType, List<XrayHelper.Edge>> entry : XRAY_CACHE.entrySet()) {
                  SkillClientUtils.XrayType type = entry.getKey();
                  float r = type.red();
                  float g = type.green();
                  float b = type.blue();
                  float a = type.alpha();

                  for (XrayHelper.Edge edge : entry.getValue()) {
                     float nx = edge.x1 != edge.x2 ? 1.0F : 0.0F;
                     float ny = edge.y1 != edge.y2 ? 1.0F : 0.0F;
                     float nz = edge.z1 != edge.z2 ? 1.0F : 0.0F;
                     consumer.addVertex(pose, edge.x1, edge.y1, edge.z1).setColor(r, g, b, a).setNormal(pose, nx, ny, nz);
                     consumer.addVertex(pose, edge.x2, edge.y2, edge.z2).setColor(r, g, b, a).setNormal(pose, nx, ny, nz);
                  }
               }

               buffer.endBatch(XRAY_LINES);
               RenderSystem.enableDepthTest();
               poseStack.popPose();
            }
         }
      }
   }

   private static void rebuildCache(Player player) {
      XRAY_CACHE.clear();
      if (!SkillClientUtils.XRAY_TYPES.isEmpty()) {
         List<SkillClientUtils.XrayType> activeTypes = SkillClientUtils.XRAY_TYPES
            .stream()
            .map(function -> function.apply(player))
            .filter(Objects::nonNull)
            .toList();
         if (!activeTypes.isEmpty()) {
            int maxRadius = activeTypes.stream().mapToInt(SkillClientUtils.XrayType::radius).max().orElse(0);
            BlockPos origin = player.blockPosition();
            Map<BlockPos, SkillClientUtils.XrayType> chosenType = new HashMap<>();
            Map<BlockPos, Block> chosenBlock = new HashMap<>();

            for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-maxRadius, -maxRadius, -maxRadius), origin.offset(maxRadius, maxRadius, maxRadius))) {
               BlockState state = player.level().getBlockState(pos);
               Block block = state.getBlock();

               for (SkillClientUtils.XrayType type : activeTypes) {
                  if (pos.closerThan(origin, type.radius()) && type.predicate().test(state)) {
                     SkillClientUtils.XrayType current = chosenType.get(pos);
                     if (current == null || type.radius() < current.radius()) {
                        BlockPos imm = pos.immutable();
                        chosenType.put(imm, type);
                        chosenBlock.put(imm, block);
                     }
                  }
               }
            }

            Map<SkillClientUtils.XrayType, Map<Block, Set<BlockPos>>> groups = new HashMap<>();

            for (Entry<BlockPos, SkillClientUtils.XrayType> entry : chosenType.entrySet()) {
               BlockPos pos = entry.getKey();
               Block block = chosenBlock.get(pos);
               groups.computeIfAbsent(entry.getValue(), type -> new HashMap<>()).computeIfAbsent(block, b -> new HashSet<>()).add(pos);
            }

            for (Entry<SkillClientUtils.XrayType, Map<Block, Set<BlockPos>>> entry : groups.entrySet()) {
               Set<XrayHelper.Edge> merged = new HashSet<>();

               for (Set<BlockPos> sameBlockPositions : entry.getValue().values()) {
                  merged.addAll(buildMergedOutlineEdges(sameBlockPositions));
               }

               XRAY_CACHE.put(entry.getKey(), new ArrayList<>(merged));
            }
         }
      }
   }

   private static List<XrayHelper.Edge> buildMergedOutlineEdges(Set<BlockPos> blocks) {
      Set<XrayHelper.EdgeWithNormal> toggled = new HashSet<>();

      for (BlockPos pos : blocks) {
         for (Direction direction : Direction.values()) {
            if (!blocks.contains(pos.relative(direction))) {
               addFaceEdgesToggled(toggled, pos, direction);
            }
         }
      }

      Set<XrayHelper.Edge> unique = new HashSet<>();

      for (XrayHelper.EdgeWithNormal edge : toggled) {
         unique.add(edge.edge);
      }

      return new ArrayList<>(unique);
   }

   private static void toggle(Set<XrayHelper.EdgeWithNormal> set, XrayHelper.EdgeWithNormal e) {
      if (!set.add(e)) {
         set.remove(e);
      }
   }

   private static void addFaceEdgesToggled(Set<XrayHelper.EdgeWithNormal> toggled, BlockPos pos, Direction faceNormal) {
      int x = pos.getX();
      int y = pos.getY();
      int z = pos.getZ();
      switch (faceNormal) {
         case UP: {
            int yy = y + 1;
            toggle(toggled, new XrayHelper.EdgeWithNormal(faceNormal, XrayHelper.Edge.of(x, yy, z, x + 1, yy, z)));
            toggle(toggled, new XrayHelper.EdgeWithNormal(faceNormal, XrayHelper.Edge.of(x + 1, yy, z, x + 1, yy, z + 1)));
            toggle(toggled, new XrayHelper.EdgeWithNormal(faceNormal, XrayHelper.Edge.of(x + 1, yy, z + 1, x, yy, z + 1)));
            toggle(toggled, new XrayHelper.EdgeWithNormal(faceNormal, XrayHelper.Edge.of(x, yy, z + 1, x, yy, z)));
            break;
         }
         case DOWN: {
            int yy = y;
            toggle(toggled, new XrayHelper.EdgeWithNormal(faceNormal, XrayHelper.Edge.of(x, yy, z, x + 1, yy, z)));
            toggle(toggled, new XrayHelper.EdgeWithNormal(faceNormal, XrayHelper.Edge.of(x + 1, yy, z, x + 1, yy, z + 1)));
            toggle(toggled, new XrayHelper.EdgeWithNormal(faceNormal, XrayHelper.Edge.of(x + 1, yy, z + 1, x, yy, z + 1)));
            toggle(toggled, new XrayHelper.EdgeWithNormal(faceNormal, XrayHelper.Edge.of(x, yy, z + 1, x, yy, z)));
            break;
         }
         case EAST: {
            int xx = x + 1;
            toggle(toggled, new XrayHelper.EdgeWithNormal(faceNormal, XrayHelper.Edge.of(xx, y, z, xx, y + 1, z)));
            toggle(toggled, new XrayHelper.EdgeWithNormal(faceNormal, XrayHelper.Edge.of(xx, y + 1, z, xx, y + 1, z + 1)));
            toggle(toggled, new XrayHelper.EdgeWithNormal(faceNormal, XrayHelper.Edge.of(xx, y + 1, z + 1, xx, y, z + 1)));
            toggle(toggled, new XrayHelper.EdgeWithNormal(faceNormal, XrayHelper.Edge.of(xx, y, z + 1, xx, y, z)));
            break;
         }
         case WEST: {
            int xx = x;
            toggle(toggled, new XrayHelper.EdgeWithNormal(faceNormal, XrayHelper.Edge.of(xx, y, z, xx, y + 1, z)));
            toggle(toggled, new XrayHelper.EdgeWithNormal(faceNormal, XrayHelper.Edge.of(xx, y + 1, z, xx, y + 1, z + 1)));
            toggle(toggled, new XrayHelper.EdgeWithNormal(faceNormal, XrayHelper.Edge.of(xx, y + 1, z + 1, xx, y, z + 1)));
            toggle(toggled, new XrayHelper.EdgeWithNormal(faceNormal, XrayHelper.Edge.of(xx, y, z + 1, xx, y, z)));
            break;
         }
         case SOUTH: {
            int zz = z + 1;
            toggle(toggled, new XrayHelper.EdgeWithNormal(faceNormal, XrayHelper.Edge.of(x, y, zz, x + 1, y, zz)));
            toggle(toggled, new XrayHelper.EdgeWithNormal(faceNormal, XrayHelper.Edge.of(x + 1, y, zz, x + 1, y + 1, zz)));
            toggle(toggled, new XrayHelper.EdgeWithNormal(faceNormal, XrayHelper.Edge.of(x + 1, y + 1, zz, x, y + 1, zz)));
            toggle(toggled, new XrayHelper.EdgeWithNormal(faceNormal, XrayHelper.Edge.of(x, y + 1, zz, x, y, zz)));
            break;
         }
         case NORTH: {
            int zz = z;
            toggle(toggled, new XrayHelper.EdgeWithNormal(faceNormal, XrayHelper.Edge.of(x, y, zz, x + 1, y, zz)));
            toggle(toggled, new XrayHelper.EdgeWithNormal(faceNormal, XrayHelper.Edge.of(x + 1, y, zz, x + 1, y + 1, zz)));
            toggle(toggled, new XrayHelper.EdgeWithNormal(faceNormal, XrayHelper.Edge.of(x + 1, y + 1, zz, x, y + 1, zz)));
            toggle(toggled, new XrayHelper.EdgeWithNormal(faceNormal, XrayHelper.Edge.of(x, y + 1, zz, x, y, zz)));
         }
      }
   }

   private record Edge(int x1, int y1, int z1, int x2, int y2, int z2) {
      static XrayHelper.Edge of(int ax, int ay, int az, int bx, int by, int bz) {
         return compare(ax, ay, az, bx, by, bz) <= 0 ? new XrayHelper.Edge(ax, ay, az, bx, by, bz) : new XrayHelper.Edge(bx, by, bz, ax, ay, az);
      }

      private static int compare(int ax, int ay, int az, int bx, int by, int bz) {
         if (ax != bx) {
            return Integer.compare(ax, bx);
         } else {
            return ay != by ? Integer.compare(ay, by) : Integer.compare(az, bz);
         }
      }
   }

   private record EdgeWithNormal(Direction normal, XrayHelper.Edge edge) {
   }
}
