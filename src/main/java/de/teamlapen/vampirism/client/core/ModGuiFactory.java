package de.teamlapen.vampirism.client.core;

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
        return null;
    }

    @Override
    public boolean hasConfigGui() {
        return false;
    }

    @Override
    public void initialize(Minecraft minecraftInstance) {

    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }
}
