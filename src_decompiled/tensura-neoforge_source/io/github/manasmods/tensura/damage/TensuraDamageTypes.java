package io.github.manasmods.tensura.damage;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class TensuraDamageTypes {
   public static final ResourceKey<DamageType> DARKNESS_ELEMENTAL = createElemental("darkness");
   public static final ResourceKey<DamageType> EARTH_ELEMENTAL = createElemental("earth");
   public static final ResourceKey<DamageType> FIRE_ELEMENTAL = createElemental("fire");
   public static final ResourceKey<DamageType> GRAVITY_ELEMENTAL = createElemental("gravity");
   public static final ResourceKey<DamageType> ICE_ELEMENTAL = createElemental("ice");
   public static final ResourceKey<DamageType> LIGHT_ELEMENTAL = createElemental("light");
   public static final ResourceKey<DamageType> LIGHTNING_ELEMENTAL = createElemental("lightning");
   public static final ResourceKey<DamageType> SPACE_ELEMENTAL = createElemental("space");
   public static final ResourceKey<DamageType> WATER_ELEMENTAL = createElemental("water");
   public static final ResourceKey<DamageType> WIND_ELEMENTAL = createElemental("wind");
   public static final ResourceKey<DamageType> CORROSION = create("corrosion");
   public static final ResourceKey<DamageType> CURSE = create("curse");
   public static final ResourceKey<DamageType> FATAL_POISON = create("fatal_poison");
   public static final ResourceKey<DamageType> FEAR = create("fear");
   public static final ResourceKey<DamageType> HOLY_DAMAGE = create("holy_damage");
   public static final ResourceKey<DamageType> INFECTION = create("infection");
   public static final ResourceKey<DamageType> INSANITY = create("insanity");
   public static final ResourceKey<DamageType> MAGICULE_POISON = create("magicule_poison");
   public static final ResourceKey<DamageType> PETRIFICATION = create("petrification");
   public static final ResourceKey<DamageType> SOUL_SCATTER = create("soul_scatter");
   public static final ResourceKey<DamageType> SUFFOCATE = create("suffocate");
   public static final ResourceKey<DamageType> BULLET = create("bullet");
   public static final ResourceKey<DamageType> BULLET_MAGIC = create("bullet_magic");
   public static final ResourceKey<DamageType> KUNAI = create("kunai");
   public static final ResourceKey<DamageType> SPEAR = create("spear");
   public static final ResourceKey<DamageType> SEVERER_BLADE = create("severer_blade");
   public static final ResourceKey<DamageType> TEMPEST_SCALE = create("tempest_scale");
   public static final ResourceKey<DamageType> UNICORN_HORN = create("unicorn_horn");
   public static final ResourceKey<DamageType> AURA_BULLET = create("aura_bullet");
   public static final ResourceKey<DamageType> AURA_SLASH = create("aura_slash");
   public static final ResourceKey<DamageType> BLACK_FLAME = create("black_flame");
   public static final ResourceKey<DamageType> BLACK_LIGHTNING = create("black_lightning");
   public static final ResourceKey<DamageType> BLOOD_DRAIN = create("blood_drain");
   public static final ResourceKey<DamageType> BLOOD_RAY = create("blood_ray");
   public static final ResourceKey<DamageType> BURN = create("burn");
   public static final ResourceKey<DamageType> DEATH_TORNADO = create("tornado");
   public static final ResourceKey<DamageType> DEATH_BLESS = create("death_bless");
   public static final ResourceKey<DamageType> DEATH_WISH = create("death_wish");
   public static final ResourceKey<DamageType> DEVOURED = create("devoured");
   public static final ResourceKey<DamageType> DIMENSION_RAY = create("dimension_ray");
   public static final ResourceKey<DamageType> DISINTEGRATION = create("disintegration");
   public static final ResourceKey<DamageType> DROWSY_DEATH = create("drowsy");
   public static final ResourceKey<DamageType> ENERGY_DRAIN = create("energy_drain");
   public static final ResourceKey<DamageType> ENERGY_SOURCE_LOST = create("energy_source_lost");
   public static final ResourceKey<DamageType> FLAME_BREATH = create("flame_breath");
   public static final ResourceKey<DamageType> GRAVITY_EXPLODE = create("gravity_explode");
   public static final ResourceKey<DamageType> GRAVITY_PRESS = create("gravity_press");
   public static final ResourceKey<DamageType> HAZY_BLOSSOM_THRUST = create("hazy_blossom_thrust");
   public static final ResourceKey<DamageType> HEAT_WAVE = create("heat_wave");
   public static final ResourceKey<DamageType> HEART_EAT = create("heart_eat");
   public static final ResourceKey<DamageType> ICE_BREATH = create("ice_breath");
   public static final ResourceKey<DamageType> INFINITE_EATER = create("infinite_eater");
   public static final ResourceKey<DamageType> LIGHTNING = create("lightning");
   public static final ResourceKey<DamageType> MAGIC_GENERIC = create("magic");
   public static final ResourceKey<DamageType> MIND_CRUSH = create("mind_crush");
   public static final ResourceKey<DamageType> MIND_REQUIEM = create("mind_requiem");
   public static final ResourceKey<DamageType> MEGIDDO = create("megiddo");
   public static final ResourceKey<DamageType> PARALYZING = create("paralyzing");
   public static final ResourceKey<DamageType> POISONOUS_BREATH = create("poisonous_breath");
   public static final ResourceKey<DamageType> REFLECTED = create("reflected");
   public static final ResourceKey<DamageType> SOUL_CONSUMED = create("soul_consumed");
   public static final ResourceKey<DamageType> SEVERANCE = create("severance");
   public static final ResourceKey<DamageType> SOUND_BLAST = create("sound_blast");
   public static final ResourceKey<DamageType> SYNTHESISE = create("synthesise");
   public static final ResourceKey<DamageType> STEEL_THREAD = create("steel_thread");
   public static final ResourceKey<DamageType> SUICIDE = create("suicide");
   public static final ResourceKey<DamageType> THUNDER_BREATH = create("thunder_breath");
   public static final ResourceKey<DamageType> WATER_BLADE = create("water_blade");
   public static final ResourceKey<DamageType> WATER_BREATH = create("water_breath");
   public static final ResourceKey<DamageType> WICKED_LIGHT_RAY = create("wicked_light_ray");
   public static final ResourceKey<DamageType> WIND_BREATH = create("wind_breath");

   public static ResourceKey<DamageType> create(String name) {
      return ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath("tensura", name));
   }

   public static ResourceKey<DamageType> createElemental(String name) {
      return ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath("tensura", name + "_elemental"));
   }

   public static DamageSource getDamageSource(Level level, ResourceKey<DamageType> type) {
      return getEntityDamageSource(level, type, null);
   }

   public static DamageSource getEntityDamageSource(Level level, ResourceKey<DamageType> type, @Nullable Entity attacker) {
      return getIndirectEntityDamageSource(level, type, attacker, attacker);
   }

   public static DamageSource getIndirectEntityDamageSource(Level level, ResourceKey<DamageType> type, @Nullable Entity source, @Nullable Entity projectile) {
      return type.location().getNamespace().equals("tensura")
         ? new SourceMessageDamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(type), projectile, source)
         : new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(type), projectile, source);
   }

   public static void bootstrap(BootstrapContext<DamageType> context) {
      createDamageType(context, DARKNESS_ELEMENTAL);
      createDamageType(context, EARTH_ELEMENTAL);
      createDamageType(context, FIRE_ELEMENTAL, DamageEffects.BURNING);
      createDamageType(context, GRAVITY_ELEMENTAL);
      createDamageType(context, ICE_ELEMENTAL, DamageEffects.FREEZING);
      createDamageType(context, LIGHT_ELEMENTAL);
      createDamageType(context, LIGHTNING_ELEMENTAL);
      createDamageType(context, SPACE_ELEMENTAL);
      createDamageType(context, WATER_ELEMENTAL);
      createDamageType(context, WIND_ELEMENTAL);
      createDamageType(context, CORROSION);
      createDamageType(context, CURSE);
      createDamageType(context, FATAL_POISON);
      createDamageType(context, FEAR);
      createDamageType(context, HOLY_DAMAGE);
      createDamageType(context, INFECTION);
      createDamageType(context, INSANITY);
      createDamageType(context, MAGICULE_POISON);
      createDamageType(context, PETRIFICATION);
      createDamageType(context, SOUL_SCATTER);
      createDamageType(context, SUFFOCATE);
      createDamageType(context, BULLET);
      createDamageType(context, BULLET_MAGIC);
      createDamageType(context, KUNAI);
      createDamageType(context, SPEAR);
      createDamageType(context, SEVERER_BLADE);
      createDamageType(context, TEMPEST_SCALE);
      createDamageType(context, UNICORN_HORN);
      createDamageType(context, AURA_BULLET);
      createDamageType(context, AURA_SLASH);
      createDamageType(context, BLACK_FLAME, DamageEffects.BURNING);
      createDamageType(context, BLACK_LIGHTNING);
      createDamageType(context, BLOOD_DRAIN);
      createDamageType(context, BLOOD_RAY);
      createDamageType(context, BURN, DamageEffects.BURNING);
      createDamageType(context, DEATH_BLESS);
      createDamageType(context, DEATH_TORNADO);
      createDamageType(context, DEATH_WISH);
      createDamageType(context, DEVOURED);
      createDamageType(context, DIMENSION_RAY);
      createDamageType(context, DISINTEGRATION);
      createDamageType(context, DROWSY_DEATH);
      createDamageType(context, ENERGY_DRAIN);
      createDamageType(context, ENERGY_SOURCE_LOST);
      createDamageType(context, FLAME_BREATH, DamageEffects.BURNING);
      createDamageType(context, GRAVITY_EXPLODE);
      createDamageType(context, GRAVITY_PRESS);
      createDamageType(context, HAZY_BLOSSOM_THRUST);
      createDamageType(context, HEAT_WAVE, DamageEffects.BURNING);
      createDamageType(context, HEART_EAT);
      createDamageType(context, ICE_BREATH, DamageEffects.FREEZING);
      createDamageType(context, INFINITE_EATER);
      createDamageType(context, LIGHTNING);
      createDamageType(context, MAGIC_GENERIC);
      createDamageType(context, MIND_CRUSH);
      createDamageType(context, MIND_REQUIEM);
      createDamageType(context, MEGIDDO);
      createDamageType(context, PARALYZING);
      createDamageType(context, POISONOUS_BREATH);
      createDamageType(context, REFLECTED);
      createDamageType(context, SOUL_CONSUMED);
      createDamageType(context, SEVERANCE);
      createDamageType(context, SOUND_BLAST);
      createDamageType(context, SYNTHESISE);
      createDamageType(context, STEEL_THREAD);
      createDamageType(context, SUICIDE);
      createDamageType(context, THUNDER_BREATH);
      createDamageType(context, WATER_BLADE);
      createDamageType(context, WATER_BREATH);
      createDamageType(context, WICKED_LIGHT_RAY);
      createDamageType(context, WIND_BREATH);
   }

   public static void createDamageType(BootstrapContext<DamageType> context, ResourceKey<DamageType> key) {
      context.register(key, new DamageType(getMsgId(key), 0.1F));
   }

   public static void createDamageType(BootstrapContext<DamageType> context, ResourceKey<DamageType> key, DamageEffects effects) {
      context.register(key, new DamageType(getMsgId(key), 0.1F, effects));
   }

   public static String getMsgId(ResourceKey<DamageType> key) {
      return "tensura." + key.location().getPath();
   }
}
