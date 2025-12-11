package de.teamlapen.sync;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

public interface DataApply< T> {

    void loadServer(T data);

    boolean loadClient(T data);

    boolean canLoadServer();

    boolean canLoadClient();

    static <T> DataApply<T> create(@Nullable Consumer<T> serverFunction, @Nullable Function<T, Boolean> clientFunction) {
        Preconditions.checkArgument(serverFunction != null || clientFunction != null, "Server or client function must be set");
        return new Default<>(serverFunction, clientFunction);
    }

    record Default<T>(@Nullable Consumer<T> serverFunction, @Nullable Function<T, Boolean> clientFunction) implements DataApply<T> {


        @Override
        public void loadServer(T data) {
            if (this.serverFunction == null) return;
            this.serverFunction.accept(data);
        }

        @Override
        public boolean loadClient(T data) {
            if (this.clientFunction == null) return false;
            return this.clientFunction.apply(data);
        }

        @Override
        public boolean canLoadServer() {
            return this.serverFunction != null;
        }

        @Override
        public boolean canLoadClient() {
            return this.clientFunction != null;
        }
    }
}
