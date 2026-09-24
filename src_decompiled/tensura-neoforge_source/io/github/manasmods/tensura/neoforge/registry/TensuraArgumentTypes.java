package io.github.manasmods.tensura.neoforge.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.manasmods.tensura.command.argument.AlignmentArgument;
import io.github.manasmods.tensura.command.argument.BossFightArgument;
import io.github.manasmods.tensura.command.argument.BossFightPlayerHandlerArgument;
import io.github.manasmods.tensura.command.argument.ElementArgument;
import io.github.manasmods.tensura.command.argument.MagicTypeArgument;
import io.github.manasmods.tensura.command.argument.SkillTypeArgument;
import io.github.manasmods.tensura.command.argument.SpiritLevelArgument;
import io.github.manasmods.tensura.command.argument.TransmissionTypeArgument;
import io.github.manasmods.tensura.command.argument.WorldRestrictionArgument;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.Registries;

public class TensuraArgumentTypes {
   private static final DeferredRegister<ArgumentTypeInfo<?, ?>> ARGUMENT_TYPES = DeferredRegister.create("tensura", Registries.COMMAND_ARGUMENT_TYPE);
   public static final RegistrySupplier<SingletonArgumentInfo<AlignmentArgument>> ALIGNMENT = ARGUMENT_TYPES.register(
      "alignment",
      () -> (SingletonArgumentInfo)ArgumentTypeInfos.registerByClass(AlignmentArgument.class, SingletonArgumentInfo.contextFree(AlignmentArgument::alignment))
   );
   public static final RegistrySupplier<SingletonArgumentInfo<ElementArgument>> ELEMENT = ARGUMENT_TYPES.register(
      "element",
      () -> (SingletonArgumentInfo)ArgumentTypeInfos.registerByClass(ElementArgument.class, SingletonArgumentInfo.contextFree(ElementArgument::element))
   );
   public static final RegistrySupplier<SingletonArgumentInfo<MagicTypeArgument>> MAGIC_TYPE = ARGUMENT_TYPES.register(
      "magic_type",
      () -> (SingletonArgumentInfo)ArgumentTypeInfos.registerByClass(MagicTypeArgument.class, SingletonArgumentInfo.contextFree(MagicTypeArgument::magicType))
   );
   public static final RegistrySupplier<SingletonArgumentInfo<SkillTypeArgument>> SKILL_TYPE = ARGUMENT_TYPES.register(
      "skill_type",
      () -> (SingletonArgumentInfo)ArgumentTypeInfos.registerByClass(SkillTypeArgument.class, SingletonArgumentInfo.contextFree(SkillTypeArgument::skillType))
   );
   public static final RegistrySupplier<SingletonArgumentInfo<SpiritLevelArgument>> SPIRIT_LEVEL = ARGUMENT_TYPES.register(
      "spirit_level",
      () -> (SingletonArgumentInfo)ArgumentTypeInfos.registerByClass(
         SpiritLevelArgument.class, SingletonArgumentInfo.contextFree(SpiritLevelArgument::spiritLevel)
      )
   );
   public static final RegistrySupplier<SingletonArgumentInfo<BossFightArgument>> BOSS_FIGHT = ARGUMENT_TYPES.register(
      "boss_fight",
      () -> (SingletonArgumentInfo)ArgumentTypeInfos.registerByClass(BossFightArgument.class, SingletonArgumentInfo.contextFree(BossFightArgument::bossFight))
   );
   public static final RegistrySupplier<SingletonArgumentInfo<BossFightPlayerHandlerArgument>> BOSS_FIGHT_PLAYER_HANDLER = ARGUMENT_TYPES.register(
      "boss_fight_player_handler",
      () -> (SingletonArgumentInfo)ArgumentTypeInfos.registerByClass(
         BossFightPlayerHandlerArgument.class, SingletonArgumentInfo.contextFree(BossFightPlayerHandlerArgument::handler)
      )
   );
   public static final RegistrySupplier<SingletonArgumentInfo<TransmissionTypeArgument>> TRANSMISSION_TYPE = ARGUMENT_TYPES.register(
      "transmission_type",
      () -> (SingletonArgumentInfo)ArgumentTypeInfos.registerByClass(
         TransmissionTypeArgument.class, SingletonArgumentInfo.contextFree(TransmissionTypeArgument::type)
      )
   );
   public static final RegistrySupplier<SingletonArgumentInfo<WorldRestrictionArgument>> WORLD_RESTRICTION = ARGUMENT_TYPES.register(
      "world_restriction",
      () -> (SingletonArgumentInfo)ArgumentTypeInfos.registerByClass(
         WorldRestrictionArgument.class, SingletonArgumentInfo.contextFree(WorldRestrictionArgument::worldRestriction)
      )
   );

   public static void init() {
      ARGUMENT_TYPES.register();
   }
}
