package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.command.arguments.SkillArgument;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class SkillCommand extends BasicCommand {

    private static final SimpleCommandExceptionType NO_FACTION = new SimpleCommandExceptionType(Component.translatable("command.vampirism.test.skill.noinfaction"));

    public static ArgumentBuilder<CommandSourceStack, ?> registerTest() {
        return create(Commands.literal("skill"));
    }

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
                        .executes(context -> {
                            return disableall(context.getSource(), context.getSource().getPlayerOrException());
                        }))
                .then(Commands.literal("enableall")
                        .executes(context -> {
                            return enableAll(context.getSource(), context.getSource().getPlayerOrException());
                        }));
    }

    private static int disableall(CommandSourceStack commandSource, ServerPlayer asPlayer) throws CommandSyntaxException {
        IFactionPlayer<?> factionPlayer = FactionPlayerHandler.getCurrentFactionPlayer(asPlayer).orElseThrow(NO_FACTION::create);
        factionPlayer.getSkillHandler().resetSkills();
        commandSource.sendSuccess(Component.translatable("command.vampirism.test.skill.all_locked"), false);
        return 0;
    }

    private static int enableAll(CommandSourceStack commandSource, ServerPlayer asPlayer) throws CommandSyntaxException {
        IFactionPlayer<?> factionPlayer = FactionPlayerHandler.getCurrentFactionPlayer(asPlayer).orElseThrow(NO_FACTION::create);
        ISkillHandler<?> skillHandler = factionPlayer.getSkillHandler();
        for (ISkill<?> skill : RegUtil.values(ModRegistries.SKILLS)) {
            if (skill.getFaction().map(f -> f != factionPlayer.getFaction()).orElse(false)) continue;
            skillHandler.enableSkill((ISkill)skill);
        }
        commandSource.sendSuccess(Component.translatable("command.vampirism.test.skill.all_unlocked"), false);
        return 0;
    }

    private static int skill(CommandSourceStack commandSource, ServerPlayer asPlayer, ISkill skill, boolean force) throws CommandSyntaxException {
        IFactionPlayer<?> factionPlayer = FactionPlayerHandler.getCurrentFactionPlayer(asPlayer).orElseThrow(NO_FACTION::create);
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
            case OK -> {
                factionPlayer.getSkillHandler().enableSkill(skill);
                commandSource.sendSuccess(Component.translatable("command.vampirism.test.skill.enabled", RegUtil.id(skill) + " (" + skill.getName().getString() + ")"), false);
            }
            case ALREADY_ENABLED -> commandSource.sendSuccess(Component.translatable("command.vampirism.test.skill.alreadyenabled", skill.getName()), false);
            case PARENT_NOT_ENABLED -> {
                ISkill<?>[] skills = factionPlayer.getSkillHandler().getParentSkills(skill);
                if (skills == null || skills.length == 0) return 0;
                if (skills.length == 1)
                    commandSource.sendFailure(Component.translatable("command.vampirism.test.skill.parent", RegUtil.id(skills[0])));
                else
                    commandSource.sendFailure(Component.translatable("command.vampirism.test.skill.parents", RegUtil.id(skills[0]), RegUtil.id(skills[1])));
            }
            case NO_POINTS -> commandSource.sendFailure(Component.translatable("command.vampirism.test.skill.nopoints"));
            case OTHER_NODE_SKILL -> commandSource.sendFailure(Component.translatable("command.vampirism.test.skill.othernode"));
            case NOT_FOUND -> commandSource.sendFailure(Component.translatable("command.vampirism.test.skill.otherfaction"));
            case LOCKED_BY_OTHER_NODE -> commandSource.sendFailure(Component.translatable("command.vampirism.test.skill.locked"));
            case LOCKED_BY_PLAYER_STATE -> commandSource.sendFailure(Component.translatable("command.vampirism.test.skill.locked_player_state"));
        }
        return 0;
    }
}
