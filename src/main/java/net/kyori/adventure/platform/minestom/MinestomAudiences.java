package net.kyori.adventure.platform.minestom;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.AudienceProvider;
import net.minestom.server.command.CommandSender;
import net.minestom.server.entity.Player;
import net.minestom.server.extensions.Extension;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.function.Predicate;

/**
 * A provider for creating {@link net.kyori.adventure.audience.Audience}s for Minestom.
 *
 * @since 4.3.0
 */
public interface MinestomAudiences extends AudienceProvider {

    static @NonNull MinestomAudiences create(final @NonNull Extension extension) {
        return MinestomAudiencesImpl.instanceFor(extension);
    }

    static @NonNull MinestomAudiences create() {
        return MinestomAudiencesImpl.instanceFor();
    }

    @NonNull Audience sender(final @NonNull CommandSender sender);

    @NonNull Audience player(final @NonNull Player player);

    @NonNull Audience filter(final @NonNull Predicate<CommandSender> filter);
}
