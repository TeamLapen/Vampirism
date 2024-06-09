package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.command.arguments.SkillArgument;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SkillCommand extends BasicCommand {

    private static final SimpleCommandExceptionType NO_FACTION = new SimpleCommandExceptionType(Component.translatable("command.vampirism.test.skill.noinfaction"));

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandBuildContext buildContext) {
        return create(Commands.literal("skills"), buildContext);
    }

    private static ArgumentBuilder<CommandSourceStack, ?> create(@NotNull ArgumentBuilder<CommandSourceStack, ?> builder, CommandBuildContext buildContext) {
        return builder.requires(context -> context.hasPermission(PERMISSION_LEVEL_ADMIN))
                .then(Commands.argument("type", ResourceArgument.resource(buildContext, VampirismRegistries.Keys.SKILL))
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

    private static int disableall(@NotNull CommandSourceStack commandSource, @NotNull ServerPlayer asPlayer) throws CommandSyntaxException {
        IFactionPlayer<?> factionPlayer = FactionPlayerHandler.getCurrentFactionPlayer(asPlayer).orElseThrow(NO_FACTION::create);
        factionPlayer.getSkillHandler().resetSkills();
        commandSource.sendSuccess(() -> Component.translatable("command.vampirism.test.skill.all_locked"), false);
        return 0;
    }

    private static <T extends IFactionPlayer<T>> int enableAll(@NotNull CommandSourceStack commandSource, @NotNull ServerPlayer asPlayer) throws CommandSyntaxException {
        T factionPlayer = FactionPlayerHandler.<T>getCurrentFactionPlayer(asPlayer).orElseThrow(NO_FACTION::create);
        ISkillHandler<T> skillHandler = factionPlayer.getSkillHandler();
        ModRegistries.SKILLS.holders().forEach(holder -> {
            if (IFaction.is(factionPlayer.getFaction(), holder.value().factions())) {
                //noinspection unchecked
                skillHandler.enableSkill((Holder<ISkill<T>>) (Object) holder);
            }
        });
        commandSource.sendSuccess(() -> Component.translatable("command.vampirism.test.skill.all_unlocked"), false);
        return 0;
    }

    private static <T extends IFactionPlayer<T>> int skill(@NotNull CommandSourceStack commandSource, @NotNull ServerPlayer asPlayer, @NotNull Holder<ISkill<?>> skill, boolean force) throws CommandSyntaxException {
        T factionPlayer = FactionPlayerHandler.<T>getCurrentFactionPlayer(asPlayer).orElseThrow(NO_FACTION::create);
        if (factionPlayer.getSkillHandler().isSkillEnabled(skill)) {
            //noinspection unchecked
            factionPlayer.getSkillHandler().disableSkill((Holder<ISkill<T>>) (Object) skill);
            commandSource.sendSuccess(() -> Component.translatable("command.vampirism.test.skill.disabled"), false);
            return 0;
        }
        ISkillHandler.Result result = factionPlayer.getSkillHandler().canSkillBeEnabled(skill);
        if (force) {
            result = ISkillHandler.Result.OK;
        }
        switch (result) {
            case OK -> {
                //noinspection unchecked
                factionPlayer.getSkillHandler().enableSkill((Holder<ISkill<T>>) (Object) skill);
                commandSource.sendSuccess(() -> Component.translatable("command.vampirism.test.skill.enabled", skill.unwrapKey().map(ResourceKey::location).map(ResourceLocation::toString).orElseThrow() + " (" + skill.value().getName().getString() + ")"), false);
            }
            case ALREADY_ENABLED -> commandSource.sendSuccess(() -> Component.translatable("command.vampirism.test.skill.alreadyenabled", skill.value().getName()), false);
            case PARENT_NOT_ENABLED -> {
                List<Holder<ISkill<?>>> skills = factionPlayer.getSkillHandler().getParentSkills(skill);
                if (skills == null || skills.isEmpty()) return 0;
                if (skills.size() == 1) {
                    commandSource.sendFailure(Component.translatable("command.vampirism.test.skill.parent", skills.getFirst().unwrapKey().map(ResourceKey::location).orElseThrow()));
                } else {
                    commandSource.sendFailure(Component.translatable("command.vampirism.test.skill.parents", skills.get(0).unwrapKey().map(ResourceKey::location).orElseThrow(), skills.get(2).unwrapKey().map(ResourceKey::location).orElseThrow()));
                }
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
