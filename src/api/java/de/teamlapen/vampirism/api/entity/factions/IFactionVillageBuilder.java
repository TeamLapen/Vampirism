package de.teamlapen.vampirism.api.entity.factions;

import de.teamlapen.vampirism.api.entity.CaptureEntityEntry;
import de.teamlapen.vampirism.api.entity.ITaskMasterEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;

import java.util.List;
import java.util.function.Supplier;

/**
 * Builder for faction village related attributes.
 * <br>
 * used for {@link IFactionBuilder}
 *
 * @apiNote supplier are invoked in {@link net.minecraftforge.fml.event.lifecycle.InterModProcessEvent}
 */
public interface IFactionVillageBuilder {

    /**
     * Supply an effect that can trigger faction raids
     *
     * @param badOmenEffect bad omen effect
     * @return this builder
     */
    IFactionVillageBuilder badOmenEffect(Supplier<Effect> badOmenEffect);

    /**
     * Supply a banner item that is equipped by entities to add a faction bad omen effect to the killer
     * @param bannerItem the banner itemstack
     * @return this builder
     */
    IFactionVillageBuilder banner(Supplier<ItemStack> bannerItem);

    /**
     * Supply a list of {@link CaptureEntityEntry} for the faction that can participate in faction raids
     *
     * @param captureEntities entry list
     * @return this builder
     *
     * @apiNote the entries should only contain entities of this faction
     */
    IFactionVillageBuilder captureEntities(Supplier<List<CaptureEntityEntry>> captureEntities);

    /**
     * Supply a faction village profession that should have the totem top as working station
     *
     * @param profession the profession
     * @return the builder
     */
    IFactionVillageBuilder factionVillagerProfession(Supplier<VillagerProfession> profession);

    /**
     * Set the superclass for all faction entities in a village
     * <br>
     * Is used for targeting
     *
     * @param clazz the super class
     * @return the builder
     */
    IFactionVillageBuilder guardSuperClass(Class<? extends MobEntity> clazz);

    /**
     * Supply a taskmaster for this faction
     *
     * @param taskmaster taskmaster entity type
     * @return this builder
     */
    IFactionVillageBuilder taskMaster(Supplier<EntityType<? extends ITaskMasterEntity>> taskmaster);

    /**
     * Supply totem top blocks for this faction
     *
     * @param fragile the totem top for world generation
     * @param crafted the totem top for crafting
     * @return this builder
     */
    IFactionVillageBuilder totem(Supplier<Block> fragile, Supplier<Block> crafted);
}
