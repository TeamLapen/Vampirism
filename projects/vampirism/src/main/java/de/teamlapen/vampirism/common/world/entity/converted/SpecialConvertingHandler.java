package de.teamlapen.vampirism.common.world.entity.converted;

import de.teamlapen.faction.common.util.SpawnUtil;
import de.teamlapen.vampirism.api.world.entity.convertible.IConvertedCreature;
import de.teamlapen.vampirism.api.world.entity.convertible.ICurableConvertedCreature;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class SpecialConvertingHandler<T extends PathfinderMob, Z extends PathfinderMob & ICurableConvertedCreature<T>> extends DefaultConvertingHandler<T> {

    private final Supplier<EntityType<Z>> convertedType;

    public SpecialConvertingHandler(Supplier<EntityType<Z>> convertedType) {
        super(null);
        this.convertedType = convertedType;
    }

    public SpecialConvertingHandler(Supplier<EntityType<Z>> convertedType, IDefaultHelper attributeHelper) {
        super(attributeHelper);
        this.convertedType = convertedType;
    }

    public EntityType<Z> getConvertedType() {
        return this.convertedType.get();
    }

    @Nullable
    @Override
    public IConvertedCreature<T> createFrom(@NotNull T entity) {
        return SpawnUtil.createEntity(this.convertedType.get(), entity.level(), EntitySpawnReason.CONVERSION).map(convertedCreature -> {
            copyImportantStuff(convertedCreature, entity);
            convertedCreature.setUUID(Mth.createInsecureUUID(convertedCreature.getRandom()));
            convertedCreature.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 2));
            return convertedCreature;
        }).orElse(null);
    }

    protected void copyImportantStuff(@NotNull Z converted, @NotNull T entity) {
        TagValueOutput output = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
        entity.saveWithoutId(output);
        converted.yBodyRot = entity.yBodyRot;
        converted.yHeadRot = entity.yHeadRot;

        var input = TagValueInput.create(ProblemReporter.DISCARDING, entity.registryAccess(), output.buildResult());
        converted.load(input);
        updateEntityAttributes(converted);
        converted.setHealth(converted.getMaxHealth() / 3 * 2);
    }
}
