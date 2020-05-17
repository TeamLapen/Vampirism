package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.entity.minion.VampireMinionEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;


public class MinionCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("minion")
                .requires(context -> context.hasPermissionLevel(PERMISSION_LEVEL_CHEAT))
                .then(Commands.literal("spawn").executes(context -> spawnMinion(context.getSource())))
                .executes(context -> 0);
    }

    private static int spawnMinion(CommandSource ctx) throws CommandSyntaxException {
        PlayerEntity p = ctx.asPlayer();
        VampireMinionEntity m = ModEntities.vampire_minion.create(p.getEntityWorld());
        m.setLordID(p.getUniqueID());
        m.copyLocationAndAnglesFrom(p);
        p.world.addEntity(m);
        return 0;
    }
}
