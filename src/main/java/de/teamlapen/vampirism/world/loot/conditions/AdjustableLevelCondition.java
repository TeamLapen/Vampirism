package de.teamlapen.vampirism.world.loot.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import de.teamlapen.vampirism.core.ModLoot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.jetbrains.annotations.NotNull;

public class AdjustableLevelCondition implements LootItemCondition {

    public static final MapCodec<AdjustableLevelCondition> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.INT.fieldOf("level").forGetter(l -> l.levelTest),
            LootContext.EntityTarget.CODEC.fieldOf("target").forGetter(l -> l.target)
    ).apply(inst, AdjustableLevelCondition::new));

    public static @NotNull Builder builder(int level, LootContext.EntityTarget target) {
        return () -> new AdjustableLevelCondition(level, target);
    }

    private final int levelTest;
    private final LootContext.EntityTarget target;

    public AdjustableLevelCondition(int level, LootContext.EntityTarget targetIn) {
        levelTest = level;
        this.target = targetIn;
    }

    @NotNull
    @Override
    public LootItemConditionType getType() {
        return ModLoot.ADJUSTABLE_LEVEL.get();
    }

    @Override
    public boolean test(@NotNull LootContext lootContext) {
        Entity e = lootContext.getParamOrNull(target.getParam());
        if (e instanceof IAdjustableLevel) {
            int l = ((IAdjustableLevel) e).getEntityLevel();
            if (levelTest != -1) {
                return levelTest == l;
            }
        }
        return false;
    }

}
