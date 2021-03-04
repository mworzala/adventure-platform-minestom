package demo;

import demo.command.BookCommand;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.minestom.MinestomAudiences;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.event.HoverEventSource;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerChatEvent;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.instance.*;
import net.minestom.server.instance.batch.ChunkBatch;
import net.minestom.server.instance.block.Block;
import net.minestom.server.sound.SoundCategory;
import net.minestom.server.utils.Position;
import net.minestom.server.world.biomes.Biome;

import java.util.Arrays;
import java.util.List;

public class MainDemo {
    public static MinestomAudiences audiences;

    public static void main(String[] args) {
        // Initialization
        MinecraftServer minecraftServer = MinecraftServer.init();

        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        // Create the instance
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer();
        // Set the ChunkGenerator
        instanceContainer.setChunkGenerator(new GeneratorDemo());
        // Enable the auto chunk loading (when players come close)
        instanceContainer.enableAutoChunkLoad(true);


        audiences = MinestomAudiences.create();
        MinecraftServer.getCommandManager().register(new BookCommand());

        // Add an event callback to specify the spawning instance (and the spawn position)
        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addEventCallback(PlayerLoginEvent.class, event -> {
            final Player player = event.getPlayer();
            final Audience audience = audiences.player(player);
            event.setSpawningInstance(instanceContainer);
            player.setRespawnPoint(new Position(0, 42, 0));

            MinecraftServer.getSchedulerManager().buildTask(() -> {
                Component component = Component.text("I like adventure!")
                        .color(TextColor.color(0xff00ff))
                        .hoverEvent(HoverEvent.showText(Component.text("It is convenient.")));

                audience.sendMessage(component);

                audience.showBossBar(BossBar.bossBar(component, 0.5f, BossBar.Color.YELLOW, BossBar.Overlay.PROGRESS));



            }).schedule();
        });

        globalEventHandler.addEventCallback(PlayerChatEvent.class, event -> {
            final Audience player = audiences.player(event.getPlayer());


            event.getPlayer().playSound("block.lever.click", SoundCategory.BLOCKS, event.getPlayer().getPosition().toBlockPosition(), 1f, 1f);

            player.playSound(Sound.sound(Key.key("block.lever.click"), Sound.Source.BLOCK, 1f, 1f));
            player.sendMessage(Component.text("Hello, World"));
        });

        // Start the server on port 25565
        minecraftServer.start("localhost", 25565);
    }

    private static class GeneratorDemo implements ChunkGenerator {

        @Override
        public void generateChunkData(ChunkBatch batch, int chunkX, int chunkZ) {
            // Set chunk blocks
            for (byte x = 0; x < Chunk.CHUNK_SIZE_X; x++)
                for (byte z = 0; z < Chunk.CHUNK_SIZE_Z; z++) {
                    for (byte y = 0; y < 40; y++) {
                        batch.setBlock(x, y, z, Block.STONE);
                    }
                }
        }

        @Override
        public void fillBiomes(Biome[] biomes, int chunkX, int chunkZ) {
            Arrays.fill(biomes, Biome.PLAINS);
        }

        @Override
        public List<ChunkPopulator> getPopulators() {
            return null;
        }
    }

}
