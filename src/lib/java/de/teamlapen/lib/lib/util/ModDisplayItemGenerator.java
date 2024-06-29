package de.teamlapen.lib.lib.util;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public abstract class ModDisplayItemGenerator implements CreativeModeTab.DisplayItemsGenerator {
    protected CreativeModeTab.Output output;
    @SuppressWarnings("FieldCanBeLocal")
    protected CreativeModeTab.ItemDisplayParameters parameters;
    protected final Set<ItemLike> items;

    public ModDisplayItemGenerator(Set<ItemLike> allItems) {
        this.items = allItems;
    }

    @Override
    public void accept(CreativeModeTab.@NotNull ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        this.output = output;
        this.parameters = parameters;

        this.addItemsToOutput();
        this.items.forEach(output::accept);
    }

    protected abstract void addItemsToOutput();

    protected void add(ItemLike item) {
        this.items.remove(item.asItem());
        output.accept(item);
    }

    protected void add(ItemStack item) {
        this.items.remove(item.getItem());
        output.accept(item);
    }

    protected void addItem(DeferredHolder<Item, ? extends Item> item) {
        add(item.get());
    }

    protected void addBlock(DeferredHolder<Block, ? extends Block> item) {
        add(item.get());
    }

    protected <T extends Item & CreativeTabItemProvider> void addItemGen(DeferredHolder<Item, T> item) {
        this.items.remove(item.get());
        item.get().generateCreativeTab(this.parameters, this.output);
    }

    protected <T extends Block & CreativeTabItemProvider> void addBlockGen(DeferredHolder<Block, T> item) {
        this.items.remove(item.get().asItem());
        item.get().generateCreativeTab(this.parameters, this.output);
    }

    public interface CreativeTabItemProvider {
        void generateCreativeTab(CreativeModeTab.@NotNull ItemDisplayParameters parameters, CreativeModeTab.Output output);
    }
}
