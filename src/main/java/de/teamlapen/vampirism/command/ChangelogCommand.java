package de.teamlapen.vampirism.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.lib.lib.util.VersionChecker;
import de.teamlapen.vampirism.VampirismMod;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

/**
 * 
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
            context.getSource().sendFeedback(new TextComponentString("There is no new version available"), true);
            return 0;
        }
        VersionChecker.Version newVersion = VampirismMod.instance.getVersionInfo().getNewVersion();
        List<String> changes = newVersion.getChanges();
        context.getSource().sendFeedback(new TextComponentString(TextFormatting.GREEN + "Vampirism " + newVersion.name + "(" + /* TODO-issue insert Minecraft Version */ ")"), true);
        for (String c : changes) {
            context.getSource().sendFeedback(new TextComponentString("-" + c), true);
        }
        context.getSource().sendFeedback(new TextComponentString(""), true);
        String template = UtilLib.translate("text.vampirism.update_message");
        String homepage = VampirismMod.instance.getVersionInfo().getHomePage();
        template = template.replaceAll("@download@", newVersion.getUrl() == null ? homepage : newVersion.getUrl()).replaceAll("@forum@", homepage);
        ITextComponent component = ITextComponent.Serializer.fromJson(template);
        context.getSource().sendFeedback(component, true);
        return 1;
    }

}
