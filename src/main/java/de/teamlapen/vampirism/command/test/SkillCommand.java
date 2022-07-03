package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.command.arguments.SkillArgument;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class SkillCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return create(Commands.literal("skills"));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> create(ArgumentBuilder<CommandSourceStack, ?> builder) {
        return builder.requires(context -> context.hasPermission(PERMISSION_LEVEL_ADMIN))
                .then(Commands.argument("type", SkillArgument.skills())
                        .executes(context -> skill(context.getSource(), context.getSource().getPlayerOrException(), SkillArgument.getSkill(context, "type"), false))
                        .then(Commands.literal("force")
                                .executes(context -> skill(context.getSource(), context.getSource().getPlayerOrException(), SkillArgument.getSkill(context, "type"), true))))
                .then(Commands.literal("disableall")
                        .executes(context -> disableall(context.getSource(), context.getSource().getPlayerOrException())));
    }

    private static int disableall(CommandSourceStack commandSource, ServerPlayer asPlayer) {
        IFactionPlayer<?> factionPlayer = asPlayer.isAlive() ? FactionPlayerHandler.getCurrentFactionPlayer(asPlayer).orElse(null) : null;
        if (factionPlayer == null) {
            commandSource.sendSuccess(Component.translatable("command.vampirism.test.skill.noinfaction"), false);
            return 0;
        }
        factionPlayer.getSkillHandler().resetSkills();
        return 0;
    }

    @SuppressWarnings("unchecked")
    private static int skill(CommandSourceStack commandSource, ServerPlayer asPlayer, @SuppressWarnings("rawtypes") ISkill skill, boolean force) {
        IFactionPlayer<?> factionPlayer = asPlayer.isAlive() ? FactionPlayerHandler.getCurrentFactionPlayer(asPlayer).orElse(null) : null;
        if (factionPlayer == null) {
            commandSource.sendSuccess(Component.translatable("command.vampirism.test.skill.noinfaction"), false);
            return 0;
        }
        if (factionPlayer.getSkillHandler().isSkillEnabled(skill)) {
            factionPlayer.getSkillHandler().disableSkill(skill);
            commandSource.sendSuccess(Component.translatable("command.vampirism.test.skill.disabled"), false);
            return 0;
        }
        ISkillHandler.Result result = factionPlayer.getSkillHandler().canSkillBeEnabled(skill);
        if (force) {
            result = ISkillHandler.Result.OK;
        }
        switch (result) {
            case OK:
                factionPlayer.getSkillHandler().enableSkill(skill);
                commandSource.sendSuccess(Component.translatable("command.vampirism.test.skill.enabled", RegUtil.id(skill)  + " (" + skill.getName().getString() + ")"), false);
                break;
            case ALREADY_ENABLED:
                commandSource.sendSuccess(Component.translatable("command.vampirism.test.skill.alreadyenabled", skill.getName()), false);
                break;
            case PARENT_NOT_ENABLED:
                ISkill<?>[] skills = factionPlayer.getSkillHandler().getParentSkills(skill);
                if (skills == null || skills.length == 0) return 0;
                if (skills.length == 1)
                    commandSource.sendSuccess(Component.translatable("command.vampirism.test.skill.parent", RegUtil.id(skills[0])), false);
                else
                    commandSource.sendSuccess(Component.translatable("command.vampirism.test.skill.parents", RegUtil.id(skills[0]), RegUtil.id(skills[1])), false);
                break;
            case NO_POINTS:
                commandSource.sendSuccess(Component.translatable("command.vampirism.test.skill.nopoints"), false);
                break;
            case OTHER_NODE_SKILL:
                commandSource.sendSuccess(Component.translatable("command.vampirism.test.skill.othernode"), false);
                break;
            case NOT_FOUND:
                commandSource.sendSuccess(Component.translatable("command.vampirism.test.skill.otherfaction"), false);
                break;
            case LOCKED_BY_OTHER_NODE:
                commandSource.sendSuccess(Component.translatable("command.vampirism.test.skill.locked"), false);
                break;
            case LOCKED_BY_PLAYER_STATE:
                commandSource.sendSuccess(Component.translatable("command.vampirism.test.skill.locked_player_state"), false);
                break;
        }
        return 0;
    }
}
