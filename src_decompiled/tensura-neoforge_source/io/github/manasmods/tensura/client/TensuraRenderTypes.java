package io.github.manasmods.tensura.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import dev.architectury.event.events.client.ClientReloadShadersEvent;
import java.io.IOException;
import java.util.Objects;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.RenderStateShard.ShaderStateShard;
import net.minecraft.client.renderer.RenderStateShard.TextureStateShard;
import net.minecraft.client.renderer.RenderType.CompositeState;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class TensuraRenderTypes {
   @Nullable
   private static ShaderInstance entityTranslucentUnlitShader;
   public static final ShaderStateShard ENTITY_TRANSLUCENT_UNLIT_SHADER = new ShaderStateShard(TensuraRenderTypes::getEntityTranslucentUnlitShader);

   public static void init() {
      ClientReloadShadersEvent.EVENT
         .register(
            (ClientReloadShadersEvent)(provider, sink) -> {
               try {
                  sink.registerShader(
                     new ShaderInstance(provider, "entity_unlit_translucent", DefaultVertexFormat.NEW_ENTITY),
                     instance -> entityTranslucentUnlitShader = instance
                  );
               } catch (IOException e) {
                  throw new RuntimeException(e);
               }
            }
         );
   }

   private static ShaderInstance getEntityTranslucentUnlitShader() {
      return Objects.requireNonNull(entityTranslucentUnlitShader, "Attempted to call getEntityTranslucentUnlitShader before shaders have finished loading.");
   }

   public static RenderType getUnlitTranslucent(ResourceLocation textureLocation) {
      return getUnlitTranslucent(textureLocation, true);
   }

   public static RenderType getUnlitTranslucent(ResourceLocation textureLocation, boolean sortingEnabled) {
      CompositeState renderState = CompositeState.builder()
         .setShaderState(ENTITY_TRANSLUCENT_UNLIT_SHADER)
         .setTextureState(new TextureStateShard(textureLocation, false, false))
         .setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
         .setCullState(RenderType.NO_CULL)
         .setLightmapState(RenderType.LIGHTMAP)
         .setOverlayState(RenderType.OVERLAY)
         .createCompositeState(true);
      return RenderType.create("entity_unlit_translucent", DefaultVertexFormat.NEW_ENTITY, Mode.QUADS, 256, true, sortingEnabled, renderState);
   }
}
