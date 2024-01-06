package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.hunter.IBasicHunter;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.vampire.IBasicVampire;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.minion.HunterMinionEntity;
import de.teamlapen.vampirism.entity.minion.MinionEntity;
import de.teamlapen.vampirism.entity.minion.VampireMinionEntity;
import de.teamlapen.vampirism.entity.minion.management.MinionData;
import de.teamlapen.vampirism.entity.minion.management.PlayerMinionController;
import de.teamlapen.vampirism.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.entity.player.hunter.skills.HunterSkills;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.entity.player.vampire.skills.VampireSkills;
import de.teamlapen.vampirism.world.MinionWorldData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;


public class MinionCommand extends BasicCommand {
    private static final DynamicCommandExceptionType fail = new DynamicCommandExceptionType((msg) -> Component.literal("Failed: " + msg));

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("minion")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_CHEAT))
                .then(Commands.literal("spawnNew")
                        .then(Commands.literal("vampire").executes(context -> spawnNewVampireMinion(context.getSource(), "Minion", -1, false))
                                .then(Commands.argument("name", StringArgumentType.string()).executes(context -> spawnNewVampireMinion(context.getSource(), StringArgumentType.getString(context, "name"), -1, false))
                                        .then(Commands.argument("type", IntegerArgumentType.integer(-1, IBasicVampire.TYPES)).executes(context -> spawnNewVampireMinion(context.getSource(), StringArgumentType.getString(context, "name"), IntegerArgumentType.getInteger(context, "type"), false))
                                                .then(Commands.argument("use_lord_skin", BoolArgumentType.bool()).executes(context -> spawnNewVampireMinion(context.getSource(), StringArgumentType.getString(context, "name"), IntegerArgumentType.getInteger(context, "type"), BoolArgumentType.getBool(context, "use_lord_skin")))))))
                        .then(Commands.literal("hunter").executes(context -> spawnNewHunterMinion(context.getSource(), "Minion", -1, 0, false))
                                .then(Commands.argument("name", StringArgumentType.string()).executes(context -> spawnNewHunterMinion(context.getSource(), StringArgumentType.getString(context, "name"), -1, 0, false))
                                        .then(Commands.argument("type", IntegerArgumentType.integer(-1, IBasicHunter.TYPES)).executes(context -> spawnNewHunterMinion(context.getSource(), StringArgumentType.getString(context, "name"), IntegerArgumentType.getInteger(context, "type"), 0, false))
                                                .then(Commands.argument("hat", IntegerArgumentType.integer(-1, 3)).executes(context -> spawnNewHunterMinion(context.getSource(), StringArgumentType.getString(context, "name"), IntegerArgumentType.getInteger(context, "type"), IntegerArgumentType.getInteger(context, "hat"), false))
                                                        .then(Commands.argument("use_lord_skin", BoolArgumentType.bool()).executes(context -> spawnNewHunterMinion(context.getSource(), StringArgumentType.getString(context, "name"), IntegerArgumentType.getInteger(context, "type"), IntegerArgumentType.getInteger(context, "hat"), BoolArgumentType.getBool(context, "use_lord_skin")))))
                                        )
                                )
                        )
                )
                .then(Commands.literal("recall")
                        .executes(context -> recall(context.getSource(), context.getSource().getPlayerOrException()))
                        .then(Commands.argument("target", EntityArgument.player())
                                .executes(context -> recall(context.getSource(), EntityArgument.getPlayer(context, "target")))))
                .then(Commands.literal("respawnAll")
                        .executes(context -> respawn(context.getSource(), context.getSource().getPlayerOrException()))
                        .then(Commands.argument("target", EntityArgument.player())
                                .executes(context -> respawn(context.getSource(), EntityArgument.getPlayer(context, "target")))))
                .then(Commands.literal("purge")
                        .executes(context -> purge(context.getSource(), context.getSource().getPlayerOrException()))
                        .then(Commands.argument("target", EntityArgument.player())
                                .executes(context -> purge(context.getSource(), EntityArgument.getPlayer(context, "target")))));
    }


    private static int spawnNewVampireMinion(@NotNull CommandSourceStack ctx, String name, int type, boolean useLordSkin) throws CommandSyntaxException {
        boolean hasIncreasedStats = VampirePlayer.getOpt(ctx.getPlayerOrException()).map(IFactionPlayer::getSkillHandler).map(skillHandler -> skillHandler.isSkillEnabled(VampireSkills.MINION_STATS_INCREASE.get())).orElse(false);
        VampireMinionEntity.VampireMinionData data = new VampireMinionEntity.VampireMinionData(name, type, useLordSkin, hasIncreasedStats);
        return spawnNewMinion(ctx, VReference.VAMPIRE_FACTION, data, ModEntities.VAMPIRE_MINION.get());
    }

    private static int spawnNewHunterMinion(@NotNull CommandSourceStack ctx, String name, int type, int hat, boolean useLordSkin) throws CommandSyntaxException {
        boolean hasIncreasedStats = HunterPlayer.getOpt(ctx.getPlayerOrException()).map(IFactionPlayer::getSkillHandler).map(skillHandler -> skillHandler.isSkillEnabled(HunterSkills.MINION_STATS_INCREASE.get())).orElse(false);
        HunterMinionEntity.HunterMinionData data = new HunterMinionEntity.HunterMinionData(name, type, hat, useLordSkin, hasIncreasedStats);
        return spawnNewMinion(ctx, VReference.HUNTER_FACTION, data, ModEntities.HUNTER_MINION.get());
    }

    @SuppressWarnings("SameReturnValue")
    private static <T extends MinionData> int spawnNewMinion(@NotNull CommandSourceStack ctx, IPlayableFaction<?> faction, @NotNull T data, EntityType<? extends MinionEntity<T>> type) throws CommandSyntaxException {
        Player p = ctx.getPlayerOrException();
        FactionPlayerHandler fph = FactionPlayerHandler.getOpt(p).filter(s -> s.getMaxMinions() > 0).orElseThrow(() -> fail.create("Can't have minions"));
            PlayerMinionController controller = MinionWorldData.getData(ctx.getServer()).getOrCreateController(fph);
            if (controller.hasFreeMinionSlot()) {

                if (fph.getCurrentFaction() == faction) {
                    int id = controller.createNewMinionSlot(data, type);
                    if (id < 0) {
                        throw fail.create("Failed to get new minion slot");
                    }
                    controller.createMinionEntityAtPlayer(id, p);
                } else {
                    throw fail.create("Wrong faction");
                }


            } else {
                throw fail.create("No free slot");
            }

        return 0;
    }

    @SuppressWarnings("SameReturnValue")
    private static int recall(@NotNull CommandSourceStack ctx, ServerPlayer player) throws CommandSyntaxException {
        FactionPlayerHandler factionPlayerHandler = FactionPlayerHandler.getOpt(player).filter(s -> s.getMaxMinions() > 0).orElseThrow(() -> fail.create("Can't have minions"));
        PlayerMinionController controller = MinionWorldData.getData(ctx.getServer()).getOrCreateController(factionPlayerHandler);
        Collection<Integer> ids = controller.recallMinions(true);
        for (Integer id : ids) {
            controller.createMinionEntityAtPlayer(id, player);
        }

        return 0;
    }


    @SuppressWarnings("SameReturnValue")
    private static int respawn(@NotNull CommandSourceStack ctx, ServerPlayer player) throws CommandSyntaxException {
        FactionPlayerHandler fph = FactionPlayerHandler.getOpt(player).filter(s -> s.getMaxMinions() > 0).orElseThrow(() -> fail.create("Can't have minions"));
            PlayerMinionController controller = MinionWorldData.getData(ctx.getServer()).getOrCreateController(fph);
            Collection<Integer> ids = controller.getUnclaimedMinions();
            for (Integer id : ids) {
                controller.createMinionEntityAtPlayer(id, player);
            }
        return 0;
    }

    @SuppressWarnings("SameReturnValue")
    private static int purge(@NotNull CommandSourceStack ctx, ServerPlayer player) throws CommandSyntaxException {
        MinionWorldData.getData(ctx.getServer()).purgeController(player.getUUID());
        ((Player) player).displayClientMessage(Component.literal("Reload world"), false);
        return 0;
    }
}
