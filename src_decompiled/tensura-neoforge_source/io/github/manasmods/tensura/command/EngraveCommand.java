package io.github.manasmods.tensura.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.manasmods.manascore.command.api.Command;
import io.github.manasmods.manascore.command.api.Execute;
import io.github.manasmods.manascore.command.api.Permission;
import io.github.manasmods.manascore.command.api.Permission.PermissionLevel;
import io.github.manasmods.manascore.command.api.parameter.EntityArg;
import io.github.manasmods.manascore.command.api.parameter.SenderArg;
import io.github.manasmods.manascore.command.api.parameter.EntityArg.Type;
import io.github.manasmods.manascore.command.api.parameter.primitive.IntegerArg;
import io.github.manasmods.manascore.command.api.parameter.resource.EnchantmentArg;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.core.Holder.Reference;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

@Command("engrave")
@Permission(value = "tensura.command.edit_ability", permissionLevel = PermissionLevel.GAMEMASTER)
public class EngraveCommand {
   @Execute
   public boolean engraveLevel(
      @SenderArg CommandSourceStack stack, @EntityArg(Type.ENTITIES) EntitySelector selector, @EnchantmentArg Reference holder, @IntegerArg("level") int level
   ) throws CommandSyntaxException {
      return engrave(stack, selector, holder, level);
   }

   @Execute
   public boolean engrave(@SenderArg CommandSourceStack stack, @EntityArg(Type.ENTITIES) EntitySelector selector, @EnchantmentArg Reference holder) throws CommandSyntaxException {
      return engrave(stack, selector, holder, 1);
   }

   public static boolean engrave(CommandSourceStack stack, EntitySelector selector, Reference holder, int level) throws CommandSyntaxException {
      List<? extends Entity> entities = selector.findEntities(stack);
      if (entities.size() == 1) {
         Entity entity = entities.getFirst();
         if (entity instanceof LivingEntity living) {
            ItemStack weapon = living.getMainHandItem();
            EnchantmentHelper.updateEnchantments(weapon, mutable -> mutable.set(holder, level));
            stack.sendSuccess(
               () -> Component.translatable("commands.enchant.success.single", new Object[]{Enchantment.getFullname(holder, level), entity.getName()})
                  .withStyle(ChatFormatting.DARK_GREEN),
               true
            );
            return true;
         } else {
            stack.sendFailure(Component.translatable("commands.enchant.failed.entity", new Object[]{entity.getName()}));
            return true;
         }
      } else {
         int i = 0;

         for (Entity entity : entities) {
            if (entity instanceof LivingEntity living) {
               ItemStack weapon = living.getMainHandItem();
               EnchantmentHelper.updateEnchantments(weapon, mutable -> mutable.set(holder, level));
               i++;
            }
         }

         MutableComponent component = Component.translatable("commands.enchant.success.multiple", new Object[]{Enchantment.getFullname(holder, level), i})
            .withStyle(ChatFormatting.DARK_GREEN);
         stack.sendSuccess(() -> component, true);
         return true;
      }
   }
}
