package de.teamlapen.vampirism.world;

import com.google.common.collect.Lists;
import de.teamlapen.vampirism.network.SUpdateMultiBossInfoPacket;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.BossInfo;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MultiBossInfo {
    private final UUID uniqueId;
    protected ITextComponent name;
    protected BossInfo.Overlay overlay;
    protected List<Color> colors;
    protected Map<Color, Float> entries;

    public MultiBossInfo(UUID uniqueIdIn, ITextComponent nameIn, BossInfo.Overlay overlayIn, Color... entries) {
        this.uniqueId = uniqueIdIn;
        this.name = nameIn;
        this.overlay = overlayIn;
        this.colors = Lists.newArrayList(entries);
        this.entries = new HashMap<>();
    }

    public MultiBossInfo(SUpdateMultiBossInfoPacket packet) {
        this.uniqueId = packet.getUniqueId();
        this.name = packet.getName();
        this.colors = packet.getColors();
        this.entries = packet.getEntries();
        this.overlay = packet.getOverlay();
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

    public ITextComponent getName() {
        return name;
    }

    public void setName(ITextComponent name) {
        this.name = name;
    }

    public BossInfo.Overlay getOverlay() {
        return overlay;
    }

    public void setOverlay(BossInfo.Overlay overlay) {
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

    public void setPercentage(float... perc) {
        for (int i = 0; i < perc.length; i++) {
            if (this.colors.size() >= i + 1) {
                this.entries.put(this.colors.get(i), perc[i]);
            }
        }
    }

    public void updateFromPackage(SUpdateMultiBossInfoPacket packet) {
        switch (packet.getOperation()) {
            case UPDATE_NAME:
                this.name = packet.getName();
                break;
            case UPDATE_PCT:
                this.entries = packet.getEntries();
                break;
            case UPDATE_STYLE:
                this.overlay = packet.getOverlay();
                break;
        }
    }
}
