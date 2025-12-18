package de.teamlapen.factions.common.server.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import de.teamlapen.factions.common.factions.minions.MinionInventory;
import de.teamlapen.factions.common.factions.minions.MinionWorldData;
import de.teamlapen.factions.common.factions.minions.PlayerMinionController;
import de.teamlapen.factions.common.server.commands.arguments.MinionArgument;
import de.teamlapen.factions.common.util.RegUtil;
import de.teamlapen.factions.server.FactionLogger;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.*;
import java.util.stream.Collectors;

public class MinionInventoryCommand extends BasicCommand {

    private static final SimpleCommandExceptionType NO_PLAYER = new SimpleCommandExceptionType(Component.translatable("command.vampirism.base.minion_inventory.no_player"));
    private static final SimpleCommandExceptionType NO_MINION = new SimpleCommandExceptionType(Component.translatable("command.vampirism.base.minion_inventory.no_minion"));

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandBuildContext builderContext) {
        return Commands.literal("modifyMinionInventory")
                .requires(context -> context.hasPermission(BasicCommand.PERMISSION_LEVEL_ADMIN))
                .then(Commands.argument("minion", MinionArgument.minions())
                        .then(Commands.literal("list")
                                .executes(context -> listInventory(context.getSource(), context.getSource().getPlayerOrException(), MinionArgument.getId(context, "minion"))))
                        .then(Commands.literal("remove")
                                .then(Commands.argument("item", ItemArgument.item(builderContext))
                                        .executes(context -> removeItem(context.getSource(), context.getSource().getPlayerOrException(), MinionArgument.getId(context, "minion"), ItemArgument.getItem(context, "item"), 1))
                                        .then(Commands.argument("count", IntegerArgumentType.integer(1))
                                                .executes(context -> removeItem(context.getSource(), context.getSource().getPlayerOrException(), MinionArgument.getId(context, "minion"), ItemArgument.getItem(context, "item"), IntegerArgumentType.getInteger(context, "count"))))))
                        .then(Commands.literal("add")
                                .then(Commands.argument("item", ItemArgument.item(builderContext))
                                        .executes(context -> addItem(context.getSource(), context.getSource().getPlayerOrException(), MinionArgument.getId(context, "minion"), ItemArgument.getItem(context, "item"), 1))
                                        .then(Commands.argument("count", IntegerArgumentType.integer(1))
                                                .executes(context -> addItem(context.getSource(), context.getSource().getPlayerOrException(), MinionArgument.getId(context, "minion"), ItemArgument.getItem(context, "item"), IntegerArgumentType.getInteger(context, "count")))))));
    }

    private static int addItem(CommandSourceStack source, ServerPlayer player, MinionArgument.MinionId playerMinionIdentifier, ItemInput item, int count) throws CommandSyntaxException {
        ItemStack itemStack = item.createItemStack(count, true);
        getInventory(playerMinionIdentifier).ifPresent(minionInventory -> {
            minionInventory.addItemStack(itemStack.copy());
            FactionLogger.info(FactionLogger.MINION_INVENTORY, "{} added {} {} to inventory of {}", player.getName().getString(), itemStack.getCount(), itemStack.getDisplayName().getString(), playerMinionIdentifier);
            source.sendSystemMessage(Component.translatable("command.vampirism.base.minion_inventory.add_success", itemStack.getCount(), itemStack.getDisplayName().getString(), playerMinionIdentifier).withStyle(ChatFormatting.AQUA));
        });

        return 0;
    }

    private static int removeItem(CommandSourceStack source, ServerPlayer player, MinionArgument.MinionId playerMinionIdentifier, ItemInput item, int count) throws CommandSyntaxException {
        ItemStack itemStack = item.createItemStack(1, false);
        getInventory(playerMinionIdentifier).ifPresent(minionInventory -> {
            List<ItemStack> itemStacks = minionInventory.getAllInventories().stream().flatMap(Collection::stream).filter(s -> ItemStack.isSameItemSameComponents(s, itemStack)).toList();
            if (!itemStacks.isEmpty()) {
                ItemStack stack = itemStacks.getFirst().split(count);
                player.addItem(stack.copy());
                FactionLogger.info(FactionLogger.MINION_INVENTORY, "{} removed {} {} from inventory of {}", player.getName().getString(), stack.getCount(), stack.getDisplayName().getString(), playerMinionIdentifier);
                source.sendSuccess(() -> Component.translatable("command.vampirism.base.minion_inventory.item_removed", stack.getCount(), stack.getDisplayName().getString(), playerMinionIdentifier).withStyle(ChatFormatting.AQUA), false);
            } else {
                source.sendFailure(Component.translatable("command.vampirism.base.minion_inventory.item_not_found"));
            }
        });

        return 0;
    }

    private static int listInventory(CommandSourceStack source, ServerPlayer player, MinionArgument.MinionId playerMinionIdentifier) throws CommandSyntaxException {
        getInventory(playerMinionIdentifier).ifPresent(inv -> {
            Map<Item, Integer> count = new HashMap<>();
            inv.getAllInventories().stream().flatMap(Collection::stream).filter(stack -> !stack.isEmpty()).forEach(item -> count.merge(item.getItem(), item.getCount(), Integer::sum));
            if (count.isEmpty()) {
                source.sendSuccess(() -> Component.translatable("command.vampirism.base.minion_inventory.empty", playerMinionIdentifier).withStyle(ChatFormatting.AQUA), false);
            } else {
                FactionLogger.info(FactionLogger.MINION_INVENTORY, "{} views inventory of {}", player.getName().getString(), playerMinionIdentifier);
                source.sendSuccess(() -> Component.translatable("command.vampirism.base.minion_inventory.content", playerMinionIdentifier).withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.UNDERLINE), false);
                source.sendSuccess(() -> Component.literal(count.entrySet().stream().map(a -> a.getValue() + " " + RegUtil.id(a.getKey())).collect(Collectors.joining("\n"))).withStyle(ChatFormatting.AQUA), false);
            }
        });
        return 0;
    }

    private static Optional<MinionInventory> getInventory(MinionArgument.MinionId playerMinionIdentifier) throws CommandSyntaxException {
        String playerName = playerMinionIdentifier.player;
        int minionId = playerMinionIdentifier.id;
        MinionWorldData data = MinionWorldData.getData(ServerLifecycleHooks.getCurrentServer());
        var profile = ServerLifecycleHooks.getCurrentServer().services().nameToIdCache().get(playerName).orElseThrow(NO_PLAYER::create);
        PlayerMinionController controller = data.getController(profile.id());
        if (controller == null) {
            throw NO_MINION.create();
        }
        Optional<MinionInventory> minionInventory = controller.contactMinionData(minionId, minionData -> {
            playerMinionIdentifier.updateName(minionData.getFormattedName().getString());
            return minionData.getInventory();
        });
        if (minionInventory.isEmpty()) {
            throw NO_MINION.create();
        }
        return minionInventory;
    }
}
