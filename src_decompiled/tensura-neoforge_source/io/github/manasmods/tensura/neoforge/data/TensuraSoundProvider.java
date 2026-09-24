package io.github.manasmods.tensura.neoforge.data;

import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;
import net.neoforged.neoforge.common.data.SoundDefinition.Sound;
import net.neoforged.neoforge.common.data.SoundDefinition.SoundType;

public class TensuraSoundProvider extends SoundDefinitionsProvider {
   public TensuraSoundProvider(PackOutput output, ExistingFileHelper helper) {
      super(output, "tensura", helper);
   }

   private ResourceLocation location(String location) {
      return ResourceLocation.fromNamespaceAndPath("tensura", location);
   }

   private ResourceLocation minecraft(String location) {
      return ResourceLocation.withDefaultNamespace(location);
   }

   public static String getSubtitle(SoundEvent event) {
      ResourceLocation location = event.getLocation();
      return "subtitles." + location.getNamespace() + "." + location.getPath();
   }

   protected void addWithSubtitle(SoundEvent soundEvent, SoundDefinition definition) {
      super.add(soundEvent, definition.subtitle(getSubtitle(soundEvent)));
   }

   public void registerSounds() {
      this.registerAbilities();
      this.registerEntities();
      this.addWithSubtitle((SoundEvent)TensuraSoundEvents.ACID_SIZZLE.get(), definition().with(defaultSound(this.location("effect/acid_sizzle")).volume(0.8)));
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.WIND_BLOW.get(),
         definition()
            .with(
               new Sound[]{
                  defaultSound(this.minecraft("mob/breeze/idle1")),
                  defaultSound(this.minecraft("mob/breeze/idle2")),
                  defaultSound(this.minecraft("mob/breeze/idle3")),
                  defaultSound(this.minecraft("mob/breeze/idle4"))
               }
            )
      );
   }

   public void registerAbilities() {
      this.addWithSubtitle((SoundEvent)TensuraSoundEvents.NANODA.get(), definition().with(defaultSound(this.location("music/nanoda")).volume(0.5F)));
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.BUFF_ACTIVATE.get(), definition().with(defaultSound(this.location("ability/buff_activate")).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.BUFF_DEACTIVATE.get(),
         definition().with(defaultSound(this.location("ability/buff_deactivate")).volume(0.8).pitch(0.75F))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.DEBUFF_ACTIVATE.get(), definition().with(defaultSound(this.location("ability/debuff_activate")).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.DEBUFF_DEACTIVATE.get(), definition().with(defaultSound(this.location("ability/debuff_deactivate")).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.DEFENCE_ACTIVATE.get(), definition().with(defaultSound(this.location("ability/defence_activate")).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.DEFENCE_DEACTIVATE.get(), definition().with(defaultSound(this.location("ability/defence_deactivate")).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), definition().with(defaultSound(this.location("ability/generic_cast")).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(), definition().with(defaultSound(this.location("ability/generic_cast_fail")).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.GENERIC_UNCAST.get(), definition().with(defaultSound(this.location("ability/generic_uncast")).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.GENERIC_HEAL.get(), definition().with(defaultSound(this.location("ability/generic_heal")).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.GENERIC_SPLIT.get(), definition().with(defaultSound(this.location("ability/generic_split")).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.ENERGY_DRAIN.get(), definition().with(defaultSound(this.location("ability/energy_drain")).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.PRESENCE_CONCEALMENT.get(), definition().with(defaultSound(this.location("ability/presence_concealment")).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.CAST_DARK.get(), definition().with(defaultSound(this.location("ability/elemental/dark_cast")).volume(0.46))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.CAST_EARTH.get(), definition().with(defaultSound(this.location("ability/elemental/earth_cast")).volume(0.46))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.CAST_FIRE.get(), definition().with(defaultSound(this.location("ability/elemental/fire_cast")).volume(0.46))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.CAST_LIGHT.get(), definition().with(defaultSound(this.location("ability/elemental/light_cast")).volume(0.2))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.CAST_LIGHTNING.get(), definition().with(defaultSound(this.location("ability/elemental/lightning_cast")).volume(0.46))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.CAST_ICE.get(), definition().with(defaultSound(this.location("ability/elemental/ice_cast")).volume(0.46))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.CAST_SPACE.get(), definition().with(defaultSound(this.location("ability/elemental/space_cast")).volume(0.46))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.CAST_WATER.get(), definition().with(defaultSound(this.location("ability/elemental/water_cast")).volume(0.46))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.CAST_WIND.get(), definition().with(defaultSound(this.location("ability/elemental/wind_cast")).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.BLOOD_RAY.get(), definition().with(defaultSound(this.location("ability/skill/blood_ray")).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.BREATH_FIRE.get(), definition().with(defaultSound(this.location("ability/skill/fire_breath")).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.BREATH_POISON.get(), definition().with(defaultSound(this.location("ability/skill/poison_breath")).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.BREATH_THUNDER.get(), definition().with(defaultSound(this.location("ability/skill/thunder_breath")).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.BREATH_WATER.get(), definition().with(defaultSound(this.location("ability/skill/water_breath")).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.BREATH_WIND.get(), definition().with(defaultSound(this.location("ability/skill/wind_breath")).volume(0.8))
      );
      this.addWithSubtitle((SoundEvent)TensuraSoundEvents.COERCION.get(), definition().with(defaultSound(this.location("ability/skill/coercion")).volume(0.8)));
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.MIST_RELEASE.get(), definition().with(defaultSound(this.location("ability/skill/mist_release")).volume(0.13))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.TRANSFORM_BEAST.get(), definition().with(defaultSound(this.location("ability/skill/beast_transform")).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.TRANSFORM_DRAGON.get(), definition().with(defaultSound(this.location("ability/skill/dragon_transform")).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.TRANSFORM_OGRE.get(), definition().with(defaultSound(this.location("ability/skill/ogre_berserker")).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.TRANSFORM_BERSERKER.get(), definition().with(defaultSound(this.location("ability/skill/berserker")).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.HAKI_START.get(), definition().with(defaultSound(this.location("ability/skill/haki_start")).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.HAKI_LOOP.get(), definition().with(defaultSound(this.location("ability/skill/haki_loop")).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.BARRIER_BREAK.get(), definition().with(defaultSound(this.location("ability/skill/barrier_break")).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.STICKY_STEEL_THREAD.get(),
         definition()
            .with(
               new Sound[]{
                  defaultSound(this.location("ability/skill/sticky_steel_thread_1")).volume(0.8),
                  defaultSound(this.location("ability/skill/sticky_steel_thread_2")).volume(0.8)
               }
            )
      );
      this.addWithSubtitle((SoundEvent)TensuraSoundEvents.EATER.get(), definition().with(defaultSound(this.location("ability/skill/eater")).volume(0.8)));
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.MUSIC_BLAST.get(), definition().with(defaultSound(this.location("ability/skill/musician_blast")).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.MUSIC_REQUIEM.get(), definition().with(defaultSound(this.location("ability/skill/musician_requiem")).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.SPATIAL_STORAGE.get(), definition().with(defaultSound(this.location("ability/skill/spatial_storage")).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.PREDATION.get(), definition().with(defaultSound(this.location("ability/skill/predation")).volume(1.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.REFLECTION.get(), definition().with(defaultSound(this.location("ability/skill/reflection")).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.EARTHSHATTER_KICK.get(),
         definition().with(defaultSound(this.location("ability/battlewill/earthshatter_kick")).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.INSTANT_MOVE.get(), definition().with(defaultSound(this.location("ability/battlewill/instant_move")).volume(0.46F))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.DISINTEGRATION.get(), definition().with(defaultSound(this.location("ability/magic/disintegration")).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.MEGIDDO_SHOOT.get(), definition().with(defaultSound(this.location("ability/magic/megiddo")).volume(0.46F))
      );
      this.addWithSubtitle((SoundEvent)TensuraSoundEvents.SWIPE.get(), definition().with(defaultSound(this.location("ability/magic/swipe")).volume(0.8)));
   }

   public void registerEntities() {
      this.addWithSubtitle((SoundEvent)TensuraSoundEvents.BIG_BITE.get(), definition().with(defaultSound(this.minecraft("mob/evocation_illager/fangs"))));
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.GENERIC_SPLASH.get(), definition().with(defaultSound(this.location("entity/generic_splash")).volume(0.13))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.GENERIC_WATER_JUMP.get(),
         definition()
            .with(
               new Sound[]{
                  defaultSound(this.minecraft("mob/dolphin/jump1")),
                  defaultSound(this.minecraft("mob/dolphin/jump2")),
                  defaultSound(this.minecraft("mob/dolphin/jump3"))
               }
            )
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.ARMORSAURUS_AMBIENT.get(), definition().with(defaultSound(this.location("entity/dino/armorsaurus_idle")).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.ARMORSAURUS_HURT.get(), definition().with(defaultSound(this.location("entity/dino/armorsaurus_hurt")).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.ARMORSAURUS_DEATH.get(), definition().with(defaultSound(this.location("entity/dino/armorsaurus_death")).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.BARGHEST_AMBIENT.get(), definition().with(defaultSound(this.location("entity/barghest/barghest_idle")).volume(0.13))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.BARGHEST_AGGRO.get(), definition().with(defaultSound(this.location("entity/barghest/barghest_aggro")).volume(0.13))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.BARGHEST_HURT.get(), definition().with(defaultSound(this.location("entity/barghest/barghest_hurt")).volume(0.13))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.BARGHEST_DEATH.get(), definition().with(defaultSound(this.location("entity/barghest/barghest_death")).volume(0.46))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.BASILISK_AMBIENT.get(),
         definition()
            .with(
               new Sound[]{
                  defaultSound(this.location("entity/basilisk/basilisk_idle")).volume(0.46),
                  defaultSound(this.location("entity/basilisk/basilisk_idle_long")).volume(0.46)
               }
            )
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.BASILISK_HURT.get(), definition().with(defaultSound(this.location("entity/basilisk/basilisk_hurt")).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.BASILISK_DEATH.get(), definition().with(defaultSound(this.location("entity/basilisk/basilisk_death")).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.BEAR_AMBIENT.get(), definition().with(defaultSound(this.location("entity/bear/bear_idle")).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.BEAR_ATTACK.get(), definition().with(defaultSound(this.location("entity/bear/bear_attack")).volume(0.8))
      );
      this.addWithSubtitle((SoundEvent)TensuraSoundEvents.BEAR_HURT.get(), definition().with(defaultSound(this.location("entity/bear/bear_hurt")).volume(0.8)));
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.BEAR_DEATH.get(), definition().with(defaultSound(this.location("entity/bear/bear_death")).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.BIRD_AMBIENT.get(),
         definition()
            .with(
               new Sound[]{
                  defaultSound(this.location("entity/bird/bird_idle")).volume(0.46), defaultSound(this.location("entity/bird/bird_idle_2")).volume(0.8)
               }
            )
      );
      this.addWithSubtitle((SoundEvent)TensuraSoundEvents.BIRD_HURT.get(), definition().with(defaultSound(this.location("entity/bird/bird_hurt")).volume(0.8)));
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.BIRD_DEATH.get(), definition().with(defaultSound(this.location("entity/bird/bird_death")).volume(0.8))
      );
      this.addWithSubtitle((SoundEvent)TensuraSoundEvents.CAT_AMBIENT.get(), definition().with(defaultSound(this.location("entity/cat/cat_idle")).volume(0.46)));
      this.addWithSubtitle((SoundEvent)TensuraSoundEvents.CAT_AGGRO.get(), definition().with(defaultSound(this.location("entity/cat/cat_aggro")).volume(0.46)));
      this.addWithSubtitle((SoundEvent)TensuraSoundEvents.CAT_HURT.get(), definition().with(defaultSound(this.location("entity/cat/cat_hurt")).volume(0.46)));
      this.addWithSubtitle((SoundEvent)TensuraSoundEvents.CAT_DEATH.get(), definition().with(defaultSound(this.location("entity/cat/cat_death")).volume(0.46)));
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.CENTIPEDE_AMBIENT.get(), definition().with(defaultSound(this.location("entity/centipede/centipede_idle")).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.CENTIPEDE_HURT.get(), definition().with(defaultSound(this.location("entity/centipede/centipede_hurt")).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.CENTIPEDE_DEATH.get(), definition().with(defaultSound(this.location("entity/centipede/centipede_death")).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.DAEMON_AMBIENT.get(),
         definition()
            .with(
               new Sound[]{
                  defaultSound(this.minecraft("mob/blaze/breathe1")),
                  defaultSound(this.minecraft("mob/blaze/breathe2")),
                  defaultSound(this.minecraft("mob/blaze/breathe3")),
                  defaultSound(this.minecraft("mob/blaze/breathe4"))
               }
            )
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.DAEMON_HURT.get(),
         definition()
            .with(
               new Sound[]{
                  defaultSound(this.minecraft("mob/blaze/hit1")),
                  defaultSound(this.minecraft("mob/blaze/hit2")),
                  defaultSound(this.minecraft("mob/blaze/hit3")),
                  defaultSound(this.minecraft("mob/blaze/hit4"))
               }
            )
      );
      this.addWithSubtitle((SoundEvent)TensuraSoundEvents.DAEMON_DEATH.get(), definition().with(defaultSound(this.minecraft("mob/blaze/death"))));
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.DIREWOLF_AMBIENT.get(), definition().with(defaultSound(this.location("entity/direwolf/direwolf_idle")).volume(0.46))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.DIREWOLF_AGGRO.get(), definition().with(defaultSound(this.location("entity/direwolf/direwolf_aggro")).volume(0.46))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.DIREWOLF_HOWL.get(), definition().with(defaultSound(this.location("entity/direwolf/direwolf_howl")).volume(0.46))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.DIREWOLF_HURT.get(), definition().with(defaultSound(this.location("entity/direwolf/direwolf_hurt")).volume(0.46))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.DIREWOLF_DEATH.get(), definition().with(defaultSound(this.location("entity/direwolf/direwolf_death")).volume(0.46))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.DOG_AMBIENT.get(),
         definition()
            .with(
               new Sound[]{
                  defaultSound(this.minecraft("mob/wolf/bark1")),
                  defaultSound(this.minecraft("mob/wolf/bark2")),
                  defaultSound(this.minecraft("mob/wolf/bark3"))
               }
            )
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.DOG_AGGRO.get(),
         definition()
            .with(
               new Sound[]{
                  defaultSound(this.minecraft("mob/wolf/growl1")),
                  defaultSound(this.minecraft("mob/wolf/growl2")),
                  defaultSound(this.minecraft("mob/wolf/growl3"))
               }
            )
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.DOG_HURT.get(),
         definition()
            .with(
               new Sound[]{
                  defaultSound(this.minecraft("mob/wolf/hurt1")),
                  defaultSound(this.minecraft("mob/wolf/hurt2")),
                  defaultSound(this.minecraft("mob/wolf/hurt3"))
               }
            )
      );
      this.addWithSubtitle((SoundEvent)TensuraSoundEvents.DOG_DEATH.get(), definition().with(defaultSound(this.minecraft("mob/wolf/death"))));
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.FISH_FLOP.get(),
         definition()
            .with(
               new Sound[]{
                  defaultSound(this.minecraft("entity/fish/flop1")),
                  defaultSound(this.minecraft("entity/fish/flop2")),
                  defaultSound(this.minecraft("entity/fish/flop3")),
                  defaultSound(this.minecraft("entity/fish/flop4"))
               }
            )
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.FISH_HURT.get(),
         definition()
            .with(
               new Sound[]{
                  defaultSound(this.minecraft("entity/fish/hurt1")),
                  defaultSound(this.minecraft("entity/fish/hurt2")),
                  defaultSound(this.minecraft("entity/fish/hurt3")),
                  defaultSound(this.minecraft("entity/fish/hurt4"))
               }
            )
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.FISH_DEATH.get(),
         definition()
            .with(
               new Sound[]{
                  defaultSound(this.minecraft("entity/fish/hurt1")).volume(1.5),
                  defaultSound(this.minecraft("entity/fish/hurt2")).volume(1.5),
                  defaultSound(this.minecraft("entity/fish/hurt3")).volume(1.5),
                  defaultSound(this.minecraft("entity/fish/hurt4")).volume(1.5)
               }
            )
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.GNOME_AMBIENT.get(), definition().with(defaultSound(this.location("entity/gnome/gnome_idle")).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.GNOME_HURT.get(), definition().with(defaultSound(this.location("entity/gnome/gnome_hurt")).volume(0.46))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.GNOME_DEATH.get(), definition().with(defaultSound(this.location("entity/gnome/gnome_death")).volume(0.46))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.GOBLIN_AMBIENT.get(),
         definition()
            .with(
               new Sound[]{
                  defaultSound(this.location("entity/goblin/goblin_idle")).volume(0.8), defaultSound(this.location("entity/goblin/goblin_idle2")).volume(0.8)
               }
            )
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.GOBLIN_AGGRO.get(), definition().with(defaultSound(this.location("entity/goblin/goblin_aggro")).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.GOBLIN_HURT.get(), definition().with(defaultSound(this.location("entity/goblin/goblin_hurt")).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.GOBLIN_DEATH.get(), definition().with(defaultSound(this.location("entity/goblin/goblin_death")).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.GOLEM_AMBIENT.get(),
         definition()
            .with(
               new Sound[]{
                  defaultSound(this.minecraft("mob/irongolem/walk1")),
                  defaultSound(this.minecraft("mob/irongolem/walk2")),
                  defaultSound(this.minecraft("mob/irongolem/walk3")),
                  defaultSound(this.minecraft("mob/irongolem/walk4"))
               }
            )
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.GOLEM_HURT.get(),
         definition()
            .with(
               new Sound[]{
                  defaultSound(this.minecraft("mob/irongolem/hit1")),
                  defaultSound(this.minecraft("mob/irongolem/hit2")),
                  defaultSound(this.minecraft("mob/irongolem/hit3"))
               }
            )
      );
      this.addWithSubtitle((SoundEvent)TensuraSoundEvents.GOLEM_DEATH.get(), definition().with(defaultSound(this.minecraft("mob/irongolem/death"))));
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.HOVER_LIZARD_AMBIENT.get(),
         definition().with(defaultSound(this.location("entity/lizard/lizard_idle")).volume(0.46).pitch(2.0F))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.HOVER_LIZARD_HURT.get(), definition().with(defaultSound(this.location("entity/lizard/lizard_hurt")).volume(0.46))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.HOVER_LIZARD_DEATH.get(),
         definition().with(defaultSound(this.location("entity/lizard/lizard_death")).volume(0.46).pitch(1.5))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.INSECT_AMBIENT.get(),
         definition().with(defaultSound(this.location("entity/centipede/centipede_idle")).volume(0.8).pitch(1.5))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.INSECT_FLY.get(),
         definition()
            .with(
               new Sound[]{
                  defaultSound(this.minecraft("mob/bee/loop1")),
                  defaultSound(this.minecraft("mob/bee/loop2")),
                  defaultSound(this.minecraft("mob/bee/loop3")),
                  defaultSound(this.minecraft("mob/bee/loop4"))
               }
            )
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.INSECT_HURT.get(),
         definition().with(defaultSound(this.location("entity/centipede/centipede_hurt")).volume(0.8).pitch(1.5))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.INSECT_DEATH.get(),
         definition().with(defaultSound(this.location("entity/centipede/centipede_death")).volume(0.8).pitch(1.5))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.LEECH_LIZARD_AMBIENT.get(), definition().with(defaultSound(this.location("entity/lizard/lizard_idle")).volume(0.46))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.LEECH_LIZARD_ATTACK.get(), definition().with(defaultSound(this.location("entity/lizard/lizard_attack")).volume(0.46))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.LEECH_LIZARD_HURT.get(), definition().with(defaultSound(this.location("entity/lizard/lizard_hurt")).volume(0.46))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.LEECH_LIZARD_DEATH.get(), definition().with(defaultSound(this.location("entity/lizard/lizard_death")).volume(0.46))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.LIZARDMAN_AMBIENT.get(),
         definition().with(defaultSound(this.location("entity/lizard/lizard_idle")).pitch(2.0F).volume(0.3))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.LIZARDMAN_HURT.get(),
         definition().with(defaultSound(this.location("entity/lizard/lizard_hurt")).pitch(1.5).volume(0.24))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.LIZARDMAN_DEATH.get(),
         definition().with(defaultSound(this.location("entity/lizard/lizard_death")).pitch(1.5).volume(0.24))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.MEGALODON_AMBIENT.get(),
         definition().with(defaultSound(this.location("entity/sea_monster/sea_monster_idle")).pitch(0.85).volume(0.46))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.MEGALODON_HURT.get(),
         definition().with(defaultSound(this.location("entity/sea_monster/sea_monster_hurt")).pitch(0.85).volume(0.46))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.MEGALODON_DEATH.get(),
         definition().with(defaultSound(this.location("entity/sea_monster/sea_monster_death")).pitch(0.85).volume(0.46))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.OWL_AMBIENT.get(), definition().with(defaultSound(this.location("entity/bird/owl_idle")).volume(0.13))
      );
      this.addWithSubtitle((SoundEvent)TensuraSoundEvents.OWL_HURT.get(), definition().with(defaultSound(this.location("entity/bird/owl_hurt")).volume(0.13)));
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.OWL_DEATH.get(), definition().with(defaultSound(this.location("entity/bird/owl_death")).volume(0.13).pitch(1.5))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.SISSIE_AMBIENT.get(),
         definition().with(defaultSound(this.location("entity/sea_monster/sea_monster_idle")).pitch(0.75).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.SISSIE_HURT.get(),
         definition().with(defaultSound(this.location("entity/sea_monster/sea_monster_hurt")).pitch(0.75).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.SISSIE_DEATH.get(),
         definition().with(defaultSound(this.location("entity/sea_monster/sea_monster_death")).pitch(0.75).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.SPEAR_TORO_AMBIENT.get(),
         definition().with(defaultSound(this.location("entity/sea_monster/sea_monster_idle")).volume(0.46))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.SPEAR_TORO_HURT.get(),
         definition().with(defaultSound(this.location("entity/sea_monster/sea_monster_hurt")).volume(0.46))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.SPEAR_TORO_DEATH.get(),
         definition().with(defaultSound(this.location("entity/sea_monster/sea_monster_death")).volume(0.46))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.SERPENT_AMBIENT.get(), definition().with(defaultSound(this.location("entity/serpent/serpent_idle")).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.SERPENT_HURT.get(), definition().with(defaultSound(this.location("entity/serpent/serpent_hurt")).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.SERPENT_DEATH.get(), definition().with(defaultSound(this.location("entity/serpent/serpent_death")).volume(0.46))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.SPIRIT_FLAME_AMBIENT.get(),
         definition()
            .with(
               new Sound[]{
                  defaultSound(this.minecraft("mob/blaze/breathe1")),
                  defaultSound(this.minecraft("mob/blaze/breathe2")),
                  defaultSound(this.minecraft("mob/blaze/breathe3")),
                  defaultSound(this.minecraft("mob/blaze/breathe4"))
               }
            )
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.SPIRIT_FLAME_HURT.get(),
         definition()
            .with(
               new Sound[]{
                  defaultSound(this.minecraft("mob/blaze/hit1")),
                  defaultSound(this.minecraft("mob/blaze/hit2")),
                  defaultSound(this.minecraft("mob/blaze/hit3")),
                  defaultSound(this.minecraft("mob/blaze/hit4"))
               }
            )
      );
      this.addWithSubtitle((SoundEvent)TensuraSoundEvents.SPIRIT_FLAME_DEATH.get(), definition().with(defaultSound(this.minecraft("mob/blaze/death"))));
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.SPIRIT_SPACE_AMBIENT.get(),
         definition()
            .with(
               new Sound[]{
                  defaultSound(this.minecraft("mob/allay/idle_without_item1")),
                  defaultSound(this.minecraft("mob/allay/idle_without_item2")),
                  defaultSound(this.minecraft("mob/allay/idle_without_item3")),
                  defaultSound(this.minecraft("mob/allay/idle_without_item4"))
               }
            )
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.SPIRIT_SPACE_HURT.get(),
         definition().with(new Sound[]{defaultSound(this.minecraft("mob/allay/hurt1")), defaultSound(this.minecraft("mob/allay/hurt2"))})
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.SPIRIT_SPACE_DEATH.get(),
         definition().with(new Sound[]{defaultSound(this.minecraft("mob/allay/death1")), defaultSound(this.minecraft("mob/allay/death2"))})
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.SPIRIT_WATER_AMBIENT.get(),
         definition()
            .with(
               new Sound[]{
                  defaultSound(this.minecraft("mob/guardian/elder_idle1")),
                  defaultSound(this.minecraft("mob/guardian/elder_idle2")),
                  defaultSound(this.minecraft("mob/guardian/elder_idle3")),
                  defaultSound(this.minecraft("mob/guardian/elder_idle4"))
               }
            )
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.SPIRIT_WATER_HURT.get(),
         definition()
            .with(
               new Sound[]{
                  defaultSound(this.minecraft("mob/guardian/elder_hit1")),
                  defaultSound(this.minecraft("mob/guardian/elder_hit2")),
                  defaultSound(this.minecraft("mob/guardian/elder_hit3")),
                  defaultSound(this.minecraft("mob/guardian/elder_hit4"))
               }
            )
      );
      this.addWithSubtitle((SoundEvent)TensuraSoundEvents.SPIRIT_WATER_DEATH.get(), definition().with(defaultSound(this.minecraft("mob/guardian/elder_death"))));
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.SPIRIT_WIND_AMBIENT.get(),
         definition()
            .with(
               new Sound[]{
                  defaultSound(this.minecraft("mob/breeze/idle_air1")),
                  defaultSound(this.minecraft("mob/breeze/idle_air2")),
                  defaultSound(this.minecraft("mob/breeze/idle_air3")),
                  defaultSound(this.minecraft("mob/breeze/idle_air4"))
               }
            )
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.SPIRIT_WIND_HURT.get(),
         definition()
            .with(
               new Sound[]{
                  defaultSound(this.minecraft("mob/breeze/hurt1")),
                  defaultSound(this.minecraft("mob/breeze/hurt2")),
                  defaultSound(this.minecraft("mob/breeze/hurt3"))
               }
            )
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.SPIRIT_WIND_DEATH.get(),
         definition().with(new Sound[]{defaultSound(this.minecraft("mob/breeze/death1")), defaultSound(this.minecraft("mob/breeze/death2"))})
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.METAL_SLIME_SQUISH.get(), definition().with(defaultSound(this.location("entity/slime/metal_slime_bounce")).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.METAL_SLIME_ATTACK.get(),
         definition().with(defaultSound(this.location("entity/slime/metal_slime_attack")).volume(0.36))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.METAL_SLIME_HURT.get(), definition().with(defaultSound(this.location("entity/slime/metal_slime_hurt")).volume(0.36))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.METAL_SLIME_DEATH.get(), definition().with(defaultSound(this.location("entity/slime/metal_slime_death")).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.ORC_AMBIENT.get(),
         definition()
            .with(
               new Sound[]{
                  defaultSound(this.minecraft("mob/piglin_brute/idle1")).volume(0.66),
                  defaultSound(this.minecraft("mob/piglin_brute/idle2")).volume(0.66),
                  defaultSound(this.minecraft("mob/piglin_brute/idle3")).volume(0.66),
                  defaultSound(this.minecraft("mob/piglin_brute/idle4")).volume(0.66),
                  defaultSound(this.minecraft("mob/piglin_brute/idle5")).volume(0.66)
               }
            )
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.ORC_HURT.get(),
         definition()
            .with(
               new Sound[]{
                  defaultSound(this.minecraft("mob/piglin_brute/hurt1")).volume(0.7),
                  defaultSound(this.minecraft("mob/piglin_brute/hurt2")).volume(0.7),
                  defaultSound(this.minecraft("mob/piglin_brute/hurt3")).volume(0.7)
               }
            )
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.ORC_DEATH.get(),
         definition()
            .with(
               new Sound[]{
                  defaultSound(this.minecraft("mob/piglin_brute/death1")).volume(0.7),
                  defaultSound(this.minecraft("mob/piglin_brute/death2")).volume(0.7),
                  defaultSound(this.minecraft("mob/piglin_brute/death3")).volume(0.8)
               }
            )
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.ORC_LAUGH.get(),
         definition()
            .with(
               new Sound[]{
                  defaultSound(this.minecraft("mob/piglin/celebrate1")).volume(0.7),
                  defaultSound(this.minecraft("mob/piglin/celebrate2")).volume(0.7),
                  defaultSound(this.minecraft("mob/piglin/celebrate3")).volume(0.8),
                  defaultSound(this.minecraft("mob/piglin/celebrate4")).volume(0.8)
               }
            )
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.ORC_TRANSFORM.get(),
         definition()
            .with(
               new Sound[]{
                  defaultSound(this.minecraft("mob/piglin/converted1")).volume(0.7), defaultSound(this.minecraft("mob/piglin/converted2")).volume(0.7)
               }
            )
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.TIGER_AMBIENT.get(), definition().with(defaultSound(this.location("entity/tiger/tiger_idle")).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.TIGER_ATTACK.get(), definition().with(defaultSound(this.location("entity/tiger/tiger_attack")).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.TIGER_HURT.get(), definition().with(defaultSound(this.location("entity/tiger/tiger_hurt")).volume(0.8))
      );
      this.addWithSubtitle(
         (SoundEvent)TensuraSoundEvents.TIGER_DEATH.get(), definition().with(defaultSound(this.location("entity/tiger/tiger_death")).volume(0.8))
      );
   }

   protected static Sound defaultSound(ResourceLocation name) {
      return sound(name, SoundType.SOUND);
   }
}
