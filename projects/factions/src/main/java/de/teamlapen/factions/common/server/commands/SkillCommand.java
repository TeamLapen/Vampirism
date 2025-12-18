package de.teamlapen.factions.common.server.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import de.teamlapen.factions.api.FactionRegistries;
import de.teamlapen.factions.api.factions.IFaction;
import de.teamlapen.factions.api.factions.refinements.IRefinementHandler;
import de.teamlapen.factions.api.factions.skills.ISkill;
import de.teamlapen.factions.api.factions.skills.ISkillHandler;
import de.teamlapen.factions.api.factions.skills.ISkillPlayer;
import de.teamlapen.factions.api.world.entities.player.IFactionPlayer;
import de.teamlapen.factions.common.core.ModRegistries;
import de.teamlapen.factions.common.factions.FactionPlayerHandler;
import de.teamlapen.factions.common.server.commands.arguments.SkillArgument;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

public class SkillCommand extends BasicCommand {

    private static final SimpleCommandExceptionType NO_FACTION = new SimpleCommandExceptionType(Component.translatable("command.vampirism.test.skill.noinfaction"));

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandBuildContext buildContext) {
        return create(Commands.literal("skills"), buildContext);
    }

    private static ArgumentBuilder<CommandSourceStack, ?> create(ArgumentBuilder<CommandSourceStack, ?> builder, CommandBuildContext buildContext) {
        return builder.requires(context -> context.hasPermission(BasicCommand.PERMISSION_LEVEL_ADMIN))
                .then(Commands.literal("disableall")
                        .executes(context -> disableAll(context.getSource(), context.getSource().getPlayerOrException()))
                        .then(Commands.argument("player", EntityArgument.players())
                                .executes(context -> disableAll(context.getSource(), EntityArgument.getPlayers(context, "player")))))
                .then(Commands.literal("disable")
                        .then(Commands.argument("skill", ResourceArgument.resource(buildContext, FactionRegistries.Keys.SKILL))
                                .executes(context -> disable(context.getSource(), context.getSource().getPlayerOrException(), SkillArgument.getSkill(context, "skill")))
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(context -> disable(context.getSource(), EntityArgument.getPlayer(context, "player"), SkillArgument.getSkill(context, "skill"))))))
                .then(Commands.literal("enableall")
                        .executes(context -> enableAll(context.getSource(), context.getSource().getPlayerOrException()))
                        .then(Commands.argument("player", EntityArgument.players())
                                .executes(context -> enableAll(context.getSource(), EntityArgument.getPlayers(context, "player")))))
                .then(Commands.literal("enable")
                        .then(Commands.argument("skill", ResourceArgument.resource(buildContext, FactionRegistries.Keys.SKILL))
                                .executes(context -> enable(context.getSource(), context.getSource().getPlayerOrException(), SkillArgument.getSkill(context, "skill"), false))
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(context -> enable(context.getSource(), EntityArgument.getPlayer(context, "player"), SkillArgument.getSkill(context, "skill"), false))
                                        .then(Commands.literal("force")
                                                .executes(context -> enable(context.getSource(), EntityArgument.getPlayer(context, "player"), SkillArgument.getSkill(context, "skill"), true))))
                                .then(Commands.literal("force")
                                        .executes(context -> enable(context.getSource(), context.getSource().getPlayerOrException(), SkillArgument.getSkill(context, "skill"), true)))))
                .then(Commands.literal("toggle")
                        .then(Commands.argument("skill", ResourceArgument.resource(buildContext, FactionRegistries.Keys.SKILL))
                                .executes(context -> toggle(context.getSource(), context.getSource().getPlayerOrException(), SkillArgument.getSkill(context, "skill"), false))
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(context -> toggle(context.getSource(), EntityArgument.getPlayer(context, "player"), SkillArgument.getSkill(context, "skill"), false))
                                        .then(Commands.literal("force")
                                                .executes(context -> toggle(context.getSource(), EntityArgument.getPlayer(context, "player"), SkillArgument.getSkill(context, "skill"), true))))
                                .then(Commands.literal("force")
                                        .executes(context -> toggle(context.getSource(), context.getSource().getPlayerOrException(), SkillArgument.getSkill(context, "skill"), true)))));
    }

    private static int disableAll(CommandSourceStack commandSource, Collection<ServerPlayer> asPlayer) {
        for (ServerPlayer serverPlayer : asPlayer) {
            disableAll(commandSource, serverPlayer);
        }
        return 0;
    }

    private static int disableAll(CommandSourceStack commandSource, ServerPlayer asPlayer) {
        ISkillHandler.get(asPlayer).ifPresent(ISkillHandler::disableAllSkills);
        IRefinementHandler.get(asPlayer).ifPresent(IRefinementHandler::resetRefinements);
        commandSource.sendSuccess(() -> Component.translatable("command.vampirism.test.skill.all_locked"), false);
        return 0;
    }

    private static <T extends IFactionPlayer<T> & ISkillPlayer<T>> int enableAll(CommandSourceStack commandSource, Collection<ServerPlayer> asPlayer) throws CommandSyntaxException {
        for (ServerPlayer serverPlayer : asPlayer) {
            enableAll(commandSource, serverPlayer);
        }
        return 0;
    }

    private static <T extends IFactionPlayer<T> & ISkillPlayer<T>> int enableAll(CommandSourceStack commandSource, ServerPlayer asPlayer) throws CommandSyntaxException {
        FactionPlayerHandler handler = FactionPlayerHandler.get(asPlayer);
        ISkillHandler<T> skillHandler = handler.<T>getSkillHandler().orElseThrow(NO_FACTION::create);
        ModRegistries.SKILLS.listElements().forEach(holder -> {
            if (IFaction.is(handler.getFaction(), holder.value().factions())) {
                //noinspection unchecked
                skillHandler.enableSkill((Holder<ISkill<T>>) (Object) holder);
            }
        });
        commandSource.sendSuccess(() -> Component.translatable("command.vampirism.test.skill.all_unlocked"), false);
        return 0;
    }

    private static <T extends IFactionPlayer<T> & ISkillPlayer<T>> int toggle(CommandSourceStack commandSource, ServerPlayer asPlayer, Holder<ISkill<?>> skill, boolean force) throws CommandSyntaxException {
        if (disable(asPlayer, skill)) {
            return 0;
        }
        enable(commandSource, asPlayer, skill, force);
        return 0;
    }

    private static <T extends IFactionPlayer<T> & ISkillPlayer<T>> int enable(CommandSourceStack commandSource, ServerPlayer asPlayer, Holder<ISkill<?>> skill, boolean force) throws CommandSyntaxException {
        ISkillHandler<T> skillHandler = ISkillHandler.<T>get(asPlayer).orElseThrow(NO_FACTION::create);
        ISkillHandler.Result result = skillHandler.canSkillBeEnabled(skill);
        if (force) {
            result = ISkillHandler.Result.OK;
        }
        switch (result) {
            case OK -> {
                //noinspection unchecked
                skillHandler.enableSkill((Holder<ISkill<T>>) (Object) skill);
                commandSource.sendSuccess(() -> Component.translatable("command.vampirism.test.skill.enabled", skill.unwrapKey().map(ResourceKey::location).map(ResourceLocation::toString).orElseThrow() + " (" + skill.value().getName().getString() + ")"), false);
            }
            case ALREADY_ENABLED -> commandSource.sendSuccess(() -> Component.translatable("command.vampirism.test.skill.alreadyenabled", skill.value().getName()), false);
            case PARENT_NOT_ENABLED -> commandSource.sendFailure(Component.translatable("command.vampirism.test.skill.parents.disabled"));
            case NO_POINTS -> commandSource.sendFailure(Component.translatable("command.vampirism.test.skill.nopoints"));
            case OTHER_NODE_SKILL -> commandSource.sendFailure(Component.translatable("command.vampirism.test.skill.othernode"));
            case NOT_FOUND -> commandSource.sendFailure(Component.translatable("command.vampirism.test.skill.otherfaction"));
            case LOCKED_BY_OTHER_NODE -> commandSource.sendFailure(Component.translatable("command.vampirism.test.skill.locked"));
            case LOCKED_BY_PLAYER_STATE -> commandSource.sendFailure(Component.translatable("command.vampirism.test.skill.locked_player_state"));
        }
        return 0;
    }

    private static <T extends IFactionPlayer<T> & ISkillPlayer<T>> boolean disable(ServerPlayer asPlayer, Holder<ISkill<?>> skill) throws CommandSyntaxException {
        FactionPlayerHandler handler = FactionPlayerHandler.get(asPlayer);
        ISkillHandler<T> skillHandler = handler.<T>getSkillHandler().orElseThrow(NO_FACTION::create);
        if (skillHandler.isSkillEnabled(skill)) {
            //noinspection unchecked
            skillHandler.disableSkill((Holder<ISkill<T>>) (Object) skill);
            return true;
        }
        return false;
    }

    private static <T extends IFactionPlayer<T> & ISkillPlayer<T>> int disable(CommandSourceStack commandSource, ServerPlayer asPlayer, Holder<ISkill<?>> skill) throws CommandSyntaxException {
        if (disable(asPlayer, skill)) {
            commandSource.sendSuccess(() -> Component.translatable("command.vampirism.test.skill.disabled"), false);
        } else {
            commandSource.sendFailure(Component.translatable("command.vampirism.test.skill.not_enabled", skill.getRegisteredName()));
        }
        return 0;
    }

}
