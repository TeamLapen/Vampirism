package de.teamlapen.vampirism.api.entity;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public interface ITaskMaster {
    ITextComponent CONTAINERNAME = new TranslationTextComponent("container.vampirism.taskmaster");
    ITextComponent NOTASK = new TranslationTextComponent("text.vampirism.taskmaster.no_tasks");

}
