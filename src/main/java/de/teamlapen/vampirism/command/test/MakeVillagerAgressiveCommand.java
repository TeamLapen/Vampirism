package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import de.teamlapen.vampirism.blockentity.TotemBlockEntity;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.Villager;

import java.util.List;

public class MakeVillagerAgressiveCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("makeVillagerAgressive")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_ADMIN))
                .executes(context -> makeVillagerAgressive(context.getSource().getPlayerOrException()));
    }

    @SuppressWarnings("SameReturnValue")
    private static int makeVillagerAgressive(ServerPlayer asPlayer) {
        List<Villager> l = asPlayer.getCommandSenderWorld().getEntitiesOfClass(Villager.class, asPlayer.getBoundingBox().inflate(3, 2, 3));
        for (Villager v : l) {
            if (v instanceof IFactionEntity) continue;
            TotemBlockEntity.makeAgressive(v);
        }
        return 0;
    }
}
