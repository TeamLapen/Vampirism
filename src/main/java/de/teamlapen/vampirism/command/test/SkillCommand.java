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

public class SkillCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSource, ?> registerTest() {
        return create(Commands.literal("skill"));
    }

    public static ArgumentBuilder<CommandSource, ?> register() {
        return create(Commands.literal("skills"));
    }

    private static ArgumentBuilder<CommandSource, ?> create(ArgumentBuilder<CommandSource, ?> builder) {
        return builder.requires(context -> context.hasPermission(PERMISSION_LEVEL_ADMIN))
                .then(Commands.argument("type", SkillArgument.skills())
                        .executes(context -> {
                            return skill(context.getSource(), context.getSource().getPlayerOrException(), SkillArgument.getSkill(context, "type"), false);
                        })
                        .then(Commands.literal("force")
                                .executes(context -> {
                                    return skill(context.getSource(), context.getSource().getPlayerOrException(), SkillArgument.getSkill(context, "type"), true);
                                })))
                .then(Commands.literal("disableall")
                        .executes(context -> {
                            return disableall(context.getSource(), context.getSource().getPlayerOrException());
                        }));
    }

    private static int disableall(CommandSource commandSource, ServerPlayerEntity asPlayer) {
        IFactionPlayer factionPlayer = asPlayer.isAlive() ? FactionPlayerHandler.get(asPlayer).getCurrentFactionPlayer().orElse(null) : null;
        if (factionPlayer == null) {
            commandSource.sendSuccess(new TranslationTextComponent("command.vampirism.test.skill.noinfaction"), false);
            return 0;
        }
        factionPlayer.getSkillHandler().resetSkills();
        return 0;
    }

    private static int skill(CommandSource commandSource, ServerPlayerEntity asPlayer, ISkill skill, boolean force) {
        IFactionPlayer factionPlayer = asPlayer.isAlive() ? FactionPlayerHandler.get(asPlayer).getCurrentFactionPlayer().orElse(null) : null;
        if (factionPlayer == null) {
            commandSource.sendSuccess(new TranslationTextComponent("command.vampirism.test.skill.noinfaction"), false);
            return 0;
        }
        if (factionPlayer.getSkillHandler().isSkillEnabled(skill)) {
            factionPlayer.getSkillHandler().disableSkill(skill);
            commandSource.sendSuccess(new TranslationTextComponent("command.vampirism.test.skill.disabled"), false);
            return 0;
        }
        ISkillHandler.Result result = factionPlayer.getSkillHandler().canSkillBeEnabled(skill);
        if (force) {
            result = ISkillHandler.Result.OK;
        }
        switch (result) {
            case OK:
                factionPlayer.getSkillHandler().enableSkill(skill);
                commandSource.sendSuccess(new TranslationTextComponent("command.vampirism.test.skill.enabled", skill.getRegistryName() + " (" + skill.getName().getString() + ")"), false);
                return 0;
            case ALREADY_ENABLED:
                commandSource.sendSuccess(new TranslationTextComponent("command.vampirism.test.skill.alreadyenabled", skill.getName()), false);
                return 0;
            case PARENT_NOT_ENABLED:
                ISkill[] skills = factionPlayer.getSkillHandler().getParentSkills(skill);
                if (skills == null || skills.length == 0) return 0;
                if (skills.length == 1)
                    commandSource.sendSuccess(new TranslationTextComponent("command.vampirism.test.skill.parent", skills[0].getRegistryName()), false);
                else
                    commandSource.sendSuccess(new TranslationTextComponent("command.vampirism.test.skill.parents", skills[0].getRegistryName(), skills[1].getRegistryName()), false);
                return 0;
            case NO_POINTS:
                commandSource.sendSuccess(new TranslationTextComponent("command.vampirism.test.skill.nopoints"), false);
                return 0;
            case OTHER_NODE_SKILL:
                commandSource.sendSuccess(new TranslationTextComponent("command.vampirism.test.skill.othernode"), false);
                return 0;
            case NOT_FOUND:
                commandSource.sendSuccess(new TranslationTextComponent("command.vampirism.test.skill.otherfaction"), false);
                return 0;
            case LOCKED_BY_OTHER_NODE:
                commandSource.sendSuccess(new TranslationTextComponent("command.vampirism.test.skill.locked"), false);
                return 0;
            case LOCKED_BY_PLAYER_STATE:
                commandSource.sendSuccess(new TranslationTextComponent("command.vampirism.test.skill.locked_player_state"), false);
                return 0;
            default:
        }
        return 0;
    }
}
