package de.teamlapen.sync.properties;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

public record DataApply<T>(@Nullable Consumer<T> serverFunction, @Nullable Function<T, Boolean> clientFunction) {


    static <T> DataApply<T> create(@Nullable Consumer<T> serverFunction, @Nullable Function<T, Boolean> clientFunction) {
        Preconditions.checkArgument(serverFunction != null || clientFunction != null, "Server or client function must be set");
        return new DataApply<>(serverFunction, clientFunction);
    }


    public void loadServer(T data) {
        if (this.serverFunction == null) return;
        this.serverFunction.accept(data);
    }

    public boolean loadClient(T data) {
        if (this.clientFunction == null) return false;
        return this.clientFunction.apply(data);
    }

    public boolean canLoadServer() {
        return this.serverFunction != null;
    }

    public boolean canLoadClient() {
        return this.clientFunction != null;
    }
}
