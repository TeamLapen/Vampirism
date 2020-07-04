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

/**
 * @authors Cheaterpaul, Maxanier
 */
public class ChangelogCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("changelog")
                .requires(context -> context.hasPermissionLevel(PERMISSION_LEVEL_ALL))
                .executes(context -> {
                    return changelog(context);
                });
    }

    private static int changelog(CommandContext<CommandSource> context) {
        if (!VampirismMod.instance.getVersionInfo().isNewVersionAvailable()) {
            context.getSource().sendFeedback(new TranslationTextComponent("command.vampirism.base.changelog.newversion"), false);
            return 0;
        }
        VersionChecker.Version newVersion = VampirismMod.instance.getVersionInfo().getNewVersion();
        List<String> changes = newVersion.getChanges();
        context.getSource().sendFeedback(new StringTextComponent(TextFormatting.GREEN + "Vampirism " + newVersion.name + "(" + SharedConstants.getVersion().getName() + ")"), true);
        for (String c : changes) {
            context.getSource().sendFeedback(new StringTextComponent("-" + c), false);
        }
        context.getSource().sendFeedback(new StringTextComponent(""), false);
        String homepage = VampirismMod.instance.getVersionInfo().getHomePage();

        ITextComponent download = new TranslationTextComponent("text.vampirism.update_message.download").func_240700_a_(style -> style.func_240715_a_(new ClickEvent(ClickEvent.Action.OPEN_URL, newVersion.getUrl() == null ? homepage : newVersion.getUrl())).setUnderlined(true).func_240712_a_(TextFormatting.BLUE));
        ITextComponent changelog = new TranslationTextComponent("text.vampirism.update_message.changelog").func_240700_a_(style -> style.func_240715_a_(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vampirism changelog")).setUnderlined(true));
        ITextComponent modpage = new TranslationTextComponent("text.vampirism.update_message.modpage").func_240700_a_(style -> style.func_240715_a_(new ClickEvent(ClickEvent.Action.OPEN_URL, homepage)).setUnderlined(true).func_240712_a_(TextFormatting.BLUE));
        context.getSource().sendFeedback(new StringTextComponent("").func_230529_a_(download).func_230529_a_(new StringTextComponent(" ")).func_230529_a_(changelog).func_230529_a_(new StringTextComponent(" ")).func_230529_a_(modpage), false);
        return 1;
    }

}
