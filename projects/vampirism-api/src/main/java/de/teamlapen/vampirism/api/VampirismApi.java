package de.teamlapen.vampirism.api;

import de.teamlapen.faction.api.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.world.IFogHandler;
import de.teamlapen.vampirism.api.world.IGarlicChunkHandler;
import de.teamlapen.vampirism.api.world.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.api.world.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IVampirePlayer;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.ServiceLoader;

/**
 * All interaction with the api should go through {@link #services()}
 */
public class VampirismApi {

    /**
     * @return the {@link IVampirismServices} implementation provided by Vampirism, discovered via {@link ServiceLoader}
     */
    public static IVampirismServices services() {
        return Holder.SERVICES;
    }

    public static IVampirePlayer vampirePlayer(Player player) {
        return player.getData(VampirismAttachments.VAMPIRE_PLAYER);
    }

    public static IHunterPlayer hunterPlayer(Player player) {
        return player.getData(VampirismAttachments.HUNTER_PLAYER);
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

    private static final class Holder {

        private static final IVampirismServices SERVICES = load();

        private static IVampirismServices load() {
            return ServiceLoader.load(IVampirismServices.class, VampirismApi.class.getClassLoader())
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No implementation of " + IVampirismServices.class.getName() + " found. Is Vampirism installed correctly?"));
        }
    }
}
