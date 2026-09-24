package io.github.manasmods.tensura.ability.skill.unique;

import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillClientUtils;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.config.ability.skill.UniqueSkillConfig;
import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.sound.TensuraSoundEvents;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.player.ITensuraPlayer;
import io.github.manasmods.tensura.storage.player.TensuraPlayerStorage;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.phys.Vec3;

public class ObserverSkill extends Skill {
   private static final UniqueSkillConfig.Observer CONFIG = ((UniqueSkillConfig)ConfigRegistry.getConfig(UniqueSkillConfig.class)).Observer;
   protected static final ResourceLocation OBSERVER = ResourceLocation.fromNamespaceAndPath("tensura", "observer");

   public ObserverSkill() {
      super(Skill.SkillType.UNIQUE);
      this.addHeldAttributeModifier(TensuraAttributes.PRESENCE_SENSE, OBSERVER, CONFIG.bonusSenseLevel, Operation.ADD_VALUE);
      this.addHeldAttributeModifier(TensuraAttributes.PRESENCE_SENSE_RADIUS, OBSERVER, CONFIG.bonusSenseRadius, Operation.ADD_VALUE);
      SkillClientUtils.XRAY_TYPES
         .add(
            player -> {
               ITensuraPlayer data = TensuraStorages.getPlayerDataFrom(player);
               return data.getPresenceSenseMode() != TensuraPlayerStorage.PresenceSenseMode.TREASURES.getId()
                  ? null
                  : new SkillClientUtils.XrayType(
                     state -> state.is(TensuraBlockTags.TREASURE_BLOCKS),
                     (int)player.getAttributeValue(TensuraAttributes.PRESENCE_SENSE_RADIUS) / 2,
                     1.0F,
                     0.84F,
                     0.0F,
                     1.0F
                  );
            }
         );
      SkillClientUtils.XRAY_TYPES
         .add(
            player -> {
               ITensuraPlayer data = TensuraStorages.getPlayerDataFrom(player);
               return data.getPresenceSenseMode() != TensuraPlayerStorage.PresenceSenseMode.TRAPS.getId()
                  ? null
                  : new SkillClientUtils.XrayType(
                     state -> state.is(TensuraBlockTags.TRAP_BLOCKS),
                     (int)player.getAttributeValue(TensuraAttributes.PRESENCE_SENSE_RADIUS) / 2,
                     1.0F,
                     0.0F,
                     0.0F,
                     1.0F
                  );
            }
         );
   }

   @Override
   public double getDefaultAcquiringMagiculeCost() {
      return CONFIG.mpAcquirement;
   }

   @Override
   protected boolean canActivateInRaceLimit(ManasSkillInstance instance, int mode) {
      return true;
   }

   public boolean canBeToggled(ManasSkillInstance instance, LivingEntity living) {
      return instance.getMastery() >= 0.0;
   }

   public int getModes(ManasSkillInstance instance) {
      return 2;
   }

   @Override
   public int nextMode(LivingEntity entity, ManasSkillInstance instance, int mode, boolean reverse) {
      return mode == 0 ? 1 : 0;
   }

   @Override
   public String getModeId(ManasSkillInstance instance, int mode) {
      return switch (mode) {
         case 0 -> "observer.danger";
         case 1 -> "observer.presence";
         default -> super.getModeId(instance, mode);
      };
   }

   @Override
   public double getMagiculeCost(LivingEntity entity, ManasSkillInstance instance, int mode) {
      return 0.0;
   }

   public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
      AttributeInstance melee = entity.getAttribute(TensuraAttributes.AUTO_MELEE_DODGE_CHANCE);
      if (melee != null) {
         melee.addOrReplacePermanentModifier(new AttributeModifier(OBSERVER, CONFIG.meleeDodge, Operation.ADD_VALUE));
      }

