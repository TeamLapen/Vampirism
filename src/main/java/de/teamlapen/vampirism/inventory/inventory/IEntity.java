package de.teamlapen.vampirism.inventory.inventory;

import net.minecraft.entity.player.PlayerEntity;

public class IEntity {

    private PlayerEntity trading;

    public IEntity(PlayerEntity trading) {
        this.trading = trading;
    }

    public void setTrading(PlayerEntity player) {
        trading = player;
    }

    public PlayerEntity getTrading() {
        return trading;
    }
}
