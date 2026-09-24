package io.github.manasmods.tensura.client;

import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.client.rendering.ColorHandlerRegistry;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.tensura.client.block.MagicEngineBlockEntityRenderer;
import io.github.manasmods.tensura.client.block.OrcDisasterHeadRenderer;
import io.github.manasmods.tensura.client.block.SpellbindingBlockEntityRenderer;
import io.github.manasmods.tensura.client.block.ToolRackBlockEntityRenderer;
import io.github.manasmods.tensura.client.entity.barrier.BarrierCubeRenderer;
import io.github.manasmods.tensura.client.entity.barrier.DisintegrationRenderer;
import io.github.manasmods.tensura.client.entity.barrier.FlareCircleRenderer;
import io.github.manasmods.tensura.client.entity.barrier.JailSphereRenderer;
import io.github.manasmods.tensura.client.entity.barrier.MegiddoBubbleRenderer;
import io.github.manasmods.tensura.client.entity.barrier.StormRenderer;
import io.github.manasmods.tensura.client.entity.beam.BeamProjectileRenderer;
import io.github.manasmods.tensura.client.entity.beam.BlackLightningBlastRenderer;
import io.github.manasmods.tensura.client.entity.beam.MagicBeamRenderer;
import io.github.manasmods.tensura.client.entity.circle.ExplosionCircleRenderer;
import io.github.manasmods.tensura.client.entity.circle.MagicCircleRenderer;
import io.github.manasmods.tensura.client.entity.field.DeathBlessingRenderer;
import io.github.manasmods.tensura.client.entity.field.HakiRenderer;
import io.github.manasmods.tensura.client.entity.field.HellFlareRenderer;
import io.github.manasmods.tensura.client.entity.field.HellfireRenderer;
import io.github.manasmods.tensura.client.entity.field.MagicExplosionRenderer;
import io.github.manasmods.tensura.client.entity.field.MagicHandsRenderer;
import io.github.manasmods.tensura.client.entity.field.MarionetteLinesRenderer;
import io.github.manasmods.tensura.client.entity.human.CloneRenderer;
import io.github.manasmods.tensura.client.entity.human.DwarfRenderer;
import io.github.manasmods.tensura.client.entity.human.FalmuthKnightRenderer;
import io.github.manasmods.tensura.client.entity.human.GazelDwargoRenderer;
import io.github.manasmods.tensura.client.entity.human.OtherworlderRenderer;
import io.github.manasmods.tensura.client.entity.human.PlayerLikeSkeletonModel;
import io.github.manasmods.tensura.client.entity.human.PlayerLikeSkeletonRenderer;
import io.github.manasmods.tensura.client.entity.human.SkeletonRenderer;
import io.github.manasmods.tensura.client.entity.human.TrainingDummyRenderer;
import io.github.manasmods.tensura.client.entity.human.ZombieRenderer;
import io.github.manasmods.tensura.client.entity.human.golem.BoneGolemRenderer;
import io.github.manasmods.tensura.client.entity.layer.DwarfLayer;
import io.github.manasmods.tensura.client.entity.layer.GoblinLayer;
import io.github.manasmods.tensura.client.entity.layer.ProfessionClothesLayer;
import io.github.manasmods.tensura.client.entity.layer.RoyalGuardArmorLayer;
import io.github.manasmods.tensura.client.entity.misc.BlackLightningRenderer;
import io.github.manasmods.tensura.client.entity.misc.ChaosEaterRenderer;
import io.github.manasmods.tensura.client.entity.misc.DeathTornadoRenderer;
import io.github.manasmods.tensura.client.entity.misc.GluttonyMistRenderer;
import io.github.manasmods.tensura.client.entity.misc.HazyBlossomRenderer;
import io.github.manasmods.tensura.client.entity.misc.LightningBoltRenderer;
import io.github.manasmods.tensura.client.entity.misc.MadOrbsRenderer;
import io.github.manasmods.tensura.client.entity.misc.MagicShieldRenderer;
import io.github.manasmods.tensura.client.entity.misc.NonPlayerFishingHookRenderer;
import io.github.manasmods.tensura.client.entity.misc.PillarRenderer;
import io.github.manasmods.tensura.client.entity.misc.PrimedCharybdisCoreRenderer;
import io.github.manasmods.tensura.client.entity.misc.TensuraBoatEntityRenderer;
import io.github.manasmods.tensura.client.entity.misc.WarpPortalRenderer;
import io.github.manasmods.tensura.client.entity.monster.AkashRenderer;
import io.github.manasmods.tensura.client.entity.monster.AquaFrogRenderer;
import io.github.manasmods.tensura.client.entity.monster.ArchDaemonRenderer;
import io.github.manasmods.tensura.client.entity.monster.ArmorsaurusRenderer;
import io.github.manasmods.tensura.client.entity.monster.ArmyWaspRenderer;
import io.github.manasmods.tensura.client.entity.monster.BarghestRenderer;
import io.github.manasmods.tensura.client.entity.monster.BasiliskRenderer;
import io.github.manasmods.tensura.client.entity.monster.BeastGnomeRenderer;
import io.github.manasmods.tensura.client.entity.monster.BlackSpiderRenderer;
import io.github.manasmods.tensura.client.entity.monster.BladeTigerRenderer;
import io.github.manasmods.tensura.client.entity.monster.CattledeerRenderer;
import io.github.manasmods.tensura.client.entity.monster.CharybdisRenderer;
import io.github.manasmods.tensura.client.entity.monster.DirewolfRenderer;
import io.github.manasmods.tensura.client.entity.monster.DragonPeacockRenderer;
import io.github.manasmods.tensura.client.entity.monster.ElementalColossusRenderer;
import io.github.manasmods.tensura.client.entity.monster.FeatheredSerpentRenderer;
import io.github.manasmods.tensura.client.entity.monster.GiantAntRenderer;
import io.github.manasmods.tensura.client.entity.monster.GiantBatRenderer;
import io.github.manasmods.tensura.client.entity.monster.GiantBearRenderer;
import io.github.manasmods.tensura.client.entity.monster.GiantCodRenderer;
import io.github.manasmods.tensura.client.entity.monster.GiantSalmonRenderer;
import io.github.manasmods.tensura.client.entity.monster.GoblinRenderer;
import io.github.manasmods.tensura.client.entity.monster.GreaterDaemonRenderer;
import io.github.manasmods.tensura.client.entity.monster.HellCaterpillarRenderer;
import io.github.manasmods.tensura.client.entity.monster.HellMothRenderer;
import io.github.manasmods.tensura.client.entity.monster.HornedBearRenderer;
import io.github.manasmods.tensura.client.entity.monster.HornedRabbitRenderer;
import io.github.manasmods.tensura.client.entity.monster.HoundDogRenderer;
import io.github.manasmods.tensura.client.entity.monster.HoverLizardRenderer;
import io.github.manasmods.tensura.client.entity.monster.IfritRenderer;
import io.github.manasmods.tensura.client.entity.monster.KnightSpiderRenderer;
import io.github.manasmods.tensura.client.entity.monster.LandfishRenderer;
import io.github.manasmods.tensura.client.entity.monster.LeechLizardRenderer;
import io.github.manasmods.tensura.client.entity.monster.LesserDaemonRenderer;
import io.github.manasmods.tensura.client.entity.monster.LizardmanRenderer;
import io.github.manasmods.tensura.client.entity.monster.MegalodonRenderer;
import io.github.manasmods.tensura.client.entity.monster.OneEyedOwlRenderer;
import io.github.manasmods.tensura.client.entity.monster.OrcDisasterRenderer;
import io.github.manasmods.tensura.client.entity.monster.OrcLordRenderer;
import io.github.manasmods.tensura.client.entity.monster.OrcRenderer;
import io.github.manasmods.tensura.client.entity.monster.PegasusRenderer;
import io.github.manasmods.tensura.client.entity.monster.PhantasporeRenderer;
import io.github.manasmods.tensura.client.entity.monster.SalamanderRenderer;
import io.github.manasmods.tensura.client.entity.monster.SissieRenderer;
import io.github.manasmods.tensura.client.entity.monster.SlimeRenderer;
import io.github.manasmods.tensura.client.entity.monster.SpearToroRenderer;
import io.github.manasmods.tensura.client.entity.monster.SylphideRenderer;
import io.github.manasmods.tensura.client.entity.monster.UndineRenderer;
import io.github.manasmods.tensura.client.entity.monster.UnicornRenderer;
import io.github.manasmods.tensura.client.entity.monster.WarGnomeRenderer;
import io.github.manasmods.tensura.client.entity.monster.WingedCatRenderer;
import io.github.manasmods.tensura.client.entity.multipart.EvilCentipedeBodyRenderer;
import io.github.manasmods.tensura.client.entity.multipart.EvilCentipedeRenderer;
import io.github.manasmods.tensura.client.entity.multipart.TempestSerpentBodyRenderer;
import io.github.manasmods.tensura.client.entity.multipart.TempestSerpentRenderer;
import io.github.manasmods.tensura.client.entity.projectile.InvisibleArrowRenderer;
import io.github.manasmods.tensura.client.entity.projectile.KunaiProjectileRenderer;
import io.github.manasmods.tensura.client.entity.projectile.MonsterSpitProjectileRenderer;
import io.github.manasmods.tensura.client.entity.projectile.SevererBladeProjectileRenderer;
import io.github.manasmods.tensura.client.entity.projectile.SpearProjectileRenderer;
import io.github.manasmods.tensura.client.entity.projectile.SpearedFinArrowRenderer;
import io.github.manasmods.tensura.client.entity.projectile.TempestScaleEntityRenderer;
import io.github.manasmods.tensura.client.entity.projectile.ThrownItemProjectileRenderer;
import io.github.manasmods.tensura.client.entity.projectile.UnicornHornProjectileRenderer;
import io.github.manasmods.tensura.client.entity.projectile.WebBulletProjectileRenderer;
import io.github.manasmods.tensura.client.entity.projectile.magic.AbsoluteSeveranceRenderer;
import io.github.manasmods.tensura.client.entity.projectile.magic.AuraBulletRenderer;
import io.github.manasmods.tensura.client.entity.projectile.magic.FlyingOrbRenderer;
import io.github.manasmods.tensura.client.entity.projectile.magic.ItemProjectileRenderer;
import io.github.manasmods.tensura.client.entity.projectile.magic.MagicArrowRenderer;
import io.github.manasmods.tensura.client.entity.projectile.magic.MagicBallRenderer;
import io.github.manasmods.tensura.client.entity.projectile.magic.MagicLanceRenderer;
import io.github.manasmods.tensura.client.entity.projectile.magic.MagicSlashRenderer;
import io.github.manasmods.tensura.client.entity.projectile.magic.MagicSphereRenderer;
import io.github.manasmods.tensura.client.entity.projectile.magic.MagicTornadoRenderer;
import io.github.manasmods.tensura.client.entity.projectile.magic.ShulkerBulletRenderer;
import io.github.manasmods.tensura.client.entity.spike.MagicSpikeRenderer;
import io.github.manasmods.tensura.client.layer.WingsModel;
import io.github.manasmods.tensura.client.screen.templates.SettingsOptions;
import io.github.manasmods.tensura.config.client.MiscClientConfig;
import io.github.manasmods.tensura.entity.magic.barrier.AntiMagicAreaEntity;
import io.github.manasmods.tensura.entity.magic.barrier.AntiShockAreaEntity;
import io.github.manasmods.tensura.entity.magic.barrier.DarkCubeEntity;
import io.github.manasmods.tensura.entity.magic.barrier.RangedBarrierEntity;
import io.github.manasmods.tensura.entity.template.TensuraBoatEntity;
import io.github.manasmods.tensura.handler.client.CommonClientHandler;
import io.github.manasmods.tensura.handler.client.OverlayHandler;
import io.github.manasmods.tensura.handler.client.PlayerInputHandler;
import io.github.manasmods.tensura.registry.block.TensuraBlockEntities;
import io.github.manasmods.tensura.registry.entity.HumanEntityTypes;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.registry.entity.MonsterEntityTypes;
import io.github.manasmods.tensura.registry.entity.ProjectileEntityTypes;
import io.github.manasmods.tensura.registry.item.TensuraArmorItems;
import io.github.manasmods.tensura.registry.item.TensuraToolItems;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class TensuraClient {
   public static MiscClientConfig CONFIG = (MiscClientConfig)ConfigRegistry.getConfig(MiscClientConfig.class);
   public static List<String> CUSTOM_GUI_MODEL_ITEMS = new ArrayList<>(
      List.of(
         "tensura:armorsaurus_gauntlet",
         "tensura:armorsaurus_shield",
         "tensura:dead_end_rainbow",
         "tensura:dragon_knuckle",
         "tensura:ice_blade",
         "tensura:meat_crusher",
         "tensura:moonlight",
         "tensura:ruhk",
         "tensura:tempest_scale_shield",
         "tensura:vortex_spear",
         "tensura:low_magic_staff",
         "tensura:medium_magic_staff",
         "tensura:high_magic_staff",
         "tensura:slime_staff",
         "tensura:grimoire_d",
         "tensura:grimoire_c",
         "tensura:grimoire_b",
         "tensura:grimoire_a",
         "tensura:grimoire_special_a"
      )
   );

   public static void init() {
      registerModelLayer();
      registerLivingEntityRenderer();
      registerEntityRenderer();
      TensuraKeybinds.init();
      PlayerInputHandler.init();
      CommonClientHandler.init();
      OverlayHandler.init();
      SettingsOptions.init();
      TensuraRenderTypes.init();
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft != null) {
         initClient();
      }
   }

   public static void initClient() {
      ColorHandlerRegistry.registerItemColors(
         (itemStack, i) -> i > 0 ? -1 : DyedItemColor.getOrDefault(itemStack, -1644826), new Supplier[]{TensuraArmorItems.MITHRIL_HELMET}
      );
   }

   public static void registerModelLayer() {
      EntityModelLayerRegistry.register(ProfessionClothesLayer.CLOTHES, () -> ProfessionClothesLayer.CLOTHES_LAYER);
      EntityModelLayerRegistry.register(ProfessionClothesLayer.Chest.CHEST, () -> ProfessionClothesLayer.Chest.CHEST_LAYER);
      EntityModelLayerRegistry.register(RoyalGuardArmorLayer.Armor.ARMORS, () -> RoyalGuardArmorLayer.ARMOR_LAYER);
      EntityModelLayerRegistry.register(RoyalGuardArmorLayer.Helmet.HELMET, () -> RoyalGuardArmorLayer.HELMET_LAYER);
      EntityModelLayerRegistry.register(RoyalGuardArmorLayer.Chest.CHEST, () -> RoyalGuardArmorLayer.CHEST_LAYER);
      EntityModelLayerRegistry.register(DwarfLayer.Face.FACE, () -> DwarfLayer.FACE_LAYER);
      EntityModelLayerRegistry.register(DwarfLayer.Hair.HAIR, () -> DwarfLayer.HAIR_HEAD_LAYER);
      EntityModelLayerRegistry.register(DwarfLayer.HairBody.HAIR_BODY, () -> DwarfLayer.HAIR_BODY_LAYER);
      EntityModelLayerRegistry.register(DwarfLayer.FacialHair.FACIAL_HAIR, () -> DwarfLayer.FACIAL_HAIR_LAYER);
      EntityModelLayerRegistry.register(DwarfLayer.Top.TOP, () -> DwarfLayer.TOP_LAYER);
      EntityModelLayerRegistry.register(DwarfLayer.Bottom.BOTTOM, () -> DwarfLayer.BOTTOM_LAYER);
      EntityModelLayerRegistry.register(DwarfLayer.Feet.FEET, () -> DwarfLayer.FEET_LAYER);
      EntityModelLayerRegistry.register(DwarfLayer.Chest.CHEST, () -> DwarfLayer.CHEST_LAYER);
      EntityModelLayerRegistry.register(GoblinLayer.Face.FACE, () -> GoblinLayer.FACE_LAYER);
      EntityModelLayerRegistry.register(GoblinLayer.Hair.HAIR, () -> GoblinLayer.HAIR_HEAD_LAYER);
      EntityModelLayerRegistry.register(GoblinLayer.HairBody.HAIR_BODY, () -> GoblinLayer.HAIR_BODY_LAYER);
      EntityModelLayerRegistry.register(GoblinLayer.Clothing.CLOTHING, () -> GoblinLayer.CLOTHING_LAYER);
      EntityModelLayerRegistry.register(GoblinLayer.Bandages.BANDAGES, () -> GoblinLayer.BANDAGES_LAYER);
      EntityModelLayerRegistry.register(GoblinLayer.Head.HEAD, () -> GoblinLayer.HEAD_LAYER);
      EntityModelLayerRegistry.register(GoblinLayer.Top.TOP, () -> GoblinLayer.TOP_LAYER);
      EntityModelLayerRegistry.register(GoblinLayer.Bottom.BOTTOM, () -> GoblinLayer.BOTTOM_LAYER);
      EntityModelLayerRegistry.register(WingsModel.WINGS_LAYER, WingsModel::createLayer);
      EntityModelLayerRegistry.register(MagicArrowRenderer.ARROW, MagicArrowRenderer::createBodyLayer);
      EntityModelLayerRegistry.register(PlayerLikeSkeletonRenderer.SKELETON, PlayerLikeSkeletonModel::createBodyLayer);
      EntityModelLayerRegistry.register(TensuraBoatEntityRenderer.createBoatModelName(TensuraBoatEntity.Type.PALM), BoatModel::createBodyModel);
      EntityModelLayerRegistry.register(TensuraBoatEntityRenderer.createChestBoatModelName(TensuraBoatEntity.Type.PALM), ChestBoatModel::createBodyModel);
   }

   public static void registerBlockEntityRenderers() {
      BlockEntityRenderers.register((BlockEntityType)TensuraBlockEntities.SIGN.get(), SignRenderer::new);
      BlockEntityRenderers.register((BlockEntityType)TensuraBlockEntities.HANGING_SIGN.get(), HangingSignRenderer::new);
      BlockEntityRenderers.register((BlockEntityType)TensuraBlockEntities.ORC_DISASTER_HEAD.get(), OrcDisasterHeadRenderer::new);
      BlockEntityRenderers.register((BlockEntityType)TensuraBlockEntities.MAGIC_ENGINE.get(), MagicEngineBlockEntityRenderer::new);
      BlockEntityRenderers.register((BlockEntityType)TensuraBlockEntities.TOOL_RACK.get(), ToolRackBlockEntityRenderer::new);
      BlockEntityRenderers.register((BlockEntityType)TensuraBlockEntities.SPELLBINDING.get(), SpellbindingBlockEntityRenderer::new);
   }

   public static void registerLivingEntityRenderer() {
      EntityRendererRegistry.register(HumanEntityTypes.BONE_GOLEM, BoneGolemRenderer::new);
      EntityRendererRegistry.register(HumanEntityTypes.CLONE, CloneRenderer::new);
      EntityRendererRegistry.register(HumanEntityTypes.TRAINING_DUMMY, TrainingDummyRenderer::new);
      EntityRendererRegistry.register(HumanEntityTypes.DWARF, DwarfRenderer::new);
      EntityRendererRegistry.register(HumanEntityTypes.GAZEL_DWARGO, GazelDwargoRenderer::new);
      EntityRendererRegistry.register(HumanEntityTypes.FALMUTH_KNIGHT, context -> new FalmuthKnightRenderer(context, true));
      EntityRendererRegistry.register(HumanEntityTypes.FOLGEN, context -> new OtherworlderRenderer(context, false));
      EntityRendererRegistry.register(HumanEntityTypes.HINATA_SAKAGUCHI, context -> new OtherworlderRenderer(context, true));
      EntityRendererRegistry.register(HumanEntityTypes.KIRARA_MIZUTANI, context -> new OtherworlderRenderer(context, true));
      EntityRendererRegistry.register(HumanEntityTypes.KYOYA_TACHIBANA, context -> new OtherworlderRenderer(context, true));
      EntityRendererRegistry.register(HumanEntityTypes.MAI_FURUKI, context -> new OtherworlderRenderer(context, true));
      EntityRendererRegistry.register(HumanEntityTypes.MARK_LAUREN, context -> new OtherworlderRenderer(context, false));
      EntityRendererRegistry.register(HumanEntityTypes.SHINJI_TANIMURA, context -> new OtherworlderRenderer(context, true));
      EntityRendererRegistry.register(HumanEntityTypes.SHIN_RYUSEI, context -> new OtherworlderRenderer(context, true));
      EntityRendererRegistry.register(HumanEntityTypes.SHIZU, context -> new OtherworlderRenderer(context, true));
      EntityRendererRegistry.register(HumanEntityTypes.SHOGO_TAGUCHI, context -> new OtherworlderRenderer(context, true));
      EntityRendererRegistry.register(HumanEntityTypes.SKELETON, SkeletonRenderer::new);
      EntityRendererRegistry.register(HumanEntityTypes.ZOMBIE, ZombieRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.AKASH, AkashRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.AQUA_FROG, AquaFrogRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.ARCH_DAEMON, ArchDaemonRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.ARMORSAURUS, ArmorsaurusRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.ARMY_WASP, ArmyWaspRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.BARGHEST, BarghestRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.BEAST_GNOME, BeastGnomeRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.BASILISK, BasiliskRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.BLACK_SPIDER, BlackSpiderRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.BLADE_TIGER, BladeTigerRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.CATTLEDEER, CattledeerRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.CHARYBDIS, CharybdisRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.DIREWOLF, DirewolfRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.DRAGON_PEACOCK, DragonPeacockRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.ELEMENTAL_COLOSSUS, ElementalColossusRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.FEATHERED_SERPENT, FeatheredSerpentRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.GIANT_ANT, GiantAntRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.GIANT_BAT, GiantBatRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.GIANT_BEAR, GiantBearRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.GIANT_COD, GiantCodRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.GIANT_SALMON, GiantSalmonRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.GOBLIN, GoblinRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.GREATER_DAEMON, GreaterDaemonRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.HELL_CATERPILLAR, HellCaterpillarRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.HELL_MOTH, HellMothRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.HORNED_BEAR, HornedBearRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.HORNED_RABBIT, HornedRabbitRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.HOUND_DOG, HoundDogRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.HOVER_LIZARD, HoverLizardRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.IFRIT, IfritRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.IFRIT_CLONE, IfritRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.KNIGHT_SPIDER, KnightSpiderRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.LANDFISH, LandfishRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.LEECH_LIZARD, LeechLizardRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.LESSER_DAEMON, LesserDaemonRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.LIZARDMAN, LizardmanRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.MEGALODON, MegalodonRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.ONE_EYED_OWL, OneEyedOwlRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.ORC, OrcRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.ORC_LORD, OrcLordRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.ORC_DISASTER, OrcDisasterRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.PEGASUS, PegasusRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.PEGACORN, PegasusRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.PHANTASPORE, PhantasporeRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.SALAMANDER, SalamanderRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.SISSIE, SissieRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.SLIME, SlimeRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.METAL_SLIME, SlimeRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.SUPERMASSIVE_SLIME, SlimeRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.SPEAR_TORO, SpearToroRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.SYLPHIDE, SylphideRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.UNDINE, UndineRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.UNICORN, UnicornRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.WAR_GNOME, WarGnomeRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.WINGED_CAT, WingedCatRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.EVIL_CENTIPEDE, EvilCentipedeRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.EVIL_CENTIPEDE_BODY, EvilCentipedeBodyRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.TEMPEST_SERPENT, TempestSerpentRenderer::new);
      EntityRendererRegistry.register(MonsterEntityTypes.TEMPEST_SERPENT_BODY, TempestSerpentBodyRenderer::new);
   }

   public static void registerEntityRenderer() {
      EntityRendererRegistry.register(MiscEntityTypes.BOAT_ENTITY, context -> new TensuraBoatEntityRenderer(context, false));
      EntityRendererRegistry.register(MiscEntityTypes.CHEST_BOAT_ENTITY, context -> new TensuraBoatEntityRenderer(context, true));
      EntityRendererRegistry.register(MiscEntityTypes.FISHING_HOOK, NonPlayerFishingHookRenderer::new);
      EntityRendererRegistry.register(MiscEntityTypes.MAGIC_CIRCLE, MagicCircleRenderer::new);
      EntityRendererRegistry.register(MiscEntityTypes.EXPLOSION_CIRCLE, ExplosionCircleRenderer::new);
      EntityRendererRegistry.register(MiscEntityTypes.ACID_RAIN, NoopRenderer::new);
      EntityRendererRegistry.register(
         MiscEntityTypes.ANTI_MAGIC_AREA,
         context -> new BarrierCubeRenderer(context, "textures/entity/barrier/anti_magic_area.png", AntiMagicAreaEntity.ANTI_MAGIC_AREA)
      );
      EntityRendererRegistry.register(
         MiscEntityTypes.ANTI_SHOCK_AREA,
         context -> new BarrierCubeRenderer(context, "textures/entity/barrier/anti_shock_area.png", AntiShockAreaEntity.ANTI_SHOCK_AREA)
      );
      EntityRendererRegistry.register(
         MiscEntityTypes.AIR_JAIL,
         context -> new JailSphereRenderer(context, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/misc/air_jail.png"))
      );
      EntityRendererRegistry.register(
         MiscEntityTypes.BLIZZARD, context -> new StormRenderer(context, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/misc/blizzard.png"))
      );
      EntityRendererRegistry.register(MiscEntityTypes.BOSS_BARRIER, NoopRenderer::new);
      EntityRendererRegistry.register(
         MiscEntityTypes.DARK_CUBE, context -> new BarrierCubeRenderer(context, "textures/entity/barrier/dark_cube_9.png", DarkCubeEntity.DARK_CUBE, 2)
      );
      EntityRendererRegistry.register(MiscEntityTypes.DISINTEGRATION, DisintegrationRenderer::new);
      EntityRendererRegistry.register(
         MiscEntityTypes.EARTH_STORM,
         context -> new StormRenderer(context, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/misc/earth_storm.png"))
      );
      EntityRendererRegistry.register(
         MiscEntityTypes.FIRE_JAIL,
         context -> new JailSphereRenderer(context, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/misc/fire_jail.png"))
      );
      EntityRendererRegistry.register(MiscEntityTypes.FLARE_CIRCLE, FlareCircleRenderer::new);
      EntityRendererRegistry.register(MiscEntityTypes.HEALING_RAIN, NoopRenderer::new);
      EntityRendererRegistry.register(
         MiscEntityTypes.HEAT_STORM,
         context -> new StormRenderer(context, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/misc/heat_storm.png"))
      );
      EntityRendererRegistry.register(MiscEntityTypes.HOLY_FIELD, NoopRenderer::new);
      EntityRendererRegistry.register(MiscEntityTypes.MEGIDDO_BUBBLE, MegiddoBubbleRenderer::new);
      EntityRendererRegistry.register(
         MiscEntityTypes.RANGED_BARRIER,
         context -> new BarrierCubeRenderer(context, "textures/entity/barrier/ranged_barrier_start_9.png", RangedBarrierEntity.RANGED_BARRIER, 2)
      );
      EntityRendererRegistry.register(MiscEntityTypes.THUNDER_RAIN, NoopRenderer::new);
      EntityRendererRegistry.register(
         MiscEntityTypes.WATER_JAIL,
         context -> new JailSphereRenderer(context, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/misc/water_jail.png"))
      );
      EntityRendererRegistry.register(MiscEntityTypes.BLACK_LIGHTNING_BLAST, BlackLightningBlastRenderer::new);
      EntityRendererRegistry.register(MiscEntityTypes.BLOOD_RAY, BeamProjectileRenderer::new);
      EntityRendererRegistry.register(MiscEntityTypes.DARKNESS_CANNON, BeamProjectileRenderer::new);
      EntityRendererRegistry.register(MiscEntityTypes.ELECTRO_BLAST, BeamProjectileRenderer::new);
      EntityRendererRegistry.register(MiscEntityTypes.SOLAR_BEAM, BeamProjectileRenderer::new);
      EntityRendererRegistry.register(MiscEntityTypes.SPATIAL_RAY, BeamProjectileRenderer::new);
      EntityRendererRegistry.register(
         MiscEntityTypes.PREDATOR_MIST,
         context -> new GluttonyMistRenderer(context, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/misc/predator_mist.png"))
      );
      EntityRendererRegistry.register(
         MiscEntityTypes.GOURMET_MIST,
         context -> new GluttonyMistRenderer(context, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/misc/gourmet_mist.png"))
      );
      EntityRendererRegistry.register(MiscEntityTypes.GLUTTONY_MIST, GluttonyMistRenderer::new);
      EntityRendererRegistry.register(MiscEntityTypes.BLACK_FLAME_BREATH, NoopRenderer::new);
      EntityRendererRegistry.register(MiscEntityTypes.FLAME_BREATH, NoopRenderer::new);
      EntityRendererRegistry.register(MiscEntityTypes.ICE_BREATH, NoopRenderer::new);
      EntityRendererRegistry.register(MiscEntityTypes.PARALYSING_BREATH, NoopRenderer::new);
      EntityRendererRegistry.register(MiscEntityTypes.POISONOUS_BREATH, NoopRenderer::new);
      EntityRendererRegistry.register(MiscEntityTypes.THUNDER_BREATH, NoopRenderer::new);
      EntityRendererRegistry.register(MiscEntityTypes.WATER_BREATH, NoopRenderer::new);
      EntityRendererRegistry.register(MiscEntityTypes.WIND_BREATH, NoopRenderer::new);
      EntityRendererRegistry.register(
         MiscEntityTypes.CURSE_BIND_HANDS,
         context -> new MagicHandsRenderer(context, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/misc/curse_bind.png"))
      );
      EntityRendererRegistry.register(MiscEntityTypes.DEATH_BLESSING, DeathBlessingRenderer::new);
      EntityRendererRegistry.register(MiscEntityTypes.BLOOD_MIST, NoopRenderer::new);
      EntityRendererRegistry.register(MiscEntityTypes.GRAVITY_FIELD, NoopRenderer::new);
      EntityRendererRegistry.register(MiscEntityTypes.FIRE_STORM, NoopRenderer::new);
      EntityRendererRegistry.register(MiscEntityTypes.HAKI_FIELD, HakiRenderer::new);
      EntityRendererRegistry.register(MiscEntityTypes.SACRED_HAKI_FIELD, HakiRenderer::new);
      EntityRendererRegistry.register(MiscEntityTypes.HELLFIRE, HellfireRenderer::new);
      EntityRendererRegistry.register(MiscEntityTypes.HELL_FLARE, HellFlareRenderer::new);
      EntityRendererRegistry.register(MiscEntityTypes.HELL_FLARE_LIMITED, HellFlareRenderer::new);
      EntityRendererRegistry.register(MiscEntityTypes.MAGIC_EXPLOSION, MagicExplosionRenderer::new);
      EntityRendererRegistry.register(MiscEntityTypes.MIASMIC_MIST, NoopRenderer::new);
      EntityRendererRegistry.register(
         MiscEntityTypes.MARIONETTE_LINES,
         context -> new MarionetteLinesRenderer(context, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/misc/demon_marionette_lines.png"))
      );
      EntityRendererRegistry.register(
         MiscEntityTypes.MUD_HANDS,
         context -> new MagicHandsRenderer(context, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/misc/mud_hands.png"))
      );
      EntityRendererRegistry.register(
         MiscEntityTypes.SHADOW_BIND_HANDS,
         context -> new MagicHandsRenderer(context, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/misc/shadow_bind.png"))
      );
      EntityRendererRegistry.register(MiscEntityTypes.SLEEP_MIST, NoopRenderer::new);
      EntityRendererRegistry.register(MiscEntityTypes.LIGHTNING_BOLT, LightningBoltRenderer::new);
      EntityRendererRegistry.register(MiscEntityTypes.BLACK_LIGHTNING_BOLT, BlackLightningRenderer::new);
      EntityRendererRegistry.register(MiscEntityTypes.DEATH_TORNADO, DeathTornadoRenderer::new);
      EntityRendererRegistry.register(MiscEntityTypes.EARTH_SPIKE, MagicSpikeRenderer::new);
      EntityRendererRegistry.register(MiscEntityTypes.ICICLE_SPIKE, MagicSpikeRenderer::new);
      EntityRendererRegistry.register(MiscEntityTypes.MUD_SPIKE, MagicSpikeRenderer::new);
      EntityRendererRegistry.register(MiscEntityTypes.EARTH_PILLAR, PillarRenderer::new);
      EntityRendererRegistry.register(MiscEntityTypes.FIRE_PILLAR, NoopRenderer::new);
      EntityRendererRegistry.register(MiscEntityTypes.ICE_PILLAR, PillarRenderer::new);
      EntityRendererRegistry.register(
         MiscEntityTypes.AURA_SHIELD,
         context -> new MagicShieldRenderer(context, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/misc/shield/aura_shield.png"))
      );
      EntityRendererRegistry.register(
         MiscEntityTypes.MAGIC_SHIELD,
         context -> new MagicShieldRenderer(context, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/misc/shield/magic_shield.png"))
      );
      EntityRendererRegistry.register(MiscEntityTypes.CHARYBDIS_CORE, PrimedCharybdisCoreRenderer::new);
      EntityRendererRegistry.register(MiscEntityTypes.FALLING_BLOCK, FallingBlockRenderer::new);
      EntityRendererRegistry.register(MiscEntityTypes.HAZY_BLOSSOM, HazyBlossomRenderer::new);
      EntityRendererRegistry.register(MiscEntityTypes.LANDMINE, NoopRenderer::new);
      EntityRendererRegistry.register(MiscEntityTypes.MAD_ORBS, MadOrbsRenderer::new);
      EntityRendererRegistry.register(
         MiscEntityTypes.SUMMONING_BEAM,
         context -> new MagicBeamRenderer(context, ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/misc/beam/summon_daemon.png"))
      );
      EntityRendererRegistry.register(MiscEntityTypes.WARP_PORTAL, WarpPortalRenderer::new);
      EntityRendererRegistry.register(
         ProjectileEntityTypes.BULLET, context -> new ItemProjectileRenderer(context, (Item)TensuraToolItems.COPPER_SHELL.get()).setXRotation(90.0F)
      );
      EntityRendererRegistry.register(ProjectileEntityTypes.CHAOS_EATER, ChaosEaterRenderer::new);
      EntityRendererRegistry.register(ProjectileEntityTypes.FUSIONIST_PROJECTILE, context -> new ItemProjectileRenderer(context, Items.STONE));
      EntityRendererRegistry.register(ProjectileEntityTypes.SNIPER_GRENADE, context -> new ItemProjectileRenderer(context, Items.TNT));
      EntityRendererRegistry.register(ProjectileEntityTypes.TEMPEST_SCALE, TempestScaleEntityRenderer::new);
      EntityRendererRegistry.register(ProjectileEntityTypes.AURA_SLASH, MagicSlashRenderer::new);
      EntityRendererRegistry.register(ProjectileEntityTypes.ACID_BALL, MagicBallRenderer::new);
      EntityRendererRegistry.register(ProjectileEntityTypes.AURA_BULLET, AuraBulletRenderer::new);
      EntityRendererRegistry.register(ProjectileEntityTypes.BLACK_FLAME_BALL, MagicBallRenderer::new);
      EntityRendererRegistry.register(ProjectileEntityTypes.BOG_SHOT, context -> new ItemProjectileRenderer(context, Items.MUDDY_MANGROVE_ROOTS));
      EntityRendererRegistry.register(ProjectileEntityTypes.BOULDER_SHOT, MagicSphereRenderer::new);
      EntityRendererRegistry.register(ProjectileEntityTypes.DIMENSION_CUT, context -> new MagicSlashRenderer(context, true));
      EntityRendererRegistry.register(ProjectileEntityTypes.FIRE_BALL, MagicBallRenderer::new);
      EntityRendererRegistry.register(ProjectileEntityTypes.FIRE_BOLT, MagicBallRenderer::new);
      EntityRendererRegistry.register(ProjectileEntityTypes.FIRE_LANCE, MagicLanceRenderer::new);
      EntityRendererRegistry.register(ProjectileEntityTypes.FLAME_ORB, FlyingOrbRenderer::new);
      EntityRendererRegistry.register(ProjectileEntityTypes.FLAME_SPHERE, MagicSphereRenderer::new);
      EntityRendererRegistry.register(ProjectileEntityTypes.FLOAT_SPHERE, ShulkerBulletRenderer::new);
      EntityRendererRegistry.register(ProjectileEntityTypes.FROST_BALL, context -> new ItemProjectileRenderer(context, Items.SNOW_BLOCK));
      EntityRendererRegistry.register(ProjectileEntityTypes.GRAVITY_SPHERE, MagicSphereRenderer::new);
      EntityRendererRegistry.register(ProjectileEntityTypes.HEAT_SPHERE, context -> new MagicSphereRenderer(context).setColor(15962931));
      EntityRendererRegistry.register(ProjectileEntityTypes.HELL_FLARE_PROJECTILE, MagicSphereRenderer::new);
      EntityRendererRegistry.register(ProjectileEntityTypes.ICE_LANCE, MagicLanceRenderer::new);
      EntityRendererRegistry.register(ProjectileEntityTypes.INVISIBLE_FIRE_BOLT, MagicBallRenderer::new);
      EntityRendererRegistry.register(ProjectileEntityTypes.LIGHT_ARROW, MagicArrowRenderer::new);
      EntityRendererRegistry.register(ProjectileEntityTypes.LIGHTNING_LANCE, MagicLanceRenderer::new);
      EntityRendererRegistry.register(ProjectileEntityTypes.LIGHTNING_SPHERE, MagicSphereRenderer::new);
      EntityRendererRegistry.register(ProjectileEntityTypes.MAGMA_SHOT, context -> new ItemProjectileRenderer(context, Items.MAGMA_BLOCK));
      EntityRendererRegistry.register(ProjectileEntityTypes.MUD_SHOT, MagicBallRenderer::new);
      EntityRendererRegistry.register(ProjectileEntityTypes.OBSIDIAN_SHOT, MagicLanceRenderer::new);
      EntityRendererRegistry.register(ProjectileEntityTypes.PLASMA_BALL, MagicBallRenderer::new);
      EntityRendererRegistry.register(ProjectileEntityTypes.POISON_BALL, MagicBallRenderer::new);
      EntityRendererRegistry.register(ProjectileEntityTypes.POISON_CUTTER, MagicSlashRenderer::new);
      EntityRendererRegistry.register(ProjectileEntityTypes.REFLECTOR_ECHO, MagicSphereRenderer::new);
      EntityRendererRegistry.register(ProjectileEntityTypes.SEVERANCE_CUTTER, AbsoluteSeveranceRenderer::new);
      EntityRendererRegistry.register(ProjectileEntityTypes.SOLAR_GRENADE, MagicSphereRenderer::new);
      EntityRendererRegistry.register(ProjectileEntityTypes.SPACE_CUT, MagicSlashRenderer::new);
      EntityRendererRegistry.register(ProjectileEntityTypes.SPATIAL_ARROW, MagicArrowRenderer::new);
      EntityRendererRegistry.register(ProjectileEntityTypes.STEAM_BALL, MagicSphereRenderer::new);
      EntityRendererRegistry.register(ProjectileEntityTypes.STONE_SHOT, MagicLanceRenderer::new);
      EntityRendererRegistry.register(ProjectileEntityTypes.THUNDER_LANCE, MagicLanceRenderer::new);
      EntityRendererRegistry.register(ProjectileEntityTypes.THUNDER_SPHERE, MagicSphereRenderer::new);
      EntityRendererRegistry.register(ProjectileEntityTypes.WATER_BALL, MagicBallRenderer::new);
      EntityRendererRegistry.register(ProjectileEntityTypes.WATER_BLADE, MagicSlashRenderer::new);
      EntityRendererRegistry.register(ProjectileEntityTypes.WIND_BLADE, MagicSlashRenderer::new);
      EntityRendererRegistry.register(ProjectileEntityTypes.WIND_SPHERE, MagicSphereRenderer::new);
      EntityRendererRegistry.register(ProjectileEntityTypes.WIND_TORNADO, MagicTornadoRenderer::new);
      EntityRendererRegistry.register(ProjectileEntityTypes.HEALING_POTION, ThrownItemRenderer::new);
      EntityRendererRegistry.register(ProjectileEntityTypes.HOLY_WATER, ThrownItemRenderer::new);
      EntityRendererRegistry.register(ProjectileEntityTypes.KUNAI, KunaiProjectileRenderer::new);
      EntityRendererRegistry.register(ProjectileEntityTypes.MONSTER_SPIT, MonsterSpitProjectileRenderer::new);
      EntityRendererRegistry.register(ProjectileEntityTypes.SEVERER_BLADE, SevererBladeProjectileRenderer::new);
      EntityRendererRegistry.register(ProjectileEntityTypes.SPEAR, SpearProjectileRenderer::new);
      EntityRendererRegistry.register(ProjectileEntityTypes.INVISIBLE_ARROW, InvisibleArrowRenderer::new);
      EntityRendererRegistry.register(ProjectileEntityTypes.SPEARED_FIN_ARROW, SpearedFinArrowRenderer::new);
      EntityRendererRegistry.register(ProjectileEntityTypes.UNICORN_HORN, UnicornHornProjectileRenderer::new);
      EntityRendererRegistry.register(ProjectileEntityTypes.THROWN_ITEM, ThrownItemProjectileRenderer::new);
      EntityRendererRegistry.register(ProjectileEntityTypes.WEB_BULLET, WebBulletProjectileRenderer::new);
   }
}
