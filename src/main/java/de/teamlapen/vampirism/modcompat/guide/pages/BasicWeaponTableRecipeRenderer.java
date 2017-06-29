package de.teamlapen.vampirism.modcompat.guide.pages;

import amerifrance.guideapi.api.IRecipeRenderer;
import amerifrance.guideapi.api.impl.Book;
import amerifrance.guideapi.api.impl.abstraction.CategoryAbstract;
import amerifrance.guideapi.api.impl.abstraction.EntryAbstract;
import amerifrance.guideapi.api.util.GuiHelper;
import amerifrance.guideapi.gui.GuiBase;
import de.teamlapen.lib.lib.util.ItemStackUtil;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.IHunterWeaponRecipe;
import de.teamlapen.vampirism.core.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import java.awt.*;
import java.util.Random;


/**
 * Renders the basic grid for a weapon table recipe
 *
 * @param <T>
 */
public abstract class BasicWeaponTableRecipeRenderer<T extends IHunterWeaponRecipe> extends IRecipeRenderer.RecipeRendererBase<T> {
    private long lastCycle = -1;
    private int cycleIdx = 0;
    private Random rand = new Random();

    public BasicWeaponTableRecipeRenderer(T recipe) {
        super(recipe);
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

        Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("vampirismguide", "textures/gui/weapon_table_recipe.png"));
        guiBase.drawTexturedModalRect(guiLeft + 42, guiTop + 43, 0, 0, 110, 75);
        guiBase.drawCenteredString(fontRendererObj, ModBlocks.weapon_table.getLocalizedName(), guiLeft + guiBase.xSize / 2, guiTop + 12, 0);
        guiBase.drawCenteredString(fontRendererObj, "§o" + getRecipeName() + "§r", guiLeft + guiBase.xSize / 2, guiTop + 14 + fontRendererObj.FONT_HEIGHT, 0);

        int outputX = 3 + (6 * 17) + (guiLeft + guiBase.xSize / 7);
        int outputY = (2 * 17) + (guiTop + guiBase.xSize / 5);

        ItemStack stack = recipe.getRecipeOutput();

        if (!ItemStackUtil.isEmpty(stack) && stack.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
            NonNullList<ItemStack> subItems = NonNullList.create();
            stack.getItem().getSubItems( stack.getItem().getCreativeTab(), subItems);
            stack = subItems.get(getRandomizedCycle(0, subItems.size()));
        }

        GuiHelper.drawItemStack(stack, outputX, outputY);
        if (GuiHelper.isMouseBetween(mouseX, mouseY, outputX, outputY, 15, 15)) {
            tooltips = GuiHelper.getTooltip(recipe.getRecipeOutput());
        }

        if (recipe.getRequiredLavaUnits() > 0) {
            GuiHelper.drawItemStack(new ItemStack(Items.LAVA_BUCKET), outputX - 16, outputY + 21);
        }

        int y = guiTop + 120;
        if (recipe.getMinHunterLevel() > 1) {
            String level = UtilLib.translateFormatted("gui.vampirism.hunter_weapon_table.level", recipe.getMinHunterLevel());
            guiBase.drawString(fontRendererObj, level, guiLeft + 40, y, Color.gray.getRGB());
            y += fontRendererObj.FONT_HEIGHT + 2;
        }
        if (recipe.getRequiredSkills().length > 0) {
            String skills = "";
            for (ISkill<IHunterPlayer> skill : recipe.getRequiredSkills()) {
                skills += "\n§o" + UtilLib.translate(skill.getUnlocalizedName()) + "§r ";

            }
            String skillText = UtilLib.translateFormatted("gui.vampirism.hunter_weapon_table.skill", skills);
            guiBase.drawSplitString(skillText, guiLeft + 40, y, 110, Color.gray.getRGB());
        }
    }

    /**
     * Draws the given stack and mouse tooltip
     *
     * @param stack Can be empty (1.11) or null (1.10)
     */
    @SideOnly(Side.CLIENT)
    protected void drawStack(ItemStack stack, int index, int stackX, int stackY, int mouseX, int mouseY) {
        if (!ItemStackUtil.isEmpty(stack)) {
            if (stack.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
                NonNullList<ItemStack> subItems = NonNullList.create();
                stack.getItem().getSubItems( stack.getItem().getCreativeTab(), subItems);
                stack = subItems.get(getRandomizedCycle(index, subItems.size()));
            }
            GuiHelper.drawItemStack(stack, stackX, stackY);
            if (GuiHelper.isMouseBetween(mouseX, mouseY, stackX, stackY, 15, 15)) {
                tooltips = GuiHelper.getTooltip(stack);
            }
        }
    }

    protected int getRandomizedCycle(int index, int max) {
        rand.setSeed(index);
        return (index + rand.nextInt(max) + cycleIdx) % max;
    }

    protected String getRecipeName() {
        return UtilLib.translate("text.shaped.crafting");
    }

}
