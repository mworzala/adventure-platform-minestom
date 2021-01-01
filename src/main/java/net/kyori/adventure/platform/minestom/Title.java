package net.kyori.adventure.platform.minestom;

import net.minestom.server.chat.JsonMessage;
import net.minestom.server.entity.Player;

public class Title {
    private final JsonMessage title;
    private final JsonMessage subtitle;
    private final int inTicks;
    private final int stayTicks;
    private final int outTicks;

    public Title(JsonMessage title, JsonMessage subtitle, int inTicks, int stayTicks, int outTicks) {
        this.title = title;
        this.subtitle = subtitle;
        this.inTicks = inTicks;
        this.stayTicks = stayTicks;
        this.outTicks = outTicks;
    }

    public void sendTo(Player player) {
        player.sendTitleTime(inTicks, stayTicks, outTicks);

        if (title == null) return;
        if (subtitle != null)
            player.sendTitleSubtitleMessage(this.title, this.subtitle);
        else player.sendTitleMessage(this.title);
    }

    public static final class Builder {
        private JsonMessage title;
        private JsonMessage subtitle;
        private int inTicks = 0;
        private int stayTicks = 0;
        private int outTicks = 0;

        Builder() {}

        public Builder title(JsonMessage title) {
            this.title = title;
            return this;
        }

        public Builder subtitle(JsonMessage subtitle) {
            this.subtitle = subtitle;
            return this;
        }

        public Builder fadeIn(int inTicks) {
            this.inTicks = inTicks;
            return this;
        }

        public Builder stay(int stayTicks) {
            this.stayTicks = stayTicks;
            return this;
        }

        public Builder fadeOut(int outTicks) {
            this.outTicks = outTicks;
            return this;
        }

        public Title build() {
            return new Title(title, subtitle, inTicks, stayTicks, outTicks);
        }
    }
}
