package de.teamlapen.vampirism.entity.player;

import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.core.ModAttributes;
import net.minecraft.SharedConstants;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages the players EntityAttribute modifiers
 */
public class LevelAttributeModifier {

    public static final ResourceLocation ID = VResourceLocation.mod("level_attribute_modifier");

    /**
     * @param player      The player to be modified
     * @param attribute   The attribute to be modified
     * @param name        the name of the modifier
     * @param level       the player level
     * @param lcap        the level cap of the modifier
     * @param max         the max modifier value
     * @param type        the exponent used to calculate the actual value depending on level, levelcap and max value
     * @param operation   The operation applied to the attribute
     * @param evenIntOnly If the modifier should be rounded to an even integer
     */
    public static void applyModifier(@NotNull Player player, @NotNull Holder<Attribute> attribute, String name, int level, int lcap, double max, double type, AttributeModifier.@NotNull Operation operation, boolean evenIntOnly) {
        double m = calculateModifierValue(level, lcap, max, type);
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance != null) {
            instance.removeModifier(ID);
            if (evenIntOnly) {
                m = Math.round(m / 2) * 2;
            }
            instance.addPermanentModifier(new AttributeModifier(ID, m, operation));
        } else if (SharedConstants.IS_RUNNING_IN_IDE) {
            throw new IllegalStateException("Attribute " + attribute + " not found for player " + player);
        }
    }

    /**
     * Calculates the modifier effect. You can decide how the modifier changes with higher levels, by using different types. Suggested values are 1/2 for a square root like behavior or 1 for a linear
     * change
     *
     * @param level  Vampire level
     * @param lcap   Level the modifier does not get any stronger
     * @param maxMod Maximal modifier effect
     * @param type   modifier type
     * @return value between 0 and maxMod
     */
    public static double calculateModifierValue(int level, int lcap, double maxMod, double type) {
        return Math.pow((Math.min(level, lcap)), type) / Math.pow(lcap, type) * maxMod;
    }

}
