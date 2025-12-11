package me.athulsib.server;

import me.athulsib.stomcheat.StomCheat;
import net.minestom.server.Auth;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.instance.block.Block;

import java.io.File;

public class SCDevServer {

    public static void main(String[] args) {
        // Initialization
        MinecraftServer minecraftServer = MinecraftServer.init(new Auth.Velocity("minestomdev"));
        TaskManager taskManager = new TaskManager();
        ScoreboardManager scoreboardManager = new ScoreboardManager(taskManager);


        // Create the instance
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer();

        // Set the ChunkGenerator
        //instanceContainer.setGenerator(unit -> unit.modifier().fillHeight(0, 40, Block.GRASS_BLOCK));
        instanceContainer.setChunkLoader(new AnvilLoader("actest"));

        // Add an event callback to specify the spawning instance (and the spawn position)
        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            final Player player = event.getPlayer();
            event.setSpawningInstance(instanceContainer);
            player.setRespawnPoint(new Pos(211, 62, 161));
        });

        globalEventHandler.addListener(PlayerSpawnEvent.class, event -> {
            taskManager.runAsync(() -> {
                event.getPlayer().setSkin(PlayerSkin.fromUsername(event.getPlayer().getUsername()));
            });
        });

        StomCheat stomCheat = new StomCheat();
        stomCheat.enable();

        // Start the server on port 25565
        minecraftServer.start("0.0.0.0", 25568);
    }
}
