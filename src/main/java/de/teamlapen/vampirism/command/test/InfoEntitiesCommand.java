package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.api.VReference;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.spawner.WorldEntitySpawner;

public class InfoEntitiesCommand extends BasicCommand {
    public static final int maxSpawns = 50;

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("info-entities")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_ADMIN))
                .executes(context -> {
                    return infoEntities(context.getSource(), context.getSource().getPlayerOrException());
                });
    }

    private static int infoEntities(CommandSource commandSource, ServerPlayerEntity asPlayer) {
        WorldEntitySpawner.EntityDensityManager densityManager = asPlayer.getLevel().getChunkSource().getLastSpawnState();
        Object2IntMap<EntityClassification> object2intmap = densityManager.getMobCategoryCounts();
        commandSource.sendSuccess(new StringTextComponent(String.format("Creature: %d (%d), Monster: %s (%s), Hunter: %s (%s), Vampire: %s (%s)", object2intmap.getOrDefault(EntityClassification.CREATURE, 0), EntityClassification.CREATURE.getMaxInstancesPerChunk(), object2intmap.getOrDefault(EntityClassification.MONSTER, 0), EntityClassification.MONSTER.getMaxInstancesPerChunk(), object2intmap.getOrDefault(VReference.HUNTER_CREATURE_TYPE, 0), VReference.HUNTER_CREATURE_TYPE.getMaxInstancesPerChunk(), object2intmap.getOrDefault(VReference.VAMPIRE_CREATURE_TYPE, 0), VReference.VAMPIRE_CREATURE_TYPE.getMaxInstancesPerChunk())), true);
        return 0;
    }
}
