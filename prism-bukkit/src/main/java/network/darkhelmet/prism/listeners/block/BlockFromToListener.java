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

package network.darkhelmet.prism.listeners.block;

import com.google.inject.Inject;

import network.darkhelmet.prism.actions.ActionFactory;
import network.darkhelmet.prism.actions.types.ActionTypeRegistry;
import network.darkhelmet.prism.api.actions.IAction;
import network.darkhelmet.prism.api.activities.Activity;
import network.darkhelmet.prism.api.activities.ISingleActivity;
import network.darkhelmet.prism.listeners.AbstractListener;
import network.darkhelmet.prism.loader.services.configuration.ConfigurationService;
import network.darkhelmet.prism.services.expectations.ExpectationService;
import network.darkhelmet.prism.services.recording.RecordingService;
import network.darkhelmet.prism.utils.LocationUtils;
import network.darkhelmet.prism.utils.TagLib;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

public class BlockFromToListener extends AbstractListener implements Listener {
    /**
     * Construct the listener.
     *
     * @param configurationService The configuration service
     * @param actionFactory The action factory
     * @param expectationService The expectation service
     * @param recordingService The recording service
     */
    @Inject
    public BlockFromToListener(
            ConfigurationService configurationService,
            ActionFactory actionFactory,
            ExpectationService expectationService,
            RecordingService recordingService) {
        super(configurationService, actionFactory, expectationService, recordingService);
    }

    /**
     * Listens for block from/to events.
     *
     * @param event The event
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockFromTo(final BlockFromToEvent event) {
        final BlockState fromState = event.getBlock().getState();
        final BlockState toState = event.getToBlock().getState();

        // If the liquid is flowing to a detachable block, log it
        if (TagLib.FLUID_BREAKABLE.isTagged(toState.getType())) {
            // Ignore if this event is disabled
            if (!configurationService.prismConfig().actions().blockBreak()) {
                return;
            }

            // Build the action
            final Block block = event.getBlock();
            final IAction action = actionFactory
                 .createBlockStateAction(ActionTypeRegistry.BLOCK_BREAK, toState);

            // Build the block activity
            ISingleActivity activity = Activity.builder()
                .action(action)
                .cause(nameFromCause(fromState))
                .location(LocationUtils.locToWorldCoordinate(block.getLocation()))
                .build();

            recordingService.addToQueue(activity);

            return;
        }

        // Ignore if this event is disabled
        if (!configurationService.prismConfig().actions().fluidFlow()) {
            return;
        }

        // Build the action
        final IAction action = actionFactory
            .createBlockStateAction(ActionTypeRegistry.FLUID_FLOW, toState);

        // Build the block activity
        ISingleActivity activity = Activity.builder()
            .action(action)
            .cause(nameFromCause(fromState))
            .location(LocationUtils.locToWorldCoordinate(fromState.getLocation()))
            .build();

        recordingService.addToQueue(activity);
    }
}
