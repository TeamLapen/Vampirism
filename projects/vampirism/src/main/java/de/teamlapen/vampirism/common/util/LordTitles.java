package de.teamlapen.vampirism.common.util;


import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.faction.api.factions.lord.ILordTitleProvider;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class LordTitles {

    private static Component title(String faction, int level, String suffix) {
        return Component.translatable("lord_title.vampirism." + faction + "." + level + suffix);
    }

    public static class VampireTitles implements ILordTitleProvider {

        @Override
        public Component getLordTitle(int level, IPlayableFaction.@NotNull TitleGender titleGender) {
            String gender = titleGender == IPlayableFaction.TitleGender.FEMALE ? "female" : "male";
            return title("vampire." + gender, level, "");
        }

        @Override
        public Component getShort(int level, IPlayableFaction.@NotNull TitleGender titleGender) {
            String gender = titleGender == IPlayableFaction.TitleGender.FEMALE ? "female" : "male";
            return title("vampire." + gender, level, ".short");
        }
    }

    public static class HunterTitles implements ILordTitleProvider {

        @Override
        public Component getLordTitle(int level, IPlayableFaction.@NonNull TitleGender titleGender) {
            return title("hunter", level, "");
        }

        @Override
        public Component getShort(int level, IPlayableFaction.@NonNull TitleGender titleGender) {
            return title("hunter", level, ".short");
        }
    }
}
