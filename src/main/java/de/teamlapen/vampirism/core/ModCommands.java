package de.teamlapen.vampirism.core;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.command.*;
import de.teamlapen.vampirism.command.arguments.*;
import de.teamlapen.vampirism.command.test.*;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.ArgumentSerializer;
import net.minecraft.command.arguments.ArgumentTypes;

import java.util.List;

public class ModCommands {

    public static void registerCommands(CommandDispatcher<CommandSource> dispatcher) {
        List<String> vampirism = Lists.newArrayList("vampirism");
        List<String> test = Lists.newArrayList("vampirism-test");
        if (VampirismMod.inDev) {
            vampirism.add("v");
            test.add("vtest");
        }

        //Vampirism commands
        for (String s : vampirism) {
            dispatcher.register(
                    LiteralArgumentBuilder.<CommandSource>literal(s)
                            .then(BindActionCommand.register())
                            .then(VersionCheckCommand.registerChangelog())//TODO 1.17 remove
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
                            .then(MinionInventoryCommand.register())
            );
        }

        //Test commands
        for (String s : test) {
            dispatcher.register(
                    LiteralArgumentBuilder.<CommandSource>literal(s)
                            .then(InfoEntitiesCommand.register())
                            .then(MarkerCommand.register())
                            .then(SkillCommand.registerTest())//TODO 1.17 remove
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
        ArgumentTypes.register("vampirism:faction", FactionArgument.class, new ArgumentSerializer<>(FactionArgument::new));
        ArgumentTypes.register("vampirism:skill", SkillArgument.class, new ArgumentSerializer<>(SkillArgument::new));
        ArgumentTypes.register("vampirism:action", ActionArgument.class, new ArgumentSerializer<>(ActionArgument::new));
        ArgumentTypes.register("vampirism:refinement_set", RefinementSetArgument.class, new ArgumentSerializer<>(RefinementSetArgument::new));
        ArgumentTypes.register("vampirism:task", TaskArgument.class, new ArgumentSerializer<>(TaskArgument::new));
        ArgumentTypes.register("vampirism:biome", BiomeArgument.class, new ArgumentSerializer<>(BiomeArgument::new));
        ArgumentTypes.register("vampirism:minion", MinionArgument.class, new MinionArgument.MinionArgumentSerializer());
    }
}
