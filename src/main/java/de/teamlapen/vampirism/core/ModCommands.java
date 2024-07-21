package de.teamlapen.vampirism.core;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.command.*;
import de.teamlapen.vampirism.command.arguments.FactionArgument;
import de.teamlapen.vampirism.command.arguments.MinionArgument;
import de.teamlapen.vampirism.command.test.*;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ModCommands {

    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, REFERENCE.MODID);

    @SuppressWarnings("unused")
    public static final DeferredHolder<ArgumentTypeInfo<?, ?>, ArgumentTypeInfo<?, ?>> FACTION_ARGUMENT_TYPE = COMMAND_ARGUMENT_TYPES.register("faction", () -> ArgumentTypeInfos.registerByClass(FactionArgument.class, new FactionArgument.Info()));
    @SuppressWarnings("unused")
    public static final DeferredHolder<ArgumentTypeInfo<?, ?>, ArgumentTypeInfo<?, ?>> MINION_ID = COMMAND_ARGUMENT_TYPES.register("minion_id", () -> ArgumentTypeInfos.registerByClass(MinionArgument.class, new MinionArgument.Info()));

    static void register(IEventBus bus) {
        COMMAND_ARGUMENT_TYPES.register(bus);
    }


    static void registerCommands(@NotNull RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        CommandBuildContext buildContext = event.getBuildContext();

        List<String> vampirism = Lists.newArrayList("vampirism");
        if (VampirismMod.inDev) {
            vampirism.add("v");
        }

        //Vampirism commands
        for (String s : vampirism) {
            dispatcher.register(
                    LiteralArgumentBuilder.<CommandSourceStack>literal(s)
                            .then(BindActionCommand.register(buildContext))
                            .then(AppearanceCommand.register())
                            .then(LevelCommand.register(buildContext))
                            .then(LordCommand.register())
                            .then(LevelUpCommand.register())
                            .then(GenderCommand.register())
                            .then(BloodBarCommand.register())
                            .then(ConfigCommand.register(dispatcher, buildContext))
                            .then(SkillCommand.register(buildContext))
                            .then(MinionInventoryCommand.register(buildContext))
                            .then(VampireSwordCommand.register())
                            .then(VillageCommand.register(buildContext))
                            .then(MinionCommand.register())
                            .then(ResetActionsCommand.register())
                            .then(Commands.literal("test")
                                    .then(InfoEntitiesCommand.register())
                                    .then(MarkerCommand.register())
                                    .then(EntityCommand.register())
                                    .then(InfoEntityCommand.register())
                                    .then(MakeVillagerAgressiveCommand.register())
                                    .then(TentCommand.register())
                                    .then(GarlicCheckCommand.register())
                                    .then(SpawnTestAnimalCommand.register())
                                    .then(HealCommand.register())
                                    .then(TaskCommand.register())
                                    .then(ForcePlayerSyncCommand.register())
                                    .then(SummonDummy.register())
                                    .then(GiveBannerCommand.register(buildContext))
                            )
            );
        }
    }

}
