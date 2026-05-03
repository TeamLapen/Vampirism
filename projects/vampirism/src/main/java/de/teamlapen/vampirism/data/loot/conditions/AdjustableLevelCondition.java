package de.teamlapen.vampirism.data.loot.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.difficulty.IAdjustableLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

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

    @Override
    public @NonNull MapCodec<? extends LootItemCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(@NotNull LootContext lootContext) {
        Entity e = lootContext.getOptionalParameter(target.contextParam());
        if (e instanceof IAdjustableLevel) {
            int l = ((IAdjustableLevel) e).getEntityLevel();
            if (levelTest != -1) {
                return levelTest == l;
            }
        }
        return false;
    }

}
