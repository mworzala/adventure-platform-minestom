package net.kyori.adventure.platform.minestom;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.facet.FacetAudienceProvider;
import net.kyori.adventure.platform.facet.Knob;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.ConsoleSender;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.extensions.Extension;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static java.util.Objects.requireNonNull;

public class MinestomAudiencesImpl extends FacetAudienceProvider<CommandSender, MinestomAudience> implements MinestomAudiences {
    static {
        final Logger logger = LoggerFactory.getLogger(MinestomAudiences.class);
        Knob.OUT = logger::debug;
        Knob.ERR = logger::warn;
    }

    private static final Map<String, MinestomAudiences> INSTANCES = Collections.synchronizedMap(new HashMap<>(4));

    static MinestomAudiences instanceFor() {
        return INSTANCES.computeIfAbsent("default", id -> new MinestomAudiencesImpl(id));
    }

    static MinestomAudiences instanceFor(final @NonNull Extension extension) {
        requireNonNull(extension, "extension");
        return INSTANCES.computeIfAbsent(extension.getDescription().getName(), MinestomAudiencesImpl::new);
    }

    private final String name;

    MinestomAudiencesImpl(final @NonNull String name) {
        this.name = name;

        final CommandSender console = MinecraftServer.getCommandManager().getConsoleSender();
        this.addViewer(console);
        this.changeViewer(console, Locale.getDefault());

        for (final Player player : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
            this.addViewer(player);
        }

        GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();
        eventHandler.addEventCallback(PlayerLoginEvent.class, event -> this.addViewer(event.getPlayer()));
        eventHandler.addEventCallback(PlayerDisconnectEvent.class, event -> this.removeViewer(event.getPlayer()));
        //todo PlayerLocaleChangeEvent
    }

    @NonNull
    @Override
    public Audience sender(final @NonNull CommandSender sender) {
        if (sender instanceof Player) {
            return this.player((Player) sender);
        } else if (sender instanceof ConsoleSender) {
            return this.console();
        } else if (sender instanceof Entity) {
            return Audience.empty();
        }
        return this.createAudience(Collections.singletonList(sender));
    }

    @NonNull
    @Override
    public Audience player(@NonNull Player player) {
        return this.player(player.getUuid());
    }

    @Override
    protected @Nullable UUID hasId(@NonNull CommandSender viewer) {
        if (viewer instanceof Player) {
            return ((Player) viewer).getUuid();
        }
        return null;
    }

    @Override
    protected boolean isConsole(@NonNull CommandSender viewer) {
        return viewer instanceof ConsoleSender;
    }

    @Override
    protected boolean hasPermission(@NonNull CommandSender viewer, @NonNull String permission) {
        return viewer.hasPermission(permission);
    }

    @Override
    protected boolean isInWorld(@NonNull CommandSender viewer, @NonNull Key world) {
        if (viewer instanceof Player) {
            return Objects.requireNonNull(((Player) viewer).getInstance()).getUniqueId().toString().equals(world.value());
        }
        return false;
    }

    @Override
    protected boolean isOnServer(@NonNull CommandSender viewer, @NonNull String server) {
        return false;
    }

    @Override
    protected @NonNull MinestomAudience createAudience(@NonNull Collection<CommandSender> viewers) {
        return new MinestomAudience(viewers);
    }
}
