package de.teamlapen.vampirism.client;

import de.teamlapen.vampirism.client.extensions.ItemExtensions;
import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

import java.util.function.Supplier;

public class ModClientEnums {

    public static final EnumProxy<RecipeBookCategories> WEAPON_TABLE_RECIPE_BOOK_CATEGORY = new EnumProxy<>(RecipeBookCategories.class, (Supplier<ItemStack>)() -> new ItemStack(ModBlocks.WEAPON_TABLE));

    public static final EnumProxy<HumanoidModel.ArmPose> DOUBLE_CROSSBOW_CHARGE = new EnumProxy<>(HumanoidModel.ArmPose.class, true, ItemExtensions.DOUBLE_CROSSBOW_CHARGE_ARM_POSE_TRANSFORMER);

    public static final EnumProxy<HumanoidModel.ArmPose> DOUBLE_CROSSBOW_HOLD = new EnumProxy<>(HumanoidModel.ArmPose.class, true, ItemExtensions.DOUBLE_CROSSBOW_HOLD_ARM_POSE_TRANSFORMER);

}
