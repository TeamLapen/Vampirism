package de.teamlapen.faction.common.world.entities.appearance;

import com.mojang.serialization.Codec;
import net.minecraft.resources.Identifier;

@SuppressWarnings("unused")
public record AppearanceKey<T>(Identifier Id) {

    public static final Codec<AppearanceKey<?>> CODEC = Identifier.CODEC.xmap(AppearanceKey::new, AppearanceKey::Id);
}
