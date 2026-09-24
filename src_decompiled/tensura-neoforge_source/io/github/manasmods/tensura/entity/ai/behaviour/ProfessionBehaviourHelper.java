package io.github.manasmods.tensura.entity.ai.behaviour;

import io.github.manasmods.tensura.data.TensuraTags;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.RetaliateOrTarget;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.FindNearestPosition;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.HumanoidConsumeItem;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.InteractDoor;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.InteractWithEntity;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.ValidateNearbyPoi;
import io.github.manasmods.tensura.entity.ai.behaviour.misc.VillagerLikeBreed;
import io.github.manasmods.tensura.entity.ai.behaviour.path.MerchantFollowTrader;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SetRandomWalkTargetAroundCenter;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SetWalkTargetToPotentialJobSite;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SetWalkTargetToWantedItem;
import io.github.manasmods.tensura.entity.ai.behaviour.path.SubordinateFollowOwner;
import io.github.manasmods.tensura.entity.ai.behaviour.profession.AcquirePoi;
import io.github.manasmods.tensura.entity.ai.behaviour.profession.AssignProfession;
import io.github.manasmods.tensura.entity.ai.behaviour.profession.ChopTree;
import io.github.manasmods.tensura.entity.ai.behaviour.profession.DepositPickedItems;
import io.github.manasmods.tensura.entity.ai.behaviour.profession.FarmAnimal;
import io.github.manasmods.tensura.entity.ai.behaviour.profession.FishAtWater;
import io.github.manasmods.tensura.entity.ai.behaviour.profession.ReplantSapling;
import io.github.manasmods.tensura.entity.ai.behaviour.profession.TradeWithMerchants;
import io.github.manasmods.tensura.entity.ai.behaviour.profession.UseFarmland;
import io.github.manasmods.tensura.entity.ai.behaviour.profession.WorkAtPoi;
import io.github.manasmods.tensura.entity.ai.behaviour.profession.YieldJobSite;
import io.github.manasmods.tensura.entity.template.PlayerLikeEntity;
import io.github.manasmods.tensura.entity.template.TensuraMerchantEntity;
import io.github.manasmods.tensura.world.TensuraGameRules;
import java.util.function.BiFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.behaviour.AllApplicableBehaviours;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.FollowParent;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.util.BrainUtils;

public class ProfessionBehaviourHelper {
   public static Vec3 getJobSitePos(LivingEntity entity) {
      GlobalPos pos = (GlobalPos)BrainUtils.getMemory(entity, MemoryModuleType.JOB_SITE);
      return pos == null ? entity.position() : Vec3.atCenterOf(pos.pos());
   }

   public static boolean isJobSiteAcquirable(Level level, BlockPos pos) {
      BlockState state = level.getBlockState(pos);
      return state.is(Blocks.BARREL) && level.getBlockEntity(pos) instanceof RandomizableContainerBlockEntity blockEntity
         ? blockEntity.getLootTable() == null && blockEntity.isEmpty()
         : !state.is(Blocks.LAVA_CAULDRON) && !state.is(Blocks.WATER_CAULDRON) && !state.is(Blocks.POWDER_SNOW_CAULDRON);
   }

   public static <E extends TensuraMerchantEntity & SmartBrainOwner<E>> BrainActivityGroup<E> getWorkingActivityGroup(E living) {
      BiFunction<E, Holder<PoiType>, Boolean> predicate = (entity, holder) -> entity.getProfession() == VillagerProfession.NONE
         ? holder.is(TensuraTags.PoiTypes.NPC_JOB_SITE)
         : entity.getProfession().acquirableJobSite().test(holder);
      return new BrainActivityGroup(Activity.WORK)
         .priority(10)
         .behaviours(
            new Behavior[]{
               new InteractDoor(),
               new HumanoidConsumeItem().startCondition(PlayerLikeEntity::shouldHeal).stopIf(entity -> !entity.shouldHeal()),
               getJobSiteFindBehaviours(predicate),
               getMerchantJobBehaviours(living, predicate),
               getMerchantIdleBehaviours(living),
               getMerchantRandomBehaviours(living)
            }
         );
   }

