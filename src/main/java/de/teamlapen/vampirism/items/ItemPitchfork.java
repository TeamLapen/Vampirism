package de.teamlapen.vampirism.items;

/**
 * Mainly intended to be used by aggressive villagers.
 */
public class ItemPitchfork extends VampirismItemWeapon {

    private final static String regName = "pitchfork";

    public ItemPitchfork() {
        super(regName, ToolMaterial.IRON, -2.9F, (ToolMaterial.IRON.getDamageVsEntity() * 1.2F));

    }
}
