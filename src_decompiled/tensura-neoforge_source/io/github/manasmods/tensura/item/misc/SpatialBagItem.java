package io.github.manasmods.tensura.item.misc;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.subclass.ISpatialStorage;
import io.github.manasmods.tensura.registry.item.misc.TensuraDataComponents;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SpatialBagItem extends Item {
   public SpatialBagItem() {
      super(new Properties().rarity(Rarity.RARE).stacksTo(1));
   }

   @Nullable
   public ManasSkill getSkill(ItemStack stack) {
      ResourceLocation skill = (ResourceLocation)stack.get((DataComponentType)TensuraDataComponents.SKILL.get());
      return skill == null ? null : (ManasSkill)SkillAPI.getSkillRegistry().get(skill);
   }

   public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
      ManasSkill skill = this.getSkill(itemStack);
      if (skill != null) {
         list.add(skill.getChatDisplayName(false));
      }
   }

   @NotNull
   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand pHand) {
      ItemStack stack = player.getItemInHand(pHand);
      ManasSkill skill = this.getSkill(stack);
      if (skill instanceof ISpatialStorage spatial) {
         Optional<ManasSkillInstance> optional = SkillAPI.getSkillsFrom(player).getSkill(skill);
         if (optional.isPresent() && optional.get().getMastery() >= 0.0) {
            if (!level.isClientSide()) {
               spatial.openSpatialStorage(player, optional.get());
            }

            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
         } else {
            return super.use(level, player, pHand);
         }
      } else {
         return super.use(level, player, pHand);
      }
   }
}
