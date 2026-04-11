package de.teamlapen.vampirism.common.tags;

import de.teamlapen.vampirism.api.util.VIdentifier;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.trading.VillagerTrade;

public class ModVillagerTradeTags {

    public static final TagKey<VillagerTrade> VAMPIRE_EXPERT_LEVEL_1 = tag("vampire_expert/level_1");
    public static final TagKey<VillagerTrade> VAMPIRE_EXPERT_LEVEL_2 = tag("vampire_expert/level_2");
    public static final TagKey<VillagerTrade> VAMPIRE_EXPERT_LEVEL_3 = tag("vampire_expert/level_3");
    public static final TagKey<VillagerTrade> VAMPIRE_EXPERT_LEVEL_4 = tag("vampire_expert/level_4");
    public static final TagKey<VillagerTrade> VAMPIRE_EXPERT_LEVEL_5 = tag("vampire_expert/level_5");

    public static final TagKey<VillagerTrade> HUNTER_EXPERT_LEVEL_1 = tag("hunter_expert/level_1");
    public static final TagKey<VillagerTrade> HUNTER_EXPERT_LEVEL_2 = tag("hunter_expert/level_2");
    public static final TagKey<VillagerTrade> HUNTER_EXPERT_LEVEL_3 = tag("hunter_expert/level_3");
    public static final TagKey<VillagerTrade> HUNTER_EXPERT_LEVEL_4 = tag("hunter_expert/level_4");
    public static final TagKey<VillagerTrade> HUNTER_EXPERT_LEVEL_5 = tag("hunter_expert/level_5");

    public static final TagKey<VillagerTrade> PRIEST_LEVEL_1 = tag("priest/level_1");
    public static final TagKey<VillagerTrade> PRIEST_LEVEL_2 = tag("priest/level_2");
    public static final TagKey<VillagerTrade> PRIEST_LEVEL_3 = tag("priest/level_3");
    public static final TagKey<VillagerTrade> PRIEST_LEVEL_4 = tag("priest/level_4");
    public static final TagKey<VillagerTrade> PRIEST_LEVEL_5 = tag("priest/level_5");

    private static TagKey<VillagerTrade> tag(String name) {
        return TagKey.create(Registries.VILLAGER_TRADE, VIdentifier.mod(name));
    }
}
