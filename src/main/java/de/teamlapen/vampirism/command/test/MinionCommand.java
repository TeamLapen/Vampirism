package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.minion.VampireMinionEntity;
import de.teamlapen.vampirism.entity.minion.management.MinionData;
import de.teamlapen.vampirism.entity.minion.management.PlayerMinionController;
import de.teamlapen.vampirism.world.MinionWorldData;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;

import java.util.Collection;


public class MinionCommand extends BasicCommand {
    private static final DynamicCommandExceptionType fail = new DynamicCommandExceptionType((msg) -> new StringTextComponent("Failed: " + msg));

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("minion")
                .requires(context -> context.hasPermissionLevel(PERMISSION_LEVEL_CHEAT))
                .then(Commands.literal("spawnNew").executes(context -> spawnNewMinion(context.getSource())))
                .then(Commands.literal("recall").executes(context -> recall(context.getSource())))
                .then(Commands.literal("respawnAll").executes(context -> respawn(context.getSource())))
                .executes(context -> 0);
    }

    private static int spawnNewMinion(CommandSource ctx) throws CommandSyntaxException {
        PlayerEntity p = ctx.asPlayer();
        FactionPlayerHandler fph = FactionPlayerHandler.get(p);
        if (fph.getMaxMinions() > 0) {
            PlayerMinionController controller = MinionWorldData.getData(ctx.getServer()).getOrCreateController(fph);
            if (controller.hasFreeMinionSlot()) {
                int id = controller.createNewMinion(new MinionData());
                if (id < 0) {
                    throw fail.create("Failed to get new minion slot");
                }
                VampireMinionEntity m = ModEntities.vampire_minion.create(p.getEntityWorld());
                m.claimMinionSlot(id, controller);
                m.copyLocationAndAnglesFrom(p);
                p.world.addEntity(m);
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
}
