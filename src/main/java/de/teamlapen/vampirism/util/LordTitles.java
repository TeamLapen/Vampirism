package de.teamlapen.vampirism.util;


import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class LordTitles {
    private static final Component VAMPIRE_1M = new TranslatableComponent("text.vampirism.lord_title.vampire.male1");
    private static final Component VAMPIRE_2M = new TranslatableComponent("text.vampirism.lord_title.vampire.male2");
    private static final Component VAMPIRE_3M = new TranslatableComponent("text.vampirism.lord_title.vampire.male3");
    private static final Component VAMPIRE_4M = new TranslatableComponent("text.vampirism.lord_title.vampire.male4");
    private static final Component VAMPIRE_5M = new TranslatableComponent("text.vampirism.lord_title.vampire.male5");
    private static final Component VAMPIRE_1F = new TranslatableComponent("text.vampirism.lord_title.vampire.female1");
    private static final Component VAMPIRE_2F = new TranslatableComponent("text.vampirism.lord_title.vampire.female2");
    private static final Component VAMPIRE_3F = new TranslatableComponent("text.vampirism.lord_title.vampire.female3");
    private static final Component VAMPIRE_4F = new TranslatableComponent("text.vampirism.lord_title.vampire.female4");
    private static final Component VAMPIRE_5F = new TranslatableComponent("text.vampirism.lord_title.vampire.female5");
    private static final Component HUNTER_1 = new TranslatableComponent("text.vampirism.lord_title.hunter.1");
    private static final Component HUNTER_2 = new TranslatableComponent("text.vampirism.lord_title.hunter.2");
    private static final Component HUNTER_3 = new TranslatableComponent("text.vampirism.lord_title.hunter.3");
    private static final Component HUNTER_4 = new TranslatableComponent("text.vampirism.lord_title.hunter.4");
    private static final Component HUNTER_5 = new TranslatableComponent("text.vampirism.lord_title.hunter.5");
    private static final Component EMPTY = new TextComponent("");

    public static Component getVampireTitle(int level, boolean female) {
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

    public static Component getHunterTitle(int level, boolean female) {
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
