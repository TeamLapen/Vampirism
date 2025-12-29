package de.teamlapen.vampirism.api;

import com.google.common.base.Preconditions;
import de.teamlapen.vampirism.api.world.IFogHandler;
import de.teamlapen.vampirism.api.world.IGarlicChunkHandler;
import de.teamlapen.vampirism.api.world.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.api.world.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IVampirePlayer;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.UnknownNullability;

import static de.teamlapen.vampirism.api.VampirismAttachments.HUNTER_PLAYER;
import static de.teamlapen.vampirism.api.VampirismAttachments.VAMPIRE_PLAYER;

/**
 * All interaction with the api should go through {@link #services()}
 */
public class VampirismApi {

    @UnknownNullability
    private static IVampirismServices SERVICES;

    public static IVampirismServices services() {
        return SERVICES;
    }

    @ApiStatus.Internal
    public static void init(IVampirismServices services) {
        Preconditions.checkArgument(SERVICES == null, "Vampirism API has already been initialized");
        SERVICES = services;
    }

    public static IVampirePlayer vampirePlayer(Player player) {
        return player.getData(VAMPIRE_PLAYER);
    }

    public static IHunterPlayer hunterPlayer(Player player) {
        return player.getData(HUNTER_PLAYER);
    }

    public static IExtendedCreatureVampirism extendedCreatureVampirism(PathfinderMob creature) {
        return creature.getData(VampirismAttachments.EXTENDED_CREATURE);
    }

    public static IGarlicChunkHandler garlicHandler(Level w) {
        return w.getData(VampirismAttachments.GARLIC_HANDLER);
    }

    public static IFogHandler fogHandler(Level w) {
        return w.getData(VampirismAttachments.FOG_HANDLER);
    }

}
