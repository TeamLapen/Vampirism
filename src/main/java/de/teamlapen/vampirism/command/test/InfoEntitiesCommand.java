package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.api.VReference;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

/**
 * 
 * @authors Cheaterpaul, Maxanier
 */
public class InfoEntitiesCommand extends BasicCommand {
	public static final int maxSpawns = 50;
	
	public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("info-entities")
        		.requires(context -> context.hasPermissionLevel(PERMISSION_LEVEL_ADMIN))
        		.executes(context -> {
                    return infoEntities(context.getSource(), context.getSource().asPlayer());
        		});
	}

    private static int infoEntities(CommandSource commandSource, ServerPlayerEntity asPlayer) {
        int entityMonster = asPlayer.getEntityWorld().countEntities(EntityClassification.MONSTER, maxSpawns, false);
        int entityMonsterSpawn = asPlayer.getEntityWorld().countEntities(EntityClassification.MONSTER, maxSpawns, true);
        int entityHunter = asPlayer.getEntityWorld().countEntities(VReference.HUNTER_CREATURE_TYPE, maxSpawns, false);
        int entityHunterSpawn = asPlayer.getEntityWorld().countEntities(VReference.HUNTER_CREATURE_TYPE, maxSpawns, true);
        int entityVampire = asPlayer.getEntityWorld().countEntities(VReference.VAMPIRE_CREATURE_TYPE, maxSpawns, false);
        int entityVampireSpawn = asPlayer.getEntityWorld().countEntities(VReference.VAMPIRE_CREATURE_TYPE, maxSpawns, true);
        commandSource.sendFeedback(new StringTextComponent(String.format("Monster: %s (%s), Hunter: %s (%s), Vampire: %s (%s)", entityMonster, entityMonsterSpawn, entityHunter, entityHunterSpawn, entityVampire, entityVampireSpawn)), true);
        return 0;
    }
}
