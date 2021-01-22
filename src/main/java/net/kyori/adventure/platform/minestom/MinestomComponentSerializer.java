package net.kyori.adventure.platform.minestom;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minestom.server.chat.JsonMessage;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

public final class MinestomComponentSerializer implements ComponentSerializer<Component, Component, JsonMessage> {
    private static final MinestomComponentSerializer INSTANCE = new MinestomComponentSerializer();
    private static final GsonComponentSerializer GSON_SERIALIZER = GsonComponentSerializer.builder().build();

    public static @NonNull MinestomComponentSerializer get() {
        return INSTANCE;
    }

    private MinestomComponentSerializer() {}

    @Override
    public @NonNull Component deserialize(@NonNull JsonMessage input) {
        return GSON_SERIALIZER.deserialize(requireNonNull(input.toString()));
    }

    @Override
    public @NonNull JsonMessage serialize(@NonNull Component component) {
        return new LazyParseJsonMessage(GSON_SERIALIZER.serialize(requireNonNull(component, "component")));
    }

    private static class LazyParseJsonMessage extends JsonMessage {
        private final String rawJson;
        private JsonObject json = null;

        public LazyParseJsonMessage(@NotNull String rawJson) {
            this.rawJson = rawJson;
        }

        @Override
        public @NotNull JsonObject getJsonObject() {
            if (this.json == null)
                this.json = JsonParser.parseString(this.rawJson).getAsJsonObject();
            return this.json;
        }


        @Override
        public @NotNull String toString() {
            return rawJson;
        }
    }
}
