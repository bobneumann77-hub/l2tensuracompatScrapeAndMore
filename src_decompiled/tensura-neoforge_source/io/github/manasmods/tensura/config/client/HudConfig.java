package io.github.manasmods.tensura.config.client;

import io.github.manasmods.manascore.config.api.Comment;
import io.github.manasmods.manascore.config.api.ManasConfig;
import io.github.manasmods.manascore.config.api.ManasSubConfig;

public class HudConfig extends ManasConfig {
   @Comment("Controls if Tensura HUD elements should be rendered at all or not")
   public boolean tensuraHud = true;
   @Comment("Controls if Vanilla HUD elements should be rendered at all or not")
   public boolean vanillaHud = false;
   public HudConfig.Status status = new HudConfig.Status();
   public HudConfig.Decorations decorations = new HudConfig.Decorations();
   public HudConfig.StatusBars statusBars = new HudConfig.StatusBars();
   public HudConfig.Abilities abilities = new HudConfig.Abilities();
   public HudConfig.Analysis analysis = new HudConfig.Analysis();

   public String getFileName() {
      return "tensura/client/hud_config";
   }

   public static class Abilities extends ManasSubConfig {
      @Comment("Ignored if defaultRendering is true")
      public float positionX = 0.0F;
      public float positionY = 0.0F;
      @Comment("The multiplier for the element size\n0.05 ~ 5")
      public float scale = 1.0F;
      @Comment(
         "Setting to 1 will make the element to render from left to right and vice versa if 2\nSetting to 0 will allow the renderer to decide dynamically\n0 ~ 2"
      )
      public int side = 0;
      @Comment("Separate check if this specific element should render\nIgnored if tensuraHud = false")
      public boolean render = true;
      @Comment("If true, dynamically adjusts the element")
      public boolean defaultRendering = true;
   }

   public static class Analysis extends ManasSubConfig {
      @Comment("Ignored if defaultRendering is true")
      public float positionX = 0.0F;
      public float positionY = 0.0F;
      @Comment("The multiplier for the element size\n0.05 ~ 5")
      public float scale = 1.0F;
      @Comment("How opaque the element should be when rendering\n0.0 ~ 1.0")
      public float opacity = 0.8F;
      @Comment(
         "Setting to 1 will make the element to render from left to right and vice versa if 2\nSetting to 0 will allow the renderer to decide dynamically\n0 ~ 2"
      )
      public int side = 2;
      @Comment("If true, will render target's hearts instead of HP (1 heart = 2 HP)")
      public boolean useHearts = false;
      @Comment("Separate check if this specific element should render\nIgnored if tensuraHud = false")
      public boolean render = true;
      @Comment("If true, dynamically adjusts the element")
      public boolean defaultRendering = true;
   }

   public static class Decorations extends ManasSubConfig {
      @Comment(
         "Properties notes:\npositionX = Horizontal position; Ignored if defaultRendering is true\npositionY = Vertical position; Ignored if defaultRendering is true\nside = 0 means dynamic rendering, 1 makes it render from left to right, 2 mirrors it; Ignored if defaultRendering is true\n0 ~ 2\nscale = Size multiplier for the element, such as 0.8 or 1.2; Uses Status's scale if defaultRendering is true\n0.05 ~ 5\nrender = Separate check if the element should render; Ignored if tensuraHud is false\ndefaultRendering = Dynamically adjust everything"
      )
      public HudConfig.Decorations.Air air = new HudConfig.Decorations.Air();
      public HudConfig.Decorations.Food food = new HudConfig.Decorations.Food();
      public HudConfig.Decorations.Armor armor = new HudConfig.Decorations.Armor();
      public HudConfig.Decorations.Barrier barrier = new HudConfig.Decorations.Barrier();
      public HudConfig.Decorations.MountHp mountHp = new HudConfig.Decorations.MountHp();
      public HudConfig.Decorations.MountSpiritualHp mountSpiritualHp = new HudConfig.Decorations.MountSpiritualHp();

      public static class Air extends ManasSubConfig {
         public float positionX = 0.0F;
         public float positionY = 0.0F;
         public int side = 0;
         public float scale = 1.0F;
         public boolean render = true;
         public boolean defaultRendering = true;
      }

      public static class Armor extends ManasSubConfig {
         public float positionX = 0.0F;
         public float positionY = 0.0F;
         public int side = 0;
         public float scale = 1.0F;
         public boolean render = true;
         public boolean defaultRendering = true;
      }

      public static class Barrier extends ManasSubConfig {
         public float positionX = 0.0F;
         public float positionY = 0.0F;
         public int side = 0;
         public float scale = 1.0F;
         public boolean render = true;
         public boolean defaultRendering = true;
      }

      public static class Food extends ManasSubConfig {
         public float positionX = 0.0F;
         public float positionY = 0.0F;
         public int side = 0;
         public float scale = 1.0F;
         public boolean render = true;
         public boolean defaultRendering = true;
      }

      public static class MountHp extends ManasSubConfig {
         public float positionX = 0.0F;
         public float positionY = 0.0F;
         public int side = 0;
         public float scale = 1.0F;
         public boolean render = true;
         public boolean defaultRendering = true;
      }

      public static class MountSpiritualHp extends ManasSubConfig {
         public float positionX = 0.0F;
         public float positionY = 0.0F;
         public int side = 0;
         public float scale = 1.0F;
         public boolean render = true;
         public boolean defaultRendering = true;
      }
   }

   public static class Status extends ManasSubConfig {
      @Comment("Ignored if defaultRendering is true")
      public float positionX = 0.0F;
      public float positionY = 0.0F;
      @Comment("The multiplier for the element size\nScales all other elements that have defaultRendering set to true except Abilities and Analysis0.0 ~ Any")
      public float scale = 1.0F;
      @Comment(
         "Setting to 1 will make the element to render from left to right and vice versa if 2\nSetting to 0 will allow the renderer to decide dynamically\nWill also move other elements that have defaultRendering set to true\n\n0 ~ 2"
      )
      public int side = 0;
      @Comment("Separate check if this specific element should render\nIgnored if tensuraHud = false")
      public boolean render = true;
      @Comment("If true, dynamically adjusts the element")
      public boolean defaultRendering = true;
   }

   public static class StatusBars extends ManasSubConfig {
      @Comment("Ignored if defaultRendering is true")
      public float positionX = 0.0F;
      public float positionY = 0.0F;
      @Comment("The multiplier for the element size\nUses Status's scale if defaultRendering is true\n0.05 ~ 5")
      public float scale = 1.0F;
      @Comment("Setting to 1 will force the element to render from left to right and vice versa if 2\nUses Status's side if defaultRendering is true\n0 ~ 2")
      public int side = 0;
      @Comment("Separate check if this specific element should render\nIgnored if tensuraHud = false")
      public boolean render = true;
      @Comment("If true, dynamically adjusts the element")
      public boolean defaultRendering = true;
   }
}
