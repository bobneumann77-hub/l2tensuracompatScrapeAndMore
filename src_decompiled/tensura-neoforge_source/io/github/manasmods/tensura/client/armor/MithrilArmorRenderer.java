package io.github.manasmods.tensura.client.armor;

import io.github.manasmods.tensura.item.armor.custom.MithrilArmorItem;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.component.DyedItemColor;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.texture.AnimatableTexture;
import software.bernie.geckolib.renderer.specialty.DyeableGeoArmorRenderer;
import software.bernie.geckolib.util.Color;

public class MithrilArmorRenderer extends DyeableGeoArmorRenderer<MithrilArmorItem> {
   public MithrilArmorRenderer() {
      super(new DefaultedArmorGeoModel(ResourceLocation.fromNamespaceAndPath("tensura", "mithril")));
      AnimatableTexture.setAndUpdate(ResourceLocation.fromNamespaceAndPath("tensura", "textures/models/armor/mithril.png"));
   }

   protected boolean isBoneDyeable(GeoBone bone) {
      return Objects.equals(bone.getName(), "tail");
   }

   @NotNull
   protected Color getColorForBone(GeoBone bone) {
      return this.getCurrentStack().is(ItemTags.DYEABLE) ? Color.ofOpaque(DyedItemColor.getOrDefault(this.getCurrentStack(), -1644826)) : Color.WHITE;
   }

   public Color getRenderColor(MithrilArmorItem animatable, float partialTick, int packedLight) {
      return Color.WHITE;
   }
}
