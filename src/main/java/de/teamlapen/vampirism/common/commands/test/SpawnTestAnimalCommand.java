package de.teamlapen.vampirism.common.commands.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.server.commands.BasicCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cow;
import org.jetbrains.annotations.NotNull;

public class SpawnTestAnimalCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("spawnTestAnimal")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_ADMIN))
                .executes(context -> spawnTestAnimal(context.getSource().getPlayerOrException()));
    }

    @SuppressWarnings("SameReturnValue")
    private static int spawnTestAnimal(@NotNull ServerPlayer asPlayer) {
        Cow cow = EntityType.COW.create(asPlayer.level(), EntitySpawnReason.COMMAND);
        cow.setHealth(cow.getMaxHealth() / 4.2f);
        cow.copyPosition(asPlayer);
        asPlayer.level().addFreshEntity(cow);
        return 0;
    }
}
