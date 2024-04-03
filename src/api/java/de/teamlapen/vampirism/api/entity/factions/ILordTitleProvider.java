package de.teamlapen.vampirism.api.entity.factions;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public interface ILordTitleProvider {

    /**
     * Get a lord title.
     *
     * @param level the lord level
     * @param titleGender the gender of the title
     * @return the lord title
     */
    @Nullable
    Component getLordTitle(int level, IPlayableFaction.TitleGender titleGender);

    /**
     * Get a short lord title.
     * @param level the lord level
     * @param titleGender the gender of the title
     * @return the short lord title
     */
    @Nullable
    Component getShort(int level, IPlayableFaction.TitleGender titleGender);

}
