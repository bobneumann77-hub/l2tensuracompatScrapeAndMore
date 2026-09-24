package io.github.manasmods.tensura.item.armor.custom;

import io.github.manasmods.tensura.client.armor.AdamantiteArmorRenderer;
import io.github.manasmods.tensura.item.armor.SimpleArmorItem;
import io.github.manasmods.tensura.registry.item.misc.TensuraArmorMaterials;
import io.github.manasmods.tensura.registry.item.misc.TensuraCreativeTabs;
import java.util.function.Consumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

public class AdamantiteArmorItem extends SimpleArmorItem implements GeoItem {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public AdamantiteArmorItem(Type type) {
      super(TensuraArmorMaterials.ADAMANTITE, type, new Properties().arch$tab(TensuraCreativeTabs.ARMOR).fireResistant(), 65);
   }

   public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
      consumer.accept(
         new GeoRenderProvider() {
            private GeoArmorRenderer<?> renderer;

            public <T extends LivingEntity> HumanoidModel<?> getGeoArmorRenderer(
               @Nullable T livingEntity, ItemStack itemStack, @Nullable EquipmentSlot equipmentSlot, @Nullable HumanoidModel<T> original
            ) {
               if (this.renderer == null) {
                  this.renderer = new AdamantiteArmorRenderer();
               }

               return this.renderer;
            }
         }
      );
   }

   public void registerControllers(ControllerRegistrar controllers) {
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
