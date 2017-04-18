package de.teamlapen.vampirism.api.items;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 * Simple interface for items that exist in different tiers.
 * This is usually used for hunter weapons and armor.
 * The tier should be stored in the tag compound.
 * All existent stacks of this item should be of one of the available tiers, if possible
 */
public interface IItemWithTier {

    /**
     * @return The tier of the item stack
     */
    TIER getTier(@Nonnull ItemStack stack);

    /**
     * Set's the tier of the given item stack
     *
     * @return the same stack for chaining
     */
    @Nonnull
    ItemStack setTier(@Nonnull ItemStack stack, TIER tier);

    enum TIER implements IStringSerializable {
        NORMAL, ENHANCED, ULTIMATE;


        @Override
        public String getName() {
            return name().toLowerCase();
        }
    }

    /**
     * Simple model location mapper.
     * ONLY for IItemWithTier items
     * Variant: "tier=<tier>"
     */
    @SideOnly(Side.CLIENT)
    class SimpleMeshDefinition implements ItemMeshDefinition {

        private final ResourceLocation base;

        public SimpleMeshDefinition(ResourceLocation base) {
            this.base = base;
        }

        @Nonnull
        @Override
        public ModelResourceLocation getModelLocation(@Nonnull ItemStack stack) {
            return new ModelResourceLocation(base, "tier=" + ((IItemWithTier) stack.getItem()).getTier(stack));
        }
    }

    /**
     * Model location mapper for armor
     * ONLY for IItemWithTier items that extend ItemArmor
     * Variant: "tier=<tier>,part=<part>"
     */
    @SideOnly(Side.CLIENT)
    class ArmorMeshDefinition implements ItemMeshDefinition {
        private final ResourceLocation base;

        public ArmorMeshDefinition(ResourceLocation base) {
            this.base = base;
        }

        @Nonnull
        @Override
        public ModelResourceLocation getModelLocation(@Nonnull ItemStack stack) {
            String tier = ((IItemWithTier) stack.getItem()).getTier(stack).getName();
            String part = ((ItemArmor) stack.getItem()).armorType.getName();
            return new ModelResourceLocation(base, "part=" + part + "_" + tier);
        }
    }
}
