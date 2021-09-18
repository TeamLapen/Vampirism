package de.teamlapen.vampirism.core;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.command.*;
import de.teamlapen.vampirism.command.arguments.*;
import de.teamlapen.vampirism.command.test.*;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.commands.synchronization.EmptyArgumentSerializer;

import java.util.List;

public class ModCommands {

    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
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
                            .then(VersionCheckCommand.register())
                            .then(CurrentDimensionCommand.register())
                            .then(EyeCommand.register())
                            .then(FangCommand.register())
                            .then(GlowingEyeCommand.register())
                            .then(LevelCommand.register())
                            .then(LordCommand.register())
                            .then(LevelUpCommand.register())
                            .then(GenderCommand.register())
                            .then(BloodBarCommand.register())
                            .then(ConfigCommand.register())
                            .then(SkillCommand.register())
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
                            .then(DebugGenCommand.register())
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

    /**
     * Make sure to use deferred queue
     */
    static void registerArgumentTypesUsage() {
        ArgumentTypes.register("vampirism_faction", FactionArgument.class, new EmptyArgumentSerializer<>(FactionArgument::new));
        ArgumentTypes.register("vampirism_skill", SkillArgument.class, new EmptyArgumentSerializer<>(SkillArgument::new));
        ArgumentTypes.register("vampirism_action", ActionArgument.class, new EmptyArgumentSerializer<>(ActionArgument::new));
        ArgumentTypes.register("vampirism_refinement_set", RefinementSetArgument.class, new EmptyArgumentSerializer<>(RefinementSetArgument::new));
        ArgumentTypes.register("vampirism_task", TaskArgument.class, new EmptyArgumentSerializer<>(TaskArgument::new));
        ArgumentTypes.register("vampirism_biome", BiomeArgument.class, new EmptyArgumentSerializer<>(BiomeArgument::new));
    }
}
