package de.teamlapen.vampirism.common.server.commands.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.faction.common.server.commands.BasicCommand;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class EnchantmentTestCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandBuildContext buildContext) {
        return Commands.literal("enchantment")
                .requires(Commands.hasPermission(Commands.LEVEL_ADMINS))
                .then(Commands.argument("item", ItemArgument.item(buildContext))
                        .then(Commands.literal("supported")
                                .executes(context -> supported(ItemArgument.getItem(context, "item"), context.getSource())))
                        .then(Commands.literal("primary")
                                .executes(context -> primary(ItemArgument.getItem(context, "item"), context.getSource())))

                );
    }


    @SuppressWarnings("NoTranslation")
    private static int primary(@NotNull ItemInput item, CommandSourceStack player) {
        return primary(item,Component.translatable("Primary enchantments for %s:", Component.translatable(item.item().value().getDescriptionId())), x -> x::isPrimaryItemFor, player);
    }

    @SuppressWarnings("NoTranslation")
    private static int supported(@NotNull ItemInput item, CommandSourceStack player) {
        return primary(item,Component.translatable("Supported enchantments for %s:", Component.translatable(item.item().value().getDescriptionId())) , x -> x::supportsEnchantment, player);
    }

    private static int primary(@NotNull ItemInput item, MutableComponent header, Function<ItemStack, Predicate<Holder<Enchantment>>> check, CommandSourceStack player) {

        Component enchantments = player.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                .listElements()
                .filter(check.apply(item.item().value().getDefaultInstance()))
                .map(x -> x.value().description().plainCopy())
                .reduce(Component.empty(), (a,b) -> a.append("\n").append(b));
        if (enchantments.getString().isEmpty()) {
            player.sendSuccess(() -> Component.literal("none"), false);
        } else {
            player.sendSuccess(() -> header.append("\n").append(enchantments), false);
        }
        return 0;
    }
}
