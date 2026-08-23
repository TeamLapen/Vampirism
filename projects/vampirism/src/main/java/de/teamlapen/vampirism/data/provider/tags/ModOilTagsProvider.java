package de.teamlapen.vampirism.data.provider.tags;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.world.items.oil.IOil;
import de.teamlapen.vampirism.common.core.ModOils;
import de.teamlapen.vampirism.common.tags.ModOilTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.KeyTagProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModOilTagsProvider extends KeyTagProvider<IOil> {

    public ModOilTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, VampirismRegistries.Keys.OIL, provider, REFERENCE.MODID);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider pProvider) {
        this.tag(ModOilTags.NON_TREASURE);
        this.tag(ModOilTags.STRONG)
                .add(ModOils.SOVEREIGN_BLOOD.getKey())
                .add(ModOils.POISON_STRONG.getKey())
                .add(ModOils.WEAKNESS_STRONG.getKey())
                .add(ModOils.SLOWNESS_STRONG.getKey())
                .add(ModOils.HEALING_STRONG.getKey())
                .add(ModOils.FIRE_RESISTANCE_STRONG.getKey())
                .add(ModOils.SWIFTNESS_STRONG.getKey())
                .add(ModOils.REGENERATION_STRONG.getKey())
                .add(ModOils.NIGHT_VISION_STRONG.getKey())
                .add(ModOils.STRENGTH_STRONG.getKey())
                .add(ModOils.JUMP_STRONG.getKey())
                .add(ModOils.WATER_BREATHING_STRONG.getKey())
                .add(ModOils.INVISIBILITY_STRONG.getKey())
                .add(ModOils.SLOW_FALLING_STRONG.getKey())
                .add(ModOils.LUCK_STRONG.getKey())
                .add(ModOils.HARM_STRONG.getKey())
                .add(ModOils.SMELT_STRONG.getKey())
                .add(ModOils.EVASION_STRONG.getKey())
                ;
    }
}