   public static <E extends TensuraMerchantEntity & SmartBrainOwner<E>> ExtendedBehaviour<E> getJobSiteFindBehaviours(
      BiFunction<E, Holder<PoiType>, Boolean> predicate
   ) {
      return new AllApplicableBehaviours(
            new ExtendedBehaviour[]{
               new AcquirePoi()
                  .predicate(predicate)
                  .skipBabies(e -> true)
                  .additionalPredicate((e, pos) -> isJobSiteAcquirable(e.level(), pos))
                  .writeTo(MemoryModuleType.POTENTIAL_JOB_SITE)
                  .successEvent((byte)14)
                  .cooldownFor(e -> 20),
               new ValidateNearbyPoi(predicate, MemoryModuleType.POTENTIAL_JOB_SITE),
               new SetWalkTargetToPotentialJobSite().speedMod(v -> 1.0F).closeEnoughWhen((v, pos) -> 1).cooldownFor(e -> 20),
               new YieldJobSite().speedMod(v -> 1.0F).closeEnoughWhen((v, pos) -> 1).cooldownFor(e -> 20),
               new AssignProfession().distance(v -> 2.0).event((byte)14).cooldownFor(e -> 20)
            }
         )
         .startCondition(entity -> !BrainUtils.hasMemory(entity, MemoryModuleType.JOB_SITE));
   }

   public static <E extends TensuraMerchantEntity & SmartBrainOwner<E>> ExtendedBehaviour<E> getMerchantJobBehaviours(
      E living, BiFunction<E, Holder<PoiType>, Boolean> predicate
   ) {
      return new AllApplicableBehaviours(
            new ExtendedBehaviour[]{
               new ValidateNearbyPoi(predicate, MemoryModuleType.JOB_SITE),
               new AllApplicableBehaviours(
                     new ExtendedBehaviour[]{
                        new SetWalkTargetToWantedItem().startCondition(rec$ -> rec$.shouldAlwaysPickUpItem()),
                        new FirstApplicableBehaviour(
                              new ExtendedBehaviour[]{
                                 getMerchantButcherBehaviours(),
                                 getMerchantFarmerBehaviours(),
                                 getMerchantLumberjackBehaviours(),
                                 getMerchantFishingBehaviours(),
                                 getMerchantShepherdBehaviours(),
                                 getBasicGuardBehaviours(living)
                              }
                           )
                           .startCondition(entity -> !entity.shouldRestock() && BrainUtils.hasMemory(entity, MemoryModuleType.JOB_SITE))
                     }
                  )
                  .startCondition(entity -> entity.level().getGameRules().getBoolean(TensuraGameRules.NPC_WORKING)),
               new WorkAtPoi()
            }
         )
         .startCondition(entity -> entity.getProfession() != VillagerProfession.NONE);
   }

   public static <E extends PlayerLikeEntity & SmartBrainOwner<E>> ExtendedBehaviour<E> getBasicJobBehaviours(E living) {
      return new AllApplicableBehaviours(
            new ExtendedBehaviour[]{
               new SetWalkTargetToWantedItem().startCondition(rec$ -> rec$.shouldAlwaysPickUpItem()),
               new FirstApplicableBehaviour(
                  new ExtendedBehaviour[]{
                     getBasicButcherBehaviours(),
                     getBasicFarmerBehaviours(),
                     getBasicLumberjackBehaviours(),
                     getBasicFishingBehaviours(),
                     getBasicShepherdBehaviours(),
                     getBasicGuardBehaviours(living)
                  }
               ),
               getDepositItemsBehaviours(),
               TensuraBehaviourHelper.getMoveToWanderFarmingPos()
            }
         )
         .startCondition(entity -> entity.isWandering() && entity.level().getGameRules().getBoolean(TensuraGameRules.NPC_WORKING));
   }

   public static <E extends TensuraMerchantEntity & SmartBrainOwner<E>> ExtendedBehaviour<E> getMerchantButcherBehaviours() {
      return new FarmAnimal().cooldownFor(e -> 40);
   }

   public static <E extends PlayerLikeEntity & SmartBrainOwner<E>> ExtendedBehaviour<E> getBasicButcherBehaviours() {
      return new FarmAnimal<PlayerLikeEntity>().speed(entity -> 0.8F).cooldownFor(e -> 60);
   }

   public static <E extends TensuraMerchantEntity & SmartBrainOwner<E>> ExtendedBehaviour<E> getMerchantFarmerBehaviours() {
      return new AllApplicableBehaviours(
            new ExtendedBehaviour[]{
               new FindNearestPosition()
                  .setRadius(32.0, 8.0)
                  .predicate((mob, pos) -> {
                     BlockState state = mob.level().getBlockState(pos);
                     return UseFarmland.isValidCrop(state, mob) || state.is(Blocks.FARMLAND) && mob.level().getBlockState(pos.above()).isAir();
                  })
                  .action((mob, pos) -> BrainUtils.setMemory(mob, MemoryModuleType.WALK_TARGET, new WalkTarget(pos, 1.0F, 1)))
                  .startCondition(entity -> !BrainUtils.hasMemory(entity, MemoryModuleType.WALK_TARGET)),
               new UseFarmland().speed(entity -> 1.0F).reselectionDelay(entity -> 10L),
               new SetRandomWalkTargetAroundCenter()
                  .attempts(20)
                  .maxDistance(entity -> 10.0)
                  .verticalProbe(10)
                  .goToCenterWhen((entity, center, radius) -> entity.getRandom().nextInt(20) == 10)
                  .center(x$0 -> getJobSitePos((LivingEntity)x$0))
                  .speedModifier((entity, pos) -> 1.0F)
                  .cooldownFor(e -> 100)
            }
         )
         .startCondition(rec$ -> rec$.shouldDoFarming());
   }

