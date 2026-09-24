package io.github.manasmods.tensura.entity.projectile;

import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.EntityEvents;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.EntityEvents.ProjectileHitEvent;
import io.github.manasmods.manascore.skill.api.EntityEvents.ProjectileHitResult;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Magic;
import io.github.manasmods.tensura.ability.skill.common.ThoughtCommunicationSkill;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.data.TensuraItemTags;
import io.github.manasmods.tensura.effect.template.TensuraMobEffect;
import io.github.manasmods.tensura.enchantment.TensuraEnchantmentHelper;
import io.github.manasmods.tensura.entity.TensuraProjectile;
import io.github.manasmods.tensura.entity.human.ShinjiTanimuraEntity;
import io.github.manasmods.tensura.event.TensuraSkillEvents;
import io.github.manasmods.tensura.item.misc.ElementCoreItem;
import io.github.manasmods.tensura.item.misc.OrbOfDominationItem;
import io.github.manasmods.tensura.item.tool.MultitoolItem;
import io.github.manasmods.tensura.registry.block.TensuraBlocks;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.ProjectileEntityTypes;
import io.github.manasmods.tensura.registry.item.misc.TensuraDataComponents;
import io.github.manasmods.tensura.world.TensuraGameRules;
import lombok.Generated;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.FireChargeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ThrownItemProjectile extends AbstractArrow {
   private static final EntityDataAccessor<ItemStack> SOURCE_ITEM = SynchedEntityData.defineId(ThrownItemProjectile.class, EntityDataSerializers.ITEM_STACK);
   protected static final EntityDataAccessor<Integer> LOYALTY_LEVEL = SynchedEntityData.defineId(ThrownItemProjectile.class, EntityDataSerializers.INT);
   protected static final EntityDataAccessor<Float> MINING_HEALTH = SynchedEntityData.defineId(ThrownItemProjectile.class, EntityDataSerializers.FLOAT);
   protected int mode = 0;
   protected ManasSkillInstance skill = null;
   private float baseDamage;
   private boolean finishPiercing;
   private int piercingEntity = 0;
   public int clientSideReturnKunaiTickCount;
   private int maxBrokenBlocks = 0;
   private int brokenBlocks = 0;
   private boolean continueBreaking = false;
   private Entity homingTarget = null;

   public ThrownItemProjectile(EntityType<? extends ThrownItemProjectile> type, Level level) {
      super(type, level);
   }

   public ThrownItemProjectile(Level worldIn, LivingEntity shooter, ItemStack pStack, boolean right, float baseDamage) {
      super((EntityType)ProjectileEntityTypes.THROWN_ITEM.get(), shooter, worldIn, pStack, null);
      if (pStack.isEmpty()) {
         this.setInvisible(true);
         ItemStack fallback = Items.DIRT.getDefaultInstance();
         this.setSourceItem(fallback);
         this.setPickupItemStack(fallback);
      } else {
         this.setSourceItem(pStack.copy());
      }

      this.baseDamage = baseDamage;
      float rot = shooter.yHeadRot + (right ? 60 : -60);
      this.setPos(
         shooter.getX() - shooter.getBbWidth() * 0.5 * Mth.sin(rot * (float) (Math.PI / 180.0)),
         shooter.getEyeY() - 0.2F,
         shooter.getZ() + shooter.getBbWidth() * 0.5 * Mth.cos(rot * (float) (Math.PI / 180.0))
      );
      if (pStack.has(DataComponents.TOOL)) {
         if (pStack.getItem() instanceof TieredItem item) {
            this.setMiningHealth(item.getTier().getUses());
         } else if (pStack.getItem() instanceof MultitoolItem item) {
            this.setMiningHealth(item.getTier().getUses());
         }
      }
   }

   @NotNull
   public ItemStack getPickupItem() {
      return this.getSourceItem().copy();
   }

   @NotNull
   public ItemStack getPickupItemStackOrigin() {
      return this.getSourceItem();
   }

   @NotNull
   protected ItemStack getDefaultPickupItem() {
      return this.getSourceItem();
   }

   @Nullable
   protected EntityHitResult findHitEntity(Vec3 pStartVec, Vec3 pEndVec) {
      return this.finishPiercing ? null : super.findHitEntity(pStartVec, pEndVec);
   }

   protected boolean canHitEntity(Entity entity) {
      return entity != this.getOwner() && super.canHitEntity(entity);
   }

   protected boolean shouldGrief() {
      return this.getOwner() != null && !this.getOwner().getType().equals(EntityType.PLAYER)
         ? this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)
         : TensuraGameRules.canSkillGrief(this.level());
   }

   private boolean isAcceptableReturnOwner() {
      Entity entity = this.getOwner();
      if (entity == null) {
         return false;
      } else {
         return !entity.isAlive() ? false : !(entity instanceof ServerPlayer) || !entity.isSpectator();
      }
   }

   public void tick() {
      if (this.getSourceItem().isEmpty()) {
         this.discard();
      } else {
         if (this.getOwner() instanceof Player owner) {
            Entity entity = this.getHomingTarget();
            if (entity != null && !this.finishPiercing && this.isAlive()) {
               this.homing(entity);
            }
         }

         Entity owner = this.getOwner();
         int level = this.isInvisible() ? 0 : this.getLoyaltyLevel();
         if (level > 0 && (this.finishPiercing || this.isNoPhysics()) && owner != null && this.isAlive()) {
            if (!this.isAcceptableReturnOwner()) {
               if (!this.level().isClientSide() && this.pickup == Pickup.ALLOWED) {
                  this.spawnAtLocation(this.getPickupItem(), 0.1F);
               }

               this.discard();
            } else {
               this.setNoPhysics(true);
               Vec3 vec3 = owner.getEyePosition().subtract(this.position());
               this.setPosRaw(this.getX(), this.getY() + vec3.y * 0.015 * level, this.getZ());
               if (this.level().isClientSide()) {
                  this.yOld = this.getY();
               }

               double d0 = 0.05 * level;
               this.setDeltaMovement(this.getDeltaMovement().scale(0.95).add(vec3.normalize().scale(d0)));
               if (this.clientSideReturnKunaiTickCount == 0) {
                  this.playSound(SoundEvents.TRIDENT_RETURN, 10.0F, 1.0F);
               }

               this.clientSideReturnKunaiTickCount++;
            }
         }

         super.tick();
         if (this.continueBreaking) {
            this.tickContinueBreaking();
         } else if (this.inGroundTime > 5) {
            this.finishPiercing = true;
         }
      }
   }

   private void tickContinueBreaking() {
      this.inGround = false;
      this.inGroundTime = 0;
      Vec3 position = this.position();
      Vec3 targetPos = position.add(this.getDeltaMovement());
      EntityHitResult result = this.findHitEntity(position, targetPos);
      if (result != null) {
         Changeable<ProjectileHitResult> resultChangeable = Changeable.of(ProjectileHitResult.DEFAULT);
         Changeable<ProjectileDeflection> deflectionChangeable = Changeable.of(ProjectileDeflection.NONE);
         ((ProjectileHitEvent)EntityEvents.PROJECTILE_HIT.invoker()).hit(result, this, deflectionChangeable, resultChangeable);
         if (resultChangeable.get() != ProjectileHitResult.PASS) {
            this.onHitEntity(result);
            this.level().gameEvent(GameEvent.PROJECTILE_LAND, result.getLocation(), Context.of(this, null));
         }
      }

      HitResult hitResult = this.level().clip(new ClipContext(position, targetPos, Block.COLLIDER, Fluid.NONE, this));
      if (hitResult.getType() == Type.BLOCK) {
         Changeable<ProjectileHitResult> resultChangeable = Changeable.of(ProjectileHitResult.DEFAULT);
         Changeable<ProjectileDeflection> deflectionChangeable = Changeable.of(ProjectileDeflection.NONE);
         ((ProjectileHitEvent)EntityEvents.PROJECTILE_HIT.invoker()).hit(hitResult, this, deflectionChangeable, resultChangeable);
         if (resultChangeable.get() != ProjectileHitResult.PASS) {
            this.onHit(hitResult);
         }
      }
   }

   public void tickDespawn() {
      int i = this.getLoyaltyLevel();
      if ((this.pickup != Pickup.ALLOWED || i <= 0) && ++this.life >= TensuraProjectile.CONFIG.thrownItemDespawnTick) {
         this.discard();
      }
   }

   private void homing(Entity living) {
      BlockPos pos = living.blockPosition();
      double posX = this.getX();
      double posY = this.getY();
      double posZ = this.getZ();
      double motionX = this.getDeltaMovement().x;
      double motionY = this.getDeltaMovement().y;
      double motionZ = this.getDeltaMovement().z;
      if (pos.getX() != 0 || pos.getY() != 0 || pos.getZ() != 0) {
         Vec3 targetVector = new Vec3(pos.getX() + 0.5 - posX, pos.getY() + 0.75 - posY, pos.getZ() + 0.5 - posZ);
         double length = targetVector.length();
         targetVector = targetVector.scale(0.3 / length);
         double weight = 0.0;
         if (length <= 3.0) {
            weight = (3.0 - length) * 0.3;
         }

         motionX = (0.9 - weight) * motionX + (0.1 + weight) * targetVector.x;
         motionY = (0.9 - weight) * motionY + (0.1 + weight) * targetVector.y;
         motionZ = (0.9 - weight) * motionZ + (0.1 + weight) * targetVector.z;
      }

      posX += motionX;
      posY += motionY;
      posZ += motionZ;
      this.setPos(posX, posY, posZ);
      this.setDeltaMovement(motionX, motionY, motionZ);
      if (living.distanceTo(this) < 0.5) {
         this.hitEntity(living);
         this.setHomingTarget(null);
      }
   }

   protected void onHitEntity(EntityHitResult pResult) {
      this.hitEntity(pResult.getEntity());
      if (this.isInvisible()) {
         this.discard();
      } else if (this.getSourceItem().getComponents().has((DataComponentType)TensuraDataComponents.DUMMY_ITEM.get())) {
         this.discard();
      }
   }

   private DamageSource getDamageSource() {
      ItemStack itemStack = this.getSourceItem();
      if (itemStack.is(TensuraItemTags.ELEMENTAL_CORES) && itemStack.getItem() instanceof ElementCoreItem core) {
         ResourceKey<DamageType> type = core.getElement().getDefaultDamage();
         return TensuraDamageTypes.getIndirectEntityDamageSource(this.level(), type != null ? type : DamageTypes.THROWN, this.getOwner(), this)
            .tensura$setAbilityInstance(this.getSkill())
            .tensura$setAbilityMode(this.getMode())
            .tensura$setMagicType(Magic.MagicType.SPIRITUAL)
            .tensura$setElement(core.getElement());
      } else {
         return TensuraDamageTypes.getIndirectEntityDamageSource(this.level(), DamageTypes.THROWN, this.getOwner(), this)
            .tensura$setAbilityInstance(this.getSkill())
            .tensura$setAbilityMode(this.getMode());
      }
   }

   public void hitEntity(Entity entity) {
      ItemStack sourceStack = this.getSourceItem();
      if (this.level() instanceof ServerLevel level) {
         Item var12 = sourceStack.getItem();
         float damage = this.baseDamage + TensuraDamageHelper.getWeaponBaseDamage(sourceStack, EquipmentSlotGroup.MAINHAND);
         Entity ownerEntity = this.getOwner();
         DamageSource damagesource = this.getDamageSource();
         if (!sourceStack.is(TensuraItemTags.HIPOKUTE_POTIONS) && !sourceStack.is(TensuraItemTags.ARCANE_POTIONS) && !(var12 instanceof OrbOfDominationItem)) {
            damage = EnchantmentHelper.modifyDamage(level, sourceStack, entity, damagesource, damage);
         } else {
            damage = 1.0F;
         }

         if (entity.hurt(damagesource, damage)) {
            if (entity.getType() == EntityType.ENDERMAN) {
               return;
            }

            if (entity instanceof LivingEntity target) {
               if (this.applySpecialItemEffects(target)) {
                  this.discard();
               }

               if (ownerEntity instanceof LivingEntity owner) {
                  var12.hurtEnemy(sourceStack, target, owner);
                  if (owner instanceof Player player) {
                     var12.interactLivingEntity(sourceStack, player, target, InteractionHand.MAIN_HAND);
                  } else if (owner instanceof ShinjiTanimuraEntity shinji) {
                     shinji.applyVirus(target);
                  }

                  EnchantmentHelper.doPostAttackEffectsWithItemSource(level, entity, damagesource, sourceStack);
                  TensuraEnchantmentHelper.doAdditionalAfterDamage(level, entity, owner, damagesource, sourceStack, damage);
                  if (this.getSourceItem().getCount() <= 0) {
                     this.discard();
                  }
               }

               this.doPostHurtEffects(target);
            }
         }

         if (entity.getType() != EntityType.ENDERMAN && ownerEntity instanceof LivingEntity livingOwner && entity instanceof LivingEntity livingEntity) {
            TensuraEnchantmentHelper.doAdditionalAfterAttack(level, livingEntity, livingOwner, damagesource, sourceStack, damage);
         }

         this.piercingEntity++;
         if (this.piercingEntity >= TensuraEnchantmentHelper.getEnchantmentLevel(level, Enchantments.PIERCING, sourceStack)) {
            this.finishPiercing = true;
            this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01, -0.1, -0.01));
            this.playSound(SoundEvents.TRIDENT_HIT, 1.0F, 1.0F);
         }
      }
   }

   protected boolean applySpecialItemEffects(LivingEntity target) {
      ItemStack sourceStack = this.getSourceItem();
      Item item = sourceStack.getItem();
      if (item.equals(Items.COBWEB)) {
         MobEffectInstance webbed = new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.WEBBED), 100, 0, true, false, true);
         target.addEffect(webbed, this.getOwner());
         return true;
      }

      if (item.equals(TensuraBlocks.Items.STICKY_COBWEB.get()) || item.equals(TensuraBlocks.Items.STICKY_STEEL_COBWEB.get())) {
         target.addEffect(new MobEffectInstance(TensuraMobEffects.getReference(TensuraMobEffects.WEBBED), 200, 0, true, false, true));
         return true;
      }

      if (!(item instanceof PotionItem)) {
         if (item.equals(Items.FIRE_CHARGE)) {
            target.setRemainingFireTicks(100);
            return true;
         } else {
            return this.spawnEntityItem(sourceStack, this.level(), this.blockPosition(), null, target);
         }
      } else {
         if (!this.level().isClientSide) {
            for (MobEffectInstance instance : ((PotionContents)sourceStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)).getAllEffects()) {
               if (((MobEffect)instance.getEffect().value()).isInstantenous()) {
                  ((MobEffect)instance.getEffect().value()).applyInstantenousEffect(this, this.getOwner(), target, instance.getAmplifier(), 1.0);
               } else {
                  TensuraMobEffect.addEffect(target, new MobEffectInstance(instance), this.getOwner(), null);
               }
            }
         }

         this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.SPLASH_POTION_BREAK, TensuraSkill.ABILITY_SOUND, 1.0F, 1.0F);
         return true;
      }
   }

   private boolean spawnEntityItem(ItemStack itemstack, Level pLevel, BlockPos blockpos, @Nullable BlockHitResult hitResult, @Nullable LivingEntity target) {
      if (itemstack.getItem() instanceof DispensibleContainerItem item) {
         Player owner = this.getOwner() instanceof Player player ? player : null;
         item.checkExtraContent(owner, pLevel, itemstack, blockpos);
         if (owner != null && target != null) {
            ThoughtCommunicationSkill.attackCommand(this, owner, target, 3.0);
         }

         if (owner != null && !owner.isCreative() && item instanceof BucketItem) {
            pLevel.addFreshEntity(new ItemEntity(pLevel, blockpos.getX(), blockpos.getY(), blockpos.getZ(), BucketItem.getEmptySuccessItem(itemstack, owner)));
         }

         if (!TensuraSkill.BASE_CONFIG.Misc.thrownLiquid) {
            return true;
         }

         if (!this.shouldGrief()) {
            return true;
         }

         if (!((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
               .grief(this.getSkill(), this.level(), this.getOwner(), blockpos.getX(), blockpos.getY(), blockpos.getZ())
               .isFalse()
            && item.emptyContents(owner, pLevel, blockpos, hitResult)) {
            if (owner != null) {
               if (owner instanceof ServerPlayer serverPlayer) {
                  CriteriaTriggers.PLACED_BLOCK.trigger(serverPlayer, blockpos, itemstack);
               }

               owner.awardStat(Stats.ITEM_USED.get(itemstack.getItem()));
            }

            ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker())
               .grief(this.getSkill(), this.level(), this.getOwner(), blockpos.getX(), blockpos.getY(), blockpos.getZ());
         }

         return true;
      } else {
         return false;
      }
   }

   protected void onHit(HitResult pResult) {
      Item item = this.getSourceItem().getItem();
      if (!item.equals(Items.TNT) && !item.equals(Items.TNT_MINECART) && !item.equals(Items.END_CRYSTAL)) {
         super.onHit(pResult);
      } else {
         float radius = item.equals(Items.END_CRYSTAL) ? 4.0F : 3.0F;
         if (this.shouldGrief()) {
            if (!((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
               .grief(this.getSkill(), this.level(), this.getOwner(), this.getX(), this.getY(), this.getZ())
               .isFalse()) {
               this.level().explode(this.getOwner(), this.getX(), this.getY(), this.getZ(), radius, false, ExplosionInteraction.BLOCK);
               ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker())
                  .grief(this.getSkill(), this.level(), this.getOwner(), this.getX(), this.getY(), this.getZ());
            }
         } else {
            this.level().explode(this.getOwner(), this.getX(), this.getY(), this.getZ(), radius, false, ExplosionInteraction.NONE);
         }

         Type type = pResult.getType();
         if (type == Type.ENTITY) {
            this.level().gameEvent(GameEvent.PROJECTILE_LAND, pResult.getLocation(), Context.of(this, null));
         } else if (type == Type.BLOCK) {
            BlockHitResult blockhitresult = (BlockHitResult)pResult;
            BlockPos blockpos = blockhitresult.getBlockPos();
            this.level().gameEvent(GameEvent.PROJECTILE_LAND, blockpos, Context.of(this, this.level().getBlockState(blockpos)));
         }

         this.discard();
      }
   }

   protected void onHitBlock(BlockHitResult pResult) {
      if (this.getMiningHealth() <= 0.0F) {
         super.onHitBlock(pResult);
      }

      ItemStack source = this.getSourceItem();
      if (this.isInvisible()) {
         this.discard();
      } else if (source.getComponents().has((DataComponentType)TensuraDataComponents.DUMMY_ITEM.get())) {
         this.discard();
      } else if (!(source.getItem() instanceof PotionItem) && !source.is(TensuraItemTags.HIPOKUTE_POTIONS) && !source.is(TensuraItemTags.ARCANE_POTIONS)) {
         if (this.getMiningHealth() > 0.0F && this.getMaxBrokenBlocks() > 0) {
            BlockPos pos = pResult.getBlockPos();
            float miningHealth = this.getMiningHealth();
            BlockState state = this.level().getBlockState(pos);
            Tool tool = (Tool)source.get(DataComponents.TOOL);
            float cost = state.getBlock().defaultDestroyTime() * 30.0F;
            boolean unbreakable = state.getBlock().defaultDestroyTime() <= -1.0F;
            if (tool == null || unbreakable || !tool.isCorrectForDrops(state) && state.requiresCorrectToolForDrops() || !(miningHealth >= cost)) {
               this.continueBreaking = false;
               super.onHitBlock(pResult);
            } else if (this.shouldGrief()) {
               if (!((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
                  .grief(this.getSkill(), this.level(), this.getOwner(), pos.getX(), pos.getY(), pos.getZ())
                  .isFalse()) {
                  this.continueBreaking = true;
                  this.setMiningHealth(miningHealth - cost);
                  this.level().destroyBlock(pos, true, this.getOwner());
                  if (this.getOwner() instanceof Player player) {
                     state.getBlock().playerWillDestroy(this.level(), pos, state, player);
                  }

                  ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker())
                     .grief(this.getSkill(), this.level(), this.getOwner(), pos.getX(), pos.getY(), pos.getZ());
               }

               if (this.brokenBlocks++ >= this.getMaxBrokenBlocks()) {
                  this.setMiningHealth(0.0F);
                  this.continueBreaking = false;
                  super.onHitBlock(pResult);
               }
            }
         } else if (!this.level().isClientSide()) {
            BlockPos blockpos = pResult.getBlockPos().relative(pResult.getDirection());
            if (source.getItem() instanceof FireChargeItem) {
               if (this.shouldGrief() && this.level().isEmptyBlock(blockpos)) {
                  if (!((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
                     .grief(this.getSkill(), this.level(), this.getOwner(), blockpos.getX(), blockpos.getY(), blockpos.getZ())
                     .isFalse()) {
                     this.level().setBlockAndUpdate(blockpos, BaseFireBlock.getState(this.level(), blockpos));
                     ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker())
                        .grief(this.getSkill(), this.level(), this.getOwner(), blockpos.getX(), blockpos.getY(), blockpos.getZ());
                  }

                  this.discard();
               }
            } else if (this.spawnEntityItem(source, this.level(), blockpos, pResult, null)) {
               this.discard();
            }
         }
      } else {
         this.discard();
      }
   }

   public void playerTouch(Player player) {
      if (!this.level().isClientSide && (this.inGround || this.isNoPhysics()) && this.tryPickup(player)) {
         player.take(this, 1);
         this.discard();
      }
   }

   protected boolean tryPickup(Player pPlayer) {
      return super.tryPickup(pPlayer) || this.isNoPhysics() && pPlayer.addItem(this.getPickupItem());
   }

   @NotNull
   protected SoundEvent getDefaultHitGroundSoundEvent() {
      return SoundEvents.ITEM_BREAK;
   }

   public ItemStack getSourceItem() {
      ItemStack stack = (ItemStack)this.entityData.get(SOURCE_ITEM);
      return stack != null && !stack.isEmpty() ? stack : Items.DIRT.getDefaultInstance();
   }

   public void setSourceItem(ItemStack pStack) {
      this.entityData.set(SOURCE_ITEM, pStack);
   }

   public int getLoyaltyLevel() {
      return (Integer)this.entityData.get(LOYALTY_LEVEL);
   }

   public void setLoyaltyLevel(int level) {
      this.entityData.set(LOYALTY_LEVEL, level);
   }

   public float getMiningHealth() {
      return (Float)this.entityData.get(MINING_HEALTH);
   }

   public void setMiningHealth(float level) {
      this.entityData.set(MINING_HEALTH, level);
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(LOYALTY_LEVEL, 0);
      builder.define(MINING_HEALTH, 0.0F);
      builder.define(SOURCE_ITEM, Items.DIRT.getDefaultInstance());
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.finishPiercing = compound.getBoolean("DealtDamage");
      this.setLoyaltyLevel(compound.getInt("Loyalty"));
      this.brokenBlocks = compound.getInt("BrokenBlocks");
      this.setMiningHealth(compound.getFloat("MiningHealth"));
      this.setMaxBrokenBlocks(compound.getInt("MaxBrokenBlocks"));
      if (compound.contains("sourceItem", 10)) {
         ItemStack parsed = ItemStack.parse(this.registryAccess(), compound.getCompound("sourceItem")).orElse(Items.DIRT.getDefaultInstance());
         if (!parsed.isEmpty()) {
            this.setSourceItem(parsed);
         }
      }

      this.setMode(compound.getInt("Mode"));
      if (compound.contains("skill") && compound.get("skill") instanceof CompoundTag tag) {
         this.skill = ManasSkillInstance.fromNBT(tag);
      } else {
         this.skill = null;
      }
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      this.setPickupItemStack(this.getSourceItem());
      super.addAdditionalSaveData(compound);
      compound.putBoolean("DealtDamage", this.finishPiercing);
      compound.putInt("Loyalty", this.getLoyaltyLevel());
      compound.putInt("BrokenBlocks", this.brokenBlocks);
      compound.putFloat("MiningHealth", this.getMiningHealth());
      compound.putInt("MaxBrokenBlocks", this.getMaxBrokenBlocks());
      compound.put("sourceItem", this.getSourceItem().saveOptional(this.registryAccess()));
      compound.putInt("Mode", this.getMode());
      if (this.skill != null) {
         compound.put("skill", this.skill.toNBT());
      }
   }

   protected float getWaterInertia() {
      return 0.6F + TensuraEnchantmentHelper.getEnchantmentLevel(this.level(), Enchantments.IMPALING, this.getSourceItem()) * 0.05F;
   }

   @Generated
   public int getMode() {
      return this.mode;
   }

   @Generated
   public void setMode(int mode) {
      this.mode = mode;
   }

   @Generated
   public ManasSkillInstance getSkill() {
      return this.skill;
   }

   @Generated
   public void setSkill(ManasSkillInstance skill) {
      this.skill = skill;
   }

   @Generated
   public int getMaxBrokenBlocks() {
      return this.maxBrokenBlocks;
   }

   @Generated
   public void setMaxBrokenBlocks(int maxBrokenBlocks) {
      this.maxBrokenBlocks = maxBrokenBlocks;
   }

   @Generated
   public void setHomingTarget(Entity homingTarget) {
      this.homingTarget = homingTarget;
   }

   @Generated
   public Entity getHomingTarget() {
      return this.homingTarget;
   }
}
