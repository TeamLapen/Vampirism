package de.teamlapen.vampirism.world.loot.conditions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.items.oil.IOil;
import de.teamlapen.vampirism.core.ModLoot;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.util.OilUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.NotNull;

public class OilItemCondition implements LootItemCondition {

    public static final MapCodec<OilItemCondition> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(ModRegistries.OILS.byNameCodec().fieldOf("oil").forGetter(condition -> condition.oil)).apply(inst, OilItemCondition::new));
    private final @NotNull IOil oil;

    public OilItemCondition(@NotNull IOil oil) {
        this.oil = oil;
    }

    @NotNull
    @Override
    public LootItemConditionType getType() {
        return ModLoot.WITH_OIL_ITEM.get();
    }

    @Override
    public boolean test(@NotNull LootContext lootContext) {
        ItemStack stack = lootContext.getParamOrNull(LootContextParams.TOOL);
        return stack != null && OilUtils.getAppliedOil(stack).map(oil -> oil == this.oil).orElse(false);
    }
}
