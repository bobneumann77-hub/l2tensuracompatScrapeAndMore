package io.github.manasmods.tensura.network.s2c;

import dev.architectury.networking.NetworkManager.PacketContext;
import dev.architectury.utils.Env;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.ResourceLocation;

public record OpenSpatialStorageMenuPayload(
   OpenSpatialStorageMenuPayload.StorageType storageType, int containerId, int size, int stackSize, int page, int entityId, ResourceLocation skill
) implements CustomPacketPayload {
   public static final Type<OpenSpatialStorageMenuPayload> TYPE = new Type(
      ResourceLocation.fromNamespaceAndPath("tensura", "open_spatial_storage_humanoid_menu")
   );
   public static final StreamCodec<FriendlyByteBuf, OpenSpatialStorageMenuPayload> STREAM_CODEC = CustomPacketPayload.codec(
      OpenSpatialStorageMenuPayload::encode, OpenSpatialStorageMenuPayload::new
   );

   public OpenSpatialStorageMenuPayload(FriendlyByteBuf buf) {
      this(
         (OpenSpatialStorageMenuPayload.StorageType)buf.readEnum(OpenSpatialStorageMenuPayload.StorageType.class),
         buf.readInt(),
         buf.readInt(),
         buf.readInt(),
         buf.readInt(),
         buf.readInt(),
         buf.readResourceLocation()
      );
   }

   public void encode(FriendlyByteBuf buf) {
      buf.writeEnum(this.storageType);
      buf.writeInt(this.containerId);
      buf.writeInt(this.size);
      buf.writeInt(this.stackSize);
      buf.writeInt(this.page);
      buf.writeInt(this.entityId);
      buf.writeResourceLocation(this.skill);
   }

   public void handle(PacketContext context) {
      if (context.getEnvironment() == Env.CLIENT) {
         switch (this.storageType()) {
            case DEFAULT:
               ClientAccess.handleOpenSpatialStorageMenuPayload(this);
               break;
            case SPATIAL_BAG:
               ClientAccess.handleOpenSpatialBagMenuPayload(this);
               break;
            case REFINING:
               ClientAccess.handleOpenRefiningMenuPayload(this);
               break;
            case REPEAT_CRAFTING:
               ClientAccess.handleOpenRepeatCraftingMenuPayload(this);
               break;
            case RESEARCHER:
               ClientAccess.handleOpenResearcherStorageMenuPayload(this);
         }
      }
   }

   public Type<OpenSpatialStorageMenuPayload> type() {
      return TYPE;
   }

   public enum StorageType {
      DEFAULT,
      SPATIAL_BAG,
      REFINING,
      REPEAT_CRAFTING,
      RESEARCHER;
   }
}
