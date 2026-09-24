package io.github.manasmods.tensura.data;

import io.github.manasmods.manascore.race.api.ManasRace;
import io.github.manasmods.manascore.race.api.RaceAPI;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public class TensuraRaceTags {
   public static final TagKey<ManasRace> CAN_GLIDE = modTag("can_glide");
   public static final TagKey<ManasRace> CAN_BREATH_WATER = modTag("can_breath_water");
   public static final TagKey<ManasRace> HAS_CREATIVE_FLIGHT = modTag("has_creative_flight");
   public static final TagKey<ManasRace> NEED_MOIST = modTag("need_moist");
   public static final TagKey<ManasRace> UNABLE_TO_HEAL_WITH_FOOD = modTag("unable_to_heal_with_food");
   public static final TagKey<ManasRace> SPAWN_AS_SPIRITUAL = modTag("spawn_as_spiritual");
   public static final TagKey<ManasRace> LIMITED_EP_IN_CENTRAL = modTag("limited_ep_in_central");
   public static final TagKey<ManasRace> HUMAN_LIKE = modTag("human_like");
   public static final TagKey<ManasRace> NECROMANCER = modTag("necromancer");
   public static final TagKey<ManasRace> UNDEAD = modTag("undead");
   public static final TagKey<ManasRace> SPIRITUAL = modTag("spiritual");
   public static final TagKey<ManasRace> DIVINE = modTag("divine");
   public static final TagKey<ManasRace> NO_BLOOD = modTag("no_blood");
   public static final TagKey<ManasRace> COLD_BLOODED = modTag("cold_blooded");
   public static final TagKey<ManasRace> BEASTFOLK = modTag("beastfolk");
   public static final TagKey<ManasRace> DAEMON = modTag("daemon");
   public static final TagKey<ManasRace> SLIME = modTag("slime");

   static TagKey<ManasRace> modTag(String name) {
      return create(ResourceLocation.fromNamespaceAndPath("tensura", name));
   }

   static TagKey<ManasRace> create(ResourceLocation name) {
      return TagKey.create(RaceAPI.getRaceRegistryKey(), name);
   }
}
