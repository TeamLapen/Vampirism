package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMapDecorations {
    public static final DeferredRegister<MapDecorationType> MAP_DECORATION_TYPES = DeferredRegister.create(Registries.MAP_DECORATION_TYPE, REFERENCE.MODID);

    public static final DeferredHolder<MapDecorationType, MapDecorationType> ANCIENT_REMAINS = register("ancient_remains", 0x851609);
    public static final DeferredHolder<MapDecorationType, MapDecorationType> CRYPT = register("crypt", 0x671787);

    static void register(IEventBus bus) {
        MAP_DECORATION_TYPES.register(bus);
    }

    private static DeferredHolder<MapDecorationType, MapDecorationType> register(String name, int color) {
        return MAP_DECORATION_TYPES.register(name, () -> new MapDecorationType(VResourceLocation.mod(name), true, color, false, true));
    }
}
