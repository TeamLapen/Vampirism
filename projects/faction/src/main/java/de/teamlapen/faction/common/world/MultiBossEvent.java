package de.teamlapen.faction.common.world;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import de.teamlapen.faction.common.network.packets.client.ClientboundUpdateMultiBossEventPacket;
import de.teamlapen.faction.common.util.Color;
import net.minecraft.network.chat.Component;
import net.minecraft.world.BossEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MultiBossEvent implements ValueIOSerializable {
    private final UUID uniqueId;
    protected Component name;
    protected BossEvent.BossBarOverlay overlay;
    protected List<Color> colors;
    protected Map<Color, Float> entries;

    public static final Codec<Map<Color, Float>> ENTRIES_CODEC = Codec.unboundedMap(Color.CODEC, Codec.FLOAT);

    public MultiBossEvent(UUID uniqueIdIn, Component nameIn, BossEvent.BossBarOverlay overlayIn, Color... entries) {
        this.uniqueId = uniqueIdIn;
        this.name = nameIn;
        this.overlay = overlayIn;
        this.colors = Lists.newArrayList(entries);
        this.entries = new HashMap<>();
    }

    public MultiBossEvent(ClientboundUpdateMultiBossEventPacket.AddOperation operation) {
        this.uniqueId = operation.uniqueId();
        this.name = operation.name();
        this.colors = operation.colors();
        this.entries = operation.entries();
        this.overlay = operation.overlay();
    }

    @Override
    public void serialize(ValueOutput output) {
        output.store("colors", Color.CODEC.listOf(), colors);
        output.store("entries", ENTRIES_CODEC, entries);
    }

    @Override
    public void deserialize(ValueInput input) {
        this.colors = input.read("colors", Color.CODEC.listOf()).orElseGet(List::of);
        this.entries = new HashMap<>(input.read("entries", ENTRIES_CODEC).orElseGet(Map::of));
    }

    public void clear() {
        this.entries.clear();
    }

    public List<Color> getColors() {
        return colors;
    }

    public void setColors(Color... entries) {
        this.colors = Lists.newArrayList(entries);
    }

    public Map<Color, Float> getEntries() {
        return entries;
    }

    public Component getName() {
        return name;
    }

    public void setName(Component name) {
        this.name = name;
    }

    public BossEvent.BossBarOverlay getOverlay() {
        return overlay;
    }

    public void setOverlay(BossEvent.BossBarOverlay overlay) {
        this.overlay = overlay;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public void setPercentage(Color color, float perc) {
        if (!this.colors.contains(color)) {
            this.colors.add(color);
        }
        this.entries.put(color, perc);
    }

    public void setPercentage(float ... perc) {
        for (int i = 0; i < perc.length; i++) {
            if (this.colors.size() >= i + 1) {
                this.entries.put(this.colors.get(i), perc[i]);
            }
        }
    }

    public void updateFromPackage(ClientboundUpdateMultiBossEventPacket.Operation packet) {
        switch (packet) {
            case ClientboundUpdateMultiBossEventPacket.UpdateNameOperation operation -> this.name = operation.name();
            case ClientboundUpdateMultiBossEventPacket.UpdateProgressOperation operation -> this.entries = operation.entries();
            case ClientboundUpdateMultiBossEventPacket.UpdateStyle operation -> this.overlay = operation.overlay();
            default -> {
            }
        }
    }
}
