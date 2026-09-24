package dev.xkmc.l2hostility.init;

import com.tterrag.registrate.providers.DataProviderInitializer;
import com.tterrag.registrate.providers.ProviderType;
import dev.xkmc.l2complements.init.registrate.LCEnchantments;
import dev.xkmc.l2core.compat.patchouli.PatchouliHelper;
import dev.xkmc.l2core.init.L2TagGen;
import dev.xkmc.l2core.init.reg.simple.Reg;
import dev.xkmc.l2core.serial.config.ConfigTypeEntry;
import dev.xkmc.l2core.serial.config.PacketHandlerWithConfig;
import dev.xkmc.l2core.serial.config.RegistrateNestedProvider;
import dev.xkmc.l2damagetracker.contents.attack.AttackEventHandler;
import dev.xkmc.l2hostility.compat.gateway.GatewayConfigGen;
import dev.xkmc.l2hostility.compat.gateway.GatewayToEternityCompat;
import dev.xkmc.l2hostility.content.capability.chunk.ChunkCapSyncToClient;
import dev.xkmc.l2hostility.content.capability.mob.MobCapSyncToClient;
import dev.xkmc.l2hostility.content.config.EntityConfig;
import dev.xkmc.l2hostility.content.config.WeaponConfig;
import dev.xkmc.l2hostility.content.config.WorldDifficultyConfig;
import dev.xkmc.l2hostility.events.LHAttackListener;
import dev.xkmc.l2hostility.init.advancements.HostilityTriggers;
import dev.xkmc.l2hostility.init.data.AdvGen;
import dev.xkmc.l2hostility.init.data.LHConfig;
import dev.xkmc.l2hostility.init.data.LHConfigGen;
import dev.xkmc.l2hostility.init.data.LHDamageTypes;
import dev.xkmc.l2hostility.init.data.LHTagGen;
import dev.xkmc.l2hostility.init.data.LangData;
import dev.xkmc.l2hostility.init.data.RecipeGen;
import dev.xkmc.l2hostility.init.data.SlotGen;
import dev.xkmc.l2hostility.init.entries.LHRegistrate;
import dev.xkmc.l2hostility.init.loot.TraitGLMProvider;
import dev.xkmc.l2hostility.init.network.LootDataToClient;
import dev.xkmc.l2hostility.init.network.TraitEffectToClient;
import dev.xkmc.l2hostility.init.registrate.LHBlocks;
import dev.xkmc.l2hostility.init.registrate.LHEffects;
import dev.xkmc.l2hostility.init.registrate.LHEnchantments;
import dev.xkmc.l2hostility.init.registrate.LHEntities;
import dev.xkmc.l2hostility.init.registrate.LHItems;
import dev.xkmc.l2hostility.init.registrate.LHMiscs;
import dev.xkmc.l2hostility.init.registrate.LHTraits;
import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.network.PacketHandler.NetDir;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("l2hostility")
@EventBusSubscriber(modid = "l2hostility", bus = Bus.MOD)
public class L2Hostility {
   public static final String MODID = "l2hostility";
   public static final PacketHandlerWithConfig HANDLER = new PacketHandlerWithConfig(
      "l2hostility",
      2,
      new Function[]{
         e -> e.create(MobCapSyncToClient.class, NetDir.PLAY_TO_CLIENT),
         e -> e.create(TraitEffectToClient.class, NetDir.PLAY_TO_CLIENT),
         e -> e.create(LootDataToClient.class, NetDir.PLAY_TO_CLIENT),
         e -> e.create(ChunkCapSyncToClient.class, NetDir.PLAY_TO_CLIENT)
      }
   );
   public static final Logger LOGGER = LogManager.getLogger();
   public static final Reg REG = new Reg("l2hostility");
   public static final LHRegistrate REGISTRATE = new LHRegistrate("l2hostility");
   public static final PatchouliHelper PATCHOULI = new PatchouliHelper(REGISTRATE, "hostility_guide");
   public static final ConfigTypeEntry<WorldDifficultyConfig> DIFFICULTY = new ConfigTypeEntry(HANDLER, "difficulty", WorldDifficultyConfig.class);
   public static final ConfigTypeEntry<WeaponConfig> WEAPON = new ConfigTypeEntry(HANDLER, "weapon", WeaponConfig.class);
   public static final ConfigTypeEntry<EntityConfig> ENTITY = new ConfigTypeEntry(HANDLER, "entity", EntityConfig.class);

