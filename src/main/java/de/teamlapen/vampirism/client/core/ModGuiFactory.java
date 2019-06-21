package de.teamlapen.vampirism.client.core;

import de.teamlapen.vampirism.client.gui.ModConfigGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.IModGuiFactory;

import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class ModGuiFactory implements IModGuiFactory {
    @Override
    public GuiScreen createConfigGui(GuiScreen parentScreen) {
        return new ModConfigGui(parentScreen);
    }//TODO Gui

    @Override
    public boolean hasConfigGui() {
        return true;
    }

    @Override
    public void initialize(Minecraft minecraftInstance) {

    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }
}
