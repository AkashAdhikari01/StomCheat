package com.athulsib.server.test;

import me.athulsib.stomcheat.alert.AlertManager;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;

public class AlertMang implements AlertManager {
    @Override
    public void sendStaffAlert(Component alertComponent) {
        for (Player player: MinecraftServer.getConnectionManager().getOnlinePlayers()) {
            player.sendActionBar(alertComponent);
            player.sendMessage("gay");
        }
    }
}
