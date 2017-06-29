package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.potion.FakeNightVisionPotion;
import de.teamlapen.vampirism.potion.PotionSanguinare;
import de.teamlapen.vampirism.potion.PotionThirst;
import de.teamlapen.vampirism.potion.VampirismPotion;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.potion.Potion;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;

/**
 * Handles all potion registrations and reference.
 */
@GameRegistry.ObjectHolder(REFERENCE.MODID)
public class ModPotions {

    public static final Potion sanguinare = getNull();
    public static final Potion thirst = getNull();
    public static final Potion saturation = getNull();
    public static final Potion sunscreen = getNull();
    public static final Potion disguise_as_vampire = getNull();
    public static final Potion fire_protection = getNull();
    public static final Potion garlic = getNull();
    @GameRegistry.ObjectHolder("minecraft:night_vision")
    public static final Potion fakeNightVisionPotion = getNull();

    @SuppressWarnings("ConstantConditions")
    private static @Nonnull
    <T> T getNull() {
        return null;
    }

    static void registerPotions(IForgeRegistry<Potion> registry) {
        registry.register(new PotionThirst("thirst", true, 859494));
        registry.register(new FakeNightVisionPotion());
        registry.register(new PotionSanguinare("sanguinare", false, 0x6A0888));
        registry.register(new VampirismPotion("saturation", false, 0xDCFF00).setIconIndex(2, 0).setBeneficial());
        Potion sunscreen = new VampirismPotion("sunscreen", false, 0xFFF100).setIconIndex(3, 0).setBeneficial();
        sunscreen.registerPotionAttributeModifier(VReference.sunDamage, "9dc9420c-3e5e-41c7-9ba4-ff70e9dc69fc", -0.5, 2);
        registry.register(sunscreen);
        registry.register(new VampirismPotion("fire_protection", false, 14981690).setIconIndex(6, 0).setBeneficial());
        registry.register(new VampirismPotion("disguise_as_vampire", false, 0x999900).setIconIndex(4, 0).setBeneficial());
        registry.register(new VampirismPotion("garlic", true, 0xFFFFFF).setIconIndex(5, 0));
    }


    static void fixMapping(RegistryEvent.MissingMappings.Mapping<Potion> m) {
        if ("night_vision".equals(m.key.getResourcePath())) {
            m.ignore();
        }
    }
}
