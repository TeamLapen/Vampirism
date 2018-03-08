package de.teamlapen.vampirism.items;

/**
 * Mainly intended to be used by aggressive villagers.
 */
public class ItemPitchfork extends VampirismItemWeapon {

    private final static String regName = "pitchfork";

    public ItemPitchfork() {
        super(regName, ToolMaterial.IRON, 0.27F, 8F);

    }
}
