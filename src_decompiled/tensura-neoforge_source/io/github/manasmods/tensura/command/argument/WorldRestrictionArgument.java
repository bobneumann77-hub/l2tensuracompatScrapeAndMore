package io.github.manasmods.tensura.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.restriction.template.IWorldRestriction;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public class WorldRestrictionArgument implements ArgumentType<String> {
   private static final List<String> EXAMPLES = List.of("safe_zone");

   public static WorldRestrictionArgument worldRestriction() {
      return new WorldRestrictionArgument();
   }

   public String parse(StringReader reader) throws CommandSyntaxException {
      return reader.readUnquotedString();
   }

   public static String getWorldRestrictionName(CommandContext<CommandSourceStack> context, String string) {
      return (String)context.getArgument(string, String.class);
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      if (context.getSource() instanceof CommandSourceStack stack) {
         ServerLevel overworld = stack.getServer().getLevel(Level.OVERWORLD);
         if (overworld == null) {
            return Suggestions.empty();
         }

         IWorldRestriction holder = TensuraStorages.getWorldRestrictionFrom(overworld);
         return SharedSuggestionProvider.suggest(holder.getRestrictions().keySet(), builder);
      } else {
         return Suggestions.empty();
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
