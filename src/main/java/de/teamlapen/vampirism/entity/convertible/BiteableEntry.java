package de.teamlapen.vampirism.entity.convertible;

/**
 * Created by Max on 15.08.2015.
 */
public class BiteableEntry {
    public final int blood;
    public final boolean convertible;
    public final ConvertingHandler convertingHandler;

    public BiteableEntry(int blood, ConvertingHandler convertingHandler) {
        this.blood = blood;
        this.convertible = true;
        this.convertingHandler = convertingHandler;
    }

    public BiteableEntry(int blood) {
        this.blood = blood;
        this.convertible = false;
        this.convertingHandler = null;
    }
}
