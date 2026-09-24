package dev.xkmc.l2hostility.init.data;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import dev.xkmc.l2core.util.ConfigInit;
import dev.xkmc.l2core.util.ConfigInit.Builder;
import dev.xkmc.l2hostility.init.L2Hostility;
import dev.xkmc.l2hostility.init.registrate.LHTraits;
import java.util.Map;
import java.util.TreeMap;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

public class LHConfig {
   public static final LHConfig.Client CLIENT = (LHConfig.Client)L2Hostility.REGISTRATE.registerClient(LHConfig.Client::new);
   public static final LHConfig.Server SERVER = (LHConfig.Server)L2Hostility.REGISTRATE.registerSynced(LHConfig.Server::new);

   public static void init() {
   }

   public static class Client extends ConfigInit {
      public final BooleanValue showTraitOverHead;
      public final BooleanValue showLevelOverHead;
      public final IntValue overHeadRenderDistance;
      public final BooleanValue overHeadRenderFullBright;
      public final IntValue overHeadLevelColor;
      public final IntValue overHeadLevelColorAbyss;
      public final DoubleValue overHeadRenderOffset;
      public final BooleanValue showOnlyWhenHovered;
      public final IntValue glowingRangeHidden;
      public final IntValue glowingRangeNear;
      public final BooleanValue showUndyingParticles;
      public final BooleanValue killerAuraSoundEffect;

      Client(Builder builder) {
         this.markL2();
         this.showTraitOverHead = builder.text("Render Traits in name plate form").define("showTraitOverHead", true);
         this.showLevelOverHead = builder.text("Render mob level in name plate form").define("showLevelOverHead", true);
         this.overHeadRenderDistance = builder.text("Name plate render distance").defineInRange("overHeadRenderDistance", 32, 0, 128);
         this.overHeadRenderOffset = builder.text("Name plate render offset in lines, upward is positive")
            .defineInRange("overHeadRenderOffset", 0.0, -100.0, 100.0);
         this.overHeadRenderFullBright = builder.text("Overhead render text becomes full bright").define("overHeadRenderFullBright", true);
         this.overHeadLevelColor = builder.text("Overhead level color in decimal form, converted from hex form")
            .defineInRange("overHeadLevelColor", 11184810, Integer.MIN_VALUE, Integer.MAX_VALUE);
         this.overHeadLevelColorAbyss = builder.text("Overhead level color for mobs affected by abyssal thorn")
            .defineInRange("overHeadLevelColorAbyss", 16733525, Integer.MIN_VALUE, Integer.MAX_VALUE);
         this.showOnlyWhenHovered = builder.text("Show nameplate style trait and name only when hovered").define("showOnlyWhenHovered", false);
         this.glowingRangeHidden = builder.text("Detector Glasses glowing range for hidden mobs").defineInRange("glowingRangeHidden", 32, 1, 256);
         this.glowingRangeNear = builder.text("Detector Glasses glowing range for nearby mobs").defineInRange("glowingRangeNear", 16, 1, 256);
         this.showUndyingParticles = builder.text("Render undying particles").define("showUndyingParticles", true);
         this.killerAuraSoundEffect = builder.text("Killer Aura sound effect").define("killerAuraSoundEffect", true);
      }
   }