      AttributeInstance projectile = entity.getAttribute(TensuraAttributes.AUTO_PROJECTILE_DODGE_CHANCE);
      if (projectile != null) {
         projectile.addOrReplacePermanentModifier(new AttributeModifier(OBSERVER, CONFIG.projectileDodge, Operation.ADD_VALUE));
      }
   }

   public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
      AttributeInstance melee = entity.getAttribute(TensuraAttributes.AUTO_MELEE_DODGE_CHANCE);
      if (melee != null) {
         melee.removeModifier(OBSERVER);
      }

      AttributeInstance projectile = entity.getAttribute(TensuraAttributes.AUTO_PROJECTILE_DODGE_CHANCE);
      if (projectile != null) {
         projectile.removeModifier(OBSERVER);
      }
   }

   public void onPressed(ManasSkillInstance instance, LivingEntity entity, int keyNumber, int mode) {
      if (entity instanceof ServerPlayer player) {
         if (player.isShiftKeyDown()) {
            ITensuraPlayer data = TensuraStorages.getPlayerDataFrom(player);
            switch (data.getPresenceSenseMode()) {
               case 1:
                  data.setPresenceSenseMode(TensuraPlayerStorage.PresenceSenseMode.TRAPS.getId());
                  player.displayClientMessage(
                     Component.translatable("tensura.skill.mode.observer.presence.traps").setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_AQUA)), true
                  );
                  break;
               case 2:
                  if (instance.isMastered(entity)) {
                     data.setPresenceSenseMode(TensuraPlayerStorage.PresenceSenseMode.TREASURES.getId());
                     player.displayClientMessage(
                        Component.translatable("tensura.skill.mode.observer.presence.treasures").setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_AQUA)),
                        true
                     );
                  } else {
                     data.setPresenceSenseMode(TensuraPlayerStorage.PresenceSenseMode.ALL_ENTITIES.getId());
                     player.displayClientMessage(
                        Component.translatable("tensura.skill.mode.observer.presence.all").setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_AQUA)), true
                     );
                  }
                  break;
               case 3:
                  data.setPresenceSenseMode(TensuraPlayerStorage.PresenceSenseMode.ALL_ENTITIES.getId());
                  player.displayClientMessage(
                     Component.translatable("tensura.skill.mode.observer.presence.all").setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_AQUA)), true
                  );
                  break;
               default:
                  data.setPresenceSenseMode(TensuraPlayerStorage.PresenceSenseMode.HOSTILE_ONLY.getId());
                  player.displayClientMessage(
                     Component.translatable("tensura.skill.mode.observer.presence.monster").setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_AQUA)), true
                  );
            }

            player.playNotifySound((SoundEvent)TensuraSoundEvents.GENERIC_CAST.get(), TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
            data.markDirty();
         }
      }
   }

   public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int heldTicks, int mode) {
      if (heldTicks % 20 == 0 && EnergyHelper.isOutOfEnergy(entity, instance, mode)) {
         return false;
      }

      if (heldTicks % BASE_CONFIG.Mastery.masteryHoldTick == 0 && heldTicks > 0) {
         instance.addMasteryPoint(entity);
      }

      return true;
   }

   public boolean onBeingTargeted(ManasSkillInstance instance, Changeable<LivingEntity> owner, LivingEntity attacker) {
      if (!this.isInSlot((LivingEntity)owner.get(), instance, 0)) {
         return true;
      }

      if (owner.get() instanceof ServerPlayer player) {
         if (!(attacker instanceof Mob mob)) {
            return true;
         } else {
            if (mob.getTarget() == null || !player.is(mob.getTarget())) {
               if (player.getRandom().nextBoolean()) {
                  instance.addMasteryPoint(player);
               }

               this.sendSound(player, mob);
            }

            return true;
         }
      } else {
         return true;
      }
   }

   private void sendSound(ServerPlayer user, LivingEntity target) {
      Vec3 eyeVec = user.getEyePosition();
      Vec3 soundPos = eyeVec.add(target.getEyePosition().subtract(eyeVec).normalize().scale(5.0));
      user.connection
         .send(
            new ClientboundSoundPacket(
               BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.BELL_BLOCK),
               TensuraSkill.ABILITY_SOUND,
               soundPos.x(),
               eyeVec.y(),
               soundPos.z(),
               1.0F,
               1.0F,
               user.getRandom().nextLong()
            )
         );
   }
}
