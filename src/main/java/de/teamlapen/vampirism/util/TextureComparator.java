package de.teamlapen.vampirism.util;

import net.minecraft.resources.ResourceLocation;

import java.util.Comparator;

public class TextureComparator {

    public static Comparator<ResourceLocation> alphaNumericComparator() {
        return (o1, o2) -> {
            String[] parts1 = o1.toString().split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
            String[] parts2 = o2.toString().split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");

            int length = Math.min(parts1.length, parts2.length);
            for (int i = 0; i < length; i++) {
                if (isInteger(parts1[i]) && isInteger(parts2[i])) {
                    int intComparison = Integer.compare(Integer.parseInt(parts1[i]), Integer.parseInt(parts2[i]));
                    if (intComparison != 0) {
                        return intComparison;
                    }
                } else {
                    int stringComparison = parts1[i].compareTo(parts2[i]);
                    if (stringComparison != 0) {
                        return stringComparison;
                    }
                }
            }

            return Integer.compare(parts1.length, parts2.length);
        };
    }

    private static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
