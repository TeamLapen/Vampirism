package de.teamlapen.vampirism.data.provider.tags;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.common.core.ModEnchantments;
import de.teamlapen.vampirism.common.tags.ModEnchantmentTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.KeyTagProvider;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class ModEnchantmentTagsProvider extends KeyTagProvider<Enchantment> {

    public ModEnchantmentTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, Registries.ENCHANTMENT, provider, REFERENCE.MODID);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(Tags.Enchantments.WEAPON_DAMAGE_ENHANCEMENTS).add(ModEnchantments.VAMPIRE_SLAYER);
        this.tag(EnchantmentTags.TREASURE).add(ModEnchantments.PRECISION);
        this.tag(ModEnchantmentTags.CROSSBOW_INCOMPATIBLE).add(Enchantments.PIERCING);
        this.tag(ModEnchantmentTags.SINGLE_HUNTER_CROSSBOW_COMPATIBLE).add(Enchantments.PIERCING, ModEnchantments.ARROW_FRUGALITY, Enchantments.QUICK_CHARGE, ModEnchantments.PRECISION);
        this.tag(ModEnchantmentTags.DOUBLE_HUNTER_CROSSBOW_COMPATIBLE).add(Enchantments.PIERCING, ModEnchantments.ARROW_FRUGALITY, Enchantments.QUICK_CHARGE, ModEnchantments.PRECISION);
        this.tag(ModEnchantmentTags.SEMI_AUTOMATIC_HUNTER_CROSSBOW_COMPATIBLE).add(Enchantments.PIERCING, ModEnchantments.ARROW_FRUGALITY);
    }
}
