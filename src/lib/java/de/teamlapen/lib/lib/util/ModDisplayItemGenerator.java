package de.teamlapen.lib.lib.util;

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
        item.get().generateCreativeTab(this.parameters, this.output);
    }

    public interface CreativeTabItemProvider {
        void generateCreativeTab(CreativeModeTab.@NotNull ItemDisplayParameters parameters, CreativeModeTab.Output output);
    }
}
