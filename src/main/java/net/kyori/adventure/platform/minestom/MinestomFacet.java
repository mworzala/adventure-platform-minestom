package net.kyori.adventure.platform.minestom;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.facet.Facet;
import net.kyori.adventure.platform.facet.FacetBase;
import net.kyori.adventure.sound.SoundStop;
import net.kyori.adventure.text.Component;
import net.minestom.server.chat.JsonMessage;
import net.minestom.server.command.CommandSender;
import net.minestom.server.entity.Player;
import net.minestom.server.item.metadata.WrittenBookMeta;
import net.minestom.server.network.packet.server.play.ChatMessagePacket;
import net.minestom.server.registry.Registries;
import net.minestom.server.sound.SoundCategory;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.StreamSupport;

import static net.kyori.adventure.platform.facet.Knob.logUnsupported;
import static net.kyori.adventure.platform.minestom.MinestomComponentSerializer.get;

public class MinestomFacet<V> extends FacetBase<V> {
    protected MinestomFacet(final @Nullable Class<? extends V> viewerClass) {
        super(viewerClass);
    }

    static class Message<V> extends MinestomFacet<V> implements Facet.Message<V, JsonMessage> {
        protected Message(final @Nullable Class<? extends V> viewerClass) {
            super(viewerClass);
        }

        @Override
        public @Nullable JsonMessage createMessage(@NonNull V viewer, @NonNull Component message) {
            return get().serialize(message);
        }
    }

    static class Chat extends Message<CommandSender> implements Facet.Chat<CommandSender, JsonMessage> {
        protected Chat() {
            super(CommandSender.class);
        }

        @Override
        public void sendMessage(@NonNull CommandSender viewer, @NonNull Identity source, @NonNull JsonMessage message, @NonNull MessageType type) {
            viewer.sendMessage(message);
        }
    }

    static class ChatWithType extends Message<CommandSender> implements Facet.Chat<CommandSender, JsonMessage> {
        protected ChatWithType() {
            super(CommandSender.class);
        }

        @Override
        public void sendMessage(@NonNull CommandSender viewer, @NonNull Identity source, @NonNull JsonMessage message, @NonNull MessageType type) {
            if (type == MessageType.CHAT)
                viewer.sendMessage(message);
            else if (type == MessageType.SYSTEM) {
                if (viewer.isConsole()) {
                    viewer.sendMessage(message.getRawMessage());
                } else {
                    ChatMessagePacket packet = new ChatMessagePacket(message.toString(), ChatMessagePacket.Position.SYSTEM_MESSAGE);
                    viewer.asPlayer().getPlayerConnection().sendPacket(packet);
                }
            } else logUnsupported(this, type);
        }
    }

    static class ActionBar extends Message<Player> implements Facet.ActionBar<Player, JsonMessage> {
        protected ActionBar() {
            super(Player.class);
        }

        @Override
        public void sendMessage(@NonNull Player viewer, @NonNull JsonMessage message) {
            viewer.sendActionBarMessage(message);
        }
    }

    static class Title extends Message<Player> implements Facet.Title<Player, JsonMessage, net.kyori.adventure.platform.minestom.Title> {
        protected Title() {
            super(Player.class);
        }

        @Override
        public @Nullable net.kyori.adventure.platform.minestom.Title createTitle(@Nullable JsonMessage title, @Nullable JsonMessage subTitle, int inTicks, int stayTicks, int outTicks) {
            final net.kyori.adventure.platform.minestom.Title.Builder builder = new net.kyori.adventure.platform.minestom.Title.Builder();

            if(title != null) builder.title(title);
            if(subTitle != null) builder.subtitle(subTitle);
            if(inTicks > -1) builder.fadeIn(inTicks);
            if(stayTicks > -1) builder.stay(stayTicks);
            if(outTicks > -1) builder.fadeOut(outTicks);

            return builder.build();
        }

        @Override
        public void showTitle(@NonNull Player viewer, net.kyori.adventure.platform.minestom.Title title) {
            title.sendTo(viewer);
        }

        @Override
        public void clearTitle(@NonNull Player viewer) {
            viewer.hideTitle();
        }

        @Override
        public void resetTitle(@NonNull Player viewer) {
            viewer.resetTitle();
        }
    }

