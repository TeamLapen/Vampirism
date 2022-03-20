package de.teamlapen.vampirism.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.util.VampireBookManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.ChangePageButton;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class VampireBookScreen extends Screen {

    private final static ResourceLocation pageTexture = new ResourceLocation(REFERENCE.MODID, "textures/gui/vampire_book.png");
    private final int xSize = 245;
    private final int ySize = 192;
    private int guiLeft, guiTop;
    private ChangePageButton buttonNext;
    private ChangePageButton buttonPrev;
    private int pageNumber;
    private final VampireBookManager.BookInfo info;
    private List<ITextProperties> content;


    public VampireBookScreen(VampireBookManager.BookInfo info) {
        super(new StringTextComponent(info.getTitle()));
        this.info = info;
    }


    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        minecraft.getTextureManager().bind(pageTexture);
        blit(stack, guiLeft, guiTop, 0, 0, xSize, ySize);

        pageNumber = MathHelper.clamp(pageNumber, 0, content.size() - 1);

        if (pageNumber < content.size()) {
            ITextProperties toDraw = content.get(pageNumber);
            FontRenderer fontRenderer = Minecraft.getInstance().font;

            List<IReorderingProcessor> cutLines = fontRenderer.split(toDraw, 170);
            int y = guiTop + 12 + 5;
            for (IReorderingProcessor cut : cutLines) {
                fontRenderer.draw(stack, cut, guiLeft + 44,  y , 0);
                y += 10;
            }
        }

        drawCenteredStringWithoutShadow(stack, font, String.format("%d/%d", pageNumber + 1, content.size()), guiLeft + xSize / 2, guiTop + 5 * ySize / 6, 0);
        drawCenteredString(stack, font, title, guiLeft + xSize / 2, guiTop - 10, Color.WHITE.getRGB());

        buttonPrev.visible = pageNumber != 0;
        buttonNext.visible = pageNumber != content.size() - 1 && !content.isEmpty();

        super.render(stack, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void init() {
        super.init();

        guiLeft = (this.width - this.xSize) / 2;
        guiTop = (this.height - this.ySize) / 2;

        addButton(buttonNext = new ChangePageButton(guiLeft + 4 * xSize / 6, guiTop + 5 * ySize / 6, true,(btn) -> {
            if (pageNumber + 1 < content.size()) {
                nextPage();
            }
        }, true));
        addButton(buttonPrev = new ChangePageButton(guiLeft + xSize / 5, guiTop + 5 * ySize / 6, false, (btn) -> {
            if (pageNumber > 0) {
                prevPage();
            }
        }, true));

        content = Arrays.stream(info.getContent()).map(StringTextComponent::new).flatMap(v -> prepareForLongText(v, 164, 120, 120).stream()).collect(Collectors.toList());

    }

    public static void drawCenteredStringWithoutShadow(MatrixStack p_238471_0_, FontRenderer p_238471_1_, String p_238471_2_, int p_238471_3_, int p_238471_4_, int p_238471_5_) {
        p_238471_1_.draw(p_238471_0_, p_238471_2_, (float) (p_238471_3_ - p_238471_1_.width(p_238471_2_) / 2), (float) p_238471_4_, p_238471_5_);
    }

    @Override
    public boolean keyPressed(int keyCode, int p_keyPressed_2_, int p_keyPressed_3_) {
        if (keyCode == GLFW.GLFW_KEY_BACKSPACE || keyCode == this.minecraft.options.keyUse.getKey().getValue()) {
            this.minecraft.setScreen(null);
            return true;
        } else if ((keyCode == GLFW.GLFW_KEY_UP || keyCode == GLFW.GLFW_KEY_RIGHT) && pageNumber + 1 < content.size()) {
            nextPage();
            return true;
        } else if ((keyCode == GLFW.GLFW_KEY_DOWN || keyCode == GLFW.GLFW_KEY_LEFT) && pageNumber > 0) {
            prevPage();
            return true;
        }
        return super.keyPressed(keyCode, p_keyPressed_2_, p_keyPressed_3_);

    }

    @Override
    public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double movement) {

        if (movement < 0)
            nextPage();
        else if (movement > 0)
            prevPage();


        return movement != 0 || super.mouseScrolled(p_mouseScrolled_1_, p_mouseScrolled_3_, movement);

    }

    public void nextPage() {
        if (pageNumber != content.size() - 1 && !content.isEmpty())
            pageNumber++;
    }

    public void prevPage() {
        if (pageNumber != 0)
            pageNumber--;
    }

    /**
     * Copied from {@link de.maxanier.guideapi.api.util.PageHelper}
     * Split the given text into multiple section if it does not fit one page.
     * The first page can have a different number of lines than the subsequent ones if desired
     * Insert new line characters to wrap the text to the available line width.
     *
     * @param text             Text component to process
     * @param lineWidth        Available width (pixel)
     * @param firstHeight      Available height on the first page (pixel)
     * @param subsequentHeight Available height on subsequent pages (pixel)
     * @return Each list element should be drawn on a individual page. Lines are wrapped using '\n'
     */
    private static List<ITextProperties> prepareForLongText(ITextProperties text, int lineWidth, int firstHeight, int subsequentHeight) {
        FontRenderer fontRenderer = Minecraft.getInstance().font;
        int firstCount = firstHeight / fontRenderer.lineHeight;
        int count = subsequentHeight / fontRenderer.lineHeight;
        List<ITextProperties> lines = new ArrayList<>(fontRenderer.getSplitter().splitLines(text, lineWidth, Style.EMPTY));
        List<ITextProperties> pages = new ArrayList<>();

        List<ITextProperties> pageLines = lines.size() > firstCount ? lines.subList(0, firstCount) : lines;
        pages.add(combineWithNewLine(pageLines));
        pageLines.clear();
        while (lines.size() > 0) {
            pageLines = lines.size() > count ? lines.subList(0, count) : lines;
            pages.add(combineWithNewLine(pageLines));
            pageLines.clear();
        }
        return pages;
    }

    /**
     * Copied from {@link de.maxanier.guideapi.api.util.PageHelper}
     * @param elements The list ist not used itself, but the elements are passed to the new ITextProperties
     * @return a new ITextProperties that combines the given elements with a newline in between
     */
    private static ITextProperties combineWithNewLine(List<ITextProperties> elements) {
        ITextProperties newLine = new StringTextComponent("\n");
        List<ITextProperties> copy = new ArrayList<>(elements.size() * 2);
        for (int i = 0; i < elements.size() - 1; i++) {
            copy.add(elements.get(i));
            copy.add(newLine);
        }
        copy.add(elements.get(elements.size() - 1));
        return ITextProperties.composite(copy);
    }

}
