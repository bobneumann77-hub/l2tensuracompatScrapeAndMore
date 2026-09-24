package io.github.manasmods.tensura.item.misc;

import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import io.github.manasmods.tensura.registry.item.misc.TensuraCreativeTabs;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.Holder;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

public class OrcDisasterHeadItem extends BlockItem implements GeoItem, Equipable {
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   private static final Component TOOLTIP_HEAD = Component.translatable("tooltip.tensura.orc_disaster_head").withStyle(ChatFormatting.GOLD);

   public OrcDisasterHeadItem() {
      super((Block)TensuraBlocks.ORC_DISASTER_HEAD.get(), new Properties().fireResistant().stacksTo(1).arch$tab(TensuraCreativeTabs.MOB_DROPS));
      DispenserBlock.registerBehavior(this, new OptionalDispenseItemBehavior() {
         protected ItemStack execute(BlockSource arg, ItemStack arg2) {
            this.setSuccess(ArmorItem.dispenseArmor(arg, arg2));
            return arg2;
         }
      });
   }

   public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
      ItemStack item = pPlayer.getItemInHand(pUsedHand);
      ItemStack slot = pPlayer.getItemBySlot(EquipmentSlot.HEAD);
      if (!slot.isEmpty()) {
         return InteractionResultHolder.fail(item);
      }

      pPlayer.setItemSlot(EquipmentSlot.HEAD, item.copy());
      if (!pLevel.isClientSide()) {
         pPlayer.awardStat(Stats.ITEM_USED.get(this.asItem()));
      }

      item.setCount(0);
      return InteractionResultHolder.sidedSuccess(item, pLevel.isClientSide());
   }

   @NotNull
   public EquipmentSlot getEquipmentSlot() {
      return EquipmentSlot.HEAD;
   }

   @NotNull
   public Holder<SoundEvent> getEquipSound() {
      return SoundEvents.ARMOR_EQUIP_LEATHER;
   }

   public int getEnchantmentValue() {
      return 30;
   }

   public boolean isEnchantable(ItemStack pStack) {
      return true;
   }

   public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
      list.add(TOOLTIP_HEAD);
   }

   public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
      consumer.accept(
         new GeoRenderProvider() {
            private GeoItemRenderer<OrcDisasterHeadItem> renderer = null;

            @NotNull
            public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
               if (this.renderer == null) {
                  this.renderer = new GeoItemRenderer(
                     new DefaultedBlockGeoModel<OrcDisasterHeadItem>(ResourceLocation.fromNamespaceAndPath("tensura", "orc_disaster_head")) {
                        public ResourceLocation getTextureResource(OrcDisasterHeadItem object) {
                           return ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/orc/orc_disaster.png");
                        }
                     }
                  );
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
