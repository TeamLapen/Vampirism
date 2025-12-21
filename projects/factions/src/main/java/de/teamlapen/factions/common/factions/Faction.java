package de.teamlapen.factions.common.factions;

import de.teamlapen.factions.api.FactionRegistries;
import de.teamlapen.factions.api.FactionsApi;
import de.teamlapen.factions.api.factions.IFaction;
import de.teamlapen.factions.api.factions.IFactionEntity;
import de.teamlapen.factions.api.factions.village.IFactionVillage;
import de.teamlapen.factions.common.util.RegUtil;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents an entity faction (e.g. Vampires)
 */
public class Faction<T extends IFactionEntity> implements IFaction<T> {
    private final int color;
    private final IFactionVillage villageFactionData;
    @Nullable
    private String descriptionId;
    @Nullable
    private String descriptionIdPlural;
    private final TextColor chatColor;

    Faction(FactionBuilder<T> builder) {
        this.color = builder.color;
        this.villageFactionData = Objects.requireNonNullElseGet(builder.villageFactionData, () -> new FactionVillageBuilder().build());
        this.chatColor = builder.chatColor == null ? TextColor.fromRgb(this.color) : builder.chatColor;
    }

    @Override
    public TextColor getChatColor() {
        return this.chatColor;
    }

    @Override
    public int getColor() {
        return color;
    }

    @Override
    public String getDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = Util.makeDescriptionId("faction", RegUtil.id(this));
        }
        return this.descriptionId;
    }

    @Override
    public String getDescriptionIdPlural() {
        if (this.descriptionIdPlural == null) {
            this.descriptionIdPlural = getDescriptionId() + ".plural";
        }
        return this.descriptionIdPlural;
    }

    @Override
    public IFactionVillage getVillageData() {
        return villageFactionData;
    }
}
