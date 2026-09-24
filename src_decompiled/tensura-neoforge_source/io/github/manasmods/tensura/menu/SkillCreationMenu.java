package io.github.manasmods.tensura.menu;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.manascore.skill.api.Skills;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.TensuraSkillInstance;
import io.github.manasmods.tensura.ability.subclass.IAbilityCreator;
import io.github.manasmods.tensura.registry.menu.TensuraMenuTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ability.IAbility;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.Generated;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

public class SkillCreationMenu extends AbstractContainerMenu {
   private final ManasSkill ability;
   private final int mode;
   private final List<ManasSkill> skills;

   public SkillCreationMenu(int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
      this(
         pContainerId,
         (ManasSkill)SkillAPI.getSkillRegistry().get(buf.readResourceLocation()),
         buf.readInt(),
         buf.readList(FriendlyByteBuf::readResourceLocation)
      );
   }

   public SkillCreationMenu(int pContainerId, ManasSkill skill, int mode, List<ResourceLocation> list) {
      super((MenuType)TensuraMenuTypes.SKILL_CREATION.get(), pContainerId);
      this.ability = skill;
      this.mode = mode;
      this.skills = list.stream().sorted(Comparator.comparing(ResourceLocation::getPath)).map(id -> (ManasSkill)SkillAPI.getSkillRegistry().get(id)).toList();
   }

   public boolean stillValid(Player pPlayer) {
      return pPlayer.isAlive();
   }

   public boolean clickMenuButton(Player player, int i) {
      if (i >= 0 && i < this.getSkills().size()) {
         ManasSkill ability = this.getAbility();
         if (ability instanceof IAbilityCreator abilityCreator) {
            Skills storage = SkillAPI.getSkillsFrom(player);
            Optional<ManasSkillInstance> optionalCreator = storage.getSkill(ability);
            if (optionalCreator.isEmpty()) {
               return false;
            }

            int mode = this.getMode();
            ManasSkillInstance creator = optionalCreator.get();
            if (creator.onCoolDown(mode)) {
               return false;
            }

            if (player.level().isClientSide()) {
               return true;
            }

            abilityCreator.onGainingCreatingMastery(creator, player);
            creator.setCoolDown(abilityCreator.getCreationCooldown(creator, player), mode);
            ManasSkill skill = this.getSkills().get(i);
            CompoundTag tag = creator.getOrCreateTag();
            ResourceLocation createdLocation = ResourceLocation.parse(tag.getString("CreatedSkill"));
            if (createdLocation.equals(skill.getRegistryName())) {
               if (!abilityCreator.allowToCreateDuplicateAbility(creator, player, skill)) {
                  return false;
               }

               Optional<ManasSkillInstance> optional = storage.getSkill(skill);
               if (optional.isPresent() && optional.get().isTemporarySkill()) {
                  abilityCreator.onCreateAbility(creator, player, optional.get());
                  optional.get().setRemoveTime(1200);
                  player.playNotifySound((SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                  creator.markDirty();
                  storage.markDirty();
                  player.closeContainer();
                  return true;
               }
            }

            ManasSkill createdSkill = (ManasSkill)SkillAPI.getSkillRegistry().get(createdLocation);
            if (createdSkill != null) {
               Optional<ManasSkillInstance> createdInstance = storage.getSkill(createdSkill);
               if (createdInstance.isPresent() && createdInstance.get().isTemporarySkill()) {
                  storage.forgetSkill(createdSkill);
               }
            }

            tag.putString("CreatedSkill", skill.getRegistryName().toString());
            TensuraSkillInstance skillInstance = new TensuraSkillInstance(skill);
            abilityCreator.onCreateAbility(creator, player, skillInstance);
            skillInstance.getOrCreateTag().putString("Creator", ability.getRegistryName().toString());
            if (SkillHelper.learnSkill(player, skillInstance, abilityCreator.getCreatedSkillTimer(creator, player))) {
               player.playNotifySound((SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
               IAbility abilityData = TensuraStorages.getAbilityFrom(player);
               if (abilityData.getAbilitySlot(0).getSkill() == null) {
                  abilityData.setAbilitySlot(0, skill, 0);
               } else if (abilityData.getAbilitySlot(1).getSkill() == null) {
                  abilityData.setAbilitySlot(1, skill, 0);
               } else if (abilityData.getAbilitySlot(2).getSkill() == null) {
                  abilityData.setAbilitySlot(2, skill, 0);
               }

               abilityData.markDirty();
            }

            creator.markDirty();
            storage.markDirty();
            player.closeContainer();
            return true;
         } else {
            return false;
         }
      } else {
         return super.clickMenuButton(player, i);
      }
   }

   public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
      return ItemStack.EMPTY;
   }

   @Generated
   public ManasSkill getAbility() {
      return this.ability;
   }

   @Generated
   public int getMode() {
      return this.mode;
   }

   @Generated
   public List<ManasSkill> getSkills() {
      return this.skills;
   }
}
