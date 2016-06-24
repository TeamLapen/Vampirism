package de.teamlapen.vampirism.potion;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.config.Configs;
import de.teamlapen.vampirism.core.ModPotions;
import de.teamlapen.vampirism.util.REFERENCE;
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


    public static Potion vanillaInstance = null;

    /**
     * Replace the night vision potion in {@link MobEffects} by the fake version.
     * Checks if it is enabled in the configs first
     */
    public static void replaceNightVision() {
        if (!Configs.disable_replaceVanillaNightVision && !(MobEffects.NIGHT_VISION instanceof FakeNightVisionPotion)) {
            VampirismMod.log.d("FakeNVPotion", "Replacing vanilla night vision (%s) with custom", MobEffects.NIGHT_VISION.getClass());
            try {
                vanillaInstance = MobEffects.NIGHT_VISION;
                Field field = ReflectionHelper.findField(MobEffects.class, "NIGHT_VISION", SRGNAMES.MobEffects_nightVision);
                field.setAccessible(true);

                Field modifierField = Field.class.getDeclaredField("modifiers");
                modifierField.setAccessible(true);
                modifierField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

                field.set(null, ModPotions.fakeNightVisionPotion);
            } catch (ReflectionHelper.UnableToFindFieldException e) {
                VampirismMod.log.e("FakeNVPotion", e, "Failed to find night vision field, names might have changed");
            } catch (IllegalAccessException e) {
                VampirismMod.log.e("FakeNVPotion", e, "Failed to replace night vision. ");
            } catch (NoSuchFieldException e) {
                VampirismMod.log.e("FakeNVPotion", e, "Failed to find night vision modifier field, names might have changed");
            }

        }
    }

    public FakeNightVisionPotion() {
        super(false, 2039713);
        setIconIndex(4, 1);
        setPotionName("effect.nightVision2");
        this.setRegistryName(REFERENCE.MODID, "night_vision");
    }

    @Override
    public boolean shouldRender(PotionEffect effect) {
        if (effect instanceof FakeNightVisionPotionEffect) return false;
        return super.shouldRender(effect);
    }
}
