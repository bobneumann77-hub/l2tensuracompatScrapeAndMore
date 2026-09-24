package dev.xkmc.l2hostility.events;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Axis;
import dev.xkmc.l2core.capability.attachment.GeneralCapabilityHolder;
import dev.xkmc.l2core.events.ClientEffectRenderEvents;
import dev.xkmc.l2hostility.compat.curios.CurioCompat;
import dev.xkmc.l2hostility.content.capability.chunk.ChunkCapHolder;
import dev.xkmc.l2hostility.content.capability.chunk.ChunkClearRenderer;
import dev.xkmc.l2hostility.content.capability.chunk.ChunkDifficulty;
import dev.xkmc.l2hostility.content.capability.mob.MasterData;
import dev.xkmc.l2hostility.content.capability.mob.MobTraitCap;
import dev.xkmc.l2hostility.content.item.traits.EnchantmentDisabler;
import dev.xkmc.l2hostility.init.L2Hostility;
import dev.xkmc.l2hostility.init.data.LHConfig;
import dev.xkmc.l2hostility.init.data.LHTagGen;
import dev.xkmc.l2hostility.init.registrate.LHItems;
import dev.xkmc.l2hostility.init.registrate.LHMiscs;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Font.DisplayMode;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderNameTagEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent.Post;
import net.neoforged.neoforge.client.event.ClientTickEvent.Pre;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.joml.Matrix4f;

@EventBusSubscriber(value = Dist.CLIENT, modid = "l2hostility", bus = Bus.GAME)
public class ClientEvents {
   private static boolean renderChunk = false;
   public static final List<Mob> MASTERS = new ArrayList<>();

   @SubscribeEvent
   public static void addTooltip(ItemTooltipEvent event) {
      Level level = event.getContext().level();
      if (level != null) {
         EnchantmentDisabler.modifyTooltip(event.getItemStack(), event.getToolTip(), level, event.getContext(), event.getFlags());
      }
   }

   @SubscribeEvent(priority = EventPriority.LOW)
   public static void renderNamePlate(RenderNameTagEvent event) {
      if (event.getEntity() instanceof LivingEntity le) {
         boolean var14 = le.getType().is(LHTagGen.HIDE_LEVEL);
         boolean hide_trait = le.getType().is(LHTagGen.HIDE_TRAITS);
         if (!var14 || !hide_trait) {
            boolean needHover = le.isInvisible() || (Boolean)LHConfig.CLIENT.showOnlyWhenHovered.get();
            if (!needHover || Minecraft.getInstance().crosshairPickEntity == le) {
               Optional<MobTraitCap> opt = ((GeneralCapabilityHolder)LHMiscs.MOB.type()).getExisting(le);
               LocalPlayer player = Minecraft.getInstance().player;
               if (!opt.isEmpty() && player != null) {
                  MobTraitCap cap = opt.get();
                  List<Component> list = cap.getTitle(
                     !var14 && (Boolean)LHConfig.CLIENT.showLevelOverHead.get(), !hide_trait && (Boolean)LHConfig.CLIENT.showTraitOverHead.get()
                  );
                  int offset = list.size();
                  float off = (float)((Double)LHConfig.CLIENT.overHeadRenderOffset.get()).doubleValue();
                  DisplayMode mode = player.hasLineOfSight(event.getEntity()) ? DisplayMode.SEE_THROUGH : DisplayMode.NORMAL;

                  for (Component e : list) {
                     renderNameTag(le, event, e, event.getPoseStack(), (offset + off) * 0.2F, mode);
                     offset--;
                  }
               }
            }
         }
      }
   }

   protected static void renderNameTag(LivingEntity le, RenderNameTagEvent event, Component text, PoseStack pose, float offset, DisplayMode mode) {
      EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
      double d0 = dispatcher.distanceToSqr(le);
      int max = (Integer)LHConfig.CLIENT.overHeadRenderDistance.get();
      if (!(d0 > max * max)) {
         int light = LHConfig.CLIENT.overHeadRenderFullBright.get() ? 15728880 : event.getPackedLight();
         Vec3 vec3 = le.getAttachments().getNullable(EntityAttachment.NAME_TAG, 0, le.getViewYRot(event.getPartialTick()));
         if (vec3 == null) {
            vec3 = new Vec3(0.0, le.getBoundingBox().getYsize(), 0.0);
         }

         pose.pushPose();
         pose.translate(vec3.x, vec3.y + offset, vec3.z);
         pose.mulPose(dispatcher.cameraOrientation());
         pose.scale(0.025F, -0.025F, 0.025F);
         Matrix4f matrix4f = pose.last().pose();
         Font font = event.getEntityRenderer().getFont();
         float f2 = -font.width(text) / 2;
         float f1 = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
         int j = (int)(f1 * 255.0F) << 24;
         font.drawInBatch(text, f2, 0.0F, -1, false, matrix4f, event.getMultiBufferSource(), mode, j, light);
         pose.popPose();
      }
   }

