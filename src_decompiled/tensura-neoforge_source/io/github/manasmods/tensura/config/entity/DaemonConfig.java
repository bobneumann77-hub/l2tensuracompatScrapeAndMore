package io.github.manasmods.tensura.config.entity;

import io.github.manasmods.manascore.config.api.Comment;
import io.github.manasmods.manascore.config.api.ManasConfig;
import io.github.manasmods.manascore.config.api.ManasSubConfig;
import java.util.List;

public class DaemonConfig extends ManasConfig {
   public DaemonConfig.ArchDaemon ArchDaemon = new DaemonConfig.ArchDaemon();
   public DaemonConfig.LowMagisteel LowMagisteel = new DaemonConfig.LowMagisteel();
   public DaemonConfig.HighMagisteel HighMagisteel = new DaemonConfig.HighMagisteel();
   public DaemonConfig.Mithril Mithril = new DaemonConfig.Mithril();
   public DaemonConfig.PureMagisteel PureMagisteel = new DaemonConfig.PureMagisteel();
   public DaemonConfig.Orichalcum Orichalcum = new DaemonConfig.Orichalcum();
   public DaemonConfig.Adamantite Adamantite = new DaemonConfig.Adamantite();
   public DaemonConfig.Hihiirokane Hihiirokane = new DaemonConfig.Hihiirokane();

   public String getFileName() {
      return "tensura/entity/daemon_config";
   }

   public static class Adamantite extends ManasSubConfig {
      public double EP = 225000.0;
      public float HP = 800.0F;
      public float Armor = 30.0F;
      public float Knockback = 0.8F;
      public float Attack = 15.0F;
      public float Speed = 1.3F;
   }

   public static class ArchDaemon extends ManasSubConfig {
      @Comment("Random colors for daemons' hair.")
      public List<Integer> hairColors = List.of(-7558, -1326982, -2575510, -6260652, -9418704, -12966368, -15066598, -2565928, -4896198);
      @Comment("Random colors for daemon eyes.")
      public List<Integer> eyeColors = List.of(-45747, -47872, -10496, -1653414, -4682710, -16711681, -8861441, -7077677, -5206785, -1, -1120554, -11184811);
      @Comment("Random colors for daemon horns.")
      public List<Integer> hornColors = List.of(-13952238, -12965344, -10861000, -9539986, -7697782, -2240336, -858423, -15066598);
      @Comment("Random colors for daemons' top clothes.")
      public List<Integer> topClothesColors = List.of(
         -14540254, -15656921, -13747622, -12828840, -10867110, -8765830, -9560289, -13738962, -5010688, -2842601, -1, -2239048, -3293541, -7640241, -11184811
      );
      @Comment("Random colors for daemons' bottom clothes.")
      public List<Integer> bottomClothesColors = List.of(
         -15658735, -14540254, -12961222, -11184811, -9669504, -3293541, -11850209, -9814230, -7640241, -10867110, -13747622
      );
      @Comment("Random colors for daemons' shoes.")
      public List<Integer> bootsColors = List.of(-15066598, -13948117, -11850209, -8749172);
      @Comment("Random colors for coats and vests.")
      public List<Integer> coatColors = List.of(
         -15658735, -13750738, -14735040, -13747622, -10867110, -8693114, -6653280, -9560289, -7640241, -11850209, -3293541, -12629446
      );
      @Comment("Random colors for neck accessories (ties, scarves, fluff, wraps).")
      public List<Integer> neckAccessoryColors = List.of(-1120554, -3293541);
      @Comment("Random colors for armbands.")
      public List<Integer> armbandColors = List.of(-5010688, -2842601, -4144960, -7640241, -11850209, -8749172, -10867110);
      @Comment("Random colors for eyeliner.")
      public List<Integer> eyeLinerColors = List.of(-1120554, -16777216, -15066598, -12961222);
   }

   public static class HighMagisteel extends ManasSubConfig {
      public double EP = 20000.0;
      public float HP = 200.0F;
      public float Armor = 10.0F;
      public float Knockback = 0.4F;
      public float Attack = 7.0F;
      public float Speed = 1.1F;
   }

   public static class Hihiirokane extends ManasSubConfig {
      public double EP = 750000.0;
      public float HP = 1200.0F;
      public float Armor = 35.0F;
      public float Knockback = 1.0F;
      public float Attack = 20.0F;
      public float Speed = 1.5F;
   }

   public static class LowMagisteel extends ManasSubConfig {
      @Comment("The minimum EP requirement to possess the Golem.")
      public double EP = 6000.0;
      @Comment("The HP of the Golem.")
      public float HP = 100.0F;
      @Comment("The Armor Point of the Golem.")
      public float Armor = 5.0F;
      @Comment("The Knockback Resistance of the Golem.")
      public float Knockback = 0.3F;
      @Comment("The Attack Damage of the Golem.")
      public float Attack = 5.0F;
      @Comment("The Speed Multiplier of the Golem.")
      public float Speed = 1.0F;
   }

   public static class Mithril extends ManasSubConfig {
      public double EP = 45000.0;
      public float HP = 400.0F;
      public float Armor = 15.0F;
      public float Knockback = 0.5F;
      public float Attack = 9.0F;
      public float Speed = 1.15F;
   }

   public static class Orichalcum extends ManasSubConfig {
      public double EP = 75000.0;
      public float HP = 600.0F;
      public float Armor = 25.0F;
      public float Knockback = 0.7F;
      public float Attack = 13.0F;
      public float Speed = 1.25F;
   }

   public static class PureMagisteel extends ManasSubConfig {
      public double EP = 60000.0;
      public float HP = 500.0F;
      public float Armor = 20.0F;
      public float Knockback = 0.6F;
      public float Attack = 11.0F;
      public float Speed = 1.2F;
   }
}
