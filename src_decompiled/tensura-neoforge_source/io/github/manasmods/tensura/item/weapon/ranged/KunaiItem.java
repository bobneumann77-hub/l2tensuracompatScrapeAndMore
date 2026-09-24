package io.github.manasmods.tensura.item.weapon.ranged;

import io.github.manasmods.tensura.entity.projectile.KunaiProjectile;
import io.github.manasmods.tensura.item.weapon.TensuraSwordItem;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import lombok.Generated;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class KunaiItem extends ProjectileWeaponItem implements ProjectileItem {
   protected float baseProjectileDamage;

   public KunaiItem(Tier pTier, float baseProjectileDamage, Properties pProperties) {
      super(pProperties.attributes(TensuraSwordItem.createAttributes(pTier, 1, -2.3F, -1.0, -1.0, 0.0, 0.5)));
      this.baseProjectileDamage = baseProjectileDamage;
      DispenserBlock.registerProjectileBehavior(this);
   }

   @NotNull
   public UseAnim getUseAnimation(ItemStack pStack) {
      return UseAnim.SPEAR;
   }

   public int getUseDuration(ItemStack itemStack, LivingEntity entity) {
      return 10000;
   }

   public float getPowerForChargeTime(int i) {
      float f = i / 20.0F;
      f = (f * f + f * 2.0F) / 3.0F;
      if (f > 1.0F) {
         f = 1.0F;
      }

      return f;
   }

   public void releaseUsing(ItemStack itemStack, Level level, LivingEntity entity, int i) {
      if (entity instanceof Player player) {
         int j = this.getUseDuration(itemStack, entity) - i;
         float f = this.getPowerForChargeTime(j);
         if (f >= 0.1) {
            ItemStack copy = itemStack.copy();
            List<ItemStack> list = this.draw(itemStack, player);
            if (level instanceof ServerLevel serverLevel && !list.isEmpty()) {
               this.shoot(serverLevel, player, player.getUsedItemHand(), itemStack, copy, list, f * 3.0F, 1.0F, null);
            }

            level.playSound(
               null,
               player.getX(),
               player.getY(),
               player.getZ(),
               SoundEvents.ARROW_SHOOT,
               SoundSource.PLAYERS,
               1.0F,
               1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F
            );
            player.awardStat(Stats.ITEM_USED.get(this));
         }
      }
   }

   protected List<ItemStack> draw(ItemStack itemStack, LivingEntity entity) {
      if (itemStack.isEmpty()) {
         return List.of();
      }

      int amount;
      if (entity.level() instanceof ServerLevel level) {
         amount = EnchantmentHelper.processProjectileCount(level, itemStack, entity, 1);
      } else {
         amount = 1;
      }

      int i = amount;
      List<ItemStack> list = new ArrayList<>(i);

      for (int j = 0; j < i; j++) {
         list.add(itemStack.copyWithCount(1));
      }

      if (this.getDurabilityUse(itemStack) <= 0 && !entity.hasInfiniteMaterials()) {
         itemStack.shrink(1);
      }

      return list;
   }

   protected Projectile createProjectile(Level level, LivingEntity entity, InteractionHand hand, ItemStack itemStack, ItemStack source, boolean canPick) {
      boolean left = hand == InteractionHand.OFF_HAND && entity.getMainArm() == HumanoidArm.RIGHT
         || hand == InteractionHand.MAIN_HAND && entity.getMainArm() == HumanoidArm.LEFT;
      KunaiProjectile kunai = new KunaiProjectile(level, entity, itemStack, !left);
      if (!canPick) {
         kunai.pickup = Pickup.CREATIVE_ONLY;
      }

      return kunai;
   }

   protected int getDurabilityUse(ItemStack itemStack) {
      return 0;
   }

   @NotNull
   public Predicate<ItemStack> getAllSupportedProjectiles() {
      return stack -> stack.is(this);
   }

   public int getDefaultProjectileRange() {
      return 10;
   }

   protected void shoot(
      ServerLevel level,
      LivingEntity entity,
      InteractionHand hand,
      ItemStack stack,
      ItemStack copy,
      List<ItemStack> list,
      float f,
      float g,
      @Nullable LivingEntity living
   ) {
      float h = EnchantmentHelper.processProjectileSpread(level, stack.isEmpty() ? copy : stack, entity, 0.0F);
      float i = list.size() == 1 ? 0.0F : 2.0F * h / (list.size() - 1);
      float j = (list.size() - 1) % 2 * i / 2.0F;
      float k = 1.0F;

      for (int l = 0; l < list.size(); l++) {
         ItemStack ammo = list.get(l);
         if (!ammo.isEmpty()) {
            float m = j + k * ((l + 1) / 2) * i;
            k = -k;
            Projectile projectile = this.createProjectile(level, entity, hand, ammo, stack.isEmpty() ? copy : stack, l == 0 && !entity.hasInfiniteMaterials());
            this.shootProjectile(entity, projectile, l, f, g, m, living);
            level.addFreshEntity(projectile);
            if (this.getDurabilityUse(stack) > 0) {
               stack.hurtAndBreak(this.getDurabilityUse(stack), entity, LivingEntity.getSlotForHand(hand));
               if (stack.isEmpty()) {
                  break;
               }
            }
         }
      }
   }

   protected void shootProjectile(LivingEntity livingEntity, Projectile projectile, int i, float f, float g, float h, @Nullable LivingEntity livingEntity2) {
      Vector3f vector3f;
      if (livingEntity2 != null) {
         double d = livingEntity2.getX() - livingEntity.getX();
         double e = livingEntity2.getZ() - livingEntity.getZ();
         double j = Math.sqrt(d * d + e * e);
         double k = livingEntity2.getY(0.3333333333333333) - projectile.getY() + j * 0.2F;
         vector3f = getProjectileShotVector(livingEntity, new Vec3(d, k, e), h);
      } else {
         Vec3 vec3 = livingEntity.getUpVector(1.0F);
         Quaternionf quaternionf = new Quaternionf().setAngleAxis(h * (float) (Math.PI / 180.0), vec3.x, vec3.y, vec3.z);
         Vec3 vec32 = livingEntity.getViewVector(1.0F);
         vector3f = vec32.toVector3f().rotate(quaternionf);
      }

      projectile.shoot(vector3f.x(), vector3f.y(), vector3f.z(), f, g);
      float l = getShotPitch(livingEntity.getRandom(), i);
      livingEntity.level()
         .playSound(
            null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, livingEntity.getSoundSource(), 1.0F, l
         );
   }

   private static float getShotPitch(RandomSource randomSource, int i) {
      return i == 0 ? 1.0F : getRandomShotPitch((i & 1) == 1, randomSource);
   }

   private static float getRandomShotPitch(boolean bl, RandomSource randomSource) {
      float f = bl ? 0.63F : 0.43F;
      return 1.0F / (randomSource.nextFloat() * 0.5F + 1.8F) + f;
   }

   private static Vector3f getProjectileShotVector(LivingEntity livingEntity, Vec3 vec3, float f) {
      Vector3f vector3f = vec3.toVector3f().normalize();
      Vector3f vector3f2 = new Vector3f(vector3f).cross(new Vector3f(0.0F, 1.0F, 0.0F));
      if (vector3f2.lengthSquared() <= 1.0E-7) {
         Vec3 vec32 = livingEntity.getUpVector(1.0F);
         vector3f2 = new Vector3f(vector3f).cross(vec32.toVector3f());
      }

      Vector3f vector3f3 = new Vector3f(vector3f).rotateAxis((float) (Math.PI / 2), vector3f2.x, vector3f2.y, vector3f2.z);
      return new Vector3f(vector3f).rotateAxis(f * (float) (Math.PI / 180.0), vector3f3.x, vector3f3.y, vector3f3.z);
   }

   @NotNull
   public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
      ItemStack itemstack = pPlayer.getItemInHand(pHand);
      pPlayer.startUsingItem(pHand);
      return InteractionResultHolder.consume(itemstack);
   }

   public Projectile asProjectile(Level level, Position position, ItemStack itemStack, Direction direction) {
      KunaiProjectile kunai = new KunaiProjectile(level, position.x(), position.y(), position.z(), itemStack.copyWithCount(1), null);
      kunai.pickup = Pickup.ALLOWED;
      return kunai;
   }

   @Generated
   public float getBaseProjectileDamage() {
      return this.baseProjectileDamage;
   }
}
