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
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ModCommands {

    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES = DeferredRegister.create(ForgeRegistries.COMMAND_ARGUMENT_TYPES, REFERENCE.MODID);

    public static final RegistryObject<ArgumentTypeInfo<?, ?>> FACTION_ARGUMENT_TYPE = COMMAND_ARGUMENT_TYPES.register("faction", () -> ArgumentTypeInfos.registerByClass(FactionArgument.class, new FactionArgument.Info()));
    public static final RegistryObject<ArgumentTypeInfo<?, ?>> SKILL = COMMAND_ARGUMENT_TYPES.register("skill", () -> ArgumentTypeInfos.registerByClass(SkillArgument.class, SingletonArgumentInfo.contextFree(SkillArgument::skills)));
    public static final RegistryObject<ArgumentTypeInfo<?, ?>> ACTION = COMMAND_ARGUMENT_TYPES.register("action", () -> ArgumentTypeInfos.registerByClass(ActionArgument.class, SingletonArgumentInfo.contextFree(ActionArgument::actions)));
    public static final RegistryObject<ArgumentTypeInfo<?, ?>> REFINEMENT_SET = COMMAND_ARGUMENT_TYPES.register("refinement_set", () -> ArgumentTypeInfos.registerByClass(RefinementSetArgument.class, SingletonArgumentInfo.contextFree(RefinementSetArgument::set)));
    public static final RegistryObject<ArgumentTypeInfo<?, ?>> BIOME = COMMAND_ARGUMENT_TYPES.register("biome", () -> ArgumentTypeInfos.registerByClass(BiomeArgument.class, SingletonArgumentInfo.contextFree(BiomeArgument::biome)));
    public static final RegistryObject<ArgumentTypeInfo<?, ?>> MINION_ID = COMMAND_ARGUMENT_TYPES.register("minion_id", () -> ArgumentTypeInfos.registerByClass(MinionArgument.class, new MinionArgument.Info()));

    static void register(IEventBus bus) {
        COMMAND_ARGUMENT_TYPES.register(bus);
    }


    public static void registerCommands(@NotNull CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
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
                            .then(BindActionCommand.register())
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
                            .then(RunTestsCommand.register())
                            .then(GarlicCheckCommand.register())
                            .then(SetSwordChargedCommand.register())
                            .then(SetSwordTrainedCommand.register())
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
