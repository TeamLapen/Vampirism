package de.teamlapen.vampirism.entity.player;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages the players EntityAttribute modifiers
 */
public class LevelAttributeModifier {

    private static final String TAG = "LevelAttributeModifier";

    private static final Map<IAttribute, UUID> modifiers = new HashMap<IAttribute, UUID>();

    static {
        modifiers.put(SharedMonsterAttributes.ATTACK_DAMAGE, UUID.fromString("7600D8C4-3517-40BE-8CB1-359D46705A0F"));
        modifiers.put(SharedMonsterAttributes.MOVEMENT_SPEED, UUID.fromString("0FCBF922-DBEC-492A-82F5-99F73AFF5065"));
        modifiers.put(SharedMonsterAttributes.MAX_HEALTH, UUID.fromString("56C17EFE-E3EC-4E27-A12F-99D2FE927B70"));
        modifiers.put(VReference.bloodExhaustion, UUID.fromString("4504ccfa-dfdc-11e5-b86d-9a79f06e9478"));
        modifiers.put(SharedMonsterAttributes.ATTACK_SPEED, UUID.fromString("37a4f596-2ff8-45e5-b074-c91df218f26b"));
    }

    /**
     * Can be used to register an additional modifiable attribute from modcompat
     *
     * @param attribute
     * @param uuid
     */
    public static void registerModdedAttributeModifier(IAttribute attribute, UUID uuid) {
        modifiers.put(attribute, uuid);
    }

    public static void applyModifier(EntityPlayer player, IAttribute attribute, String name, int level, int lcap, double max, double type) {
        UUID mod = modifiers.get(attribute);
        if (mod == null) {
            VampirismMod.log.w(TAG, "Cannot modify %s, no modifier is registered", attribute);
            return;
        }
        double m = calculateModifierValue(level, lcap, max, type);
        IAttributeInstance instance = player.getEntityAttribute(attribute);
        rmMod(instance, mod);

        instance.applyModifier(new AttributeModifier(mod, (attribute.getAttributeUnlocalizedName() + " " + name + " Boost"), m, 2));
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
    private static double calculateModifierValue(int level, int lcap, double maxMod, double type) {
        return Math.pow((level > lcap ? lcap : level), type) / Math.pow(lcap, type) * maxMod;
    }

    /**
     * Removes existing modifiers
     *
     * @param att  Attribute
     * @param uuid UUID of modifier to remove
     */
    private static void rmMod(IAttributeInstance att, UUID uuid) {
        AttributeModifier m = att.getModifier(uuid);
        if (m != null) {
            att.removeModifier(m);
        }
    }
}