   public static <E extends PlayerLikeEntity & SmartBrainOwner<E>> ExtendedBehaviour<E> getBasicFarmerBehaviours() {
      return new AllApplicableBehaviours(
         new ExtendedBehaviour[]{
            new FindNearestPosition()
               .setRadius(32.0, 8.0)
               .predicate((mob, pos) -> {
                  BlockState state = mob.level().getBlockState(pos);
                  return UseFarmland.isValidCrop(state, mob) || state.is(Blocks.FARMLAND) && mob.level().getBlockState(pos.above()).isAir();
               })
               .action((mob, pos) -> BrainUtils.setMemory(mob, MemoryModuleType.WALK_TARGET, new WalkTarget(pos, 1.0F, 1)))
               .startCondition(entity -> !entity.shouldDoFarming() && BrainUtils.hasMemory(entity, MemoryModuleType.WALK_TARGET)),
            new UseFarmland().startCondition(entity -> !entity.getBrain().isActive(Activity.REST))
         }
      );
   }

   public static <E extends TensuraMerchantEntity & SmartBrainOwner<E>> ExtendedBehaviour<E> getMerchantFishingBehaviours() {
      return new AllApplicableBehaviours(
         new ExtendedBehaviour[]{
            new FindNearestPosition()
               .setRadius(32.0, 8.0)
               .predicate((mob, pos) -> FishAtWater.getValidWater().test(mob, pos))
               .action((mob, pos) -> BrainUtils.setMemory(mob, MemoryModuleType.WALK_TARGET, new WalkTarget(pos, 1.0F, 1)))
               .startCondition(entity -> !entity.shouldDoFarming() && BrainUtils.hasMemory(entity, MemoryModuleType.WALK_TARGET)),
            new FishAtWater<PlayerLikeEntity>()
               .fishingLuck((entity, stack) -> 1 + EnchantmentHelper.getFishingLuckBonus((ServerLevel)entity.level(), stack, entity))
               .startCondition(entity -> entity.getBrain().isActive(Activity.WORK))
         }
      );
   }

   public static <E extends PlayerLikeEntity & SmartBrainOwner<E>> ExtendedBehaviour<E> getBasicFishingBehaviours() {
      return new AllApplicableBehaviours(
         new ExtendedBehaviour[]{
            new FindNearestPosition()
               .setRadius(32.0, 8.0)
               .predicate((mob, pos) -> FishAtWater.getValidWater().test(mob, pos))
               .action((mob, pos) -> BrainUtils.setMemory(mob, MemoryModuleType.WALK_TARGET, new WalkTarget(pos, 1.0F, 1)))
               .startCondition(entity -> entity.shouldDoFishing() && !BrainUtils.hasMemory(entity, MemoryModuleType.WALK_TARGET)),
            new FishAtWater().startCondition(entity -> !entity.getBrain().isActive(Activity.REST))
         }
      );
   }

   public static <E extends PlayerLikeEntity & SmartBrainOwner<E>> ExtendedBehaviour<E> getBasicGuardBehaviours(E living) {
      return new AllApplicableBehaviours(
         new ExtendedBehaviour[]{
            TensuraBehaviourHelper.getPreyTargeting(living, entity -> false),
            new RetaliateOrTarget()
               .alertAlliesWhen((mob, entity) -> !mob.isTame() && !entity.isInvisible())
               .attackablePredicate(entity -> living.shouldTarget(living, entity, living::shouldTarget))
               .isAllyIf((owner, ally) -> {
                  if (!owner.getClass().isAssignableFrom(ally.getClass()) || BrainUtils.getTargetOfEntity(ally) != null) {
                     return false;
                  } else if (!(ally instanceof PlayerLikeEntity entity)) {
                     return false;
                  } else if (entity.shouldDoGuarding() && owner.getOwner() == entity.getOwner()) {
                     Entity lastHurtBy = (Entity)BrainUtils.getMemory(ally, MemoryModuleType.HURT_BY_ENTITY);
                     return lastHurtBy == null || !ally.isAlliedTo(lastHurtBy);
                  } else {
                     return false;
                  }
               })
               .startCondition(rec$ -> rec$.shouldDoGuarding()),
            new SubordinateFollowOwner()
         }
      );
   }

