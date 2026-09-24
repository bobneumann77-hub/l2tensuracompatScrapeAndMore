package io.github.manasmods.tensura.network.c2s;

import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.utils.Env;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.data.TensuraSkillTags;
import io.github.manasmods.tensura.item.misc.MagicTomeItem;
import io.github.manasmods.tensura.item.weapon.spell.SimpleSpellCastItem;
import io.github.manasmods.tensura.menu.SpellbindingMenu;
import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import io.github.manasmods.tensura.registry.item.misc.TensuraDataComponents;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.WeakHashMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.NotNull;

public record RequestSpellbindingPacket(ResourceLocation id, boolean adding) implements CustomPacketPayload {
   public static final Type<RequestSpellbindingPacket> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("tensura", "request_spellbinding"));
   public static final StreamCodec<FriendlyByteBuf, RequestSpellbindingPacket> STREAM_CODEC = CustomPacketPayload.codec(
      RequestSpellbindingPacket::encode, RequestSpellbindingPacket::new
   );
   private static final Map<UUID, Integer> LAST_SPELLBIND_TICK = Collections.synchronizedMap(new WeakHashMap<>());

   public RequestSpellbindingPacket(FriendlyByteBuf buf) {
      this(buf.readResourceLocation(), buf.readBoolean());
   }

   public void encode(FriendlyByteBuf buf) {
      buf.writeResourceLocation(this.id());
      buf.writeBoolean(this.adding());
   }

   public void handle(PacketContext context) {
      if (context.getEnvironment() == Env.SERVER) {
         context.queue(
            () -> {
               Player sender = context.getPlayer();
               if (sender != null && sender.containerMenu instanceof SpellbindingMenu menu) {
                  if (sender instanceof ServerPlayer player) {
                     int now = player.tickCount;
                     UUID uuid = player.getUUID();
                     Integer last = LAST_SPELLBIND_TICK.get(uuid);
                     if (last != null && now >= last && now - last < 2) {
                        return;
                     }

                     LAST_SPELLBIND_TICK.put(uuid, now);
                  }

                  ItemStack stack = menu.getBlockEntity().getItem(0);
                  if (!stack.isEmpty() && stack.is(TensuraItemTags.SPELL_BINDABLE)) {
                     ManasSkill skill = (ManasSkill)SkillAPI.getSkillRegistry().get(this.id());
                     if (skill != null) {
                        Optional<ManasSkillInstance> optional = menu.getAbilities().stream().filter(test -> test.getSkill().equals(skill)).findFirst();
                        if (!optional.isEmpty()) {
                           ManasSkillInstance instance = optional.get();
                           if (!stack.is((Item)TensuraMaterialItems.UNBOUND_TOME.get())) {
                              List<ResourceLocation> skills = (List<ResourceLocation>)stack.get((DataComponentType)TensuraDataComponents.SKILL_LIST.get());
                              int slot = SimpleSpellCastItem.getMagicSlots(sender.level(), stack);
                              if (skills == null) {
                                 if (!this.adding() || slot <= 0) {
                                    return;
                                 }

                                 if (!instance.is(TensuraSkillTags.MAGIC) || instance.is(TensuraSkillTags.UNBINDABLE_MAGIC) || instance.getMastery() < 0.0) {
                                    return;
                                 }

                                 Changeable<ManasSkillInstance> instanceChangeable = Changeable.of(instance);
                                 Changeable<Integer> modeChangeable = Changeable.of(0);
                                 if (skill instanceof TensuraSkill tensuraSkill) {
                                    if (!tensuraSkill.canBeSlotted(instance, sender, 0)) {
                                       return;
                                    }

                                    if (!tensuraSkill.onAbilityEquipped(sender, instanceChangeable, modeChangeable, Changeable.of(0), Changeable.of(0))) {
                                       return;
                                    }
                                 }

                                 List<ResourceLocation> locations = List.of(((ManasSkillInstance)instanceChangeable.get()).getSkillId());
                                 stack.set((DataComponentType)TensuraDataComponents.MODE.get(), (Integer)modeChangeable.get());
                                 if (!stack.has((DataComponentType)TensuraDataComponents.SKILL.get())) {
                                    stack.set((DataComponentType)TensuraDataComponents.SKILL.get(), locations.getFirst());
                                 }

                                 stack.set((DataComponentType)TensuraDataComponents.SKILL_LIST.get(), locations);
                                 sender.playNotifySound((SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                                 if (stack.getItem() instanceof SimpleSpellCastItem castItem) {
                                    castItem.onPostMagicBinding(sender, stack);
                                 }
                              } else {
                                 List<ResourceLocation> skillIds = new ArrayList<>(skills);
                                 if (this.adding()) {
                                    if (!instance.is(TensuraSkillTags.MAGIC) || instance.is(TensuraSkillTags.UNBINDABLE_MAGIC) || instance.getMastery() < 0.0) {
                                       return;
                                    }

                                    if (!skillIds.contains(instance.getSkillId()) && skillIds.size() < slot) {
                                       Changeable<ManasSkillInstance> instanceChangeable = Changeable.of(instance);
                                       Changeable<Integer> modeChangeable = Changeable.of(
                                          (Integer)stack.getOrDefault((DataComponentType)TensuraDataComponents.MODE.get(), 0)
                                       );
                                       if (skill instanceof TensuraSkill tensuraSkill) {
                                          if (!tensuraSkill.canBeSlotted(instance, sender, 0)) {
                                             return;
                                          }

                                          if (!tensuraSkill.onAbilityEquipped(sender, instanceChangeable, modeChangeable, Changeable.of(0), Changeable.of(0))) {
                                             return;
                                          }
                                       }

                                       skillIds.add(((ManasSkillInstance)instanceChangeable.get()).getSkillId());
                                       stack.set((DataComponentType)TensuraDataComponents.SKILL_LIST.get(), skillIds);
                                       sender.playNotifySound((SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                                       if (stack.getItem() instanceof SimpleSpellCastItem castItem) {
                                          castItem.onPostMagicBinding(sender, stack);
                                       }
                                    }
                                 } else if (skillIds.contains(instance.getSkillId())) {
                                    ResourceLocation selected = (ResourceLocation)stack.get((DataComponentType)TensuraDataComponents.SKILL.get());
                                    if (Objects.equals(selected, instance.getSkillId())) {
                                       stack.remove((DataComponentType)TensuraDataComponents.SKILL.get());
                                    }

                                    skillIds.removeIf(location -> Objects.equals(location, instance.getSkillId()));
                                    stack.set((DataComponentType)TensuraDataComponents.SKILL_LIST.get(), skillIds);
                                    sender.playNotifySound((SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                                    if (stack.getItem() instanceof SimpleSpellCastItem castItem) {
                                       castItem.onPostMagicBinding(sender, stack);
                                    }

                                    if (stack.has(DataComponents.CUSTOM_DATA)) {
                                       CompoundTag tag = ((CustomData)stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)).copyTag();
                                       if (tag.contains(instance.getSkillId().toString())) {
                                          tag.remove(instance.getSkillId().toString());
                                          if (tag.isEmpty()) {
                                             stack.remove(DataComponents.CUSTOM_DATA);
                                          } else {
                                             stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
                                          }
                                       }
                                    }
                                 }
                              }
                           } else if (instance.is(TensuraSkillTags.MAGIC) && !instance.is(TensuraSkillTags.UNBINDABLE_MAGIC) && !(instance.getMastery() < 0.0)) {
                              if (this.adding() && instance.isMastered(sender) && !instance.is(TensuraSkillTags.TOME_COPY_EXCLUDED)) {
                                 menu.getBlockEntity().setItem(0, MagicTomeItem.createForMagic(instance.getSkill()));
                                 sender.playNotifySound((SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                              }
                           }
                        }
                     }
                  }
               }
            }
         );
      }
   }

   @NotNull
   public Type<RequestSpellbindingPacket> type() {
      return TYPE;
   }
}
