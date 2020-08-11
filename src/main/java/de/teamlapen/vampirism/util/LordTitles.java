package de.teamlapen.vampirism.util;


import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class LordTitles {
    private static final ITextComponent VAMPIRE_1M = new TranslationTextComponent("text.vampirism.lord_title.vampire.male1");
    private static final ITextComponent VAMPIRE_2M = new TranslationTextComponent("text.vampirism.lord_title.vampire.male2");
    private static final ITextComponent VAMPIRE_3M = new TranslationTextComponent("text.vampirism.lord_title.vampire.male3");
    private static final ITextComponent VAMPIRE_4M = new TranslationTextComponent("text.vampirism.lord_title.vampire.male4");
    private static final ITextComponent VAMPIRE_5M = new TranslationTextComponent("text.vampirism.lord_title.vampire.male5");
    private static final ITextComponent VAMPIRE_1F = new TranslationTextComponent("text.vampirism.lord_title.vampire.female1");
    private static final ITextComponent VAMPIRE_2F = new TranslationTextComponent("text.vampirism.lord_title.vampire.female2");
    private static final ITextComponent VAMPIRE_3F = new TranslationTextComponent("text.vampirism.lord_title.vampire.female3");
    private static final ITextComponent VAMPIRE_4F = new TranslationTextComponent("text.vampirism.lord_title.vampire.female4");
    private static final ITextComponent VAMPIRE_5F = new TranslationTextComponent("text.vampirism.lord_title.vampire.female5");
    private static final ITextComponent HUNTER_1 = new TranslationTextComponent("text.vampirism.lord_title.hunter.1");
    private static final ITextComponent HUNTER_2 = new TranslationTextComponent("text.vampirism.lord_title.hunter.2");
    private static final ITextComponent HUNTER_3 = new TranslationTextComponent("text.vampirism.lord_title.hunter.3");
    private static final ITextComponent HUNTER_4 = new TranslationTextComponent("text.vampirism.lord_title.hunter.4");
    private static final ITextComponent HUNTER_5 = new TranslationTextComponent("text.vampirism.lord_title.hunter.5");
    private static final ITextComponent EMPTY = new StringTextComponent("");

    public static ITextComponent getVampireTitle(int level, boolean female) {
        if (female) {
            switch (level) {
                case 1:
                    return VAMPIRE_1F;
                case 2:
                    return VAMPIRE_2F;
                case 3:
                    return VAMPIRE_3F;
                case 4:
                    return VAMPIRE_4F;
                case 5:
                    return VAMPIRE_5F;
            }
        } else {
            switch (level) {
                case 1:
                    return VAMPIRE_1M;
                case 2:
                    return VAMPIRE_2M;
                case 3:
                    return VAMPIRE_3M;
                case 4:
                    return VAMPIRE_4M;
                case 5:
                    return VAMPIRE_5M;
            }
        }
        return EMPTY;
    }

    public static ITextComponent getHunterTitle(int level, boolean female) {
        switch (level) {
            case 1:
                return HUNTER_1;
            case 2:
                return HUNTER_2;
            case 3:
                return HUNTER_3;
            case 4:
                return HUNTER_4;
            case 5:
                return HUNTER_5;
        }
        return EMPTY;
    }


}
