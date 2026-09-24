package io.github.manasmods.tensura.config.client;

import io.github.manasmods.manascore.config.api.Comment;
import io.github.manasmods.manascore.config.api.ManasConfig;
import java.util.List;

public class MiscClientConfig extends ManasConfig {
   @Comment("The strength multiplier of the Camera shake effect from Tensura's feature.")
   public float cameraShakeStrength = 1.0F;
   @Comment("Give Tensura spider entities more friendly textures.")
   public boolean arachnophobia = false;
   @Comment("Flip entities (even players) upside down visually when named \"Dinnerbone\" with Tensura Naming.")
   public boolean dinnerboneNameFlip = false;
   @Comment("Whether should Tensura: Reincarnated's custom title screen should render.")
   public boolean tensuraTitleScreen = true;
   @Comment("List of custom splash text on the mod's custom Title Screen.")
   public List<String> customSplashText = List.of(
      "I'm not a bad slime slurp",
      "I am Atomic",
      "Disintegration!",
      "I'll devour all your sins",
      "Megiddo!",
      "Nanoda!",
      "Oop",
      "TTIGRAAS2 Electric Boogaloo",
      "Delete my Hard Drive",
      "Successfully acquired [Degenerate]",
      "He's lurking",
      "Removed Memorybrine",
      "Memory leak free mod!",
      "Join The Mod's Official Discord AND Server!",
      "Devour All! Gluttony!",
      "Thank you Patreon and Ko-fi Supporters",
      "You probably won't get the skill you want",
      "Pick slime, be a normie",
      "Did you even read the Light Novel?",
      "Hipokute makes me hiccup-te",
      "You can decraft, de enchant and steal skills",
      "Ok!",
      "NO UNYIELDING HAHAHAHA",
      "Goth Mommy Slime",
      "I'm Just Arthur",
      "The Nugget Incident!",
      "This is technically Tensura:Reincarnated:Reincarnated",
      "The cannoli of Hihi'irokane!"
   );

   public String getFileName() {
      return "tensura/client/misc_config";
   }
}
