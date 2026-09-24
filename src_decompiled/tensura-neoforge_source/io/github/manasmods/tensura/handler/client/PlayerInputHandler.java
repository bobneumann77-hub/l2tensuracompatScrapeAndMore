package io.github.manasmods.tensura.handler.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientRawInputEvent;
import dev.architectury.event.events.client.ClientRawInputEvent.KeyPressed;
import dev.architectury.event.events.client.ClientRawInputEvent.MouseScrolled;
import dev.architectury.networking.NetworkManager;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.manascore.skill.api.SkillEvents;
import io.github.manasmods.manascore.skill.api.SkillEvents.SkillScrollClientEvent;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Magic;
import io.github.manasmods.tensura.client.TensuraKeybinds;
import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.entity.template.subclass.ITensuraMount;
import io.github.manasmods.tensura.event.TensuraInputEvents;
import io.github.manasmods.tensura.network.c2s.RequestAbilityModeChangePacket;
import io.github.manasmods.tensura.network.c2s.RequestDodgePacket;
import io.github.manasmods.tensura.network.c2s.RequestMountAbilityPacket;
import io.github.manasmods.tensura.network.c2s.RequestSkillNumberKeyPacket;
import io.github.manasmods.tensura.network.c2s.RequestSpellChangePacket;
import io.github.manasmods.tensura.registry.item.misc.TensuraDataComponents;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ability.AbilitySlot;
import io.github.manasmods.tensura.storage.ability.IAbility;
import io.github.manasmods.tensura.util.client.ScreenHelper;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public class PlayerInputHandler {
   public static void init() {
      ClientRawInputEvent.MOUSE_SCROLLED.register((MouseScrolled)(minecraft, amountX, amountY) -> {
         if (TensuraKeybinds.NEXT_ABILITY_MODE.isDown()) {
            Player player = minecraft.player;
            if (player != null && player.getMainHandItem().is(TensuraItemTags.SPELL_CAST_WEAPONS)) {
               NetworkManager.sendToServer(new RequestSpellChangePacket(-amountY, false, InteractionHand.MAIN_HAND));
               return EventResult.interruptFalse();
            } else {
               NetworkManager.sendToServer(RequestAbilityModeChangePacket.changePresetPacket(amountY, true));
               return EventResult.interruptFalse();
            }
         } else if (TensuraKeybinds.PREVIOUS_ABILITY_MODE.isDown()) {
            Player player = minecraft.player;
            if (player != null && player.getMainHandItem().is(TensuraItemTags.SPELL_CAST_WEAPONS)) {
               NetworkManager.sendToServer(new RequestSpellChangePacket(amountY, false, InteractionHand.MAIN_HAND));
               return EventResult.interruptFalse();
            } else {
               NetworkManager.sendToServer(RequestAbilityModeChangePacket.changePresetPacket(-amountY, true));
               return EventResult.interruptFalse();
            }
         } else {
            if (minecraft.options.keySprint.isDown()) {
               Player player = minecraft.player;
               if (player != null && player.getVehicle() instanceof ITensuraMount mount && mount.hasScrollAbility(player)) {
                  NetworkManager.sendToServer(new RequestMountAbilityPacket(amountY));
                  return EventResult.interruptFalse();
               }
            }

            return EventResult.pass();
         }
      });
      SkillEvents.SKILL_SCROLL_CLIENT.register((SkillScrollClientEvent)(skillInstance, owner, mode, delta) -> {
         IAbility ability = TensuraStorages.getAbilityFrom(owner);
         if (TensuraKeybinds.ACTIVATE_SLOT_1.isDown()) {
            AbilitySlot slot = ability.getAbilitySlot(0);
            if (slot.getSkill() != skillInstance.getSkill()) {
               return EventResult.interruptFalse();
            }

            mode.set(slot.getMode());
            return EventResult.pass();
         } else if (TensuraKeybinds.ACTIVATE_SLOT_2.isDown()) {
            AbilitySlot slot = ability.getAbilitySlot(1);
            if (slot.getSkill() != skillInstance.getSkill()) {
               return EventResult.interruptFalse();
            }

            mode.set(slot.getMode());
            return EventResult.pass();
         } else if (TensuraKeybinds.ACTIVATE_SLOT_3.isDown()) {
            AbilitySlot slot = ability.getAbilitySlot(2);
            if (slot.getSkill() != skillInstance.getSkill()) {
               return EventResult.interruptFalse();
            }

            mode.set(slot.getMode());
            return EventResult.pass();
         } else if (Magic.isStaffCasting(skillInstance, owner)) {
            mode.set((Integer)owner.getUseItem().getOrDefault((DataComponentType)TensuraDataComponents.MODE.get(), 0));
            return EventResult.pass();
         } else {
            return EventResult.interruptFalse();
         }
      });
      ClientRawInputEvent.KEY_PRESSED
         .register(
            (KeyPressed)(client, key, scanCode, action, modifiers) -> {
               Player player = client.player;
               if (player == null) {
                  return EventResult.pass();
               }

               if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 292)) {
                  return EventResult.pass();
               }

               if (action != 1) {
                  return EventResult.pass();
               }

               if (Minecraft.getInstance().screen == null) {
                  if (TensuraKeybinds.MAIN_GUI.matches(key, scanCode)) {
                     ScreenHelper.openScreen(player.isShiftKeyDown() ? -1 : 4);
                     player.playNotifySound((SoundEvent)TensuraSoundEvents.SPATIAL_STORAGE.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 1.0F);
                     return EventResult.interruptFalse();
                  }

                  if (TensuraKeybinds.RELOAD_CONFIGS.matches(key, scanCode)) {
                     ConfigRegistry.loadConfigSyncData();
                     player.displayClientMessage(Component.literal("Successfully reloaded configs!"), true);
                     OverlayHandler.reloadAllValues();
                     return EventResult.interruptFalse();
                  }
               }

               if (key >= 48 && key <= 57) {
                  int numberKey = key - 48;
                  List<ResourceLocation> packetSkills = new ArrayList<>();

                  for (ManasSkillInstance instance : SkillAPI.getSkillsFrom(player).getLearnedSkills()) {
                     if (!((TensuraInputEvents.NumberKeyPressClientEvent)TensuraInputEvents.SKILL_NUMBER_KEY_CLIENT.invoker())
                        .press(instance, player, numberKey)
                        .isFalse()) {
                        packetSkills.add(instance.getSkillId());
                     }
                  }

                  if (!packetSkills.isEmpty()) {
                     NetworkManager.sendToServer(new RequestSkillNumberKeyPacket(numberKey, packetSkills));
                     return EventResult.interruptFalse();
                  }

                  if (!TensuraKeybinds.NEXT_ABILITY_MODE.isDown() && !TensuraKeybinds.PREVIOUS_ABILITY_MODE.isDown()) {
                     return EventResult.pass();
                  }

                  NetworkManager.sendToServer(RequestAbilityModeChangePacket.changePresetPacket(numberKey, false));
                  return EventResult.interruptFalse();
               } else {
                  return EventResult.pass();
               }
            }
         );
      TensuraInputEvents.MOVEMENT_INPUT_UPDATE_EVENT.register((TensuraInputEvents.MovementInputUpdateEvent)(player, input) -> {
         if (!player.isCreative() && !player.isSpectator()) {
            if (SkillUtils.shouldCancelJump(player)) {
               input.jumping = false;
               input.shiftKeyDown = false;
            }

            if (player.getAbilities().flying && player.getAttributeValue(Attributes.MOVEMENT_SPEED) <= 0.0) {
               input.forwardImpulse = 0.0F;
               input.leftImpulse = 0.0F;
            }
         }

         if (TensuraKeybinds.DODGE.isDown()) {
            if (Minecraft.getInstance().screen != null || input.jumping || !RequestDodgePacket.canDodge(player)) {
               return;
            }

            if (input.leftImpulse > 0.0F && input.forwardImpulse > 0.0F) {
               NetworkManager.sendToServer(new RequestDodgePacket(RequestDodgePacket.DodgeDirection.FORWARD_LEFT));
            } else if (input.leftImpulse < 0.0F && input.forwardImpulse > 0.0F) {
               NetworkManager.sendToServer(new RequestDodgePacket(RequestDodgePacket.DodgeDirection.FORWARD_RIGHT));
            } else if (input.leftImpulse > 0.0F && input.forwardImpulse < 0.0F) {
               NetworkManager.sendToServer(new RequestDodgePacket(RequestDodgePacket.DodgeDirection.BACKWARD_LEFT));
            } else if (input.leftImpulse < 0.0F && input.forwardImpulse < 0.0F) {
               NetworkManager.sendToServer(new RequestDodgePacket(RequestDodgePacket.DodgeDirection.BACKWARD_RIGHT));
            } else if (input.leftImpulse > 0.0F) {
               NetworkManager.sendToServer(new RequestDodgePacket(RequestDodgePacket.DodgeDirection.LEFT));
            } else if (input.leftImpulse < 0.0F) {
               NetworkManager.sendToServer(new RequestDodgePacket(RequestDodgePacket.DodgeDirection.RIGHT));
            } else if (input.forwardImpulse > 0.0F) {
               NetworkManager.sendToServer(new RequestDodgePacket(RequestDodgePacket.DodgeDirection.FORWARD));
            } else {
               NetworkManager.sendToServer(new RequestDodgePacket(RequestDodgePacket.DodgeDirection.BACKWARD));
            }
         }
      });
      TensuraInputEvents.SKILL_NUMBER_KEY_CLIENT.register((TensuraInputEvents.NumberKeyPressClientEvent)(skillInstance, owner, key) -> {
         IAbility ability = TensuraStorages.getAbilityFrom(owner);
         if (TensuraKeybinds.ACTIVATE_SLOT_1.isDown()) {
            if (ability.isAbilityInActivePreset(0, skillInstance.getSkill())) {
               return EventResult.pass();
            }
         } else if (TensuraKeybinds.ACTIVATE_SLOT_2.isDown()) {
            if (ability.isAbilityInActivePreset(1, skillInstance.getSkill())) {
               return EventResult.pass();
            }
         } else if (TensuraKeybinds.ACTIVATE_SLOT_3.isDown() && ability.isAbilityInActivePreset(2, skillInstance.getSkill())) {
            return EventResult.pass();
         }

         return EventResult.interruptFalse();
      });
   }
}
