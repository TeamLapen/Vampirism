package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;

import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.core.ModParticles;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;

/**
 * 
 * @authors Cheaterpaul, Maxanier
 */
public class HalloweenCommand extends BasicCommand{

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("halloween")
                .requires(context -> context.hasPermissionLevel(PERMISSION_LEVEL_ADMIN))
                .executes(context -> {
                    return halloween(context.getSource().asPlayer());
                });
    }

    private static int halloween(ServerPlayerEntity asPlayer) {
        // DraculaHalloweenEntity draculaHalloween = (DraculaHalloweenEntity) UtilLib.spawnEntityBehindEntity(asPlayer, new ResourceLocation(REFERENCE.MODID, ModEntities.SPECIAL_DRACULA_HALLOWEEN));
        // draculaHalloween.setOwnerId(asPlayer.getUniqueID());
        asPlayer.world.addParticle(ModParticles.halloween, asPlayer.posX, asPlayer.posY, asPlayer.posZ, 0, 0, 0);
        return 0;
    }
}
