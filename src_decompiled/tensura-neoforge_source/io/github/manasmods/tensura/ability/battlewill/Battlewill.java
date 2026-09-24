package io.github.manasmods.tensura.ability.battlewill;

import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.advancement.AbilityTrigger;
import io.github.manasmods.tensura.registry.TensuraStats;
import io.github.manasmods.tensura.registry.advancement.TensuraCriteriaTriggers;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public class Battlewill extends TensuraSkill {
   public int getMaxMastery() {
      return 400;
   }

   @Nullable
   @Override
   public MutableComponent getColoredName() {
      MutableComponent name = super.getName();
      return name == null ? null : name.withStyle(ChatFormatting.RED);
   }

   @Nullable
   public ResourceLocation getSkillIcon() {
      ResourceLocation id = this.getRegistryName();
      return id == null
         ? ResourceLocation.fromNamespaceAndPath("tensura", "textures/temp_textures/item/confused_rimuru.png")
         : ResourceLocation.fromNamespaceAndPath("tensura", "textures/battlewill/" + id.getPath().replace('/', '.') + ".png");
   }

   @Override
   public void addLearningStatistic(ManasSkillInstance instance, ServerPlayer player) {
      if (!instance.isTemporarySkill() && !instance.isSubInstance() && !(instance.getMastery() < 0.0)) {
         player.awardStat(TensuraStats.BATTLEWILL_LEARNT);
         ((AbilityTrigger)TensuraCriteriaTriggers.BATTLEWILL_LEARNT.get()).trigger(player, this);
      }
   }

   @Override
   public void addMasteryStatistic(ServerPlayer player) {
      player.awardStat(TensuraStats.BATTLEWILL_MASTERED);
      ((AbilityTrigger)TensuraCriteriaTriggers.BATTLEWILL_MASTERED.get()).trigger(player, this);
   }
}
