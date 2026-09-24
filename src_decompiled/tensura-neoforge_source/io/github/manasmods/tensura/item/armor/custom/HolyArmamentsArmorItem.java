package io.github.manasmods.tensura.item.armor.custom;

import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.client.armor.HolyArmamentsArmorRenderer;
import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.item.armor.SimpleArmorItem;
import io.github.manasmods.tensura.registry.item.misc.TensuraArmorMaterials;
import io.github.manasmods.tensura.registry.item.misc.TensuraCreativeTabs;
import java.util.function.Consumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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

public class HolyArmamentsArmorItem extends SimpleArmorItem implements GeoItem {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public HolyArmamentsArmorItem(Type type) {
      super(TensuraArmorMaterials.MITHRIL, type, new Properties().arch$tab(TensuraCreativeTabs.ARMOR).fireResistant(), 50);
   }

   public boolean isFoil(ItemStack pStack) {
      return false;
   }

   public static void updateFlight(Entity entity, ItemStack from, ItemStack to) {
      if (!entity.isSpectator()) {
         if (to.is(TensuraItemTags.HOLY_ARMAMENTS_ITEMS) || from.is(TensuraItemTags.HOLY_ARMAMENTS_ITEMS)) {
            if (entity instanceof Player player && !player.isCreative()) {
               if (!player.getAbilities().mayfly) {
                  if (isFullSet(player)) {
                     player.getAbilities().mayfly = true;
                     player.onUpdateAbilities();
                  }
               } else if (!SkillUtils.canFlyLegit(player)) {
                  player.getAbilities().mayfly = false;
                  player.getAbilities().flying = false;
                  player.onUpdateAbilities();
               }
            }
         }
      }
   }

   public static void updateFlight(Entity entity) {
      if (!entity.isSpectator()) {
         if (entity instanceof Player player && !player.isCreative()) {
            if (!player.getAbilities().mayfly) {
               if (isFullSet(player)) {
                  player.getAbilities().mayfly = true;
                  player.onUpdateAbilities();
               }
            } else if (!SkillUtils.canFlyLegit(player)) {
               player.getAbilities().mayfly = false;
               player.getAbilities().flying = false;
               player.onUpdateAbilities();
            }
         }
      }
   }

   public static boolean isFullSet(LivingEntity entity) {
      if (!entity.getItemBySlot(EquipmentSlot.CHEST).is(TensuraItemTags.HOLY_ARMAMENTS_ITEMS)) {
         return false;
      } else {
         return !entity.getItemBySlot(EquipmentSlot.LEGS).is(TensuraItemTags.HOLY_ARMAMENTS_ITEMS)
            ? false
            : entity.getItemBySlot(EquipmentSlot.FEET).is(TensuraItemTags.HOLY_ARMAMENTS_ITEMS);
      }
   }

   public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
      consumer.accept(
         new GeoRenderProvider() {
            private GeoArmorRenderer<?> renderer;

            public <T extends LivingEntity> HumanoidModel<?> getGeoArmorRenderer(
               @Nullable T livingEntity, ItemStack itemStack, @Nullable EquipmentSlot equipmentSlot, @Nullable HumanoidModel<T> original
            ) {
               if (this.renderer == null) {
                  this.renderer = new HolyArmamentsArmorRenderer();
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
