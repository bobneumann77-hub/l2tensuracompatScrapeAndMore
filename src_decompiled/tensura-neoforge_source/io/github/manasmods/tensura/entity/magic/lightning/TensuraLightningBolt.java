package io.github.manasmods.tensura.entity.magic.lightning;

import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.magic.Magic;
import io.github.manasmods.tensura.damage.TensuraDamageHelper;
import io.github.manasmods.tensura.damage.TensuraDamageTypes;
import io.github.manasmods.tensura.data.TensuraBlockTags;
import io.github.manasmods.tensura.event.TensuraSkillEvents;
import io.github.manasmods.tensura.particle.TensuraParticleHelper;
import io.github.manasmods.tensura.particle.TensuraParticleUtils;
import io.github.manasmods.tensura.registry.particle.TensuraParticleTypes;
import io.github.manasmods.tensura.storage.effect.EffectStorage;
import io.github.manasmods.tensura.world.TensuraGameRules;
import lombok.Generated;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class TensuraLightningBolt extends net.minecraft.world.entity.LightningBolt {
   private static final EntityDataAccessor<Integer> ADDITIONAL_VISUAL = SynchedEntityData.defineId(TensuraLightningBolt.class, EntityDataSerializers.INT);
   @Nullable
   private Entity owner = null;
   private float tensuraDamage = 5.0F;
   protected float secondaryDamage = 0.0F;
   private float radius = 2.0F;
   protected double apCost = 0.0;
   protected double mpCost = 0.0;
   protected int mode = 0;
   protected ManasSkillInstance skill = null;
   protected boolean shouldPlaceFire = true;
   protected boolean elementalAttack = false;
   protected Element element = null;

   public TensuraLightningBolt(EntityType<? extends net.minecraft.world.entity.LightningBolt> type, Level pLevel) {
      super(type, pLevel);
      this.life = this.getDamageTick() + 1;
      this.setVisualOnly(true);
   }

   public TensuraLightningBolt(EntityType<? extends net.minecraft.world.entity.LightningBolt> type, Level pLevel, Entity owner) {
      super(type, pLevel);
      this.life = this.getDamageTick() + 1;
      this.setOwner(owner);
      this.setVisualOnly(true);
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(ADDITIONAL_VISUAL, 0);
   }

   public void addAdditionalSaveData(CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      compound.putInt("AdditionalVisual", this.getAdditionalVisual());
      compound.putInt("Mode", this.getMode());
      compound.putDouble("APCost", this.getApCost());
      compound.putDouble("MPCost", this.getMpCost());
      compound.putBoolean("ElementalAttack", this.isElementalAttack());
      if (this.skill != null) {
         compound.put("skill", this.skill.toNBT());
      }

      if (this.element != null) {
         compound.putInt("element", this.element.getId());
      }
   }

   public void readAdditionalSaveData(CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      this.setAdditionalVisual(compound.getInt("AdditionalVisual"));
      this.setMode(compound.getInt("Mode"));
      this.setApCost(compound.getDouble("APCost"));
      this.setMpCost(compound.getDouble("MPCost"));
      this.setElementalAttack(compound.getBoolean("ElementalAttack"));
      if (compound.contains("skill") && compound.get("skill") instanceof CompoundTag tag) {
         this.skill = ManasSkillInstance.fromNBT(tag);
      }

      if (compound.contains("element")) {
         this.element = Element.byId(compound.getInt("element"));
      }
   }

   public int getAdditionalVisual() {
      return (Integer)this.entityData.get(ADDITIONAL_VISUAL);
   }

   public void setAdditionalVisual(int i) {
      this.entityData.set(ADDITIONAL_VISUAL, i);
   }

   public void setSkill(LivingEntity entity, ManasSkillInstance instance, TensuraSkill skill, int mode) {
      this.setSkill(instance);
      this.setApCost(skill.getAuraCost(entity, instance, mode));
      this.setMpCost(skill.getMagiculeCost(entity, instance, mode));
   }

   public float getDamage() {
      return 0.0F;
   }

   protected BlockState getFireBlock() {
      return Blocks.FIRE.defaultBlockState();
   }

   public ResourceKey<DamageType> getDamageType() {
      return TensuraDamageTypes.LIGHTNING;
   }

   public ResourceKey<DamageType> getSecondaryDamageType() {
      return TensuraDamageTypes.MAGIC_GENERIC;
   }

   protected ParticleOptions getLightningParticle() {
      return (ParticleOptions)TensuraParticleTypes.LIGHTNING_EFFECT.get();
   }

   protected int getDamageTick() {
      return 2;
   }

   protected boolean shouldGrief() {
      return this.getOwner() != null && !this.getOwner().getType().equals(EntityType.PLAYER)
         ? this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)
         : TensuraGameRules.canSkillGrief(this.level());
   }

   protected boolean canHit(Entity entity) {
      if (entity == this) {
         return false;
      } else if (entity == this.getOwner()) {
         return false;
      } else {
         return this.getOwner() != null && this.getOwner().isAlliedTo(entity) ? false : entity.isAlive();
      }
   }

   public void tick() {
      super.tick();
      if (!this.level().isClientSide()) {
         if (this.life == this.getDamageTick()) {
            this.level()
               .playSound(
                  null,
                  this.getX(),
                  this.getY(),
                  this.getZ(),
                  SoundEvents.LIGHTNING_BOLT_THUNDER,
                  TensuraSkill.ABILITY_SOUND,
                  10.0F,
                  0.8F + this.random.nextFloat() * 0.2F
               );
         }

         if (this.life >= 2 && this.life <= this.getDamageTick()) {
            this.level()
               .playSound(
                  null,
                  this.getX(),
                  this.getY(),
                  this.getZ(),
                  SoundEvents.LIGHTNING_BOLT_IMPACT,
                  TensuraSkill.ABILITY_SOUND,
                  2.0F,
                  0.5F + this.random.nextFloat() * 0.2F
               );
            this.spawnImpactParticles();
            this.doAoEDamage();
         }
      }
   }

   protected void spawnImpactParticles() {
      TensuraParticleHelper.spawnServerParticles(
         this.level(), TensuraParticleUtils.getColorlessWave(0.75F, this.getRadius()), this.getX(), this.getY() + 0.5, this.getZ()
      );
      TensuraParticleHelper.addServerParticlesAroundSelf(this, this.getLightningParticle(), this.getRadius());
      TensuraParticleHelper.spawnServerParticles(
         this.level(), this.getLightningParticle(), this.getX(), this.getY(), this.getZ(), 10 * this.getAdditionalVisual(), 0.5, 0.5, 0.5, 1.0, false
      );
   }

   protected void doAoEDamage() {
      float electricRadius = this.isInWaterOrBubble() ? this.radius * 1.5F : this.radius;

      for (Entity target : this.level()
         .getEntitiesOfClass(
            LivingEntity.class, this.getBoundingBox().inflate(electricRadius), entity -> this.canHit(entity) && entity.distanceTo(this) < electricRadius
         )) {
         TensuraParticleHelper.addServerParticlesAroundSelf(target, this.getLightningParticle(), 1.0);
         this.dealDamage(target);
         if (this.level() instanceof ServerLevel serverLevel) {
            target.thunderHit(serverLevel, this);
         }
      }

      if (this.life == this.getDamageTick() && this.shouldPlaceFire && this.shouldGrief()) {
         this.placeFireAndMeltGlass(this.position(), (int)this.radius);
      }
   }

   protected boolean dealDamage(Entity target) {
      return this.dealDamage(target, this.getTensuraDamage(), 1.0F);
   }

   protected boolean dealDamage(Entity target, float damage, float costMultiplier) {
      Magic.MagicType magicType = this.isElementalAttack() ? Magic.MagicType.SPIRITUAL : null;
      DamageSource source = this.getDamageSource(costMultiplier).tensura$setMagicType(magicType);
      if (this.getSecondaryDamage() > 0.0F) {
         Magic.MagicType secondMagicType = this.isElementalAttack() ? Magic.MagicType.ASPECTUAL : null;
         DamageSource secondSource = this.getDamageSource(this.getSecondaryDamageType(), costMultiplier).tensura$setMagicType(secondMagicType);
         return TensuraDamageHelper.hurtDouble(target, source, damage, secondSource, this.getSecondaryDamage());
      } else {
         return damage > 0.0F && target.hurt(source, damage);
      }
   }

   public DamageSource getDamageSource(float costMultiplier) {
      return this.getDamageSource(this.getDamageType(), costMultiplier);
   }

   public DamageSource getDamageSource(ResourceKey<DamageType> type, float costMultiplier) {
      return TensuraDamageTypes.getIndirectEntityDamageSource(this.level(), type, this.getOwner(), this)
         .tensura$setAbilityInstance(this.getSkill())
         .tensura$setAbilityMode(this.getMode())
         .tensura$setElement(this.getElement())
         .tensura$setMagiculeCost(this.getMpCost() * costMultiplier)
         .tensura$setAuraCost(this.getApCost() * costMultiplier);
   }

   protected void placeFireAndMeltGlass(Vec3 pos, int radius) {
      Level level = this.level();
      EffectStorage.setCameraShake(this, radius, 0.1F, 15);
      SkillHelper.launchBlock(
         this,
         this.getOwner(),
         this.position(),
         radius,
         1,
         radius / 12.0F,
         radius / 12.0F,
         blockStatex -> this.random.nextInt(3) != 1 ? false : blockStatex.is(TensuraBlockTags.SKILL_BREAK_EASY),
         blockPos -> true,
         this.getSkill()
      );
      int yPos = Mth.floor(pos.y()) - 1;
      int xPos = Mth.floor(pos.x());
      int zPos = Mth.floor(pos.z());
      boolean placedBlocks = false;
      boolean removedBlocks = false;
      boolean shouldPlaceFire = this.getOwner() instanceof Player;
      MutableBlockPos cursor = new MutableBlockPos();
      MutableBlockPos cursorDown = new MutableBlockPos();
      int radiusSqr = radius * radius;

      for (int j = -radius; j <= radius; j++) {
         for (int k = -radius; k <= radius; k++) {
            for (int i = -2; i <= 2; i++) {
               int newYPos = yPos + i;
               int newXPos = xPos + j;
               int newZPos = zPos + k;
               cursor.set(newXPos, newYPos, newZPos);
               if (!(cursor.distToCenterSqr(pos) > radiusSqr)
                  && this.random.nextInt(3) == 1
                  && !((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_PRE.invoker())
                     .grief(this.getSkill(), this.level(), this.getOwner(), newXPos, newYPos, newZPos)
                     .isFalse()) {
                  BlockState blockState = level.getBlockState(cursor);
                  if (blockState.is(BlockTags.SAND)) {
                     placedBlocks = level.setBlockAndUpdate(cursor.immutable(), Blocks.GLASS.defaultBlockState()) || placedBlocks;
                  } else {
                     if (blockState.canBeReplaced() && blockState.getFluidState().isEmpty()) {
                        cursorDown.set(newXPos, newYPos - 1, newZPos);
                        BlockState blockStateDown = level.getBlockState(cursorDown);
                        if (blockStateDown.isFaceSturdy(level, cursorDown, Direction.UP)) {
                           removedBlocks = level.removeBlock(cursor.immutable(), true) || removedBlocks;
                        }
                     }

                     if (shouldPlaceFire && FireBlock.canBePlacedAt(level, cursor, Direction.UP)) {
                        BlockPos placePos = cursor.immutable();
                        placedBlocks = level.setBlockAndUpdate(placePos, this.getFireBlock()) || placedBlocks;
                        level.scheduleTick(placePos, blockState.getBlock(), 20);
                     }

                     ((TensuraSkillEvents.SkillGriefEvent)TensuraSkillEvents.SKILL_GRIEF_POS.invoker())
                        .grief(this.getSkill(), this.level(), this.getOwner(), newXPos, newYPos, newZPos);
                  }
               }
            }
         }
      }

      if (removedBlocks && this.getOwner() != null) {
         this.level().gameEvent(this.getOwner(), GameEvent.BLOCK_CHANGE, this.blockPosition());
      }

      if (placedBlocks && this.getOwner() != null) {
         this.level().gameEvent(this.getOwner(), GameEvent.BLOCK_CHANGE, this.blockPosition());
      }
   }

   @Nullable
   @Generated
   public Entity getOwner() {
      return this.owner;
   }

   @Generated
   public void setOwner(@Nullable Entity owner) {
      this.owner = owner;
   }

   @Generated
   public float getTensuraDamage() {
      return this.tensuraDamage;
   }

   @Generated
   public void setTensuraDamage(float tensuraDamage) {
      this.tensuraDamage = tensuraDamage;
   }

   @Generated
   public float getSecondaryDamage() {
      return this.secondaryDamage;
   }

   @Generated
   public void setSecondaryDamage(float secondaryDamage) {
      this.secondaryDamage = secondaryDamage;
   }

   @Generated
   public float getRadius() {
      return this.radius;
   }

   @Generated
   public void setRadius(float radius) {
      this.radius = radius;
   }

   @Generated
   public double getApCost() {
      return this.apCost;
   }

   @Generated
   public void setApCost(double apCost) {
      this.apCost = apCost;
   }

   @Generated
   public double getMpCost() {
      return this.mpCost;
   }

   @Generated
   public void setMpCost(double mpCost) {
      this.mpCost = mpCost;
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
   public void setShouldPlaceFire(boolean shouldPlaceFire) {
      this.shouldPlaceFire = shouldPlaceFire;
   }

   @Generated
   public boolean isElementalAttack() {
      return this.elementalAttack;
   }

   @Generated
   public void setElementalAttack(boolean elementalAttack) {
      this.elementalAttack = elementalAttack;
   }

   @Generated
   public Element getElement() {
      return this.element;
   }

   @Generated
   public void setElement(Element element) {
      this.element = element;
   }
}
