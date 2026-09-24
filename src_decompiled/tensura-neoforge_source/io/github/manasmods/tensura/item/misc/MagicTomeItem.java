package io.github.manasmods.tensura.item.misc;

import dev.architectury.networking.NetworkManager;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.TensuraSkillInstance;
import io.github.manasmods.tensura.data.TensuraSkillTags;
import io.github.manasmods.tensura.network.s2c.DisplayTotemStackEffectPayload;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import io.github.manasmods.tensura.registry.item.misc.TensuraDataComponents;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import java.util.List;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MagicTomeItem extends Item {
   public MagicTomeItem() {
      super(new Properties().rarity(Rarity.RARE).fireResistant().stacksTo(1));
   }

   public static ItemStack createForMagic(ManasSkill skill) {
      ItemStack stack = ((Item)TensuraMaterialItems.MAGIC_TOME.get()).getDefaultInstance();
      stack.set((DataComponentType)TensuraDataComponents.SKILL.get(), skill.getRegistryName());
      return stack;
   }

   @Nullable
   public ManasSkill getSkill(ItemStack stack) {
      ResourceLocation skill = (ResourceLocation)stack.get((DataComponentType)TensuraDataComponents.SKILL.get());
      return skill == null ? null : (ManasSkill)SkillAPI.getSkillRegistry().get(skill);
   }

   public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
      ManasSkill skill = this.getSkill(itemStack);
      if (skill != null) {
         list.add(skill.getChatDisplayName(false));
         list.add(skill.getSkillDescription());
      }
   }

   public int getUseDuration(ItemStack pStack, LivingEntity entity) {
      return 200;
   }

   public UseAnim getUseAnimation(ItemStack pStack) {
      return UseAnim.BOW;
   }

   @NotNull
   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand pHand) {
      ItemStack stack = player.getItemInHand(pHand);
      if (player.getCooldowns().isOnCooldown(stack.getItem()) && !player.getAbilities().instabuild) {
         return InteractionResultHolder.fail(stack);
      }

      player.startUsingItem(pHand);
      return InteractionResultHolder.consume(stack);
   }

   public void onUseTick(Level pLevel, LivingEntity pLivingEntity, ItemStack pStack, int pRemainingUseDuration) {
      if (pRemainingUseDuration % 4 == 0) {
         TensuraParticleHelper.spawnEnchantingTableParticle(pLevel, pLivingEntity.getEyePosition().add(0.0, 0.5, 0.0), ParticleTypes.ENCHANT, 8);
      }
   }

   public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity, int time) {
      if (!level.isClientSide()) {
         int useTicks = this.getUseDuration(stack, entity) - time;
         if (useTicks >= 10) {
            ManasSkill skill = this.getSkill(stack);
            if (skill == null) {
               skill = this.getRandomAspectual(entity);
            }

            if (skill != null) {
               if (entity instanceof ServerPlayer player) {
                  if (skill != null && this.canLearnSkill(entity, skill)) {
                     CriteriaTriggers.CONSUME_ITEM.trigger(player, stack);
                     this.consumeAndCooldown(player, stack);
                     NetworkManager.sendToPlayer(player, new DisplayTotemStackEffectPayload((CompoundTag)stack.saveOptional(player.registryAccess())));
                  } else {
                     this.consumeAndCooldown(player, stack);
                  }
               }
            }
         }
      }
   }

   private void consumeAndCooldown(Player player, ItemStack stack) {
      if (!player.hasInfiniteMaterials()) {
         player.getCooldowns().addCooldown(stack.getItem(), 200);
         stack.shrink(1);
      }
   }

   private boolean canLearnSkill(LivingEntity entity, ManasSkill skill) {
      TensuraSkillInstance instance = new TensuraSkillInstance(skill);
      int mastery = skill instanceof TensuraSkill tensuraSkill
         ? tensuraSkill.getAcquirementMastery(entity)
         : TensuraSkill.BASE_CONFIG.Learning.learningPointRequirement * -1;
      instance.setMastery(mastery);
      if (SkillHelper.learnSkill(entity, instance)) {
         entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.TOTEM_USE, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
         TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.TOTEM_OF_UNDYING, 1.0);
         TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.TOTEM_OF_UNDYING, 2.0);
         TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.FLASH, 1.0);
         return true;
      } else {
         entity.sendSystemMessage(
            Component.translatable("tensura.skill.temporary.already_have", new Object[]{instance.getChatDisplayName(true)}).withStyle(ChatFormatting.RED)
         );
         entity.level()
            .playSound(null, entity.getX(), entity.getY(), entity.getZ(), TensuraSoundEvents.GENERIC_CAST_FAIL, TensuraSkill.ABILITY_SOUND, 2.0F, 1.0F);
         TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.ANGRY_VILLAGER, 1.0);
         return false;
      }
   }

   private ManasSkill getRandomAspectual(LivingEntity entity) {
      Optional<Named<ManasSkill>> optional = entity.registryAccess()
         .registryOrThrow(SkillAPI.getSkillRegistry().key())
         .getTag(TensuraSkillTags.ASPECTUAL_MAGIC);
      if (optional.isEmpty()) {
         return null;
      }

      List<Holder<ManasSkill>> random = optional.get().stream().toList();
      return (ManasSkill)random.get(entity.getRandom().nextInt(random.size())).value();
   }
}
