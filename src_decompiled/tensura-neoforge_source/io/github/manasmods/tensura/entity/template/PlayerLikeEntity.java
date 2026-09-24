package io.github.manasmods.tensura.entity.template;

import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.enchantment.TensuraEnchantmentHelper;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.CrossbowAttack;
import io.github.manasmods.tensura.entity.ai.behaviour.attack.RangedWeaponAttack;
import io.github.manasmods.tensura.entity.magic.misc.NonPlayerFishingHook;
import io.github.manasmods.tensura.item.weapon.ranged.KunaiItem;
import io.github.manasmods.tensura.item.weapon.ranged.SimpleBowItem;
import io.github.manasmods.tensura.item.weapon.ranged.SimpleCrossbowItem;
import io.github.manasmods.tensura.item.weapon.ranged.WebGunItem;
import io.github.manasmods.tensura.storage.effect.EffectStorage;
import io.github.manasmods.tensura.world.TensuraGameRules;
import java.util.function.Predicate;
import lombok.Generated;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerLikeEntity extends TensuraHumanoidEntity implements RangedAttackMob {
   @Nullable
   public NonPlayerFishingHook fishing;
   private SimpleContainer pickedItems;

   public PlayerLikeEntity(EntityType<? extends PlayerLikeEntity> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.setCanPickUpLoot(true);
      this.createPickedItems();
   }

   protected void createPickedItems() {
      SimpleContainer container = this.pickedItems;
      this.pickedItems = new SimpleContainer(this.getInventorySize());
      if (container != null) {
         container.removeListener(this);
         int i = Math.min(container.getContainerSize(), this.pickedItems.getContainerSize());

         for (int j = 0; j < i; j++) {
            ItemStack itemStack = container.getItem(j);
            if (!itemStack.isEmpty()) {
               this.pickedItems.setItem(j, itemStack.copy());
            }
         }
      }

      this.pickedItems.addListener(this);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      ListTag listTag = new ListTag();

      for (int i = 0; i < this.pickedItems.getContainerSize(); i++) {
         ItemStack stack = this.pickedItems.getItem(i);
         if (!stack.isEmpty()) {
            CompoundTag tag = new CompoundTag();
            tag.putByte("Slot", (byte)i);
            listTag.add(stack.save(this.registryAccess(), tag));
         }
      }

      compound.put("PickedItems", listTag);
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.createPickedItems();
      ListTag list = compound.getList("PickedItems", 10);

      for (int i = 0; i < list.size(); i++) {
         CompoundTag tag = list.getCompound(i);
         int slot = tag.getByte("Slot") & 255;
         if (slot < this.pickedItems.getContainerSize()) {
            ItemStack stack = ItemStack.parseOptional(this.registryAccess(), tag);
            if (!stack.isEmpty()) {
               this.pickedItems.setItem(slot, stack);
            }
         }
      }
   }

   @Override
   public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
      if (DATA_FLAGS_ID.equals(pKey)) {
         this.reapplyPosition();
         this.refreshDimensions();
      }

      super.onSyncedDataUpdated(pKey);
   }

   public boolean shouldHeal() {
      if (this.isSleeping()) {
         return false;
      }

      double maxHP = EffectStorage.getSeveranceMaxHealth(this);
      return this.isAngry() && maxHP > this.getMaxHealth() / 2.0F
         ? this.getHealth() <= this.getMaxHealth() / 2.0F
         : this.getHealth() < maxHP && !this.shouldSwim();
   }

   public boolean shouldSwim() {
      return !this.onGround()
         && !this.isAngry()
         && this.getDeltaMovement().lengthSqr() >= 0.0025
         && this.isInLiquid()
         && this.level().getBlockState(this.blockPosition().below(1)).canBeReplaced();
   }

   @Override
   public boolean isFood(ItemStack pStack) {
      return !pStack.is(TensuraItemTags.DUBIOUS_POISON_INGREDIENT) && !pStack.is(TensuraItemTags.DUBIOUS_RAW_INGREDIENT) && pStack.has(DataComponents.FOOD);
   }

   @NotNull
   @Override
   public EntityDimensions getDefaultDimensions(Pose pPose) {
      EntityDimensions entitydimensions = super.getDefaultDimensions(pPose);
      if (this.isSleeping()) {
         return entitydimensions;
      } else if (this.shouldSwim()) {
         return entitydimensions.scale(1.0F, 0.25F);
      } else {
         return !this.isOrderedToSit() && !this.isInSittingPose() ? entitydimensions : this.getSittingDimension(entitydimensions);
      }
   }

   protected EntityDimensions getSittingDimension(EntityDimensions original) {
      return original.scale(1.0F, 0.75F);
   }

   public double getFluidJumpThreshold() {
      float threshold = this.isBaby() ? 0.1F : 0.2F;
      return super.getFluidJumpThreshold() + threshold;
   }

   protected boolean doSleepingPose() {
      return true;
   }

   @Override
   public void tick() {
      super.tick();
      this.updatePoses();
      this.updateSwingTime();
      if (this.fishing != null && !this.fishing.isAlive()) {
         this.fishing = null;
      }
   }

   @Override
   public void aiStep() {
      super.aiStep();
      Level level = this.level();
      if (!level.isClientSide()) {
         if (!level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            if (level.getGameRules().getBoolean(TensuraGameRules.NPC_GRIEF)) {
               if (this.canPickUpLoot() && this.isAlive() && !this.dead) {
                  level.getProfiler().push("npcGrief");
                  Vec3i reach = this.getPickupReach();

                  for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, this.getBoundingBox().inflate(reach.getX(), reach.getY(), reach.getZ()))) {
                     if (!item.isRemoved() && !item.getItem().isEmpty() && !item.hasPickUpDelay() && this.wantsToPickUp(item.getItem())) {
                        this.pickUpItem(item);
                     }
                  }

                  level.getProfiler().pop();
               }
            }
         }
      }
   }

   protected void updatePoses() {
      Pose pose;
      if (this.isFallFlying()) {
         pose = Pose.FALL_FLYING;
      } else if (this.isSleeping()) {
         pose = this.doSleepingPose() ? Pose.SLEEPING : Pose.STANDING;
      } else if (this.isSwimming() || this.shouldSwim()) {
         pose = Pose.SWIMMING;
      } else if (this.isAutoSpinAttack()) {
         pose = Pose.SPIN_ATTACK;
      } else if (this.isShiftKeyDown()) {
         pose = Pose.CROUCHING;
      } else {
         pose = Pose.STANDING;
      }

      this.setPose(pose);
   }

   @Override
   public boolean doHurtTarget(Entity pEntity) {
      boolean flag = super.doHurtTarget(pEntity);
      if (flag) {
         this.swing(InteractionHand.MAIN_HAND, true);
      }

      return flag;
   }

   public boolean shouldDoButchering() {
      return !this.isWandering() ? false : this.getMainHandItem().is(TensuraItemTags.SHORT_SWORDS) || this.getMainHandItem().is(TensuraItemTags.DAGGERS);
   }

   public boolean shouldDoFarming() {
      return !this.isWandering() ? false : this.getMainHandItem().is(ItemTags.HOES) || this.getMainHandItem().is(TensuraItemTags.SICKLES);
   }

   public boolean shouldDoFishing() {
      return !this.isWandering() ? false : this.getMainHandItem().is(TensuraItemTags.FISHING_RODS);
   }

   public boolean shouldDoGuarding() {
      return false;
   }

   public boolean shouldDoLumberjack() {
      return !this.isWandering() ? false : this.getMainHandItem().is(ItemTags.AXES);
   }

   public boolean shouldDoShepherd() {
      return !this.isWandering() ? false : this.getMainHandItem().is(Items.SHEARS);
   }

   public boolean shouldAlwaysPickUpItem() {
      return this.shouldDoButchering()
         || this.shouldDoFarming()
         || this.shouldDoFishing()
         || this.shouldDoLumberjack()
         || this.shouldDoShepherd()
         || this.shouldDoGuarding();
   }

   public boolean shouldPickUpLoot(ItemStack stack) {
      if (this.isFood(stack)) {
         return true;
      } else if (this.shouldDoGuarding()) {
         return stack.is(TensuraItemTags.GUARD_STORABLE) || stack.is(ItemTags.WEAPON_ENCHANTABLE);
      } else if (this.shouldDoButchering()) {
         return stack.is(TensuraItemTags.BUTCHER_STORABLE) || stack.is(TensuraItemTags.SHORT_SWORDS);
      } else if (this.shouldDoFarming()) {
         return stack.is(TensuraItemTags.FARMER_STORABLE) || stack.is(ItemTags.HOES) || stack.is(TensuraItemTags.SICKLES);
      } else if (this.shouldDoFishing()) {
         return stack.is(TensuraItemTags.FISHERMAN_STORABLE) || stack.is(TensuraItemTags.FISHING_RODS);
      } else if (this.shouldDoLumberjack()) {
         return stack.is(TensuraItemTags.LUMBERJACK_STORABLE) || stack.is(ItemTags.AXES);
      } else {
         return !this.shouldDoShepherd() ? false : stack.is(TensuraItemTags.SHEPHERD_STORABLE) || stack.is(Items.SHEARS);
      }
   }

   public boolean isNPCStorableItem(ItemStack pStack) {
      if (this.shouldDoGuarding()) {
         return pStack.is(TensuraItemTags.GUARD_STORABLE);
      } else if (this.shouldDoButchering()) {
         return pStack.is(TensuraItemTags.BUTCHER_STORABLE);
      } else if (this.shouldDoFarming()) {
         return pStack.is(TensuraItemTags.FARMER_STORABLE);
      } else if (this.shouldDoFishing()) {
         return pStack.is(TensuraItemTags.FISHERMAN_STORABLE);
      } else if (this.shouldDoLumberjack()) {
         return pStack.is(TensuraItemTags.LUMBERJACK_STORABLE);
      } else {
         return this.shouldDoShepherd() ? pStack.is(TensuraItemTags.SHEPHERD_STORABLE) : pStack.is(TensuraItemTags.NPC_STORABLE);
      }
   }

   public int getRemainingPickedItems(ItemStack pStack) {
      return pStack.is(TensuraItemTags.NPC_KEEP) ? 4 : 0;
   }

   public boolean wantsToPickUp(ItemStack pStack) {
      if (!this.isAlive()) {
         return false;
      }

      if (this.shouldPickUpLoot(pStack)) {
         return true;
      }

      if (this.isTame()) {
         return false;
      }

      for (EquipmentSlot slot : EquipmentSlot.values()) {
         if (this.getItemBySlot(slot).isEmpty()) {
            return true;
         }
      }

      return this.inventory.hasAnyMatching(stack -> stack.getItem() == pStack.getItem() && stack.isStackable());
   }

   protected void pickUpItem(ItemEntity itemEntity) {
      ItemStack stack = itemEntity.getItem();
      if (!this.isTame() || !this.shouldPickUpLoot(itemEntity.getItem())) {
         ItemStack copy = this.equipItemIfPossible(stack.copy());
         int takenCount = Math.min(copy.getCount(), copy.getMaxStackSize());
         if (!copy.isEmpty()) {
            this.onItemPickup(itemEntity);
            this.take(itemEntity, takenCount);
            stack.shrink(takenCount);
            this.addToPickedList(copy.copyWithCount(takenCount));
            if (stack.isEmpty()) {
               itemEntity.discard();
            }

            return;
         }
      }

      if (this.inventory.canAddItem(stack)) {
         ItemStack taken = this.inventory.addItem(stack);
         int takenCount = taken.isEmpty()
            ? Math.min(stack.getCount(), stack.getMaxStackSize())
            : Math.min(stack.getCount() - taken.getCount(), stack.getMaxStackSize());
         if (takenCount > 0) {
            this.addToPickedList(stack.copyWithCount(takenCount));
            this.updateContainerEquipment();
            this.onItemPickup(itemEntity);
            this.take(itemEntity, takenCount);
            stack.shrink(takenCount);
            if (stack.isEmpty()) {
               itemEntity.discard();
            }
         }
      }
   }

   public ItemStack equipItemIfPossible(ItemStack itemStack) {
      EquipmentSlot slot = this.getEquipmentSlotForItem(itemStack);
      ItemStack stack = this.getItemBySlot(slot);
      boolean replace = this.canReplaceCurrentItem(itemStack, stack);
      if (slot.isArmor() && !replace) {
         slot = EquipmentSlot.MAINHAND;
         stack = this.getItemBySlot(slot);
         replace = stack.isEmpty();
      }

      if (replace && this.canHoldItem(itemStack)) {
         int id = this.getSlotId(slot);
         if (id == -1) {
            return ItemStack.EMPTY;
         }

         double chance = this.getEquipmentDropChance(slot);
         if (!stack.isEmpty() && Math.max(this.random.nextFloat() - 0.1F, 0.0F) < chance) {
            this.spawnAtLocation(stack);
         }

         ItemStack limit = slot.limit(itemStack);
         this.inventory.setItem(id, limit);
         this.setItemSlotAndDropWhenKilled(slot, this.inventory.getItem(id));
         return limit;
      } else {
         return ItemStack.EMPTY;
      }
   }

   public void addToPickedList(ItemStack stack) {
      if (!stack.isEmpty() && this.isNPCStorableItem(stack)) {
         ItemStack stackCopy = stack.copy();
         this.pickedItems.addItem(stackCopy);
      }
   }

   public boolean shouldDepositPickedItems() {
      for (ItemStack itemStack : this.getPickedItems().getItems()) {
         if (!itemStack.isEmpty() && this.getPickedItems().countItem(itemStack.getItem()) > this.getRemainingPickedItems(itemStack)) {
            return true;
         }
      }

      return false;
   }

   protected void hurtCurrentlyUsedShield(float f) {
      ItemStack shield = this.getItemInHand(InteractionHand.OFF_HAND);
      this.swing(InteractionHand.OFF_HAND, true);
      if (f >= 3.0F) {
         int i = 1 + Mth.floor(f);
         InteractionHand interactionHand = this.getUsedItemHand();
         shield.hurtAndBreak(i, this, getSlotForHand(interactionHand));
         if (shield.isEmpty()) {
            this.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
            this.updateContainerEquipment();
            this.playSound(SoundEvents.SHIELD_BREAK, 0.8F, 0.8F + this.level().random.nextFloat() * 0.4F);
         } else {
            this.playSound(SoundEvents.SHIELD_BLOCK, 0.8F, 0.8F + this.level().random.nextFloat() * 0.4F);
         }
      }
   }

   public boolean isDamageSourceBlocked(DamageSource damageSource) {
      if (!TensuraDamageHelper.isPhysicalAttack(damageSource)) {
         return false;
      } else {
         return damageSource.tensura$getBarrierBypassLevel() >= 2.0F ? false : super.isDamageSourceBlocked(damageSource);
      }
   }

   public boolean isBlocking() {
      if (!this.getOffhandItem().isEmpty()) {
         return this.getOffhandItem().getUseAnimation() != UseAnim.BLOCK
            ? false
            : this.random.nextFloat() <= 0.3 || this.getOffhandItem().getUseDuration(this) - this.useItemRemaining >= 5;
      } else {
         return false;
      }
   }

   protected void populateDefaultEquipmentSlots(RandomSource pRandom, DifficultyInstance pDifficulty) {
      if (!(pRandom.nextFloat() >= 0.2F)) {
         int i = pRandom.nextInt(2);
         if (pRandom.nextFloat() < 0.095F) {
            i++;
         }

         if (pRandom.nextFloat() < 0.095F) {
            i++;
         }

         if (pRandom.nextFloat() < 0.095F) {
            i++;
         }

         boolean flag = true;

         for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == Type.HUMANOID_ARMOR) {
               ItemStack itemstack = this.getItemBySlot(slot);
               if (!flag && pRandom.nextFloat() < 0.25) {
                  break;
               }

               flag = false;
               if (itemstack.isEmpty()) {
                  Item item = this.getEquipmentForArmor(slot, i);
                  if (item != null) {
                     int slotId = this.getSlotId(slot);
                     if (slotId >= 0) {
                        ItemStack stack = new ItemStack(item);
                        this.inventory.setItem(slotId, stack);
                        this.updateContainerEquipment();
                     }
                  }
               }
            }
         }
      }
   }

   @Nullable
   public Item getEquipmentForArmor(EquipmentSlot pSlot, int pChance) {
      return getEquipmentForSlot(pSlot, pChance);
   }

   public boolean usingRangedWeapon() {
      return this.usingSpear() ? true : this.getMainHandItem().getItem() instanceof ProjectileWeaponItem;
   }

   public boolean usingSpear() {
      ItemStack weapon = this.getMainHandItem();
      LivingEntity target = this.getTarget();
      if (target == null) {
         return false;
      }

      if (weapon.getUseAnimation().equals(UseAnim.SPEAR)) {
         if (TensuraEnchantmentHelper.getEnchantmentLevel(this.level(), Enchantments.RIPTIDE, weapon) > 0) {
            return false;
         } else {
            return !(weapon.getItem() instanceof ProjectileItem) ? false : this.isWithinMeleeAttackRange(target);
         }
      } else {
         return false;
      }
   }

   @NotNull
   public ItemStack getProjectile(ItemStack itemStack) {
      if (itemStack.getItem() instanceof ProjectileWeaponItem weaponItem) {
         Predicate var7 = weaponItem.getSupportedHeldProjectiles();
         ItemStack itemstack = ProjectileWeaponItem.getHeldProjectile(this, var7);
         if (!itemstack.isEmpty()) {
            return itemstack;
         }

         var7 = weaponItem.getAllSupportedProjectiles();

         for (int i = 0; i < this.inventory.getContainerSize(); i++) {
            ItemStack stack = this.inventory.getItem(i);
            if (var7.test(stack)) {
               return stack;
            }
         }

         return !this.isTame() ? new ItemStack(Items.ARROW) : ItemStack.EMPTY;
      } else {
         return ItemStack.EMPTY;
      }
   }

   public void performRangedAttack(LivingEntity target, float pDistanceFactor) {
      ItemStack weapon = this.getMainHandItem();
      switch (weapon.getItem()) {
         case BowItem ignoredBow:
            this.performBowAttack(target, weapon, pDistanceFactor);
            break;
         case CrossbowItem ignoredCross:
            this.performCrossbowAttack(pDistanceFactor);
            break;
         case WebGunItem webGunItem:
            webGunItem.performShooting(target.level(), target, InteractionHand.MAIN_HAND, weapon, 2.0F, 1.0F, target);
            this.noActionTime = 0;
            break;
         default:
            this.performSpearAttack(target, weapon, pDistanceFactor);
      }
   }

   protected void performBowAttack(LivingEntity target, ItemStack bowStack, float distance) {
      ItemStack projectile = this.getProjectile(bowStack);
      if (!projectile.isEmpty()) {
         AbstractArrow abstractArrow = ProjectileUtil.getMobArrow(this, projectile, distance, bowStack);
         double d = target.getX() - this.getX();
         double e = target.getY(0.33) - abstractArrow.getY();
         double g = target.getZ() - this.getZ();
         double h = Math.sqrt(d * d + g * g);
         float inaccuracy = bowStack.getItem() instanceof SimpleBowItem bow ? bow.getInaccuracy() : 1.0F;
         abstractArrow.shoot(d, e + h * 0.2, g, distance, inaccuracy);
         this.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
         this.level().addFreshEntity(abstractArrow);
         if (TensuraEnchantmentHelper.getEnchantmentLevel(this.level(), Enchantments.INFINITY, bowStack) <= 0) {
            projectile.shrink(1);
         }

         this.inventory.getItem(4).hurtAndBreak(1, this, EquipmentSlot.MAINHAND);
         this.inventory.setChanged();
      }
   }

   public void performCrossbowAttack(float distance) {
      ItemStack itemStack = this.getItemInHand(InteractionHand.MAIN_HAND);
      if (itemStack.getItem() instanceof CrossbowItem crossbow) {
         crossbow.performShooting(this.level(), this, InteractionHand.MAIN_HAND, itemStack, distance, 0.0F, null);
      }

      this.noActionTime = 0;
   }

   protected void performSpearAttack(LivingEntity pTarget, ItemStack weapon, float distance) {
      if (weapon.getItem() instanceof ProjectileItem projectileItem) {
         ItemStack copy = weapon.copy();
         copy.enchant(TensuraEnchantmentHelper.getEnchantment(this.level(), Enchantments.LOYALTY), 0);
         float rot = this.yHeadRot + 60.0F;
         Vec3 position = new Vec3(
            this.getX() - this.getBbWidth() * 0.5 * Mth.sin(rot * (float) Math.PI / 180.0F),
            this.getEyeY() - 0.2F,
            this.getZ() + this.getBbWidth() * 0.5 * Mth.cos(rot * (float) Math.PI / 180.0F)
         );
         Projectile projectile = projectileItem.asProjectile(this.level(), position, copy, Direction.UP);
         if (projectile instanceof AbstractArrow arrow) {
            arrow.pickup = Pickup.DISALLOWED;
         }

         projectile.setOwner(this);
         double d0 = pTarget.getX() - this.getX();
         double d1 = pTarget.getY(0.33) - projectile.getY();
         double d2 = pTarget.getZ() - this.getZ();
         double d3 = Math.sqrt(d0 * d0 + d2 * d2);
         projectile.shoot(d0, d1 + d3 * 0.2F, d2, distance, 1.0F);
         this.playSound(SoundEvents.DROWNED_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
         this.level().addFreshEntity(projectile);
      }
   }

   public static <E extends PlayerLikeEntity> RangedWeaponAttack<E> getBowAttack() {
      return (RangedWeaponAttack<E>)new RangedWeaponAttack(InteractionHand.MAIN_HAND)
         .shootPower(
            entity -> {
               ItemStack weapon = entity.getMainHandItem();
               return weapon.getItem() instanceof SimpleBowItem bowItem
                  ? bowItem.getPowerForChargeTime(entity.getTicksUsingItem()) * 1.5F
                  : BowItem.getPowerForTime(entity.getTicksUsingItem()) * 1.5F;
            }
         )
         .delayFor(entity -> {
            ItemStack weapon = entity.getMainHandItem();
            return weapon.getItem() instanceof SimpleBowItem bowItem ? bowItem.getChargeTicks() : 20;
         })
         .startCondition(entity -> {
            if (entity.usingSpear()) {
               return false;
            }

            ItemStack weapon = entity.getMainHandItem();
            return weapon.getItem() instanceof ProjectileWeaponItem item ? !(item instanceof CrossbowItem) : false;
         });
   }

   public static <E extends PlayerLikeEntity> CrossbowAttack<E> getCrossbowAttack() {
      return (CrossbowAttack<E>)new CrossbowAttack(InteractionHand.MAIN_HAND)
         .shootPower(
            entity -> {
               ChargedProjectiles charged = (ChargedProjectiles)entity.getMainHandItem().get(DataComponents.CHARGED_PROJECTILES);
               if (charged == null) {
                  return 0.0F;
               } else {
                  return entity.getMainHandItem().getItem() instanceof SimpleCrossbowItem bowItem
                     ? bowItem.getShootingPower(charged)
                     : charged.contains(Items.FIREWORK_ROCKET) ? 1.6F : 3.15F;
               }
            }
         )
         .delayFor(entity -> {
            ItemStack weapon = entity.getMainHandItem();
            ChargedProjectiles charged = (ChargedProjectiles)weapon.get(DataComponents.CHARGED_PROJECTILES);
            if (charged != null && !charged.isEmpty()) {
               return 3;
            }

            int chargeTicks = weapon.getItem() instanceof SimpleCrossbowItem bowItem ? bowItem.getChargeTicks() : 25;
            return SimpleCrossbowItem.getChargeDuration(weapon, entity, chargeTicks + 5);
         })
         .startCondition(entity -> entity.getMainHandItem().getUseAnimation().equals(UseAnim.CROSSBOW));
   }

   public static <E extends PlayerLikeEntity> RangedWeaponAttack<E> getSpearAttack(int delay) {
      return (RangedWeaponAttack<E>)new RangedWeaponAttack(InteractionHand.MAIN_HAND).shootPower(entity -> {
         ItemStack weapon = entity.getMainHandItem();
         return weapon.getItem() instanceof KunaiItem kunai ? kunai.getPowerForChargeTime(entity.getTicksUsingItem()) : 1.2F;
      }).delayFor(entity -> delay).startCondition(PlayerLikeEntity::usingSpear);
   }

   @Generated
   public SimpleContainer getPickedItems() {
      return this.pickedItems;
   }
}
