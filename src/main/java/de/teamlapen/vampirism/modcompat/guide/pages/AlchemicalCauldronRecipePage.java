package de.teamlapen.vampirism.modcompat.guide.pages;

import amerifrance.guideapi.api.impl.Book;
import amerifrance.guideapi.api.impl.Page;
import amerifrance.guideapi.api.impl.abstraction.CategoryAbstract;
import amerifrance.guideapi.api.impl.abstraction.EntryAbstract;
import amerifrance.guideapi.api.util.GuiHelper;
import amerifrance.guideapi.gui.GuiBase;
import com.google.common.collect.Lists;
import de.teamlapen.lib.lib.util.ItemStackUtil;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.IAlchemicalCauldronRecipe;
import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import java.awt.*;
import java.util.List;
import java.util.Random;


/**
 * Renders a recipe for the alchemical cauldron
 */
public class AlchemicalCauldronRecipePage extends Page {
    protected final IAlchemicalCauldronRecipe recipe;
    protected List<String> tooltips = Lists.newArrayList();
    private long lastCycle = -1;
    private int cycleIdx = 0;
    private Random rand = new Random();

    public AlchemicalCauldronRecipePage(IAlchemicalCauldronRecipe recipe) {
        this.recipe = recipe;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void draw(Book book, CategoryAbstract category, EntryAbstract entry, int guiLeft, int guiTop, int mouseX, int mouseY, GuiBase guiBase, FontRenderer fontRendererObj) {
        Minecraft mc = Minecraft.getMinecraft();
        long time = mc.world.getTotalWorldTime();
        if (lastCycle < 0 || lastCycle < time - 20) {
            if (lastCycle > 0) {
                cycleIdx++;
                cycleIdx = Math.max(0, cycleIdx);
            }
            lastCycle = mc.world.getTotalWorldTime();
        }

        Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("vampirismguide", "textures/gui/alchemical_cauldron_recipe.png"));
        guiBase.drawTexturedModalRect(guiLeft + 32, guiTop + 43, 0, 0, 110, 75);
        guiBase.drawCenteredString(fontRendererObj, ModBlocks.alchemicalCauldron.getLocalizedName(), guiLeft + guiBase.xSize / 2, guiTop + 12, 0);
        guiBase.drawCenteredString(fontRendererObj, "§o" + getRecipeName() + "§r", guiLeft + guiBase.xSize / 2, guiTop + 14 + fontRendererObj.FONT_HEIGHT, 0);

        int outputX = 95 + (guiLeft + guiBase.xSize / 7);
        int outputY = 34 + (guiTop + guiBase.ySize / 5);
        int in1X = 23 + (guiLeft + guiBase.xSize / 7);
        int in1Y = 22 + (guiTop + guiBase.ySize / 5);
        int in2X = 53 + (guiLeft + guiBase.xSize / 7);
        int in2Y = 22 + (guiTop + guiBase.ySize / 5);

        ItemStack stack = recipe.getOutput();

        if (stack.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
            NonNullList<ItemStack> subItems = NonNullList.create();
            stack.getItem().getSubItems( stack.getItem().getCreativeTab(), subItems);
            stack = subItems.get(getRandomizedCycle(0, subItems.size()));
        }

        GuiHelper.drawItemStack(stack, outputX, outputY);
        if (GuiHelper.isMouseBetween(mouseX, mouseY, outputX, outputY, 15, 15)) {
            tooltips = GuiHelper.getTooltip(recipe.getOutput());
        }

        ItemStack input = recipe.getIngredient();

        if (!ItemStackUtil.isEmpty(input)) {
            if (input.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
                NonNullList<ItemStack> subItems = NonNullList.create();
                input.getItem().getSubItems( input.getItem().getCreativeTab(), subItems);
                input = subItems.get(getRandomizedCycle(0, subItems.size()));
            }
            GuiHelper.drawItemStack(input, in2X, in2Y);
            if (GuiHelper.isMouseBetween(mouseX, mouseY, in2X, in2Y, 15, 15)) {
                tooltips = GuiHelper.getTooltip(input);
            }
        }


        ItemStack liquid = recipe.getDescriptiveFluidStack();

        if (!ItemStackUtil.isEmpty(liquid) && liquid.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
            NonNullList<ItemStack> subItems = NonNullList.create();
            liquid.getItem().getSubItems( liquid.getItem().getCreativeTab(), subItems);
            liquid = subItems.get(getRandomizedCycle(0, subItems.size()));
        }

        GuiHelper.drawItemStack(liquid, in1X, in1Y);
        if (GuiHelper.isMouseBetween(mouseX, mouseY, in1X, in1Y, 15, 15)) {
            tooltips = GuiHelper.getTooltip(liquid);
        }


        int y = guiTop + 120;
        if (recipe.getRequiredLevel() > 1) {
            String level = UtilLib.translateFormatted("gui.vampirism.hunter_weapon_table.level", recipe.getRequiredLevel());
            guiBase.drawString(fontRendererObj, level, guiLeft + 50, y, Color.gray.getRGB());
            y += fontRendererObj.FONT_HEIGHT + 2;
        }
        if (recipe.getRequiredSkills().length > 0) {
            String skills = "";
            for (ISkill<IHunterPlayer> skill : recipe.getRequiredSkills()) {
                skills += "\n§o" + UtilLib.translate(skill.getUnlocalizedName()) + "§r ";

            }
            String skillText = UtilLib.translateFormatted("gui.vampirism.hunter_weapon_table.skill", skills);
            guiBase.drawSplitString(skillText, guiLeft + 50, y, 100, Color.gray.getRGB());
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void drawExtras(Book book, CategoryAbstract category, EntryAbstract entry, int guiLeft, int guiTop, int mouseX, int mouseY, GuiBase guiBase, FontRenderer fontRendererObj) {
        guiBase.drawHoveringText(tooltips, mouseX, mouseY);
        tooltips.clear();
    }

    protected int getRandomizedCycle(int index, int max) {
        rand.setSeed(index);
        return (index + rand.nextInt(max) + cycleIdx) % max;
    }

    protected String getRecipeName() {
        return UtilLib.translate("text.shaped.crafting");
    }
}
