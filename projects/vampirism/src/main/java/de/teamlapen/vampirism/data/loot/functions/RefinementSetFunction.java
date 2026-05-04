package de.teamlapen.vampirism.data.loot.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.refinements.IRefinementSet;
import de.teamlapen.faction.api.world.items.IRefinementItem;
import de.teamlapen.faction.common.core.ModRegistries;
import de.teamlapen.vampirism.common.world.items.RefinementItem;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RefinementSetFunction extends LootItemConditionalFunction {

    @SuppressWarnings("unchecked")
    public static final MapCodec<RefinementSetFunction> CODEC = RecordCodecBuilder.mapCodec(inst ->
            commonFields(inst).and(ModRegistries.FACTIONS.holderByNameCodec().fieldOf("faction").forGetter(l -> (Holder<IFaction<?>>) l.faction))
                    .apply(inst, RefinementSetFunction::new)
    );

    public static @NotNull Builder<?> builder(Holder<? extends IFaction<?>> faction) {
        return simpleBuilder(conditions -> new RefinementSetFunction(conditions, faction));
    }

    public static @NotNull Builder<?> builder() {
        return simpleBuilder(conditions -> new RefinementSetFunction(conditions, null));
    }

    @Nullable
    public final Holder<? extends IFaction<?>> faction;

    public RefinementSetFunction(@NotNull List<LootItemCondition> conditionsIn, @Nullable Holder<? extends IFaction<?>> faction) {
        super(conditionsIn);
        this.faction = faction;
    }

    @Override
    public MapCodec<? extends LootItemConditionalFunction> codec() {
        return CODEC;
    }

    @NotNull
    @Override
    protected ItemStack run(@NotNull ItemStack stack, @NotNull LootContext context) {
        if (stack.getItem() instanceof IRefinementItem) {
            IRefinementSet set = RefinementItem.getRandomRefinementForItem(faction, ((IRefinementItem) stack.getItem()));
            if (set != null) {
                ((IRefinementItem) stack.getItem()).applyRefinementSet(stack, set);
            }
        }
        return stack;
    }

}
