package de.teamlapen.vampirism.entity.factions;

import de.teamlapen.vampirism.api.entity.factions.ILordTitleProvider;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents one playable faction (e.g. Vampire Player)
 * One instance should be used for players and entities at the same time.
 */
public class PlayableFaction<T extends IFactionPlayer<T>> extends Faction<T> implements IPlayableFaction<T> {
    private final int highestLevel;
    private final int highestLordLevel;
    private final Supplier<AttachmentType<T>> playerCapabilitySupplier;
    private final ILordTitleProvider lordTitleFunction;
    private final Function<IRefinementItem.AccessorySlotType, IRefinementItem> refinementItemBySlot;
    private final boolean hasLordSkills;

    PlayableFaction(FactionRegistry.@NotNull PlayableFactionBuilder<T> builder) {
        super(builder);
        this.highestLevel = builder.highestLevel;
        this.highestLordLevel = builder.lord.maxLevel;
        this.playerCapabilitySupplier = builder.playerCapabilitySupplier;
        this.lordTitleFunction = builder.lord.lordTitleFunction;
        this.refinementItemBySlot = builder.refinementItemBySlot;
        this.hasLordSkills = builder.lord.lordSkillsEnabled;
    }

    @Override
    public Class<T> getFactionPlayerInterface() {
        return super.getFactionEntityInterface();
    }

    @Override
    public int getHighestLordLevel() {
        return highestLordLevel;
    }

    @Override
    public int getHighestReachableLevel() {
        return highestLevel;
    }

    /**
     * @deprecated use HasLordSkills tag instead
     */
    @Override
    public boolean hasLordSkills() {
        return this.hasLordSkills;
    }

    @NotNull
    @Override
    public Component getLordTitle(int level, TitleGender female) {
        var title = lordTiles().getLordTitle(level, female);
        return title == null ? Component.empty() : title;
    }

    @Override
    public ILordTitleProvider lordTiles() {
        return this.lordTitleFunction;
    }

    @Override
    public @NotNull Optional<T> getPlayerCapability(@NotNull Player player) {
        return Optional.of(player.getData(playerCapabilitySupplier.get()));
    }

    @Override
    public boolean hasRefinements() {
        return this.refinementItemBySlot != null;
    }

    @Override
    public <Z extends Item & IRefinementItem> Z getRefinementItem(IRefinementItem.AccessorySlotType type) {
        assert this.refinementItemBySlot != null;
        //noinspection unchecked
        return ((Z) this.refinementItemBySlot.apply(type));
    }

    @Override
    public @NotNull String toString() {
        return "PlayableFaction{" +
                "id='" + id + '\'' +
                '}';
    }
}
