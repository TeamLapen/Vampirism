package de.teamlapen.vampirism.player.refinements;

import de.teamlapen.vampirism.api.entity.player.refinement.IRefinement;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Function;

public class Refinement extends ForgeRegistryEntry<IRefinement> implements IRefinement {

    private final Attribute attribute;
    private final Function<UUID, AttributeModifier> modifier;

    public Refinement(Attribute attribute, Function<UUID, AttributeModifier> modifier) {
        this.attribute = attribute;
        this.modifier = modifier;
    }

    public Refinement() {
        this.attribute = null;
        this.modifier = null;
    }


    @Override
    public AttributeModifier createAttributeModifier(UUID uuid) {
        return this.modifier==null?null:this.modifier.apply(uuid);
    }

    @Nullable
    @Override
    public Attribute getAttribute() {
        return this.attribute;
    }

    @Nonnull
    @Override
    public String getDescriptionKey() {
        return "refinement." + getRegistryName().getNamespace() + "." + getRegistryName().getPath() + ".desc";
    }
}
