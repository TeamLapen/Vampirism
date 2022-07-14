package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.api.VReference;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.NaturalSpawner;

public class InfoEntitiesCommand extends BasicCommand {
    public static final int maxSpawns = 50;

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("info-entities")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_ADMIN))
                .executes(context -> infoEntities(context.getSource(), context.getSource().getPlayerOrException()));
    }

    @SuppressWarnings("SameReturnValue")
    private static int infoEntities(CommandSourceStack commandSource, ServerPlayer asPlayer) {
        NaturalSpawner.SpawnState densityManager = asPlayer.getLevel().getChunkSource().getLastSpawnState();
        Object2IntMap<MobCategory> object2intmap = densityManager.getMobCategoryCounts();
        commandSource.sendSuccess(Component.literal(String.format("Creature: %d (%d), Monster: %s (%s), Hunter: %s (%s), Vampire: %s (%s)", object2intmap.getOrDefault(MobCategory.CREATURE, 0), MobCategory.CREATURE.getMaxInstancesPerChunk(), object2intmap.getOrDefault(MobCategory.MONSTER, 0), MobCategory.MONSTER.getMaxInstancesPerChunk(), object2intmap.getOrDefault(VReference.HUNTER_CREATURE_TYPE, 0), VReference.HUNTER_CREATURE_TYPE.getMaxInstancesPerChunk(), object2intmap.getOrDefault(VReference.VAMPIRE_CREATURE_TYPE, 0), VReference.VAMPIRE_CREATURE_TYPE.getMaxInstancesPerChunk())), true);
        return 0;
    }
}
