package net.kyori.adventure.platform.minestom;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minestom.server.chat.JsonMessage;
import org.checkerframework.checker.nullness.qual.NonNull;

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
        JsonObject serialized = JsonParser.parseString(GSON_SERIALIZER.serialize(requireNonNull(component, "component"))).getAsJsonObject();
        return new JsonMessage.RawJsonMessage(serialized);
    }
}
