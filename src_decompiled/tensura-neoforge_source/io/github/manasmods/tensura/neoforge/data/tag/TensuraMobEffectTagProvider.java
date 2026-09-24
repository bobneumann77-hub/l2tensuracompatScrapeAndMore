package io.github.manasmods.tensura.neoforge.data.tag;

import io.github.manasmods.tensura.data.TensuraTags;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

public class TensuraMobEffectTagProvider extends TagsProvider<MobEffect> {
   public TensuraMobEffectTagProvider(PackOutput output, CompletableFuture<Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
      super(output, Registries.MOB_EFFECT, lookupProvider, "tensura", existingFileHelper);
   }

   protected void addTags(@NotNull Provider provider) {
      this.tag(TensuraTags.MobEffects.HIDDEN_ICON)
         .add(
            new ResourceKey[]{
               TensuraMobEffects.ALLY_BOOST.getKey(),
               TensuraMobEffects.AUDITORY_SENSE.getKey(),
               TensuraMobEffects.BATS_MODE.getKey(),
               TensuraMobEffects.BEAST_TRANSFORMATION.getKey(),
               TensuraMobEffects.BLACK_BURN.getKey(),
               TensuraMobEffects.DIAMOND_PATH.getKey(),
               TensuraMobEffects.DISINTEGRATING.getKey(),
               TensuraMobEffects.DRAGON_MODE.getKey(),
               TensuraMobEffects.ENERGY_BLOCKADE.getKey(),
               TensuraMobEffects.EARTH_LOCK.getKey(),
               TensuraMobEffects.ENGORGEMENT.getKey(),
               TensuraMobEffects.FALSIFIER.getKey(),
               TensuraMobEffects.FATE_CHANGE.getKey(),
               TensuraMobEffects.GUARDED.getKey(),
               TensuraMobEffects.FUTURE_VISION.getKey(),
               TensuraMobEffects.HAKI_COAT.getKey(),
               TensuraMobEffects.HOLY_DAMAGE.getKey(),
               TensuraMobEffects.INSTANT_REGENERATION.getKey(),
               TensuraMobEffects.INSPIRATION.getKey(),
               TensuraMobEffects.LUST_DRAIN.getKey(),
               TensuraMobEffects.LUST_EMBRACEMENT.getKey(),
               TensuraMobEffects.MAD_OGRE.getKey(),
               TensuraMobEffects.MAGIC_INTERFERENCE.getKey(),
               TensuraMobEffects.MAGIC_AURA.getKey(),
               TensuraMobEffects.MAGIC_ELEMENTAL_TRANSFORMATION.getKey(),
               TensuraMobEffects.MIND_CONTROL.getKey(),
               TensuraMobEffects.MOVEMENT_INTERFERENCE.getKey(),
               TensuraMobEffects.OGRE_BERSERKER.getKey(),
               TensuraMobEffects.PRESENCE_CONCEALMENT.getKey(),
               TensuraMobEffects.PRESENCE_SENSE.getKey(),
               TensuraMobEffects.REST.getKey(),
               TensuraMobEffects.SELF_REGENERATION.getKey(),
               TensuraMobEffects.SHADOW_STEP.getKey(),
               TensuraMobEffects.SPEARHEAD.getKey(),
               TensuraMobEffects.FLASHED_BLINDNESS.getKey(),
               TensuraMobEffects.TRUE_BLINDNESS.getKey(),
               TensuraMobEffects.WARPING.getKey(),
               TensuraMobEffects.WIND_PROTECTION.getKey()
            }
         );
      this.tag(TensuraTags.MobEffects.AFFECTED_BY_ANTI_SKILL)
         .addTag(TensuraTags.MobEffects.SKILL_EFFECT)
         .add(new ResourceKey[]{TensuraMobEffects.CONFUSION.getKey(), TensuraMobEffects.WARPING.getKey(), TensuraMobEffects.SLEEP.getKey()});
      this.tag(TensuraTags.MobEffects.AFFECTED_BY_LAW_MANIPULATION)
         .add(
            new ResourceKey[]{
               MobEffects.HUNGER.getKey(),
               MobEffects.POISON.getKey(),
               MobEffects.BLINDNESS.getKey(),
               MobEffects.CONFUSION.getKey(),
               MobEffects.DARKNESS.getKey(),
               MobEffects.DIG_SLOWDOWN.getKey(),
               MobEffects.MOVEMENT_SLOWDOWN.getKey(),
               MobEffects.WEAKNESS.getKey(),
               TensuraMobEffects.BURDEN.getKey(),
               TensuraMobEffects.FRAGILITY.getKey(),
               TensuraMobEffects.CURSE.getKey(),
               TensuraMobEffects.FATAL_POISON.getKey(),
               TensuraMobEffects.PARALYSIS.getKey(),
               TensuraMobEffects.PETRIFICATION.getKey(),
               TensuraMobEffects.HYPNOSIS.getKey(),
               TensuraMobEffects.MAGIC_INTERFERENCE.getKey()
            }
         );
      this.tag(TensuraTags.MobEffects.IGNORE_ANTI_MAGICULE)
         .add(
            new ResourceKey[]{
               MobEffects.GLOWING.getKey(),
               TensuraMobEffects.INFINITE_IMPRISONMENT.getKey(),
               TensuraMobEffects.LUST_EMBRACEMENT.getKey(),
               TensuraMobEffects.OPPRESSION.getKey(),
               TensuraMobEffects.SOUL_DRAIN.getKey(),
               TensuraMobEffects.DROWSINESS.getKey(),
               TensuraMobEffects.CURSE.getKey(),
               TensuraMobEffects.SPATIAL_BLOCKADE.getKey(),
               TensuraMobEffects.MIND_CONTROL.getKey(),
               TensuraMobEffects.MOVEMENT_INTERFERENCE.getKey(),
               TensuraMobEffects.ENERGY_BLOCKADE.getKey(),
               TensuraMobEffects.DISINTEGRATING.getKey(),
               TensuraMobEffects.ANTI_SKILL.getKey(),
               TensuraMobEffects.ANTI_SHOCK.getKey(),
               TensuraMobEffects.ANTI_MAGIC.getKey(),
               TensuraMobEffects.SLEEP.getKey(),
               TensuraMobEffects.MAGICULE_POISON.getKey()
            }
         );
      this.tag(TensuraTags.MobEffects.SPIRITUAL_AFFECTED)
         .addTag(TensuraTags.MobEffects.SKILL_BUFF)
         .add(
            new ResourceKey[]{
               TensuraMobEffects.ANTI_SKILL.getKey(),
               TensuraMobEffects.ANTI_MAGIC.getKey(),
               TensuraMobEffects.ANTI_SHOCK.getKey(),
               TensuraMobEffects.DROWSINESS.getKey(),
               TensuraMobEffects.INFINITE_IMPRISONMENT.getKey(),
               TensuraMobEffects.MIND_CONTROL.getKey(),
               TensuraMobEffects.SPATIAL_BLOCKADE.getKey(),
               TensuraMobEffects.SOUL_DRAIN.getKey(),
               TensuraMobEffects.MAGIC_INTERFERENCE.getKey()
            }
         )
         .add(
            new ResourceKey[]{
               TensuraMobEffects.CURSE.getKey(),
               TensuraMobEffects.CONFUSION.getKey(),
               TensuraMobEffects.DISINTEGRATING.getKey(),
               TensuraMobEffects.ENERGY_BLOCKADE.getKey(),
               TensuraMobEffects.FEAR.getKey(),
               TensuraMobEffects.HYPNOSIS.getKey(),
               TensuraMobEffects.ILLUSION_BOOST.getKey(),
               TensuraMobEffects.INSANITY.getKey(),
               TensuraMobEffects.MAGICULE_POISON.getKey(),
               TensuraMobEffects.MAGICULE_REGENERATION.getKey(),
               TensuraMobEffects.SILENCE.getKey(),
               TensuraMobEffects.SLEEP.getKey(),
               TensuraMobEffects.RAMPAGE.getKey(),
               TensuraMobEffects.FLASHED_BLINDNESS.getKey(),
               TensuraMobEffects.TRUE_BLINDNESS.getKey()
            }
         );
      this.tag(TensuraTags.MobEffects.TRANSFORMATION)
         .add(
            new ResourceKey[]{
               TensuraMobEffects.BEAST_TRANSFORMATION.getKey(),
               TensuraMobEffects.DRAGON_MODE.getKey(),
               TensuraMobEffects.MAGIC_ELEMENTAL_TRANSFORMATION.getKey(),
               TensuraMobEffects.OGRE_BERSERKER.getKey()
            }
         );
      this.tag(TensuraTags.MobEffects.SKILL_BUFF)
         .add(
            new ResourceKey[]{
               TensuraMobEffects.ALLY_BOOST.getKey(),
               TensuraMobEffects.AUDITORY_SENSE.getKey(),
               TensuraMobEffects.AURA_SWORD.getKey(),
               TensuraMobEffects.BEAST_TRANSFORMATION.getKey(),
               TensuraMobEffects.DRAGON_MODE.getKey(),
               TensuraMobEffects.EARTH_LOCK.getKey(),
               TensuraMobEffects.ENEMY_SEARCH.getKey(),
               TensuraMobEffects.ENGORGEMENT.getKey(),
               TensuraMobEffects.FALSIFIER.getKey(),
               TensuraMobEffects.FATE_CHANGE.getKey(),
               TensuraMobEffects.FUTURE_VISION.getKey(),
               TensuraMobEffects.GUARDED.getKey(),
               TensuraMobEffects.HAKI_COAT.getKey(),
               TensuraMobEffects.HEALTHCARE.getKey(),
               TensuraMobEffects.INSTANT_REGENERATION.getKey(),
               TensuraMobEffects.INSPIRATION.getKey(),
               TensuraMobEffects.LUST_DRAIN.getKey(),
               TensuraMobEffects.LUST_EMBRACEMENT.getKey(),
               TensuraMobEffects.MAD_OGRE.getKey(),
               TensuraMobEffects.MAGIC_BARRIER.getKey(),
               TensuraMobEffects.MAGIC_AURA.getKey(),
               TensuraMobEffects.MAGIC_ELEMENTAL_TRANSFORMATION.getKey(),
               TensuraMobEffects.OGRE_BERSERKER.getKey(),
               TensuraMobEffects.OGRE_GUILLOTINE.getKey(),
               TensuraMobEffects.PHYSICAL_BARRIER.getKey(),
               TensuraMobEffects.PRESENCE_CONCEALMENT.getKey(),
               TensuraMobEffects.PRESENCE_SENSE.getKey(),
               TensuraMobEffects.PROTECTION.getKey(),
               TensuraMobEffects.REINFORCEMENT.getKey(),
               TensuraMobEffects.REST.getKey(),
               TensuraMobEffects.SELF_REGENERATION.getKey(),
               TensuraMobEffects.SEVERANCE_BLADE.getKey(),
               TensuraMobEffects.SHADOW_STEP.getKey(),
               TensuraMobEffects.SPEARHEAD.getKey(),
               TensuraMobEffects.STRENGTHEN.getKey(),
               TensuraMobEffects.WIND_PROTECTION.getKey()
            }
         );
      this.tag(TensuraTags.MobEffects.SKILL_DEBUFF)
         .add(
            new ResourceKey[]{
               TensuraMobEffects.ANTI_MAGIC.getKey(),
               TensuraMobEffects.ANTI_SHOCK.getKey(),
               TensuraMobEffects.SLEEP.getKey(),
               TensuraMobEffects.BLACK_BURN.getKey(),
               TensuraMobEffects.DROWSINESS.getKey(),
               TensuraMobEffects.INFECTION.getKey(),
               TensuraMobEffects.INFINITE_IMPRISONMENT.getKey(),
               TensuraMobEffects.MIND_CONTROL.getKey(),
               TensuraMobEffects.OPPRESSION.getKey(),
               TensuraMobEffects.SPATIAL_BLOCKADE.getKey(),
               TensuraMobEffects.SOUL_DRAIN.getKey(),
               TensuraMobEffects.MAGIC_INTERFERENCE.getKey(),
               TensuraMobEffects.MOVEMENT_INTERFERENCE.getKey()
            }
         );
      this.tag(TensuraTags.MobEffects.SKILL_EFFECT).addTag(TensuraTags.MobEffects.SKILL_BUFF).addTag(TensuraTags.MobEffects.SKILL_DEBUFF);
      this.tag(TensuraTags.MobEffects.INCURABLE_BY_MILK)
         .addTags(new TagKey[]{TensuraTags.MobEffects.HIDDEN_ICON})
         .addTags(new TagKey[]{TensuraTags.MobEffects.SKILL_EFFECT})
         .add(
            new ResourceKey[]{
               TensuraMobEffects.CURSE.getKey(),
               TensuraMobEffects.FEAR.getKey(),
               TensuraMobEffects.FROST.getKey(),
               TensuraMobEffects.ILLUSION_BOOST.getKey(),
               TensuraMobEffects.INSANITY.getKey(),
               TensuraMobEffects.INFECTION.getKey(),
               TensuraMobEffects.MAGICULE_POISON.getKey(),
               TensuraMobEffects.PETRIFICATION.getKey(),
               TensuraMobEffects.SLEEP.getKey(),
               TensuraMobEffects.RAMPAGE.getKey(),
               TensuraMobEffects.WEBBED.getKey()
            }
         )
         .add(
            new ResourceKey[]{
               TensuraMobEffects.ANTI_SKILL.getKey(),
               TensuraMobEffects.CONFUSION.getKey(),
               TensuraMobEffects.MAGICULE_REGENERATION.getKey(),
               TensuraMobEffects.WARPING.getKey()
            }
         );
   }
}
