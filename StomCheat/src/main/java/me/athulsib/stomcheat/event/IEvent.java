package me.athulsib.stomcheat.event;


import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.event.player.PlayerPacketOutEvent;

public interface IEvent {

    void onPacket(PlayerPacketEvent event);

    void onPacket(PlayerPacketOutEvent event);
}