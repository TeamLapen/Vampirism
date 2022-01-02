package de.teamlapen.vampirism.api.entity.factions;

import net.minecraft.util.text.ITextComponent;

import java.util.function.BiFunction;

@FunctionalInterface
public interface LordTitles extends BiFunction<Integer, Boolean, ITextComponent> {

    ITextComponent apply(Integer rank, Boolean gender);

    default boolean areGenderNeutral() {
        return false;
    }

    @FunctionalInterface
    interface LordTitlesNeutral extends LordTitles {

        ITextComponent apply(int rank);

        default ITextComponent apply(Integer rank, Boolean gender){
            return apply(rank);
        }

        default boolean areGenderNeutral() {
            return true;
        }

    }
}