   public static class Server extends ConfigInit {
      public final IntValue killsPerLevel;
      public final IntValue maxPlayerLevel;
      public final IntValue maxMobLevel;
      public final IntValue newPlayerProtectRange;
      public final DoubleValue playerDeathDecay;
      public final BooleanValue keepInventoryRuleKeepDifficulty;
      public final BooleanValue deathDecayDimension;
      public final BooleanValue deathDecayTraitCap;
      public final BooleanValue enableEntitySpecificDatapack;
      public final BooleanValue enableStructureSpecificDatapack;
      public final DoubleValue healthFactor;
      public final BooleanValue exponentialHealth;
      public final DoubleValue damageFactor;
      public final BooleanValue exponentialDamage;
      public final DoubleValue expDropFactor;
      public final DoubleValue drownedTridentChancePerLevel;
      public final IntValue dimensionFactor;
      public final DoubleValue distanceFactor;
      public final DoubleValue globalApplyChance;
      public final DoubleValue globalTraitChance;
      public final DoubleValue globalTraitSuppression;
      public final BooleanValue allowLegendary;
      public final BooleanValue allowSectionDifficulty;
      public final BooleanValue allowBypassMinimum;
      public final BooleanValue allowHostilityOrb;
      public final BooleanValue allowHostilitySpawner;
      public final BooleanValue allowExtraEnchantments;
      public final IntValue defaultLevelBase;
      public final DoubleValue defaultLevelVar;
      public final DoubleValue defaultLevelScale;
      public final DoubleValue initialTraitChanceSlope;
      public final BooleanValue allowNoAI;
      public final BooleanValue allowPlayerAllies;
      public final BooleanValue allowTraitOnOwnable;
      public final DoubleValue dropRateFromSpawner;
      public final BooleanValue enableEquipmentDatapack;
      public final IntValue bottleOfCurseLevel;
      public final IntValue envyExtraLevel;
      public final IntValue greedExtraLevel;
      public final IntValue lustExtraLevel;
      public final IntValue wrathExtraLevel;
      public final IntValue abrahadabraExtraLevel;
      public final IntValue nidhoggurExtraLevel;
      public final DoubleValue nidhoggurDropFactor;
      public final DoubleValue greedDropFactor;
      public final DoubleValue envyDropRate;
      public final DoubleValue gluttonyBottleDropRate;
      public final DoubleValue prideDamageBonus;
      public final DoubleValue prideHealthBonus;
      public final DoubleValue prideTraitFactor;
      public final DoubleValue wrathDamageBonus;
      public final BooleanValue disableHostilityLootCurioRequirement;
      public final BooleanValue banBottles;
      public final BooleanValue nidhoggurCapAtItemMaxStack;
      public final BooleanValue bookOfReprintSpread;
      public final IntValue hostilitySpawnCount;
      public final IntValue hostilitySpawnLevelFactor;
      public final DoubleValue tankHealth;
      public final DoubleValue tankArmor;
      public final DoubleValue tankTough;
      public final DoubleValue speedy;
      public final DoubleValue regen;
      public final DoubleValue adaptFactor;
      public final DoubleValue reflectFactor;
      public final IntValue dispellTime;
      public final DoubleValue dispellDamageFactor;
      public final DoubleValue dispellDamageReductionBase;
      public final DoubleValue dementorDamageFactor;
      public final DoubleValue dementorDamageReductionBase;
      public final DoubleValue killerAuraDamageFactor;
      public final IntValue fieryTime;
      public final IntValue weakTime;
      public final IntValue slowTime;
      public final IntValue poisonTime;
      public final IntValue witherTime;
      public final IntValue levitationTime;
      public final IntValue blindTime;
      public final IntValue confusionTime;
      public final IntValue soulBurnerTime;
      public final IntValue freezingTime;
      public final IntValue curseTime;
      public final IntValue teleportDuration;
      public final IntValue teleportRange;
      public final IntValue repellRange;
      public final DoubleValue repellStrength;
      public final DoubleValue corrosionDurability;
      public final DoubleValue erosionDurability;
      public final DoubleValue corrosionDamage;
      public final DoubleValue erosionDamage;
      public final IntValue ragnarokTime;
      public final BooleanValue ragnarokSealBackpack;
      public final BooleanValue ragnarokSealSlotAdder;
      public final IntValue killerAuraDamage;
      public final IntValue killerAuraRange;
      public final IntValue killerAuraInterval;
      public final IntValue shulkerInterval;
      public final IntValue grenadeInterval;
      public final DoubleValue grenadeDamageFactor;
      public final DoubleValue drainDamage;
      public final DoubleValue drainDuration;
      public final IntValue drainDurationMax;
      public final IntValue counterStrikeDuration;
      public final IntValue counterStrikeRange;
      public final IntValue pullingRange;
      public final DoubleValue pullingStrength;
      public final DoubleValue reprintDamage;
      public final IntValue reprintBypass;
      public final DoubleValue ringOfLifeMaxDamage;
      public final IntValue flameThornTime;
      public final IntValue ringOfReflectionRadius;
      public final IntValue witchWandFactor;
      public final DoubleValue ringOfCorrosionFactor;
      public final DoubleValue ringOfCorrosionPenalty;
      public final DoubleValue ringOfHealingRate;
      public final IntValue witchChargeMinDuration;
      public final DoubleValue insulatorFactor;
      public final IntValue orbRadius;
      public final DoubleValue splitDropRateFactor;
      public final DoubleValue equipmentDropRate;
      public final BooleanValue enableHostilityOrbDrop;
      public final BooleanValue enableCurioCheckFilter;
      public final IntValue removeTraitCheckInterval;
      public final IntValue auraEffectApplicationInterval;
      public final IntValue selfEffectApplicationInterval;
      public final IntValue maxTraitCount;
      public final BooleanValue enableAdaptiveLeveling;
      public final Map<String, BooleanValue> map = new TreeMap<>();
      public final Map<String, IntValue> range = new TreeMap<>();

