package de.teamlapen.vampirism.world;

import de.teamlapen.vampirism.network.UpdateMultiBossInfoPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.BossInfo;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class MultiBossInfo {
    private final UUID uniqueId;
    protected ITextComponent name;
    protected BossInfo.Overlay overlay;
    protected Map<ResourceLocation, Entry> entries;

    public MultiBossInfo(UUID uniqueIdIn, ITextComponent nameIn, BossInfo.Overlay overlayIn, Entry... entries) {
        this.uniqueId = uniqueIdIn;
        this.name = nameIn;
        this.overlay = overlayIn;
        this.entries = Arrays.stream(entries).collect(Collectors.toMap(a -> a.id, id -> id));
    }

    public MultiBossInfo(UpdateMultiBossInfoPacket packet) {
        this.uniqueId = packet.getUniqueId();
        this.name = packet.getName();
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

    public Map<ResourceLocation, Entry> getEntries() {
        return entries;
    }

    public void setPercentage(ResourceLocation id, float perc) {
        this.entries.get(id).setPercentage(perc);
    }

    public void setEntries(Entry... entry){
        this.entries = Arrays.stream(entry).collect(Collectors.toMap(Entry::getId, b -> b));
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


    public static class Entry{
        private final ResourceLocation id;
        private final BossInfo.Color color;
        private float percentage;
        private final int ordinal;

        public Entry(ResourceLocation id, BossInfo.Color color, int ordinal) {
            this(id, color, 0, ordinal);
        }

        public Entry(ResourceLocation id, BossInfo.Color color, float perc, int ordinal) {
            this.id = id;
            this.color = color;
            this.percentage = perc;
            this.ordinal = ordinal;
        }

        public void setPercentage(float percentage) {
            this.percentage = percentage;
        }

        public float getPercentage() {
            return percentage;
        }

        public int getOrdinal() {
            return ordinal;
        }

        public BossInfo.Color getColor() {
            return color;
        }

        public ResourceLocation getId() {
            return id;
        }
    }
}
