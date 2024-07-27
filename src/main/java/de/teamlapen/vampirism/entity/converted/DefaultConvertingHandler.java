package de.teamlapen.vampirism.entity.converted;

import de.teamlapen.vampirism.api.entity.convertible.IConvertedCreature;
import de.teamlapen.vampirism.api.entity.convertible.IConvertingHandler;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.datamaps.ConverterEntry;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Default converting handler for entities.
 * Used for some vanilla entities, but can also be used for third party entities.
 * Converts the entity into a {@link ConvertedCreatureEntity}
 */
public class DefaultConvertingHandler<T extends PathfinderMob> implements IConvertingHandler<T> {

    private static final Logger LOGGER = LogManager.getLogger();

    protected final @NotNull IDefaultHelper helper;
    protected final @Nullable ResourceLocation overlayTexture;

    /**
     * @param helper If null a default one will be used
     */
    public DefaultConvertingHandler(@Nullable IDefaultHelper helper, @Nullable ResourceLocation overlayTexture) {
        this.helper = Objects.requireNonNullElse(helper, new VampirismEntityRegistry.DefaultHelper(ConverterEntry.ConvertingAttributeModifier.DEFAULT));
        this.overlayTexture = overlayTexture;
    }

    @Nullable
    @Override
    public IConvertedCreature<T> createFrom(@NotNull T entity) {
        //noinspection unchecked
        return Helper.createEntity((EntityType<ConvertedCreatureEntity<T>>) (Object) ModEntities.CONVERTED_CREATURE.get(), entity.getCommandSenderWorld()).map(convertedCreature -> {
            copyImportantStuff(convertedCreature, entity);
            convertedCreature.setUUID(Mth.createInsecureUUID(convertedCreature.getRandom())); //Set a new uuid to avoid confusion as the class of the entity associated with the uuid changes
            convertedCreature.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 2));
            convertedCreature.getSourceEntityDataParamOpt().ifPresent(s -> convertedCreature.asEntity().getEntityData().set(s, BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString()));
            return convertedCreature;
        }).orElse(null);
    }

    /**
     * @return The helper for this handler
     */
    public @NotNull IDefaultHelper getHelper() {
        return helper;
    }

    protected void copyImportantStuff(@NotNull ConvertedCreatureEntity<T> converted, @NotNull T entity) {
        converted.copyPosition(entity);
        converted.setEntityCreature(entity);
        updateEntityAttributes(converted);
        converted.setHealth(converted.getMaxHealth() / 3 * 2);
        converted.yBodyRot = entity.yBodyRot;
        converted.yHeadRot = entity.yHeadRot;
    }

    @SuppressWarnings({"unchecked", "DataFlowIssue"})
    @Override
    public void updateEntityAttributes(PathfinderMob creature) {
        try {
            helper.getAttributeModifier().forEach(((attribute, valueProvider) -> {
                AttributeSupplier supplier = DefaultAttributes.getSupplier((EntityType<? extends PathfinderMob>) creature.getType());
                if (supplier.hasAttribute(attribute)) {
                    creature.getAttribute(attribute).setBaseValue(supplier.getBaseValue(attribute) * valueProvider.getFirst().sample(creature.getRandom()));
                }
            }));
        } catch (NullPointerException ex) {
            LOGGER.error("Failed to update entity attributes for {} {}", creature, ex);
        }
    }
}
