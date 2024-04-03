package de.teamlapen.vampirism.api.entity.factions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Represents one playable faction (e.g. Vampire Player)
 * One instance should be used for players and entities at the same time.
 */
public interface IPlayableFaction<T extends IFactionPlayer<T>> extends IFaction<T> {

    Codec<IPlayableFaction<?>> CODEC = RecordCodecBuilder.create(ins -> ins.group(ResourceLocation.CODEC.fieldOf("id").forGetter(IPlayableFaction::getID)).apply(ins, (id) -> (IPlayableFaction<?>) VampirismAPI.factionRegistry().getFactionByID(id)));

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
     * @deprecated Use {@link ILordTitleProvider#getLordTitle(int, TitleGender)} instead
     */
    @Deprecated
    @NotNull
    Component getLordTitle(int level, TitleGender female);

    /**
     * Gets the lord title provider for this faction
     */
    ILordTitleProvider lordTiles();

    /**
     * @return The "<? extends IFactionPlayer>" of this faction for the given player
     */
    Optional<T> getPlayerCapability(Player player);

    /**
     * @return If this faction is allowed to have accessories
     */
    boolean hasRefinements();

    /**
     * Gets the corresponding item for the slot
     *
     * @throws NullPointerException if there are no accessories available
     */
    <Z extends Item & IRefinementItem> Z getRefinementItem(IRefinementItem.AccessorySlotType type);

    /**
     * @return If the faction has lord skills
     */
    boolean hasLordSkills();

    enum TitleGender implements StringRepresentable {
        UNKNOWN("unknown"),
        MALE("unknown"),
        FEMALE("unknown");

        private final String name;

        TitleGender(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }
    }
}
