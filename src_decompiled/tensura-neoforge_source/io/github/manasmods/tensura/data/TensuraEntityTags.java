package io.github.manasmods.tensura.data;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class TensuraEntityTags {
   public static TagKey<EntityType<?>> SLIME_WALKABLE_MOBS = modTag("slime_walkable_mobs");
   public static TagKey<EntityType<?>> WEB_WALKABLE_MOBS = modTag("web_walkable_mobs");
   public static TagKey<EntityType<?>> ANIMAL_PREY = modTag("animal_prey");
   public static TagKey<EntityType<?>> GUARD_PREY = modTag("guard_prey");
   public static TagKey<EntityType<?>> OTHERWORLDER_PREY = modTag("otherworlder_prey");
   public static TagKey<EntityType<?>> ORC_LORD_PREY = modTag("orc_lord_prey");
   public static TagKey<EntityType<?>> HELL_NEUTRAL = modTag("hell_neutral");
   public static TagKey<EntityType<?>> HINATA_NEUTRAL = modTag("hinata_neutral");
   public static TagKey<EntityType<?>> EP_INITIATE_EXCLUDED = modTag("ep_initiate_excluded");
   public static TagKey<EntityType<?>> EP_DROP_EXCLUDED = modTag("ep_drop_excluded");
   public static TagKey<EntityType<?>> SOUL_DROP_EXCLUDED = modTag("soul_drop_excluded");
   public static TagKey<EntityType<?>> HUMAN_LIKE = modTag("human_like");
   public static TagKey<EntityType<?>> NAMEABLE = modTag("can_be_named");
   public static TagKey<EntityType<?>> DROP_CRYSTAL = modTag("drop_crystal");
   public static TagKey<EntityType<?>> MONSTER = modTag("monster");
   public static TagKey<EntityType<?>> DAEMONS = modTag("daemons");
   public static TagKey<EntityType<?>> SLIMES = modTag("slimes");
   public static TagKey<EntityType<?>> CLONES = modTag("clones");
   public static TagKey<EntityType<?>> NON_LIVING = modTag("non_living");
   public static TagKey<EntityType<?>> SPIRIT_PROTECTOR = modTag("spirit_protector");
   public static TagKey<EntityType<?>> HERO_BOSS = modTag("boss_for_hero");
   public static TagKey<EntityType<?>> NO_FEAR = modTag("no_fear");
   public static TagKey<EntityType<?>> FULL_GRAVITY_CONTROL = modTag("full_gravity_control");
   public static TagKey<EntityType<?>> NO_CHARISMA = modTag("no_charisma");
   public static TagKey<EntityType<?>> NO_CHARM = modTag("no_charm");
   public static TagKey<EntityType<?>> NO_MIND_CONTROL = modTag("no_mind_control");
   public static TagKey<EntityType<?>> NO_POSSESSION = modTag("no_possession");
   public static TagKey<EntityType<?>> NO_SACRIFICE = modTag("no_sacrifice");
   public static TagKey<EntityType<?>> NO_SEVERANCE = modTag("no_severance");
   public static TagKey<EntityType<?>> NO_SYNTHESISE = modTag("no_synthesise");
   public static TagKey<EntityType<?>> NO_ENERGY_DRAIN = modTag("no_current_ep_drain");
   public static TagKey<EntityType<?>> NO_EP_PLUNDER = modTag("no_max_ep_plunder");
   public static TagKey<EntityType<?>> NO_FORCED_MOVE = modTag("no_forced_move");
   public static TagKey<EntityType<?>> NO_FORCED_WARP = modTag("no_forced_warp");
   public static TagKey<EntityType<?>> NO_SKILL_PLUNDER = modTag("no_skill_plunder");
   public static TagKey<EntityType<?>> NO_SPIRITUAL_DAMAGE = modTag("no_spiritual_damage");
   public static TagKey<EntityType<?>> CAN_DIE_IN_LABYRINTH = modTag("can_die_in_labyrinth");
   public static TagKey<EntityType<?>> SPIRITUAL = modTag("spiritual");
   public static TagKey<EntityType<?>> NO_TSUKUMOGAMI_UPDATE = modTag("no_tsukumogami_update");
   public static TagKey<EntityType<?>> NO_HIGHLIGHT = modTag("no_highlight");
   public static TagKey<EntityType<?>> NO_SOUND = modTag("no_sound");
   public static TagKey<EntityType<?>> NO_BLOOD = modTag("no_blood");
   public static TagKey<EntityType<?>> COLD_BLOODED = modTag("cold_blooded");
   public static TagKey<EntityType<?>> COLD_SOURCE = modTag("cold_source");
   public static TagKey<EntityType<?>> HOT_SOURCE = modTag("hot_source");
   public static TagKey<EntityType<?>> HOSTILE_MONSTER = modTag("hostile_monster");
   public static TagKey<EntityType<?>> NEUTRAL_MOB = modTag("neutral_monster");
   public static TagKey<EntityType<?>> TRAP_ENTITY = modTag("trap_entity");
   public static TagKey<EntityType<?>> TREASURE_ENTITY = modTag("treasure_entity");
   public static TagKey<EntityType<?>> CANNOT_DODGE = modTag("cannot_dodge");
   public static TagKey<EntityType<?>> CAN_WARP_SHOT = modTag("can_warp_shot");
   public static TagKey<EntityType<?>> CAN_STAY_INVISIBLE = modTag("can_stay_invisible");
   public static TagKey<EntityType<?>> CAN_EVAPORATE = modTag("can_evaporate");
   public static TagKey<EntityType<?>> CAN_DISTINGUISH = modTag("can_distinguish");

   static TagKey<EntityType<?>> modTag(String name) {
      return create(ResourceLocation.fromNamespaceAndPath("tensura", name));
   }

   static TagKey<EntityType<?>> create(ResourceLocation name) {
      return TagKey.create(Registries.ENTITY_TYPE, name);
   }
}
