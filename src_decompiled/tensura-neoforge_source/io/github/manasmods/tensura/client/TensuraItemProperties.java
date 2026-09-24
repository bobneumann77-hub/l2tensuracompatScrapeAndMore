package io.github.manasmods.tensura.client;

import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.magic.aspectual.AspectualMagic;
import io.github.manasmods.tensura.data.TensuraSkillTags;
import io.github.manasmods.tensura.item.weapon.custom.SpatialBladeItem;
import io.github.manasmods.tensura.item.weapon.ranged.SimpleBowItem;
import io.github.manasmods.tensura.item.weapon.ranged.SimpleCrossbowItem;
import io.github.manasmods.tensura.item.weapon.ranged.WebGunItem;
import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import io.github.manasmods.tensura.registry.item.TensuraMobDropItems;
import io.github.manasmods.tensura.registry.item.TensuraToolItems;
import io.github.manasmods.tensura.registry.item.misc.TensuraDataComponents;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;

public class TensuraItemProperties {
   public static void addCustomItemProperties() {
      magicTome((Item)TensuraMaterialItems.MAGIC_TOME.get());
      spatialBladeMode((SpatialBladeItem)TensuraToolItems.SPATIAL_BLADE.get());
      spearThrowing((Item)TensuraToolItems.BEAST_HORN_SPEAR.get());
      spearThrowing((Item)TensuraToolItems.UNICORN_HORN_SPEAR.get());
      spearThrowing((Item)TensuraToolItems.WOODEN_SPEAR.get());
      spearThrowing((Item)TensuraToolItems.STONE_SPEAR.get());
      spearThrowing((Item)TensuraToolItems.IRON_SPEAR.get());
      spearThrowing((Item)TensuraToolItems.SILVER_SPEAR.get());
      spearThrowing((Item)TensuraToolItems.GOLDEN_SPEAR.get());
      spearThrowing((Item)TensuraToolItems.DIAMOND_SPEAR.get());
      spearThrowing((Item)TensuraToolItems.LOW_MAGISTEEL_SPEAR.get());
      spearThrowing((Item)TensuraToolItems.NETHERITE_SPEAR.get());
      spearThrowing((Item)TensuraToolItems.HIGH_MAGISTEEL_SPEAR.get());
      spearThrowing((Item)TensuraToolItems.MITHRIL_SPEAR.get());
      spearThrowing((Item)TensuraToolItems.ORICHALCUM_SPEAR.get());
      spearThrowing((Item)TensuraToolItems.PURE_MAGISTEEL_SPEAR.get());
      spearThrowing((Item)TensuraToolItems.ADAMANTITE_SPEAR.get());
      spearThrowing((Item)TensuraToolItems.HIHIIROKANE_SPEAR.get());
      spearThrowing((Item)TensuraToolItems.VORTEX_SPEAR.get());
      bowPulling((Item)TensuraToolItems.SHORT_BOW.get());
      bowPulling((Item)TensuraToolItems.LONG_BOW.get());
      bowPulling((Item)TensuraToolItems.WAR_BOW.get());
      bowPulling((Item)TensuraToolItems.SHORT_SPIDER_BOW.get());
      bowPulling((Item)TensuraToolItems.SPIDER_BOW.get());
      bowPulling((Item)TensuraToolItems.LONG_SPIDER_BOW.get());
      bowPulling((Item)TensuraToolItems.WAR_SPIDER_BOW.get());
      crossbowPulling((SimpleCrossbowItem)TensuraToolItems.ANT_CROSSBOW.get());
      crossbowProjectiles((Item)TensuraToolItems.ANT_CROSSBOW.get());
      crossbowProjectiles(Items.CROSSBOW);
      gunLoading((WebGunItem)TensuraToolItems.WEB_GUN.get());
      gunSliding((Item)TensuraToolItems.WALTHER_P99.get());
      shieldBlocking((Item)TensuraToolItems.TEMPEST_SCALE_SHIELD.get());
      shieldBlocking((Item)TensuraToolItems.ARMORSAURUS_SHIELD.get());
      offHandModel((Item)TensuraToolItems.ARMORSAURUS_GAUNTLET.get());
      metalSlimeBucket((Item)TensuraMaterialItems.SLIME_IN_A_BUCKET.get());
      hasTsukumogamiInactive((Item)TensuraToolItems.HIHIIROKANE_SWORD.get());
      hasTsukumogamiInactive((Item)TensuraToolItems.HIHIIROKANE_SHORT_SWORD.get());
      hasTsukumogamiInactive((Item)TensuraToolItems.HIHIIROKANE_LONG_SWORD.get());
      hasTsukumogamiInactive((Item)TensuraToolItems.HIHIIROKANE_GREAT_SWORD.get());
      hasTsukumogamiInactive((Item)TensuraToolItems.HIHIIROKANE_KATANA.get());
      hasTsukumogamiInactive((Item)TensuraToolItems.HIHIIROKANE_KODACHI.get());
      hasTsukumogamiInactive((Item)TensuraToolItems.HIHIIROKANE_TACHI.get());
      hasTsukumogamiInactive((Item)TensuraToolItems.HIHIIROKANE_ODACHI.get());
      hasTsukumogamiInactive((Item)TensuraToolItems.HIHIIROKANE_SPEAR.get());
      hasTsukumogamiInactive((Item)TensuraToolItems.HIHIIROKANE_SCYTHE.get());
      hasTsukumogamiInactive((Item)TensuraToolItems.HIHIIROKANE_PICKAXE.get());
      hasTsukumogamiInactive((Item)TensuraToolItems.HIHIIROKANE_AXE.get());
      hasTsukumogamiInactive((Item)TensuraToolItems.HIHIIROKANE_SHOVEL.get());
      hasTsukumogamiInactive((Item)TensuraToolItems.HIHIIROKANE_HOE.get());
      hasTsukumogamiInactive((Item)TensuraToolItems.HIHIIROKANE_SICKLE.get());
   }

