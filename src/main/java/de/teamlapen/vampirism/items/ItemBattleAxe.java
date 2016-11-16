package de.teamlapen.vampirism.items;

/**
 * Temporary axe for testing some stuff
 * TODO remove
 */
public class ItemBattleAxe extends VampirismHunterWeapon.SimpleHunterSword {
    private final static String name = "battleAxe";

    public ItemBattleAxe() {
        super(name, ToolMaterial.IRON, 2, 1.5F);
        this.setCreativeTab(null);
    }


}
