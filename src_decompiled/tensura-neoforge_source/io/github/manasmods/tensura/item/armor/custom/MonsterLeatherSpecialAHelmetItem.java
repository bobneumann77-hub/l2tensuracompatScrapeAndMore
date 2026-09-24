package io.github.manasmods.tensura.item.armor.custom;

import io.github.manasmods.tensura.client.armor.DefaultArmorRenderer;
import io.github.manasmods.tensura.item.armor.SimpleHelmetItem;
import java.util.function.Consumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MonsterLeatherSpecialAHelmetItem extends SimpleHelmetItem implements GeoItem {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public MonsterLeatherSpecialAHelmetItem(Holder<ArmorMaterial> armorMaterial, Properties properties) {
      super(armorMaterial, properties, 75);
   }

   public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
      consumer.accept(
         new GeoRenderProvider() {
            private GeoArmorRenderer<?> renderer;

            public <T extends LivingEntity> HumanoidModel<?> getGeoArmorRenderer(
               @Nullable T livingEntity, ItemStack itemStack, @Nullable EquipmentSlot equipmentSlot, @Nullable HumanoidModel<T> original
            ) {
               if (this.renderer == null) {
                  this.renderer = new DefaultArmorRenderer(ResourceLocation.fromNamespaceAndPath("tensura", "monster_leather_special_a"));
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
