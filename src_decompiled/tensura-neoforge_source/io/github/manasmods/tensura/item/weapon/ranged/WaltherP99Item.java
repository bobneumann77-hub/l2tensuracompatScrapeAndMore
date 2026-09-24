package io.github.manasmods.tensura.item.weapon.ranged;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.SkillUtils;
import io.github.manasmods.tensura.ability.magic.Element;
import io.github.manasmods.tensura.ability.skill.unique.SniperSkill;
import io.github.manasmods.tensura.entity.projectile.magic.BulletProjectile;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.registry.item.TensuraToolItems;
import io.github.manasmods.tensura.registry.item.misc.TensuraCreativeTabs;
import io.github.manasmods.tensura.registry.item.misc.TensuraDataComponents;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.ItemHelper;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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

public class WaltherP99Item extends ProjectileWeaponItem {
   public WaltherP99Item() {
      super(new Properties().arch$tab(TensuraCreativeTabs.GEARS).durability(500));
   }

   public UseAnim getUseAnimation(ItemStack pStack) {
      return UseAnim.BOW;
   }

   public int getUseDuration(ItemStack itemStack, LivingEntity entity) {
      return getChargeDuration(itemStack, entity) + 3;
   }

   public static int getChargeDuration(ItemStack itemStack, LivingEntity entity) {
      float f = EnchantmentHelper.modifyCrossbowChargingTime(itemStack, entity, 0.5F);
      return Mth.floor(f * 20.0F);
   }

   public static boolean isCharged(ItemStack itemStack) {
      return WebGunItem.isCharged(itemStack);
   }

