package io.github.manasmods.tensura.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.boss.template.IBossFightHolder;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public class BossFightArgument implements ArgumentType<String> {
   private static final List<String> EXAMPLES = List.of("dwarf_king");

   public static BossFightArgument bossFight() {
      return new BossFightArgument();
   }

   public String parse(StringReader reader) throws CommandSyntaxException {
      return reader.readUnquotedString();
   }

   public static String getBossFightName(CommandContext<CommandSourceStack> context, String string) {
      return (String)context.getArgument(string, String.class);
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      if (context.getSource() instanceof CommandSourceStack stack) {
         ServerLevel overworld = stack.getServer().getLevel(Level.OVERWORLD);
         if (overworld == null) {
            return Suggestions.empty();
         }

         IBossFightHolder bossFightHolder = TensuraStorages.getBossFightHolder(overworld);
         return SharedSuggestionProvider.suggest(bossFightHolder.getBossFights().keySet(), builder);
      } else {
         return Suggestions.empty();
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
