package io.github.manasmods.tensura.config.client;

import io.github.manasmods.manascore.config.api.Comment;
import io.github.manasmods.manascore.config.api.ManasConfig;

public class MenuConfig extends ManasConfig {
   @Comment("Should FOV be slightly modified (zoomed out) upon opening a menu")
   public boolean modifyFov = true;
   @Comment(
      "Should menus have a fade-in-out effect upon opening and closing them\n(it takes 10 ticks / half a second for the effect to end before you can do anything)"
   )
   public boolean fadeEffects = false;
   @Comment("The value by which to multiply the scale of every menu and its elements (Vanilla options also affect menu scale)")
   public float scale = 1.0F;
   @Comment("How much should the background be blurred; 0 - 5")
   public int blurStrength = 0;
   @Comment("Should the ability slots/bars be automatically shown when opening ability GUIs")
   public boolean autoAbilitySlot = false;

   public String getFileName() {
      return "tensura/client/menu_config";
   }
}
