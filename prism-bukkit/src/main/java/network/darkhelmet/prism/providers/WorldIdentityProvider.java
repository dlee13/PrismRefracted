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

package network.darkhelmet.prism.providers;

import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import network.darkhelmet.prism.api.providers.IWorldIdentityProvider;
import network.darkhelmet.prism.api.util.NamedIdentity;

import org.bukkit.Bukkit;
import org.bukkit.World;

@Singleton
public class WorldIdentityProvider implements IWorldIdentityProvider {
    @Override
    public Collection<NamedIdentity> worlds() {
        List<NamedIdentity> worlds = new ArrayList<>();
        for (World world : Bukkit.getServer().getWorlds()) {
            worlds.add(new NamedIdentity(world.getUID(), world.getName()));
        }

        return worlds;
    }
}
