package io.github.manasmods.tensura.ability.subclass;

import dev.architectury.registry.menu.MenuRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.menu.SkillCreationMenu;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;

public interface IAbilityCreator {
   int getCreationCooldown(ManasSkillInstance var1, Player var2);

   int getCreatedSkillTimer(ManasSkillInstance var1, Player var2);

   void onGainingCreatingMastery(ManasSkillInstance var1, Player var2);

   void onCreateAbility(ManasSkillInstance var1, Player var2, ManasSkillInstance var3);

   default boolean allowToCreateDuplicateAbility(ManasSkillInstance creator, Player player, ManasSkill created) {
      return true;
   }

   default void openSkillCreationMenu(ManasSkillInstance instance, ServerPlayer serverPlayer, int mode, List<ResourceLocation> skills) {
      MenuRegistry.openExtendedMenu(
         serverPlayer,
         new SimpleMenuProvider((i, inventory, player) -> new SkillCreationMenu(i, instance.getSkill(), mode, skills), instance.getDisplayName()),
         buf -> {
            buf.writeResourceLocation(instance.getSkillId());
            buf.writeInt(mode);
            buf.writeCollection(skills, FriendlyByteBuf::writeResourceLocation);
         }
      );
      serverPlayer.playNotifySound((SoundEvent)TensuraSoundEvents.SPATIAL_STORAGE.get(), TensuraSkill.ABILITY_SOUND, 0.75F, 1.0F);
   }
}
