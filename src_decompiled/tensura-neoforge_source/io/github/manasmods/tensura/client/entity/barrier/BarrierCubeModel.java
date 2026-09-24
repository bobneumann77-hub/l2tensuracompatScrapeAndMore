package io.github.manasmods.tensura.client.entity.barrier;

import io.github.manasmods.tensura.entity.TensuraProjectile;
import java.util.Arrays;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.texture.AnimatableTexture;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class BarrierCubeModel<E extends TensuraProjectile & GeoEntity> extends DefaultedEntityGeoModel<E> {
   @Nullable
   private final ResourceLocation[] startTexture;
   private final ResourceLocation texture;
   private final int startSpeed;

   public BarrierCubeModel(ResourceLocation texture, @Nullable ResourceLocation[] startTexture, int startSpeed) {
      super(ResourceLocation.fromNamespaceAndPath("tensura", "barrier_cube"), false);
      this.texture = texture;
      this.startTexture = startTexture;
      this.startSpeed = startSpeed;
      AnimatableTexture.setAndUpdate(ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/barrier/anti_magic_area.png"));
      AnimatableTexture.setAndUpdate(ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/barrier/anti_shock_area.png"));
   }

   public BarrierCubeModel(ResourceLocation texture, @Nullable ResourceLocation[] startTexture) {
      this(texture, startTexture, 4);
   }

   protected String subtype() {
      return "entity/misc";
   }

   public RenderType getRenderType(E animatable, ResourceLocation texture) {
      return RenderType.entityTranslucent(texture);
   }

   public ResourceLocation getTextureResource(E instance) {
      if (this.startTexture != null) {
         if (instance.getAge() < this.startSpeed * this.startTexture.length) {
            return Arrays.stream(this.startTexture).toList().get(instance.getAge() / this.startSpeed % this.startTexture.length);
         }

         int time = instance.getLife() - instance.getAge();
         if (time < this.startSpeed * this.startTexture.length) {
            return Arrays.stream(this.startTexture).toList().get(time / this.startSpeed % this.startTexture.length);
         }
      }

      return this.texture == null ? ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/barrier/anti_magic_area.png") : this.texture;
   }
}