    static class Position extends MinestomFacet<Player> implements Facet.Position<Player, net.minestom.server.utils.Position> {
        protected Position() {
            super(Player.class);
        }

        @Override
        public boolean isApplicable(@NonNull Player viewer) {
            return viewer.getInstance() != null;
        }

        @Nullable
        @Override
        public net.minestom.server.utils.Position createPosition(@NonNull Player viewer) {
            return viewer.getPosition();
        }

        @NotNull
        @Override
        public net.minestom.server.utils.Position createPosition(double x, double y, double z) {
            return new net.minestom.server.utils.Position((float) x, (float) y, (float) z);
        }
    }

    static class Sound extends Position implements Facet.Sound<Player, net.minestom.server.utils.Position> {

        @Override
        public void playSound(@NonNull Player viewer, net.kyori.adventure.sound.@NonNull Sound sound, net.minestom.server.utils.@NonNull Position position) {
            final net.minestom.server.sound.Sound type = this.type(sound.name());
            SoundCategory category = this.category(sound.source());
            if (category == null) category = SoundCategory.MASTER;

            if (type != null) {
                viewer.playSound(type, category, (int) position.getX(), (int) position.getY(), (int) position.getZ(), sound.volume(), sound.pitch());
            } else {
                viewer.playSound(sound.source().name(), category, (int) position.getX(), (int) position.getY(), (int) position.getZ(), sound.volume(), sound.pitch());
            }
        }

        @Override
        public void stopSound(@NonNull Player viewer, @NonNull SoundStop sound) {
            // Minestom does not currently support stopping individual sounds or categories.
            viewer.stopSound();
        }

        public @Nullable net.minestom.server.sound.Sound type(final @Nullable Key sound) {
            if (sound == null) return null;
            return Registries.getSound(sound.value());
        }

        public @Nullable SoundCategory category(final net.kyori.adventure.sound.Sound.Source source) {
            if (source == null) return null;
            switch (source) {
                case MASTER:
                    return SoundCategory.MASTER;
                case MUSIC:
                    return SoundCategory.MUSIC;
                case RECORD:
                    return SoundCategory.RECORDS;
                case WEATHER:
                    return SoundCategory.WEATHER;
                case BLOCK:
                    return SoundCategory.BLOCKS;
                case HOSTILE:
                    return SoundCategory.HOSTILE;
                case NEUTRAL:
                    return SoundCategory.NEUTRAL;
                case PLAYER:
                    return SoundCategory.PLAYERS;
                case AMBIENT:
                    return SoundCategory.AMBIENT;
                case VOICE:
                    return SoundCategory.VOICE;
                default:
                    return null;
            }
        }
    }

    static class Book extends Message<Player> implements Facet.Book<Player, JsonMessage, WrittenBookMeta> {
        protected Book() {
            super(Player.class);
        }

        @Override
        public @Nullable WrittenBookMeta createBook(@NonNull JsonMessage title, @NonNull JsonMessage author, @NonNull Iterable<JsonMessage> pages) {
            final WrittenBookMeta bookMeta = new WrittenBookMeta();
            bookMeta.setTitle(title.getRawMessage());
            bookMeta.setAuthor(title.getRawMessage());
            bookMeta.setPages(ImmutableList.copyOf(pages));
            return bookMeta;
        }

        @Override
        public void openBook(@NonNull Player viewer, @NonNull WrittenBookMeta book) {
            viewer.openBook(book);
        }
    }

//    static class BossBarBuilder extends MinestomFacet<Player> implements Facet.BossBar.Builder<Player, MinestomFacet.BossBar> {
//        protected BossBarBuilder() {
//            super(Player.class);
//        }
//
//        @NotNull
//        @Override
//        public MinestomFacet.BossBar createBossBar(@NonNull Collection<Player> viewers) {
//            return new BossBar(viewers);
//        }
//    }

    // todo Boss bar

    static class TabList extends Message<Player> implements Facet.TabList<Player, JsonMessage> {
        protected TabList() {
            super(Player.class);
        }

        @Override
        public void send(final Player player, final @Nullable JsonMessage header, final @Nullable JsonMessage footer) {
            player.sendHeaderFooter(header, footer);
        }
    }

}
