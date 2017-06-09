package de.teamlapen.vampirism.api.entity.player.skills;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * Skill that can be unlocked
 */
public interface ISkill<T extends ISkillPlayer> {

    /**
     * @return Unique lowercase id
     */
    String getID();

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
     *
     * @return
     */
    @SideOnly(Side.CLIENT)
    String getLocalizedDescription();

    /**
     * Should return the min U texture coordinate within the icon map
     *
     * @return
     */
    @SideOnly(Side.CLIENT)
    int getMinU();

    /**
     * Should return the min V texture coordinate within the icon map
     *
     * @return
     */
    @SideOnly(Side.CLIENT)
    int getMinV();

    @SideOnly(Side.CLIENT)
    int getRenderColumn();

    @SideOnly(Side.CLIENT)
    int getRenderRow();

    String getUnlocalizedName();

    /**
     * Called when the skill is disenabled (Server: on load from nbt/on disabling all skills e.g. via the gui. Client: on update from server)
     *
     * @param player
     */
    void onDisable(T player);

    /**
     * Called when the skill is enabled (Server: on load from nbt/on enabling it via the gui. Client: on update from server)
     *
     * @param player
     */
    void onEnable(T player);

    /**
     * Save this. It's required for rendering
     *
     * @param row
     * @param column
     */
    void setRenderPos(int row, int column);
}
