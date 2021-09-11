package de.teamlapen.lib.lib.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;

public abstract class BaseItemModelGenerator extends ItemModelProvider {

    public BaseItemModelGenerator(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
        super(generator, modid, existingFileHelper);
    }

    /**
     * create {@link ItemModelBuilder} from the block with default texture
     * @param name the block for the {@link ItemModelBuilder}
     */
    @SuppressWarnings("ConstantConditions")
    public ItemModelBuilder block(Block name) {
        return block(name, name.getRegistryName().getPath());
    }

    /**
     * create {@link ItemModelBuilder} from the block with texture
     * @param name the block for the {@link ItemModelBuilder}
     * @param path the block path in the {@code assets/vampirism/texture/block} folder
     */
    @SuppressWarnings("ConstantConditions")
    public ItemModelBuilder block(Block name, String path) {
        try {
            return super.withExistingParent(name.getRegistryName().getPath(), this.modid + ":block/" + path);
        } catch (IllegalStateException e) {
            return getBuilder(name.getRegistryName().getPath()).parent(new ModelFile.UncheckedModelFile(this.modid + ":block/" + path));
        }
    }

    @Nonnull
    @Override
    public String getName() {
        return this.modid + " Item Models";
    }

    /**
     * create {@link ItemModelBuilder} from item with default texture
     * @param item the item for the {@link ItemModelBuilder}
     */
    @SuppressWarnings("ConstantConditions")
    public ItemModelBuilder item(Item item) {
        return withExistingParent(item, mcLoc("item/generated")).texture("layer0", this.modid + ":item/" + item.getRegistryName().getPath());
    }

    /**
     * create {@link ItemModelBuilder} from item with layered texture
     * @param item the item for the {@link ItemModelBuilder}
     * @param texture all layers for the model
     *
     * @see #item(String, ResourceLocation...)
     */
    @SuppressWarnings("ConstantConditions")
    public ItemModelBuilder item(Item item, ResourceLocation... texture) {
        return item(item.getRegistryName().getPath(), texture);
    }

    /**
     * create {@link ItemModelBuilder} from item with layered texture
     * @param item the item for the {@link ItemModelBuilder}
     * @param texture all layers for the model
     */
    public ItemModelBuilder item(String item, ResourceLocation... texture) {
        ItemModelBuilder model = withExistingParent(item, mcLoc("item/generated"));
        for (int i = 0; i < texture.length; i++) {
            model.texture("layer" + i, texture[i]);
        }
        return model;
    }

    @Nonnull
    public ItemModelBuilder withExistingParent(Item name, Item parent) {
        return this.withExistingParent(name, parent.getRegistryName());
    }

    @SuppressWarnings({"UnusedReturnValue", "ConstantConditions"})
    @Nonnull
    public ItemModelBuilder withExistingParent(Block name, ResourceLocation parent) {
        return super.withExistingParent(name.getRegistryName().getPath(), parent);
    }

    @SuppressWarnings("ConstantConditions")
    @Nonnull
    public ItemModelBuilder withExistingParent(Item name, ResourceLocation parent) {
        return super.withExistingParent(name.getRegistryName().getPath(), parent);
    }
}
