package de.teamlapen.vampirism.api.entity.player.skills;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Skill that can be unlocked
 */
public interface ISkill extends IForgeRegistryEntry<ISkill> {
    /**
     * @return The faction this skill belongs to
     */
    @Nonnull
    IPlayableFaction getFaction();

    /**
     * Should return the location of the icon map where the icon is in
     * Texture must be 256x80
     *
     * @return null to use vampirism's default one
     */
    @SideOnly(Side.CLIENT)
    @Nullable
    ResourceLocation getIconLoc();

    /**
     * The description for this skill. Can be null
     */
    @SideOnly(Side.CLIENT)
    String getLocalizedDescription();

    /**
     * Should return the min U texture coordinate within the icon map
     */
    @SideOnly(Side.CLIENT)
    int getMinU();

    /**
     * Should return the min V texture coordinate within the icon map
     */
    @SideOnly(Side.CLIENT)
    int getMinV();

    /**
     * Can return null if not registered, but since this has to be registered, we don't want annoying null warnings everywhere
     *
     * @return
     */
    @Nonnull
    @Override
    ResourceLocation getRegistryName();

    @SideOnly(Side.CLIENT)
    int getRenderColumn();

    @SideOnly(Side.CLIENT)
    int getRenderRow();

    String getUnlocalizedName();

    /**
     * Called when the skill is disenabled (Server: on load from nbt/on disabling all skills e.g. via the gui. Client: on update from server)
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
