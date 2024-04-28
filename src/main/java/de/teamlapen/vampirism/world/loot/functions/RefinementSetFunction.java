package de.teamlapen.vampirism.world.loot.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import de.teamlapen.vampirism.core.ModLoot;
import de.teamlapen.vampirism.items.RefinementItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RefinementSetFunction extends LootItemConditionalFunction {

    public static final MapCodec<RefinementSetFunction> CODEC = RecordCodecBuilder.mapCodec(inst ->
            commonFields(inst).and(IFaction.CODEC.fieldOf("faction").forGetter(l -> l.faction))
                    .apply(inst, RefinementSetFunction::new)
            );
    public static @NotNull Builder<?> builder(IFaction<?> faction) {
        return simpleBuilder(conditions -> new RefinementSetFunction(conditions, faction));
    }

    public static @NotNull Builder<?> builder() {
        return simpleBuilder(conditions -> new RefinementSetFunction(conditions, null));
    }

    @Nullable
    public final IFaction<?> faction;

    public RefinementSetFunction(@NotNull List<LootItemCondition> conditionsIn, @Nullable IFaction<?> faction) {
        super(conditionsIn);
        this.faction = faction;
    }

    @NotNull
    @Override
    public LootItemFunctionType<RefinementSetFunction> getType() {
        return ModLoot.ADD_REFINEMENT_SET.get();
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