      Server(Builder builder) {
         this.markL2();
         this.enableEntitySpecificDatapack = builder.text("Allow entity specific difficulty configs to load").define("enableEntitySpecificDatapack", true);
         this.enableStructureSpecificDatapack = builder.text("Allow structure specific difficulty configs to load")
            .define("enableStructureSpecificDatapack", true);
         this.enableEquipmentDatapack = builder.text("Allow datapack configs to add extra items and enchantments to entities")
            .define("enableEquipmentDatapack", true);
         builder.push("scaling", "Mob Scaling");
         this.healthFactor = builder.text("Health factor per level").defineInRange("healthFactor", 0.03, 0.0, 1000.0);
         this.exponentialHealth = builder.text("Use exponential health").define("exponentialHealth", false);
         this.damageFactor = builder.text("Damage factor per level").defineInRange("damageFactor", 0.02, 0.0, 1000.0);
         this.exponentialDamage = builder.text("Use exponential damage").define("exponentialDamage", false);
         this.expDropFactor = builder.text("Experience drop factor per level").defineInRange("expDropFactor", 0.05, 0.0, 1000.0);
         this.drownedTridentChancePerLevel = builder.text("Chance per level for drowned to hold trident")
            .defineInRange("drownedTridentChancePerLevel", 0.005, 0.0, 1000.0);
         this.dimensionFactor = builder.text("Difficulty bonus per level visited").defineInRange("dimensionFactor", 10, 0, 1000);
         this.distanceFactor = builder.text("Difficulty bonus per block from origin").defineInRange("distanceFactor", 0.003, 0.0, 1000.0);
         this.globalApplyChance = builder.text("Chance for health/damage bonus and trait to apply")
            .comment("Not applicable to mobs with minimum level.")
            .defineInRange("globalApplyChance", 1.0, 0.0, 1.0);
         this.globalTraitChance = builder.text("Chance for trait to apply")
            .comment("Not applicable to mobs with minimum level.")
            .defineInRange("globalTraitChance", 1.0, 0.0, 1.0);
         this.globalTraitSuppression = builder.text("Chance to stop adding traits after adding a trait")
            .comment("Not applicable to mobs with minimum level.")
            .defineInRange("globalTraitSuppression", 0.1, 0.0, 1.0);
         this.allowLegendary = builder.text("Allow legendary traits").define("allowLegendary", true);
         this.allowSectionDifficulty = builder.text("Allow chunk section to accumulate difficulty").define("allowSectionDifficulty", true);
         this.allowBypassMinimum = builder.text("Allow difficulty clearing bypass mob minimum level").define("allowBypassMinimum", true);
         this.allowExtraEnchantments = builder.text("Allow level-related extra enchantment spawning").define("allowExtraEnchantments", true);
         this.defaultLevelBase = builder.text("Default dimension base difficulty for mod dimensions").defineInRange("defaultLevelBase", 20, 0, 1000);
         this.defaultLevelVar = builder.text("Default dimension difficulty variation for mod dimensions").defineInRange("defaultLevelVar", 16.0, 0.0, 1000.0);
         this.defaultLevelScale = builder.text("Default dimension difficulty scale for mod dimensions").defineInRange("defaultLevelScale", 1.5, 0.0, 10.0);
         this.initialTraitChanceSlope = builder.text("Mobs at Lv.N will have N x k% chance to have trait")
            .comment("Default k% = 0.01, so Lv.N mobs with have N% chance to have trait")
            .comment("Mobs with entity config and trait chance of 1 will not be affected")
            .defineInRange("initialTraitChanceSlope", 0.01, 0.0, 1.0);
         this.splitDropRateFactor = builder.text("Slimes hostility loot drop rate decay per split").defineInRange("splitDropRateFactor", 0.25, 0.0, 1.0);
         this.allowNoAI = builder.text("Allow mobs without AI to have levels").define("allowNoAI", false);
         this.allowPlayerAllies = builder.text("Allow mobs allied to player to have levels").define("allowPlayerAllies", false);
         this.allowTraitOnOwnable = builder.text("Keep traits on mobs tamed by player").define("allowTraitOnOwnable", false);
         this.dropRateFromSpawner = builder.text("Drop rate of hostility loot from mobs from spawner").defineInRange("dropRateFromSpawner", 0.5, 0.0, 1.0);
         this.equipmentDropRate = builder.text("Drop rate of equipments spawned via hostility").defineInRange("equipmentDropRate", 0.085, 0.0, 1.0);
         this.maxTraitCount = builder.text("Max number of traits on mobs").defineInRange("maxTraitCount", 9, 1, 100);
         this.enableAdaptiveLeveling = builder.text("Allow player to increase difficulty by killing mobs").define("enableAdaptiveLeveling", true);
         builder.pop();
         builder.push("difficulty", "Difficulty Settings");
         this.maxPlayerLevel = builder.text("Max player adaptive level").defineInRange("maxPlayerLevel", 2000, 1, 100000);
         this.maxMobLevel = builder.text("Max mob level").defineInRange("maxMobLevel", 3000, 1, 100000);
         this.killsPerLevel = builder.text("Difficulty increment takes this many kills of same level mob").defineInRange("killsPerLevel", 30, 1, 100000);
         this.playerDeathDecay = builder.text("Decay in player difficulty on death").defineInRange("playerDeathDecay", 0.8, 0.0, 2.0);
         this.keepInventoryRuleKeepDifficulty = builder.text("Allow KeepInventory to keep difficulty as well").define("keepInventoryRuleKeepDifficulty", false);
         this.deathDecayDimension = builder.text("On player death, clear dimension penalty").define("deathDecayDimension", true);
         this.deathDecayTraitCap = builder.text("On player death, reduce max trait spawned by 1").define("deathDecayTraitCap", true);
         this.newPlayerProtectRange = builder.text(
               "Mobs spawned within this range will use lowest player level in range instead of nearest player's level to determine mob level"
            )
            .defineInRange("newPlayerProtectRange", 48, 0, 128);
         builder.pop();
         builder.push("orb_and_spawner", "Hostility Orb and Hostility Spawner");
         this.allowHostilityOrb = builder.text("Allow to use hostility orb").define("allowHostilityOrb", true);
         this.enableHostilityOrbDrop = builder.text("Give player hostility orbs when upleveling difficulty").define("enableHostilityOrbDrop", true);
         this.orbRadius = builder.text("Radius for Hostility Orb to take effect.")
            .comment("0 means 1x1x1 section, 1 means 3x3x3 sections, 2 means 5x5x5 sections")
            .defineInRange("orbRadius", 2, 0, 10);
         this.allowHostilitySpawner = builder.text("Allow to use hostility spawner").define("allowHostilitySpawner", true);
         this.hostilitySpawnCount = builder.text("Number of mobs to spawn in Hostility Spawner").defineInRange("hostilitySpawnCount", 16, 1, 64);
         this.hostilitySpawnLevelFactor = builder.text("Level bonus factor for mobs to spawn in Hostility Spawner")
            .defineInRange("hostilitySpawnLevelFactor", 2, 1, 10000);
         builder.pop();
         builder.push("items", "Items");
         this.banBottles = builder.text("Ban drinking bottle of curse and sanity").define("banBottles", false);
         this.disableHostilityLootCurioRequirement = builder.text("Disable curio requirement for hostility loot")
            .define("disableHostilityLootCurioRequirement", false);
         this.bottleOfCurseLevel = builder.text("Number of level to add when using bottle of curse").defineInRange("bottleOfCurseLevel", 50, 0, 1000);
         this.witchChargeMinDuration = builder.text("Minimum duration for witch charge to be effective, in ticks")
            .defineInRange("witchChargeMinDuration", 200, 20, 10000);
         this.ringOfLifeMaxDamage = builder.text("Max percentage of max health a damage can hurt wearer of Ring of Life")
            .defineInRange("ringOfLifeMaxDamage", 0.9, 0.0, 1.0);
         this.flameThornTime = builder.text("Time in ticks of Soul Flame to inflict").defineInRange("flameThornTime", 100, 1, 10000);
         this.ringOfReflectionRadius = builder.text("Radius in blocks for Ring of Reflection to work").defineInRange("ringOfReflectionRadius", 16, 1, 256);
         this.witchWandFactor = builder.text("Factor of effect duration for witch wand, to make up for splash decay")
            .defineInRange("witchWandFactor", 4, 1, 100);
         this.ringOfCorrosionFactor = builder.text("Factor of maximum durability to cost for ring of corrosion")
            .defineInRange("ringOfCorrosionFactor", 0.2, 0.0, 1.0);
         this.ringOfCorrosionPenalty = builder.text("Penalty of maximum durability to cost for ring of corrosion")
            .defineInRange("ringOfCorrosionPenalty", 0.1, 0.0, 1.0);
         this.ringOfHealingRate = builder.text("Percentage of health to heal every second").defineInRange("ringOfHealingRate", 0.05, 0.0, 1.0);
         this.envyExtraLevel = builder.text("Number of level to add when using Curse of Envy").defineInRange("envyExtraLevel", 50, 0, 1000);
         this.greedExtraLevel = builder.text("Number of level to add when using Curse of Greed").defineInRange("greedExtraLevel", 50, 0, 1000);
         this.lustExtraLevel = builder.text("Number of level to add when using Curse of Lust").defineInRange("lustExtraLevel", 50, 0, 1000);
         this.wrathExtraLevel = builder.text("Number of level to add when using Curse of Wrath").defineInRange("wrathExtraLevel", 50, 0, 1000);
         this.greedDropFactor = builder.text("Hostility loot drop factor when using Curse of Greed").defineInRange("greedDropFactor", 2.0, 1.0, 10.0);
         this.envyDropRate = builder.text("Trait item drop rate per rank when using Curse of Envy").defineInRange("envyDropRate", 0.02, 0.0, 1.0);
         this.gluttonyBottleDropRate = builder.text("Bottle of Curse drop rate per level when using Curse of Gluttony")
            .defineInRange("gluttonyBottleDropRate", 0.02, 0.0, 1.0);
         this.wrathDamageBonus = builder.text("Damage bonus per level difference when using Curse of Wrath").defineInRange("wrathDamageBonus", 0.02, 0.0, 1.0);
         this.prideDamageBonus = builder.text("Damage bonus per level when using Curse of Pride").defineInRange("prideDamageBonus", 0.005, 0.0, 1.0);
         this.prideHealthBonus = builder.text("Health boost per level in percentage when using Curse of Pride")
            .defineInRange("prideHealthBonus", 0.005, 0.0, 1.0);
         this.prideTraitFactor = builder.text("Trait cost multiplier when using Curse of Pride").defineInRange("prideTraitFactor", 0.5, 0.01, 1.0);
         this.abrahadabraExtraLevel = builder.text("Number of level to add when using Abrahadabra").defineInRange("abrahadabraExtraLevel", 100, 0, 1000);
         this.nidhoggurExtraLevel = builder.text("Number of level to add when using Greed of Nidhoggur").defineInRange("nidhoggurExtraLevel", 100, 0, 1000);
         this.nidhoggurDropFactor = builder.text("All loot drop factor when using Greed of Nidhoggur").defineInRange("nidhoggurDropFactor", 0.005, 0.0, 10.0);
         this.nidhoggurCapAtItemMaxStack = builder.text("Cap drop at item max stack size").define("nidhoggurCapAtItemMaxStack", true);
         this.bookOfReprintSpread = builder.text("When using book of reprint to copy books, drop extra on player and does not allow overstacking")
            .define("bookOfReprintSpread", false);
         this.insulatorFactor = builder.text("Insulator Enchantment factor for reducing pushing").defineInRange("insulatorFactor", 0.8, 0.0, 1.0);
         builder.pop();
         builder.push("performance", "Performance");
         this.enableCurioCheckFilter = builder.text("Enable curios checks whitelist for items such as ring of ocean, on only selected mobs to reduce lag")
            .define("enableCurioCheckFilter", true);
         this.removeTraitCheckInterval = builder.text("Interval for which traits check if they are banned and to be removed")
            .defineInRange("removeTraitCheckInterval", 10, 1, 1000);
         this.auraEffectApplicationInterval = builder.text("Interval for aura effect traits to apply")
            .defineInRange("auraEffectApplicationInterval", 5, 1, 1000);
         this.selfEffectApplicationInterval = builder.text("Interval for self effect traits to apply")
            .defineInRange("selfEffectApplicationInterval", 200, 1, 1000);
         builder.pop();
         LHTraits.register();
         builder.push("traits", "Trait Settings");
         this.tankHealth = builder.text("Health bonus for Tank trait per level").defineInRange("tankHealth", 0.2, 0.0, 1000.0);
         this.tankArmor = builder.text("Armor bonus for Tank trait per level").defineInRange("tankArmor", 4.0, 0.0, 1000.0);
         this.tankTough = builder.text("Toughness bonus for Tank trait per level").defineInRange("tankTough", 4.0, 0.0, 1000.0);
         this.speedy = builder.text("Speed bonus for Speedy trait per level").defineInRange("speedy", 0.2, 0.0, 1000.0);
         this.regen = builder.text("Regen rate for Regeneration trait per second per level").defineInRange("regen", 0.01, 0.0, 1000.0);
         this.adaptFactor = builder.text("Damage factor for Adaptive. Higher means less reduction").defineInRange("adaptFactor", 0.5, 0.0, 1000.0);
         this.reflectFactor = builder.text("Reflect factor per level for Reflect. 0.5 means reflect 50% damage (then scaled by level bonus)")
            .defineInRange("reflectFactor", 0.1, 0.0, 1000.0);
         this.dispellTime = builder.text("Duration in ticks for enchantments to be disabled per level for Dispell").defineInRange("dispellTime", 200, 1, 60000);
         this.fieryTime = builder.text("Duration in seconds to set target on fire by Fiery").defineInRange("fieryTime", 5, 0, 3000);
         this.weakTime = builder.text("Duration in ticks for Weakness").defineInRange("weakTime", 200, 0, 3000);
         this.slowTime = builder.text("Duration in ticks for Slowness").defineInRange("slowTime", 160, 0, 3000);
         this.poisonTime = builder.text("Duration in ticks for Poison").defineInRange("poisonTime", 200, 0, 3000);
         this.witherTime = builder.text("Duration in ticks for Wither").defineInRange("witherTime", 200, 0, 3000);
         this.levitationTime = builder.text("Duration in ticks for Levitation").defineInRange("levitationTime", 100, 0, 3000);
         this.blindTime = builder.text("Duration in ticks for Blindness").defineInRange("blindTime", 200, 0, 3000);
         this.confusionTime = builder.text("Duration in ticks for Nausea").defineInRange("confusionTime", 160, 0, 3000);
         this.soulBurnerTime = builder.text("Duration in ticks for Soul Burner").defineInRange("soulBurnerTime", 60, 0, 3000);
         this.freezingTime = builder.text("Duration in ticks for Freezing").defineInRange("freezingTime", 100, 0, 3000);
         this.curseTime = builder.text("Duration in ticks for Cursed").defineInRange("curseTime", 200, 0, 3000);
         this.teleportDuration = builder.text("Interval in ticks for Teleport").defineInRange("teleportDuration", 100, 0, 3000);
         this.teleportRange = builder.text("Range in blocks for Teleport").defineInRange("teleportRange", 16, 0, 64);
         this.repellRange = builder.text("Range in blocks for Repell").defineInRange("repellRange", 10, 0, 64);
         this.repellStrength = builder.text("Repell force strength, default is 0.2").defineInRange("repellStrength", 0.15, 0.0, 1.0);
         this.corrosionDurability = builder.text("Fraction of remaining durability to corrode, per trait rank")
            .defineInRange("corrosionDurability", 0.1, 0.0, 1.0);
         this.corrosionDamage = builder.text("Damage bonus when nothing to corrode").defineInRange("corrosionDamage", 0.25, 0.0, 1.0);
         this.erosionDurability = builder.text("Fraction of lost durability to erode, per trait rank").defineInRange("erosionDurability", 0.05, 0.0, 1.0);
         this.erosionDamage = builder.text("Damage bonus when nothing to erode").defineInRange("erosionDamage", 0.25, 0.0, 1.0);
         this.ragnarokTime = builder.text("Seal time per level for Ragnarok").defineInRange("ragnarokTime", 20, 1, 1000);
         this.ragnarokSealBackpack = builder.text("Allow Ragnarok to seal items with Backpack in its id").define("ragnarokSealBackpack", false);
         this.ragnarokSealSlotAdder = builder.text("Allow Ragnarok to seal curios items that adds curios slot").define("ragnarokSealSlotAdder", false);
         this.killerAuraDamage = builder.text("Damage for killer aura").defineInRange("killerAuraDamage", 6, 1, 10000);
         this.killerAuraRange = builder.text("Range for for killer aura").defineInRange("killerAuraRange", 6, 1, 32);
         this.killerAuraInterval = builder.text("Interval for for killer aura").defineInRange("killerAuraInterval", 120, 1, 10000);
         this.shulkerInterval = builder.text("Interval for for shulker").defineInRange("shulkerInterval", 40, 1, 10000);
         this.grenadeInterval = builder.text("Interval for for explode shulker").defineInRange("explodeShulkerInterval", 60, 1, 10000);
         this.drainDamage = builder.text("Damage bonus for each negative effects").defineInRange("drainDamage", 0.1, 0.0, 100.0);
         this.drainDuration = builder.text("Duration boost for negative effects").defineInRange("drainDuration", 0.5, 0.0, 100.0);
         this.drainDurationMax = builder.text("Max duration boost for negative effects").defineInRange("drainDurationMax", 1200, 0, 10000);
         this.counterStrikeDuration = builder.text("Interval in ticks for Counter Strike").defineInRange("counterStrikeDuration", 100, 0, 3000);
         this.counterStrikeRange = builder.text("Range in blocks for Counter Strike").defineInRange("counterStrikeRange", 6, 0, 64);
         this.pullingRange = builder.text("Range in blocks for Pulling").defineInRange("pullingRange", 10, 0, 64);
         this.pullingStrength = builder.text("Pulling force strength, default is 0.2").defineInRange("pullingStrength", 0.15, 0.0, 10.0);
         this.reprintDamage = builder.text("Reprint damage factor per enchantment point").defineInRange("reprintDamage", 0.02, 0.0, 1.0);
         this.reprintBypass = builder.text("Reprint will gain Void Touch 20 and Vanishing Curse when it hits a mob with max Enchantment level of X or higher")
            .defineInRange("reprintBypass", 10, 0, 10000);
         this.dispellDamageFactor = builder.text("Damage Bonus Factor for enchantment bypassing damage")
            .comment("Example: 0.5 means +2% per level damage bonus becoming +1% per level")
            .defineInRange("dispellDamageFactor", 0.5, 0.0, 1.0);
         this.dementorDamageFactor = builder.text("Damage Bonus Factor for dementor-affected damage")
            .comment("Example: 0.5 means +2% per level damage bonus becoming +1% per level")
            .defineInRange("dementorDamageFactor", 0.5, 0.0, 1.0);
         this.killerAuraDamageFactor = builder.text("Damage Bonus Factor for killer aura damage")
            .comment("Example: 0.5 means +2% per level damage bonus becoming +1% per level")
            .defineInRange("killerAuraDamageFactor", 0.5, 0.0, 1.0);
         this.grenadeDamageFactor = builder.text("Damage Bonus Factor for grenade damage")
            .comment("Example: 0.5 means +2% per level damage bonus becoming +1% per level")
            .defineInRange("grenadeDamageFactor", 0.25, 0.0, 1.0);
         this.dispellDamageReductionBase = builder.text("Damage Reduction Base for Dispell")
            .comment("Magic damage will take a log with base of this config number")
            .comment("Example: 1.1 means 100 -> 48, 1000 -> 72")
            .comment("Example: 2 means 16 -> 4, 1024 -> 10")
            .comment("Example: 10 means 100 -> 2, 1000000 -> 6")
            .defineInRange("dispellDamageReductionBase", 2.0, 1.01, 100.0);
         this.dementorDamageReductionBase = builder.text("Damage Reduction Base for Dementor")
            .comment("Non-Magic damage will take a log with base of this config number")
            .comment("Example: 1.1 means 100 -> 48, 1000 -> 72")
            .comment("Example: 2 means 16 -> 4, 1024 -> 10")
            .comment("Example: 10 means 100 -> 2, 1000000 -> 6")
            .defineInRange("dementorDamageReductionBase", 2.0, 1.01, 100.0);
         this.effectAura(builder, "gravity", 10);
         this.effectAura(builder, "moonwalk", 10);
         this.effectAura(builder, "arena", 24);
         builder.pop();
         builder.push("toggle", " Trait Toggles");

         for (String e : L2Hostility.REGISTRATE.getList()) {
            this.map.put(e, builder.text("Enable Trait " + RegistrateLangProvider.toEnglishName(e)).define("allow_" + e, true));
         }

         builder.pop();
      }

      private void effectAura(Builder builder, String str, int def) {
         this.range.put(str, builder.text("Effect range for trait " + str).defineInRange(str + "Range", def, 0, 100));
      }
   }
}
