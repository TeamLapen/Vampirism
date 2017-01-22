package de.teamlapen.vampirism.api.items;

import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import net.minecraft.item.crafting.IRecipe;

import javax.annotation.Nonnull;

/**
 * Recipe that can be used in the hunter weapon crafting table
 */
public interface IHunterWeaponRecipe extends IRecipe {


    /**
     * @return The hunter level required to craft this
     */
    int getMinHunterLevel();

    /**
     * Measured in 1/5 buckets. Min value=0
     *
     * @return The amount of lava required for this recipe.
     */
    int getRequiredLavaUnits();

    /**
     * @return The skills that have to be unlocked to craft this. Can be empty
     */
    @Nonnull
    ISkill<IHunterPlayer>[] getRequiredSkills();
}
