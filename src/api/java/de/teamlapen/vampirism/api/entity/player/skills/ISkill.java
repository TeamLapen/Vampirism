package de.teamlapen.vampirism.api.entity.player.skills;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;

/**
 * Skill that can be unlocked
 */
public interface ISkill extends IForgeRegistryEntry<ISkill> {
    /**
     * The description for this skill. Can be null
     */
    @OnlyIn(Dist.CLIENT)
    ITextComponent getDescription();

    /**
     * @return The faction this skill belongs to
     */
    @Nonnull
    IPlayableFaction getFaction();

    /**
     * Can return null if not registered, but since this has to be registered, we don't want annoying null warnings everywhere
     */
    @Override
    ResourceLocation getRegistryName();

    @OnlyIn(Dist.CLIENT)
    int getRenderColumn();

    @OnlyIn(Dist.CLIENT)
    int getRenderRow();

    default ITextComponent getName() {
        return new TranslationTextComponent(getTranslationKey());
    }

    /**
     * Use {@link ISkill#getName()}
     */
    @Deprecated
    String getTranslationKey();

    /**
     * Called when the skill is disenabled (Server: on load from nbt/on disabling all skills e.g. via the gui. Client: on update from server)
     *
     * @param player Must be of the type that {@link ISkill#getFaction()} belongs to
     */
    void onDisable(IFactionPlayer player);

    /**
     * Called when the skill is enabled (Server: on load from nbt/on enabling it via the gui. Client: on update from server)
     *
     * @param player Must be of the type that {@link ISkill#getFaction()} belongs to
     */
    void onEnable(IFactionPlayer player);

    /**
     * Save this. It's required for rendering
     *
     * @param row
     * @param column
     */
    void setRenderPos(int row, int column);
}
