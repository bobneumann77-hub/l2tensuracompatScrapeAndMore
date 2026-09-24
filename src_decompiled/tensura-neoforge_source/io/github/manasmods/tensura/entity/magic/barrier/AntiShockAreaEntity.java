package io.github.manasmods.tensura.entity.magic.barrier;

import io.github.manasmods.tensura.ability.TensuraSkill;
import io.github.manasmods.tensura.registry.effect.TensuraMobEffects;
import io.github.manasmods.tensura.registry.entity.MiscEntityTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

public class AntiShockAreaEntity extends BarrierEntity implements GeoEntity {
   public static final ResourceLocation[] ANTI_SHOCK_AREA = new ResourceLocation[]{
      ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/barrier/anti_shock_area_start_0.png"),
      ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/barrier/anti_shock_area_start_1.png"),
      ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/barrier/anti_shock_area_start_2.png"),
      ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/barrier/anti_shock_area_start_3.png"),
      ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/barrier/anti_shock_area_start_4.png"),
      ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/barrier/anti_shock_area_start_5.png"),
      ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/barrier/anti_shock_area_start_6.png"),
      ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/barrier/anti_shock_area_start_7.png"),
      ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/barrier/anti_shock_area_start_8.png"),
      ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/barrier/anti_shock_area_start_9.png"),
      ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/barrier/anti_shock_area_start_10.png"),
      ResourceLocation.fromNamespaceAndPath("tensura", "textures/entity/barrier/anti_shock_area_start_11.png")
   };
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

   public AntiShockAreaEntity(EntityType<? extends AntiShockAreaEntity> entityType, Level level) {
      super(entityType, level);
   }

   public AntiShockAreaEntity(Level level, LivingEntity entity) {
      this((EntityType<? extends AntiShockAreaEntity>)MiscEntityTypes.ANTI_SHOCK_AREA.get(), level);
      this.setOwner(entity);
   }

   @Override
   public boolean shouldCreateParts() {
      return false;
   }

   @Override
   public boolean shouldRenderAtSqrDistance(double pDistance) {
      return true;
   }

   @Override
   public boolean hurt(DamageSource pSource, float pAmount) {
      return false;
   }

   @Override
   protected void updateVisualSize() {
   }

   @Override
   public void tick() {
      super.tick();
      if (this.level().isClientSide()) {
         if (this.tickCount % 80 == 0 || this.tickCount == 1) {
            this.level()
               .playLocalSound(
                  this.getX(),
                  this.getY() + this.getSize(),
                  this.getZ(),
                  SoundEvents.BEACON_AMBIENT,
                  TensuraSkill.ABILITY_SOUND,
                  0.2F * this.getSize(),
                  1.0F,
                  true
               );
         }
      }
   }

   @Override
   public void applyEffect(LivingEntity entity) {
      super.applyEffect(entity);
      MobEffectInstance antiShock = new MobEffectInstance(
         TensuraMobEffects.getReference(TensuraMobEffects.ANTI_SHOCK), this.tickEachHit + 5, 0, true, false, true
      );
      entity.addEffect(antiShock, this.getOwner());
   }

   public void registerControllers(ControllerRegistrar controllers) {
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }
}
