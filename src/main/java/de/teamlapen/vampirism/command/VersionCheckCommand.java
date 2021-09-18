package de.teamlapen.vampirism.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.lib.lib.util.VersionChecker;
import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.List;

public class VersionCheckCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return setup(Commands.literal("checkForUpdate"));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> setup(ArgumentBuilder<CommandSourceStack, ?> builder) {
        return builder.requires(context -> context.hasPermission(PERMISSION_LEVEL_ALL))
                .executes(VersionCheckCommand::changelog);
    }

    private static int changelog(CommandContext<CommandSourceStack> context) {
        if (!VampirismMod.instance.getVersionInfo().isNewVersionAvailable()) {
            context.getSource().sendSuccess(new TranslatableComponent("command.vampirism.base.changelog.newversion"), false);
            return 0;
        }
        VersionChecker.Version newVersion = VampirismMod.instance.getVersionInfo().getNewVersion();
        List<String> changes = newVersion.getChanges();
        context.getSource().sendSuccess(new TextComponent(ChatFormatting.GREEN + "Vampirism " + newVersion.name + "(" + SharedConstants.getCurrentVersion().getName() + ")"), true);
        for (String c : changes) {
            context.getSource().sendSuccess(new TextComponent("-" + c), false);
        }
        context.getSource().sendSuccess(new TextComponent(""), false);
        String homepage = VampirismMod.instance.getVersionInfo().getHomePage();

        Component download = new TranslatableComponent("text.vampirism.update_message.download").withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, newVersion.getUrl() == null ? homepage : newVersion.getUrl())).setUnderlined(true).applyFormat(ChatFormatting.BLUE));
        Component changelog = new TranslatableComponent("text.vampirism.update_message.changelog").withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vampirism checkForUpdate")).setUnderlined(true));
        Component modpage = new TranslatableComponent("text.vampirism.update_message.modpage").withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, homepage)).setUnderlined(true).applyFormat(ChatFormatting.BLUE));
        context.getSource().sendSuccess(new TextComponent("").append(download).append(new TextComponent(" ")).append(changelog).append(new TextComponent(" ")).append(modpage), false);
        return 1;
    }

}
