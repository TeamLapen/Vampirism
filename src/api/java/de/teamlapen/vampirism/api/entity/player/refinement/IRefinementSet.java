package de.teamlapen.vampirism.api.entity.player.refinement;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.Set;

public interface IRefinementSet {

    int getColor();

    @Nonnull
    IFaction<?> getFaction();

    @Nonnull
    Component getName();

    @Nonnull
    Rarity getRarity();

    /**
     * TODO 1.19 remove and rename the new method
     * Use {@link #getRefinementRegistryObjects()}
     */
    @Deprecated
    @Nonnull
    Set<IRefinement> getRefinements();

    @Nonnull
    Set<RegistryObject<? extends IRefinement>> getRefinementRegistryObjects();

    /**
     * @return The accessory type this can be on, or empty if all
     */
    Optional<IRefinementItem.AccessorySlotType> getSlotType();

    enum Rarity {
        COMMON(4, ChatFormatting.WHITE),
        UNCOMMON(3, ChatFormatting.GREEN),
        RARE(3, ChatFormatting.BLUE),
        EPIC(2, ChatFormatting.DARK_PURPLE),
        LEGENDARY(1, ChatFormatting.GOLD);

        public final int weight;
        public final ChatFormatting color;

        Rarity(int weight, ChatFormatting color) {
            this.weight = weight;
            this.color = color;
        }
    }
}
