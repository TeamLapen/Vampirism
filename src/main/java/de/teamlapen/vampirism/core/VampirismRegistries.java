package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;

/**
 * 1.12
 *
 * @author maxanier
 */
public class VampirismRegistries {
    public static final ResourceLocation SKILLS_ID = new ResourceLocation("vampirism:skills");
    public static final ResourceLocation ACTIONS_ID = new ResourceLocation("vampirism:actions");
    public static final ResourceLocation HUNTER_WEAPON_RECIPE_ID = new ResourceLocation("vampirism:hunter_weapon_recipe");

    public static final IForgeRegistry<ISkill> SKILLS;
    public static final IForgeRegistry<IAction> ACTIONS;

    static {
        SKILLS = makeRegistry(SKILLS_ID, ISkill.class, Integer.MAX_VALUE >> 5);
        ACTIONS = makeRegistry(ACTIONS_ID, IAction.class, Integer.MAX_VALUE >> 5);
    }

    private static <T extends IForgeRegistryEntry<T>> IForgeRegistry<T> makeRegistry(ResourceLocation name, Class<T> type, int max) {
        return new RegistryBuilder<T>().setName(name).setType(type).setMaxID(max).create();
    }
}
