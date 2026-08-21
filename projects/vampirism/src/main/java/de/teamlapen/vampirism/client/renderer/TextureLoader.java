package de.teamlapen.vampirism.client.renderer;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Collection;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TextureLoader {

    public static Collection<Identifier> loadTexturesIn(String path) {
        return Minecraft.getInstance().getResourceManager().listResources(path, s -> s.getPath().endsWith(".png")).keySet().stream().sorted(new NumericalSort()).toList();
    }

    public static Object2ObjectLinkedOpenHashMap<Identifier, Identifier> mapTexturesInById(String path) {
        var textures = loadTexturesIn(path);

        return textures.stream().collect(Collectors.toMap(x -> x.withPath(TextureLoader::lastSegment), x -> x,(_, b) -> b, Object2ObjectLinkedOpenHashMap::new));
    }

    private static String lastSegment(String path) {
        String[] split = path.split("/");
        if (split.length == 0) return "";
        var last = split[split.length - 1];
        int i = last.lastIndexOf('.');
        return i == -1 ? last : last.substring(0, i);
    }

    private static class NumericalSort implements Comparator<Identifier> {

        private static final Pattern CHUNK = Pattern.compile("\\d+|\\D+");

        @Override
        public int compare(Identifier o1, Identifier o2) {
            int i = o1.getNamespace().compareTo(o2.getNamespace());
            if (i != 0) return i;
            return compareNaturally(o1.getPath(), o2.getPath());
        }

        private static int compareNaturally(String a, String b) {
            Matcher ma = CHUNK.matcher(a);
            Matcher mb = CHUNK.matcher(b);
            while (ma.find() && mb.find()) {
                String ca = ma.group();
                String cb = mb.group();
                int cmp = NumberUtils.isDigits(ca) && NumberUtils.isDigits(cb)
                        ? Integer.compare(NumberUtils.toInt(ca), NumberUtils.toInt(cb))
                        : ca.compareTo(cb);
                if (cmp != 0) return cmp;
            }
            return a.length() - b.length();
        }

    }
}
