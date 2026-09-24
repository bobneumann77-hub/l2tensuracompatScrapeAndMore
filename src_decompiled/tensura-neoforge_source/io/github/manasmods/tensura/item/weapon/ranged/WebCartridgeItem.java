package io.github.manasmods.tensura.item.weapon.ranged;

import io.github.manasmods.tensura.entity.projectile.WebBulletProjectile;
import io.github.manasmods.tensura.registry.item.misc.TensuraCreativeTabs;
import lombok.Generated;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;

public class WebCartridgeItem extends Item implements ProjectileItem {
   protected final int webbedDuration;
   protected final int silenceDuration;
   protected final double silenceChance;
   protected final int dissolvingDuration;
   protected final Block webBlock;

   public WebCartridgeItem(Properties pProperties, int webbedDuration, int silenceDuration, int silenceChance, int dissolvingDuration, Block webBlock) {
      super(pProperties);
      this.webbedDuration = webbedDuration;
      this.silenceDuration = silenceDuration;
      this.silenceChance = silenceChance;
      this.dissolvingDuration = dissolvingDuration;
      this.webBlock = webBlock;
      DispenserBlock.registerProjectileBehavior(this);
   }

   public WebCartridgeItem(int webbedDuration, int silenceDuration, double silenceChance, int dissolvingDuration, Block webBlock) {
      this(new Properties().arch$tab(TensuraCreativeTabs.GEARS).stacksTo(16), webbedDuration, silenceDuration, silenceDuration, dissolvingDuration, webBlock);
   }

   public Projectile asProjectile(Level level, Position position, ItemStack itemStack, Direction direction) {
      WebBulletProjectile bullet = new WebBulletProjectile(level, position.x(), position.y(), position.z());
      bullet.setSourceItem(itemStack);
      return bullet;
   }

   @Generated
   public int getWebbedDuration() {
      return this.webbedDuration;
   }

   @Generated
   public int getSilenceDuration() {
      return this.silenceDuration;
   }

   @Generated
   public double getSilenceChance() {
      return this.silenceChance;
   }

   @Generated
   public int getDissolvingDuration() {
      return this.dissolvingDuration;
   }

   @Generated
   public Block getWebBlock() {
      return this.webBlock;
   }
}
