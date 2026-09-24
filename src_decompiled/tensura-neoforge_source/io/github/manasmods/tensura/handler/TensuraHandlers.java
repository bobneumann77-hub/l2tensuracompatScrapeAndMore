package io.github.manasmods.tensura.handler;

import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.PlayerEvent.PlayerJoin;
import dev.architectury.networking.NetworkManager;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.tensura.config.entity.AttributeConfig;
import io.github.manasmods.tensura.mixin.accessor.AccessorDataComponentSimpleType;
import io.github.manasmods.tensura.mixin.accessor.AccessorRangedAttribute;
import io.github.manasmods.tensura.network.s2c.SendBooleanGameruleUpdatePayload;
import io.github.manasmods.tensura.network.s2c.SendIntegerGameruleUpdatePayload;
import io.github.manasmods.tensura.world.TensuraGameRules;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class TensuraHandlers {
   public static void init() {
      AbilityHandler.init();
      AttributeHandler.init();
      BehaviourHandler.init();
      DamagingHandler.init();
      DeathHandler.init();
      EffectsHandler.init();
      GearHandler.init();
      MobHandler.init();
      RacesHandler.init();
      LabyrinthHandler.init();
      PlayerEvent.PLAYER_JOIN
         .register(
            (PlayerJoin)player -> {
               if (!player.level().isClientSide()) {
                  NetworkManager.sendToPlayer(
                     player,
                     new SendIntegerGameruleUpdatePayload(
                        SendIntegerGameruleUpdatePayload.GameruleKey.AWAKEN_SOUL, player.level().getGameRules().getInt(TensuraGameRules.DEMON_LORD_AWAKEN)
                     )
                  );
                  NetworkManager.sendToPlayer(
                     player,
                     new SendIntegerGameruleUpdatePayload(
                        SendIntegerGameruleUpdatePayload.GameruleKey.RESET_PER_SKILL_LOCK,
                        player.level().getGameRules().getInt(TensuraGameRules.RESET_PER_SKILL_LOCK)
                     )
                  );
                  NetworkManager.sendToPlayer(
                     player,
                     new SendIntegerGameruleUpdatePayload(
                        SendIntegerGameruleUpdatePayload.GameruleKey.RESET_INCOMPLETE_PENALTY,
                        player.level().getGameRules().getInt(TensuraGameRules.RESET_INCOMPLETE_PENALTY)
                     )
                  );
                  NetworkManager.sendToPlayer(
                     player,
                     new SendBooleanGameruleUpdatePayload(
                        SendBooleanGameruleUpdatePayload.GameruleKey.PLAYER_MANUAL_DODGING,
                        player.level().getGameRules().getBoolean(TensuraGameRules.PLAYER_MANUAL_DODGING)
                     )
                  );
                  NetworkManager.sendToPlayer(
                     player,
                     new SendBooleanGameruleUpdatePayload(
                        SendBooleanGameruleUpdatePayload.GameruleKey.TENSURA_NAME,
                        player.level().getGameRules().getBoolean(TensuraGameRules.TENSURA_DISPLAY_NAME)
                     )
                  );
                  NetworkManager.sendToPlayer(
                     player,
                     new SendBooleanGameruleUpdatePayload(
                        SendBooleanGameruleUpdatePayload.GameruleKey.DISABLE_NULLIFICATION,
                        player.level().getGameRules().getBoolean(TensuraGameRules.DISABLE_NULLIFICATION)
                     )
                  );
               }
            }
         );
   }

   public static void onAfterRegistration() {
      AccessorDataComponentSimpleType maxStack = (AccessorDataComponentSimpleType)DataComponents.MAX_STACK_SIZE;
      maxStack.setCodec(ExtraCodecs.intRange(1, 100));
      AttributeConfig config = (AttributeConfig)ConfigRegistry.getConfig(AttributeConfig.class);
      AccessorRangedAttribute accessorHP = (AccessorRangedAttribute)Attributes.MAX_HEALTH.value();
      accessorHP.setMaxValue(config.maxHP);
      AccessorRangedAttribute accessorAttack = (AccessorRangedAttribute)Attributes.ATTACK_DAMAGE.value();
      accessorAttack.setMaxValue(config.maxAttack);
      AccessorRangedAttribute accessorArmor = (AccessorRangedAttribute)Attributes.ARMOR.value();
      accessorArmor.setMaxValue(config.maxArmor);
      AccessorRangedAttribute accessorToughness = (AccessorRangedAttribute)Attributes.ARMOR_TOUGHNESS.value();
      accessorToughness.setMaxValue(config.maxArmorToughness);
      AccessorRangedAttribute accessorScale = (AccessorRangedAttribute)Attributes.SCALE.value();
      accessorScale.setMaxValue(config.maxScale);
      accessorScale.setMinValue(config.minScale);
   }
}
