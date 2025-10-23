package org.example.optional;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import java.util.Objects;

public abstract class SimpleOptional<T> {

    @SuppressWarnings("rawtypes")
    private static final SimpleOptional ABSENT = new Absent();

    protected SimpleOptional() {}

    public static <T> SimpleOptional<T> fromNullable(T nullableReference) {
        return (nullableReference == null)
                ? SimpleOptional.<T>absent()
                : new Present<>(nullableReference);
    }

    public static <T> SimpleOptional<T> of(T reference) {
        return new Present<>(Objects.requireNonNull(reference));
    }

    @SuppressWarnings("unchecked")
    public static <T> SimpleOptional<T> absent() {
        return (SimpleOptional<T>) ABSENT;
    }

    public abstract boolean isPresent();
    public abstract T get();
    public abstract T or(T defaultValue);
    public abstract T or(Supplier<? extends T> supplier);
    public abstract T orNull();
    public abstract <V> SimpleOptional<V> transform(Function<? super T, V> function);

    private static final class Present<T> extends SimpleOptional<T> {
        private final T reference;

        Present(T reference) {
            this.reference = reference;
        }

        @Override
        public boolean isPresent() {
            return true;
        }

        @Override
        public T get() {
            return reference;
        }

        @Override
        public T or(T defaultValue) {
            Objects.requireNonNull(defaultValue, "use orNull() instead of or(null)");
            return reference;
        }

        @Override
        public T or(Supplier<? extends T> supplier) {
            Objects.requireNonNull(supplier);
            return reference;
        }

        @Override
        public T orNull() {
            return reference;
        }

        @Override
        public <V> SimpleOptional<V> transform(Function<? super T, V> function) {
            return fromNullable(
                    Objects.requireNonNull(function, "transform function cannot be null")
                            .apply(reference)
            );
        }

        @Override
        public boolean equals(Object object) {
            if (object instanceof Present) {
                Present<?> other = (Present<?>) object;
                return reference.equals(other.reference);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return 0x598df91c + reference.hashCode();
        }

        @Override
        public String toString() {
            return "Optional.of(" + reference + ")";
        }
    }

    private static final class Absent extends SimpleOptional<Object> {

        @Override
        public boolean isPresent() {
            return false;
        }

        @Override
        public Object get() {
            throw new IllegalStateException("Optional.get() cannot be called on an absent value");
        }

        @Override
        public Object or(Object defaultValue) {
            return Objects.requireNonNull(defaultValue, "use orNull() instead of or(null)");
        }

        @Override
        public Object or(Supplier<?> supplier) {
            return Objects.requireNonNull(
                    Objects.requireNonNull(supplier, "supplier cannot be null").get(),
                    "use orNull() instead of a supplier that returns null"
            );
        }

        @Override
        public Object orNull() {
            return null;
        }

        @Override
        public <V> SimpleOptional<V> transform(Function<? super Object, V> function) {
            Objects.requireNonNull(function);
            return absent();
        }

        @Override
        public boolean equals(Object object) {
            return object == this;
        }

        @Override
        public int hashCode() {
            return 0x79a31aac;
        }

        @Override
        public String toString() {
            return "Optional.absent()";
        }
    }
}