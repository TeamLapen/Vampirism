package de.teamlapen.vampirism.data.remote;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import de.teamlapen.faction.common.util.serialization.IdentifierTypeAdapter;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.settings.ISettingsProvider;
import de.teamlapen.vampirism.api.settings.Supporter;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
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
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class SettingsProvider implements ISettingsProvider {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().registerTypeHierarchyAdapter(Identifier.class, new IdentifierTypeAdapter()).registerTypeHierarchyAdapter(Supporter.class, new SupporterDeserializer()).create();

    private final HttpClient client;
    private final String baseUrl;

    public SettingsProvider(String baseUrl) {
        this.baseUrl = baseUrl;
        this.client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).executor(Util.ioPool()).build();
    }

    @Override
    public @NotNull CompletableFuture<Optional<Collection<Supporter>>> getSupportersAsync() {
        return retrieveSupportersAsync().handleAsync(this::checkSupporter);
    }

    public CompletableFuture<Collection<Supporter>> retrieveSupportersAsync() {
        return get("supporter/list").thenApplyAsync(x -> GSON.fromJson(x, TypeToken.getParameterized(List.class, Supporter.class).getType()), Util.backgroundExecutor());
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

    private Optional<Collection<Supporter>> checkSupporter(Collection<Supporter> file, Throwable error) {
        if (error != null) {
            LOGGER.error("Failed to retrieve supporter from server", error);
        }
        if (VampirismMod.inDev || file == null) {
                InputStream inputStream = VampirismMod.class.getResourceAsStream("/supporters.json");
                if (inputStream != null) {
                    try {
                        List<Supporter> list = GSON.fromJson(new JsonReader(new InputStreamReader(inputStream)), TypeToken.getParameterized(List.class, Supporter.class).getType());
                        return Optional.of(list);
                    } catch (JsonSyntaxException ex) {
                        LOGGER.error("Failed to retrieve supporter from file", ex);
                    }
                }
        }
        return Optional.ofNullable(file);
    }

}
