package io.github.manasmods.tensura.item.weapon.spell;

import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.manascore.skill.api.Skills;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.entity.monster.SlimeEntity;
import io.github.manasmods.tensura.entity.variant.SlimeColor;
import io.github.manasmods.tensura.entity.variant.SlimeType;
import io.github.manasmods.tensura.item.TensuraToolTiers;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.item.TensuraMaterialItems;
import io.github.manasmods.tensura.registry.item.TensuraMobDropItems;
import io.github.manasmods.tensura.registry.item.misc.TensuraCreativeTabs;
import io.github.manasmods.tensura.registry.item.misc.TensuraDataComponents;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.util.ObjectSelectionHelper;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class SlimeStaffItem extends SimpleSpellCastItem {
   public SlimeStaffItem() {
      super(
         10,
         3,
         TensuraToolTiers.HIGH_MAGISTEEL,
         -7,
         0.1,
         new Properties()
            .arch$tab(TensuraCreativeTabs.GEARS)
            .durability(1000)
            .rarity(Rarity.EPIC)
            .component((DataComponentType)TensuraDataComponents.SKILL.get(), ResourceLocation.fromNamespaceAndPath("tensura", "summon_slime"))
      );
   }

   public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
      return repair.getItem() == TensuraMobDropItems.SLIME_CHUNK.get()
         || repair.getItem() == TensuraMobDropItems.SLIME_CORE.get()
         || repair.getItem() == TensuraMaterialItems.HIGH_MAGISTEEL_INGOT.get();
   }

   public MutableComponent getSummonName() {
      Component summon = Component.translatable("tooltip.tensura.spell_cast_item.slime_summon").withStyle(ChatFormatting.BLUE);
      return Component.literal("[").append(summon).append("]");
   }

   public MutableComponent getSummonName(ItemStack stack) {
      Component summon = Component.translatable("tooltip.tensura.spell_cast_item.slime_summon").withStyle(ChatFormatting.BLUE);
      int mode = (Integer)stack.getOrDefault((DataComponentType)TensuraDataComponents.MODE.get(), 0);
      MutableComponent modeName;
      if (mode <= 0) {
         modeName = Component.translatable("tooltip.tensura.spell_cast_item.slime_summon.summon");
      } else {
         modeName = Component.translatable("tooltip.tensura.spell_cast_item.slime_summon.control");
      }

      return Component.literal("[").append(summon).append(" - ").append(modeName.withStyle(ChatFormatting.GRAY)).append("]");
   }

   @Override
   public void renderTooltip(ItemStack stack, List<Component> lines) {
      List<ResourceLocation> skills = (List<ResourceLocation>)stack.get((DataComponentType)TensuraDataComponents.SKILL_LIST.get());
      if (skills != null && !skills.isEmpty()) {
         super.renderTooltip(stack, lines);
      } else {
         lines.add(Component.empty());
         lines.add(Component.translatable("tooltip.tensura.spell_cast_item.list").withStyle(ChatFormatting.GRAY));
      }

      if ((Boolean)stack.getOrDefault((DataComponentType)TensuraDataComponents.ALTERNATIVE_MODE.get(), true)) {
         lines.addLast(Component.literal(" ").append(this.getSummonName(stack).withStyle(ChatFormatting.GOLD)));
      } else {
         lines.addLast(Component.literal(" ").append(this.getSummonName()).withStyle(ChatFormatting.GRAY));
      }
   }

   @Override
   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      if (level.isClientSide()) {
         return InteractionResultHolder.sidedSuccess(stack, true);
      }

      boolean slimeMode = (Boolean)stack.getOrDefault((DataComponentType)TensuraDataComponents.ALTERNATIVE_MODE.get(), true);
      if (!slimeMode) {
         return super.use(level, player, hand);
      }

      HitResult hitresult = getPlayerPOVHitResult(level, player, Fluid.NONE);
      if (!hitresult.getType().equals(Type.BLOCK)) {
         if (!player.isSecondaryUseActive()) {
            LivingEntity target = ObjectSelectionHelper.getTargetingEntity(player, 32.0, true, false);
            if (target == null) {
               return InteractionResultHolder.fail(player.getItemInHand(hand));
            }

            if (target.hasInfiniteMaterials()) {
               return InteractionResultHolder.fail(player.getItemInHand(hand));
            }

            List<SlimeEntity> list = level.getEntitiesOfClass(SlimeEntity.class, player.getBoundingBox().inflate(25.0), entity -> entity.isOwnedBy(player));
            if (list.isEmpty()) {
               return InteractionResultHolder.fail(player.getItemInHand(hand));
            }

            for (SlimeEntity slime : list) {
               slime.setOrderedToSit(Boolean.FALSE);
               slime.setTarget(target);
            }

            player.level()
               .playSound(
                  null, player.getX(), player.getY(), player.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), TensuraSkill.ABILITY_SOUND, 0.8F, 0.8F
               );
            target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 100, 0, false, false, false));
            player.getItemInHand(hand).hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
            player.swing(hand);
            return InteractionResultHolder.consume(player.getItemInHand(hand));
         }

         List<SlimeEntity> list = level.getEntitiesOfClass(SlimeEntity.class, player.getBoundingBox().inflate(25.0), entity -> entity.isOwnedBy(player));
         if (list.isEmpty()) {
            return InteractionResultHolder.fail(stack);
         }

         boolean sit = Boolean.TRUE.equals(stack.get((DataComponentType)TensuraDataComponents.SECONDARY_MODE.get()));
         stack.set((DataComponentType)TensuraDataComponents.SECONDARY_MODE.get(), !sit);

         for (SlimeEntity slime : list) {
            slime.setOrderedToSit(!sit);
            if (slime.isOrderedToSit()) {
               slime.jumping = false;
               slime.getNavigation().stop();
               slime.setTarget(null);
            }
         }

         if (!sit) {
            player.displayClientMessage(Component.translatable("tensura.message.slime_staff.sit"), true);
         } else {
            player.displayClientMessage(Component.translatable("tensura.message.slime_staff.follow"), true);
         }

         player.level()
            .playSound(
               null, player.getX(), player.getY(), player.getZ(), (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), TensuraSkill.ABILITY_SOUND, 0.8F, 0.8F
            );
         player.getItemInHand(hand).hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
         player.swing(hand);
      }

      return InteractionResultHolder.pass(player.getItemInHand(hand));
   }

   public InteractionResult useOn(UseOnContext context) {
      Player player = context.getPlayer();
      if (player == null) {
         return super.useOn(context);
      }

      ItemStack stack = context.getItemInHand();
      boolean slimeMode = (Boolean)stack.getOrDefault((DataComponentType)TensuraDataComponents.ALTERNATIVE_MODE.get(), true);
      if (!slimeMode) {
         return super.useOn(context);
      }

      Level level = context.getLevel();
      double xPos = context.getClickLocation().x;
      double yPos = context.getClickLocation().y;
      double zPos = context.getClickLocation().z;
      int mode = (Integer)stack.getOrDefault((DataComponentType)TensuraDataComponents.MODE.get(), 0);
      if (player.hasInfiniteMaterials() || !player.getCooldowns().isOnCooldown(this)) {
         if (mode != 0) {
            if (!level.isClientSide()) {
               List<SlimeEntity> list = level.getEntitiesOfClass(
                  SlimeEntity.class, player.getBoundingBox().inflate(25.0), entity -> entity.isOwnedBy(player) && !entity.isOrderedToSit()
               );
               if (list.isEmpty()) {
                  return super.useOn(context);
               }

               stack.hurtAndBreak(50, player, LivingEntity.getSlotForHand(context.getHand()));

               for (SlimeEntity slime : list) {
                  slime.unRide();
                  slime.moveTo(xPos, yPos, zPos);
               }

               player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_TELEPORT, TensuraSkill.ABILITY_SOUND, 0.8F, 0.8F);
            }
         } else {
            if (level instanceof ServerLevel serverlevel) {
               stack.hurtAndBreak(100, player, LivingEntity.getSlotForHand(context.getHand()));
               if (player instanceof ServerPlayer serverPlayer) {
                  CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, ObjectSelectionHelper.getBlockPos(new Vec3(xPos, yPos, zPos)), stack);
               }

               SlimeEntity slime = new SlimeEntity((EntityType<? extends SlimeEntity>)MonsterEntityTypes.SLIME.get(), level);
               slime.setPos(xPos, yPos, zPos);
               slime.finalizeSpawn(serverlevel, serverlevel.getCurrentDifficultyAt(slime.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
               int variant = player.getRandom().nextInt(4);
               slime.setColor(SlimeColor.byId(variant));
               slime.setSize(3.0F, true);
               slime.setVariant(SlimeType.SUMMONED);
               slime.tame(player);
               level.addFreshEntity(slime);
               player.level()
                  .playSound(
                     null,
                     player.getX(),
                     player.getY(),
                     player.getZ(),
                     (SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(),
                     TensuraSkill.ABILITY_SOUND,
                     0.8F,
                     0.8F
                  );
               slime.slimeProduceTime = 3600;
            }

            if (!player.hasInfiniteMaterials()) {
               player.getCooldowns().addCooldown(this, 20);
            }

            level.addParticle(TensuraParticleUtils.getColorlessWave(0.3F, 1.0F), xPos, yPos + 0.25, zPos, 0.0, 0.05, 0.0);
         }
      }

      return super.useOn(context);
   }

   @Override
   public void onPostMagicBinding(Player player, ItemStack stack) {
      List<ResourceLocation> skills = (List<ResourceLocation>)stack.get((DataComponentType)TensuraDataComponents.SKILL_LIST.get());
      if (skills == null || skills.isEmpty()) {
         stack.set((DataComponentType)TensuraDataComponents.ALTERNATIVE_MODE.get(), true);
         stack.set((DataComponentType)TensuraDataComponents.SKILL.get(), ResourceLocation.fromNamespaceAndPath("tensura", "summon_slime"));
      }
   }

   @Override
   public void changeMode(Player player, ItemStack stack) {
      boolean slimeMode = (Boolean)stack.getOrDefault((DataComponentType)TensuraDataComponents.ALTERNATIVE_MODE.get(), true);
      if (!slimeMode) {
         super.changeMode(player, stack);
      } else {
         int mode = (Integer)stack.getOrDefault((DataComponentType)TensuraDataComponents.MODE.get(), 0);
         if (mode == 0) {
            stack.set((DataComponentType)TensuraDataComponents.MODE.get(), 1);
            player.displayClientMessage(
               Component.translatable(
                     "tensura.skill.mode.changed",
                     new Object[]{this.getSummonName(), Component.translatable("tooltip.tensura.spell_cast_item.slime_summon.control")}
                  )
                  .setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)),
               true
            );
         } else {
            stack.set((DataComponentType)TensuraDataComponents.MODE.get(), 0);
            player.displayClientMessage(
               Component.translatable(
                     "tensura.skill.mode.changed",
                     new Object[]{this.getSummonName(), Component.translatable("tooltip.tensura.spell_cast_item.slime_summon.summon")}
                  )
                  .setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)),
               true
            );
         }

         player.getCooldowns().addCooldown(stack.getItem(), 3);
      }
   }

   @Override
   public void changeMagic(Player player, ItemStack stack, double delta) {
      List<ResourceLocation> skills = (List<ResourceLocation>)stack.get((DataComponentType)TensuraDataComponents.SKILL_LIST.get());
      if (skills != null && !skills.isEmpty()) {
         ResourceLocation selected = (ResourceLocation)stack.getOrDefault((DataComponentType)TensuraDataComponents.SKILL.get(), skills.getFirst());
         if (skills.size() > 0) {
            int newSkill = 0;
            int index = skills.indexOf(selected);
            int max = Math.min(skills.size(), SimpleSpellCastItem.getMagicSlots(player.level(), stack));
            if (index != -1) {
               newSkill = (int)(index + delta);
               if (newSkill >= max + 1) {
                  newSkill = 0;
               } else if (newSkill < 0) {
                  newSkill = max;
               }
            }

            if (newSkill == max) {
               stack.set((DataComponentType)TensuraDataComponents.MODE.get(), 0);
               stack.set((DataComponentType)TensuraDataComponents.ALTERNATIVE_MODE.get(), true);
               stack.set((DataComponentType)TensuraDataComponents.SKILL.get(), ResourceLocation.fromNamespaceAndPath("tensura", "summon_slime"));
               player.displayClientMessage(
                  Component.translatable("tensura.skill.preset.changed_spell", new Object[]{this.getSummonName(stack)})
                     .setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)),
                  true
               );
            } else {
               stack.set((DataComponentType)TensuraDataComponents.ALTERNATIVE_MODE.get(), false);
               ResourceLocation skill = skills.get(newSkill);
               Skills storage = SkillAPI.getSkillsFrom(player);
               ManasSkill manasSkill = (ManasSkill)SkillAPI.getSkillRegistry().get(skill);
               ManasSkillInstance instance = getMagicInstance(storage, stack, manasSkill, true);
               if (instance == null) {
                  stack.set((DataComponentType)TensuraDataComponents.MODE.get(), 0);
                  stack.set((DataComponentType)TensuraDataComponents.SKILL.get(), skill);
                  if (manasSkill == null) {
                     return;
                  }

                  MutableComponent name;
                  if (manasSkill instanceof TensuraSkill tensuraSkill) {
                     name = Component.literal("[")
                        .append(tensuraSkill.getColoredName())
                        .append(" - ")
                        .append(tensuraSkill.getModeName(instance, 0))
                        .append("]");
                  } else {
                     name = manasSkill.getChatDisplayName(false);
                  }

                  player.displayClientMessage(
                     Component.translatable("tensura.skill.preset.changed_spell", new Object[]{name}).setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)),
                     true
                  );
               } else {
                  Changeable<ManasSkillInstance> instanceChangeable = Changeable.of(instance);
                  Changeable<Integer> modeChangeable = Changeable.of(0);
                  if (instance.getSkill() instanceof TensuraSkill tensuraSkill
                     && (
                        !tensuraSkill.canBeSlotted(instance, player, 0)
                           || !tensuraSkill.onAbilityEquipped(player, instanceChangeable, modeChangeable, Changeable.of(0), Changeable.of(0))
                     )) {
                     if ((Integer)modeChangeable.get() == -1) {
                        skills.removeIf(resourceLocation -> resourceLocation.equals(skill));
                        stack.set((DataComponentType)TensuraDataComponents.SKILL_LIST.get(), skills);
                        this.changeMagic(player, stack, delta);
                     }

                     return;
                  }

                  stack.set((DataComponentType)TensuraDataComponents.MODE.get(), (Integer)modeChangeable.get());
                  stack.set((DataComponentType)TensuraDataComponents.SKILL.get(), ((ManasSkillInstance)instanceChangeable.get()).getSkillId());
                  MutableComponent name;
                  if (manasSkill instanceof TensuraSkill tensuraSkill) {
                     name = Component.literal("[")
                        .append(tensuraSkill.getColoredName())
                        .append(" - ")
                        .append(tensuraSkill.getModeName(instance, (Integer)modeChangeable.get()))
                        .append("]");
                  } else {
                     name = manasSkill.getChatDisplayName(false);
                  }

                  player.displayClientMessage(
                     Component.translatable("tensura.skill.preset.changed_spell", new Object[]{name}).setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)),
                     true
                  );
               }
            }
         }
      }
   }
}
