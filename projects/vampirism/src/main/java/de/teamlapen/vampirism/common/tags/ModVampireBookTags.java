package de.teamlapen.vampirism.common.tags;

import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.api.world.items.components.IVampireBook;
import net.minecraft.tags.TagKey;

public class ModVampireBookTags {
    public static final TagKey<IVampireBook> IS_GENERAL = tag("is_general");
    public static final TagKey<IVampireBook> IS_VAMPIRE = tag("is_vampire");
    public static final TagKey<IVampireBook> IS_HUNTER = tag("is_hunter");
    public static final TagKey<IVampireBook> NON_TREASURE = tag("non_treasure");

    private static TagKey<IVampireBook> tag(String name) {
        return TagKey.create(VampirismRegistries.Keys.VAMPIRE_BOOK, VIdentifier.mod(name));
    }
}
