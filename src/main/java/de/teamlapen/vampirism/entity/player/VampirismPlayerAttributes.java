package de.teamlapen.vampirism.entity.player;


import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.entity.player.hunter.HunterPlayerSpecialAttribute;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayerSpecialAttributes;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Cache frequently accessed values from the player capabilities.
 * Injected into {@link Player} via Mixin {@link de.teamlapen.vampirism.mixin.MixinPlayerEntity}
 * If you need 100% guaranteed correct values, use the capabilities instead. Otherwise, prefer this for performance reason
 */
public class VampirismPlayerAttributes {
    public static VampirismPlayerAttributes get(@NotNull Player player) {
        return ((IVampirismPlayer) player).getVampAtts();
    }

    private final VampirePlayerSpecialAttributes vampSpecial = new VampirePlayerSpecialAttributes();
    private final HunterPlayerSpecialAttribute huntSpecial = new HunterPlayerSpecialAttribute();
    public int vampireLevel = 0;
    public int hunterLevel = 0;
    @Nullable
    @Deprecated
    public Holder<? extends IPlayableFaction<?>> faction = null;
    public int lordLevel = 0;

    public @NotNull HunterPlayerSpecialAttribute getHuntSpecial() {
        return huntSpecial;
    }

    public @NotNull VampirePlayerSpecialAttributes getVampSpecial() {
        return vampSpecial;
    }

    @SuppressWarnings({"RedundantCast", "unchecked"})
    public <T extends IFactionPlayer<T>> Holder<? extends IPlayableFaction<T>> faction() {
        return ((Holder<? extends IPlayableFaction<T>>) (Object) faction);
    }
}
