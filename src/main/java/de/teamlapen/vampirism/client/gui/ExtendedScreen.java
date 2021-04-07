package de.teamlapen.vampirism.client.gui;

import net.minecraft.client.renderer.ItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ExtendedScreen {

    ItemRenderer getItemRenderer();

    TaskContainer getTaskContainer();
}
