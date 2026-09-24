package io.github.manasmods.tensura.entity.magic.field.beam;

import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.RaceAPI;
import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.data.TensuraRaceTags;
import io.github.manasmods.tensura.entity.magic.field.AreaField;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import java.util.Optional;
import lombok.Generated;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class SummoningBeam extends AreaField implements GeoEntity {
   protected double minEP = 0.0;
   protected double maxEP = 10000.0;
   protected Player foundSummon = null;
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public SummoningBeam(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.noCulling = true;
   }

   public SummoningBeam(Level level, Entity entity) {
      this((EntityType<? extends Projectile>)MiscEntityTypes.SUMMONING_BEAM.get(), level);
      this.setOwner(entity);
   }

   @NotNull
   @Override
   public EntityDimensions getDimensions(Pose pPose) {
      return EntityDimensions.scalable(this.getSize() * 2.0F, this.getSize() * 2.0F);
   }

   public boolean shouldRenderAtSqrDistance(double d) {
      return true;
   }

   @Override
   public void tick() {
      super.tick();
      if (this.level().isClientSide()) {
         if (this.tickCount % 80 == 0 || this.tickCount == 1) {
            this.level()
               .playLocalSound(
                  this.getX(), this.getY() + this.getSize(), this.getZ(), SoundEvents.BEACON_AMBIENT, TensuraSkill.ABILITY_SOUND, this.getSize(), 1.0F, true
               );
         }
      }
   }

   public void findSummon() {
      if (!this.level().isClientSide() && this.getFoundSummon() == null) {
         for (ServerPlayer target : this.level().getEntitiesOfClass(ServerPlayer.class, this.getBoundingBox())) {
            if (this.getFoundSummon() == null) {
               IExistence existence = TensuraStorages.getExistenceFrom(target);
               if (existence.isSpiritualForm()
                  && existence.getSummoner() == null
                  && existence.getTemporaryOwner() == null
                  && existence.getPermanentOwner() == null) {
                  Optional<ManasRaceInstance> optional = RaceAPI.getRaceFrom(target).getRace();
                  if (!optional.isEmpty() && optional.get().is(TensuraRaceTags.DAEMON)) {
                     double EP = EnergyHelper.getMaxEP(target);
                     if (!(EP < this.getMinEP()) && !(EP > this.getMaxEP())) {
                        this.setFoundSummon(target);
                     }
                  }
               }
            }
         }
      }
   }

   public void remove(RemovalReason removalReason) {
      this.setRemoved(removalReason);
      if (this.level() instanceof ServerLevel level) {
         level.setChunkForced(this.blockPosition().getX(), this.blockPosition().getZ(), false);
      }
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(
         new AnimationController(
            this,
            "controller",
            1,
            event -> {
               if (this.getAge() < 25) {
                  return event.setAndContinue(RawAnimation.begin().thenPlay("animation.magic_beam.start"));
               } else {
                  return this.getLife() - this.getAge() < 25
                     ? event.setAndContinue(RawAnimation.begin().thenPlayAndHold("animation.magic_beam.end"))
                     : event.setAndContinue(RawAnimation.begin().thenLoop("animation.magic_beam.loop"));
               }
            }
         )
      );
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   @Generated
   public double getMinEP() {
      return this.minEP;
   }

   @Generated
   public void setMinEP(double minEP) {
      this.minEP = minEP;
   }

   @Generated
   public double getMaxEP() {
      return this.maxEP;
   }

   @Generated
   public void setMaxEP(double maxEP) {
      this.maxEP = maxEP;
   }

   @Generated
   public Player getFoundSummon() {
      return this.foundSummon;
   }

   @Generated
   public void setFoundSummon(Player foundSummon) {
      this.foundSummon = foundSummon;
   }
}
