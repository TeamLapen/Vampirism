package de.teamlapen.vampirism.common.tags;

import de.teamlapen.vampirism.api.util.VIdentifier;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.timeline.Timeline;

public class ModTimelineTags {

    public static final TagKey<Timeline> IN_VELMORRA = tag("in_velmorra");

    private static TagKey<Timeline> tag(String name) {
        return TagKey.create(Registries.TIMELINE, VIdentifier.mod(name));
    }
}
