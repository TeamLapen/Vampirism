package de.teamlapen.vampirism.entity.minion.management;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.minion.IMinionData;
import de.teamlapen.vampirism.api.entity.minion.IMinionEntity;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.api.entity.player.ILordPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.core.ModAdvancements;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.function.Supplier;


public abstract class DefaultMinionTask<T extends IMinionTask.IMinionTaskDesc<Q>, Q extends IMinionData> extends ForgeRegistryEntry<IMinionTask<?, ?>> implements IMinionTask<T, Q> {

    private ITextComponent name;
    private final Supplier<ISkill> requiredSkill;

    public DefaultMinionTask() {
        this(() -> null);
    }

    public DefaultMinionTask(Supplier<ISkill> requiredSkill) {
        this.requiredSkill = requiredSkill;
    }

    @Nullable
    @Override
    public T activateTask(@Nullable PlayerEntity lord, @Nullable IMinionEntity minion, Q data) {
        triggerAdvancements(lord);
        return null;
    }

    @Override
    public ITextComponent getName() {
        if (name == null) {
            name = new TranslationTextComponent(Util.makeDescriptionId("minion_task", getRegistryName()));
        }
        return name;
    }

    protected void triggerAdvancements(PlayerEntity player) {
        if (player instanceof ServerPlayerEntity) {
            ModAdvancements.TRIGGER_MINION_ACTION.trigger(((ServerPlayerEntity) player), this);
        }
    }

    public boolean isRequiredSkillUnlocked(IPlayableFaction<?> faction, @Nullable ILordPlayer player) {
        return this.requiredSkill.get() == null || player == null || faction.getPlayerCapability(player.getPlayer()).map(a -> a.getSkillHandler().isSkillEnabled(this.requiredSkill.get())).orElse(false);
    }
}
