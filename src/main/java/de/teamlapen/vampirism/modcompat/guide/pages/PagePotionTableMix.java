package de.teamlapen.vampirism.modcompat.guide.pages;

import de.maxanier.guideapi.api.SubTexture;
import de.maxanier.guideapi.api.impl.Book;
import de.maxanier.guideapi.api.impl.Page;
import de.maxanier.guideapi.api.impl.abstraction.CategoryAbstract;
import de.maxanier.guideapi.api.impl.abstraction.EntryAbstract;
import de.maxanier.guideapi.api.util.GuiHelper;
import de.maxanier.guideapi.api.util.PageHelper;
import de.maxanier.guideapi.gui.BaseScreen;
import de.teamlapen.vampirism.api.items.ExtendedPotionMix;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;


public class PagePotionTableMix extends Page {
    private final static Logger LOGGER = LogManager.getLogger();
    private static final SubTexture POTION_GRID = new SubTexture(new ResourceLocation("vampirismguide", "textures/gui/container/potion_table.png"), 0, 0, 89, 75);
    private final Component description;
    private final ExtendedPotionMix @NotNull [] recipes;
    private long lastCycle = -1L;
    private ItemStack input;
    private ItemStack output;
    private ItemStack in1;
    private ItemStack in2;
    private int recipeCycle;
    private int currentIngredient1Index;
    @NotNull
    private ItemStack @NotNull [] ingredients1 = new ItemStack[0];
    private int currentIngredient2Index;
    @NotNull
    private ItemStack @NotNull [] ingredients2 = new ItemStack[0];

    public PagePotionTableMix(Component description, ExtendedPotionMix @NotNull ... recipes) {
        assert recipes.length > 0;
        this.description = description;
        this.recipes = recipes;
        deriveItemStacks(recipes[0]);
        in1 = ingredients1[0];
        in2 = ingredients2[0];
    }

    public PagePotionTableMix(Component description, @NotNull List<ExtendedPotionMix> recipes) {
        this(description, recipes.toArray(new ExtendedPotionMix[0]));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void draw(@NotNull GuiGraphics guiGraphics, RegistryAccess registryAccess, Book book, CategoryAbstract category, EntryAbstract entry, int guiLeft, int guiTop, int mouseX, int mouseY, @NotNull BaseScreen guiBase, Font font) {
        //Update cycle
        long time = guiBase.getMinecraft().level != null ? guiBase.getMinecraft().level.getGameTime() : 0L;
        if (this.lastCycle < 0L || this.lastCycle < time - 60L) {
            if (this.lastCycle > 0L) {
                this.cycle();
            }

            this.lastCycle = time;
        }


        int xStart = guiLeft + guiBase.xSize / 2 - 44;
        int yStart = guiTop + 20;
        POTION_GRID.draw(guiGraphics, xStart, yStart);

        List<Component> tooltip = null;
        int x = xStart + 7;
        int y = yStart + 55;
        GuiHelper.drawItemStack(guiGraphics, input, x, y);
        if (GuiHelper.isMouseBetween(mouseX, mouseY, x, y, 15, 15)) tooltip = GuiHelper.getTooltip(input);
        x += 21;
        GuiHelper.drawItemStack(guiGraphics, input, x, y);
        if (GuiHelper.isMouseBetween(mouseX, mouseY, x, y, 15, 15)) tooltip = GuiHelper.getTooltip(input);
        x += 21;
        GuiHelper.drawItemStack(guiGraphics, input, x, y);
        if (GuiHelper.isMouseBetween(mouseX, mouseY, x, y, 15, 15)) tooltip = GuiHelper.getTooltip(input);
        x = xStart + 29;
        y = yStart + 4;
        GuiHelper.drawItemStack(guiGraphics, in1, x, y);
        if (GuiHelper.isMouseBetween(mouseX, mouseY, x, y, 15, 15)) tooltip = GuiHelper.getTooltip(in1);
        x = xStart + 4;
        y = yStart + 12;
        GuiHelper.drawItemStack(guiGraphics, in2, x, y);
        if (GuiHelper.isMouseBetween(mouseX, mouseY, x, y, 15, 15)) tooltip = GuiHelper.getTooltip(in2);
        x = xStart + 71;
        y = yStart + 29;
        GuiHelper.drawItemStack(guiGraphics, output, x, y);
        if (GuiHelper.isMouseBetween(mouseX, mouseY, x, y, 15, 15)) tooltip = GuiHelper.getTooltip(output);
        PageHelper.drawFormattedText(guiGraphics, guiLeft + 43, yStart + 80, guiBase, description);

        if (tooltip != null) {
            guiGraphics.renderComponentTooltip(font, tooltip, mouseX, mouseY);
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

    private void deriveItemStacks(@NotNull ExtendedPotionMix recipe) {
        input = PotionContents.createItemStack(Items.POTION, recipe.input);
        output = PotionContents.createItemStack(Items.POTION, recipe.output);
        ingredients1 = Arrays.stream(recipe.reagent1.get().getItems()).map(ItemStack::copy).peek(stack -> stack.setCount(recipe.reagent1Count)).toArray(ItemStack[]::new);
        ingredients2 =  Arrays.stream(recipe.reagent2.get().getItems()).map(ItemStack::copy).peek(stack -> stack.setCount(recipe.reagent2Count)).toArray(ItemStack[]::new);
        if (ingredients1.length == 0) {
            ingredients1 = new ItemStack[]{ItemStack.EMPTY};
        }
        if (ingredients2.length == 0) {
            ingredients2 = new ItemStack[]{ItemStack.EMPTY};
        }
    }

}