   public L2Hostility() {
      LHBlocks.register();
      LHItems.register();
      LHEffects.register();
      LHTraits.register();
      LHEntities.register();
      LHMiscs.register();
      LHConfig.init();
      LHEnchantments.register();
      TraitGLMProvider.register();
      HostilityTriggers.register();
      PATCHOULI.buildModel()
         .buildShapelessRecipe(e -> e.requires(Items.BOOK).requires(Items.ROTTEN_FLESH).requires(Items.BONE), () -> Items.BOOK)
         .buildBook("L2Hostility Guide", "Find out the mechanics and mob traits to know what to prepare for", 1, LHBlocks.TAB.key());
      AttackEventHandler.register(4500, new LHAttackListener());
      if (ModList.get().isLoaded("gateways")) {
         NeoForge.EVENT_BUS.register(GatewayToEternityCompat.class);
      }
   }

   @SubscribeEvent
   public static void modifyAttributes(EntityAttributeModificationEvent event) {
      event.add(EntityType.PLAYER, LHMiscs.ADD_LEVEL);
   }

   @SubscribeEvent
   public static void setup(FMLCommonSetupEvent event) {
      event.enqueueWork(() -> {});
   }

   @SubscribeEvent(priority = EventPriority.HIGH)
   public static void gatherData(GatherDataEvent event) {
      REGISTRATE.addDataGenerator(ProviderType.LANG, LangData::addTranslations);
      REGISTRATE.addDataGenerator(ProviderType.RECIPE, RecipeGen::genRecipe);
      REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, LHTagGen::onBlockTagGen);
      REGISTRATE.addDataGenerator(L2TagGen.ENCH_TAGS, LHTagGen::onEnchTagGen);
      REGISTRATE.addDataGenerator(L2TagGen.EFF_TAGS, LHTagGen::onEffTagGen);
      REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, LHTagGen::onItemTagGen);
      REGISTRATE.addDataGenerator(ProviderType.ENTITY_TAGS, LHTagGen::onEntityTagGen);
      REGISTRATE.addDataGenerator(LHTraits.TRAIT_TAGS, LHTagGen::onTraitTagGen);
      REGISTRATE.addDataGenerator(ProviderType.ADVANCEMENT, AdvGen::genAdvancements);
      REGISTRATE.addDataGenerator(RegistrateNestedProvider.TYPE, pvdx -> pvdx.add(LHConfigGen::new));
      boolean server = event.includeServer();
      PackOutput output = event.getGenerator().getPackOutput();
      CompletableFuture<Provider> pvd = event.getLookupProvider();
      ExistingFileHelper helper = event.getExistingFileHelper();
      DataGenerator gen = event.getGenerator();
      gen.addProvider(server, new TraitGLMProvider(output, pvd));
      gen.addProvider(server, new SlotGen("l2hostility", output, helper, pvd));
      DataProviderInitializer init = REGISTRATE.getDataGenInitializer();
      init.addDependency(ProviderType.RECIPE, ProviderType.DYNAMIC);
      init.addDependency(RegistrateNestedProvider.TYPE, ProviderType.DYNAMIC);
      LHEnchantments.DC.addParent(LCEnchantments.REG);
      new LHDamageTypes().generate();
      if (ModList.get().isLoaded("gateways")) {
         gen.addProvider(server, new GatewayConfigGen(gen));
      }
   }

   public static void toTrackingChunk(LevelChunk chunk, SerialPacketBase<?> packet) {
      if (chunk.getLevel() instanceof ServerLevel sl) {
         HANDLER.toTrackingChunk(sl, chunk.getPos(), packet);
      }
   }

   public static ResourceLocation loc(String id) {
      return ResourceLocation.fromNamespaceAndPath("l2hostility", id);
   }
}
