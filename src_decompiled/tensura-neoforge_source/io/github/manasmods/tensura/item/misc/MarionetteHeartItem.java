package io.github.manasmods.tensura.item.misc;

import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.item.tool.custom.DragonKnuckleItem;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.registry.item.misc.TensuraCreativeTabs;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.Alignment;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.SubordinateHelper;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class MarionetteHeartItem extends Item {
   private static final Component TOOLTIP_HEART = Component.translatable("tooltip.tensura.marionette_heart").withStyle(ChatFormatting.RED);

   public MarionetteHeartItem() {
      super(new Properties().arch$tab(TensuraCreativeTabs.MISCELLANEOUS).stacksTo(1).attributes(DragonKnuckleItem.createAttributes(-0.99, -3.0F, 0.0)));
   }

   public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
      list.add(TOOLTIP_HEART);
   }

   public boolean isFoil(ItemStack pStack) {
      return true;
   }

   public int getUseDuration(ItemStack pStack, LivingEntity entity) {
      return 10000;
   }

   public UseAnim getUseAnimation(ItemStack pStack) {
      return UseAnim.BOW;
   }

   @NotNull
   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand pHand) {
      ItemStack stack = player.getItemInHand(pHand);
      if (player.getCooldowns().isOnCooldown(stack.getItem()) && !player.hasInfiniteMaterials()) {
         return InteractionResultHolder.fail(stack);
      }

      player.startUsingItem(pHand);
      return InteractionResultHolder.consume(stack);
   }

   public void onUseTick(Level pLevel, LivingEntity pLivingEntity, ItemStack pStack, int pRemainingUseDuration) {
      if (pRemainingUseDuration % 4 == 0) {
         TensuraParticleHelper.addServerParticlesAroundSelf(pLivingEntity, ParticleTypes.ENCHANTED_HIT);
      }
   }

   public void releaseUsing(@NotNull ItemStack pStack, @NotNull Level level, @NotNull LivingEntity entity, int pTimeLeft) {
      int useTicks = this.getUseDuration(pStack, entity) - pTimeLeft;
      if (useTicks >= 10) {
         if (entity instanceof Player player) {
            IExistence existence = TensuraStorages.getExistenceFrom(entity);
            if (existence.getAlignment().equals(Alignment.DEFAULT)) {
               if (player instanceof ServerPlayer serverPlayer) {
                  CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, pStack);
               }

               if (!player.hasInfiniteMaterials()) {
                  DamageSource source = TensuraDamageTypes.getDamageSource(level, TensuraDamageTypes.ENERGY_DRAIN);
                  player.hurt(source, player.getMaxHealth() / 2.0F);
                  player.getCooldowns().addCooldown(pStack.getItem(), 2400);
                  pStack.shrink(1);
               }

               existence.setOriginalAlignment(Alignment.MAJIN);
               existence.setAlignment(Alignment.MAJIN);
               EnergyHelper.isOutOfMagiculeConsuming(entity, existence.getMagicule() - 200.0);
               existence.markDirty();
               entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.TOTEM_USE, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
               TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.EXPLOSION_EMITTER, 0.0);
               TensuraParticleHelper.addServerParticlesAroundSelf(entity, (ParticleOptions)TensuraParticleTypes.SOLAR_FLASH.get(), 1.0);
            } else {
               entity.level()
                  .playSound(
                     null,
                     entity.getX(),
                     entity.getY(),
                     entity.getZ(),
                     (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                     TensuraSkill.ABILITY_SOUND,
                     1.0F,
                     1.0F
                  );
               TensuraParticleHelper.addServerParticlesAroundSelf(entity, ParticleTypes.ANGRY_VILLAGER, 0.0);
            }
         }
      }
   }

   public boolean hurtEnemy(ItemStack pStack, LivingEntity target, LivingEntity pAttacker) {
      if (target.isAlive() && SubordinateHelper.isSubordinate(pAttacker, target)) {
         IExistence existence = TensuraStorages.getExistenceFrom(target);
         if (existence.getAlignment().equals(Alignment.DEFAULT)) {
            target.invulnerableTime = 0;
            DamageSource source = TensuraDamageTypes.getDamageSource(target.level(), TensuraDamageTypes.ENERGY_DRAIN);
            target.hurt(source, target.getMaxHealth() / 2.0F);
            if (pAttacker instanceof Player player && !player.hasInfiniteMaterials()) {
               player.getCooldowns().addCooldown(pStack.getItem(), 2400);
               pStack.shrink(1);
            }

            existence.setOriginalAlignment(Alignment.MAJIN);
            existence.setAlignment(Alignment.MAJIN);
            EnergyHelper.isOutOfMagiculeConsuming(target, existence.getMagicule() - 200.0);
            existence.markDirty();
            pAttacker.level().playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.TOTEM_USE, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
            TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.TOTEM_OF_UNDYING, 0.0);
            TensuraParticleHelper.addServerParticlesAroundSelf(target, (ParticleOptions)TensuraParticleTypes.SOLAR_FLASH.get(), 1.0);
         } else {
            target.level()
               .playSound(
                  null,
                  target.getX(),
                  target.getY(),
                  target.getZ(),
                  (SoundEvent)TensuraSoundEvents.GENERIC_CAST_FAIL.get(),
                  TensuraSkill.ABILITY_SOUND,
                  1.0F,
                  1.0F
               );
            TensuraParticleHelper.addServerParticlesAroundSelf(target, ParticleTypes.ANGRY_VILLAGER, 0.0);
         }

         return true;
      } else {
         return super.hurtEnemy(pStack, target, pAttacker);
      }
   }
}
