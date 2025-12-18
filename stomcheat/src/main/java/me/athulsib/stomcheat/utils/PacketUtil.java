package me.athulsib.stomcheat.utils;

import lombok.Getter;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.event.player.PlayerPacketOutEvent;
import net.minestom.server.network.packet.client.ClientPacket;
import net.minestom.server.network.packet.client.common.ClientKeepAlivePacket;
import net.minestom.server.network.packet.client.common.ClientPongPacket;
import net.minestom.server.network.packet.client.common.ClientSettingsPacket;
import net.minestom.server.network.packet.client.play.*;
import net.minestom.server.network.packet.server.ServerPacket;
import net.minestom.server.network.packet.server.common.KeepAlivePacket;
import net.minestom.server.network.packet.server.common.PingPacket;
import net.minestom.server.network.packet.server.play.*;

import java.util.HashMap;
import java.util.Map;

@Getter
public class PacketUtil {

    public enum InteractAction {
        INTERACT,
        ATTACK,
        INTERACT_AT
    }

    public enum Packets {
        SERVER_POSITION,
        SERVER_KEEPALIVE,
        SERVER_TRANSACTION,
        SERVER_VELOCITY,
        SERVER_RESPAWN,
        SERVER_OPEN_WINDOW,
        SERVER_ENTITY_METADATA,
        SERVER_ABILITES,
        SERVER_ITEM_SLOT,
        SERVER_ENTITY,
        SERVER_ENTITY_TELEPORT,
        SERVER_ENTITY_HEAD_ROTATION,
        SERVER_NAMED_ENTITY,
        SERVER_ENTITY_DESTORY,
        SERVER_REL_LOOK,
        SERVER_REL_POSITION,
        SERVER_REL_POSITION_LOOK,
        SERVER_PLAYER_SPAWN,
        SERVER_LIVING_SPAWN,
        SERVER_ATTACH,
        SERVER_DESTORY,
        SERVER_BLOCK_CHANGE,
        SERVER_EXPLODE,
        CLIENT_TRANSACTION,
        CLIENT_KEEPALIVE,
        CLIENT_POSITION,
        //CLIENT_FLYING,
        CLIENT_POSITION_LOOK,
        CLIENT_LOOK,
        CLIENT_BLOCK_PLACE,
        CLIENT_BLOCK_DIG,
        CLIENT_ARM_ANIMATION,
        CLIENT_USE_ENTITY,
        CLIENT_ENTITY_ACTION,
        CLIENT_COMMAND,
        CLIENT_CREATIVE_INVENTORY,
        CLIENT_CLOSE_WINDOW,
        CLIENT_HELD_ITEM_SLOT,
        CLIENT_CUSTOM_PAYLOAD,
        CLIENT_WINDOW_CLICK,
        CLIENT_TAB_COMPLETE,
        CLIENT_RESOUCE_PACK,
        CLIENT_SETTINGS,
        CLIENT_ABILITES,
        CLIENT_STEER,
        ENTITY_RIDE_MOVE,
        SERVER_BED,
        SERVER_REL_ENTITY_MOVE,
        SERVER_REL_TELEPORT,
        SERVER_EFFECT,
        SERVER_EFFECT_REMOVE,
        NOT_DEFINDED
    }

    private static final Map<Class<? extends ClientPacket>, Packets> receivePacketMap = new HashMap<>();
    private static final Map<Class<? extends ServerPacket>, Packets> sendPacketMap = new HashMap<>();

    // some relevant packets
    static {
        receivePacketMap.put(ClientPlayerPositionAndRotationPacket.class, Packets.CLIENT_POSITION_LOOK);
        receivePacketMap.put(ClientPlayerPositionPacket.class, Packets.CLIENT_POSITION);
        receivePacketMap.put(ClientPlayerRotationPacket.class, Packets.CLIENT_LOOK);
        receivePacketMap.put(ClientPlayerDiggingPacket.class, Packets.CLIENT_BLOCK_DIG);
        receivePacketMap.put(ClientPlayerBlockPlacementPacket.class, Packets.CLIENT_BLOCK_PLACE);
        receivePacketMap.put(ClientPlayerAbilitiesPacket.class, Packets.CLIENT_ABILITES);
        receivePacketMap.put(ClientKeepAlivePacket.class, Packets.CLIENT_KEEPALIVE);
        receivePacketMap.put(ClientPongPacket.class, Packets.CLIENT_TRANSACTION);
        receivePacketMap.put(ClientInteractEntityPacket.class, Packets.CLIENT_USE_ENTITY);
        receivePacketMap.put(ClientAnimationPacket.class, Packets.CLIENT_ARM_ANIMATION);
        receivePacketMap.put(ClientEntityActionPacket.class, Packets.CLIENT_ENTITY_ACTION);
        receivePacketMap.put(ClientCreativeInventoryActionPacket.class, Packets.CLIENT_CREATIVE_INVENTORY);
        receivePacketMap.put(ClientSettingsPacket.class, Packets.CLIENT_SETTINGS);

        sendPacketMap.put(EntityPositionPacket.class, Packets.SERVER_REL_ENTITY_MOVE);
        sendPacketMap.put(EntityPositionAndRotationPacket.class, Packets.SERVER_REL_POSITION_LOOK);
        sendPacketMap.put(EntityRotationPacket.class, Packets.SERVER_REL_LOOK);
        sendPacketMap.put(EntityTeleportPacket.class, Packets.SERVER_ENTITY_TELEPORT);
        sendPacketMap.put(PlayerPositionAndLookPacket.class, Packets.SERVER_POSITION);
        sendPacketMap.put(AttachEntityPacket.class, Packets.SERVER_ATTACH);
        sendPacketMap.put(EntityVelocityPacket.class, Packets.SERVER_VELOCITY);
        sendPacketMap.put(KeepAlivePacket.class, Packets.SERVER_KEEPALIVE);
        sendPacketMap.put(PingPacket.class, Packets.SERVER_TRANSACTION);
        sendPacketMap.put(PlayerInfoUpdatePacket.class, Packets.SERVER_PLAYER_SPAWN);
        sendPacketMap.put(SpawnEntityPacket.class, Packets.SERVER_LIVING_SPAWN);
        sendPacketMap.put(SetSlotPacket.class, Packets.SERVER_ITEM_SLOT);

    }

    public static Packets toPacketReceive(PlayerPacketEvent event) {
        return receivePacketMap.getOrDefault(event.getPacket().getClass(), Packets.NOT_DEFINDED);
    }

    public static Packets toPacketSend(PlayerPacketOutEvent event) {
        return sendPacketMap.getOrDefault(event.getPacket().getClass(), Packets.NOT_DEFINDED);
    }

    public static InteractAction getAction(ClientInteractEntityPacket packet) {
        return switch (packet.type()) {
            case ClientInteractEntityPacket.InteractAt interactAt -> InteractAction.INTERACT_AT;
            case ClientInteractEntityPacket.Attack attack -> InteractAction.INTERACT;
            case ClientInteractEntityPacket.Interact interact -> InteractAction.ATTACK;
            default -> null;
        };
    }
}
