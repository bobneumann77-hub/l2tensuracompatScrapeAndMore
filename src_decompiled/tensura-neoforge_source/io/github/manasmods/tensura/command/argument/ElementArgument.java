package io.github.manasmods.tensura.command.argument;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.manasmods.tensura.ability.magic.Element;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.StringRepresentableArgument;
import org.jetbrains.annotations.NotNull;

public class ElementArgument extends StringRepresentableArgument<Element> {
   private ElementArgument() {
      super(Element.CODEC, Element::values);
   }

   public static ElementArgument element() {
      return new ElementArgument();
   }

   public static Element getElement(CommandContext<CommandSourceStack> context, String string) {
      return (Element)context.getArgument(string, Element.class);
   }

   @NotNull
   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
      return SharedSuggestionProvider.suggest(
         Element.getCommandSuggestElemental().stream().map(Element::getSerializedName).map(x$0 -> this.convertId(x$0)).collect(Collectors.toList()),
         suggestionsBuilder
      );
   }
}
