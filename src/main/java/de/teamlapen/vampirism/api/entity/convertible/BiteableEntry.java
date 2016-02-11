package de.teamlapen.vampirism.api.entity.convertible;

/**
 * Stores max blood as well as information about converting for EntityCreature classes
 */
public class BiteableEntry {
    /**
     * Max blood the creature can have
     */
    public final int blood;
    /**
     * Whether the creature is convertible or not
     */
    public final boolean convertible;
    /**
     * if {@link #convertible} is true, this stores the handler which should be used for conversion
     */
    public final ConvertingHandler convertingHandler;

    /**
     * Entry for a biteable and convertible creature
     *
     * @param blood             Max blood the creature can have
     * @param convertingHandler Handler used for conversion
     */
    public BiteableEntry(int blood, ConvertingHandler convertingHandler) {
        this.blood = blood;
        this.convertible = true;
        this.convertingHandler = convertingHandler;
    }

    /**
     * Entry for a biteable, but non convertible creature
     *
     * @param blood Max blood the creature can have
     */
    public BiteableEntry(int blood) {
        this.blood = blood;
        this.convertible = false;
        this.convertingHandler = null;
    }
}
