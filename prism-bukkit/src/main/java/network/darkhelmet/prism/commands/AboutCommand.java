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

package network.darkhelmet.prism.commands;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.annotation.SubCommand;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;

import network.darkhelmet.prism.services.messages.MessageService;

import org.bukkit.command.CommandSender;

@Command(value = "prism", alias = {"pr"})
public class AboutCommand extends BaseCommand {
    /**
     * The message service.
     */
    private final MessageService messageService;

    /**
     * The bukkit audiences.
     */
    private final BukkitAudiences audiences;

    /**
     * The version.
     */
    private final String version;

    /**
     * Construct the about command.
     *
     * @param messageService The message service
     * @param audiences The bukkit audiences
     * @param version The prism version
     */
    @Inject
    public AboutCommand(
            MessageService messageService,
            BukkitAudiences audiences,
            @Named("version") String version) {
        this.messageService = messageService;
        this.audiences = audiences;
        this.version = version;
    }

    /**
     * Run the about command, or default to this if prism is run with no subcommand.
     *
     * @param sender The command sender
     */
    @Default
    @SubCommand("about")
    @Permission("prism.admin")
    public void onAbout(final CommandSender sender) {
        messageService.about(sender, version);

        Component links = Component.text()
            .append(Component.text("Links: ", NamedTextColor.GRAY))
            .append(link("Discord", "https://discord.gg/7FxZScH4EJ"))
            .append(Component.text(" "))
            .append(link("Docs", "https://prism.readthedocs.io/")).build();

        audiences.sender(sender).sendMessage(links);
    }

    /**
     * Convenience method to create a link.
     *
     * @param label The label
     * @param url The url
     * @return The component
     */
    protected Component link(String label, String url) {
        return Component.text()
            .append(Component.text("[", NamedTextColor.AQUA))
            .append(Component.text(label, NamedTextColor.WHITE))
            .append(Component.text("]", NamedTextColor.AQUA))
            .clickEvent(ClickEvent.openUrl(url))
                .hoverEvent(HoverEvent.showText(Component.text("Click to open in a browser")))
            .build();
    }
}
