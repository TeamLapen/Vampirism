package de.teamlapen.vampirism.potion;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.config.Configs;
import de.teamlapen.vampirism.util.SRGNAMES;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Potion which replaces the vanilla night vision one.
 */
public class FakeNightVisionPotion extends Potion {
    public static final FakeNightVisionPotion instance = new FakeNightVisionPotion();

    /**
     * Replace the night vision potion in {@link MobEffects} by the fake version.
     * Checks if it is enabled in the configs first
     */
    public static void replaceNightVision() {
        if (!Configs.disable_replaceVanillaNightVision) {
            VampirismMod.log.d("FakeNVPotion", "Replacing vanilla night vision (%s) with custom", MobEffects.nightVision.getClass());
            try {
                Field field = ReflectionHelper.findField(MobEffects.class, "nightVision", SRGNAMES.MobEffects_nightVision);
                field.setAccessible(true);

                Field modifierField = Field.class.getDeclaredField("modifiers");
                modifierField.setAccessible(true);
                modifierField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

                field.set(null, instance);
            } catch (NoSuchFieldException e) {
                VampirismMod.log.e("FakeNVPotion", e, "Failed to find night vision field, names might have changed");
            } catch (IllegalAccessException e) {
                VampirismMod.log.e("FakeNVPotion", e, "Failed to replace night vision. ");
            }

        }
    }

    protected FakeNightVisionPotion() {
        super(false, 2039713);
        setIconIndex(4, 1);
        setPotionName("potion.nightVision");
    }

    @Override
    public boolean shouldRender(PotionEffect effect) {
        if (effect instanceof FakeNightVisionPotionEffect) return false;
        return super.shouldRender(effect);
    }
}
