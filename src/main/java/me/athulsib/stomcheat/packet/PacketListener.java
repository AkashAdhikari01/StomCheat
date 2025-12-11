package me.athulsib.stomcheat.packet;

import me.athulsib.stomcheat.StomCheat;
import me.athulsib.stomcheat.user.User;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.event.player.PlayerPacketOutEvent;

public class PacketListener {

    public PacketListener() {
        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addListener(PlayerPacketEvent.class, this::onPacketReceive);
        globalEventHandler.addListener(PlayerPacketOutEvent.class, this::onPacketSend);
    }


    private void onPacketReceive(PlayerPacketEvent event) {
        Player player = event.getPlayer();

        User user = StomCheat.getInstance().getUserManager().getUser(player);
        if (user == null) return;

        if (user.getThread() == null) {
            user.setThread(StomCheat.getInstance().getThreadManager().assignThread(user));
        }

        user.handle(event);
    }


    private void onPacketSend(PlayerPacketOutEvent event) {
        Player player = event.getPlayer();

        User user = StomCheat.getInstance().getUserManager().getUser(player);
        if (user == null) return;

        if (user.getThread() == null) {
            user.setThread(StomCheat.getInstance().getThreadManager().assignThread(user));
        }

        user.handle(event);
    }
}
