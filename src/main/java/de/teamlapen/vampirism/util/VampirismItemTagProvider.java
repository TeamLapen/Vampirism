package de.teamlapen.vampirism.util;

import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;

public class VampirismItemTagProvider extends ItemTagsProvider {
    public VampirismItemTagProvider(DataGenerator gen) {
        super(gen);
    }

    @Override
    public String getName() {
        return "Vampirism Item Tags";
    }

    @Override
    protected void registerTags() {
        copy(ModTags.Blocks.OBSIDIAN, ModTags.Items.OBSIDIAN);
        copy(ModTags.Blocks.CASTLE_BLOCK, ModTags.Items.CASTLE_BLOCK);

        getBuilder(ModTags.Items.CROSSBOW_ARROW).add(ModItems.crossbow_arrow_normal, ModItems.crossbow_arrow_spitfire, ModItems.crossbow_arrow_vampire_killer);
        getBuilder(ModTags.Items.HUNTER_INTEL).add(ModItems.hunter_intel_0, ModItems.hunter_intel_1, ModItems.hunter_intel_2, ModItems.hunter_intel_3, ModItems.hunter_intel_4, ModItems.hunter_intel_5, ModItems.hunter_intel_6, ModItems.hunter_intel_7, ModItems.hunter_intel_8, ModItems.hunter_intel_9);
        getBuilder(ModTags.Items.PURE_BLOOD).add(ModItems.pure_blood_0, ModItems.pure_blood_1, ModItems.pure_blood_2, ModItems.pure_blood_3, ModItems.pure_blood_4);
        getBuilder(ModTags.Items.VAMPIRE_CLOAK).add(ModItems.vampire_cloak_black_blue, ModItems.vampire_cloak_black_red, ModItems.vampire_cloak_black_white, ModItems.vampire_cloak_red_black, ModItems.vampire_cloak_white_black);
    }
}
