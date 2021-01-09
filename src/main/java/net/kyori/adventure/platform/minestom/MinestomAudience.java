package net.kyori.adventure.platform.minestom;

import net.kyori.adventure.platform.facet.Facet;
import net.kyori.adventure.platform.facet.FacetAudience;
import net.minestom.server.command.CommandSender;
import net.minestom.server.entity.Player;

import java.util.Collection;

public class MinestomAudience extends FacetAudience<CommandSender> {
    private static final Collection<Facet.Chat<?, ?>> CHAT = Facet.of(
            MinestomFacet.ChatWithType::new,
            MinestomFacet.Chat::new);
    private static final Collection<Facet.ActionBar<? extends Player, ?>> ACTION_BAR = Facet.of(
            MinestomFacet.ActionBar::new);
    private static final Collection<Facet.Title<? extends Player, ?, ?>> TITLE = Facet.of(
            MinestomFacet.Title::new);
    private static final Collection<Facet.Sound<? extends Player, ?>> SOUND = Facet.of(
            MinestomFacet.Sound::new);
    private static final Collection<Facet.TabList<? extends Player, ?>> TAB_LIST = Facet.of(
            MinestomFacet.TabList::new);
    private static final Collection<Facet.Book<? extends Player, ?, ?>> BOOK = Facet.of(
            MinestomFacet.Book::new);

    public MinestomAudience(Collection<CommandSender> viewers) {
        super(viewers, null, CHAT, ACTION_BAR, TITLE, SOUND, BOOK, null, TAB_LIST);
    }
}
