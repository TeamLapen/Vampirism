package de.teamlapen.vampirism.modcompat.guide.pages;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.maxanier.guideapi.api.SubTexture;
import de.maxanier.guideapi.api.impl.Book;
import de.maxanier.guideapi.api.impl.Page;
import de.maxanier.guideapi.api.impl.abstraction.CategoryAbstract;
import de.maxanier.guideapi.api.impl.abstraction.EntryAbstract;
import de.maxanier.guideapi.api.util.GuiHelper;
import de.maxanier.guideapi.api.util.PageHelper;
import de.maxanier.guideapi.gui.BaseScreen;
import de.teamlapen.vampirism.api.items.ExtendedPotionMix;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;


public class PagePotionTableMix extends Page {
    private final static Logger LOGGER = LogManager.getLogger();
    private static final SubTexture POTION_GRID = new SubTexture(new ResourceLocation("vampirismguide", "textures/gui/potion_table.png"), 0, 0, 89, 75);
    private final ITextComponent description;
    private final ExtendedPotionMix[] recipes;
    private long lastCycle = -1L;
    private ItemStack input;
    private ItemStack output;
    private ItemStack in1;
    private ItemStack in2;
    private int recipeCycle;
    private int currentIngredient1Index;
    @Nonnull
    private ItemStack[] ingredients1 = new ItemStack[0];
    private int currentIngredient2Index;
    @Nonnull
    private ItemStack[] ingredients2 = new ItemStack[0];

    public PagePotionTableMix(ITextComponent description, ExtendedPotionMix... recipes) {
        assert recipes.length > 0;
        this.description = description;
        this.recipes = recipes;
        deriveItemStacks(recipes[0]);
        in1 = ingredients1[0];
        in2 = ingredients2[0];
    }

    public PagePotionTableMix(ITextComponent description, List<ExtendedPotionMix> recipes) {
        this(description, recipes.toArray(new ExtendedPotionMix[0]));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void draw(MatrixStack stack, Book book, CategoryAbstract category, EntryAbstract entry, int guiLeft, int guiTop, int mouseX, int mouseY, BaseScreen guiBase, FontRenderer fontRendererObj) {
        //Update cycle
        long time = guiBase.getMinecraft().world != null ? guiBase.getMinecraft().world.getGameTime() : 0L;
        if (this.lastCycle < 0L || this.lastCycle < time - 60L) {
            if (this.lastCycle > 0L) {
                this.cycle();
            }

            this.lastCycle = time;
        }


        int xStart = guiLeft + guiBase.xSize / 2 - 44;
        int yStart = guiTop + 20;
        POTION_GRID.draw(stack, xStart, yStart);

        List<ITextComponent> tooltip = null;
        int x = xStart + 7;
        int y = yStart + 55;
        GuiHelper.drawItemStack(stack, input, x, y);
        if (GuiHelper.isMouseBetween(mouseX, mouseY, x, y, 15, 15)) tooltip = GuiHelper.getTooltip(input);
        x += 21;
        GuiHelper.drawItemStack(stack, input, x, y);
        if (GuiHelper.isMouseBetween(mouseX, mouseY, x, y, 15, 15)) tooltip = GuiHelper.getTooltip(input);
        x += 21;
        GuiHelper.drawItemStack(stack, input, x, y);
        if (GuiHelper.isMouseBetween(mouseX, mouseY, x, y, 15, 15)) tooltip = GuiHelper.getTooltip(input);
        x = xStart + 29;
        y = yStart + 4;
        GuiHelper.drawItemStack(stack, in1, x, y);
        if (GuiHelper.isMouseBetween(mouseX, mouseY, x, y, 15, 15)) tooltip = GuiHelper.getTooltip(in1);
        x = xStart + 4;
        y = yStart + 12;
        GuiHelper.drawItemStack(stack, in2, x, y);
        if (GuiHelper.isMouseBetween(mouseX, mouseY, x, y, 15, 15)) tooltip = GuiHelper.getTooltip(in2);
        x = xStart + 71;
        y = yStart + 29;
        GuiHelper.drawItemStack(stack, output, x, y);
        if (GuiHelper.isMouseBetween(mouseX, mouseY, x, y, 15, 15)) tooltip = GuiHelper.getTooltip(output);
        PageHelper.drawFormattedText(stack, guiLeft + 43, yStart + 80, guiBase, description);

        if (tooltip != null) {
            guiBase.func_243308_b(stack, tooltip, mouseX, mouseY);
        }

    }

    private void cycle() {
        if (++currentIngredient2Index >= ingredients2.length) {
            currentIngredient2Index = 0;
            if (++currentIngredient1Index >= ingredients1.length) {
                currentIngredient1Index = 0;
                if (++recipeCycle >= recipes.length) {
                    this.recipeCycle = 0;
                }
                deriveItemStacks(recipes[recipeCycle]);

            }
        }
        in1 = ingredients1[currentIngredient1Index];
        in2 = ingredients2[currentIngredient2Index];
    }

    private void deriveItemStacks(ExtendedPotionMix recipe) {
        input = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), recipe.input.get());
        output = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), recipe.output.get());
        ingredients1 = recipe.reagent1.map(Ingredient::getMatchingStacks).map(Arrays::stream).orElse(Stream.empty()).map(ItemStack::copy).peek(stack -> stack.setCount(recipe.reagent1Count)).toArray(ItemStack[]::new);
        ingredients2 = recipe.reagent2.map(Ingredient::getMatchingStacks).map(Arrays::stream).orElse(Stream.empty()).map(ItemStack::copy).peek(stack -> stack.setCount(recipe.reagent2Count)).toArray(ItemStack[]::new);
        if (ingredients1.length == 0) {
            ingredients1 = new ItemStack[]{ItemStack.EMPTY};
        }
        if (ingredients2.length == 0) {
            ingredients2 = new ItemStack[]{ItemStack.EMPTY};
        }
    }

}