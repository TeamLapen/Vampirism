package de.teamlapen.vampirism.player.skills;

import com.google.gson.*;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.network.SSkillTreePacket;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;


public class SkillTreeManager extends JsonReloadListener {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().registerTypeHierarchyAdapter(SkillNode.Builder.class, (JsonDeserializer<SkillNode.Builder>) (json, typeOfT, context) -> {
        JsonObject asObject = JSONUtils.convertToJsonObject(json, "skillnode");
        return SkillNode.Builder.deserialize(asObject, context);
    }).create();
    private static SkillTreeManager instance;

    public static SkillTreeManager getInstance() {
        if (instance == null) {
            instance = new SkillTreeManager();
        }
        return instance;
    }

    private final SkillTree skillTree = new SkillTree();

    private SkillTreeManager() {
        super(GSON, "vampirismskillnodes");
    }

    public SkillTree getSkillTree() {
        return skillTree;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resourceLocationJsonObjectMap, @Nonnull IResourceManager iResourceManager, @Nonnull IProfiler iProfiler) {
        Map<ResourceLocation, SkillNode.Builder> parsed = new HashMap<>();
        resourceLocationJsonObjectMap.forEach((id, object) -> {
            try {
                SkillNode.Builder builder = GSON.fromJson(object, SkillNode.Builder.class);
                if (builder != null) {
                    parsed.put(id, builder);
                }
            } catch (IllegalArgumentException | JsonParseException e) {
                LOGGER.error("Failed to load skill node {}: {}", id, e.getMessage());
            }
        });

        skillTree.loadNodes(parsed);
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) { //On first pack load server will be null, so sending package crashes
            VampirismMod.dispatcher.sendToAll(new SSkillTreePacket(skillTree.getCopy()));
        }
    }


}
