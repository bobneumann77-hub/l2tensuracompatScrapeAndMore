package io.github.manasmods.tensura.neoforge.data.tag;

import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.impl.data.RaceTagProvider;
import io.github.manasmods.tensura.data.TensuraRaceTags;
import io.github.manasmods.tensura.registry.race.TensuraRaces;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;

public class TensuraRaceTagProvider extends RaceTagProvider {
   public TensuraRaceTagProvider(PackOutput output, CompletableFuture<Provider> lookupProvider) {
      super(output, lookupProvider);
   }

   protected void addTags(Provider arg) {
      this.tag(TensuraRaceTags.CAN_GLIDE)
         .add(
            new ManasRace[]{
               (ManasRace)TensuraRaces.HARPY.get(),
               (ManasRace)TensuraRaces.HARPY_QUEEN.get(),
               (ManasRace)TensuraRaces.SPIRIT_BIRD.get(),
               (ManasRace)TensuraRaces.DIVINE_BIRD.get(),
               (ManasRace)TensuraRaces.DRAGONEWT.get(),
               (ManasRace)TensuraRaces.TRUE_DRAGONEWT.get(),
               (ManasRace)TensuraRaces.DIVINE_DRAGON.get()
            }
         );
      this.tag(TensuraRaceTags.CAN_BREATH_WATER)
         .add(
            new ManasRace[]{
               (ManasRace)TensuraRaces.MERFOLK.get(),
               (ManasRace)TensuraRaces.ENLIGHTENED_MERFOLK.get(),
               (ManasRace)TensuraRaces.MERFOLK_SAINT.get(),
               (ManasRace)TensuraRaces.DIVINE_FISH.get()
            }
         );
      this.tag(TensuraRaceTags.HAS_CREATIVE_FLIGHT)
         .add(
            new ManasRace[]{
               (ManasRace)TensuraRaces.LESSER_DAEMON.get(),
               (ManasRace)TensuraRaces.GREATER_DAEMON.get(),
               (ManasRace)TensuraRaces.ARCH_DAEMON.get(),
               (ManasRace)TensuraRaces.DAEMON_LORD.get(),
               (ManasRace)TensuraRaces.DEVIL_LORD.get()
            }
         );
      this.tag(TensuraRaceTags.NEED_MOIST)
         .add(
            new ManasRace[]{
               (ManasRace)TensuraRaces.MERFOLK.get(),
               (ManasRace)TensuraRaces.ENLIGHTENED_MERFOLK.get(),
               (ManasRace)TensuraRaces.MERFOLK_SAINT.get(),
               (ManasRace)TensuraRaces.DIVINE_FISH.get()
            }
         );
      this.tag(TensuraRaceTags.UNABLE_TO_HEAL_WITH_FOOD)
         .add(
            new ManasRace[]{
               (ManasRace)TensuraRaces.GHOUL.get(),
               (ManasRace)TensuraRaces.VAMPIRE.get(),
               (ManasRace)TensuraRaces.VAMPIRE_OVERCOMER.get(),
               (ManasRace)TensuraRaces.VAMPIRE_LORD.get(),
               (ManasRace)TensuraRaces.DIVINE_VAMPIRE.get()
            }
         );
      this.tag(TensuraRaceTags.SPAWN_AS_SPIRITUAL)
         .add(
            new ManasRace[]{
               (ManasRace)TensuraRaces.LESSER_DAEMON.get(), (ManasRace)TensuraRaces.GREATER_DAEMON.get(), (ManasRace)TensuraRaces.ARCH_DAEMON.get()
            }
         );
      this.tag(TensuraRaceTags.LIMITED_EP_IN_CENTRAL)
         .add(
            new ManasRace[]{
               (ManasRace)TensuraRaces.LESSER_DAEMON.get(),
               (ManasRace)TensuraRaces.GREATER_DAEMON.get(),
               (ManasRace)TensuraRaces.ARCH_DAEMON.get(),
               (ManasRace)TensuraRaces.DAEMON_LORD.get()
            }
         );
      this.tag(TensuraRaceTags.HUMAN_LIKE)
         .add(
            new ManasRace[]{
               (ManasRace)TensuraRaces.HUMAN.get(),
               (ManasRace)TensuraRaces.DWARF.get(),
               (ManasRace)TensuraRaces.ELF.get(),
               (ManasRace)TensuraRaces.MERFOLK.get(),
               (ManasRace)TensuraRaces.BEASTFOLK.get()
            }
         );
      this.tag(TensuraRaceTags.NECROMANCER)
         .add(
            new ManasRace[]{
               (ManasRace)TensuraRaces.WIGHT.get(),
               (ManasRace)TensuraRaces.WIGHT_KING.get(),
               (ManasRace)TensuraRaces.SPIRIT_SKELETON.get(),
               (ManasRace)TensuraRaces.DIVINE_SKELETON.get()
            }
         );
      this.tag(TensuraRaceTags.UNDEAD)
         .add(
            new ManasRace[]{
               (ManasRace)TensuraRaces.GHOUL.get(),
               (ManasRace)TensuraRaces.WIGHT.get(),
               (ManasRace)TensuraRaces.WIGHT_KING.get(),
               (ManasRace)TensuraRaces.SPIRIT_SKELETON.get(),
               (ManasRace)TensuraRaces.DIVINE_SKELETON.get()
            }
         );
      this.tag(TensuraRaceTags.SPIRITUAL)
         .addTag(TensuraRaceTags.DIVINE)
         .addTag(TensuraRaceTags.DAEMON)
         .add(
            new ManasRace[]{
               (ManasRace)TensuraRaces.HUMAN_SAINT.get(),
               (ManasRace)TensuraRaces.ELF_SAINT.get(),
               (ManasRace)TensuraRaces.DWARF_SAINT.get(),
               (ManasRace)TensuraRaces.MERFOLK_SAINT.get(),
               (ManasRace)TensuraRaces.HOBGOBLIN_SAINT.get(),
               (ManasRace)TensuraRaces.TRUE_DRAGONEWT.get(),
               (ManasRace)TensuraRaces.SPIRIT_BEAST.get(),
               (ManasRace)TensuraRaces.SPIRIT_BIRD.get(),
               (ManasRace)TensuraRaces.SPIRIT_BOAR.get(),
               (ManasRace)TensuraRaces.SPIRIT_ONI.get(),
               (ManasRace)TensuraRaces.SPIRIT_SKELETON.get(),
               (ManasRace)TensuraRaces.DEATH_ONI.get(),
               (ManasRace)TensuraRaces.VAMPIRE_LORD.get(),
               (ManasRace)TensuraRaces.DEMON_SLIME.get()
            }
         );
      this.tag(TensuraRaceTags.DIVINE)
         .add(
            new ManasRace[]{
               (ManasRace)TensuraRaces.DIVINE_HUMAN.get(),
               (ManasRace)TensuraRaces.DIVINE_ELF.get(),
               (ManasRace)TensuraRaces.DIVINE_DWARF.get(),
               (ManasRace)TensuraRaces.DIVINE_BEAST.get(),
               (ManasRace)TensuraRaces.DIVINE_BIRD.get(),
               (ManasRace)TensuraRaces.DIVINE_BOAR.get(),
               (ManasRace)TensuraRaces.DIVINE_DRAGON.get(),
               (ManasRace)TensuraRaces.DIVINE_FISH.get(),
               (ManasRace)TensuraRaces.DIVINE_GIANT.get(),
               (ManasRace)TensuraRaces.DIVINE_ONI.get(),
               (ManasRace)TensuraRaces.DIVINE_FIGHTER.get(),
               (ManasRace)TensuraRaces.DIVINE_VAMPIRE.get(),
               (ManasRace)TensuraRaces.DIVINE_SKELETON.get(),
               (ManasRace)TensuraRaces.GOD_SLIME.get(),
               (ManasRace)TensuraRaces.DEVIL_LORD.get()
            }
         );
      this.tag(TensuraRaceTags.NO_BLOOD)
         .addTag(TensuraRaceTags.SPIRITUAL)
         .addTag(TensuraRaceTags.UNDEAD)
         .add(new ManasRace[]{(ManasRace)TensuraRaces.SLIME.get(), (ManasRace)TensuraRaces.METAL_SLIME.get()});
      this.tag(TensuraRaceTags.COLD_BLOODED)
         .addTag(TensuraRaceTags.SPIRITUAL)
         .addTag(TensuraRaceTags.UNDEAD)
         .add(
            new ManasRace[]{
               (ManasRace)TensuraRaces.SLIME.get(),
               (ManasRace)TensuraRaces.METAL_SLIME.get(),
               (ManasRace)TensuraRaces.GOD_SLIME.get(),
               (ManasRace)TensuraRaces.LIZARDMAN.get(),
               (ManasRace)TensuraRaces.DRAGONEWT.get()
            }
         );
      this.tag(TensuraRaceTags.BEASTFOLK)
         .add(
            new ManasRace[]{
               (ManasRace)TensuraRaces.BEASTFOLK.get(),
               (ManasRace)TensuraRaces.BEAST_LORD.get(),
               (ManasRace)TensuraRaces.SPIRIT_BEAST.get(),
               (ManasRace)TensuraRaces.DIVINE_BEAST.get()
            }
         );
      this.tag(TensuraRaceTags.DAEMON)
         .add(
            new ManasRace[]{
               (ManasRace)TensuraRaces.LESSER_DAEMON.get(),
               (ManasRace)TensuraRaces.GREATER_DAEMON.get(),
               (ManasRace)TensuraRaces.ARCH_DAEMON.get(),
               (ManasRace)TensuraRaces.DAEMON_LORD.get(),
               (ManasRace)TensuraRaces.DEVIL_LORD.get()
            }
         );
      this.tag(TensuraRaceTags.SLIME)
         .add(
            new ManasRace[]{
               (ManasRace)TensuraRaces.SLIME.get(),
               (ManasRace)TensuraRaces.METAL_SLIME.get(),
               (ManasRace)TensuraRaces.DEMON_SLIME.get(),
               (ManasRace)TensuraRaces.GOD_SLIME.get()
            }
         );
   }
}
