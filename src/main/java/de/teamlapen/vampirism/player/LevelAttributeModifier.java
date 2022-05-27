package de.teamlapen.vampirism.player;

import de.teamlapen.vampirism.core.ModAttributes;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages the players EntityAttribute modifiers
 */
public class LevelAttributeModifier {

    private final static Logger LOGGER = LogManager.getLogger(LevelAttributeModifier.class);

    private static final Map<Attribute, UUID> modifiers = new HashMap<>();

    static {
        modifiers.put(Attributes.ATTACK_DAMAGE, UUID.fromString("7600D8C4-3517-40BE-8CB1-359D46705A0F"));
        modifiers.put(Attributes.MOVEMENT_SPEED, UUID.fromString("0FCBF922-DBEC-492A-82F5-99F73AFF5065"));
        modifiers.put(Attributes.MAX_HEALTH, UUID.fromString("56C17EFE-E3EC-4E27-A12F-99D2FE927B70"));
        modifiers.put(ModAttributes.BLOOD_EXHAUSTION.get(), UUID.fromString("4504ccfa-dfdc-11e5-b86d-9a79f06e9478"));
        modifiers.put(Attributes.ATTACK_SPEED, UUID.fromString("37a4f596-2ff8-45e5-b074-c91df218f26b"));
        modifiers.put(Attributes.ARMOR, UUID.fromString("84769cda-82dd-46f8-8069-15d659b29408"));
    }

    /**
     * Can be used to register an additional modifiable attribute from modcompat
     */
    public static void registerModdedAttributeModifier(Attribute attribute, UUID uuid) {
        modifiers.put(attribute, uuid);
    }

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
    public static void applyModifier(Player player, Attribute attribute, String name, int level, int lcap, double max, double type, AttributeModifier.Operation operation, boolean evenIntOnly) {
        UUID mod = modifiers.get(attribute);
        if (mod == null) {
            LOGGER.warn("Cannot modify {}, no modifier is registered", attribute);
            return;
        }
        double m = calculateModifierValue(level, lcap, max, type);
        AttributeInstance instance = player.getAttribute(attribute);
        rmMod(instance, mod);
        if (evenIntOnly) {
            m = Math.round(m / 2) * 2;
        }
        instance.addPermanentModifier(new AttributeModifier(mod, (attribute.getDescriptionId/*getName*/() + " " + name + " Boost"), m, operation));
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

    /**
     * Removes existing modifiers
     *
     * @param att  Attribute
     * @param uuid UUID of modifier to remove
     */
    private static void rmMod(AttributeInstance att, UUID uuid) {
        AttributeModifier m = att.getModifier(uuid);
        if (m != null) {
            att.removeModifier(m);
        }
    }

    /**
     * @param attribute the attribute whose UUID you need
     * @return the UUID of the given attribute
     */
    public static UUID getUUID(Attribute attribute) {
        return modifiers.get(attribute);
    }
}
