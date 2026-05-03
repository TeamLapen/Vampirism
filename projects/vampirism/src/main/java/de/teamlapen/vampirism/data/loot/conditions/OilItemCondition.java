package de.teamlapen.vampirism.data.loot.conditions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.world.items.oil.IOil;
import de.teamlapen.vampirism.common.core.ModRegistries;
import de.teamlapen.vampirism.common.util.OilUtils;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class OilItemCondition implements LootItemCondition {

    public static final MapCodec<OilItemCondition> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(ModRegistries.OILS.byNameCodec().fieldOf("oil").forGetter(condition -> condition.oil)).apply(inst, OilItemCondition::new));
    private final @NotNull IOil oil;

    public OilItemCondition(@NotNull IOil oil) {
        this.oil = oil;
    }

    @Override
    public @NonNull MapCodec<? extends LootItemCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull LootContext lootContext) {
        ItemInstance stack = lootContext.getOptionalParameter(LootContextParams.TOOL);
        return stack != null && OilUtils.getAppliedOil(stack).map(oil -> oil == this.oil).orElse(false);
    }
}
