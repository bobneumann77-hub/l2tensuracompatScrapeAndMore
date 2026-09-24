package io.github.manasmods.tensura.registry.sound;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.JukeboxSong;

public class TensuraJukeboxSongs {
   public static final ResourceKey<JukeboxSong> NANODA = create("nanoda");

   public static void bootstrap(BootstrapContext<JukeboxSong> context) {
      context.register(NANODA, new JukeboxSong(TensuraSoundEvents.NANODA, Component.translatable("jukebox_song.tensura.nanoda"), 32.0F, 15));
   }

   public static ResourceKey<JukeboxSong> create(String name) {
      return ResourceKey.create(Registries.JUKEBOX_SONG, ResourceLocation.fromNamespaceAndPath("tensura", name));
   }
}
