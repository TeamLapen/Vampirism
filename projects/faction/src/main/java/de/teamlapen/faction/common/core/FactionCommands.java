package de.teamlapen.faction.common.core;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.teamlapen.faction.api.util.REFERENCE;
import de.teamlapen.faction.common.server.commands.*;
import de.teamlapen.faction.common.server.commands.actions.ActionsCommand;
import de.teamlapen.faction.common.server.commands.actions.ResetActionsCommand;
import de.teamlapen.faction.common.server.commands.arguments.FactionArgument;
import de.teamlapen.faction.common.server.commands.arguments.MinionArgument;
import de.teamlapen.faction.common.server.commands.customization.CustomizationCommand;
import de.teamlapen.faction.common.server.commands.minion.MinionCommand;
import de.teamlapen.faction.common.server.commands.minion.MinionInventoryCommand;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

public class FactionCommands {

    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, REFERENCE.MOD_ID);

    @SuppressWarnings("unused")
    public static final DeferredHolder<ArgumentTypeInfo<?, ?>, ArgumentTypeInfo<?, ?>> FACTION_ARGUMENT_TYPE = COMMAND_ARGUMENT_TYPES.register("faction", () -> ArgumentTypeInfos.registerByClass(FactionArgument.class, new FactionArgument.Info()));
    @SuppressWarnings("unused")
    public static final DeferredHolder<ArgumentTypeInfo<?, ?>, ArgumentTypeInfo<?, ?>> MINION_ID = COMMAND_ARGUMENT_TYPES.register("minion_id", () -> ArgumentTypeInfos.registerByClass(MinionArgument.class, new MinionArgument.Info()));

    static void register(IEventBus bus) {
        COMMAND_ARGUMENT_TYPES.register(bus);
    }

    @ApiStatus.Internal
    public static void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        List<String> rootCommands = Lists.newArrayList("factions");
        if (!FMLEnvironment.isProduction()) {
            rootCommands.add("f");
            FactionCommand.registerDev(dispatcher, event.getBuildContext());
        }

        for (String rootCommand : rootCommands) {
            var literal = LiteralArgumentBuilder.<CommandSourceStack>literal(rootCommand);
            registerCommand(literal, event.getBuildContext());
            dispatcher.register(literal);
        }
    }

    private static void registerCommand(LiteralArgumentBuilder<CommandSourceStack> parent, CommandBuildContext buildContext) {
        parent.then(ActionsCommand.register(buildContext))
                .then(FactionCommand.register(buildContext))
                .then(CustomizationCommand.register())
                .then(SkillCommand.register(buildContext))
                .then(VillageCommand.register(buildContext))
                .then(MinionCommand.register(buildContext))
                .then(ResetActionsCommand.register())
        ;

    }

}
