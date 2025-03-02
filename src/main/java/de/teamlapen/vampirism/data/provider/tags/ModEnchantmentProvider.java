package de.teamlapen.vampirism.data.provider.tags;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.core.ModEnchantments;
import de.teamlapen.vampirism.core.tags.ModEnchantmentTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModEnchantmentProvider extends TagsProvider<Enchantment> {

    public ModEnchantmentProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, Registries.ENCHANTMENT, provider, REFERENCE.MODID);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider pProvider) {
        this.tag(Tags.Enchantments.WEAPON_DAMAGE_ENHANCEMENTS).add(ModEnchantments.VAMPIRE_SLAYER);
        this.tag(ModEnchantmentTags.CROSSBOW_INCOMPATIBLE).add(Enchantments.PIERCING);
        this.tag(ModEnchantmentTags.HUNTER_CROSSBOW_COMPATIBLE).add(Enchantments.PIERCING, Enchantments.POWER);
        this.tag(ModEnchantmentTags.SINGLE_HUNTER_CROSSBOW_COMPATIBLE).addTag(ModEnchantmentTags.HUNTER_CROSSBOW_COMPATIBLE).add(Enchantments.QUICK_CHARGE);
        this.tag(ModEnchantmentTags.DOUBLE_HUNTER_CROSSBOW_COMPATIBLE).addTag(ModEnchantmentTags.HUNTER_CROSSBOW_COMPATIBLE).add(Enchantments.QUICK_CHARGE);
        this.tag(ModEnchantmentTags.SEMI_AUTOMATIC_HUNTER_CROSSBOW_COMPATIBLE).addTag(ModEnchantmentTags.HUNTER_CROSSBOW_COMPATIBLE);
    }
}
