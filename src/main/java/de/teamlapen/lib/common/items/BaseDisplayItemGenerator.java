package de.teamlapen.lib.common.items;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

public abstract class BaseDisplayItemGenerator implements CreativeModeTab.DisplayItemsGenerator {
    public CreativeModeTab.Output output;
    public CreativeModeTab.ItemDisplayParameters parameters;

    @Override
    public void accept(CreativeModeTab.@NotNull ItemDisplayParameters parameters, CreativeModeTab.@NotNull Output output) {
        this.output = output;
        this.parameters = parameters;

        this.addAll();
    }

    protected abstract void addAll();

    protected void add(ItemLike item) {
        output.accept(item);
    }

    protected void add(ItemStack item) {
        output.accept(item);
    }

    protected void addIfPresent(ResourceLocation id) {
        Item item = BuiltInRegistries.ITEM.getValue(ResourceKey.create(Registries.ITEM, id));
        if (item != null) add(item);
    }

    protected <T extends Item & CreativeTabItemProvider> void addItemGen(DeferredHolder<Item, T> item) {
        item.get().generateCreativeTab(this.parameters, this.output);
    }

    protected <T extends Block & CreativeTabItemProvider> void addBlockGen(DeferredHolder<Block, T> item) {
        item.get().generateCreativeTab(this.parameters, this.output);
    }

    public interface CreativeTabItemProvider {
        void generateCreativeTab(CreativeModeTab.@NotNull ItemDisplayParameters parameters, CreativeModeTab.Output output);
    }
}
