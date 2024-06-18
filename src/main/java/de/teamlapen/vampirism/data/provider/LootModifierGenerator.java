package de.teamlapen.vampirism.data.provider;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.core.ModOils;
import de.teamlapen.vampirism.world.loot.SmeltItemLootModifier;
import de.teamlapen.vampirism.world.loot.conditions.FactionCondition;
import de.teamlapen.vampirism.world.loot.conditions.OilItemCondition;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;

import java.util.concurrent.CompletableFuture;

public class LootModifierGenerator extends GlobalLootModifierProvider {

    public LootModifierGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, REFERENCE.MODID);
    }

    @Override
    protected void start() {
        add("smelting", new SmeltItemLootModifier(new OilItemCondition(ModOils.SMELT.get()), new FactionCondition(VReference.HUNTER_FACTION)));
    }
}
