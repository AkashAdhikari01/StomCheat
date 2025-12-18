package me.athulsib.stomcheat.minestom;

import me.athulsib.stomcheat.StomCheat;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;

public class MinestomListener {

    public MinestomListener() {
        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addListener(PlayerSpawnEvent.class, this::onJoin);
        globalEventHandler.addListener(PlayerDisconnectEvent.class, this::onLeave);
    }

    public void onJoin(final PlayerSpawnEvent event) {
        StomCheat.getInstance().getUserManager().addUser(event.getPlayer());
    }

    public void onLeave(final PlayerDisconnectEvent event) {
        StomCheat.getInstance().getUserManager().removeUser(event.getPlayer());
    }
}
