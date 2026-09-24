package io.github.manasmods.tensura.network.c2s;

import dev.architectury.event.EventResult;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.utils.Env;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.manascore.skill.api.SkillEvents;
import io.github.manasmods.manascore.skill.api.Skills;
import io.github.manasmods.manascore.skill.api.SkillEvents.SkillToggleEvent;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.subclass.ISubAbilityModeHolder;
import io.github.manasmods.tensura.event.TensuraSkillEvents;
import io.github.manasmods.tensura.menu.ReincarnationMenu;
import io.github.manasmods.tensura.network.s2c.UpdateAbilitySlotScreenPayload;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ability.AbilitySlot;
import io.github.manasmods.tensura.storage.ability.IAbility;
import io.github.manasmods.tensura.storage.player.ITensuraPlayer;
import io.github.manasmods.tensura.world.TensuraGameRules;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record RequestAbilityModeChangePacket(
   String presetName, int presetId, double presetDelta, int slot, boolean alternative, ResourceLocation location, RequestAbilityModeChangePacket.Action action
) implements CustomPacketPayload {
   public static final Type<RequestAbilityModeChangePacket> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("tensura", "request_ability_mode_change"));
   public static final StreamCodec<FriendlyByteBuf, RequestAbilityModeChangePacket> STREAM_CODEC = CustomPacketPayload.codec(
      RequestAbilityModeChangePacket::encode, RequestAbilityModeChangePacket::new
   );

   public RequestAbilityModeChangePacket(FriendlyByteBuf buf) {
      this(
         buf.readUtf(),
         buf.readInt(),
         buf.readDouble(),
         buf.readInt(),
         buf.readBoolean(),
         buf.readResourceLocation(),
         (RequestAbilityModeChangePacket.Action)buf.readEnum(RequestAbilityModeChangePacket.Action.class)
      );
   }

   public static RequestAbilityModeChangePacket changePresetPacket(double presetDelta, boolean scroll) {
      return new RequestAbilityModeChangePacket(
         "", -1, presetDelta, -1, scroll, ResourceLocation.withDefaultNamespace("empty"), RequestAbilityModeChangePacket.Action.CHANGE_PRESET
      );
   }

   public static RequestAbilityModeChangePacket changeModePacket(int slot, int preset, boolean reverse) {
      return new RequestAbilityModeChangePacket(
         "", preset, 0.0, slot, reverse, ResourceLocation.withDefaultNamespace("empty"), RequestAbilityModeChangePacket.Action.CHANGE_MODE
      );
   }

   public static RequestAbilityModeChangePacket changeModePacket(int slot, boolean reverse) {
      return changeModePacket(slot, -1, reverse);
   }

   public static RequestAbilityModeChangePacket changeAbilityPacket(int slot, int preset, ResourceLocation location, int mode) {
      if (location == null) {
         location = ResourceLocation.withDefaultNamespace("empty");
      }

      return new RequestAbilityModeChangePacket("", preset, mode, slot, false, location, RequestAbilityModeChangePacket.Action.CHANGE_ABILITY);
   }

   public static RequestAbilityModeChangePacket changeAbilityPacket(int slot, int preset, ResourceLocation location) {
      return changeAbilityPacket(slot, preset, location, 0);
   }

   public static RequestAbilityModeChangePacket toggleAbilityPacket(ResourceLocation location) {
      if (location == null) {
         location = ResourceLocation.withDefaultNamespace("empty");
      }

      return new RequestAbilityModeChangePacket("", -1, 0.0, -1, false, location, RequestAbilityModeChangePacket.Action.TOGGLE_ABILITY);
   }

   public static RequestAbilityModeChangePacket lockAbilityPacket(ResourceLocation location) {
      if (location == null) {
         location = ResourceLocation.withDefaultNamespace("empty");
      }

      return new RequestAbilityModeChangePacket("", -1, 0.0, -1, false, location, RequestAbilityModeChangePacket.Action.LOCK_ABILITY);
   }

   public static RequestAbilityModeChangePacket renamePresetPacket(int preset, String name) {
      return new RequestAbilityModeChangePacket(
         name, preset, -1.0, -1, false, ResourceLocation.withDefaultNamespace("empty"), RequestAbilityModeChangePacket.Action.RENAME_PRESET
      );
   }

   public static RequestAbilityModeChangePacket removeSubAbilityMode(ResourceLocation location, String skillId, int mode) {
      if (location == null) {
         location = ResourceLocation.withDefaultNamespace("empty");
      }

      return new RequestAbilityModeChangePacket(skillId, -1, 0.0, mode, false, location, RequestAbilityModeChangePacket.Action.REMOVE_SUB_MODE);
   }

   public void encode(FriendlyByteBuf buf) {
      buf.writeUtf(this.presetName);
      buf.writeInt(this.presetId);
      buf.writeDouble(this.presetDelta);
      buf.writeInt(this.slot);
      buf.writeBoolean(this.alternative);
      buf.writeResourceLocation(this.location);
      buf.writeEnum(this.action);
   }

   public void handle(PacketContext context) {
      if (context.getEnvironment() == Env.SERVER) {
         context.queue(() -> {
            ServerPlayer player = (ServerPlayer)context.getPlayer();
            if (player != null) {
               switch (this.action) {
                  case CHANGE_MODE:
                     this.changeMode(player);
                     break;
                  case CHANGE_PRESET:
                     this.changePreset(player);
                     break;
                  case RENAME_PRESET:
                     this.renamePreset(player);
                     break;
                  case CHANGE_ABILITY:
                     this.changeAbility(player);
                     break;
                  case TOGGLE_ABILITY:
                     this.toggleAbility(player);
                     break;
                  case LOCK_ABILITY:
                     this.lockAbility(player);
                     break;
                  case REMOVE_SUB_MODE:
                     Optional<ManasSkillInstance> optional = SkillAPI.getSkillsFrom(player).getSkill(this.location);
                     if (optional.isEmpty()) {
                        return;
                     }

                     if (!(optional.get().getSkill() instanceof ISubAbilityModeHolder holder)) {
                        return;
                     }

                     ManasSkill skill = (ManasSkill)SkillAPI.getSkillRegistry().get(ResourceLocation.parse(this.presetName));
                     if (skill == null) {
                        return;
                     }

                     holder.removeSubSkill(optional.get(), player, skill, this.slot);
               }

               player.manasCore$sync(player);
            }
         });
      }
   }

   @NotNull
   public Type<RequestAbilityModeChangePacket> type() {
      return TYPE;
   }

   public void changeMode(Player player) {
      IAbility ability = TensuraStorages.getAbilityFrom(player);
      int preset = this.presetId == -1 ? ability.getActivePreset() : this.presetId;
      AbilitySlot abilitySlot = ability.getAbilitySlot(preset, this.slot);
      ManasSkill skill = abilitySlot.getSkill();
      if (skill != null) {
         Skills skills = SkillAPI.getSkillsFrom(player);
         Optional<ManasSkillInstance> optional = skills.getSkill(skill);
         if (!optional.isEmpty()) {
            ManasSkillInstance instance = optional.get();
            if (instance.canInteractSkill(player)) {
               int mode = abilitySlot.getMode();
               if (instance.getModes() > 1) {
                  if (skill instanceof TensuraSkill tensuraSkill) {
                     int nextMode = tensuraSkill.nextMode(player, instance, mode, this.alternative);
                     if (nextMode == -1) {
                        player.displayClientMessage(
                           Component.translatable("tensura.skill.mode.cannot_change", new Object[]{instance.getChatDisplayName(false)})
                              .withStyle(ChatFormatting.RED),
                           true
                        );
                     } else {
                        Changeable<ManasSkillInstance> instanceChangeable = Changeable.of(instance);
                        Changeable<Integer> modeChangeable = Changeable.of(nextMode);
                        Changeable<Integer> slotChangeable = Changeable.of(this.slot);
                        Changeable<Integer> presetChangeable = Changeable.of(this.presetId);
                        if (!tensuraSkill.onAbilityEquipped(player, instanceChangeable, modeChangeable, slotChangeable, presetChangeable)) {
                           return;
                        }

                        ability.setAbilitySlot(
                           preset, (Integer)slotChangeable.get(), ((ManasSkillInstance)instanceChangeable.get()).getSkill(), (Integer)modeChangeable.get()
                        );
                        player.displayClientMessage(
                           Component.translatable(
                                 "tensura.skill.mode.changed",
                                 new Object[]{
                                    ((ManasSkillInstance)instanceChangeable.get()).getChatDisplayName(false),
                                    tensuraSkill.getModeName((ManasSkillInstance)instanceChangeable.get(), (Integer)modeChangeable.get())
                                 }
                              )
                              .setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)),
                           true
                        );
                        ability.markDirty();
                     }
                  }
               } else {
                  player.displayClientMessage(
                     Component.translatable("tensura.skill.mode.no_mode", new Object[]{instance.getChatDisplayName(false)}).withStyle(ChatFormatting.RED), true
                  );
               }
            }
         }
      }
   }

   public void changeAbility(ServerPlayer player) {
      IAbility ability = TensuraStorages.getAbilityFrom(player);
      if (this.location.equals(ResourceLocation.withDefaultNamespace("empty"))) {
         ability.setAbilitySlot(this.presetId, this.slot, null, 0);
         ability.markDirty();
      } else {
         Optional<ManasSkillInstance> optional = SkillAPI.getSkillsFrom(player).getSkill(this.location);
         if (!optional.isEmpty()) {
            ManasSkillInstance instance = optional.get();
            Changeable<ManasSkillInstance> instanceChangeable = Changeable.of(instance);
            Changeable<Integer> modeChangeable = Changeable.of((int)this.presetDelta);
            Changeable<Integer> slotChangeable = Changeable.of(this.slot);
            Changeable<Integer> presetChangeable = Changeable.of(this.presetId);
            if (instance.getSkill() instanceof TensuraSkill skill) {
               if (!skill.canBeSlotted(instance, player, 0)) {
                  return;
               }

               if (!skill.onAbilityEquipped(player, instanceChangeable, modeChangeable, slotChangeable, presetChangeable)) {
                  ManasSkill slotted = ability.getAbilitySlot(this.presetId, this.slot).getSkill();
                  ResourceLocation location = slotted == null ? ResourceLocation.withDefaultNamespace("empty") : slotted.getRegistryName();
                  NetworkManager.sendToPlayer(player, new UpdateAbilitySlotScreenPayload(player.getId(), this.slot, location));
                  return;
               }
            }

            EventResult result = ((TensuraSkillEvents.AbilityEquipEvent)TensuraSkillEvents.ABILITY_EQUIP.invoker())
               .equip(player, instanceChangeable, modeChangeable, slotChangeable, presetChangeable);
            if (result.isFalse()) {
               ManasSkill slotted = ability.getAbilitySlot(this.presetId, this.slot).getSkill();
               ResourceLocation location = slotted == null ? ResourceLocation.withDefaultNamespace("empty") : slotted.getRegistryName();
               NetworkManager.sendToPlayer(player, new UpdateAbilitySlotScreenPayload(player.getId(), this.slot, location));
            } else {
               ability.setAbilitySlot(
                  (Integer)presetChangeable.get(),
                  (Integer)slotChangeable.get(),
                  ((ManasSkillInstance)instanceChangeable.get()).getSkill(),
                  (Integer)modeChangeable.get()
               );
               ability.markDirty();
               player.manasCore$sync(true);
            }
         }
      }
   }

   public void toggleAbility(Player player) {
      Optional<ManasSkillInstance> optional = SkillAPI.getSkillsFrom(player).getSkill(this.location);
      if (!optional.isEmpty()) {
         ManasSkillInstance instance = optional.get();
         Changeable<ManasSkillInstance> changeable = Changeable.of(instance);
         if (!((SkillToggleEvent)SkillEvents.TOGGLE_SKILL.invoker()).toggleSkill(changeable, player).isFalse()) {
            ((ManasSkillInstance)changeable.get()).setToggled(!((ManasSkillInstance)changeable.get()).isToggled());
            if (((ManasSkillInstance)changeable.get()).isToggled()) {
               ((ManasSkillInstance)changeable.get()).onToggleOn(player);
               player.level()
                  .playSound(
                     player, player.getX(), player.getY(), player.getZ(), (SoundEvent)TensuraSoundEvents.BUFF_ACTIVATE.get(), SoundSource.PLAYERS, 0.75F, 1.0F
                  );
            } else {
               ((ManasSkillInstance)changeable.get()).onToggleOff(player);
               player.level()
                  .playSound(
                     player,
                     player.getX(),
                     player.getY(),
                     player.getZ(),
                     (SoundEvent)TensuraSoundEvents.BUFF_DEACTIVATE.get(),
                     SoundSource.PLAYERS,
                     0.75F,
                     1.0F
                  );
            }
         }
      }
   }

   public void lockAbility(Player player) {
      Optional<ManasSkillInstance> optional = SkillAPI.getSkillsFrom(player).getSkill(this.location);
      if (!optional.isEmpty()) {
         ManasSkillInstance instance = optional.get();
         ITensuraPlayer playerData = TensuraStorages.getPlayerDataFrom(player);
         int per = player.level().getGameRules().getInt(TensuraGameRules.RESET_PER_SKILL_LOCK);
         if (playerData.getBonusSkillLock() > 0 || per > 0) {
            if (playerData.getLockedSkills().contains(instance.getSkillId())) {
               playerData.removeLockedSkill(instance.getSkillId());
               playerData.markDirty();
            } else {
               int count = per > 0 ? playerData.getBonusSkillLock() + playerData.getResetCounter() / per : playerData.getBonusSkillLock();
               if (playerData.getLockedSkills().size() < count) {
                  if (instance.getMastery() >= 0.0 && !instance.isTemporarySkill()) {
                     if (!getLockableSkills(ReincarnationMenu.getSkillPool(), player.level()).contains(instance.getSkill())) {
                        player.sendSystemMessage(
                           Component.translatable("tensura.command.reset_counter.lock.unavailable", new Object[]{instance.getChatDisplayName(true)})
                              .withStyle(ChatFormatting.RED)
                        );
                        player.level()
                           .playSound(
                              player,
                              player.getX(),
                              player.getY(),
                              player.getZ(),
                              (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                              SoundSource.PLAYERS,
                              0.75F,
                              1.0F
                           );
                        return;
                     }

                     playerData.addLockedSkill(instance.getSkillId());
                     playerData.markDirty();
                  }
               }
            }
         }
      }
   }

   public void changePreset(Player player) {
      IAbility ability = TensuraStorages.getAbilityFrom(player);
      if (this.alternative) {
         int active = ability.getActivePreset();
         int newPreset = (int)(active + this.presetDelta);
         if (newPreset > 8) {
            newPreset = 0;
         }

         if (newPreset < 0) {
            newPreset = 8;
         }

         if (newPreset != active) {
            ability.setActivePreset(newPreset);
            ability.markDirty();
         }

         player.displayClientMessage(
            Component.translatable("tensura.skill.preset.changed", new Object[]{ability.getPresetName(newPreset)})
               .setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)),
            true
         );
      } else if (this.presetDelta < 10.0 && this.presetDelta > 0.0) {
         int key = (int)this.presetDelta - 1;
         if (ability.getActivePreset() != key) {
            ability.setActivePreset(key);
            ability.markDirty();
            player.displayClientMessage(
               Component.translatable("tensura.skill.preset.changed", new Object[]{ability.getPresetName(key)})
                  .setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)),
               true
            );
         } else {
            player.displayClientMessage(
               Component.translatable("tensura.skill.preset.no_change", new Object[]{ability.getPresetName(key)}).withStyle(ChatFormatting.RED), true
            );
         }
      }
   }

   public void renamePreset(Player player) {
      IAbility abilityData = TensuraStorages.getAbilityFrom(player);
      abilityData.setPresetName(this.presetId, this.presetName);
   }

   public static List<ManasSkill> getLockableSkills(List<ManasSkill> pool, Level pLevel) {
      return pool.stream()
         .filter(
            skill -> {
               if (pLevel instanceof ServerLevel serverLevel) {
                  return !serverLevel.getGameRules().getBoolean(TensuraGameRules.TRULY_UNIQUE)
                     ? true
                     : !TensuraStorages.getUniqueStorageFrom(serverLevel.getServer().overworld()).hasSkill(skill.getRegistryName());
               } else {
                  return true;
               }
            }
         )
         .toList();
   }

   public enum Action {
      CHANGE_MODE,
      CHANGE_PRESET,
      RENAME_PRESET,
      CHANGE_ABILITY,
      TOGGLE_ABILITY,
      LOCK_ABILITY,
      REMOVE_SUB_MODE;
   }
}
