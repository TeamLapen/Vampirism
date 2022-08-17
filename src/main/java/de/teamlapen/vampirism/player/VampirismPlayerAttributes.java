package de.teamlapen.vampirism.player;


import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.player.hunter.HunterPlayerSpecialAttribute;
import de.teamlapen.vampirism.player.vampire.VampirePlayerSpecialAttributes;
import net.minecraft.world.entity.player.Player;

import org.jetbrains.annotations.Nullable;

/**
 * Cache frequently accessed values from the player capabilities.
 * Injected into {@link Player} via Mixin {@link de.teamlapen.vampirism.mixin.MixinPlayerEntity}
 * If you need 100% guaranteed correct values, use the capabilities instead. Otherwise, prefer this for performance reason
 */
public class VampirismPlayerAttributes {
    public static VampirismPlayerAttributes get(Player player) {
        return ((IVampirismPlayer) player).getVampAtts();
    }

    private final VampirePlayerSpecialAttributes vampSpecial = new VampirePlayerSpecialAttributes();
    private final HunterPlayerSpecialAttribute huntSpecial = new HunterPlayerSpecialAttribute();
    public int vampireLevel = 0;
    public int hunterLevel = 0;
    @Nullable
    public IPlayableFaction<?> faction = null;
    public int lordLevel = 0;

    public HunterPlayerSpecialAttribute getHuntSpecial() {
        return huntSpecial;
    }

    public VampirePlayerSpecialAttributes getVampSpecial() {
        return vampSpecial;
    }
}
