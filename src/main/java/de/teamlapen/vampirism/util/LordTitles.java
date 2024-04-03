package de.teamlapen.vampirism.util;


import de.teamlapen.vampirism.api.entity.factions.ILordTitleProvider;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import net.minecraft.network.chat.Component;

public class LordTitles {
    private static final Component VAMPIRE_1M = Component.translatable("text.vampirism.lord_title.vampire.male1");
    private static final Component VAMPIRE_2M = Component.translatable("text.vampirism.lord_title.vampire.male2");
    private static final Component VAMPIRE_3M = Component.translatable("text.vampirism.lord_title.vampire.male3");
    private static final Component VAMPIRE_4M = Component.translatable("text.vampirism.lord_title.vampire.male4");
    private static final Component VAMPIRE_5M = Component.translatable("text.vampirism.lord_title.vampire.male5");
    private static final Component VAMPIRE_1MS = Component.translatable("text.vampirism.lord_title.vampire.male1.short");
    private static final Component VAMPIRE_2MS = Component.translatable("text.vampirism.lord_title.vampire.male2.short");
    private static final Component VAMPIRE_3MS = Component.translatable("text.vampirism.lord_title.vampire.male3.short");
    private static final Component VAMPIRE_4MS = Component.translatable("text.vampirism.lord_title.vampire.male4.short");
    private static final Component VAMPIRE_5MS = Component.translatable("text.vampirism.lord_title.vampire.male5.short");
    private static final Component VAMPIRE_1F = Component.translatable("text.vampirism.lord_title.vampire.female1");
    private static final Component VAMPIRE_2F = Component.translatable("text.vampirism.lord_title.vampire.female2");
    private static final Component VAMPIRE_3F = Component.translatable("text.vampirism.lord_title.vampire.female3");
    private static final Component VAMPIRE_4F = Component.translatable("text.vampirism.lord_title.vampire.female4");
    private static final Component VAMPIRE_5F = Component.translatable("text.vampirism.lord_title.vampire.female5");
    private static final Component VAMPIRE_1FS = Component.translatable("text.vampirism.lord_title.vampire.female1.short");
    private static final Component VAMPIRE_2FS = Component.translatable("text.vampirism.lord_title.vampire.female2.short");
    private static final Component VAMPIRE_3FS = Component.translatable("text.vampirism.lord_title.vampire.female3.short");
    private static final Component VAMPIRE_4FS = Component.translatable("text.vampirism.lord_title.vampire.female4.short");
    private static final Component VAMPIRE_5FS = Component.translatable("text.vampirism.lord_title.vampire.female5.short");
    private static final Component HUNTER_1 = Component.translatable("text.vampirism.lord_title.hunter.1");
    private static final Component HUNTER_2 = Component.translatable("text.vampirism.lord_title.hunter.2");
    private static final Component HUNTER_3 = Component.translatable("text.vampirism.lord_title.hunter.3");
    private static final Component HUNTER_4 = Component.translatable("text.vampirism.lord_title.hunter.4");
    private static final Component HUNTER_5 = Component.translatable("text.vampirism.lord_title.hunter.5");
    private static final Component HUNTER_1S = Component.translatable("text.vampirism.lord_title.hunter.1.short");
    private static final Component HUNTER_2S = Component.translatable("text.vampirism.lord_title.hunter.2.short");
    private static final Component HUNTER_3S = Component.translatable("text.vampirism.lord_title.hunter.3.short");
    private static final Component HUNTER_4S = Component.translatable("text.vampirism.lord_title.hunter.4.short");
    private static final Component HUNTER_5S = Component.translatable("text.vampirism.lord_title.hunter.5.short");


    @SuppressWarnings("DuplicatedCode")
    public static class VampireTitles implements ILordTitleProvider {
        @Override
        public Component getLordTitle(int level, IPlayableFaction.TitleGender titleGender) {
            return switch (titleGender) {
                case FEMALE -> switch (level) {
                    case 1 -> VAMPIRE_1F;
                    case 2 -> VAMPIRE_2F;
                    case 3 -> VAMPIRE_3F;
                    case 4 -> VAMPIRE_4F;
                    case 5 -> VAMPIRE_5F;
                    default -> null;
                };
                case MALE, UNKNOWN -> switch (level) {
                    case 1 -> VAMPIRE_1M;
                    case 2 -> VAMPIRE_2M;
                    case 3 -> VAMPIRE_3M;
                    case 4 -> VAMPIRE_4M;
                    case 5 -> VAMPIRE_5M;
                    default -> null;
                };
            };
        }

        @Override
        public Component getShort(int level, IPlayableFaction.TitleGender titleGender) {
            return switch (titleGender) {
                case FEMALE -> switch (level) {
                    case 1 -> VAMPIRE_1FS;
                    case 2 -> VAMPIRE_2FS;
                    case 3 -> VAMPIRE_3FS;
                    case 4 -> VAMPIRE_4FS;
                    case 5 -> VAMPIRE_5FS;
                    default -> null;
                };
                case MALE, UNKNOWN -> switch (level) {
                    case 1 -> VAMPIRE_1MS;
                    case 2 -> VAMPIRE_2MS;
                    case 3 -> VAMPIRE_3MS;
                    case 4 -> VAMPIRE_4MS;
                    case 5 -> VAMPIRE_5MS;
                    default -> null;
                };
            };
        }
    }

    @SuppressWarnings("DuplicatedCode")
    public static class HunterTitles implements ILordTitleProvider {
        @Override
        public Component getLordTitle(int level, IPlayableFaction.TitleGender titleGender) {
            return switch (level) {
                case 1 -> HUNTER_1;
                case 2 -> HUNTER_2;
                case 3 -> HUNTER_3;
                case 4 -> HUNTER_4;
                case 5 -> HUNTER_5;
                default -> null;
            };
        }

        @Override
        public Component getShort(int level, IPlayableFaction.TitleGender titleGender) {
            return switch (level) {
                case 1 -> HUNTER_1S;
                case 2 -> HUNTER_2S;
                case 3 -> HUNTER_3S;
                case 4 -> HUNTER_4S;
                case 5 -> HUNTER_5S;
                default -> null;
            };
        }
    }


}
