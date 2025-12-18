package de.teamlapen.vampirism.common.world.entity.player.refinements;

import de.teamlapen.factions.api.factions.refinements.IRefinement;
import de.teamlapen.vampirism.common.util.RegUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.Holder;
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
    @Nullable
    private String descriptionId;

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

    @Override
    public @NotNull MutableComponent getDescription() {
        var desc = IRefinement.super.getDescription();
        if (this.detrimental) {
            desc.withStyle(ChatFormatting.RED);
        }
        return desc;
    }

    @Override
    public @NotNull String getDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = Util.makeDescriptionId("refinement", RegUtil.id(this)) + ".desc";
        }
        return this.descriptionId;
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
