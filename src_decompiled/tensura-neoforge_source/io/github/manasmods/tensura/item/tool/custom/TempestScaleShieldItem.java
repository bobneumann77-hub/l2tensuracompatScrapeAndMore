package io.github.manasmods.tensura.item.tool.custom;

import io.github.manasmods.tensura.client.item.TempestScaleShieldItemRenderer;
import io.github.manasmods.tensura.item.tool.MultitoolItem;
import io.github.manasmods.tensura.item.tool.SimpleShieldItem;
import io.github.manasmods.tensura.registry.item.TensuraMobDropItems;
import io.github.manasmods.tensura.registry.item.misc.TensuraCreativeTabs;
import java.util.function.Consumer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class TempestScaleShieldItem extends SimpleShieldItem implements GeoItem {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public TempestScaleShieldItem() {
      super(
         new Properties().arch$tab(TensuraCreativeTabs.GEARS).durability(3964).fireResistant().attributes(MultitoolItem.createAttributes(14, -3.0F, 0.0, -1.0))
      );
   }

   public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
      return TensuraMobDropItems.CHARYBDIS_SCALE.get() == repair.getItem() || super.isValidRepairItem(toRepair, repair);
   }

   public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
      consumer.accept(new GeoRenderProvider() {
         private TempestScaleShieldItemRenderer renderer;

         public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
            if (this.renderer == null) {
               this.renderer = new TempestScaleShieldItemRenderer();
            }

            return this.renderer;
         }
      });
   }

   public void registerControllers(ControllerRegistrar controllers) {
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