   @SubscribeEvent
   public static void onClientTick(Pre event) {
      MASTERS.clear();
   }

   @SubscribeEvent
   public static void onClientTick(Post event) {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player != null && player.tickCount % 2 == 0) {
         renderChunk = CurioCompat.hasItemInCurioOrSlot(player, (Item)LHItems.DETECTOR_GLASSES.get())
            && CurioCompat.hasItemInCurioOrSlot(player, (Item)LHItems.DETECTOR.get());
      }
   }

   @SubscribeEvent
   public static void onLevelRenderLast(RenderLevelStageEvent event) {
      if (event.getStage() == Stage.AFTER_TRIPWIRE_BLOCKS) {
         Player player = Minecraft.getInstance().player;
         if (player == null) {
            return;
         }

         Optional<ChunkCapHolder> opt = ChunkDifficulty.at(player.level(), player.blockPosition());
         if (opt.isEmpty()) {
            return;
         }

         if (!renderChunk) {
            return;
         }

         ChunkClearRenderer.render(event.getPoseStack(), player, opt.get(), event.getPartialTick().getGameTimeDeltaPartialTick(true));
      }

      if (event.getStage() == Stage.AFTER_WEATHER) {
         LevelRenderer renderer = event.getLevelRenderer();
         BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
         VertexConsumer cons = buffers.getBuffer(ClientEffectRenderEvents.get2DIcon(L2Hostility.loc("textures/entity/chain.png")));
         PoseStack pose = event.getPoseStack();
         pose.pushPose();
         Vec3 cam = event.getCamera().getPosition();
         pose.translate(-cam.x, -cam.y, -cam.z);
         ClientLevel level = Minecraft.getInstance().level;
         if (level != null) {
            for (Mob e : MASTERS) {
               if (e.isAlive()) {
                  MobTraitCap cap = (MobTraitCap)((GeneralCapabilityHolder)LHMiscs.MOB.type()).getOrCreate(e);
                  if (cap.asMaster != null) {
                     Vec3 p0 = e.position().add(0.0, e.getBbHeight() / 2.0F, 0.0);

                     for (MasterData.Minion minions : cap.asMaster.data) {
                        Mob m = minions.minion;
                        if (m != null && m.isAlive()) {
                           MobTraitCap scap = (MobTraitCap)((GeneralCapabilityHolder)LHMiscs.MOB.type()).getOrCreate(m);
                           if (scap.asMinion != null) {
                              Vec3 p1 = m.position().add(0.0, m.getBbHeight() / 2.0F, 0.0);
                              renderLink(event.getPoseStack(), cons, p0, p1, scap.asMinion.protectMaster);
                           }
                        }
                     }
                  }
               }
            }
         }

         pose.popPose();
      }
   }

   private static void renderLink(PoseStack pose, VertexConsumer cons, Vec3 p0, Vec3 p1, boolean protect) {
      Vec3 vec3 = p1.subtract(p0);
      float len = (float)vec3.length();
      if (!(len < 0.2F)) {
         pose.pushPose();
         pose.translate(p0.x, p0.y, p0.z);
         double d0 = vec3.horizontalDistance();
         pose.mulPose(Axis.YP.rotation((float)Mth.atan2(vec3.x, vec3.z)));
         pose.mulPose(Axis.XP.rotation((float)((Math.PI / 2) - Mth.atan2(vec3.y, d0))));
         float r = 0.125F;
         float off = protect ? 0.5F : 0.0F;
         renderQuad(pose.last(), cons, 0.0F, len, -r, r, 0.0F, 0.0F, off, off + 0.25F, 0.0F, len);
         renderQuad(pose.last(), cons, 0.0F, len, r, -r, 0.0F, 0.0F, off, off + 0.25F, 0.0F, len);
         renderQuad(pose.last(), cons, 0.0F, len, 0.0F, 0.0F, -r, r, off + 0.25F, off + 0.5F, 0.0F, len);
         renderQuad(pose.last(), cons, 0.0F, len, 0.0F, 0.0F, r, -r, off + 0.25F, off + 0.5F, 0.0F, len);
         pose.popPose();
      }
   }

   private static void renderQuad(
      Pose entry, VertexConsumer vc, float y0, float y1, float x0, float x1, float z0, float z1, float u0, float u1, float v0, float v1
   ) {
      vertex(entry, vc, x0, y1, z0, u1, v0);
      vertex(entry, vc, x0, y0, z0, u1, v1);
      vertex(entry, vc, x1, y0, z1, u0, v1);
      vertex(entry, vc, x1, y1, z1, u0, v0);
   }

   private static void vertex(Pose entry, VertexConsumer vc, float x, float y, float z, float u, float v) {
      vc.addVertex(entry.pose(), x, y, z).setUv(u, v).setNormal(entry, 0.0F, 1.0F, 0.0F);
   }
}
