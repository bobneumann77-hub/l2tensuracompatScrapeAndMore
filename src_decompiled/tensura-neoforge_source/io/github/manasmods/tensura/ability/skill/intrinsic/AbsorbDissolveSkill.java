package io.github.manasmods.tensura.ability.skill.intrinsic;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.IntrinsicSkillConfig;
import io.github.manasmods.tensura.data.TensuraRaceTags;
import io.github.manasmods.tensura.data.disolving.ItemDissolving;
import io.github.manasmods.tensura.race.TensuraRace;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.data.TensuraCustomData;
import io.github.manasmods.tensura.registry.item.TensuraMobDropItems;
import io.github.manasmods.tensura.util.EnergyHelper;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.FoodProperties.PossibleEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;

public class AbsorbDissolveSkill extends Skill {
   private static final IntrinsicSkillConfig.AbsorbDissolve CONFIG = ((IntrinsicSkillConfig)ConfigRegistry.getConfig(IntrinsicSkillConfig.class)).AbsorbDissolve;
   private static final ResourceLocation SLIME_CORE_BOOST = ResourceLocation.fromNamespaceAndPath("tensura", "slime_core");

   public AbsorbDissolveSkill() {
      super(Skill.SkillType.INTRINSIC);
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      ItemStack itemStack = entity.getMainHandItem();
      Registry<ItemDissolving> registry = entity.level().registryAccess().registryOrThrow(TensuraCustomData.ITEM_DISSOLVING);
      registry.stream().filter(dissolving -> dissolving.item().equals(itemStack.getItem().arch$registryName())).findFirst().ifPresentOrElse(data -> {
         EnergyHelper.gainMagicule(entity, data.magicule() * CONFIG.magiculeMultiplier, EnergyHelper.GainType.NORMAL);
         if (data.health() > 0.0) {
            entity.heal((float)data.health() * CONFIG.healthMultiplier);
         }

         applySlimeCore(entity, itemStack);
         instance.addMasteryPoint(entity);
         FoodProperties foods = (FoodProperties)itemStack.get(DataComponents.FOOD);
         if (foods != null) {
            for (PossibleEffect effect : foods.effects()) {
               if (entity.getRandom().nextFloat() < effect.probability()) {
                  entity.addEffect(effect.effect());
               }
            }
         }

         PotionContents potion = (PotionContents)itemStack.get(DataComponents.POTION_CONTENTS);
         if (potion != null) {
            potion.forEachEffect(entity::addEffect);
         }

         if (entity instanceof ServerPlayer serverPlayer) {
            serverPlayer.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, itemStack);
         }

         itemStack.shrink(1);
         entity.swing(InteractionHand.MAIN_HAND, true);
         entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_BURP, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
      }, () -> entity.sendSystemMessage(Component.translatable("tensura.ability.activation_failed.item").withStyle(ChatFormatting.RED)));
   }

   public static void applySlimeCore(LivingEntity entity, ItemStack stack) {
      if (stack.getItem().equals(TensuraMobDropItems.SLIME_CORE.get())) {
         Optional<ManasRaceInstance> optional = RaceAPI.getRaceFrom(entity).getRace();
         if (!optional.isEmpty() && optional.get().is(TensuraRaceTags.SLIME)) {
            AttributeInstance health = entity.getAttribute(Attributes.MAX_HEALTH);
            if (health != null) {
               AttributeInstance size = entity.getAttribute(Attributes.SCALE);
               if (size != null) {
                  AttributeInstance width = entity.getAttribute(TensuraAttributes.WIDTH_MULTIPLIER);
                  if (width != null) {
                     AttributeModifier modifierSize = size.getModifier(SLIME_CORE_BOOST);
                     double defaultSize = size.hasModifier(TensuraRace.DEFAULT_RACE_ID)
                        ? size.getBaseValue() + size.getModifier(TensuraRace.DEFAULT_RACE_ID).amount()
                        : size.getValue();
                     double currentSize = modifierSize == null ? 0.0 : modifierSize.amount();
                     AttributeModifier modifierWidth = width.getModifier(SLIME_CORE_BOOST);
                     double currentWidth = modifierWidth == null ? 0.0 : modifierWidth.amount();
                     double oldWidth = width.getValue() - currentWidth;
                     double bonus = CONFIG.bonusSize;
                     if (entity.isShiftKeyDown()) {
                        double newSize = currentSize - bonus;
                        if (newSize <= 0.0) {
                           health.removeModifier(SLIME_CORE_BOOST);
                           size.removeModifier(SLIME_CORE_BOOST);
                           width.removeModifier(SLIME_CORE_BOOST);
                        } else {
                           AttributeModifier modifier = new AttributeModifier(SLIME_CORE_BOOST, newSize, Operation.ADD_VALUE);
                           size.addOrReplacePermanentModifier(modifier);
                           double newWidth = -newSize * oldWidth / (newSize + defaultSize);
                           width.addOrReplacePermanentModifier(new AttributeModifier(SLIME_CORE_BOOST, newWidth, Operation.ADD_VALUE));
                           health.addOrReplacePermanentModifier(
                              new AttributeModifier(SLIME_CORE_BOOST, modifier.amount() / bonus * CONFIG.bonusHP, Operation.ADD_VALUE)
                           );
                           entity.heal(CONFIG.bonusHP);
                        }
                     } else if (currentSize < CONFIG.maxSize) {
                        double newSize = Math.min(currentSize + bonus, CONFIG.maxSize);
                        double newWidth = -newSize * oldWidth / (newSize + defaultSize);
                        AttributeModifier modifier = new AttributeModifier(SLIME_CORE_BOOST, newSize, Operation.ADD_VALUE);
                        size.addOrReplacePermanentModifier(modifier);
                        width.addOrReplacePermanentModifier(new AttributeModifier(SLIME_CORE_BOOST, newWidth, Operation.ADD_VALUE));
                        health.addOrReplacePermanentModifier(
                           new AttributeModifier(SLIME_CORE_BOOST, modifier.amount() / bonus * CONFIG.bonusHP, Operation.ADD_VALUE)
                        );
                        entity.heal(CONFIG.bonusHP);
                     }
                  }
               }
            }
         }
      }
   }

   public static void resetSlimeCoreBoost(LivingEntity entity) {
      AttributeInstance health = entity.getAttribute(Attributes.MAX_HEALTH);
      if (health != null) {
         health.removeModifier(SLIME_CORE_BOOST);
      }

      AttributeInstance size = entity.getAttribute(Attributes.SCALE);
      if (size != null) {
         size.removeModifier(SLIME_CORE_BOOST);
      }

      AttributeInstance width = entity.getAttribute(TensuraAttributes.WIDTH_MULTIPLIER);
      if (width != null) {
         width.removeModifier(SLIME_CORE_BOOST);
      }
   }
}
