package de.teamlapen.vampirism.common.world.items.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.factions.FactionsMod;
import de.teamlapen.factions.api.world.items.components.IFactionRestriction;
import de.teamlapen.factions.common.components.FactionRestriction;
import de.teamlapen.factions.common.components.IFactionRestrictionProvider;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.world.items.components.IAppliedOilContent;
import de.teamlapen.vampirism.api.world.items.oil.IApplicableOil;
import de.teamlapen.vampirism.api.world.items.oil.IOil;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.core.ModFactions;
import de.teamlapen.vampirism.common.core.ModRegistries;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public record AppliedOilContent(Holder<IApplicableOil> oil, int duration) implements IAppliedOilContent, IFactionRestrictionProvider {

    public static final Codec<AppliedOilContent> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    ModRegistries.OILS.holderByNameCodec().validate(s -> s.value() instanceof IApplicableOil ? DataResult.success(s) : DataResult.error(() -> "only applicable oils are allowed")).xmap(s -> (Holder<IApplicableOil>) (Object) s, s -> (Holder<IOil>) (Object) s).fieldOf("oil").forGetter(o -> o.oil),
                    Codec.INT.fieldOf("duration").forGetter(o -> o.duration)
            ).apply(inst, AppliedOilContent::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, AppliedOilContent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.holderRegistry(VampirismRegistries.Keys.OIL).map(s -> (Holder<IApplicableOil>) (Object) s, l -> (Holder<IOil>) (Object) l), AppliedOilContent::oil, ByteBufCodecs.VAR_INT, AppliedOilContent::duration, AppliedOilContent::new
    );
    public static final FactionRestriction HUNTER_RESTRICTION = FactionRestriction.builder(ModFactions.HUNTER).build();

    public static ItemStack apply(ItemStack stack, Holder<IApplicableOil> oil) {
        return apply(stack, oil, oil.value().getMaxDuration(stack));
    }

    public static ItemStack apply(ItemStack stack, Holder<IApplicableOil> oil, int duration) {
        if (duration <= 0) {
            return remove(stack);
        }
        stack.set(ModDataComponents.APPLIED_OIL, new AppliedOilContent(oil, duration));
        return stack;
    }

    public static ItemStack remove(ItemStack stack) {
        stack.remove(ModDataComponents.APPLIED_OIL);
        return stack;
    }

    public static Optional<AppliedOilContent> getAppliedOil(ItemStack stack) {
        return Optional.ofNullable(stack.get(ModDataComponents.APPLIED_OIL));
    }

    @Override
    public IFactionRestriction getFactionRestriction() {
        return HUNTER_RESTRICTION;
    }

    //<editor-fold desc="Tooltip>

    public static void addTooltipIfExist(ItemStack stack, List<Component> tooltip, TooltipFlag flag) {
        addTooltipIfExist(FactionsMod.proxy.getClientPlayer(), stack, tooltip, flag);
    }

    public static void addTooltipIfExist(@Nullable Player player, ItemStack stack, List<Component> tooltip, TooltipFlag flag) {
        AppliedOilContent appliedOilContent = stack.get(ModDataComponents.APPLIED_OIL);
        if (appliedOilContent != null) {
            appliedOilContent.addTooltip(stack, tooltip, flag);
        }
    }

    @SuppressWarnings("DataFlowIssue")
    public void addTooltip(ItemStack stack, List<Component> tooltip, TooltipFlag flag) {
        ResourceLocation id = oil().getKey().location();
        MutableComponent component = Component.translatable(String.format("oil.%s.%s", id.getNamespace(), id.getPath())).withStyle(ChatFormatting.LIGHT_PURPLE);
        if (oil().value().hasDuration()) {
            int maxDuration = oil().value().getMaxDuration(stack);
            float perc = duration / (float) maxDuration;
            ChatFormatting status = perc > 0.5 ? ChatFormatting.GREEN : perc > 0.25 ? ChatFormatting.GOLD : ChatFormatting.RED;
            if (flag.isAdvanced()) {
                component.append(" ").append(Component.literal("%s/%s".formatted(duration, maxDuration)).withStyle(status));
            } else {
                component.append(" ").append(Component.translatable("text.vampirism.oil.wetting_status").withStyle(status));
            }
        }
        tooltip.add(component);
    }

    //</editor-fold>
}
