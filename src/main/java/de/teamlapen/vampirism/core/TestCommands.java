package de.teamlapen.vampirism.core;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.command.test.*;

import net.minecraft.command.CommandSource;

import java.util.List;

/**
 * Command for testing and debugging
 * 
 * @author Cheaterpaul
 */
public class TestCommands {

    private final List<String> aliases = Lists.newArrayList();

    public TestCommands(CommandDispatcher<CommandSource> dispatcher) {
        aliases.add("vampirism-test");
        if (VampirismMod.inDev) {
            aliases.add("vtest");
        }
        for (String s : aliases) {
            dispatcher.register(
                    LiteralArgumentBuilder.<CommandSource>literal(s)
                            .then(InfoEntitiesCommand.register())
                            .then(MarkerCommand.register())
                            .then(GiveTestTargetMapCommand.register())
                            .then(GarlicProfilerCommand.register())
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
                            .then(HealCommand.register()));
        }
    }
}
