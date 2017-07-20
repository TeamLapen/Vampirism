package de.teamlapen.vampirism.api.entity.player.actions;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;

/**
 * Interface for player actions
 */
public interface IAction extends IForgeRegistryEntry<IAction> {
    /**
     * Checks if the player can use this action
     * @param player Must be instance of class that belongs to {@link IAction#getFaction()}
     */
    PERM canUse(IFactionPlayer player);

    /**
     * @return Cooldown time in ticks until the action can be used again
     */
    int getCooldown();

    /**

     * @return the faction, which players can use this action
     */
    @Nonnull
    IPlayableFaction getFaction();

    /**
     * Should return the location of the icon map where the icon is in
     * Texture has to be 256x80
     *
     * @return
     */
    @SideOnly(Side.CLIENT)
    ResourceLocation getIconLoc();

    /**

     * @return the min U texture coordinate within the icon map
     */
    @SideOnly(Side.CLIENT)
    int getMinU();

    /**

     * @return the min V texture coordinate within the icon map
     */
    @SideOnly(Side.CLIENT)
    int getMinV();

    String getUnlocalizedName();

    /**
     * Called when the action is activated. Only called server side
     *
     * @param player Must be instance of class that belongs to {@link IAction#getFaction()}
     * @return Whether the action was successfully activated. !Does not give any feedback to the user!
     */
    boolean onActivated(IFactionPlayer player);

    enum PERM {
        ALLOWED, DISABLED, NOT_UNLOCKED, DISALLOWED, COOLDOWN//Cooldown should not be used by the skill itself, but only by the {@link IActionHandler}
    }
}
