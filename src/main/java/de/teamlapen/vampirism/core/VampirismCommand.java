package de.teamlapen.vampirism.core;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.command.BindActionCommand;
import de.teamlapen.vampirism.command.ChangelogCommand;
import de.teamlapen.vampirism.command.CheckForVampireBiomeCommand;
import de.teamlapen.vampirism.command.CurrentDimensionCommand;
import de.teamlapen.vampirism.command.EyeCommand;
import de.teamlapen.vampirism.command.FangCommand;
import de.teamlapen.vampirism.command.GlowingEyeCommand;
import de.teamlapen.vampirism.command.LevelCommand;
import de.teamlapen.vampirism.command.LevelUpCommand;
import de.teamlapen.vampirism.command.ResetBalanceCommand;

import net.minecraft.command.CommandSource;

import java.util.List;

/**
 * Central command for this mod
 */
public class VampirismCommand {

    private final List<String> aliases = Lists.newArrayList();

    public VampirismCommand(CommandDispatcher<CommandSource> dispatcher) {
        aliases.add("vampirism");
        if (VampirismMod.inDev)
            aliases.add("v");
        for (String s : aliases) {
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
    }
}
