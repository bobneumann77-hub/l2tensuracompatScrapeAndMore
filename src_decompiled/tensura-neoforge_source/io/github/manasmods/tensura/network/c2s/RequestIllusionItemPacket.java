package io.github.manasmods.tensura.network.c2s;

import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.utils.Env;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.manascore.skill.api.Skills;
import io.github.manasmods.tensura.registry.item.misc.TensuraDataComponents;
import java.util.Optional;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record RequestIllusionItemPacket(ResourceLocation skill, CompoundTag stack, EquipmentSlot slot) implements CustomPacketPayload {
   public static final Type<RequestIllusionItemPacket> TYPE = new Type(ResourceLocation.fromNamespaceAndPath("tensura", "request_illusion_item"));
   public static final StreamCodec<FriendlyByteBuf, RequestIllusionItemPacket> STREAM_CODEC = CustomPacketPayload.codec(
      RequestIllusionItemPacket::encode, RequestIllusionItemPacket::new
   );

   public RequestIllusionItemPacket(FriendlyByteBuf buf) {
      this(buf.readResourceLocation(), (CompoundTag)buf.readNbt(NbtAccounter.create(8192L)), (EquipmentSlot)buf.readEnum(EquipmentSlot.class));
   }

   public static RequestIllusionItemPacket getDefault(ManasSkill skill, @Nullable CompoundTag stack, EquipmentSlot slot) {
      return new RequestIllusionItemPacket(skill.getRegistryName(), stack == null ? new CompoundTag() : stack, slot);
   }

   public void encode(FriendlyByteBuf buf) {
      buf.writeResourceLocation(this.skill);
      buf.writeNbt(this.stack);
      buf.writeEnum(this.slot);
   }

   public void handle(PacketContext context) {
      if (context.getEnvironment() == Env.SERVER) {
         context.queue(() -> {
            Player sender = context.getPlayer();
            if (sender != null) {
               Skills storage = SkillAPI.getSkillsFrom(sender);
               Optional<ManasSkillInstance> optional = storage.getSkill(this.skill);
               if (!optional.isEmpty()) {
                  ManasSkillInstance instance = optional.get();
                  CompoundTag tag = instance.getOrCreateTag();
                  tag.put(this.slot.getSerializedName(), this.stack);
                  instance.markDirty();
                  storage.markDirty();
               }
            }
         });
      }
   }

   @NotNull
   public Type<RequestIllusionItemPacket> type() {
      return TYPE;
   }

   public static ItemStack getFalsifierItemForScreen(LivingEntity pLivingEntity, ManasSkill skill, EquipmentSlot slot) {
      ItemStack original = pLivingEntity.getItemBySlot(slot);
      Skills storage = SkillAPI.getSkillsFrom(pLivingEntity);
      Optional<ManasSkillInstance> optional = storage.getSkill(skill);
      if (optional.isEmpty()) {
         return original;
      } else {
         CompoundTag tag = optional.get().getTag();
         if (tag != null && tag.contains(slot.getName())) {
            ItemStack itemStack = parseCompound(pLivingEntity.registryAccess(), tag.getCompound(slot.getSerializedName()));
            return itemStack == null ? original : itemStack;
         } else {
            return original;
         }
      }
   }

   public static ItemStack getFalsifierItem(LivingEntity pLivingEntity, ManasSkill skill, ItemStack original, EquipmentSlot slot) {
      Skills storage = SkillAPI.getSkillsFrom(pLivingEntity);
      Optional<ManasSkillInstance> optional = storage.getSkill(skill);
      if (optional.isEmpty()) {
         return original;
      }

      CompoundTag tag = optional.get().getTag();
      if (tag != null && tag.contains(slot.getName())) {
         ItemStack itemStack = parseCompound(pLivingEntity.registryAccess(), tag.getCompound(slot.getSerializedName()));
         if (itemStack == null) {
            return original;
         } else {
            return itemStack.has((DataComponentType)TensuraDataComponents.DUMMY_ITEM.get()) ? new ItemStack(Items.AIR) : itemStack;
         }
      } else {
         return original;
      }
   }

   public static ItemStack getEmptyStack() {
      ItemStack stack = new ItemStack(Items.BARRIER);
      stack.set((DataComponentType)TensuraDataComponents.DUMMY_ITEM.get(), true);
      return stack;
   }

   @Nullable
   public static ItemStack parseCompound(Provider provider, CompoundTag tag) {
      if (tag.getBoolean(TensuraDataComponents.DUMMY_ITEM.getRegisteredName())) {
         ItemStack stack = new ItemStack(Items.BARRIER);
         stack.set((DataComponentType)TensuraDataComponents.DUMMY_ITEM.get(), true);
         return stack;
      } else {
         Optional<ItemStack> stack = ItemStack.CODEC.parse(provider.createSerializationContext(NbtOps.INSTANCE), tag).result();
         return stack.orElse(null);
      }
   }
}
