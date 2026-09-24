package io.github.manasmods.tensura.effect;

import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.manascore.race.api.Races;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.race.TensuraRace;
import io.github.manasmods.tensura.race.wight.WightRace;
import io.github.manasmods.tensura.registry.race.TensuraRaces;
import io.github.manasmods.tensura.storage.Alignment;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.effect.EffectStorage;
import io.github.manasmods.tensura.storage.player.ITensuraPlayer;
import java.awt.Color;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;

public class CurseEffect extends TensuraMobEffect {
   public CurseEffect() {
      super(MobEffectCategory.HARMFUL, new Color(92, 31, 184).getRGB());
      this.addAttributeModifier(Attributes.MAX_HEALTH, ResourceLocation.fromNamespaceAndPath("tensura", "curse"), -0.2, Operation.ADD_MULTIPLIED_TOTAL);
   }

   public void onEffectStarted(LivingEntity entity, int i) {
      super.onEffectStarted(entity, i);
      float maxHP = EffectStorage.getSeveranceMaxHealth(entity);
      if (entity.getHealth() > maxHP) {
         entity.setHealth(maxHP);
      }
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      if (amplifier >= 4 && entity.hurt(TensuraDamageTypes.getDamageSource(entity.level(), TensuraDamageTypes.CURSE), entity.getMaxHealth())) {
         this.wightTransformation(entity);
      }

      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return amplifier >= 3 && duration % 20 == 0;
   }

   private boolean canBecomeWight(ManasRaceInstance race, LivingEntity player) {
      return race.getRace() instanceof TensuraRace tensuraRace && tensuraRace.getAlignment().equals(Alignment.MAJIN)
         ? false
         : race.getPreviousEvolutions(player).isEmpty();
   }

   private void wightTransformation(LivingEntity entity) {
      if (!entity.isAlive()) {
         if (entity instanceof Player pPlayer) {
            if (!(entity.getRandom().nextFloat() >= 0.05)) {
               Races races = RaceAPI.getRaceFrom(entity);
               Optional<ManasRaceInstance> optional = races.getRace();
               if (optional.isPresent()) {
                  ManasRaceInstance race = optional.get();
                  if (!this.canBecomeWight(race, entity)) {
                     return;
                  }

                  ManasRaceInstance wight = ((WightRace)TensuraRaces.WIGHT.get()).createDefaultInstance();
                  wight.deserialize(race.serialize(new CompoundTag()));
                  races.setRace(wight, false, false);
                  ((WightRace)TensuraRaces.WIGHT.get()).resetExistenceData(entity);
                  races.markDirty();
                  ITensuraPlayer playerData = TensuraStorages.getPlayerDataFrom(pPlayer);
                  playerData.setTrackedEvolution(null);
                  playerData.markDirty();
               }
            }
         }
      }
   }
}
