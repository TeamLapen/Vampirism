package de.teamlapen.vampirism.potion;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.config.Configs;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

/**
 * Potion which replaces the vanilla night vision one.
 */
public class FakeNightVisionPotion extends Potion {
    public static final FakeNightVisionPotion instance = new FakeNightVisionPotion();

    protected FakeNightVisionPotion() {
        super(new ResourceLocation(REFERENCE.MODID, "night_vision"), false, 2039713);
        setIconIndex(4, 1);
        setPotionName("potion.nightVision");
    }

    /**
     * Replace the night vision potion in {@link Potion#potionTypes} by the fake version using the same id.
     * Checks if it is enabled in the configs first
     */
    public static void replaceNightVision() {
        if (Configs.replaceVanillaNightVision) {
            instance.id = Potion.nightVision.getId();
            VampirismMod.log.d("FakeNVPotion", "Replacing vanilla night vision (%s) at %d", Potion.potionTypes[instance.getId()].getClass(), instance.getId());
            Potion.potionTypes[instance.getId()] = instance;
        }
    }

    @Override
    public boolean shouldRender(PotionEffect effect) {
        if (effect instanceof FakeNightVisionPotionEffect) return false;
        return super.shouldRender(effect);
    }
}
