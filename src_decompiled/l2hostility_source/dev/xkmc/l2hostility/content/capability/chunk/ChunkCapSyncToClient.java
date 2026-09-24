package dev.xkmc.l2hostility.content.capability.chunk;

import dev.xkmc.l2hostility.init.network.ClientSyncHandler;
import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.codec.TagCodec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public record ChunkCapSyncToClient(CompoundTag tag, ResourceLocation level, int x, int z) implements SerialPacketBase<ChunkCapSyncToClient> {
   public static ChunkCapSyncToClient of(ChunkCapHolder chunk) {
      ResourceLocation level = chunk.chunk().getLevel().dimension().location();
      int x = chunk.chunk().getPos().x;
      int z = chunk.chunk().getPos().z;
      CompoundTag tag = new TagCodec(chunk.chunk().getLevel().registryAccess()).toTag(new CompoundTag(), chunk.cap());
      return new ChunkCapSyncToClient(tag, level, x, z);
   }

   public void handle(Player player) {
      ClientSyncHandler.handleChunkUpdate(this.level, this.x, this.z, this.tag);
   }
}