   private static void metalSlimeBucket(Item item) {
      ItemProperties.register(
         item,
         ResourceLocation.withDefaultNamespace("metal"),
         (stack, pLevel, pEntity, pValue) -> stack.has(DataComponents.CUSTOM_DATA)
               && ((CustomData)stack.get(DataComponents.CUSTOM_DATA)).copyTag().getBoolean("Metal")
            ? 1.0F
            : 0.0F
      );
   }

   private static void spatialBladeMode(SpatialBladeItem item) {
      ItemProperties.register(item, ResourceLocation.withDefaultNamespace("hilt"), (pStack, pLevel, pEntity, pValue) -> item.isHiltOnly(pStack) ? 1.0F : 0.0F);
   }

   private static void spearThrowing(Item item) {
      ItemProperties.register(
         item,
         ResourceLocation.withDefaultNamespace("throwing"),
         (pStack, pLevel, pEntity, pValue) -> pEntity != null && pEntity.isUsingItem() && pEntity.getUseItem() == pStack ? 1.0F : 0.0F
      );
   }

   private static void bowPulling(Item bow) {
      ItemProperties.register(bow, ResourceLocation.withDefaultNamespace("pull"), (itemStack, level, entity, i) -> {
         if (entity == null) {
            return 0.0F;
         }

         if (entity.getUseItem() != itemStack) {
            return 0.0F;
         }

         float chargeTicks = ((SimpleBowItem)itemStack.getItem()).getChargeTicks();
         return (itemStack.getUseDuration(entity) - entity.getUseItemRemainingTicks()) / chargeTicks;
      });
      ItemProperties.register(
         bow,
         ResourceLocation.withDefaultNamespace("pulling"),
         (itemStack, level, entity, i) -> entity != null && entity.isUsingItem() && entity.getUseItem() == itemStack ? 1.0F : 0.0F
      );
   }

   private static void gunSliding(Item gun) {
      ItemProperties.register(
         gun,
         ResourceLocation.withDefaultNamespace("sliding"),
         (itemStack, level, entity, i) -> entity != null && entity.isUsingItem() && entity.getUseItem() == itemStack ? 1.0F : 0.0F
      );
   }

   private static void gunLoading(WebGunItem gun) {
      ItemProperties.register(
         gun,
         ResourceLocation.withDefaultNamespace("pulling"),
         (itemStack, level, entity, i) -> entity != null && entity.isUsingItem() && entity.getUseItem() == itemStack && !WebGunItem.isCharged(itemStack)
            ? 1.0F
            : 0.0F
      );
      ItemProperties.register(
         gun, ResourceLocation.withDefaultNamespace("charged"), (itemStack, level, entity, i) -> WebGunItem.isCharged(itemStack) ? 1.0F : 0.0F
      );
   }

   private static void shieldBlocking(Item item) {
      ItemProperties.register(
         item,
         ResourceLocation.withDefaultNamespace("blocking"),
         (pStack, level, pEntity, i) -> pEntity != null && pEntity.isUsingItem() && pEntity.getUseItem() == pStack ? 1.0F : 0.0F
      );
   }

