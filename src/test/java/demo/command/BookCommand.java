package demo.command;

import demo.MainDemo;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Arguments;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;

public class BookCommand extends Command {
    private final Component PAGE_1 = Component
            .text("Page one")
            .hoverEvent(HoverEvent
                    .showText(Component.text("With a hover event")))
            .color(TextColor.color(0x00FFAA));
    private final Component PAGE_2 = Component.text("Page two");
    private final Component PAGE_3 = Component.text("Page three");

    public BookCommand() {
        super("book");

        setDefaultExecutor(this::execute);
    }

    private void execute(CommandSender sender, Arguments args) {
        if (!sender.isPlayer()) {
            sender.sendMessage("Command only for players!");
            return;
        }
        Player player = sender.asPlayer();
        Audience audience = MainDemo.audiences.player(player);

        audience.openBook(Book.builder().pages(PAGE_1, PAGE_2, PAGE_3));
    }
}
