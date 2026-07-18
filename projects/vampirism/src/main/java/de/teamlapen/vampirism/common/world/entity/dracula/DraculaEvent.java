package de.teamlapen.vampirism.common.world.entity.dracula;

import de.teamlapen.vampirism.common.network.packets.client.ClientboundDraculaEventPacket;

import java.util.UUID;

public class DraculaEvent {

    private final UUID id;
    protected float percentage;
    protected FightStage stage;
    protected boolean inVulnerable;

    public DraculaEvent(UUID id, float percentage, FightStage stage, boolean inVulnerable) {
        this.id = id;
        this.percentage = percentage;
        this.stage = stage;
        this.inVulnerable = inVulnerable;
    }

    public UUID id() {
        return this.id;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }

    public float getPercentage() {
        return percentage;
    }

    public void setStage(FightStage stage) {
        this.stage = stage;
    }

    public FightStage getStage() {
        return stage;
    }

    public void setInVulnerable(boolean inVulnerable) {
        this.inVulnerable = inVulnerable;
    }

    public boolean isInVulnerable() {
        return inVulnerable;
    }

    public static DraculaEvent fromOperation(ClientboundDraculaEventPacket.AddOperation operation) {
        return new DraculaEvent(operation.id(), operation.percentage(), operation.stage(), operation.isVulnerable());
    }
}