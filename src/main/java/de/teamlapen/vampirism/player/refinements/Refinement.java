package de.teamlapen.vampirism.player.refinements;

import de.teamlapen.vampirism.api.entity.player.refinement.IRefinement;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.BiFunction;

public class Refinement implements IRefinement {

    private final @Nullable Attribute attribute;
    private final @Nullable BiFunction<UUID, Double, AttributeModifier> modifier;
    private final @Nullable UUID uuid;
    private final double baseValue;
    private boolean detrimental = false;
    private MutableComponent description;

    public Refinement(Attribute attribute, UUID uuid, double baseValue, BiFunction<UUID, Double, AttributeModifier> modifier) {
        this.attribute = attribute;
        this.modifier = modifier;
        this.uuid = uuid;
        this.baseValue = baseValue;
    }

    public Refinement() {
        this.attribute = null;
        this.modifier = null;
        this.baseValue = 0;
        this.uuid = null;
    }

    @Override
    public AttributeModifier createAttributeModifier(UUID uuid, double value) {
        return this.modifier == null ? null : this.modifier.apply(uuid, value);
    }

    @Nullable
    @Override
    public Attribute getAttribute() {
        return this.attribute;
    }

    @NotNull
    @Override
    public Component getDescription() {
        if (description == null) {
            description = Component.translatable("refinement." + RegUtil.id(this).getNamespace() + "." + RegUtil.id(this).getPath() + ".desc");
            if (detrimental) description.withStyle(ChatFormatting.RED);
        }
        return description;
    }

    @Override
    public double getModifierValue() {
        return this.baseValue;
    }

    @Nullable
    @Override
    public UUID getUUID() {
        return this.uuid;
    }

    /**
     * Set when refinement actually makes things worse instead of better
     *
     * @return this
     */
    public @NotNull Refinement setDetrimental() {
        this.detrimental = true;
        return this;
    }
}
