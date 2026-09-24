package io.github.manasmods.tensura.ability.skill.unique;

import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.PlayerEvent.OpenMenu;
import dev.architectury.networking.NetworkManager;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.ability.subclass.IResearcherEnchanter;
import io.github.manasmods.tensura.ability.subclass.ISpatialStorage;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.data.TensuraTags;
import io.github.manasmods.tensura.menu.ResearcherEnchantingMenu;
import io.github.manasmods.tensura.menu.ResearcherStorageMenu;
import io.github.manasmods.tensura.menu.container.SpatialStorageContainer;
import io.github.manasmods.tensura.network.s2c.OpenSpatialStorageMenuPayload;
import io.github.manasmods.tensura.registry.skill.UniqueSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import java.util.List;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.jetbrains.annotations.NotNull;

public class GodlyCraftsmanSkill extends Skill implements ISpatialStorage, IResearcherEnchanter {
   private static final UniqueSkillConfig.GodlyCraftsman CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).GodlyCraftsman;

   public GodlyCraftsmanSkill() {
      super(Skill.SkillType.UNIQUE);
   }

   @Override
   public double getDefaultAcquiringMagiculeCost() {
      return CONFIG.mpAcquirement;
   }

   @Override
   public boolean checkAcquiringRequirement(Player entity, double newEP) {
      return SkillUtils.isSkillMastered(entity, (ManasSkill)UniqueSkills.RESEARCHER.get());
   }

   @Override
   public int getAcquirementMastery(LivingEntity entity) {
      return -1;
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      IResearcherEnchanter.addSelectedEnchantments(entity, ItemEnchantments.EMPTY, this, true);
      if (entity instanceof ServerPlayer player && player.isShiftKeyDown() && !(player.containerMenu instanceof ResearcherEnchantingMenu)) {
         player.closeContainer();
         this.openEnchantingMenu(player, instance);
         player.playNotifySound((SoundEvent)TensuraSoundEvents.SPATIAL_STORAGE.get(), TensuraSkill.ABILITY_SOUND, 0.75F, 1.0F);
      } else {
         this.openSpatialStorage(entity, instance);
      }
   }

   @NotNull
   @Override
   public SpatialStorageContainer getSpatialStorage(ManasSkillInstance instance, Provider provide) {
      SpatialStorageContainer container = new SpatialStorageContainer(54, 256);
      container.fromTag(instance.getOrCreateTag().getList("SpatialStorage", 10), provide);
      return container;
   }

   @Override
   public void openSpatialStoragePage(ServerPlayer player, LivingEntity owner, ManasSkillInstance instance, int page) {
      player.nextContainerCounter();
      ManasSkill skill = instance.getSkill();
      SpatialStorageContainer container = this.getSpatialStorage(instance, owner.registryAccess());
      NetworkManager.sendToPlayer(
         player,
         new OpenSpatialStorageMenuPayload(
            OpenSpatialStorageMenuPayload.StorageType.RESEARCHER,
            player.containerCounter,
            container.getContainerSize(),
            container.getMaxStackSize(),
            page,
            owner.getId(),
            skill.getRegistryName()
         )
      );
      player.containerMenu = new ResearcherStorageMenu(player.containerCounter, player.getInventory(), owner, container, skill, page);
      player.initMenu(player.containerMenu);
      ((OpenMenu)PlayerEvent.OPEN_MENU.invoker()).open(player, player.containerMenu);
   }

   @Override
   public int getMaximumBonusLevel() {
      return CONFIG.maxBonusLevel;
   }

   @Override
   public List<String> getBlacklistEnchantments() {
      return CONFIG.enchantmentBlacklist;
   }

   @Override
   public List<String> getBlackListBonusLevelEnchantments() {
      return CONFIG.maxBonusBlacklist;
   }

   @Override
   public float getCurseChancePerEngraving() {
      return CONFIG.curseChance;
   }

   @Override
   public boolean isAllowedToCopyEnchantments(LivingEntity entity, ItemStack stack) {
      return !EnchantmentHelper.hasTag(stack, TensuraTags.Enchantments.SEALING_CURSE);
   }
}
