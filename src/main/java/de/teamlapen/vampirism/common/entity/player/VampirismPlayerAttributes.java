package de.teamlapen.vampirism.common.entity.player;


import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.common.core.ModFactions;
import de.teamlapen.vampirism.common.entity.player.hunter.HunterPlayerSpecialAttribute;
import de.teamlapen.vampirism.common.entity.player.vampire.VampirePlayerSpecialAttributes;
import de.teamlapen.vampirism.misc.mixin.PlayerMixin;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Cache frequently accessed values from the player capabilities.
 * Injected into {@link Player} via Mixin {@link PlayerMixin}
 * If you need 100% guaranteed correct values, use the capabilities instead. Otherwise, prefer this for performance reason
 */
public class VampirismPlayerAttributes {
    public static VampirismPlayerAttributes get(@NotNull Player player) {
        return ((IVampirismPlayer) player).vampirism$getVampAtts();
    }

    private final VampirePlayerSpecialAttributes vampSpecial = new VampirePlayerSpecialAttributes();
    private final HunterPlayerSpecialAttribute huntSpecial = new HunterPlayerSpecialAttribute();
    public int vampireLevel = 0;
    public int hunterLevel = 0;
    @NotNull
    @Deprecated
    public Holder<? extends IPlayableFaction<?>> faction = ModFactions.NEUTRAL;
    public int lordLevel = 0;

    public @NotNull HunterPlayerSpecialAttribute getHuntSpecial() {
        return huntSpecial;
    }

    public @NotNull VampirePlayerSpecialAttributes getVampSpecial() {
        return vampSpecial;
    }

    @NotNull
    @SuppressWarnings({"RedundantCast", "unchecked"})
    public <T extends IFactionPlayer<T>> Holder<? extends IPlayableFaction<T>> faction() {
        return ((Holder<? extends IPlayableFaction<T>>) (Object) faction);
    }
}
