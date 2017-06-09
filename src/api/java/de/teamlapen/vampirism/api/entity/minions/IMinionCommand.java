package de.teamlapen.vampirism.api.entity.minions;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Interface for minion commands, which can be executed by {@link IMinion}
 *
 * @author Max
 */
public interface IMinionCommand {

    /**
     * @return if the command can be activated
     */
    boolean canBeActivated();

    /**
     * Should return the location of the icon map where the icon is in
     *
     * @return null to use vampirism's default one
     */
    @SideOnly(Side.CLIENT)
    @Nullable
    ResourceLocation getIconLoc();

    /**
     * @return An id, which is unique for each command of a minion
     */
    int getId();

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
     * Called serverside when the command is activated. Usually used to add AI
     */
    void onActivated();

    /**
     * Called serverside when the command is deactivated. Usually used to remove added AI
     */
    void onDeactivated();

    /**
     * If this returns true, while the command is activated, minions (at least the RemoteVampireMinion) pick up such an item, if they stand on it.
     *
     * @param item
     * @return
     */
    boolean shouldPickupItem(@Nonnull ItemStack item);
}