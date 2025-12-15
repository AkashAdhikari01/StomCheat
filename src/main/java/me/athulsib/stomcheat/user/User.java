package me.athulsib.stomcheat.user;

import me.athulsib.stomcheat.check.Check;

import me.athulsib.stomcheat.process.MovementProcessor;
import me.athulsib.stomcheat.thread.Thread;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.event.player.PlayerPacketOutEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class User {

    private final Player player;
    private final UUID uuid;

    private final String userName;

    private final List<Check> checks;

    //TODO Make processor registration order configurable & add custom processor support
    private MovementProcessor movementProcessor;

    // Assigned execution thread for this user
    private Thread thread;

    public User(Player player) {
        this.player = player;
        this.uuid = player.getUuid();
        this.userName = player.getUsername();
        this.checks = new ArrayList<>();
        this.movementProcessor = new MovementProcessor(this);
    }


    public void handle(PlayerPacketEvent event) {
        this.thread.execute(()  -> {
            this.movementProcessor.onPacket(event);
            for (Check check : this.checks) {
                check.onPacket(event);
            }
        });
    }

    public void handle(PlayerPacketOutEvent event) {
        this.thread.execute(() -> {
            this.movementProcessor.onPacket(event);
            for (Check check : this.checks) {
                check.onPacket(event);
            }
        });
    }
}
