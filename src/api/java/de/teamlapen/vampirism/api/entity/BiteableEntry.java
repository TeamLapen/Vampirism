package de.teamlapen.vampirism.api.entity;

import de.teamlapen.vampirism.api.entity.convertible.IConvertingHandler;

/**
 * Stores max blood as well as information about converting for EntityCreature classes
 */
public class BiteableEntry {

    /**
     * Max blood the creature can have
     * If 0 the entity is not biteable
     */
    public final int blood;
    /**
     * Whether the creature is convertible or not
     */
    public final boolean convertible;
    /**
     * if {@link #convertible} is true, this stores the handler which should be used for conversion
     */
    public final IConvertingHandler convertingHandler;

    /**
     * Entry for a biteable and convertible creature
     *
     * @param blood             Max blood the creature can have
     * @param convertingHandler Handler used for conversion
     */
    public BiteableEntry(int blood, IConvertingHandler convertingHandler) {
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

    public BiteableEntry modifyBloodValue(int blood) {
        return this.convertible ? new BiteableEntry(blood, this.convertingHandler) : new BiteableEntry(blood);
    }
}
