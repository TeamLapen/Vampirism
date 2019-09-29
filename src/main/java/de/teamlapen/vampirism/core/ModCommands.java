package de.teamlapen.vampirism.core;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.command.*;
import de.teamlapen.vampirism.command.arguments.ActionArgument;
import de.teamlapen.vampirism.command.arguments.FactionArgument;
import de.teamlapen.vampirism.command.arguments.SkillArgument;
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
                            .then(ChangelogCommand.register())
                            .then(CheckForVampireBiomeCommand.register())
                            .then(CurrentDimensionCommand.register())
                            .then(EyeCommand.register())
                            .then(FangCommand.register())
                            .then(GlowingEyeCommand.register())
                            .then(LevelCommand.register())
                            .then(LevelUpCommand.register())
                            .then(ResetBalanceCommand.register())
            );
        }

        //Test commands
        for (String s : test) {
            dispatcher.register(
                    LiteralArgumentBuilder.<CommandSource>literal(s)
                            .then(InfoEntitiesCommand.register())
                            .then(MarkerCommand.register())
                            .then(GiveTestTargetMapCommand.register())
                            .then(SkillCommand.register())
                            .then(EmptyBloodBarCommand.register())
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
                            .then(PlaceCommand.register())
                            .then(HalloweenCommand.register())
                            .then(SetSwordChargedCommand.register())
                            .then(SetSwordTrainedCommand.register())
                            .then(SpawnTestAnimalCommand.register())
                            .then(HealCommand.register())
                            .then(CaptureVillageCommand.register()));
        }
    }

    public static void registerArgumentTypes() {
        ArgumentTypes.register("vampirism_faction", FactionArgument.class, new ArgumentSerializer<>(FactionArgument::new));
        ArgumentTypes.register("vampirism_skill", SkillArgument.class, new ArgumentSerializer<>(SkillArgument::new));
        ArgumentTypes.register("vampirism_action", ActionArgument.class, new ArgumentSerializer<>(ActionArgument::new));

    }
}
