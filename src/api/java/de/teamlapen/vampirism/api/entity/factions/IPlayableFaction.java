package de.teamlapen.vampirism.api.entity.factions;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;

/**
 * Represents one playable faction (e.g. Vampire Player)
 * One instance should be used for players and entities at the same time.
 */
public interface IPlayableFaction<T extends IFactionPlayer> extends IFaction<T> {
    Class<T> getFactionPlayerInterface();

    /**
     * @return The highest reachable lord level or 0 if no lord
     */
    int getHighestLordLevel();

    /**
     * @return Highest reachable level for players
     */
    int getHighestReachableLevel();

    /**
     * @param level  lord level
     * @param female Female version
     * @return A text component representing the title of the player at the given lord level. empty if level==0
     */
    @Nonnull
    ITextComponent getLordTitle(int level, boolean female);

    boolean hasGenderTitles();

    /**
     * @param player
     * @return The "<? extends IFactionPlayer>" of this faction for the given player
     */
    LazyOptional<T> getPlayerCapability(PlayerEntity player);

    /**
     * @return If the level should be rendered
     */
    boolean renderLevel();

    /**
     * Set if the level should be rendered, default is true
     *
     * @param render
     */
    IPlayableFaction<T> setRenderLevel(boolean render);

    /**
     * @return If this faction is allowed to have accessories
     */
    boolean hasRefinements();

    /**
     * Gets the corresponding item for the slot
     *
     * @param type
     * @throws NullPointerException if there are no accessories available
     */
    <Z extends Item & IRefinementItem> Z getRefinementItem(IRefinementItem.AccessorySlotType type);

    /**
     * @return If the faction has lord skills
     */
    boolean hasLordSkills();

}
