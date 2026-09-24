package io.github.manasmods.tensura.ability.skill.unique;

import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.PlayerEvent.OpenMenu;
import dev.architectury.networking.NetworkManager;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.manascore.skill.api.Skills;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.TensuraSkillInstance;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.ability.subclass.IResearcherEnchanter;
import io.github.manasmods.tensura.ability.subclass.ISpatialStorage;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.menu.ResearcherEnchantingMenu;
import io.github.manasmods.tensura.menu.ResearcherStorageMenu;
import io.github.manasmods.tensura.menu.container.SpatialStorageContainer;
import io.github.manasmods.tensura.network.s2c.OpenResearcherEnchantingMenuPayload;
import io.github.manasmods.tensura.network.s2c.OpenSpatialStorageMenuPayload;
import io.github.manasmods.tensura.registry.skill.UniqueSkills;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.jetbrains.annotations.NotNull;

public class ResearcherSkill extends Skill implements ISpatialStorage, IResearcherEnchanter {
   private static final UniqueSkillConfig.Researcher CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).Researcher;

   public ResearcherSkill() {
      super(Skill.SkillType.UNIQUE);
   }

   @Override
   public double getDefaultAcquiringMagiculeCost() {
      return CONFIG.mpAcquirement;
   }

   public void onSkillMastered(ManasSkillInstance instance, LivingEntity entity) {
      if (!instance.isSubInstance()) {
         TensuraSkillInstance skill = new TensuraSkillInstance((ManasSkill)UniqueSkills.GODLY_CRAFTSMAN.get());
         skill.setMastery(((GodlyCraftsmanSkill)UniqueSkills.GODLY_CRAFTSMAN.get()).getAcquirementMastery(entity));
         if (SkillHelper.learnSkill(entity, skill)
            && IResearcherEnchanter.addEnchantments(
               entity, IResearcherEnchanter.getAllEnchantments(entity, instance), (ManasSkill)UniqueSkills.GODLY_CRAFTSMAN.get()
            )) {
            instance.getOrCreateTag().remove("StoredEnchantments");
         }
      }
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (entity instanceof ServerPlayer player && player.isShiftKeyDown() && !(player.containerMenu instanceof ResearcherEnchantingMenu)) {
         player.closeContainer();
         ManasSkillInstance craftsman = this.getGodlyCraftsman(entity);
         if (craftsman != null) {
            IResearcherEnchanter.addSelectedEnchantments(entity, ItemEnchantments.EMPTY, craftsman.getSkill(), true);
            player.nextContainerCounter();
            NetworkManager.sendToPlayer(
               player, new OpenResearcherEnchantingMenuPayload(player.containerCounter, player.getId(), craftsman.getSkill().getRegistryName())
            );
            player.containerMenu = new ResearcherEnchantingMenu(player.containerCounter, player.getInventory(), player, craftsman.getSkill());
            player.initMenu(player.containerMenu);
            ((OpenMenu)PlayerEvent.OPEN_MENU.invoker()).open(player, player.containerMenu);
         } else {
            IResearcherEnchanter.addSelectedEnchantments(entity, ItemEnchantments.EMPTY, this, true);
            this.openEnchantingMenu(player, instance);
         }

         player.playNotifySound((SoundEvent)TensuraSoundEvents.SPATIAL_STORAGE.get(), TensuraSkill.ABILITY_SOUND, 0.75F, 1.0F);
      } else {
         this.openSpatialStorage(entity, instance);
      }
   }

   @NotNull
   @Override
   public SpatialStorageContainer getSpatialStorage(ManasSkillInstance instance, Provider provide) {
      SpatialStorageContainer container = new SpatialStorageContainer(45, 128);
      container.fromTag(instance.getOrCreateTag().getList("SpatialStorage", 10), provide);
      return container;
   }

   @Override
   public void openSpatialStorage(LivingEntity entity, ManasSkillInstance instance) {
      ManasSkillInstance craftsman = this.getGodlyCraftsman(entity);
      if (craftsman != null) {
         ItemEnchantments enchantments = IResearcherEnchanter.getAllEnchantments(entity, instance);
         if (!enchantments.isEmpty() && IResearcherEnchanter.addEnchantments(entity, enchantments, (ManasSkill)UniqueSkills.GODLY_CRAFTSMAN.get())) {
            instance.getOrCreateTag().remove("StoredEnchantments");
         }

         IResearcherEnchanter.addSelectedEnchantments(entity, ItemEnchantments.EMPTY, craftsman.getSkill(), true);
         this.moveItemsToSpatialStorage(instance, craftsman, entity, true);
      } else {
         IResearcherEnchanter.addSelectedEnchantments(entity, ItemEnchantments.EMPTY, this, true);
         ISpatialStorage.super.openSpatialStorage(entity, instance);
      }
   }

   @Override
   public boolean addItemToSpatialStorage(ManasSkillInstance instance, LivingEntity entity, ItemStack stack) {
      ManasSkillInstance craftsman = this.getGodlyCraftsman(entity);
      return craftsman != null
         ? ((ISpatialStorage)craftsman.getSkill()).addItemToSpatialStorage(craftsman, entity, stack)
         : ISpatialStorage.super.addItemToSpatialStorage(instance, entity, stack);
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

   private ManasSkillInstance getGodlyCraftsman(LivingEntity entity) {
      Skills storage = SkillAPI.getSkillsFrom(entity);
      Optional<ManasSkillInstance> craftsmanOptional = storage.getSkill((ManasSkill)UniqueSkills.GODLY_CRAFTSMAN.get());
      return craftsmanOptional.isPresent() && craftsmanOptional.get().getMastery() >= 0.0 ? craftsmanOptional.get() : null;
   }
}
