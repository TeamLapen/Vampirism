package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.hunter.IBasicHunter;
import de.teamlapen.vampirism.api.entity.vampire.IBasicVampire;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.minion.HunterMinionEntity;
import de.teamlapen.vampirism.entity.minion.MinionEntity;
import de.teamlapen.vampirism.entity.minion.VampireMinionEntity;
import de.teamlapen.vampirism.entity.minion.management.MinionData;
import de.teamlapen.vampirism.entity.minion.management.PlayerMinionController;
import de.teamlapen.vampirism.world.MinionWorldData;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;

import java.util.Collection;


public class MinionCommand extends BasicCommand {
    private static final DynamicCommandExceptionType fail = new DynamicCommandExceptionType((msg) -> new StringTextComponent("Failed: " + msg));

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("minion")
                .requires(context -> context.hasPermissionLevel(PERMISSION_LEVEL_CHEAT))
                .then(Commands.literal("spawnNew")
                        .then(Commands.literal("vampire").executes(context -> spawnNewVampireMinion(context.getSource(), "Minion", -1))
                                .then(Commands.argument("name", StringArgumentType.string()).executes(context -> spawnNewVampireMinion(context.getSource(), StringArgumentType.getString(context, "name"), -1))
                                        .then(Commands.argument("type", IntegerArgumentType.integer(-1, IBasicVampire.TYPES)).executes(context -> spawnNewVampireMinion(context.getSource(), StringArgumentType.getString(context, "name"), IntegerArgumentType.getInteger(context, "type"))))))
                        .then(Commands.literal("hunter").executes(context -> spawnNewHunterMinion(context.getSource(), "Minion", -1, 0))
                                .then(Commands.argument("name", StringArgumentType.string()).executes(context -> spawnNewHunterMinion(context.getSource(), StringArgumentType.getString(context, "name"), -1, 0))
                                        .then(Commands.argument("type", IntegerArgumentType.integer(-1, IBasicHunter.TYPES)).executes(context -> spawnNewHunterMinion(context.getSource(), StringArgumentType.getString(context, "name"), IntegerArgumentType.getInteger(context, "type"), 0))
                                                .then(Commands.argument("hat", IntegerArgumentType.integer(-1, 3)).executes(context -> spawnNewHunterMinion(context.getSource(), StringArgumentType.getString(context, "name"), IntegerArgumentType.getInteger(context, "type"), IntegerArgumentType.getInteger(context, "hat")))))
                                )
                        )
                )
                .then(Commands.literal("recall").executes(context -> recall(context.getSource())))
                .then(Commands.literal("respawnAll").executes(context -> respawn(context.getSource())))
                .then(Commands.literal("purge").executes(context -> purge(context.getSource())))
                .executes(context -> 0);
    }


    private static int spawnNewVampireMinion(CommandSource ctx, String name, int type) throws CommandSyntaxException {
        VampireMinionEntity.VampireMinionData data = new VampireMinionEntity.VampireMinionData(20, new StringTextComponent(name).applyTextStyle(VReference.VAMPIRE_FACTION.getChatColor()), type);
        return spawnNewMinion(ctx, VReference.VAMPIRE_FACTION, data, ModEntities.vampire_minion);
    }

    private static int spawnNewHunterMinion(CommandSource ctx, String name, int type, int hat) throws CommandSyntaxException {
        HunterMinionEntity.HunterMinionData data = new HunterMinionEntity.HunterMinionData(20, new StringTextComponent(name).applyTextStyle(VReference.HUNTER_FACTION.getChatColor()), type, hat);
        return spawnNewMinion(ctx, VReference.HUNTER_FACTION, data, ModEntities.hunter_minion);
    }

    private static <T extends MinionData> int spawnNewMinion(CommandSource ctx, IPlayableFaction<?> faction, T data, EntityType<? extends MinionEntity<T>> type) throws CommandSyntaxException {
        PlayerEntity p = ctx.asPlayer();
        FactionPlayerHandler fph = FactionPlayerHandler.get(p);
        if (fph.getMaxMinions() > 0) {
            PlayerMinionController controller = MinionWorldData.getData(ctx.getServer()).getOrCreateController(fph);
            if (controller.hasFreeMinionSlot()) {

                if (fph.getCurrentFaction() == faction) {
                    int id = controller.createNewMinionSlot(data);
                    if (id < 0) {
                        throw fail.create("Failed to get new minion slot");
                    }
                    MinionEntity<T> m = type.create(p.getEntityWorld());
                    m.claimMinionSlot(id, controller);
                    m.copyLocationAndAnglesFrom(p);
                    p.world.addEntity(m);
                } else {
                    throw fail.create("Wrong faction");
                }


            } else {
                throw fail.create("No free slot");
            }

        } else {
            throw fail.create("Can't have minions");
        }

        return 0;
    }

    private static int recall(CommandSource ctx) throws CommandSyntaxException {
        PlayerEntity p = ctx.asPlayer();
        FactionPlayerHandler fph = FactionPlayerHandler.get(p);
        if (fph.getMaxMinions() > 0) {
            PlayerMinionController controller = MinionWorldData.getData(ctx.getServer()).getOrCreateController(fph);
            Collection<Integer> ids = controller.recallMinions();
            for (Integer id : ids) {
                VampireMinionEntity m = ModEntities.vampire_minion.create(p.getEntityWorld());
                m.claimMinionSlot(id, controller);
                m.copyLocationAndAnglesFrom(p);
                p.world.addEntity(m);
            }
        } else {
            throw fail.create("Can't have minions");
        }

        return 0;
    }


    private static int respawn(CommandSource ctx) throws CommandSyntaxException {
        PlayerEntity p = ctx.asPlayer();
        FactionPlayerHandler fph = FactionPlayerHandler.get(p);
        if (fph.getMaxMinions() > 0) {
            PlayerMinionController controller = MinionWorldData.getData(ctx.getServer()).getOrCreateController(fph);
            Collection<Integer> ids = controller.getUnclaimedMinions();
            for (Integer id : ids) {
                VampireMinionEntity m = ModEntities.vampire_minion.create(p.getEntityWorld());
                m.claimMinionSlot(id, controller);
                m.copyLocationAndAnglesFrom(p);
                p.world.addEntity(m);
            }

        } else {
            throw fail.create("Can't have minions");
        }

        return 0;
    }

    private static int purge(CommandSource ctx) throws CommandSyntaxException {
        PlayerEntity p = ctx.asPlayer();
        MinionWorldData.getData(ctx.getServer()).purgeController(p.getUniqueID());
        p.sendStatusMessage(new StringTextComponent("Reload world"), false);
        return 0;
    }
}
