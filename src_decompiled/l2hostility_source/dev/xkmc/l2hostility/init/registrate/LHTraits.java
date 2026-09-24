package dev.xkmc.l2hostility.init.registrate;

import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateTagsProvider.IntrinsicImpl;
import dev.xkmc.l2complements.init.registrate.LCEffects;
import dev.xkmc.l2core.init.L2TagGen;
import dev.xkmc.l2core.init.reg.datapack.DataMapReg;
import dev.xkmc.l2core.init.reg.registrate.L2Registrate.RegistryInstance;
import dev.xkmc.l2hostility.content.config.TraitConfig;
import dev.xkmc.l2hostility.content.config.TraitExclusion;
import dev.xkmc.l2hostility.content.entity.BulletType;
import dev.xkmc.l2hostility.content.traits.base.AttributeTrait;
import dev.xkmc.l2hostility.content.traits.base.MobTrait;
import dev.xkmc.l2hostility.content.traits.base.SelfEffectTrait;
import dev.xkmc.l2hostility.content.traits.base.TargetEffectTrait;
import dev.xkmc.l2hostility.content.traits.common.AdaptingTrait;
import dev.xkmc.l2hostility.content.traits.common.AuraEffectTrait;
import dev.xkmc.l2hostility.content.traits.common.FieryTrait;
import dev.xkmc.l2hostility.content.traits.common.GravityTrait;
import dev.xkmc.l2hostility.content.traits.common.InvisibleTrait;
import dev.xkmc.l2hostility.content.traits.common.ReflectTrait;
import dev.xkmc.l2hostility.content.traits.common.RegenTrait;
import dev.xkmc.l2hostility.content.traits.common.ShulkerTrait;
import dev.xkmc.l2hostility.content.traits.goals.CounterStrikeTrait;
import dev.xkmc.l2hostility.content.traits.goals.EnderTrait;
import dev.xkmc.l2hostility.content.traits.highlevel.ArenaTrait;
import dev.xkmc.l2hostility.content.traits.highlevel.CorrosionTrait;
import dev.xkmc.l2hostility.content.traits.highlevel.DrainTrait;
import dev.xkmc.l2hostility.content.traits.highlevel.ErosionTrait;
import dev.xkmc.l2hostility.content.traits.highlevel.GrowthTrait;
import dev.xkmc.l2hostility.content.traits.highlevel.ReprintTrait;
import dev.xkmc.l2hostility.content.traits.highlevel.SplitTrait;
import dev.xkmc.l2hostility.content.traits.legendary.DementorTrait;
import dev.xkmc.l2hostility.content.traits.legendary.DispellTrait;
import dev.xkmc.l2hostility.content.traits.legendary.KillerAuraTrait;
import dev.xkmc.l2hostility.content.traits.legendary.MasterTrait;
import dev.xkmc.l2hostility.content.traits.legendary.PullingTrait;
import dev.xkmc.l2hostility.content.traits.legendary.RagnarokTrait;
import dev.xkmc.l2hostility.content.traits.legendary.RepellingTrait;
import dev.xkmc.l2hostility.content.traits.legendary.UndyingTrait;
import dev.xkmc.l2hostility.init.L2Hostility;
import dev.xkmc.l2hostility.init.data.LHConfig;
import dev.xkmc.l2hostility.init.data.LHTagGen;
import dev.xkmc.l2hostility.init.entries.TraitBuilder;
import dev.xkmc.l2hostility.init.entries.TraitEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class LHTraits {
   public static final RegistryInstance<MobTrait> TRAITS = L2Hostility.REGISTRATE.newRegistry("trait", MobTrait.class, e -> e.sync(true));
   public static final DataMapReg<MobTrait, TraitConfig> DATA = L2Hostility.REG.dataMap("trait_data", TRAITS.key(), TraitConfig.class);
   public static final DataMapReg<MobTrait, TraitExclusion> EXCLUSION = L2Hostility.REG.dataMap("trait_exclusion", TRAITS.key(), TraitExclusion.class);
   public static final ProviderType<IntrinsicImpl<MobTrait>> TRAIT_TAGS = L2TagGen.getProvider(TRAITS.key(), TRAITS.reg());
   public static final TagKey<MobTrait> POTION = LHTagGen.createTraitTag("potion_trait");
   public static final TraitEntry<AttributeTrait> TANK = L2Hostility.REGISTRATE
      .<AttributeTrait>regTrait(
         "tank",
         () -> new AttributeTrait(
            ChatFormatting.GREEN,
            new AttributeTrait.AttributeEntry("tank_health", Attributes.MAX_HEALTH, LHConfig.SERVER.tankHealth::get, Operation.ADD_MULTIPLIED_TOTAL),
            new AttributeTrait.AttributeEntry("tank_armor", Attributes.ARMOR, LHConfig.SERVER.tankArmor::get, Operation.ADD_VALUE),
            new AttributeTrait.AttributeEntry("tank_tough", Attributes.ARMOR_TOUGHNESS, LHConfig.SERVER.tankTough::get, Operation.ADD_VALUE)
         ),
         new TraitConfig(20, 100, 5, 20)
      )
      .lang("Tanky")
      .register();
   public static final TraitEntry<AttributeTrait> SPEEDY = L2Hostility.REGISTRATE
      .<AttributeTrait>regTrait(
         "speedy",
         () -> new AttributeTrait(
            ChatFormatting.AQUA,
            new AttributeTrait.AttributeEntry("speedy", Attributes.MOVEMENT_SPEED, LHConfig.SERVER.speedy::get, Operation.ADD_MULTIPLIED_TOTAL)
         ),
         new TraitConfig(20, 100, 5, 50)
      )
      .lang("Speedy")
      .register();
   public static final TraitEntry<SelfEffectTrait> PROTECTION = L2Hostility.REGISTRATE
      .<SelfEffectTrait>regTrait("protection", () -> new SelfEffectTrait(MobEffects.DAMAGE_RESISTANCE), new TraitConfig(40, 100, 4, 50))
      .addBlacklist(e -> e.addTag(LHTagGen.SEMIBOSS))
      .lang("Protected")
      .register();
   public static final TraitEntry<TargetEffectTrait> WEAKNESS = ((TraitBuilder)L2Hostility.REGISTRATE
         .regTrait(
            "weakness",
            () -> new TargetEffectTrait(lv -> new MobEffectInstance(MobEffects.WEAKNESS, (Integer)LHConfig.SERVER.weakTime.get(), lv - 1)),
            new TraitConfig(30, 50, 5, 40)
         )
         .tag(TRAIT_TAGS, new TagKey[]{POTION}))
      .lang("Weakener")
      .register();
   public static final TraitEntry<TargetEffectTrait> SLOWNESS = ((TraitBuilder)L2Hostility.REGISTRATE
         .regTrait(
            "slowness",
            () -> new TargetEffectTrait(lv -> new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, (Integer)LHConfig.SERVER.slowTime.get(), lv)),
            new TraitConfig(20, 50, 5, 20)
         )
         .tag(TRAIT_TAGS, new TagKey[]{POTION}))
      .lang("Stray")
      .register();
   public static final TraitEntry<TargetEffectTrait> POISON = ((TraitBuilder)L2Hostility.REGISTRATE
         .regTrait(
            "poison",
            () -> new TargetEffectTrait(lv -> new MobEffectInstance(MobEffects.POISON, (Integer)LHConfig.SERVER.poisonTime.get() * lv)),
            new TraitConfig(20, 75, 3, 20)
         )
         .tag(TRAIT_TAGS, new TagKey[]{POTION}))
      .lang("Poisonous")
      .register();
   public static final TraitEntry<TargetEffectTrait> WITHER = ((TraitBuilder)((TraitBuilder)L2Hostility.REGISTRATE
            .regTrait(
               "wither",
               () -> new TargetEffectTrait(lv -> new MobEffectInstance(MobEffects.WITHER, (Integer)LHConfig.SERVER.witherTime.get(), lv - 1)),
               new TraitConfig(20, 50, 3, 20)
            )
            .dataMap(EXCLUSION.reg(), TraitExclusion.builder().of(POISON, 0.5).build()))
         .tag(TRAIT_TAGS, new TagKey[]{POTION}))
      .lang("Withering")
      .register();
   public static final TraitEntry<TargetEffectTrait> BLIND = ((TraitBuilder)L2Hostility.REGISTRATE
         .regTrait(
            "blindness",
            () -> new TargetEffectTrait(lv -> new MobEffectInstance(MobEffects.BLINDNESS, (Integer)LHConfig.SERVER.blindTime.get() * lv)),
            new TraitConfig(30, 25, 3, 40)
         )
         .tag(TRAIT_TAGS, new TagKey[]{POTION}))
      .lang("Blinder")
      .register();
   public static final TraitEntry<TargetEffectTrait> CONFUSION = ((TraitBuilder)((TraitBuilder)L2Hostility.REGISTRATE
            .regTrait(
               "nausea",
               () -> new TargetEffectTrait(lv -> new MobEffectInstance(MobEffects.CONFUSION, (Integer)LHConfig.SERVER.confusionTime.get() * lv)),
               new TraitConfig(30, 25, 3, 40)
            )
            .dataMap(EXCLUSION.reg(), TraitExclusion.builder().of(BLIND, 0.5).build()))
         .tag(TRAIT_TAGS, new TagKey[]{POTION}))
      .lang("Distorter")
      .register();
   public static final TraitEntry<TargetEffectTrait> LEVITATION = ((TraitBuilder)((TraitBuilder)L2Hostility.REGISTRATE
            .regTrait(
               "levitation",
               () -> new TargetEffectTrait(lv -> new MobEffectInstance(MobEffects.LEVITATION, (Integer)LHConfig.SERVER.levitationTime.get() * lv)),
               new TraitConfig(50, 50, 3, 50)
            )
            .dataMap(EXCLUSION.reg(), TraitExclusion.builder().of(SLOWNESS, 0.5).build()))
         .tag(TRAIT_TAGS, new TagKey[]{POTION}))
      .lang("Levitater")
      .register();
   public static final TraitEntry<TargetEffectTrait> SOUL_BURNER = ((TraitBuilder)((TraitBuilder)L2Hostility.REGISTRATE
            .regTrait(
               "soul_burner",
               () -> new TargetEffectTrait(lv -> new MobEffectInstance(LCEffects.FLAME, (Integer)LHConfig.SERVER.soulBurnerTime.get(), lv - 1)),
               new TraitConfig(50, 50, 3, 70)
            )
            .dataMap(EXCLUSION.reg(), TraitExclusion.builder().of(POISON, 0.5).of(WITHER, 0.5).of(LHTraits.FIERY, 1.0).build()))
         .tag(TRAIT_TAGS, new TagKey[]{POTION}))
      .lang("Soul Burner")
      .register();
   public static final TraitEntry<TargetEffectTrait> FREEZING = ((TraitBuilder)((TraitBuilder)L2Hostility.REGISTRATE
            .regTrait(
               "freezing",
               () -> new TargetEffectTrait(lv -> new MobEffectInstance(LCEffects.ICE, (Integer)LHConfig.SERVER.freezingTime.get() * lv)),
               new TraitConfig(30, 50, 3, 50)
            )
            .dataMap(EXCLUSION.reg(), TraitExclusion.builder().of(SLOWNESS, 0.5).of(LEVITATION, 0.5).of(BLIND, 0.5).of(CONFUSION, 0.5).build()))
         .tag(TRAIT_TAGS, new TagKey[]{POTION}))
      .lang("Freezing")
      .register();
   public static final TraitEntry<TargetEffectTrait> CURSED = ((TraitBuilder)L2Hostility.REGISTRATE
         .regTrait(
            "cursed",
            () -> new TargetEffectTrait(lv -> new MobEffectInstance(LCEffects.CURSE, (Integer)LHConfig.SERVER.curseTime.get() * lv)),
            new TraitConfig(20, 100, 3, 20)
         )
         .tag(TRAIT_TAGS, new TagKey[]{POTION}))
      .lang("Cursed")
      .register();
   public static final TraitEntry<InvisibleTrait> INVISIBLE = L2Hostility.REGISTRATE
      .<InvisibleTrait>regTrait("invisible", InvisibleTrait::new, new TraitConfig(30, 100, 1, 50))
      .addWhitelist(
         e -> e.add(
            new EntityType[]{
               EntityType.ENDERMAN,
               EntityType.SPIDER,
               EntityType.CAVE_SPIDER,
               EntityType.ZOMBIE,
               EntityType.HUSK,
               EntityType.DROWNED,
               EntityType.SKELETON,
               EntityType.STRAY,
               EntityType.BOGGED,
               EntityType.WITHER
            }
         )
      )
      .lang("Invisible")
      .register();
   public static final TraitEntry<ShulkerTrait> SHULKER = L2Hostility.REGISTRATE
      .<ShulkerTrait>regTrait(
         "shulker",
         () -> new ShulkerTrait(ChatFormatting.LIGHT_PURPLE, LHConfig.SERVER.shulkerInterval::get, BulletType.PLAIN, 0),
         new TraitConfig(50, 100, 1, 70)
      )
      .addBlacklist(e -> e.addTag(LHTagGen.SEMIBOSS))
      .desc("Shoot bullets every %s seconds after the previous bullet disappears.")
      .lang("Shulker")
      .register();
   public static final TraitEntry<ShulkerTrait> GRENADE = ((TraitBuilder)L2Hostility.REGISTRATE
         .regTrait(
            "grenade",
            () -> new ShulkerTrait(ChatFormatting.RED, LHConfig.SERVER.grenadeInterval::get, BulletType.EXPLODE, 15),
            new TraitConfig(100, 100, 5, 100)
         )
         .addBlacklist(e -> e.addTag(LHTagGen.SEMIBOSS))
         .dataMap(EXCLUSION.reg(), TraitExclusion.builder().of(SHULKER, 1.0).build()))
      .desc("Shoot explosive bullets every %s seconds after the previous bullet disappears.")
      .lang("Grenade")
      .register();
   public static final TraitEntry<FieryTrait> FIERY = L2Hostility.REGISTRATE
      .<FieryTrait>regTrait("fiery", FieryTrait::new, new TraitConfig(20, 100, 1, 20))
      .desc("Ignite attacker and attack target for %s seconds. Makes mob immune to fire.")
      .lang("Fiery")
      .register();
   public static final TraitEntry<RegenTrait> REGEN = L2Hostility.REGISTRATE
      .<RegenTrait>regTrait("regenerate", () -> new RegenTrait(ChatFormatting.RED), new TraitConfig(30, 100, 5, 50))
      .addBlacklist(e -> e.addTag(LHTagGen.EFFIMM))
      .desc("Heals %s%% of full health every second.")
      .lang("Regenerating")
      .register();
   public static final TraitEntry<DementorTrait> DEMENTOR = ((TraitBuilder)L2Hostility.REGISTRATE
         .regTrait("dementor", () -> new DementorTrait(ChatFormatting.DARK_GRAY), new TraitConfig(120, 50, 1, 150))
         .dataMap(EXCLUSION.reg(), TraitExclusion.builder().of(LHTraits.ADAPTIVE, 0.5).build()))
      .desc("Resistant to physical damage. Damage bypass armor.")
      .lang("Dementor")
      .register();
   public static final TraitEntry<DispellTrait> DISPELL = ((TraitBuilder)L2Hostility.REGISTRATE
         .regTrait("dispell", () -> new DispellTrait(ChatFormatting.DARK_PURPLE), new TraitConfig(100, 50, 3, 150))
         .dataMap(EXCLUSION.reg(), TraitExclusion.builder().of(DEMENTOR, 0.75).of(LHTraits.ADAPTIVE, 0.5).build()))
      .desc(
         "Resistant to magic damage. Damage bypass magical protections. Randomly picks %s enchanted equipment and disable enchantments on them for %s seconds."
      )
      .lang("Dispell")
      .register();
   public static final TraitEntry<AdaptingTrait> ADAPTIVE = L2Hostility.REGISTRATE
      .<AdaptingTrait>regTrait("adaptive", () -> new AdaptingTrait(ChatFormatting.GOLD), new TraitConfig(80, 50, 5, 100))
      .desc("Memorize damage types taken and stack %s%% damage reduction for those damage every time. Memorizes last %s different damage types.")
      .lang("Adaptive")
      .register();
   public static final TraitEntry<ReflectTrait> REFLECT = L2Hostility.REGISTRATE
      .<ReflectTrait>regTrait("reflect", () -> new ReflectTrait(ChatFormatting.DARK_RED), new TraitConfig(80, 50, 5, 100))
      .desc("Reflect direct physical damage as %s%% magical damage")
      .lang("Reflect")
      .register();
   public static final TraitEntry<UndyingTrait> UNDYING = L2Hostility.REGISTRATE
      .<UndyingTrait>regTrait("undying", () -> new UndyingTrait(ChatFormatting.DARK_BLUE), new TraitConfig(150, 100, 1, 150))
      .addBlacklist(e -> e.addTag(LHTagGen.SEMIBOSS))
      .desc("Mob will heal to full health every time it dies.")
      .lang("Undying")
      .register();
   public static final TraitEntry<RepellingTrait> REPELLING = L2Hostility.REGISTRATE
      .<RepellingTrait>regTrait("repelling", () -> new RepellingTrait(ChatFormatting.DARK_GREEN), new TraitConfig(80, 50, 1, 100))
      .addWhitelist(
         e -> e.add(
            new EntityType[]{
               EntityType.SKELETON,
               EntityType.STRAY,
               EntityType.PILLAGER,
               EntityType.EVOKER,
               EntityType.WITCH,
               EntityType.GUARDIAN,
               EntityType.ELDER_GUARDIAN,
               EntityType.WITHER
            }
         )
      )
      .desc("Mob will push away entities hostile to it within %s blocks, and immune to projectiles.")
      .lang("Repelling")
      .register();
   public static final TraitEntry<PullingTrait> PULLING = ((TraitBuilder)L2Hostility.REGISTRATE
         .regTrait("pulling", () -> new PullingTrait(ChatFormatting.DARK_BLUE), new TraitConfig(80, 50, 1, 100))
         .addWhitelist(e -> e.addTag(LHTagGen.MELEE_WEAPON_TARGET))
         .dataMap(EXCLUSION.reg(), TraitExclusion.builder().of(REPELLING, 1.0).build()))
      .desc("Mob will pull entities hostile to it within %s blocks.")
      .lang("Pulling")
      .register();
   public static final TraitEntry<EnderTrait> ENDER = L2Hostility.REGISTRATE
      .<EnderTrait>regTrait("teleport", () -> new EnderTrait(ChatFormatting.DARK_PURPLE), new TraitConfig(120, 100, 1, 150))
      .addBlacklist(pvd -> pvd.addTag(LHTagGen.SEMIBOSS))
      .desc("Mob will attempt to teleport to avoid physical damage and track targets.")
      .lang("Teleport")
      .register();
   public static final TraitEntry<CorrosionTrait> CORROSION = L2Hostility.REGISTRATE
      .<CorrosionTrait>regTrait("corrosion", () -> new CorrosionTrait(ChatFormatting.DARK_RED), new TraitConfig(120, 50, 3, 200))
      .desc(
         "When hit target, randomly picks %s equipments and increase their durability loss by %s. When there aren't enough equipments, increase damage by %s per piece"
      )
      .lang("Corrosion")
      .register();
   public static final TraitEntry<ErosionTrait> EROSION = ((TraitBuilder)L2Hostility.REGISTRATE
         .regTrait("erosion", () -> new ErosionTrait(ChatFormatting.DARK_BLUE), new TraitConfig(120, 50, 3, 200))
         .dataMap(EXCLUSION.reg(), TraitExclusion.builder().of(CORROSION, 1.0).build()))
      .desc(
         "When hit target, randomly picks %s equipments and reduce their durability by %s. When there aren't enough equipments, increase damage by %s per piece"
      )
      .lang("Erosion")
      .register();
   public static final TraitEntry<KillerAuraTrait> KILLER_AURA = L2Hostility.REGISTRATE
      .<KillerAuraTrait>regTrait("killer_aura", () -> new KillerAuraTrait(ChatFormatting.DARK_RED), new TraitConfig(100, 50, 3, 300))
      .desc("Deal %s magic damage to players and entities targeting it within %s blocks and apply trait effects for every %ss")
      .lang("Killer Aura")
      .register();
   public static final TraitEntry<RagnarokTrait> RAGNAROK = L2Hostility.REGISTRATE
      .<RagnarokTrait>regTrait("ragnarok", () -> new RagnarokTrait(ChatFormatting.DARK_BLUE), new TraitConfig(300, 100, 3, 600))
      .desc("When hit target, randomly picks %s equipments and seal them, which takes %ss to unseal.")
      .lang("Ragnarok")
      .register();
   public static final TraitEntry<GrowthTrait> GROWTH = L2Hostility.REGISTRATE
      .<GrowthTrait>regTrait("growth", () -> new GrowthTrait(ChatFormatting.DARK_GREEN), new TraitConfig(60, 300, 3, 100))
      .desc("Slime will grow larger when at full health. Automatically gain Regenerate trait.")
      .lang("Growth")
      .register();
   public static final TraitEntry<SplitTrait> SPLIT = L2Hostility.REGISTRATE
      .<SplitTrait>regTrait("split", () -> new SplitTrait(ChatFormatting.GREEN), new TraitConfig(70, 100, 3, 120))
      .addWhitelist(
         e -> e.add(
            new EntityType[]{
               EntityType.ZOMBIE,
               EntityType.ZOMBIE_VILLAGER,
               EntityType.ZOMBIFIED_PIGLIN,
               EntityType.DROWNED,
               EntityType.HUSK,
               EntityType.SKELETON,
               EntityType.WITHER_SKELETON,
               EntityType.STRAY,
               EntityType.SPIDER,
               EntityType.CAVE_SPIDER,
               EntityType.CREEPER,
               EntityType.VEX,
               EntityType.SILVERFISH,
               EntityType.ENDERMITE
            }
         )
      )
      .desc("When mob dies, it will split into 2 of itself with half levels but same trait. This trait reduce by 1 when split.")
      .lang("Split")
      .register();
   public static final TraitEntry<DrainTrait> DRAIN = L2Hostility.REGISTRATE
      .<DrainTrait>regTrait("drain", () -> new DrainTrait(ChatFormatting.LIGHT_PURPLE), new TraitConfig(80, 100, 3, 100))
      .desc(
         "Grants a random potion trait with same level. When hit target, remove %s beneficial effects, deal %s more damage for every harmful effects, and increase their duration by %s. At most increase to %ss."
      )
      .lang("Drain")
      .register();
   public static final TraitEntry<ReprintTrait> REPRINT = L2Hostility.REGISTRATE
      .<ReprintTrait>regTrait("reprint", () -> new ReprintTrait(ChatFormatting.LIGHT_PURPLE), new TraitConfig(100, 100, 1, 100))
      .desc("Mob will copy target enchantments, and deal %s more damage per enchantment point")
      .lang("Reprint")
      .register();
   public static final TraitEntry<CounterStrikeTrait> STRIKE = L2Hostility.REGISTRATE
      .<CounterStrikeTrait>regTrait("counter_strike", () -> new CounterStrikeTrait(ChatFormatting.WHITE), new TraitConfig(50, 100, 1, 60))
      .addWhitelist(e -> e.addTag(LHTagGen.MELEE_WEAPON_TARGET).add(EntityType.WARDEN))
      .desc("After attacked, it will attempt to perform a counter strike.")
      .lang("Counter Strike")
      .register();
   public static final TraitEntry<GravityTrait> GRAVITY = L2Hostility.REGISTRATE
      .<GravityTrait>regTrait("gravity", () -> new GravityTrait(LHEffects.GRAVITY), new TraitConfig(50, 25, 3, 80))
      .desc("Increase gravity for mobs around it. Knock attackers downward when damaged.")
      .lang("Gravity")
      .register();
   public static final TraitEntry<AuraEffectTrait> MOONWALK = ((TraitBuilder)L2Hostility.REGISTRATE
         .regTrait("moonwalk", () -> new AuraEffectTrait(LHEffects.MOONWALK), new TraitConfig(50, 25, 3, 80))
         .dataMap(EXCLUSION.reg(), TraitExclusion.builder().of(GRAVITY, 1.0).build()))
      .desc("Decrease gravity for mobs around it")
      .lang("Moonwalk")
      .register();
   public static final TraitEntry<ArenaTrait> ARENA = L2Hostility.REGISTRATE
      .<ArenaTrait>regTrait("arena", ArenaTrait::new, new TraitConfig(1000, 1, 1, 50))
      .addWhitelist(pvd -> pvd.addTag(LHTagGen.SEMIBOSS))
      .desc("Players around it cannot place or break blocks. Immune damage from entities not affected by this.")
      .lang("Arena")
      .register();
   public static final TraitEntry<MasterTrait> MASTER = L2Hostility.REGISTRATE
      .<MasterTrait>regTrait("master", () -> new MasterTrait(ChatFormatting.GOLD), new TraitConfig(200, 50, 1, 200))
      .desc("Summons minions around the mob. Some minions will protect master.")
      .lang("Master")
      .register();

   public static void register() {
   }
}
