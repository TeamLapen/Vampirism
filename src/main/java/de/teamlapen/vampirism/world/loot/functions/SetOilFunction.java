package de.teamlapen.vampirism.world.loot.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.items.oil.IOil;
import de.teamlapen.vampirism.core.ModLoot;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.util.OilUtils;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class SetOilFunction extends LootItemConditionalFunction {

    public static final Codec<SetOilFunction> CODEC = RecordCodecBuilder.create(inst ->
            commonFields(inst).and(
                    ExtraCodecs.strictOptionalField(ModRegistries.OILS.byNameCodec(), "oil").forGetter(l -> Optional.ofNullable(l.oil))
            ).apply(inst, SetOilFunction::new));
    private final IOil oil;
    private final boolean random;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    SetOilFunction(List<LootItemCondition> conditions, Optional<IOil> oil) {
        super(conditions);
        if (oil.isPresent()){
            this.oil = oil.get();
            this.random = false;
        } else {
            this.oil = null;
            this.random = true;
        }
    }

    @Override
    protected @NotNull ItemStack run(@NotNull ItemStack pStack, @NotNull LootContext pContext) {
        IOil oil = this.oil;
        if (this.random) {
            Collection<IOil> values = ModRegistries.OILS.stream().toList();
            oil = values.stream().skip((int) (values.size() * pContext.getRandom().nextDouble())).findFirst().orElseThrow(() -> new IllegalStateException("No oils registered"));
        }
        OilUtils.setOil(pStack, oil);
        return pStack;
    }

    public static LootItemConditionalFunction.Builder<?> setOil(@NotNull IOil oil) {
        return simpleBuilder((conditions) -> new SetOilFunction(conditions, Optional.of(oil)));
    }

    public static LootItemConditionalFunction.Builder<?> random() {
        return simpleBuilder((conditions) -> new SetOilFunction(conditions, Optional.empty()));
    }

    @Override
    public @NotNull LootItemFunctionType getType() {
        return ModLoot.SET_OIL.get();
    }
}
