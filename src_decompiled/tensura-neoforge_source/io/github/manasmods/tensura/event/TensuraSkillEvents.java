package io.github.manasmods.tensura.event;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.event.EventResult;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class TensuraSkillEvents {
   public static Event<TensuraSkillEvents.SkillLearningEvent> SKILL_LEARNING = EventFactory.createEventResult(new TensuraSkillEvents.SkillLearningEvent[0]);
   public static Event<TensuraSkillEvents.AbilityEquipEvent> ABILITY_EQUIP = EventFactory.createEventResult(new TensuraSkillEvents.AbilityEquipEvent[0]);
   public static Event<TensuraSkillEvents.SkillGriefEvent> SKILL_GRIEF_PRE = EventFactory.createEventResult(new TensuraSkillEvents.SkillGriefEvent[0]);
   public static Event<TensuraSkillEvents.SkillGriefEvent> SKILL_GRIEF_POS = EventFactory.createLoop(new TensuraSkillEvents.SkillGriefEvent[0]);
   public static Event<TensuraSkillEvents.SkillPlunderEvent> SKILL_PLUNDER = EventFactory.createEventResult(new TensuraSkillEvents.SkillPlunderEvent[0]);

   @FunctionalInterface
   public interface AbilityEquipEvent {
      EventResult equip(Player var1, Changeable<ManasSkillInstance> var2, Changeable<Integer> var3, Changeable<Integer> var4, Changeable<Integer> var5);
   }

   @FunctionalInterface
   public interface SkillGriefEvent {
      EventResult grief(@Nullable ManasSkillInstance var1, Level var2, @Nullable Entity var3, double var4, double var6, double var8);
   }

   @FunctionalInterface
   public interface SkillLearningEvent {
      EventResult learn(ManasSkillInstance var1, LivingEntity var2, int var3, double var4, Changeable<Double> var6);
   }

   @FunctionalInterface
   public interface SkillPlunderEvent {
      EventResult plunder(@Nullable Entity var1, @Nullable Entity var2, boolean var3, Changeable<ManasSkill> var4);
   }
}
