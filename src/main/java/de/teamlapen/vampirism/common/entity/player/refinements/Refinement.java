package de.teamlapen.vampirism.common.entity.player.refinements;

import de.teamlapen.vampirism.api.entity.player.refinement.IRefinement;
import de.teamlapen.vampirism.common.util.RegUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public class Refinement implements IRefinement {

    private final @Nullable Holder<Attribute> attribute;
    private final @Nullable BiFunction<ResourceLocation, Double, AttributeModifier> modifier;
    private final double baseValue;
    private boolean detrimental = false;
    private MutableComponent description;

    public Refinement(@Nullable Holder<Attribute> attribute, double baseValue, @Nullable BiFunction<ResourceLocation, Double, AttributeModifier> modifier) {
        this.attribute = attribute;
        this.modifier = modifier;
        this.baseValue = baseValue;
    }

    public Refinement() {
        this.attribute = null;
        this.modifier = null;
        this.baseValue = 0;
    }

    @Override
    public @Nullable BiFunction<ResourceLocation, Double, AttributeModifier> attributeFactory() {
        return this.modifier;
    }

    @Nullable
    @Override
    public Holder<Attribute> getAttribute() {
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
