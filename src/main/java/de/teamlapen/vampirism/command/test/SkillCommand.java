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
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

public class SkillCommand extends BasicCommand {

    private static final SimpleCommandExceptionType NO_FACTION = new SimpleCommandExceptionType(new TranslationTextComponent("command.vampirism.test.skill.noinfaction"));

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
                        }))
                .then(Commands.literal("enableall")
                        .executes(context -> {
                            return enableAll(context.getSource(), context.getSource().getPlayerOrException());
                        }));
    }

    private static int disableall(CommandSource commandSource, ServerPlayerEntity asPlayer) throws CommandSyntaxException {
        IFactionPlayer<?> factionPlayer = FactionPlayerHandler.getCurrentFactionPlayer(asPlayer).orElseThrow(NO_FACTION::create);
        factionPlayer.getSkillHandler().resetSkills();
        commandSource.sendSuccess(new TranslationTextComponent("command.vampirism.test.skill.all_locked"), false);
        return 0;
    }

    private static int enableAll(CommandSource commandSource, ServerPlayerEntity asPlayer) throws CommandSyntaxException {
        IFactionPlayer<?> factionPlayer = FactionPlayerHandler.getCurrentFactionPlayer(asPlayer).orElseThrow(NO_FACTION::create);
        ISkillHandler<?> skillHandler = factionPlayer.getSkillHandler();
        for (ISkill skill : ModRegistries.SKILLS.getValues()) {
            if (skill.getFaction() != factionPlayer.getFaction()) continue;
            skillHandler.enableSkill(skill);
        }
        commandSource.sendSuccess(new TranslationTextComponent("command.vampirism.test.skill.all_unlocked"), false);
        return 0;
    }

    private static int skill(CommandSource commandSource, ServerPlayerEntity asPlayer, ISkill skill, boolean force) throws CommandSyntaxException {
        IFactionPlayer<?> factionPlayer = FactionPlayerHandler.getCurrentFactionPlayer(asPlayer).orElseThrow(NO_FACTION::create);
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
                    commandSource.sendFailure(new TranslationTextComponent("command.vampirism.test.skill.parent", skills[0].getRegistryName()));
                else
                    commandSource.sendFailure(new TranslationTextComponent("command.vampirism.test.skill.parents", skills[0].getRegistryName(), skills[1].getRegistryName()));
                return 0;
            case NO_POINTS:
                commandSource.sendFailure(new TranslationTextComponent("command.vampirism.test.skill.nopoints"));
                return 0;
            case OTHER_NODE_SKILL:
                commandSource.sendFailure(new TranslationTextComponent("command.vampirism.test.skill.othernode"));
                return 0;
            case NOT_FOUND:
                commandSource.sendFailure(new TranslationTextComponent("command.vampirism.test.skill.otherfaction"));
                return 0;
            case LOCKED_BY_OTHER_NODE:
                commandSource.sendFailure(new TranslationTextComponent("command.vampirism.test.skill.locked"));
                return 0;
            case LOCKED_BY_PLAYER_STATE:
                commandSource.sendFailure(new TranslationTextComponent("command.vampirism.test.skill.locked_player_state"));
                return 0;
            default:
        }
        return 0;
    }
}
