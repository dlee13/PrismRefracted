/*
 * Prism (Refracted)
 *
 * Copyright (c) 2022 M Botsko (viveleroi)
 *                    Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package network.darkhelmet.prism;

import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.core.argument.named.Argument;
import dev.triumphteam.cmd.core.argument.named.ArgumentKey;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;

import io.papermc.lib.PaperLib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;

import network.darkhelmet.prism.actions.types.ActionTypeRegistry;
import network.darkhelmet.prism.api.IPrism;
import network.darkhelmet.prism.api.actions.types.IActionType;
import network.darkhelmet.prism.api.actions.types.IActionTypeRegistry;
import network.darkhelmet.prism.api.services.recording.IRecordingService;
import network.darkhelmet.prism.api.storage.IStorageAdapter;
import network.darkhelmet.prism.commands.AboutCommand;
import network.darkhelmet.prism.commands.ExtinguishCommand;
import network.darkhelmet.prism.commands.LookupCommand;
import network.darkhelmet.prism.commands.NearCommand;
import network.darkhelmet.prism.commands.PageCommand;
import network.darkhelmet.prism.commands.PreviewCommand;
import network.darkhelmet.prism.commands.PurgeCommand;
import network.darkhelmet.prism.commands.ReloadCommand;
import network.darkhelmet.prism.commands.RestoreCommand;
import network.darkhelmet.prism.commands.RollbackCommand;
import network.darkhelmet.prism.commands.WandCommand;
import network.darkhelmet.prism.core.utils.VersionUtils;
import network.darkhelmet.prism.listeners.block.BlockBreakListener;
import network.darkhelmet.prism.listeners.block.BlockBurnListener;
import network.darkhelmet.prism.listeners.block.BlockDispenseListener;
import network.darkhelmet.prism.listeners.block.BlockExplodeListener;
import network.darkhelmet.prism.listeners.block.BlockFadeListener;
import network.darkhelmet.prism.listeners.block.BlockFertilizeListener;
import network.darkhelmet.prism.listeners.block.BlockFormListener;
import network.darkhelmet.prism.listeners.block.BlockFromToListener;
import network.darkhelmet.prism.listeners.block.BlockIgniteListener;
import network.darkhelmet.prism.listeners.block.BlockPistonExtendListener;
import network.darkhelmet.prism.listeners.block.BlockPistonRetractListener;
import network.darkhelmet.prism.listeners.block.BlockPlaceListener;
import network.darkhelmet.prism.listeners.block.BlockSpreadListener;
import network.darkhelmet.prism.listeners.entity.EntityBlockFormListener;
import network.darkhelmet.prism.listeners.entity.EntityChangeBlockListener;
import network.darkhelmet.prism.listeners.entity.EntityDeathListener;
import network.darkhelmet.prism.listeners.entity.EntityExplodeListener;
import network.darkhelmet.prism.listeners.entity.EntityPickupItemListener;
import network.darkhelmet.prism.listeners.entity.EntityPlaceListener;
import network.darkhelmet.prism.listeners.entity.EntitySpawnListener;
import network.darkhelmet.prism.listeners.entity.EntityUnleashListener;
import network.darkhelmet.prism.listeners.hanging.HangingBreakListener;
import network.darkhelmet.prism.listeners.hanging.HangingPlaceListener;
import network.darkhelmet.prism.listeners.inventory.InventoryClickListener;
import network.darkhelmet.prism.listeners.inventory.InventoryDragListener;
import network.darkhelmet.prism.listeners.leaves.LeavesDecayListener;
import network.darkhelmet.prism.listeners.player.PlayerBedEnterListener;
import network.darkhelmet.prism.listeners.player.PlayerBucketEmptyListener;
import network.darkhelmet.prism.listeners.player.PlayerBucketFillListener;
import network.darkhelmet.prism.listeners.player.PlayerDropItemListener;
import network.darkhelmet.prism.listeners.player.PlayerEggThrowListener;
import network.darkhelmet.prism.listeners.player.PlayerExpChangeListener;
import network.darkhelmet.prism.listeners.player.PlayerHarvestBlockListener;
import network.darkhelmet.prism.listeners.player.PlayerInteractListener;
import network.darkhelmet.prism.listeners.player.PlayerJoinListener;
import network.darkhelmet.prism.listeners.player.PlayerLeashEntityListener;
import network.darkhelmet.prism.listeners.player.PlayerQuitListener;
import network.darkhelmet.prism.listeners.player.PlayerShearEntityListener;
import network.darkhelmet.prism.listeners.player.PlayerTakeLecternBookListener;
import network.darkhelmet.prism.listeners.player.PlayerTeleportListener;
import network.darkhelmet.prism.listeners.player.PlayerUnleashEntityListener;
import network.darkhelmet.prism.listeners.portal.PortalCreateListener;
import network.darkhelmet.prism.listeners.projectile.ProjectileLaunchListener;
import network.darkhelmet.prism.listeners.sheep.SheepDyeWoolListener;
import network.darkhelmet.prism.listeners.sponge.SpongeAbsorbListener;
import network.darkhelmet.prism.listeners.structure.StructureGrowListener;
import network.darkhelmet.prism.listeners.tnt.TntPrimeListener;
import network.darkhelmet.prism.listeners.vehicle.VehicleEnterListener;
import network.darkhelmet.prism.listeners.vehicle.VehicleExitListener;
import network.darkhelmet.prism.loader.services.dependencies.Dependency;
import network.darkhelmet.prism.loader.services.dependencies.DependencyService;
import network.darkhelmet.prism.loader.services.dependencies.loader.PluginLoader;
import network.darkhelmet.prism.loader.services.scheduler.ThreadPoolScheduler;
import network.darkhelmet.prism.providers.InjectorProvider;
import network.darkhelmet.prism.services.recording.RecordingService;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class PrismBukkit implements IPrism {
    /**
     * Cache static instance.
     */
    private static PrismBukkit instance;

    /**
     * The bootstrap.
     */
    private final PrismBukkitBootstrap bootstrap;

    /**
     * The injector provider.
     */
    @Getter
    private InjectorProvider injectorProvider;

    /**
     * Sets a numeric version we can use to handle differences between serialization formats.
     */
    @Getter
    protected short serializerVersion;

    /**
     * The storage adapter.
     */
    @Getter
    private IStorageAdapter storageAdapter;

    /**
     * The action type registry.
     */
    @Getter
    private IActionTypeRegistry actionTypeRegistry;

    /**
     * The thread pool scheduler.
     */
    private final ThreadPoolScheduler threadPoolScheduler;

    /**
     * Get this instance.
     *
     * @return The plugin instance
     */
    public static PrismBukkit getInstance() {
        return instance;
    }

    /**
     * Constructor.
     */
    public PrismBukkit(PrismBukkitBootstrap bootstrap) {
        this.bootstrap = bootstrap;
        this.threadPoolScheduler = new ThreadPoolScheduler(loader());
        instance = this;
    }

    /**
     * Get all platform dependencies.
     *
     * @return The platform dependencies
     */
    protected Set<Dependency> platformDependencies() {
        return EnumSet.of(
            Dependency.ADVENTURE_PLATFORM_BUKKIT,
            Dependency.NBT_API,
            Dependency.TASKCHAIN_BUKKIT,
            Dependency.TASKCHAIN_CORE
        );
    }

    /**
     * On enable.
     */
    public void onEnable() {
        DependencyService dependencyService = new DependencyService(
            bootstrap.loggingService(),
            bootstrap.loader().configurationService(),
            loaderPlugin().getDataFolder().toPath(),
            bootstrap.classPathAppender(),
            threadPoolScheduler
        );
        dependencyService.loadAllDependencies(platformDependencies());

        Short serializerVer = VersionUtils.minecraftVersion(Bukkit.getVersion());
        serializerVersion = serializerVer != null ? serializerVer : -1;
        bootstrap.loggingService().logger().info(String.format("Serializer version: %d", serializerVersion));

        injectorProvider = new InjectorProvider(this, bootstrap.loggingService());

        // Choose and initialize the datasource
        try {
            storageAdapter = injectorProvider.injector().getInstance(IStorageAdapter.class);
            if (!storageAdapter.ready()) {
                disable();

                return;
            }
        } catch (Exception e) {
            bootstrap.loggingService().handleException(e);

            disable();

            return;
        }

        actionTypeRegistry = injectorProvider.injector().getInstance(IActionTypeRegistry.class);

        String pluginName = this.loaderPlugin().getDescription().getName();
        String pluginVersion = this.loaderPlugin().getDescription().getVersion();
        bootstrap.loggingService().logger().info(
            String.format("Initializing %s %s by viveleroi", pluginName, pluginVersion));

        if (loaderPlugin().isEnabled()) {
            // Initialize some classes
            injectorProvider.injector().getInstance(RecordingService.class);

            // Register event listeners
            registerEvent(BlockBreakListener.class);
            registerEvent(BlockBurnListener.class);
            registerEvent(BlockDispenseListener.class);
            registerEvent(BlockExplodeListener.class);
            registerEvent(BlockFadeListener.class);
            registerEvent(BlockFertilizeListener.class);
            registerEvent(BlockFormListener.class);
            registerEvent(BlockFromToListener.class);
            registerEvent(BlockIgniteListener.class);
            registerEvent(BlockPistonExtendListener.class);
            registerEvent(BlockPistonRetractListener.class);
            registerEvent(BlockPlaceListener.class);
            registerEvent(BlockSpreadListener.class);
            registerEvent(EntityBlockFormListener.class);
            registerEvent(EntityChangeBlockListener.class);
            registerEvent(EntityDeathListener.class);
            registerEvent(EntityExplodeListener.class);
            registerEvent(EntityPickupItemListener.class);
            registerEvent(EntityPlaceListener.class);
            registerEvent(EntitySpawnListener.class);
            registerEvent(EntityUnleashListener.class);
            registerEvent(HangingBreakListener.class);
            registerEvent(HangingPlaceListener.class);
            registerEvent(InventoryClickListener.class);
            registerEvent(InventoryDragListener.class);
            registerEvent(LeavesDecayListener.class);
            registerEvent(PlayerBedEnterListener.class);
            registerEvent(PlayerBucketEmptyListener.class);
            registerEvent(PlayerBucketFillListener.class);
            registerEvent(PlayerDropItemListener.class);
            registerEvent(PlayerEggThrowListener.class);
            registerEvent(PlayerExpChangeListener.class);
            registerEvent(PlayerHarvestBlockListener.class);
            registerEvent(PlayerInteractListener.class);
            registerEvent(PlayerJoinListener.class);
            registerEvent(PlayerLeashEntityListener.class);
            registerEvent(PlayerQuitListener.class);
            registerEvent(PlayerShearEntityListener.class);
            registerEvent(PlayerTakeLecternBookListener.class);
            registerEvent(PlayerTeleportListener.class);
            registerEvent(PlayerUnleashEntityListener.class);
            registerEvent(ProjectileLaunchListener.class);
            registerEvent(PortalCreateListener.class);
            registerEvent(SheepDyeWoolListener.class);
            registerEvent(SpongeAbsorbListener.class);
            registerEvent(StructureGrowListener.class);
            registerEvent(VehicleEnterListener.class);
            registerEvent(VehicleExitListener.class);

            if (PaperLib.isPaper()) {
                registerEvent(TntPrimeListener.class);
            }

            // Register commands
            BukkitCommandManager<CommandSender> commandManager = BukkitCommandManager.create(loaderPlugin());

            // Register action types auto-suggest
            commandManager.registerSuggestion(SuggestionKey.of("actions"), (sender, context) -> {
                List<String> actionFamilies = new ArrayList<>();
                for (IActionType actionType : injectorProvider.injector()
                        .getInstance(ActionTypeRegistry.class).actionTypes()) {
                    actionFamilies.add(actionType.familyKey());
                }

                return actionFamilies;
            });

            // Register online player auto-suggest
            commandManager.registerSuggestion(SuggestionKey.of("players"), (sender, context) -> {
                List<String> players = new ArrayList<>();
                for (Player player : loaderPlugin().getServer().getOnlinePlayers()) {
                    players.add(player.getName());
                }

                return players;
            });

            // Register world auto-suggest
            commandManager.registerSuggestion(SuggestionKey.of("worlds"), (sender, context) -> {
                List<String> worlds = new ArrayList<>();
                for (World world : loaderPlugin().getServer().getWorlds()) {
                    worlds.add(world.getName());
                }

                return worlds;
            });

            // Register "in" parameter
            commandManager.registerSuggestion(SuggestionKey.of("ins"), (sender, context) ->
                Arrays.asList("chunk", "world"));

            commandManager.registerNamedArguments(
                ArgumentKey.of("params"),
                Argument.forBoolean().name("reversed").build(),
                Argument.forInt().name("r").build(),
                Argument.forString().name("in").suggestion(SuggestionKey.of("ins")).build(),
                Argument.forString().name("since").build(),
                Argument.forString().name("before").build(),
                Argument.forString().name("cause").build(),
                Argument.forString().name("world").suggestion(SuggestionKey.of("worlds")).build(),
                Argument.forString().name("at").build(),
                Argument.forString().name("bounds").build(),
                Argument.listOf(String.class).name("a").suggestion(SuggestionKey.of("actions")).build(),
                Argument.listOf(Material.class).name("m").build(),
                Argument.listOf(EntityType.class).name("e").build(),
                Argument.listOf(String.class).name("p").suggestion(SuggestionKey.of("players")).build()
            );

            commandManager.registerCommand(injectorProvider.injector().getInstance(AboutCommand.class));
            commandManager.registerCommand(injectorProvider.injector().getInstance(ExtinguishCommand.class));
            commandManager.registerCommand(injectorProvider.injector().getInstance(LookupCommand.class));
            commandManager.registerCommand(injectorProvider.injector().getInstance(NearCommand.class));
            commandManager.registerCommand(injectorProvider.injector().getInstance(PageCommand.class));
            commandManager.registerCommand(injectorProvider.injector().getInstance(PreviewCommand.class));
            commandManager.registerCommand(injectorProvider.injector().getInstance(PurgeCommand.class));
            commandManager.registerCommand(injectorProvider.injector().getInstance(ReloadCommand.class));
            commandManager.registerCommand(injectorProvider.injector().getInstance(RestoreCommand.class));
            commandManager.registerCommand(injectorProvider.injector().getInstance(RollbackCommand.class));
            commandManager.registerCommand(injectorProvider.injector().getInstance(WandCommand.class));
        }
    }

    /**
     * Register an event.
     *
     * @param type The event class
     * @param <T> The type
     */
    protected <T> void registerEvent(Class<? extends Listener> type) {
        loaderPlugin().getServer().getPluginManager()
            .registerEvents(injectorProvider.injector().getInstance(type), loaderPlugin());
    }

    /**
     * Get the loader plugin.
     *
     * @return The loader
     */
    public PluginLoader loader() {
        return bootstrap.loader();
    }

    /**
     * Get the loader as a bukkit plugin.
     *
     * @return The loader as a bukkit plugin
     */
    public JavaPlugin loaderPlugin() {
        return (JavaPlugin) bootstrap.loader();
    }

    /**
     * Disable the plugin.
     */
    protected void disable() {
        Bukkit.getPluginManager().disablePlugin(loaderPlugin());

        threadPoolScheduler.shutdownScheduler();
        threadPoolScheduler.shutdownExecutor();

        bootstrap.loggingService().logger().error("Prism has to disable due to a fatal error.");
    }

    /**
     * On disable.
     */
    public void onDisable() {
        IRecordingService recordingService = injectorProvider.injector().getInstance(IRecordingService.class);
        if (!recordingService.queue().isEmpty()) {
            loader().loggingService().logger().info(
                "Blocking shut down to try to fully drain prism recording queue...");

            recordingService.drainSync();

            loader().loggingService().logger().info("Recording queue now empty.");
        }

        if (storageAdapter != null) {
            storageAdapter.close();
        }

        BukkitAudiences audiences = injectorProvider.injector().getInstance(BukkitAudiences.class);
        if (audiences != null) {
            audiences.close();
        }
    }
}
