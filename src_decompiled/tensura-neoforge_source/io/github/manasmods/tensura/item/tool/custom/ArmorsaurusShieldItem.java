package io.github.manasmods.tensura.item.tool.custom;

import io.github.manasmods.tensura.client.item.ArmorsaurusShieldItemRenderer;
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

public class ArmorsaurusShieldItem extends SimpleShieldItem implements GeoItem {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public ArmorsaurusShieldItem() {
      super(new Properties().arch$tab(TensuraCreativeTabs.GEARS).durability(2000).attributes(MultitoolItem.createAttributes(5, -3.0F, 0.0, -1.0)));
   }

   public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
      if (TensuraMobDropItems.ARMORSAURUS_SHELL.get() == repair.getItem()) {
         return true;
      } else {
         return TensuraMobDropItems.ARMORSAURUS_SCALE.get() == repair.getItem() ? true : super.isValidRepairItem(toRepair, repair);
      }
   }

   public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
      consumer.accept(new GeoRenderProvider() {
         private ArmorsaurusShieldItemRenderer renderer;

         public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
            if (this.renderer == null) {
               this.renderer = new ArmorsaurusShieldItemRenderer();
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
