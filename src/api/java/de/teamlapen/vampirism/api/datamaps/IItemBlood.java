package de.teamlapen.vampirism.api.datamaps;

/**
 * Blood extension for items.
 * <br>
 * <br>
 * This interface is used as <a href="https://docs.neoforged.net/docs/datamaps/">neoforge datamap</a> entry for {@link de.teamlapen.vampirism.api.VampirismRegistries#ITEM_BLOOD_MAP}
 */
public interface IItemBlood {

    /**
     * @return The amount of impure blood this item can be converted to
     */
    int blood();
}
