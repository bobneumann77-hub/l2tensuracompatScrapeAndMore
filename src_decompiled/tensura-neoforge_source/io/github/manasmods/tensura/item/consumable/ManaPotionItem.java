package io.github.manasmods.tensura.item.consumable;

import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.entity.projectile.ThrownHealingPotion;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.item.TensuraConsumableItems;
import io.github.manasmods.tensura.registry.item.misc.TensuraCreativeTabs;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import java.util.List;
import lombok.Generated;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties.Builder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

public class ManaPotionItem extends PotionItem {
   private final float magiculeAmount;
   private boolean percentage = false;

   public ManaPotionItem(int nutrition, float saturationModifier, float magiculeAmount) {
      super(
         new Properties()
            .arch$tab(TensuraCreativeTabs.CONSUMABLES)
            .stacksTo(16)
            .component(DataComponents.POTION_CONTENTS, new PotionContents(Potions.WATER))
            .food(new Builder().nutrition(nutrition).saturationModifier(saturationModifier).build())
      );
      this.magiculeAmount = magiculeAmount;
   }

   public ManaPotionItem setMagiculePercentage() {
      this.percentage = true;
      return this;
   }

   @NotNull
   public ItemStack getDefaultInstance() {
      return new ItemStack(this);
   }

   @NotNull
   public String getDescriptionId(ItemStack pStack) {
      return this.getDescriptionId();
   }

   public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
   }

   public boolean isFoil(ItemStack pStack) {
      return pStack.isEnchanted();
   }

   public int getUseDuration(ItemStack pStack, LivingEntity entity) {
      return 16;
   }

   @NotNull
   public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
      if (!pPlayer.isShiftKeyDown()) {
         return ItemUtils.startUsingInstantly(pLevel, pPlayer, pHand);
      }

      ItemStack itemstack = pPlayer.getItemInHand(pHand);
      if (!pLevel.isClientSide()) {
         ThrownHealingPotion potion = new ThrownHealingPotion(pLevel, pPlayer);
         potion.setItem(itemstack);
         potion.shootFromRotation(pPlayer, pPlayer.getXRot(), pPlayer.getYRot(), -20.0F, 0.75F, 1.0F);
         pLevel.addFreshEntity(potion);
      }

      pPlayer.awardStat(Stats.ITEM_USED.get(this));
      if (!pPlayer.hasInfiniteMaterials()) {
         itemstack.shrink(1);
      }

      return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
   }

   @NotNull
   public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity entity) {
      this.applyEffect(entity, 1.0F);
      if (entity instanceof ServerPlayer player) {
         player.awardStat(Stats.ITEM_USED.get(this));
         CriteriaTriggers.CONSUME_ITEM.trigger(player, pStack);
      }

      if (!entity.hasInfiniteMaterials()) {
         pStack.shrink(1);
         if (pStack.isEmpty()) {
            return new ItemStack((ItemLike)TensuraConsumableItems.MAGIC_BOTTLE.get());
         }

         if (entity instanceof Player player) {
            ItemStack bottle = new ItemStack((ItemLike)TensuraConsumableItems.MAGIC_BOTTLE.get());
            if (!player.getInventory().add(bottle)) {
               player.drop(bottle, false);
            }
         }
      }

      entity.gameEvent(GameEvent.DRINK);
      return pStack;
   }

   @NotNull
   public InteractionResult interactLivingEntity(ItemStack pStack, Player player, LivingEntity entity, InteractionHand pHand) {
      if (entity.isAlive()) {
         if (!player.hasInfiniteMaterials()) {
            ItemStack bottle = new ItemStack((ItemLike)TensuraConsumableItems.MAGIC_BOTTLE.get());
            if (!player.getInventory().add(bottle)) {
               player.drop(bottle, false);
            }
         }

         entity.eat(player.level(), pStack);
         this.applyEffect(entity, 1.0F);
         entity.level().playSound(player, entity, SoundEvents.GENERIC_DRINK, SoundSource.NEUTRAL, 1.0F, 1.0F);
         return InteractionResult.sidedSuccess(player.level().isClientSide());
      } else {
         return InteractionResult.PASS;
      }
   }

   @NotNull
   public InteractionResult useOn(UseOnContext pContext) {
      return InteractionResult.PASS;
   }

   public void applyEffect(LivingEntity entity, float multiplier) {
      if (entity.isAlive()) {
         this.regenerateMagicule(entity, multiplier);
         entity.level()
            .playSound(
               null, entity.getX(), entity.getY(), entity.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_HEAL.get(), TensuraSkill.ABILITY_SOUND, 0.5F, 1.0F
            );
         TensuraParticleHelper.spawnServerParticles(
            entity.level(),
            TensuraParticleUtils.getBlueWave(0.9F, entity.getBbWidth() * 2.5F, -0.5F, true),
            entity.getX(),
            entity.getY() + entity.getBbHeight() * 0.33,
            entity.getZ()
         );
         TensuraParticleHelper.spawnServerParticles(
            entity.level(),
            TensuraParticleUtils.getBlueWave(0.9F, entity.getBbWidth() * 2.5F, -0.5F, true),
            entity.getX(),
            entity.getY() + entity.getBbHeight() * 0.66,
            entity.getZ()
         );
      }
   }

   protected void regenerateMagicule(LivingEntity entity, float multiplier) {
      if (this.getMagiculeAmount() > 0.0F) {
         double maxMP = EnergyHelper.getMaxMagicule(entity);
         IExistence existence = TensuraStorages.getExistenceFrom(entity);
         if (existence.getMagicule() < maxMP) {
            double amount = this.isPercentage() ? maxMP * this.getMagiculeAmount() : this.getMagiculeAmount();
            existence.setMagicule(Math.min(maxMP, existence.getMagicule() + amount * multiplier));
            existence.markDirty();
         }
      }
   }

   @Generated
   public float getMagiculeAmount() {
      return this.magiculeAmount;
   }

   @Generated
   public boolean isPercentage() {
      return this.percentage;
   }
}
