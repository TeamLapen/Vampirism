package de.teamlapen.vampirism.misc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import de.teamlapen.lib.lib.util.ResourceLocationTypeAdapter;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.settings.ISettingsProvider;
import de.teamlapen.vampirism.api.settings.Supporter;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Flow;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SettingsProvider implements ISettingsProvider {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().registerTypeHierarchyAdapter(ResourceLocation.class, new ResourceLocationTypeAdapter()).create();

    private final HttpClient client;
    private final String baseUrl;
    private final Map<String, String> settingValues = new HashMap<>();

    public SettingsProvider(String baseUrl) {
        this.baseUrl = baseUrl;
        this.client = HttpClient.newHttpClient();
    }

    @Override
    public void syncSettingsCache() {
        retrieveSettingValuesAsync().handleAsync(this::checkSettings).thenAccept(newValues -> {
            this.settingValues.clear();
            newValues.ifPresent(this.settingValues::putAll);
        });
    }

    @Override
    public @NotNull Optional<String> getSettingsValue(@NotNull String key) {
        return Optional.ofNullable(this.settingValues.get(key));
    }

    @Override
    public boolean isSettingTrue(@NotNull String key) {
        return "true".equals(this.settingValues.get(key));
    }

    @Override
    public @NotNull CompletableFuture<Optional<Collection<Supporter>>> getSupportersAsync() {
        return retrieveSupportersAsync().handleAsync(this::checkSupporter);
    }

    @Nullable
    public CompletableFuture<String> getSettingValueAsync(String key) {
        return get("config/get?configId=" + key);
    }

    public CompletableFuture<Map<String, String>> retrieveSettingValuesAsync() {
        return get("config/list").thenApply(x -> GSON.fromJson(x, TypeToken.getParameterized(Map.class, String.class, String.class).getType()));
    }

    public CompletableFuture<Map<String, String>> retrieveSettingValuesAsync(String modid) {
        return get("config/list?modid=" + modid).thenApply(x -> GSON.fromJson(x, TypeToken.getParameterized(Map.class, String.class, String.class).getType()));
    }

    public CompletableFuture<Collection<Supporter>> retrieveSupportersAsync() {
        return get("supporter/list").thenApply(x -> GSON.fromJson(x, TypeToken.getParameterized(List.class, Supporter.class).getType()));
    }

    public CompletableFuture<Collection<Supporter>> retrieveSupportersAsync(String modid) {
        return retrieveSupportersAsync().thenApply(x -> x.stream().filter(y -> y.faction().getNamespace().equals(modid)).collect(Collectors.toList()));
    }

    private CompletableFuture<String> get(String path) {
        try {
            var request = HttpRequest.newBuilder(new URI(this.baseUrl + "/" + path)).GET().build();
            return this.client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body);
        } catch (URISyntaxException e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    private Optional<Map<String, String>> checkSettings(Map<String, String> settings, Throwable error) {
        if (error != null) {
            LOGGER.error("Failed to retrieve settings from server", error);
        }
        if (false) {
            InputStream inputStream = VampirismMod.class.getResourceAsStream("/default_remote_config.json");
            if (inputStream != null) {
                try {
                    return Optional.of(GSON.fromJson(new JsonReader(new InputStreamReader(inputStream)), TypeToken.getParameterized(Map.class, String.class, String.class).getType()));
                } catch (JsonSyntaxException ex) {
                    LOGGER.error("Failed to retrieve settings from file", ex);
                }
            }
        }
        return Optional.ofNullable(settings);
    }

    private Optional<Collection<Supporter>> checkSupporter(Collection<Supporter> file, Throwable error) {
        if (error != null) {
            LOGGER.error("Failed to retrieve supporter from server", error);
        }
        if (false) {
                InputStream inputStream = VampirismMod.class.getResourceAsStream("/supporters.json");
                if (inputStream != null) {
                    try {
                        return Optional.of(GSON.<Supporter.OldList>fromJson(new JsonReader(new InputStreamReader(inputStream)), TypeToken.get(Supporter.OldList.class).getType()).toNew());
                    } catch (JsonSyntaxException ex) {
                        LOGGER.error("Failed to retrieve supporter from file", ex);
                    }
                }
        }
        return Optional.ofNullable(file);
    }

}
