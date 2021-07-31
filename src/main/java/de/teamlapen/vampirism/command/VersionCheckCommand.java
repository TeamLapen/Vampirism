package de.teamlapen.vampirism.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.lib.lib.util.VersionChecker;
import de.teamlapen.vampirism.VampirismMod;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;

import java.util.List;

public class VersionCheckCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSource, ?> registerChangelog() {
        return setup(Commands.literal("changelog"));

    }

    public static ArgumentBuilder<CommandSource, ?> register() {
        return setup(Commands.literal("checkForUpdate"));
    }

    private static ArgumentBuilder<CommandSource, ?> setup(ArgumentBuilder<CommandSource, ?> builder) {
        return builder.requires(context -> context.hasPermission(PERMISSION_LEVEL_ALL))
                .executes(VersionCheckCommand::changelog);
    }

    private static int changelog(CommandContext<CommandSource> context) {
        if (!VampirismMod.instance.getVersionInfo().isNewVersionAvailable()) {
            context.getSource().sendSuccess(new TranslationTextComponent("command.vampirism.base.changelog.newversion"), false);
            return 0;
        }
        VersionChecker.Version newVersion = VampirismMod.instance.getVersionInfo().getNewVersion();
        List<String> changes = newVersion.getChanges();
        context.getSource().sendSuccess(new StringTextComponent(TextFormatting.GREEN + "Vampirism " + newVersion.name + "(" + SharedConstants.getCurrentVersion().getName() + ")"), true);
        for (String c : changes) {
            context.getSource().sendSuccess(new StringTextComponent("-" + c), false);
        }
        context.getSource().sendSuccess(new StringTextComponent(""), false);
        String homepage = VampirismMod.instance.getVersionInfo().getHomePage();

        ITextComponent download = new TranslationTextComponent("text.vampirism.update_message.download").withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, newVersion.getUrl() == null ? homepage : newVersion.getUrl())).setUnderlined(true).applyFormat(TextFormatting.BLUE));
        ITextComponent changelog = new TranslationTextComponent("text.vampirism.update_message.changelog").withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vampirism changelog")).setUnderlined(true));
        ITextComponent modpage = new TranslationTextComponent("text.vampirism.update_message.modpage").withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, homepage)).setUnderlined(true).applyFormat(TextFormatting.BLUE));
        context.getSource().sendSuccess(new StringTextComponent("").append(download).append(new StringTextComponent(" ")).append(changelog).append(new StringTextComponent(" ")).append(modpage), false);
        return 1;
    }

}
