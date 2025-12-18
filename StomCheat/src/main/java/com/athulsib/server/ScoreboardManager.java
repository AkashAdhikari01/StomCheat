package com.athulsib.server;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.scoreboard.Sidebar;
import net.minestom.server.timer.Task;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class ScoreboardManager {
    private final TaskManager taskManager;
    private final Map<Player, Sidebar> playerSideboards = new ConcurrentHashMap<>();
    private final Map<Player, PlayerData> playerDataMap = new ConcurrentHashMap<>();
    private final Map<Player, Set<String>> playerLines = new ConcurrentHashMap<>();
    private Task updateTask;

    public ScoreboardManager(TaskManager taskManager) {
        this.taskManager = taskManager;
        init();
    }

    private void init() {
        // Register event listeners
        MinecraftServer.getGlobalEventHandler().addListener(EventListener.builder(PlayerSpawnEvent.class)
                .handler(this::onPlayerJoin)
                .build());

        MinecraftServer.getGlobalEventHandler().addListener(EventListener.builder(PlayerDisconnectEvent.class)
                .handler(this::onPlayerLeave)
                .build());

        // Start update task
        updateTask = taskManager.runAsyncTimer(this::updateAllScoreboards, 50, 50, TimeUnit.MILLISECONDS);
    }

    private void onPlayerJoin(PlayerSpawnEvent event) {
        Player player = event.getPlayer();
        createScoreboard(player);
        playerDataMap.put(player, new PlayerData(player));
        playerLines.put(player, new HashSet<>());
    }

    private void onPlayerLeave(PlayerDisconnectEvent event) {
        Player player = event.getPlayer();
        removeScoreboard(player);
        playerDataMap.remove(player);
        playerLines.remove(player);
    }

    private void createScoreboard(Player player) {
        // Create sidebar with title
        Sidebar sidebar = new Sidebar(Component.text("StomCheat", NamedTextColor.GOLD));

        // Add player as viewer
        sidebar.addViewer(player);

        // Store reference
        playerSideboards.put(player, sidebar);
    }

    private void removeScoreboard(Player player) {
        Sidebar sidebar = playerSideboards.remove(player);
        if (sidebar != null) {
            // Remove player as viewer
            sidebar.removeViewer(player);
        }
    }

    private void updateAllScoreboards() {
        // Update player data first
        playerDataMap.values().forEach(PlayerData::update);

        // Then update scoreboards on main thread
        taskManager.runSync(() -> {
            playerSideboards.forEach((player, sidebar) -> {
                List<Component> lines = getLines(player);
                updateSidebar(player, sidebar, lines);
            });
        });
    }

    private void updateSidebar(Player player, Sidebar sidebar, List<Component> lines) {
        Set<String> currentLineIds = playerLines.get(player);
        if (currentLineIds == null) return;

        // Create a set of line IDs we want to keep
        Set<String> desiredLineIds = new HashSet<>();

        // Update or create lines
        for (int i = 0; i < lines.size(); i++) {
            String lineId = "line_" + i;
            desiredLineIds.add(lineId);

            if (currentLineIds.contains(lineId)) {
                // Update existing line
                sidebar.updateLineContent(lineId, lines.get(i));
                sidebar.updateLineScore(lineId, lines.size() - i);
            } else {
                // Create new line
                Sidebar.ScoreboardLine line = new Sidebar.ScoreboardLine(
                        lineId,
                        lines.get(i),
                        lines.size() - i
                );
                sidebar.createLine(line);
                currentLineIds.add(lineId);
            }
        }

        // Remove lines that are no longer needed
        Set<String> toRemove = new HashSet<>(currentLineIds);
        toRemove.removeAll(desiredLineIds);

        for (String lineId : toRemove) {
            sidebar.removeLine(lineId);
            currentLineIds.remove(lineId);
        }
    }

    public List<Component> getLines(Player player) {
        PlayerData data = playerDataMap.get(player);
        if (data == null) return Collections.emptyList();

        List<Component> lines = new ArrayList<>();

        // Player info
        lines.add(Component.empty());
        lines.add(Component.text("Player: ", NamedTextColor.GRAY)
                .append(Component.text(player.getUsername(), NamedTextColor.YELLOW)));

        // Speed info (horizontal blocks per second)
        lines.add(Component.text("Speed: ", NamedTextColor.GRAY)
                .append(Component.text(String.format("%.2f b/s", data.getSpeed()), NamedTextColor.AQUA)));

        // Momentum info (horizontal velocity)
        lines.add(Component.text("Momentum: ", NamedTextColor.GRAY)
                .append(Component.text(String.format("%.2f", data.getMomentum()), NamedTextColor.GREEN)));

        // Position info
        lines.add(Component.empty());
        lines.add(Component.text("Position: ", NamedTextColor.GRAY)
                .append(Component.text(String.format("%.1f, %.1f, %.1f",
                        player.getPosition().x(),
                        player.getPosition().y(),
                        player.getPosition().z()), NamedTextColor.WHITE)));

        // Ping info
        lines.add(Component.text("Ping: ", NamedTextColor.GRAY)
                .append(Component.text(player.getLatency() + "ms", NamedTextColor.RED)));

        // Online players
        lines.add(Component.empty());
        lines.add(Component.text("Online: ", NamedTextColor.GRAY)
                .append(Component.text(MinecraftServer.getConnectionManager().getOnlinePlayers().size(), NamedTextColor.GREEN)));

        return lines;
    }

    public void shutdown() {
        if (updateTask != null) {
            updateTask.cancel();
        }

        // Remove all scoreboards
        playerSideboards.forEach((player, sidebar) -> sidebar.removeViewer(player));
        playerSideboards.clear();
        playerDataMap.clear();
        playerLines.clear();
    }

    /**
     * Helper class to track player data between updates
     */
    private static class PlayerData {
        private final Player player;
        private double lastSpeed;
        private double lastMomentum;

        public PlayerData(Player player) {
            this.player = player;
        }

        public void update() {
            // Calculate horizontal speed (blocks per second)
            // Only consider X and Z components, ignore Y (vertical movement)
            double horizontalVelocityX = player.getVelocity().x();
            double horizontalVelocityZ = player.getVelocity().z();

            // Calculate horizontal velocity magnitude (blocks per tick)
            double horizontalVelocity = Math.sqrt(horizontalVelocityX * horizontalVelocityX + horizontalVelocityZ * horizontalVelocityZ);

            // Convert to blocks per second (20 ticks per second)
            this.lastSpeed = horizontalVelocity * 20;

            // Store momentum (horizontal velocity in blocks per tick)
            this.lastMomentum = horizontalVelocity;
        }

        public double getSpeed() {
            return lastSpeed;
        }

        public double getMomentum() {
            return lastMomentum;
        }
    }
}