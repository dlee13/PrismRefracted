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

package network.darkhelmet.prism.api.services.recording;

import java.util.concurrent.LinkedBlockingQueue;

import network.darkhelmet.prism.api.activities.ISingleActivity;

public interface IRecordingService {
    /**
     * Add an activity to the recording queue.
     *
     * @param activity Activity
     * @return True if added to queue
     */
    boolean addToQueue(final ISingleActivity activity);

    /**
     * Cancels and removes the current recording task.
     */
    void clearTask();

    /**
     * Drain the queue synchronously.
     *
     * <p>This is done sync to block any other processes.</p>
     */
    void drainSync();

    /**
     * Get the queue.
     *
     * @return the queue
     */
    LinkedBlockingQueue<ISingleActivity> queue();

    /**
     * Schedule the next recording task.
     *
     * @param task The task
     */
    void queueNextRecording(Runnable task);
}
