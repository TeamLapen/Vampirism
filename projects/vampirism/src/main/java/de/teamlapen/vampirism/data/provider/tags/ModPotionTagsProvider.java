package de.teamlapen.vampirism.data.provider.tags;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.common.core.ModPotions;
import de.teamlapen.vampirism.common.tags.ModPotionTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.KeyTagProvider;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("ConstantConditions")
public class ModPotionTagsProvider extends KeyTagProvider<Potion> {

    public ModPotionTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, Registries.POTION, provider, REFERENCE.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(ModPotionTags.SERUM_BLOCKED)
                .add(ModPotions.RESISTANCE.getKey())
                .add(ModPotions.STRONG_RESISTANCE.getKey())
                .add(ModPotions.LONG_RESISTANCE.getKey())
                .add(Potions.HEALING.getKey())
                .add(ModPotions.VERY_STRONG_HEALING.getKey())
                .add(Potions.STRONG_HEALING.getKey())
                .add(Potions.TURTLE_MASTER.getKey())
                .add(Potions.STRONG_TURTLE_MASTER.getKey())
                .add(Potions.LONG_TURTLE_MASTER.getKey());
    }
}
