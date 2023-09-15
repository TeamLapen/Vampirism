package de.teamlapen.vampirism.entity.converted.converter;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.entity.convertible.Converter;
import de.teamlapen.vampirism.api.entity.convertible.IConvertedCreature;
import de.teamlapen.vampirism.api.entity.convertible.IConvertingHandler;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.data.reloadlistener.ConvertiblesReloadListener;
import de.teamlapen.vampirism.entity.converted.SpecialConvertingHandler;
import de.teamlapen.vampirism.entity.converted.VampirismEntityRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

public class SpecialConverter<T extends PathfinderMob, Z extends PathfinderMob & IConvertedCreature<T>> implements Converter {

    public static final Codec<SpecialConverter<?, ?>> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ForgeRegistries.ENTITY_TYPES.getCodec().fieldOf("converted_type").forGetter(i -> i.convertedType),
            ConvertiblesReloadListener.EntityEntry.ConvertingAttributeModifier.CODEC.optionalFieldOf("attribute_helper").forGetter(i -> Optional.ofNullable(i.helper))
    ).apply(instance, SpecialConverter::new));

    private final EntityType<Z> convertedType;
    private final ConvertiblesReloadListener.EntityEntry.ConvertingAttributeModifier helper;

    @SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "unchecked"})
    private SpecialConverter(EntityType<?> convertedType, Optional<ConvertiblesReloadListener.EntityEntry.ConvertingAttributeModifier> helper) {
        this.convertedType = (EntityType<Z>) convertedType;
        this.helper = helper.orElse(ConvertiblesReloadListener.EntityEntry.ConvertingAttributeModifier.DEFAULT);
    }

    public SpecialConverter(Supplier<? extends EntityType<Z>> convertedType, ConvertiblesReloadListener.EntityEntry.ConvertingAttributeModifier helper) {
        this.convertedType = convertedType.get();
        this.helper = helper;
    }

    public SpecialConverter(Supplier<? extends EntityType<Z>> convertedType) {
        this.convertedType = convertedType.get();
        this.helper = null;
    }

    @Override
    public IConvertingHandler<?> createHandler() {
        return new SpecialConvertingHandler<>(() -> this.convertedType, null, new VampirismEntityRegistry.DatapackHelper(this.helper));
    }

    @Override
    public IConvertingHandler<?> createHandler(@Nullable ResourceLocation texture) {
        return new SpecialConvertingHandler<>(() -> this.convertedType, texture, new VampirismEntityRegistry.DatapackHelper(this.helper));
    }

    @Override
    public Codec<? extends Converter> codec() {
        return ModEntities.SPECIAL_CONVERTER.get();
    }
}
