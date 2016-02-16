package de.teamlapen.vampirism.api.entity.player.vampire;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Interface for vampire player skills
 */
public interface IVampireSkill {
    /**
     * Checks if the player can use this skill
     */
    PERM canUse(IVampirePlayer vampire);

    /**
     * @return Cooldown time in ticks until the skill can be used again
     */
    int getCooldown();

    String getUnlocalizedName();

    /**
     * Called when the skill is activated SERVER SIDE ONLY
     *
     * @param vampire
     * @return Whether the skill was successfully activated. !Does not give any feedback to the user!
     */
    boolean onActivated(IVampirePlayer vampire);

    /**
     * Should return the location of the icon map where the icon is in
     *
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

    enum PERM {
        ALLOWED, DISABLED, LEVEL_TO_LOW, DISALLOWED, COOLDOWN//Cooldown should not be used by the skill itself, but only by the {@link ISkillHandler}
    }

}
