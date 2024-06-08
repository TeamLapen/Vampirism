package de.teamlapen.vampirism.core;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.command.*;
import de.teamlapen.vampirism.command.arguments.*;
import de.teamlapen.vampirism.command.test.*;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ModCommands {

    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, REFERENCE.MODID);

    public static final DeferredHolder<ArgumentTypeInfo<?, ?>, ArgumentTypeInfo<?, ?>> FACTION_ARGUMENT_TYPE = COMMAND_ARGUMENT_TYPES.register("faction", () -> ArgumentTypeInfos.registerByClass(FactionArgument.class, new FactionArgument.Info()));
    public static final DeferredHolder<ArgumentTypeInfo<?, ?>, ArgumentTypeInfo<?, ?>> SKILL = COMMAND_ARGUMENT_TYPES.register("skill", () -> ArgumentTypeInfos.registerByClass(SkillArgument.class, SingletonArgumentInfo.contextFree(SkillArgument::skills)));
    public static final DeferredHolder<ArgumentTypeInfo<?, ?>, ArgumentTypeInfo<?, ?>> REFINEMENT_SET = COMMAND_ARGUMENT_TYPES.register("refinement_set", () -> ArgumentTypeInfos.registerByClass(RefinementSetArgument.class, SingletonArgumentInfo.contextFree(RefinementSetArgument::set)));
    public static final DeferredHolder<ArgumentTypeInfo<?, ?>, ArgumentTypeInfo<?, ?>> BIOME = COMMAND_ARGUMENT_TYPES.register("biome", () -> ArgumentTypeInfos.registerByClass(BiomeArgument.class, SingletonArgumentInfo.contextFree(BiomeArgument::biome)));
    public static final DeferredHolder<ArgumentTypeInfo<?, ?>, ArgumentTypeInfo<?, ?>> MINION_ID = COMMAND_ARGUMENT_TYPES.register("minion_id", () -> ArgumentTypeInfos.registerByClass(MinionArgument.class, new MinionArgument.Info()));

    static void register(IEventBus bus) {
        COMMAND_ARGUMENT_TYPES.register(bus);
    }


    static void registerCommands(@NotNull RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        CommandBuildContext buildContext = event.getBuildContext();

        List<String> vampirism = Lists.newArrayList("vampirism");
        List<String> test = Lists.newArrayList("vampirism-test");
        if (VampirismMod.inDev) {
            vampirism.add("v");
            test.add("vtest");
        }

        //Vampirism commands
        for (String s : vampirism) {
            dispatcher.register(
                    LiteralArgumentBuilder.<CommandSourceStack>literal(s)
                            .then(BindActionCommand.register(buildContext))
                            .then(CurrentDimensionCommand.register())
                            .then(EyeCommand.register())
                            .then(FangCommand.register())
                            .then(GlowingEyeCommand.register())
                            .then(LevelCommand.register())
                            .then(LordCommand.register())
                            .then(LevelUpCommand.register())
                            .then(GenderCommand.register())
                            .then(BloodBarCommand.register())
                            .then(ConfigCommand.register(dispatcher, buildContext))
                            .then(SkillCommand.register())
                            .then(MinionInventoryCommand.register(buildContext))
            );
        }

        //Test commands
        for (String s : test) {
            dispatcher.register(
                    LiteralArgumentBuilder.<CommandSourceStack>literal(s)
                            .then(InfoEntitiesCommand.register())
                            .then(MarkerCommand.register())
                            .then(EntityCommand.register())
                            .then(InfoEntityCommand.register())
                            .then(BiomeCommand.register())
                            .then(MakeVillagerAgressiveCommand.register())
                            .then(ResetActionsCommand.register())
                            .then(TentCommand.register())
                            .then(VampireBookCommand.register())
                            .then(GarlicCheckCommand.register())
                            .then(VampireSwordCommand.register())
                            .then(SpawnTestAnimalCommand.register())
                            .then(HealCommand.register())
                            .then(VillageCommand.register())
                            .then(MinionCommand.register())
                            .then(TaskCommand.register())
                            .then(ForcePlayerSyncCommand.register())
                            .then(GiveAccessoriesCommand.register())
                            .then(SummonDummy.register())
                            .then(GiveBannerCommand.register())
            );
        }
    }

}
