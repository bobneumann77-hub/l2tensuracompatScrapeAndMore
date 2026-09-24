package io.github.manasmods.tensura.client.screen.templates;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.event.events.client.ClientLifecycleEvent.ClientState;
import io.github.manasmods.manascore.config.ConfigRegistry;
import io.github.manasmods.tensura.client.screen.widgets.SettingsButton;
import io.github.manasmods.tensura.client.screen.widgets.SettingsEditBox;
import io.github.manasmods.tensura.config.client.HudConfig;
import io.github.manasmods.tensura.config.client.MenuConfig;
import io.github.manasmods.tensura.config.client.MiscClientConfig;
import io.github.manasmods.tensura.event.TensuraLocalPlayerEvents;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.Generated;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.network.chat.Component;

public class SettingsOptions {
   private static final Map<SettingsOptions.Category, List<AbstractWidget>> OPTIONS = new HashMap<>();
   private static int freeId = 0;

   public static void addOption(AbstractWidget option, SettingsOptions.Category category) {
      OPTIONS.computeIfAbsent(category, list -> new ArrayList<>()).add(option);
   }

   public static ImmutableMap<SettingsOptions.Category, ImmutableList<AbstractWidget>> getAllOptions() {
      Builder<SettingsOptions.Category, ImmutableList<AbstractWidget>> builder = ImmutableMap.builder();
      OPTIONS.entrySet()
         .stream()
         .sorted(Comparator.comparingInt(entry -> entry.getKey().getId()))
         .forEachOrdered(entry -> builder.put(entry.getKey(), ImmutableList.copyOf(entry.getValue())));
      return builder.build();
   }

   public static ImmutableList<AbstractWidget> getOptionsFromCategory(SettingsOptions.Category category) {
      return ImmutableList.copyOf(OPTIONS.getOrDefault(category, Collections.emptyList()));
   }

   public static void saveAll() {
      ((HudConfig)ConfigRegistry.getConfig(HudConfig.class)).save();
      ((MenuConfig)ConfigRegistry.getConfig(MenuConfig.class)).save();
      ((MiscClientConfig)ConfigRegistry.getConfig(MiscClientConfig.class)).save();
      ((TensuraLocalPlayerEvents.SETTINGS_EVENT.Save)TensuraLocalPlayerEvents.SETTINGS_EVENT.SAVE.invoker()).saveSettings();
   }

   public static void resetAll() {
      for (List<AbstractWidget> widgets : OPTIONS.values()) {
         for (AbstractWidget widget : widgets) {
            if (widget instanceof IResetButton reset && reset.getResetButton() != null) {
               reset.getResetButton().onPress();
            }
         }
      }
   }

   public static SettingsOptions.Category createCategory(Component header) {
      return new SettingsOptions.Category(header);
   }

   private static int getFreeId() {
      return ++freeId;
   }

   public static void init() {
      ClientLifecycleEvent.CLIENT_STARTED
         .register((ClientState)event -> ((TensuraLocalPlayerEvents.SETTINGS_EVENT.Load)TensuraLocalPlayerEvents.SETTINGS_EVENT.LOAD.invoker()).loadSettings());
   }

   public static class BooleanOption extends SettingsButton {
      private static final Component TRUE = Component.translatable("tensura.settings.true").withColor(5635925);
      private static final Component FALSE = Component.translatable("tensura.settings.false").withColor(16711680);

      public BooleanOption(Component optionText, Supplier<Boolean> check, Component tooltip, OnPress onPress, OnPress onReset) {
         super(optionText, () -> check.get() ? TRUE : FALSE, List.of(tooltip), onPress, onReset);
      }

      public BooleanOption(Component optionText, Supplier<Boolean> check, List<Component> tooltip, OnPress onPress, OnPress onReset) {
         super(optionText, () -> check.get() ? TRUE : FALSE, tooltip, onPress, onReset);
      }
   }

   public enum BuiltinCategories {
      GLOBAL(get("global")),
      MENU(get("menu")),
      HUD_STATUS(get("status")),
      HUD_STATUS_BARS(get("status_bars")),
      HUD_ABILITIES(get("abilities")),
      HUD_ANALYSIS(get("analysis")),
      HUD_DECORATIONS(get("decorations")),
      MISC(get("miscellaneous"));

      private final SettingsOptions.Category category;

      BuiltinCategories(SettingsOptions.Category category) {
         this.category = category;
      }

      private static SettingsOptions.Category get(String string) {
         return SettingsOptions.createCategory(Component.translatable("tensura.settings.category." + string));
      }

      @Generated
      public SettingsOptions.Category getCategory() {
         return this.category;
      }
   }

   public static class Category {
      private final Component header;
      private final int id;

      private Category(Component header) {
         this.header = header;
         this.id = SettingsOptions.getFreeId();
      }

      @Override
      public boolean equals(Object that) {
         if (this == that) {
            return true;
         } else {
            return that != null && this.getClass() == that.getClass() ? this.id == ((SettingsOptions.Category)that).getId() : false;
         }
      }

      @Override
      public int hashCode() {
         return Objects.hashCode(this.id);
      }

      @Generated
      public Component getHeader() {
         return this.header;
      }

      @Generated
      public int getId() {
         return this.id;
      }
   }

   public static class InputOption extends SettingsEditBox {
      public InputOption(
         Font font,
         Supplier<String> value,
         Component optionText,
         List<Component> tooltip,
         Consumer<String> responder,
         SettingsEditBox.OnPress onPress,
         net.minecraft.client.gui.components.Button.OnPress onReset,
         boolean centerOnFocus
      ) {
         super(font, value, optionText, tooltip, responder, onPress, onReset, centerOnFocus);
      }

      public InputOption(
         Font font,
         Supplier<String> value,
         Component optionText,
         Component tooltip,
         Consumer<String> responder,
         SettingsEditBox.OnPress onPress,
         net.minecraft.client.gui.components.Button.OnPress onReset,
         boolean centerOnFocus
      ) {
         this(font, value, optionText, List.of(tooltip), responder, onPress, onReset, centerOnFocus);
      }

      public InputOption(
         Font font,
         Supplier<String> value,
         Component optionText,
         Component tooltip,
         Consumer<String> responder,
         net.minecraft.client.gui.components.Button.OnPress onReset,
         boolean centerOnFocus
      ) {
         this(font, value, optionText, tooltip, responder, null, onReset, centerOnFocus);
      }

      public InputOption(
         Font font,
         Supplier<String> value,
         Component optionText,
         List<Component> tooltip,
         Consumer<String> responder,
         net.minecraft.client.gui.components.Button.OnPress onReset,
         boolean centerOnFocus
      ) {
         this(font, value, optionText, tooltip, responder, null, onReset, centerOnFocus);
      }
   }
}
