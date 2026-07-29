package de.teamlapen.faction.common.factions;

import de.teamlapen.faction.api.factions.FactionProperties;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.IFactionEntity;
import de.teamlapen.faction.api.factions.village.IFactionVillage;
import de.teamlapen.faction.common.core.FactionDataComponents;
import de.teamlapen.faction.common.core.ModRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentInitializers;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;

/**
 * Represents an entity faction (e.g. Vampires)
 */
public class Faction<T extends IFactionEntity> implements IFaction<T> {
    private final String descriptionId;
    private final Holder.Reference<IFaction<?>> builtInRegistryHolder;

    private static final int WHITE_COLOR = 0xffffff;
    private static final TextColor WHITE_TEXT = TextColor.fromRgb(WHITE_COLOR);


    public Faction(FactionProperties properties) {
        this.builtInRegistryHolder = ModRegistries.FACTIONS.createIntrusiveHolder(this);
        this.descriptionId = properties.effectiveDescriptionId();
        DataComponentInitializers.Initializer<IFaction<?>> iFactionInitializer = properties.finalizeInitializer(Component.translatable(properties.effectiveDescriptionIdSingular()), Component.translatable(properties.effectiveDescriptionIdPlural()));
        BuiltInRegistries.DATA_COMPONENT_INITIALIZERS.add(properties.itemIdOrThrow(), iFactionInitializer);
    }

    @Override
    public TextColor getChatColor() {
        return this.builtInRegistryHolder.components().getOrDefault(FactionDataComponents.CHAT_COLOR, WHITE_TEXT);
    }

    public DataComponentMap components() {
        return this.builtInRegistryHolder.components();
    }


    @Override
    public int getColor() {
        return this.builtInRegistryHolder.components().getOrDefault(FactionDataComponents.FACTION_COLOR, WHITE_COLOR);
    }

    @Override
    public String getDescriptionId() {
        return this.descriptionId;
    }

    @Override
    public Component getNameSingular() {
        return this.builtInRegistryHolder.components().getOrDefault(FactionDataComponents.FACTION_NAME_SINGULAR, CommonComponents.EMPTY);
    }

    @Override
    public Component getNamePlural() {
        return this.builtInRegistryHolder.components().getOrDefault(FactionDataComponents.FACTION_NAME_PLURAL, CommonComponents.EMPTY);
    }

}