   private static void crossbowPulling(SimpleCrossbowItem item) {
      ItemProperties.register(
         item,
         ResourceLocation.withDefaultNamespace("pull"),
         (stack, level, entity, i) -> {
            if (entity == null) {
               return 0.0F;
            } else {
               return SimpleCrossbowItem.isCharged(stack)
                  ? 0.0F
                  : (float)(stack.getUseDuration(entity) - entity.getUseItemRemainingTicks())
                     / SimpleCrossbowItem.getChargeDuration(stack, entity, item.getChargeTicks());
            }
         }
      );
      ItemProperties.register(
         item,
         ResourceLocation.withDefaultNamespace("pulling"),
         (itemStack, clientLevel, livingEntity, i) -> livingEntity != null
               && livingEntity.isUsingItem()
               && livingEntity.getUseItem() == itemStack
               && !CrossbowItem.isCharged(itemStack)
            ? 1.0F
            : 0.0F
      );
      ItemProperties.register(
         item, ResourceLocation.withDefaultNamespace("charged"), (itemStack, clientLevel, livingEntity, i) -> CrossbowItem.isCharged(itemStack) ? 1.0F : 0.0F
      );
      ItemProperties.register(
         item,
         ResourceLocation.withDefaultNamespace("firework"),
         (stack, level, living, i) -> SimpleCrossbowItem.containsChargedProjectile(stack, Items.FIREWORK_ROCKET) ? 1.0F : 0.0F
      );
   }

   private static void crossbowProjectiles(Item item) {
      ItemProperties.register(
         item,
         ResourceLocation.withDefaultNamespace("unicorn_horn"),
         (stack, level, living, i) -> living != null
               && CrossbowItem.isCharged(stack)
               && SimpleCrossbowItem.containsChargedProjectile(stack, (Item)TensuraMobDropItems.UNICORN_HORN.get())
            ? 1.0F
            : 0.0F
      );
      ItemProperties.register(
         item,
         ResourceLocation.withDefaultNamespace("invisible_arrow"),
         (stack, level, living, i) -> living != null
               && CrossbowItem.isCharged(stack)
               && SimpleCrossbowItem.containsChargedProjectile(stack, (Item)TensuraToolItems.INVISIBLE_ARROW.get())
            ? 1.0F
            : 0.0F
      );
      ItemProperties.register(
         item,
         ResourceLocation.withDefaultNamespace("speared_fin"),
         (stack, level, living, i) -> living != null
               && CrossbowItem.isCharged(stack)
               && SimpleCrossbowItem.containsChargedProjectile(stack, (Item)TensuraToolItems.SPEARED_FIN_ARROW.get())
            ? 1.0F
            : 0.0F
      );
      ItemProperties.register(
         item,
         ResourceLocation.withDefaultNamespace("spectral_arrow"),
         (stack, level, living, i) -> living != null
               && CrossbowItem.isCharged(stack)
               && SimpleCrossbowItem.containsChargedProjectile(stack, Items.SPECTRAL_ARROW)
            ? 1.0F
            : 0.0F
      );
   }

   private static void offHandModel(Item item) {
      ItemProperties.register(
         item,
         ResourceLocation.withDefaultNamespace("offhand"),
         (itemStack, level, livingEntity, i) -> livingEntity != null && livingEntity.getItemBySlot(EquipmentSlot.OFFHAND) == itemStack ? 1.0F : 0.0F
      );
   }

   private static void hasTsukumogamiInactive(Item item) {
      ItemProperties.register(item, ResourceLocation.withDefaultNamespace("inactive"), (stack, level, livingEntity, i) -> {
         if (livingEntity != null) {
            return ((Float)stack.getOrDefault((DataComponentType)TensuraDataComponents.TSUKUMOGAMI_INACTIVE.get(), 0.0F)).floatValue() >= 0.5 ? 1.0F : 0.0F;
         } else {
            return 0.0F;
         }
      });
   }

   private static void magicTome(Item item) {
      for (AspectualMagic.AspectualType type : AspectualMagic.AspectualType.values()) {
         ItemProperties.register(item, ResourceLocation.withDefaultNamespace(type.getNamespace()), (itemStack, level, livingEntity, i) -> {
            ResourceLocation skill = (ResourceLocation)itemStack.get((DataComponentType)TensuraDataComponents.SKILL.get());
            if (skill == null) {
               return 0.0F;
            } else {
               return SkillAPI.getSkillRegistry().get(skill) instanceof AspectualMagic magic && magic.getAspectualType() == type ? 1.0F : 0.0F;
            }
         });
      }

      ItemProperties.register(item, ResourceLocation.withDefaultNamespace("necromancy"), (itemStack, level, livingEntity, i) -> {
         ResourceLocation skill = (ResourceLocation)itemStack.get((DataComponentType)TensuraDataComponents.SKILL.get());
         if (skill == null) {
            return 0.0F;
         } else {
            return SkillAPI.getSkillRegistry().delegate(skill).is(TensuraSkillTags.NECROMANCY_MAGIC) ? 1.0F : 0.0F;
         }
      });
      ItemProperties.register(item, ResourceLocation.withDefaultNamespace("summoning"), (itemStack, level, livingEntity, i) -> {
         ResourceLocation skill = (ResourceLocation)itemStack.get((DataComponentType)TensuraDataComponents.SKILL.get());
         if (skill == null) {
            return 0.0F;
         } else {
            return SkillAPI.getSkillRegistry().delegate(skill).is(TensuraSkillTags.SUMMONING_MAGIC) ? 1.0F : 0.0F;
         }
      });
   }
}
