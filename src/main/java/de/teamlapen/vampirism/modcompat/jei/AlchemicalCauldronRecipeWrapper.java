//package de.teamlapen.vampirism.modcompat.jei;
//
//import com.google.common.collect.Lists;
//import de.teamlapen.lib.lib.util.UtilLib;
//import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
//import de.teamlapen.vampirism.api.items.IAlchemicalCauldronRecipe;
//import mezz.jei.api.ingredients.IIngredients;
//import mezz.jei.api.recipe.BlankRecipeWrapper;
//import mezz.jei.api.recipe.IStackHelper;
//import net.minecraft.client.Minecraft;
//import net.minecraft.item.ItemStack;
//import net.minecraftforge.fml.relauncher.Side;
//import net.minecraftforge.fml.relauncher.SideOnly;
//
//import java.awt.*;
//import java.util.Collections;
//import java.util.List;
//
//
//public class AlchemicalCauldronRecipeWrapper extends BlankRecipeWrapper {
//    private final List<List<ItemStack>> inputs;
//    private final ItemStack output;
//    private final IAlchemicalCauldronRecipe recipe;
//
//    public AlchemicalCauldronRecipeWrapper(IAlchemicalCauldronRecipe recipe, IStackHelper stackHelper) {
//        ItemStack ingred = recipe.getIngredient();
//        inputs = Lists.newLinkedList();
//        inputs.add(Collections.singletonList(recipe.getDescriptiveFluidStack()));
//        inputs.add(ingred.isEmpty() ? Collections.emptyList() : stackHelper.getSubtypes(ingred));
//        this.recipe = recipe;
//        this.output = recipe.getOutput();
//    }
//
//    @SideOnly(Side.CLIENT)
//    @Override
//    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
//        int x = 0;
//        int y = 75;
//        if (recipe.getRequiredLevel() > 1) {
//            String level = UtilLib.translateFormatted("gui.vampirism.alchemical_cauldron.level", recipe.getRequiredLevel());
//
//            minecraft.fontRenderer.drawString(level, x, y, Color.gray.getRGB());
//            y += minecraft.fontRenderer.FONT_HEIGHT + 2;
//        }
//        if (recipe.getRequiredSkills().length > 0) {
//            StringBuilder skills = new StringBuilder();
//            for (ISkill skill : recipe.getRequiredSkills()) {
//                skills.append(UtilLib.translate(skill.getUnlocalizedName())).append(" ");
//
//            }
//            String skillText = UtilLib.translateFormatted("gui.vampirism.alchemical_cauldron.skill", skills.toString());
//            minecraft.fontRenderer.drawSplitString(skillText, x, y, 132, Color.gray.getRGB());
//
//
//        }
//    }
//
//    @Override
//    public void getIngredients(IIngredients ingredients) {
//        ingredients.setInputLists(ItemStack.class, inputs);
//        ingredients.setOutput(ItemStack.class, output);
//    }
//}
