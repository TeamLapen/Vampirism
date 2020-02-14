package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.command.arguments.SkillArgument;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * @authors Cheaterpaul, Maxanier
 */
public class SkillCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("skill")
                .requires(context -> context.hasPermissionLevel(PERMISSION_LEVEL_ADMIN))
                .then(Commands.argument("type", SkillArgument.skills())
                        .executes(context -> {
                            return skill(context.getSource(), context.getSource().asPlayer(), SkillArgument.getSkill(context, "type"));
                        }))
                .then(Commands.literal("disableall")
                        .executes(context -> {
                            return disableall(context.getSource(), context.getSource().asPlayer());
                        }));
    }

    private static int disableall(CommandSource commandSource, ServerPlayerEntity asPlayer) {
        IFactionPlayer factionPlayer = asPlayer.isAlive() ? FactionPlayerHandler.get(asPlayer).getCurrentFactionPlayer() : null;
        if (factionPlayer == null) {
            commandSource.sendFeedback(new TranslationTextComponent("command.vampirism.test.skill.noinfaction"), false);
            return 0;
        }
        factionPlayer.getSkillHandler().resetSkills();
        return 0;
    }

    private static int skill(CommandSource commandSource, ServerPlayerEntity asPlayer, ISkill skill) {
        IFactionPlayer factionPlayer = asPlayer.isAlive() ? FactionPlayerHandler.get(asPlayer).getCurrentFactionPlayer() : null;
        if (factionPlayer == null) {
            commandSource.sendFeedback(new TranslationTextComponent("command.vampirism.test.skill.noinfaction"), false);
            return 0;
        }
        if (factionPlayer.getSkillHandler().isSkillEnabled(skill)) {
            factionPlayer.getSkillHandler().disableSkill(skill);
            commandSource.sendFeedback(new TranslationTextComponent("command.vampirism.test.skill.disabled"), false);
            return 0;
        }
        ISkillHandler.Result result = factionPlayer.getSkillHandler().canSkillBeEnabled(skill);
        switch (result) {
            case OK:
                factionPlayer.getSkillHandler().enableSkill(skill);
                commandSource.sendFeedback(new TranslationTextComponent("command.vampirism.test.skill.enabled", skill.getRegistryName() + " (" + new TranslationTextComponent(skill.getTranslationKey()).getFormattedText() + ")"), false);
                return 0;
            case ALREADY_ENABLED:
                commandSource.sendFeedback(new TranslationTextComponent("command.vampirism.test.skill.alreadyenabled", new TranslationTextComponent(skill.getTranslationKey())), false);
                return 0;
            case PARENT_NOT_ENABLED:
                ISkill[] skills = factionPlayer.getSkillHandler().getParentSkills(skill);
                if (skills == null || skills.length == 0) return 0;
                if (skills.length == 1)
                    commandSource.sendFeedback(new TranslationTextComponent("command.vampirism.test.skill.parent", skills[0].getRegistryName()), false);
                else
                    commandSource.sendFeedback(new TranslationTextComponent("command.vampirism.test.skill.parents", skills[0].getRegistryName(), skills[1].getRegistryName()), false);
                return 0;
            case NO_POINTS:
                commandSource.sendFeedback(new TranslationTextComponent("command.vampirism.test.skill.nopoints"), false);
                return 0;
            case OTHER_NODE_SKILL:
                commandSource.sendFeedback(new TranslationTextComponent("command.vampirism.test.skill.othernode"), false);
                return 0;
            case NOT_FOUND:
                commandSource.sendFeedback(new TranslationTextComponent("command.vampirism.test.skill.otherfaction"), false);
                return 0;
            default:
        }
        return 0;
    }
}
