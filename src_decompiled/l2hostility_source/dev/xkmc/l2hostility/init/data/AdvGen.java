package dev.xkmc.l2hostility.init.data;

import com.tterrag.registrate.providers.RegistrateAdvancementProvider;
import dev.xkmc.l2complements.init.registrate.LCEffects;
import dev.xkmc.l2core.serial.advancements.AdvancementGenerator;
import dev.xkmc.l2core.serial.advancements.CriterionBuilder;
import dev.xkmc.l2core.serial.advancements.AdvancementGenerator.TabBuilder;
import dev.xkmc.l2core.serial.advancements.AdvancementGenerator.TabBuilder.Entry;
import dev.xkmc.l2hostility.content.traits.base.AttributeTrait;
import dev.xkmc.l2hostility.content.traits.base.MobTrait;
import dev.xkmc.l2hostility.content.traits.base.SelfEffectTrait;
import dev.xkmc.l2hostility.content.traits.common.AdaptingTrait;
import dev.xkmc.l2hostility.content.traits.common.RegenTrait;
import dev.xkmc.l2hostility.content.traits.goals.EnderTrait;
import dev.xkmc.l2hostility.content.traits.legendary.DispellTrait;
import dev.xkmc.l2hostility.content.traits.legendary.RagnarokTrait;
import dev.xkmc.l2hostility.content.traits.legendary.UndyingTrait;
import dev.xkmc.l2hostility.init.L2Hostility;
import dev.xkmc.l2hostility.init.advancements.KillTraitCountTrigger;
import dev.xkmc.l2hostility.init.advancements.KillTraitEffectTrigger;
import dev.xkmc.l2hostility.init.advancements.KillTraitFlameTrigger;
import dev.xkmc.l2hostility.init.advancements.KillTraitsTrigger;
import dev.xkmc.l2hostility.init.registrate.LHItems;
import dev.xkmc.l2hostility.init.registrate.LHTraits;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.ConsumeItemTrigger.TriggerInstance;
import net.minecraft.advancements.critereon.ItemPredicate.Builder;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class AdvGen {
   public static void genAdvancements(RegistrateAdvancementProvider pvd) {
      AdvancementGenerator gen = new AdvancementGenerator(pvd, "l2hostility");
      Entry root = new TabBuilder(gen, "hostility")
         .root(
            "root",
            ((EnderTrait)LHTraits.ENDER.get()).asItem(),
            CriterionBuilder.item((Item)LHItems.HOSTILITY_ORB.get()),
            "Welcome to L2Hostility",
            "Your survival guide"
         )
         .root();
      root.root()
         .patchouli(
            L2Hostility.REGISTRATE,
            CriterionBuilder.item((Item)LHItems.HOSTILITY_ORB.get()),
            L2Hostility.PATCHOULI,
            "Intro to L2Hostility",
            "Read the hostility guide"
         )
         .root()
         .create("detector", (Item)LHItems.DETECTOR.get(), CriterionBuilder.item((Item)LHItems.DETECTOR.get()), "Safety Compass", "Obtain Hostility Detector")
         .create(
            "glasses",
            (Item)LHItems.DETECTOR_GLASSES.get(),
            CriterionBuilder.item((Item)LHItems.DETECTOR_GLASSES.get()),
            "The Invisible Threats",
            "Obtain Detector Glasses to find out invisible mobs"
         )
         .root()
         .create("kill_first", Items.IRON_SWORD, CriterionBuilder.one(KillTraitCountTrigger.ins(1)), "Worthy Opponent", "Kill a mob with traits")
         .create("kill_5_traits", Items.DIAMOND_SWORD, CriterionBuilder.one(KillTraitCountTrigger.ins(5)), "Legendary Battle", "Kill a mob with 5 traits")
         .type(AdvancementType.GOAL)
         .create("kill_10_traits", Items.NETHERITE_SWORD, CriterionBuilder.one(KillTraitCountTrigger.ins(10)), "Legend Slayer", "Kill a mob with 10 traits")
         .type(AdvancementType.CHALLENGE)
         .root()
         .enter()
         .create(
            "kill_tanky",
            ((SelfEffectTrait)LHTraits.PROTECTION.get()).asItem(),
            CriterionBuilder.one(KillTraitsTrigger.ins((MobTrait)LHTraits.PROTECTION.get(), (MobTrait)LHTraits.TANK.get())),
            "Can Opener",
            "Kill a mob with Protection and Tanky Trait"
         )
         .type(AdvancementType.GOAL)
         .create(
            "kill_adaptive",
            ((AdaptingTrait)LHTraits.ADAPTIVE.get()).asItem(),
            CriterionBuilder.one(
               KillTraitsTrigger.ins(
                  (MobTrait)LHTraits.PROTECTION.get(), (MobTrait)LHTraits.REGEN.get(), (MobTrait)LHTraits.TANK.get(), (MobTrait)LHTraits.ADAPTIVE.get()
               )
            ),
            "Counter-Defensive Measure",
            "Kill a mob with Protection, Regeneration, Tanky, and Adaptive Trait"
         )
         .type(AdvancementType.CHALLENGE)
         .create(
            "kill_dementor",
            ((DispellTrait)LHTraits.DISPELL.get()).asItem(),
            CriterionBuilder.one(KillTraitsTrigger.ins((MobTrait)LHTraits.DEMENTOR.get(), (MobTrait)LHTraits.DISPELL.get())),
            "Immunity Invalidator",
            "Kill a mob with Dementor and Dispell Trait"
         )
         .type(AdvancementType.CHALLENGE)
         .create(
            "kill_ragnarok",
            ((RagnarokTrait)LHTraits.RAGNAROK.get()).asItem(),
            CriterionBuilder.one(KillTraitsTrigger.ins((MobTrait)LHTraits.KILLER_AURA.get(), (MobTrait)LHTraits.RAGNAROK.get())),
            "The Final Battle",
            "Kill a mob with Killer Aura and Ragnarok Trait"
         )
         .type(AdvancementType.CHALLENGE)
         .root()
         .enter()
         .create(
            "effect_kill_regen",
            ((RegenTrait)LHTraits.REGEN.get()).asItem(),
            CriterionBuilder.one(KillTraitEffectTrigger.ins((MobTrait)LHTraits.REGEN.get(), LCEffects.CURSE)),
            "Prevent Healing",
            "Use curse effect on mobs with Regeneration and kill it"
         )
         .type(AdvancementType.GOAL)
         .create(
            "effect_kill_adaptive",
            ((AdaptingTrait)LHTraits.ADAPTIVE.get()).asItem(),
            CriterionBuilder.or()
               .add(KillTraitEffectTrigger.ins((MobTrait)LHTraits.ADAPTIVE.get(), LCEffects.FLAME))
               .add(KillTraitEffectTrigger.ins((MobTrait)LHTraits.ADAPTIVE.get(), MobEffects.POISON))
               .add(KillTraitEffectTrigger.ins((MobTrait)LHTraits.ADAPTIVE.get(), MobEffects.WITHER))
               .add(KillTraitFlameTrigger.ins((MobTrait)LHTraits.ADAPTIVE.get(), KillTraitFlameTrigger.Type.FLAME)),
            "Prevent Adaption",
            "Use poison/wither/soul flame effect or fire on mobs with Adaptive and kill it"
         )
         .type(AdvancementType.GOAL)
         .create(
            "effect_kill_undead",
            ((UndyingTrait)LHTraits.UNDYING.get()).asItem(),
            CriterionBuilder.one(KillTraitEffectTrigger.ins((MobTrait)LHTraits.UNDYING.get(), LCEffects.CURSE)),
            "Prevent Reviving",
            "Use curse effect on mobs with Undying and kill it"
         )
         .type(AdvancementType.CHALLENGE)
         .create(
            "effect_kill_teleport",
            ((EnderTrait)LHTraits.ENDER.get()).asItem(),
            CriterionBuilder.one(KillTraitEffectTrigger.ins((MobTrait)LHTraits.ENDER.get(), LCEffects.INCARCERATE)),
            "Prevent Teleporting",
            "Use incarceration effect on mobs with Teleport and kill it"
         )
         .type(AdvancementType.CHALLENGE);
      Entry ingot = root.root()
         .enter()
         .create("ingot", (Item)LHItems.CHAOS_INGOT.get(), CriterionBuilder.item((Item)LHItems.CHAOS_INGOT.get()), "Pandora's Box", "Obtain a Chaos Ingot");
      ingot.create("sloth", (Item)LHItems.CURSE_SLOTH.get(), CriterionBuilder.item((Item)LHItems.CURSE_SLOTH.get()), "I want a break", "Obtain Curse of Sloth")
         .type(AdvancementType.GOAL);
      Entry trait = ingot.create(
            "envy", (Item)LHItems.CURSE_ENVY.get(), CriterionBuilder.item((Item)LHItems.CURSE_ENVY.get()), "I want that!", "Obtain Curse of Envy"
         )
         .type(AdvancementType.GOAL)
         .create(
            "trait", ((AttributeTrait)LHTraits.TANK.get()).asItem(), CriterionBuilder.item(LHTagGen.TRAIT_ITEM), "Gate to the New World", "Obtain a trait item"
         );
      trait.create(
            "greed", (Item)LHItems.CURSE_GREED.get(), CriterionBuilder.item((Item)LHItems.CURSE_GREED.get()), "The More the Better", "Obtain Curse of Greed"
         )
         .type(AdvancementType.GOAL);
      trait.create("lust", (Item)LHItems.CURSE_LUST.get(), CriterionBuilder.item((Item)LHItems.CURSE_LUST.get()), "Naked Corpse", "Obtain Curse of Lust")
         .type(AdvancementType.GOAL);
      Entry miracle = trait.create(
            "gluttony",
            (Item)LHItems.CURSE_GLUTTONY.get(),
            CriterionBuilder.item((Item)LHItems.CURSE_GLUTTONY.get()),
            "Hostility Unlimited",
            "Obtain Curse of Gluttony"
         )
         .create(
            "miracle",
            (Item)LHItems.MIRACLE_INGOT.get(),
            CriterionBuilder.item((Item)LHItems.MIRACLE_INGOT.get()),
            "Miracle of the World",
            "Obtain Miracle Ingot"
         );
      trait.create(
            "breed",
            ((RegenTrait)LHTraits.REGEN.get()).asItem(),
            CriterionBuilder.one(TriggerInstance.usedItem(Builder.item().of(LHTagGen.TRAIT_ITEM))),
            "Breeding Mobs",
            "Use a trait item on mobs"
         )
         .create(
            "imagine_breaker",
            LHItems.IMAGINE_BREAKER.asStack(),
            CriterionBuilder.item((Item)LHItems.IMAGINE_BREAKER.get()),
            "Reality Breakthrough",
            "Obtain Imagine Breaker"
         )
         .type(AdvancementType.CHALLENGE);
      miracle.create("wrath", (Item)LHItems.CURSE_WRATH.get(), CriterionBuilder.item((Item)LHItems.CURSE_WRATH.get()), "Revenge Time", "Obtain Curse of Wrath")
         .type(AdvancementType.CHALLENGE);
      miracle.create(
            "pride", (Item)LHItems.CURSE_PRIDE.get(), CriterionBuilder.item((Item)LHItems.CURSE_PRIDE.get()), "King of Hostility", "Obtain Curse of Pride"
         )
         .type(AdvancementType.CHALLENGE);
      miracle.create("abrahadabra", (Item)LHItems.ABRAHADABRA.get(), CriterionBuilder.item((Item)LHItems.ABRAHADABRA.get()), "The Finale", "Obtain Abrahadabra")
         .type(AdvancementType.CHALLENGE);
      root.finish();
   }
}
