package de.teamlapen.vampirism.entity.minion.management;

import de.teamlapen.vampirism.api.entity.minion.IMinionData;
import de.teamlapen.vampirism.api.entity.minion.IMinionEntity;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.core.ModAdvancements;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;


public abstract class DefaultMinionTask<T extends IMinionTask.IMinionTaskDesc<Q>, Q extends IMinionData> extends ForgeRegistryEntry<IMinionTask<?, ?>> implements IMinionTask<T, Q> {
    private ITextComponent name;

    @Nullable
    @Override
    public T activateTask(@Nullable PlayerEntity lord, @Nullable IMinionEntity minion, Q data) {
        triggerAdvancements(lord);
        return null;
    }

    protected void triggerAdvancements(PlayerEntity player){
        if (player instanceof ServerPlayerEntity) {
            ModAdvancements.TRIGGER_MINION_ACTION.trigger(((ServerPlayerEntity) player),this);
        }
    }

    @Override
    public ITextComponent getName() {
        if (name == null) {
            name = new TranslationTextComponent(Util.makeTranslationKey("minion_task", getRegistryName()));
        }
        return name;
    }
}
