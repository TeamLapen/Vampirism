package de.teamlapen.lib.lib.util;

import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public abstract class ModDisplayItemGenerator implements CreativeModeTab.DisplayItemsGenerator {
    protected CreativeModeTab.Output output;
    @SuppressWarnings("FieldCanBeLocal")
    protected FeatureFlagSet featureFlagSet;
    @SuppressWarnings("FieldCanBeLocal")
    protected boolean hasPermission;
    protected final Set<ItemLike> items;

    public ModDisplayItemGenerator(Set<ItemLike> allItems) {
        this.items = allItems;
    }

    @Override
    public void accept(@NotNull FeatureFlagSet featureFlagSet, CreativeModeTab.@NotNull Output output, boolean hasPermission) {
        this.output = output;
        this.featureFlagSet = featureFlagSet;
        this.hasPermission = hasPermission;

        this.addItemsToOutput();
        this.items.forEach(output::accept);
    }

    protected abstract void addItemsToOutput();

    protected void add(ItemLike item) {
        this.items.remove(item);
        output.accept(item);
    }

    protected void add(ItemStack item) {
        this.items.remove(item.getItem());
        output.accept(item);
    }

    protected void addItem(RegistryObject<? extends Item> item) {
        add(item.get());
    }

    protected void addBlock(RegistryObject<? extends Block> item) {
        add(item.get());
    }

    protected  <T extends ItemLike & CreativeTabItemProvider> void addGen(RegistryObject<T> item) {
        this.items.remove(item.get());
        item.get().generateCreativeTab(featureFlagSet, output, hasPermission);
    }

    public interface CreativeTabItemProvider {
        void generateCreativeTab(FeatureFlagSet featureFlagSet, CreativeModeTab.Output output, boolean hasPermission);
    }
}
