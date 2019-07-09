package de.teamlapen.vampirism.entity.minions.commands;

import de.teamlapen.vampirism.api.entity.minions.IMinion;
import de.teamlapen.vampirism.api.entity.minions.IMinionCommand;
import de.teamlapen.vampirism.util.MinionHelper;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

/**
 * Default implementation of IMinionCommand
 *
 * @author Maxanier
 */
public abstract class DefaultMinionCommand implements IMinionCommand {


    protected final CreatureEntity minionEntity;
    protected final IMinion minion;
    private final int id;

    public DefaultMinionCommand(int id, IMinion minion) {
        this.id = id;
        this.minion = minion;
        this.minionEntity = MinionHelper.entity(minion);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public ResourceLocation getIconLoc() {
        return null;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public boolean shouldPickupItem(@Nonnull ItemStack item) {
        return false;
    }

    @Override
    public String toString() {
        return getUnlocalizedName() + ":" + id;
    }

}