   public static <E extends PlayerLikeEntity & SmartBrainOwner<E>> ExtendedBehaviour<E> getMerchantLumberjackBehaviours() {
      return new AllApplicableBehaviours(
            new ExtendedBehaviour[]{new ReplantSapling.SetWalkTarget(), new ChopTree.SetWalkTarget(), new ReplantSapling(), new ChopTree(entity -> 200)}
         )
         .startCondition(rec$ -> rec$.shouldDoLumberjack());
   }

   public static <E extends PlayerLikeEntity & SmartBrainOwner<E>> ExtendedBehaviour<E> getBasicLumberjackBehaviours() {
      return new AllApplicableBehaviours(
            new ExtendedBehaviour[]{
               new ReplantSapling.SetWalkTarget().setSaplingDistance(2.5),
               new ChopTree.SetWalkTarget(),
               new ReplantSapling().setSaplingDistance(2.5),
               new ChopTree(entity -> 240)
            }
         )
         .startCondition(rec$ -> rec$.shouldDoLumberjack());
   }

   public static <E extends TensuraMerchantEntity & SmartBrainOwner<E>> ExtendedBehaviour<E> getMerchantShepherdBehaviours() {
      return new FarmAnimal<TensuraMerchantEntity>()
         .canFarm(rec$ -> rec$.shouldDoShepherd())
         .types(Sheep.class)
         .shouldHarvest(entity -> true)
         .cooldownFor(e -> 40);
   }

   public static <E extends PlayerLikeEntity & SmartBrainOwner<E>> ExtendedBehaviour<E> getBasicShepherdBehaviours() {
      return new FarmAnimal<PlayerLikeEntity>()
         .canFarm(rec$ -> rec$.shouldDoShepherd())
         .types(Sheep.class)
         .shouldHarvest(entity -> true)
         .speed(entity -> 0.8F)
         .cooldownFor(e -> 60);
   }

   public static <E extends PlayerLikeEntity & SmartBrainOwner<E>> ExtendedBehaviour<E> getDepositItemsBehaviours() {
      return new AllApplicableBehaviours(
            new ExtendedBehaviour[]{
               new FindNearestPosition()
                  .setRadius(32.0, 8.0)
                  .predicate((x$0, x$1) -> DepositPickedItems.canUseContainer(x$0, x$1))
                  .action((mob, pos) -> BrainUtils.setMemory(mob, MemoryModuleType.WALK_TARGET, new WalkTarget(pos, 1.0F, 1)))
                  .startCondition(entity -> !BrainUtils.hasMemory(entity, MemoryModuleType.WALK_TARGET))
                  .cooldownFor(e -> 300),
               new DepositPickedItems()
            }
         )
         .startCondition(rec$ -> rec$.shouldDepositPickedItems());
   }

   public static <E extends TensuraMerchantEntity & SmartBrainOwner<E>> ExtendedBehaviour<E> getMerchantIdleBehaviours(E living) {
      return new AllApplicableBehaviours(
         new ExtendedBehaviour[]{
            new InteractWithEntity(living.getType(), MemoryModuleType.BREED_TARGET)
               .selfPredicate(AgeableMob::canBreed)
               .targetPredicate(AgeableMob::canBreed)
               .bothPredicate((entity, target) -> ((Animal)entity).canMate((Animal)target))
               .interactTime(entity -> 300),
            new VillagerLikeBreed(),
            new FollowParent().startCondition(entity -> !entity.isTame() || entity.isWandering()),
            new TradeWithMerchants(),
            new MerchantFollowTrader(),
            new SetPlayerLookTarget(),
            new SetRandomLookTarget()
         }
      );
   }

   public static <E extends TensuraMerchantEntity & SmartBrainOwner<E>> ExtendedBehaviour<E> getMerchantRandomBehaviours(E living) {
      return new OneRandomBehaviour(
            new ExtendedBehaviour[]{
               new SetRandomWalkTargetAroundCenter()
                  .maxDistance(entity -> 10.0)
                  .center(x$0 -> getJobSitePos((LivingEntity)x$0))
                  .goToCenterWhen((entity, center, radius) -> entity.distanceToSqr(center) > radius || entity.shouldRestock())
                  .startCondition(entity -> BrainUtils.hasMemory(entity, MemoryModuleType.JOB_SITE)),
               new FirstApplicableBehaviour(
                  new ExtendedBehaviour[]{
                     new InteractWithEntity(living.getType(), MemoryModuleType.INTERACTION_TARGET).startCondition(entity -> entity.getRandom().nextInt(7) == 1),
                     TensuraBehaviourHelper.setWanderAroundHome(),
                     new SetRandomWalkTarget().startCondition(entity -> !BrainUtils.hasMemory(entity, MemoryModuleType.JOB_SITE))
                  }
               ),
               new Idle().runFor(entity -> entity.getRandom().nextInt(30, 60))
            }
         )
         .startCondition(entity -> !entity.isOrderedToSit());
   }
}
