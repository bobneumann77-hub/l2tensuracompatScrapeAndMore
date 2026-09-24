package io.github.manasmods.tensura.registry.entity;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.manasmods.tensura.entity.human.CloneEntity;
import io.github.manasmods.tensura.entity.human.DwarfEntity;
import io.github.manasmods.tensura.entity.human.FalmuthKnightEntity;
import io.github.manasmods.tensura.entity.human.FolgenEntity;
import io.github.manasmods.tensura.entity.human.GazelDwargoEntity;
import io.github.manasmods.tensura.entity.human.HinataSakaguchiEntity;
import io.github.manasmods.tensura.entity.human.KiraraMizutaniEntity;
import io.github.manasmods.tensura.entity.human.KyoyaTachinbanaEntity;
import io.github.manasmods.tensura.entity.human.MaiFurukiEntity;
import io.github.manasmods.tensura.entity.human.MarkLaurenEntity;
import io.github.manasmods.tensura.entity.human.ShinRyuseiEntity;
import io.github.manasmods.tensura.entity.human.ShinjiTanimuraEntity;
import io.github.manasmods.tensura.entity.human.ShizuEntity;
import io.github.manasmods.tensura.entity.human.ShogoTaguchiEntity;
import io.github.manasmods.tensura.entity.human.golem.BoneGolemEntity;
import io.github.manasmods.tensura.entity.human.golem.TrainingDummyEntity;
import io.github.manasmods.tensura.entity.human.undead.SkeletonHumanoidEntity;
import io.github.manasmods.tensura.entity.human.undead.ZombieHumanoidEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;

