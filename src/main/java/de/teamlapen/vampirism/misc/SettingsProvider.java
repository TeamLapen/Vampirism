package de.teamlapen.vampirism.misc;

import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import de.teamlapen.lib.lib.util.ResourceLocationTypeAdapter;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.settings.ISettingsProvider;
import de.teamlapen.vampirism.api.settings.Supporter;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class SettingsProvider implements ISettingsProvider {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().registerTypeHierarchyAdapter(ResourceLocation.class, new ResourceLocationTypeAdapter()).create();

    private final HttpClient client;
    private final String baseUrl;
    private final String apiVersion;
    private final Map<String, String> settingValues = new HashMap<>();

    public SettingsProvider(String baseUrl, String apiVersion) {
        this.baseUrl = baseUrl;
        this.apiVersion = apiVersion;
        this.client = HttpClient.newHttpClient();
    }

    public void syncSettingsCache() {
        getSettingValuesAsync().handleAsync(this::checkSettings).thenAccept(x -> {
            this.settingValues.clear();
            this.settingValues.putAll(x);
        });
    }

    @Nullable
    public String getSettingsValue(String key) {
        return this.settingValues.get(key);
    }

    public @NotNull Optional<String> getSettingsValueOpt(String key) {
        return Optional.ofNullable(this.settingValues.get(key));
    }

    public boolean isSettingTrue(String key) {
        return "true".equals(this.settingValues.get(key));
    }

    @Override
    public @NotNull CompletableFuture<Collection<Supporter>> getSupportersAsync() {
        return GetSupportersAsync().handleAsync(this::checkSupporter);
    }

    @Nullable
    public CompletableFuture<String> getSettingValueAsync(String key) {
        return get("config/get?configId=" + key);
    }

    public CompletableFuture<Map<String, String>> getSettingValuesAsync() {
        return get("config/list").thenApply(x -> GSON.fromJson(x, TypeToken.getParameterized(Map.class, String.class, String.class).getType()));
    }

    public CompletableFuture<Map<String, String>> getSettingValuesAsync(String modid) {
        return get("config/list?modid=" + modid).thenApply(x -> GSON.fromJson(x, TypeToken.getParameterized(Map.class, String.class, String.class).getType()));
    }

    public CompletableFuture<Collection<Supporter>> GetSupportersAsync() {
        Type type = TypeToken.getParameterized(List.class, Supporter.class).getType();
        return get("supporter/list").thenApply(x -> GSON.fromJson(x, TypeToken.getParameterized(List.class, Supporter.class).getType()));
    }

    public CompletableFuture<Collection<Supporter>> GetSupportersAsync(String modid) {
        return GetSupportersAsync().thenApply(x -> x.stream().filter(y -> y.faction().getNamespace().equals(modid)).collect(Collectors.toList()));
    }

    private CompletableFuture<String> get(String path) {
        return get(this.apiVersion, path);
    }

    private CompletableFuture<String> get(String apiVersion, String path) {
        try {
            var request = HttpRequest.newBuilder(new URI(this.baseUrl + "/" + apiVersion + "/" + path)).GET().build();
            return this.client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body);
        } catch (URISyntaxException e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    private Map<String, String> checkSettings(Map<String, String> settings, Throwable error) {
        if (error != null) {
            LOGGER.error("Failed to retrieve settings from server", error);
        }
        if (VampirismMod.inDev || settings == null) {
            try {
                InputStream inputStream = VampirismMod.class.getResourceAsStream("/config_values.json");
                String data = new String(ByteStreams.toByteArray(inputStream));
                inputStream.close();
                return GSON.fromJson(data, TypeToken.getParameterized(Map.class, String.class, String.class).getType());
            } catch (IOException e) {
                LOGGER.error("Failed to retrieve supporter form resource", e);
                if (settings == null) {
                    return Collections.emptyMap();
                }
            }
        }
        return settings;
    }

    private Collection<Supporter> checkSupporter(Collection<Supporter> file, Throwable error) {
        if (error != null) {
            LOGGER.error("Failed to retrieve supporter from server", error);
        }
        if (VampirismMod.inDev || file == null) {
            try {
                InputStream inputStream = VampirismMod.class.getResourceAsStream("/supporters.json");
                String data = new String(ByteStreams.toByteArray(inputStream));
                inputStream.close();
                return GSON.fromJson(data, TypeToken.getParameterized(List.class, Supporter.class).getType());
            } catch (IOException e) {
                LOGGER.error("Failed to retrieve supporter form resource", e);
                return Collections.emptyList();
            }
        }
        return file;
    }

}
