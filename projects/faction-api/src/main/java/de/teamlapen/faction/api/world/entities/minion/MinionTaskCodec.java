package de.teamlapen.faction.api.world.entities.minion;

import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.*;
import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.util.SafeCast;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class MinionTaskCodec<Z extends IMinionData, I extends IMinionTask<U, Z>, U extends IMinionTask.IMinionTaskDesc<Z>> implements Codec<U> {

    private final InnerCodec inner = new InnerCodec();

    @Override
    public <T> DataResult<Pair<U, T>> decode(DynamicOps<T> ops, T input) {
        DataResult<Pair<Pair<I, U>, T>> result = inner.decode(ops, input);
        return result.map(pair -> Pair.of(pair.getFirst().getSecond(), pair.getSecond()));
    }

    @Override
    public <T> DataResult<T> encode(U input, DynamicOps<T> ops, T prefix) {
        return inner.encode(Pair.of(SafeCast.cast(input.getTask()), input), ops, prefix);
    }

    private class InnerCodec implements Codec<Pair<I, U>> {

        @Override
        public <T> DataResult<Pair<Pair<I, U>, T>> decode(DynamicOps<T> ops, T input) {
            return ops.getList(input).setLifecycle(Lifecycle.stable())
                    .flatMap(stream -> {
                        Decoder<T> tDecoder = new Decoder<>(ops);
                        stream.accept(tDecoder::accept);
                        return tDecoder.getResult();
                    });
        }

        private class Decoder<T> {
            private static final DataResult<Unit> INITIAL_RESULT = DataResult.success(Unit.INSTANCE, Lifecycle.stable());

            private final DynamicOps<T> ops;
            private final Stream.Builder<T> failed = Stream.builder();
            private DataResult<Unit> result = INITIAL_RESULT;

            @Nullable
            private I task;
            @Nullable
            private U desc;

            public Decoder(DynamicOps<T> ops) {
                this.ops = ops;
            }

            public void accept(T d) {
                if (this.task != null && this.desc != null) {
                    this.failed.add(d);
                    return;
                }
                if (this.task == null) {
                    final var taskDecode = FactionRegistries.MINION_TASK.get().byNameCodec().decode(ops, d);
                    taskDecode.error().ifPresent(x -> failed.add(d));
                    taskDecode.resultOrPartial().ifPresent(x -> this.task = SafeCast.cast(x.getFirst()));
                    this.result = this.result.apply2stable((result, element) -> result, taskDecode);
                } else {
                    DataResult<? extends Pair<?, T>> decode1 = this.task.descriptionCodec().decode(this.ops, d);
                    decode1.error().ifPresent(x -> this.failed.add(d));
                    decode1.resultOrPartial().ifPresent(x -> this.desc = SafeCast.cast(x.getFirst()));
                    this.result = this.result.apply2stable((result, element) -> result, decode1);

                }
            }

            public DataResult<Pair<Pair<I, U>,T>> getResult() {
                if (task == null) {
                    return DataResult.error(() -> "Task could not be deserialized");
                }
                if (desc == null) {
                    return DataResult.error(() -> "Description could not be deserialized");
                }

                T errors = ops.createList(failed.build());
                Pair<Pair<I,U>, T> pairTPair = Pair.of(Pair.of(task, desc), errors);
                return result.map(ignored -> pairTPair).setPartial(pairTPair);
            }
        }

        @Override
        public <T> DataResult<T> encode(Pair<I, U> input, DynamicOps<T> ops, T prefix) {
            final ListBuilder<T> builder = ops.listBuilder();
            builder.add(FactionRegistries.MINION_TASK.get().byNameCodec().encode(input.getFirst(), ops, prefix));
            builder.add(input.getFirst().descriptionCodec().encode(input.getSecond(), ops, prefix));
            return builder.build(prefix);
        }
    }
}