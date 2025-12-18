package me.athulsib.stomcheat.wrapper;

import lombok.Getter;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.network.packet.client.ClientPacket;
import net.minestom.server.network.packet.client.play.ClientPlayerPositionAndRotationPacket;
import net.minestom.server.network.packet.client.play.ClientPlayerPositionPacket;
import net.minestom.server.network.packet.client.play.ClientPlayerRotationPacket;

@Getter
public class FlyingWrapper {

    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;
    private final boolean onGround;
    private final boolean hasLook;
    private final boolean pos;

    public FlyingWrapper(PlayerPacketEvent event) {
        ClientPacket packet = event.getPacket();
        switch (packet) {
            case ClientPlayerPositionAndRotationPacket clientPlayerPositionAndRotationPacket -> {
                this.x = clientPlayerPositionAndRotationPacket.position().x();
                this.y = clientPlayerPositionAndRotationPacket.position().y();
                this.z = clientPlayerPositionAndRotationPacket.position().z();
                this.yaw = clientPlayerPositionAndRotationPacket.position().yaw();
                this.pitch = clientPlayerPositionAndRotationPacket.position().pitch();
                this.onGround = clientPlayerPositionAndRotationPacket.onGround();
                this.hasLook = true;
                this.pos = true;
            }
            case ClientPlayerPositionPacket clientPlayerPositionPacket -> {
                this.x = clientPlayerPositionPacket.position().x();
                this.y = clientPlayerPositionPacket.position().y();
                this.z = clientPlayerPositionPacket.position().z();
                this.yaw = this.getYaw();
                this.pitch = this.getPitch();
                this.onGround = clientPlayerPositionPacket.onGround();
                this.hasLook = false;
                this.pos = true;
            }
            case ClientPlayerRotationPacket clientPlayerRotationPacket -> {
                this.x = this.getX();
                this.y = this.getY();
                this.z = this.getZ();
                this.yaw = clientPlayerRotationPacket.yaw();
                this.pitch = clientPlayerRotationPacket.pitch();
                this.onGround = clientPlayerRotationPacket.onGround();
                this.hasLook = true;
                this.pos = false;
            }
            default -> throw new IllegalStateException("Unexpected value: " + packet);
        }

    }

    public FlyingWrapper(ClientPlayerRotationPacket packet) {
        this.x = this.getX();
        this.y = this.getY();
        this.z = this.getZ();
        this.yaw = packet.yaw();
        this.pitch = packet.pitch();
        this.onGround = packet.onGround();
        this.hasLook = true;
        this.pos = false;

    }

    public FlyingWrapper(ClientPlayerPositionPacket packet) {
        this.x = packet.position().x();
        this.y = packet.position().y();
        this.z = packet.position().z();
        this.yaw = this.getYaw();
        this.pitch = this.getPitch();
        this.onGround = packet.onGround();
        this.hasLook = false;
        this.pos = true;

    }

    public FlyingWrapper(ClientPlayerPositionAndRotationPacket packet) {
        this.x = packet.position().x();
        this.y = packet.position().y();
        this.z = packet.position().z();
        this.yaw = packet.position().yaw();
        this.pitch = packet.position().pitch();
        this.onGround = packet.onGround();
        this.hasLook = true;
        this.pos = true;

    }

    public boolean hasPositionChanged(FlyingWrapper wrapper) {
        return wrapper == null || this.x != wrapper.x || this.y != wrapper.y || this.z != wrapper.z;
    }

    public boolean hasRotationChanged(FlyingWrapper wrapper) {
        return wrapper == null || this.yaw != wrapper.yaw || this.pitch != wrapper.pitch;
    }
}