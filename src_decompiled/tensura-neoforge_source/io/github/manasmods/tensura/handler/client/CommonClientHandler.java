package io.github.manasmods.tensura.handler.client;

import dev.architectury.event.events.client.ClientTooltipEvent;
import dev.architectury.event.events.client.ClientTooltipEvent.Item;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.tensura.client.screen.SettingsScreen;
import io.github.manasmods.tensura.client.screen.templates.SimpleScreen;
import io.github.manasmods.tensura.config.client.MenuConfig;
import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.effect.debuff.LustEmbracementEffect;
import io.github.manasmods.tensura.event.TensuraLocalPlayerEvents;
import io.github.manasmods.tensura.item.weapon.ranged.SimpleBowItem;
import io.github.manasmods.tensura.item.weapon.spell.SimpleSpellCastItem;
import io.github.manasmods.tensura.registry.item.misc.TensuraDataComponents;
import io.github.manasmods.tensura.world.TensuraGameRules;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;

public class CommonClientHandler {
   public static void init() {
      ClientTooltipEvent.ITEM
         .register(
            (Item)(stack, lines, tooltipContext, flag) -> {
               if (stack.has((DataComponentType)TensuraDataComponents.EP.get())) {
                  lines.add(
                     1,
                     Component.translatable(
                        "tooltip.tensura.gear_durability_EP",
                        new Object[]{
                           stack.get((DataComponentType)TensuraDataComponents.EP_DURABILITY.get()),
                           stack.get((DataComponentType)TensuraDataComponents.EP.get())
                        }
                     )
                  );
               }

               if (stack.is(TensuraItemTags.SPELL_CAST_WEAPONS) && stack.getItem() instanceof SimpleSpellCastItem castItem) {
                  castItem.renderTooltip(stack, lines);
               }

               if (stack.is(TensuraItemTags.RESET_SCROLLS)) {
                  Minecraft mc = Minecraft.getInstance();
                  if (mc.level == null) {
                     return;
                  }

                  int penalty = mc.level.getGameRules().getInt(TensuraGameRules.RESET_INCOMPLETE_PENALTY);
                  if (penalty > 0) {
                     lines.add(2, Component.translatable("tooltip.tensura.reset_scroll.penalty", new Object[]{penalty}).withStyle(ChatFormatting.YELLOW));
                     lines.add(2, Component.literal(""));
                  }
               }
            }
         );
      TensuraLocalPlayerEvents.FOV_MODIFIER_EVENT.register((TensuraLocalPlayerEvents.TensuraFovModifierEvent)(player, fov) -> {
         MenuConfig menuConfig = (MenuConfig)ConfigRegistry.getConfig(MenuConfig.class);
         if (Minecraft.getInstance().screen instanceof SimpleScreen && menuConfig.modifyFov) {
            return fov * 1.1F;
         }

         AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
         if (speed != null && speed.hasModifier(LustEmbracementEffect.EMBRACEMENT)) {
            fov /= ((float)speed.getValue() / player.getAbilities().getWalkingSpeed() + 1.0F) / 2.0F;
         }

         ItemStack itemStack = player.getUseItem();
         if (player.isUsingItem() && itemStack.getItem() instanceof SimpleBowItem bowItem) {
            int i = player.getTicksUsingItem();
            float second = (float)i / bowItem.getChargeTicks();
            if (second > 1.0F) {
               second = 1.0F;
            } else {
               second *= second;
            }

            fov *= 1.0F - second * 0.15F;
         }

         return fov;
      });
      TensuraLocalPlayerEvents.SETTINGS_EVENT.LOAD.register(SettingsScreen::loadSettings);
   }
}
