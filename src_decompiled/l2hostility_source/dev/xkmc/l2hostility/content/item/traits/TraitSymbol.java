package dev.xkmc.l2hostility.content.item.traits;

import dev.xkmc.l2core.capability.attachment.GeneralCapabilityHolder;
import dev.xkmc.l2hostility.content.capability.mob.MobTraitCap;
import dev.xkmc.l2hostility.content.config.TraitConfig;
import dev.xkmc.l2hostility.content.traits.base.MobTrait;
import dev.xkmc.l2hostility.content.traits.legendary.LegendaryTrait;
import dev.xkmc.l2hostility.init.data.LHConfig;
import dev.xkmc.l2hostility.init.data.LangData;
import dev.xkmc.l2hostility.init.registrate.LHMiscs;
import dev.xkmc.l2hostility.init.registrate.LHTraits;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;

public class TraitSymbol extends Item {
   private static boolean allow(Player player, MobTrait trait, LivingEntity target) {
      if (!(Boolean)LHConfig.SERVER.allowPlayerAllies.get() && target.isAlliedTo(player)) {
         return false;
      } else {
         return !LHConfig.SERVER.allowTraitOnOwnable.get() && target instanceof OwnableEntity own && own.getOwner() instanceof Player
            ? false
            : trait.allow(target);
      }
   }

   public TraitSymbol(Properties properties) {
      super(properties);
   }

   public MobTrait get() {
      return (MobTrait)LHTraits.TRAITS.get().get(BuiltInRegistries.ITEM.getKey(this));
   }

   protected String getOrCreateDescriptionId() {
      return Util.makeDescriptionId(LHTraits.TRAITS.key().location().getPath(), BuiltInRegistries.ITEM.getKey(this));
   }

   public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
      Optional<MobTraitCap> opt = ((GeneralCapabilityHolder)LHMiscs.MOB.type()).getExisting(target);
      if (opt.isEmpty()) {
         return InteractionResult.PASS;
      }

      if (!((GeneralCapabilityHolder)LHMiscs.MOB.type()).isProper(target) && !player.getAbilities().instabuild) {
         return InteractionResult.FAIL;
      }

      MobTraitCap cap = opt.get();
      MobTrait trait = this.get();
      if (!allow(player, trait, target)) {
         if (player instanceof ServerPlayer sp) {
            sp.sendSystemMessage(LangData.MSG_ERR_DISALLOW.get().withStyle(ChatFormatting.RED), true);
         }

         return InteractionResult.FAIL;
      } else if (cap.getTraitLevel(trait) >= trait.getMaxLevel(player.registryAccess())) {
         if (player instanceof ServerPlayer sp) {
            sp.sendSystemMessage(LangData.MSG_ERR_MAX.get().withStyle(ChatFormatting.RED), true);
         }

         return InteractionResult.FAIL;
      } else {
         if (player.level().isClientSide()) {
            return InteractionResult.SUCCESS;
         }

         int val = cap.traits.compute(trait, (k, v) -> (v == null ? 0 : v) + 1);
         trait.initialize(target, val);
         trait.postInit(target, val);
         cap.syncToClient(target);
         target.setHealth(target.getMaxHealth());
         if (player instanceof ServerPlayer sp) {
            sp.sendSystemMessage(LangData.MSG_SET_TRAIT.get(trait.getDesc(), target.getDisplayName(), val), true);
            CriteriaTriggers.CONSUME_ITEM.trigger(sp, stack);
         }

         if (!player.getAbilities().instabuild) {
            stack.shrink(1);
         }

         return InteractionResult.SUCCESS;
      }
   }

   public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> list, TooltipFlag flag) {
      list.add(this.get().getFullDesc(null));
      if (this.get().isBanned()) {
         list.add(LangData.TOOLTIP_BANNED.get().withStyle(ChatFormatting.RED));
      } else {
         if (this.get() instanceof LegendaryTrait) {
            list.add(LangData.TOOLTIP_LEGENDARY.get().withStyle(ChatFormatting.GOLD));
         }

         Level level = ctx.level();
         if (level != null) {
            RegistryAccess access = level.registryAccess();
            if (flag.hasShiftDown()) {
               TraitConfig config = this.get().getConfig(access);
               list.add(
                  LangData.TOOLTIP_MIN_LEVEL.get(Component.literal(config.min_level() + "").withStyle(ChatFormatting.AQUA)).withStyle(ChatFormatting.GRAY)
               );
               list.add(LangData.TOOLTIP_LEVEL_COST.get(Component.literal(config.cost() + "").withStyle(ChatFormatting.AQUA)).withStyle(ChatFormatting.GRAY));
               list.add(
                  LangData.TOOLTIP_WEIGHT.get(Component.literal(config.weight() + "").withStyle(ChatFormatting.DARK_AQUA)).withStyle(ChatFormatting.DARK_GRAY)
               );
            } else {
               this.get().addDetail(access, list);
               list.add(LangData.SHIFT.get().withStyle(ChatFormatting.GRAY));
            }
         }
      }
   }
}