public class HumanEntityTypes {
   private static final DeferredRegister<EntityType<?>> HUMAN = DeferredRegister.create("tensura", Registries.ENTITY_TYPE);
   public static final RegistrySupplier<EntityType<BoneGolemEntity>> BONE_GOLEM = HUMAN.register(
      "bone_golem",
      () -> Builder.of(BoneGolemEntity::new, MobCategory.MONSTER)
         .sized(0.6F, 2.0F)
         .eyeHeight(1.62F)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "bone_golem").toString())
   );
   public static final RegistrySupplier<EntityType<CloneEntity>> CLONE = HUMAN.register(
      "clone",
      () -> Builder.of(CloneEntity::new, MobCategory.MONSTER)
         .sized(0.6F, 1.8F)
         .eyeHeight(1.62F)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "clone").toString())
   );
   public static final RegistrySupplier<EntityType<TrainingDummyEntity>> TRAINING_DUMMY = HUMAN.register(
      "training_dummy",
      () -> Builder.of(TrainingDummyEntity::new, MobCategory.MONSTER)
         .sized(0.6F, 1.8F)
         .eyeHeight(1.62F)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "training_dummy").toString())
   );
   public static final RegistrySupplier<EntityType<DwarfEntity>> DWARF = HUMAN.register(
      "dwarf",
      () -> Builder.of(DwarfEntity::new, MobCategory.CREATURE)
         .sized(0.6F, 1.8F)
         .eyeHeight(1.305F)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "dwarf").toString())
   );
   public static final RegistrySupplier<EntityType<GazelDwargoEntity>> GAZEL_DWARGO = HUMAN.register(
      "gazel_dwargo",
      () -> Builder.of(GazelDwargoEntity::new, MobCategory.CREATURE)
         .sized(0.6F, 2.0F)
         .eyeHeight(1.305F)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "gazel_dwargo").toString())
   );
   public static final RegistrySupplier<EntityType<FalmuthKnightEntity>> FALMUTH_KNIGHT = HUMAN.register(
      "falmuth_knight",
      () -> Builder.of(FalmuthKnightEntity::new, MobCategory.MONSTER)
         .sized(0.6F, 1.8F)
         .eyeHeight(1.62F)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "falmuth_knight").toString())
   );
   public static final RegistrySupplier<EntityType<FolgenEntity>> FOLGEN = HUMAN.register(
      "folgen",
      () -> Builder.of(FolgenEntity::new, MobCategory.MONSTER)
         .sized(0.6F, 1.8F)
         .eyeHeight(1.62F)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "folgen").toString())
   );
   public static final RegistrySupplier<EntityType<HinataSakaguchiEntity>> HINATA_SAKAGUCHI = HUMAN.register(
      "hinata_sakaguchi",
      () -> Builder.of(HinataSakaguchiEntity::new, MobCategory.MONSTER)
         .sized(0.6F, 1.8F)
         .eyeHeight(1.62F)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "hinata_sakaguchi").toString())
   );
   public static final RegistrySupplier<EntityType<KiraraMizutaniEntity>> KIRARA_MIZUTANI = HUMAN.register(
      "kirara_mizutani",
      () -> Builder.of(KiraraMizutaniEntity::new, MobCategory.MONSTER)
         .sized(0.6F, 1.8F)
         .eyeHeight(1.62F)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "kirara_mizutani").toString())
   );
   public static final RegistrySupplier<EntityType<KyoyaTachinbanaEntity>> KYOYA_TACHIBANA = HUMAN.register(
      "kyoya_tachibana",
      () -> Builder.of(KyoyaTachinbanaEntity::new, MobCategory.MONSTER)
         .sized(0.6F, 1.8F)
         .eyeHeight(1.62F)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "kyoya_tachibana").toString())
   );
   public static final RegistrySupplier<EntityType<MaiFurukiEntity>> MAI_FURUKI = HUMAN.register(
      "mai_furuki",
      () -> Builder.of(MaiFurukiEntity::new, MobCategory.MONSTER)
         .sized(0.6F, 1.8F)
         .eyeHeight(1.62F)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "mai_furuki").toString())
   );
   public static final RegistrySupplier<EntityType<MarkLaurenEntity>> MARK_LAUREN = HUMAN.register(
      "mark_lauren",
      () -> Builder.of(MarkLaurenEntity::new, MobCategory.MONSTER)
         .sized(0.6F, 1.8F)
         .eyeHeight(1.62F)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "mark_lauren").toString())
   );
   public static final RegistrySupplier<EntityType<ShinjiTanimuraEntity>> SHINJI_TANIMURA = HUMAN.register(
      "shinji_tanimura",
      () -> Builder.of(ShinjiTanimuraEntity::new, MobCategory.MONSTER)
         .sized(0.6F, 1.8F)
         .eyeHeight(1.62F)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "shinji_tanimura").toString())
   );
   public static final RegistrySupplier<EntityType<ShinRyuseiEntity>> SHIN_RYUSEI = HUMAN.register(
      "shin_ryusei",
      () -> Builder.of(ShinRyuseiEntity::new, MobCategory.MONSTER)
         .sized(0.6F, 1.8F)
         .eyeHeight(1.62F)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "shin_ryusei").toString())
   );
   public static final RegistrySupplier<EntityType<ShizuEntity>> SHIZU = HUMAN.register(
      "shizu",
      () -> Builder.of(ShizuEntity::new, MobCategory.MONSTER)
         .sized(0.6F, 1.8F)
         .eyeHeight(1.62F)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "shizu").toString())
   );
   public static final RegistrySupplier<EntityType<ShogoTaguchiEntity>> SHOGO_TAGUCHI = HUMAN.register(
      "shogo_taguchi",
      () -> Builder.of(ShogoTaguchiEntity::new, MobCategory.MONSTER)
         .sized(0.6F, 1.8F)
         .eyeHeight(1.62F)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "shogo_taguchi").toString())
   );
   public static final RegistrySupplier<EntityType<SkeletonHumanoidEntity>> SKELETON = HUMAN.register(
      "skeleton",
      () -> Builder.of(SkeletonHumanoidEntity::new, MobCategory.MONSTER)
         .sized(0.6F, 1.8F)
         .eyeHeight(1.62F)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "skeleton").toString())
   );
   public static final RegistrySupplier<EntityType<ZombieHumanoidEntity>> ZOMBIE = HUMAN.register(
      "zombie",
      () -> Builder.of(ZombieHumanoidEntity::new, MobCategory.MONSTER)
         .sized(0.6F, 1.8F)
         .eyeHeight(1.62F)
         .build(ResourceLocation.fromNamespaceAndPath("tensura", "zombie").toString())
   );

   public static void init() {
      HUMAN.register();
   }
}
