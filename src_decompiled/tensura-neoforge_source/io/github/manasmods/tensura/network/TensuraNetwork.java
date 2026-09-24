package io.github.manasmods.tensura.network;

import io.github.manasmods.manascore.network.api.util.NetworkUtils;
import io.github.manasmods.tensura.network.c2s.RequestAbilityModeChangePacket;
import io.github.manasmods.tensura.network.c2s.RequestDodgePacket;
import io.github.manasmods.tensura.network.c2s.RequestEvolutionPacket;
import io.github.manasmods.tensura.network.c2s.RequestIllusionItemPacket;
import io.github.manasmods.tensura.network.c2s.RequestMountAbilityPacket;
import io.github.manasmods.tensura.network.c2s.RequestNamingKeyPacket;
import io.github.manasmods.tensura.network.c2s.RequestNamingMenuPacket;
import io.github.manasmods.tensura.network.c2s.RequestResearcherEnchantingPacket;
import io.github.manasmods.tensura.network.c2s.RequestSkillNumberKeyPacket;
import io.github.manasmods.tensura.network.c2s.RequestSpatialActionPacket;
import io.github.manasmods.tensura.network.c2s.RequestSpellChangePacket;
import io.github.manasmods.tensura.network.c2s.RequestSpellbindingPacket;
import io.github.manasmods.tensura.network.s2c.DisplayTotemEffectPayload;
import io.github.manasmods.tensura.network.s2c.DisplayTotemStackEffectPayload;
import io.github.manasmods.tensura.network.s2c.HurtLivingPartPayload;
import io.github.manasmods.tensura.network.s2c.OpenDegenerateMenuPayload;
import io.github.manasmods.tensura.network.s2c.OpenHumanoidMenuPayload;
import io.github.manasmods.tensura.network.s2c.OpenIllusionItemScreenPayload;
import io.github.manasmods.tensura.network.s2c.OpenMountMenuPayload;
import io.github.manasmods.tensura.network.s2c.OpenResearcherEnchantingMenuPayload;
import io.github.manasmods.tensura.network.s2c.OpenSpatialMovementMenuPayload;
import io.github.manasmods.tensura.network.s2c.OpenSpatialStorageMenuPayload;
import io.github.manasmods.tensura.network.s2c.OpenSubAbilitySelectionMenuPayload;
import io.github.manasmods.tensura.network.s2c.SendBooleanGameruleUpdatePayload;
import io.github.manasmods.tensura.network.s2c.SendIntegerGameruleUpdatePayload;
import io.github.manasmods.tensura.network.s2c.UpdateAbilitySlotScreenPayload;

