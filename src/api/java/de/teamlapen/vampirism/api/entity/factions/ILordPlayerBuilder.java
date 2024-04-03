package de.teamlapen.vampirism.api.entity.factions;

import de.teamlapen.vampirism.api.entity.minion.IMinionData;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public interface ILordPlayerBuilder<T extends IFactionPlayer<T>> {

    /**
     * Sets the maximum lord level for player of this faction<br>
     * If set to 0 the player can not become lord
     *
     * @param highestLordLevel the highest possible lord level for players,  {@code highestLordLevel >= 0}
     * @return the builder
     */
    ILordPlayerBuilder<T> lordLevel(int highestLordLevel);

    /**
     * @deprecated use {@link #lordTitle(ILordTitleProvider)} instead
     */
    @Deprecated
    ILordPlayerBuilder<T> lordTitle(@NotNull BiFunction<Integer, IPlayableFaction.TitleGender, Component> lordTitleFunction);

    /**
     * Sets custom lord titles
     * <br>
     * Only relevant when lord level > 0
     *
     * @param lordTitleFunction an object that provides the title for a lord player
     * @return the builder
     */
    ILordPlayerBuilder<T> lordTitle(@NotNull ILordTitleProvider lordTitleFunction);

    /**
     * Enables this faction to have lord skills
     *
     * @return the builder
     */
    ILordPlayerBuilder<T> enableLordSkills();

    <Z extends IMinionData> IMinionBuilder<T,Z> minion(ResourceLocation minionId, Supplier<Z> data);

    IPlayableFactionBuilder<T> build();

}
