package me.athulsib.stomcheat.check.impl.other.badpackets;


import me.athulsib.stomcheat.check.Check;
import me.athulsib.stomcheat.check.CheckData;
import me.athulsib.stomcheat.utils.PacketUtil;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.network.packet.client.play.ClientHeldItemChangePacket;

@CheckData(
        name = "BadPackets",
        type = "B",
        description = "Impossible same slot packet",
        punishmentVL = 3)
public class BadPacketsB extends Check {

    int lastSlot = -69;

    @Override
    public void onPacket(PlayerPacketEvent event) {

        switch (PacketUtil.toPacketReceive(event)) {
            case CLIENT_HELD_ITEM_SLOT: {
                ClientHeldItemChangePacket packet = (ClientHeldItemChangePacket) event.getPacket();
                int slot = packet.slot();

                if (slot == lastSlot) {
                    this.fail("Same held item slot packet sent twice in a row: " + slot);
                }

                lastSlot = slot;
                break;
            }
        }
    }
}