public class TensuraNetwork {
   public static void init() {
      NetworkUtils.registerC2SPayload(RequestAbilityModeChangePacket.TYPE, RequestAbilityModeChangePacket.STREAM_CODEC, RequestAbilityModeChangePacket::handle);
      NetworkUtils.registerC2SPayload(RequestSkillNumberKeyPacket.TYPE, RequestSkillNumberKeyPacket.STREAM_CODEC, RequestSkillNumberKeyPacket::handle);
      NetworkUtils.registerC2SPayload(RequestMountAbilityPacket.TYPE, RequestMountAbilityPacket.STREAM_CODEC, RequestMountAbilityPacket::handle);
      NetworkUtils.registerC2SPayload(
         RequestResearcherEnchantingPacket.TYPE, RequestResearcherEnchantingPacket.STREAM_CODEC, RequestResearcherEnchantingPacket::handle
      );
      NetworkUtils.registerC2SPayload(RequestSpatialActionPacket.TYPE, RequestSpatialActionPacket.STREAM_CODEC, RequestSpatialActionPacket::handle);
      NetworkUtils.registerC2SPayload(RequestIllusionItemPacket.TYPE, RequestIllusionItemPacket.STREAM_CODEC, RequestIllusionItemPacket::handle);
      NetworkUtils.registerC2SPayload(RequestDodgePacket.TYPE, RequestDodgePacket.STREAM_CODEC, RequestDodgePacket::handle);
      NetworkUtils.registerC2SPayload(RequestEvolutionPacket.TYPE, RequestEvolutionPacket.STREAM_CODEC, RequestEvolutionPacket::handle);
      NetworkUtils.registerC2SPayload(RequestNamingKeyPacket.TYPE, RequestNamingKeyPacket.STREAM_CODEC, RequestNamingKeyPacket::handle);
      NetworkUtils.registerC2SPayload(RequestNamingMenuPacket.TYPE, RequestNamingMenuPacket.STREAM_CODEC, RequestNamingMenuPacket::handle);
      NetworkUtils.registerC2SPayload(RequestSpellbindingPacket.TYPE, RequestSpellbindingPacket.STREAM_CODEC, RequestSpellbindingPacket::handle);
      NetworkUtils.registerC2SPayload(RequestSpellChangePacket.TYPE, RequestSpellChangePacket.STREAM_CODEC, RequestSpellChangePacket::handle);
      NetworkUtils.registerS2CPayload(DisplayTotemEffectPayload.TYPE, DisplayTotemEffectPayload.STREAM_CODEC, DisplayTotemEffectPayload::handle);
      NetworkUtils.registerS2CPayload(DisplayTotemStackEffectPayload.TYPE, DisplayTotemStackEffectPayload.STREAM_CODEC, DisplayTotemStackEffectPayload::handle);
      NetworkUtils.registerS2CPayload(HurtLivingPartPayload.TYPE, HurtLivingPartPayload.STREAM_CODEC, HurtLivingPartPayload::handle);
      NetworkUtils.registerS2CPayload(OpenIllusionItemScreenPayload.TYPE, OpenIllusionItemScreenPayload.STREAM_CODEC, OpenIllusionItemScreenPayload::handle);
      NetworkUtils.registerS2CPayload(UpdateAbilitySlotScreenPayload.TYPE, UpdateAbilitySlotScreenPayload.STREAM_CODEC, UpdateAbilitySlotScreenPayload::handle);
      NetworkUtils.registerS2CPayload(
         SendBooleanGameruleUpdatePayload.TYPE, SendBooleanGameruleUpdatePayload.STREAM_CODEC, SendBooleanGameruleUpdatePayload::handle
      );
      NetworkUtils.registerS2CPayload(
         SendIntegerGameruleUpdatePayload.TYPE, SendIntegerGameruleUpdatePayload.STREAM_CODEC, SendIntegerGameruleUpdatePayload::handle
      );
      NetworkUtils.registerS2CPayload(OpenHumanoidMenuPayload.TYPE, OpenHumanoidMenuPayload.STREAM_CODEC, OpenHumanoidMenuPayload::handle);
      NetworkUtils.registerS2CPayload(OpenMountMenuPayload.TYPE, OpenMountMenuPayload.STREAM_CODEC, OpenMountMenuPayload::handle);
      NetworkUtils.registerS2CPayload(
         OpenResearcherEnchantingMenuPayload.TYPE, OpenResearcherEnchantingMenuPayload.STREAM_CODEC, OpenResearcherEnchantingMenuPayload::handle
      );
      NetworkUtils.registerS2CPayload(OpenSpatialMovementMenuPayload.TYPE, OpenSpatialMovementMenuPayload.STREAM_CODEC, OpenSpatialMovementMenuPayload::handle);
      NetworkUtils.registerS2CPayload(OpenSpatialStorageMenuPayload.TYPE, OpenSpatialStorageMenuPayload.STREAM_CODEC, OpenSpatialStorageMenuPayload::handle);
      NetworkUtils.registerS2CPayload(
         OpenSubAbilitySelectionMenuPayload.TYPE, OpenSubAbilitySelectionMenuPayload.STREAM_CODEC, OpenSubAbilitySelectionMenuPayload::handle
      );
      NetworkUtils.registerS2CPayload(OpenDegenerateMenuPayload.TYPE, OpenDegenerateMenuPayload.STREAM_CODEC, OpenDegenerateMenuPayload::handle);
   }
}
