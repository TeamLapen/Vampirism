package de.teamlapen.vampirism.api.entity.player.actions;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Interface for player actions
 */
public interface IAction<T extends IActionPlayer> {
    /**
     * Checks if the player can use this action
     */
    PERM canUse(T player);

    /**
     * @return Cooldown time in ticks until the action can be used again
     */
    int getCooldown();

    /**
     * Return the faction, which players can use this action
     *
     * @return
     */
    IPlayableFaction<? extends IActionPlayer> getFaction();

    /**
     * Should return the location of the icon map where the icon is in
     * Texture has to be 256x80
     * @return
     */
    @SideOnly(Side.CLIENT)
    ResourceLocation getIconLoc();

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

    String getUnlocalizedName();

    /**
     * Called when the action is activated. Only called server side
     *
     * @return Whether the action was successfully activated. !Does not give any feedback to the user!
     */
    boolean onActivated(T player);

    enum PERM {
        ALLOWED, DISABLED, NOT_UNLOCKED, DISALLOWED, COOLDOWN//Cooldown should not be used by the skill itself, but only by the {@link IActionHandler}
    }
}
