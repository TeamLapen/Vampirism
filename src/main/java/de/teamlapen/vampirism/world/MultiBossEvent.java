package de.teamlapen.vampirism.world;

import com.google.common.collect.Lists;
import de.teamlapen.lib.util.Color;
import de.teamlapen.vampirism.network.ClientboundUpdateMultiBossEventPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.world.BossEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MultiBossEvent {
    private final UUID uniqueId;
    protected Component name;
    protected BossEvent.BossBarOverlay overlay;
    protected List<Color> colors;
    protected Map<Color, Float> entries;

    public MultiBossEvent(UUID uniqueIdIn, Component nameIn, BossEvent.BossBarOverlay overlayIn, Color... entries) {
        this.uniqueId = uniqueIdIn;
        this.name = nameIn;
        this.overlay = overlayIn;
        this.colors = Lists.newArrayList(entries);
        this.entries = new HashMap<>();
    }

    public MultiBossEvent(@NotNull ClientboundUpdateMultiBossEventPacket packet) {
        this.uniqueId = packet.uniqueId();
        this.name = packet.name();
        this.colors = packet.colors();
        this.entries = packet.entries();
        this.overlay = packet.overlay();
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

    public void setPercentage(float @NotNull ... perc) {
        for (int i = 0; i < perc.length; i++) {
            if (this.colors.size() >= i + 1) {
                this.entries.put(this.colors.get(i), perc[i]);
            }
        }
    }

    public void updateFromPackage(@NotNull ClientboundUpdateMultiBossEventPacket packet) {
        switch (packet.operation()) {
            case UPDATE_NAME -> this.name = packet.name();
            case UPDATE_PROGRESS -> this.entries = packet.entries();
            case UPDATE_STYLE -> this.overlay = packet.overlay();
        }
    }
}