   public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
      if (this.isDummyGun(stack)) {
         if (Boolean.TRUE.equals(stack.get((DataComponentType)TensuraDataComponents.ALTERNATIVE_MODE.get()))) {
            list.add(Component.translatable("tooltip.tensura.sniper_pistol.tooltip.mode_magic"));
         } else {
            list.add(Component.translatable("tooltip.tensura.sniper_pistol.tooltip.mode_physical"));
         }

         list.add(Component.translatable("tooltip.tensura.sniper_pistol.tooltip.mode").withStyle(ChatFormatting.GRAY));
      } else {
         ChargedProjectiles chargedProjectiles = (ChargedProjectiles)stack.get(DataComponents.CHARGED_PROJECTILES);
         if (chargedProjectiles != null && !chargedProjectiles.isEmpty()) {
            ItemStack ammo = (ItemStack)chargedProjectiles.getItems().get(0);
            list.add(Component.translatable("item.minecraft.crossbow.projectile").append(CommonComponents.SPACE).append(ammo.getDisplayName()));
         }
      }
   }

   public boolean useOnRelease(ItemStack itemStack) {
      return itemStack.is(this);
   }

   @NotNull
   public Predicate<ItemStack> getAllSupportedProjectiles() {
      return stack -> stack.is(TensuraToolItems.COPPER_SHELL);
   }

   public int getDefaultProjectileRange() {
      return 20;
   }

   private boolean isDummyGun(ItemStack stack) {
      return stack.has((DataComponentType)TensuraDataComponents.DUMMY_ITEM.get());
   }

   @Nullable
   private ManasSkillInstance getSniperSkill(Player player, ItemStack stack) {
      ResourceLocation skill = (ResourceLocation)stack.get((DataComponentType)TensuraDataComponents.SKILL.get());
      if (skill != null) {
         Optional<ManasSkillInstance> optional = SkillAPI.getSkillsFrom(player).getSkill(skill);
         return optional.orElse(null);
      } else {
         return null;
      }
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      ItemStack stack = player.getItemInHand(hand);
      if (!this.isDummyGun(stack)) {
         ChargedProjectiles chargedProjectiles = (ChargedProjectiles)stack.get(DataComponents.CHARGED_PROJECTILES);
         if (chargedProjectiles != null && !chargedProjectiles.isEmpty()) {
            this.performShooting(level, player, hand, stack, 2.0F, 1.0F, null);
            return InteractionResultHolder.consume(stack);
         } else if (!player.getProjectile(stack).isEmpty()) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
         } else {
            return InteractionResultHolder.fail(stack);
         }
      } else {
         if (stack.has((DataComponentType)TensuraDataComponents.SKILL.get())) {
            ManasSkill skill = (ManasSkill)SkillAPI.getSkillRegistry().get((ResourceLocation)stack.get((DataComponentType)TensuraDataComponents.SKILL.get()));
            if (skill == null || !SkillUtils.hasSkill(player, skill)) {
               ItemHelper.breakItem(stack, player, LivingEntity.getSlotForHand(hand));
               return InteractionResultHolder.fail(stack);
            }
         }

         if (player.isShiftKeyDown()) {
            boolean magic = Boolean.TRUE.equals(stack.get((DataComponentType)TensuraDataComponents.ALTERNATIVE_MODE.get()));
            stack.set((DataComponentType)TensuraDataComponents.ALTERNATIVE_MODE.get(), !magic);
            player.swing(hand, true);
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
         } else {
            player.startUsingItem(hand);
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
         }
      }
   }

   public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int i) {
      if (this.isDummyGun(stack)) {
         if (entity instanceof Player player) {
            InteractionHand var14 = entity.getUsedItemHand();
            if (!level.isClientSide() && !player.getCooldowns().isOnCooldown(this)) {
               boolean magic = Boolean.TRUE.equals(stack.get((DataComponentType)TensuraDataComponents.ALTERNATIVE_MODE.get()));
               if (magic) {
                  if (EnergyHelper.isOutOfEnergy(player, 0.0, SniperSkill.CONFIG.energyCostPistol)) {
                     return;
                  }
               } else if (EnergyHelper.isOutOfEnergy(player, SniperSkill.CONFIG.energyCostPistol, 0.0)) {
                  return;
               }

               level.playSound(
                  null,
                  player.getX(),
                  player.getY(),
                  player.getZ(),
                  SoundEvents.BLAZE_SHOOT,
                  SoundSource.PLAYERS,
                  0.5F,
                  0.4F + (level.getRandom().nextFloat() * 0.4F + 0.8F)
               );
               boolean left = var14 == InteractionHand.OFF_HAND && entity.getMainArm() == HumanoidArm.RIGHT
                  || var14 == InteractionHand.MAIN_HAND && entity.getMainArm() == HumanoidArm.LEFT;
               BulletProjectile bullet = new BulletProjectile(level, player, !left);
               bullet.setDamage(SniperSkill.CONFIG.physicalDamage);
               ManasSkillInstance sniper = this.getSniperSkill(player, stack);
               bullet.setSkill(sniper);
               boolean mastered = sniper != null && sniper.isMastered(player);
               if (magic) {
                  bullet.setMagic(true);
                  bullet.setMpCost(SniperSkill.CONFIG.energyCostPistol);
                  bullet.setDamage(mastered ? SniperSkill.CONFIG.magicDamageMastered : SniperSkill.CONFIG.magicDamage);
                  if (entity.getAttributeValue(TensuraAttributes.WARP_SHOT) > 0.0) {
                     bullet.setElement(Element.SPACE);
                  }

                  if (!mastered) {
                     player.getCooldowns().addCooldown(this, SniperSkill.CONFIG.magicCooldown);
                  }
               } else if (!mastered) {
                  player.getCooldowns().addCooldown(this, SniperSkill.CONFIG.physicalCooldown);
               }

               Vec3 vec3 = player.getViewVector(2.0F);
               bullet.shoot(vec3.x(), vec3.y(), vec3.z(), 3.0F, 0.0F);
               bullet.setNoGravity(true);
               level.addFreshEntity(bullet);
               player.awardStat(Stats.ITEM_USED.get(this));
               if (sniper != null) {
                  sniper.addMasteryPoint(player);
               }
            }
         }
      } else {
         int j = this.getUseDuration(stack, entity) - i;
         float f = getPowerForTime(j, stack, entity);
         if (f >= 1.0F && !isCharged(stack) && tryLoadProjectiles(entity, stack)) {
            level.playSound(
               null,
               entity.getX(),
               entity.getY(),
               entity.getZ(),
               SoundEvents.CROSSBOW_LOADING_END,
               entity.getSoundSource(),
               1.0F,
               1.0F / (level.getRandom().nextFloat() * 0.5F + 1.0F) + 0.2F
            );
         }
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
      return new BulletProjectile(level, entity, !left);
   }

   private static float getShotPitch(RandomSource randomSource, int i) {
      return i == 0 ? 1.0F : getRandomShotPitch((i & 1) == 1, randomSource);
   }

   private static float getRandomShotPitch(boolean bl, RandomSource randomSource) {
      float f = bl ? 0.63F : 0.43F;
      return 1.0F / (randomSource.nextFloat() * 0.5F + 1.8F) + f;
   }
}
