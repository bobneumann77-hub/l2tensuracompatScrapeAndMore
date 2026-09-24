package io.github.manasmods.tensura.item.armor.custom;

import io.github.manasmods.tensura.client.armor.DefaultArmorRenderer;
import io.github.manasmods.tensura.item.armor.SimpleArmorItem;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.item.misc.TensuraArmorMaterials;
import io.github.manasmods.tensura.registry.item.misc.TensuraCreativeTabs;
import java.util.function.Consumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

public class AntiMagicMaskItem extends SimpleArmorItem implements GeoItem {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public AntiMagicMaskItem() {
      super(
         TensuraArmorMaterials.ANTI_MAGIC_MASK,
         Type.HELMET,
         new Properties()
            .arch$tab(TensuraCreativeTabs.ARMOR)
            .fireResistant()
            .attributes(createAntiMagicAttributes(Type.HELMET, (ArmorMaterial)TensuraArmorMaterials.ANTI_MAGIC_MASK.get())),
         60
      );
   }

   public static ItemAttributeModifiers createAntiMagicAttributes(Type type, ArmorMaterial armorMaterial) {
      EquipmentSlotGroup equipmentSlotGroup = EquipmentSlotGroup.bySlot(type.getSlot());
      ResourceLocation location = ResourceLocation.withDefaultNamespace("armor." + type.getName());
      return SimpleArmorItem.createAttributes(type, armorMaterial)
         .add(TensuraAttributes.PRESENCE_CONCEALMENT, new AttributeModifier(location, 0.5, Operation.ADD_VALUE), equipmentSlotGroup)
         .build();
   }

   public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
      consumer.accept(
         new GeoRenderProvider() {
            private GeoArmorRenderer<?> renderer;

            public <T extends LivingEntity> HumanoidModel<?> getGeoArmorRenderer(
               @Nullable T livingEntity, ItemStack itemStack, @Nullable EquipmentSlot equipmentSlot, @Nullable HumanoidModel<T> original
            ) {
               if (this.renderer == null) {
                  this.renderer = new DefaultArmorRenderer(ResourceLocation.fromNamespaceAndPath("tensura", "anti_magic_mask"));
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
