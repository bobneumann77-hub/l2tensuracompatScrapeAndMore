package io.github.manasmods.tensura.item.weapon.ranged;

import io.github.manasmods.tensura.entity.projectile.WebBulletProjectile;
import io.github.manasmods.tensura.registry.item.misc.TensuraCreativeTabs;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class WebGunItem extends ProjectileWeaponItem {
   public WebGunItem() {
      super(new Properties().arch$tab(TensuraCreativeTabs.GEARS).durability(340));
   }

   public UseAnim getUseAnimation(ItemStack pStack) {
      return UseAnim.CROSSBOW;
   }

   public int getUseDuration(ItemStack itemStack, LivingEntity entity) {
      return getChargeDuration(itemStack, entity) + 3;
   }

   public static int getChargeDuration(ItemStack itemStack, LivingEntity entity) {
      float f = EnchantmentHelper.modifyCrossbowChargingTime(itemStack, entity, 1.25F);
      return Mth.floor(f * 20.0F);
   }

   public static boolean isCharged(ItemStack itemStack) {
      ChargedProjectiles chargedProjectiles = (ChargedProjectiles)itemStack.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
      return !chargedProjectiles.isEmpty();
   }

   public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
      ChargedProjectiles chargedProjectiles = (ChargedProjectiles)itemStack.get(DataComponents.CHARGED_PROJECTILES);
      if (chargedProjectiles != null && !chargedProjectiles.isEmpty()) {
         ItemStack ammo = (ItemStack)chargedProjectiles.getItems().get(0);
         list.add(Component.translatable("item.minecraft.crossbow.projectile").append(CommonComponents.SPACE).append(ammo.getDisplayName()));
      }
   }

   public boolean useOnRelease(ItemStack itemStack) {
      return itemStack.is(this);
   }

   @NotNull
   public Predicate<ItemStack> getAllSupportedProjectiles() {
      return stack -> stack.getItem() instanceof WebCartridgeItem;
   }

   public int getDefaultProjectileRange() {
      return 7;
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
      ItemStack itemStack = player.getItemInHand(interactionHand);
      ChargedProjectiles chargedProjectiles = (ChargedProjectiles)itemStack.get(DataComponents.CHARGED_PROJECTILES);
      if (chargedProjectiles != null && !chargedProjectiles.isEmpty()) {
         this.performShooting(level, player, interactionHand, itemStack, 2.0F, 1.0F, null);
         return InteractionResultHolder.consume(itemStack);
      } else if (!player.getProjectile(itemStack).isEmpty()) {
         player.startUsingItem(interactionHand);
         return InteractionResultHolder.consume(itemStack);
      } else {
         return InteractionResultHolder.fail(itemStack);
      }
   }

   public void releaseUsing(ItemStack itemStack, Level level, LivingEntity livingEntity, int i) {
      int j = this.getUseDuration(itemStack, livingEntity) - i;
      float f = getPowerForTime(j, itemStack, livingEntity);
      if (f >= 1.0F && !isCharged(itemStack) && tryLoadProjectiles(livingEntity, itemStack)) {
         level.playSound(
            null,
            livingEntity.getX(),
            livingEntity.getY(),
            livingEntity.getZ(),
            SoundEvents.CROSSBOW_LOADING_END,
            livingEntity.getSoundSource(),
            1.0F,
            1.0F / (level.getRandom().nextFloat() * 0.5F + 1.0F) + 0.2F
         );
      }
   }

   private static float getPowerForTime(int i, ItemStack itemStack, LivingEntity livingEntity) {
      float f = (float)i / getChargeDuration(itemStack, livingEntity);
      if (f > 1.0F) {
         f = 1.0F;
      }

      return f;
   }

   private static boolean tryLoadProjectiles(LivingEntity livingEntity, ItemStack itemStack) {
      List<ItemStack> list = draw(itemStack, livingEntity.getProjectile(itemStack), livingEntity);
      if (!list.isEmpty()) {
         itemStack.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.of(list));
         return true;
      } else {
         return false;
      }
   }

   public void performShooting(
      Level level, LivingEntity livingEntity, InteractionHand interactionHand, ItemStack itemStack, float f, float g, @Nullable LivingEntity livingEntity2
   ) {
      if (level instanceof ServerLevel serverLevel) {
         ChargedProjectiles chargedProjectiles = (ChargedProjectiles)itemStack.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
         if (chargedProjectiles != null && !chargedProjectiles.isEmpty()) {
            this.shoot(
               serverLevel, livingEntity, interactionHand, itemStack, chargedProjectiles.getItems(), f, g, livingEntity instanceof Player, livingEntity2
            );
            if (livingEntity instanceof ServerPlayer player) {
               CriteriaTriggers.SHOT_CROSSBOW.trigger(player, itemStack);
               player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
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
         .playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), SoundEvents.CROSSBOW_SHOOT, livingEntity.getSoundSource(), 1.0F, l);
   }

   private static Vector3f getProjectileShotVector(LivingEntity livingEntity, Vec3 vec3, float f) {
      Vector3f normalize = vec3.toVector3f().normalize();
      Vector3f cross = new Vector3f(normalize).cross(new Vector3f(0.0F, 1.0F, 0.0F));
      if (cross.lengthSquared() <= 1.0E-7) {
         Vec3 vec32 = livingEntity.getUpVector(1.0F);
         cross = new Vector3f(normalize).cross(vec32.toVector3f());
      }

      Vector3f rotated = new Vector3f(normalize).rotateAxis((float) (Math.PI / 2), cross.x, cross.y, cross.z);
      return new Vector3f(normalize).rotateAxis(f * (float) (Math.PI / 180.0), rotated.x, rotated.y, rotated.z);
   }

   protected Projectile createProjectile(Level level, LivingEntity entity, ItemStack gun, ItemStack ammo, boolean bl) {
      boolean left = entity.getUsedItemHand() == InteractionHand.OFF_HAND && entity.getMainArm() == HumanoidArm.RIGHT
         || entity.getUsedItemHand() == InteractionHand.MAIN_HAND && entity.getMainArm() == HumanoidArm.LEFT;
      return new WebBulletProjectile(level, entity, !left, gun, ammo);
   }

   private static float getShotPitch(RandomSource randomSource, int i) {
      return i == 0 ? 1.0F : getRandomShotPitch((i & 1) == 1, randomSource);
   }

   private static float getRandomShotPitch(boolean bl, RandomSource randomSource) {
      float f = bl ? 0.63F : 0.43F;
      return 1.0F / (randomSource.nextFloat() * 0.5F + 1.8F) + f;
   }
}
