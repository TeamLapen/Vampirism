package de.teamlapen.vampirism.api.items;

import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import net.minecraft.item.crafting.IRecipe;

import javax.annotation.Nullable;

/**
 * Recipe that can be used in the hunter weapon crafting table
 */
public interface IHunterWeaponRecipe extends IRecipe {


    /**
     * @return The hunter level required to craft this
     */
    int getMinHunterLevel();

    /**
     * @return The skill that has to be unlocked to craft this or null if none is required
     */
    @Nullable
    ISkill<IHunterPlayer> getRequiredSkill();
}
