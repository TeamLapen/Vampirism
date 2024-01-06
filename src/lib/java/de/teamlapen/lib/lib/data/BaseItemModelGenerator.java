package de.teamlapen.lib.lib.data;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

public abstract class BaseItemModelGenerator extends ItemModelProvider {

    public BaseItemModelGenerator(@NotNull PackOutput packOutput, @NotNull String modid, @NotNull ExistingFileHelper existingFileHelper) {
        super(packOutput, modid, existingFileHelper);
    }

    /**
     * create {@link ItemModelBuilder} from the block with default texture
     *
     * @param name the block for the {@link ItemModelBuilder}
     */
    @SuppressWarnings("ConstantConditions")
    public ItemModelBuilder block(Block name) {
        return block(name, BuiltInRegistries.BLOCK.getKey(name).getPath());
    }

    /**
     * create {@link ItemModelBuilder} from the block with texture
     *
     * @param name the block for the {@link ItemModelBuilder}
     * @param path the block path in the {@code assets/vampirism/texture/block} folder
     */
    @SuppressWarnings("ConstantConditions")
    public ItemModelBuilder block(Block name, String path) {
        try {
            return super.withExistingParent(BuiltInRegistries.BLOCK.getKey(name).getPath(), this.modid + ":block/" + path);
        } catch (IllegalStateException e) {
            return getBuilder(BuiltInRegistries.BLOCK.getKey(name).getPath()).parent(new ModelFile.UncheckedModelFile(this.modid + ":block/" + path));
        }
    }

    @NotNull
    @Override
    public String getName() {
        return this.modid + " Item Models";
    }

    /**
     * create {@link ItemModelBuilder} from item with default texture
     *
     * @param item the item for the {@link ItemModelBuilder}
     */
    @SuppressWarnings("ConstantConditions")
    public ItemModelBuilder item(Item item) {
        return withExistingParent(item, mcLoc("item/generated")).texture("layer0", this.modid + ":item/" + BuiltInRegistries.ITEM.getKey(item).getPath());
    }

    /**
     * create {@link ItemModelBuilder} from item with layered texture
     *
     * @param item    the item for the {@link ItemModelBuilder}
     * @param texture all layers for the model
     * @see #item(String, ResourceLocation...)
     */
    @SuppressWarnings("ConstantConditions")
    public ItemModelBuilder item(Item item, ResourceLocation... texture) {
        return item(BuiltInRegistries.ITEM.getKey(item).getPath(), texture);
    }

    /**
     * create {@link ItemModelBuilder} from item with layered texture
     *
     * @param item    the item for the {@link ItemModelBuilder}
     * @param texture all layers for the model
     */
    public ItemModelBuilder item(String item, ResourceLocation @NotNull ... texture) {
        ItemModelBuilder model = withExistingParent(item, mcLoc("item/generated"));
        for (int i = 0; i < texture.length; i++) {
            model.texture("layer" + i, texture[i]);
        }
        return model;
    }

    @NotNull
    public ItemModelBuilder withExistingParent(Item name, Item parent) {
        return this.withExistingParent(name, BuiltInRegistries.ITEM.getKey(parent));
    }

    @SuppressWarnings({"UnusedReturnValue", "ConstantConditions"})
    @NotNull
    public ItemModelBuilder withExistingParent(Block name, ResourceLocation parent) {
        return super.withExistingParent(BuiltInRegistries.BLOCK.getKey(name).getPath(), parent);
    }

    @SuppressWarnings("ConstantConditions")
    @NotNull
    public ItemModelBuilder withExistingParent(Item name, ResourceLocation parent) {
        return super.withExistingParent(BuiltInRegistries.ITEM.getKey(name).getPath(), parent);
    }
}
