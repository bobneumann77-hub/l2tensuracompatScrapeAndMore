package io.github.manasmods.tensura.network.c2s;

import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.utils.Env;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.manascore.skill.api.Skills;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.event.TensuraInputEvents;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public record RequestSkillNumberKeyPacket(int key, List<ResourceLocation> skillList) implements CustomPacketPayload {
   public static final Type<RequestSkillNumberKeyPacket> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("tensura", "request_skill_number_key"));
   public static final StreamCodec<FriendlyByteBuf, RequestSkillNumberKeyPacket> STREAM_CODEC = CustomPacketPayload.codec(
      RequestSkillNumberKeyPacket::encode, RequestSkillNumberKeyPacket::new
   );

   public RequestSkillNumberKeyPacket(FriendlyByteBuf buf) {
      this(buf.readInt(), validateList(buf.readList(FriendlyByteBuf::readResourceLocation)));
   }

   private static List<ResourceLocation> validateList(List<ResourceLocation> list) {
      int maxSize = 10;
      if (list.size() > maxSize) {
         throw new IllegalArgumentException("Skill list exceeds maximum size of " + maxSize);
      } else {
         return list;
      }
   }

   public void encode(FriendlyByteBuf buf) {
      buf.writeInt(this.key);
      buf.writeCollection(this.skillList, FriendlyByteBuf::writeResourceLocation);
   }

   public void handle(PacketContext context) {
      if (context.getEnvironment() == Env.SERVER) {
         context.queue(
            () -> {
               Player player = context.getPlayer();
               if (player != null) {
                  Skills storage = SkillAPI.getSkillsFrom(player);

                  for (ResourceLocation skillId : this.skillList) {
                     storage.getSkill(skillId)
                        .ifPresent(
                           skillInstance -> {
                              Changeable<ManasSkillInstance> skillChangeable = Changeable.of(skillInstance);
                              Changeable<Integer> keyChangeable = Changeable.of(this.key);
                              if (!((TensuraInputEvents.NumberKeyPressEvent)TensuraInputEvents.SKILL_NUMBER_KEY.invoker())
                                 .press(skillChangeable, player, keyChangeable)
                                 .isFalse()) {
                                 ManasSkillInstance skill = (ManasSkillInstance)skillChangeable.get();
                                 if (skill != null && !keyChangeable.isEmpty()) {
                                    if (skill.canInteractSkill(player)) {
                                       if (skill.getSkill() instanceof TensuraSkill tensuraSkill) {
                                          tensuraSkill.onNumberKeyPress(skillInstance, player, (Integer)keyChangeable.get());
                                       }

                                       skill.markDirty();
                                       storage.markDirty();
                                    }
                                 }
                              }
                           }
                        );
                  }
               }
            }
         );
      }
   }

   @NotNull
   public Type<RequestSkillNumberKeyPacket> type() {
      return TYPE;
   }
}
