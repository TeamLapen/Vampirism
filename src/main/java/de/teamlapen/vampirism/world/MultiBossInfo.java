package de.teamlapen.vampirism.world;

import com.google.common.collect.Lists;
import de.teamlapen.vampirism.network.UpdateMultiBossInfoPacket;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.BossInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MultiBossInfo {
    private final UUID uniqueId;
    protected ITextComponent name;
    protected BossInfo.Overlay overlay;
    protected List<BossInfo.Color> colors;
    protected Map<BossInfo.Color,Float> entries;

    public MultiBossInfo(UUID uniqueIdIn, ITextComponent nameIn, BossInfo.Overlay overlayIn, BossInfo.Color... entries) {
        this.uniqueId = uniqueIdIn;
        this.name = nameIn;
        this.overlay = overlayIn;
        this.colors = Lists.newArrayList(entries);
        this.entries = new HashMap<>();
    }

    public MultiBossInfo(UpdateMultiBossInfoPacket packet) {
        this.uniqueId = packet.getUniqueId();
        this.name = packet.getName();
        this.colors = packet.getColors();
        this.entries = packet.getEntries();
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public ITextComponent getName() {
        return name;
    }

    public void setName(ITextComponent name) {
        this.name = name;
    }

    public List<BossInfo.Color> getColors() {
        return colors;
    }

    public Map<BossInfo.Color, Float> getEntries() {
        return entries;
    }

    public void setPercentage(BossInfo.Color color, float perc) {
        this.entries.put(color,perc);
    }

    public void setPercentage(float... perc) {
        for (int i = 0; i < perc.length; i++) {
            if (this.colors.size() >= i + 1){
                this.entries.put(this.colors.get(i),perc[i]);
            }
        }
    }

    public void setColors(BossInfo.Color... entries) {
        this.colors = Lists.newArrayList(entries);
    }

    public void updateFromPackage(UpdateMultiBossInfoPacket packet) {
        switch (packet.getOperation()){
            case UPDATE_NAME:
                this.name = packet.getName();
                break;
            case UPDATE_PCT:
                this.entries = packet.getEntries();
                break;
        }
    }
}
