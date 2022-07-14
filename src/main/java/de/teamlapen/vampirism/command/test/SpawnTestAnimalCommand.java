package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cow;

public class SpawnTestAnimalCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("spawnTestAnimal")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_ADMIN))
                .executes(context -> spawnTestAnimal(context.getSource().getPlayerOrException()));
    }

    @SuppressWarnings("SameReturnValue")
    private static int spawnTestAnimal(ServerPlayer asPlayer) {
        Cow cow = EntityType.COW.create(asPlayer.getCommandSenderWorld());
        cow.setHealth(cow.getMaxHealth() / 4.2f);
        cow.copyPosition(asPlayer);
        asPlayer.level.addFreshEntity(cow);
        return 0;
    }
}
