package de.teamlapen.faction.api.world.entities;

public interface ICustomizationHolder {

    ICustomizationHolder NONE = () -> 0;

    int getEntityTextureType();
}
