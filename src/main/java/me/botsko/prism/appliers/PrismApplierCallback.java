package me.botsko.prism.appliers;

import me.botsko.prism.Il8n;
import me.botsko.prism.Prism;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map.Entry;

public class PrismApplierCallback implements ApplierCallback {

    @Override
    public void handle(CommandSender sender, ApplierResult result) {

        // Did we move anyone?
        final HashMap<Entity, Integer> entitiesMoved = result.getEntitiesMoved();
        if (!entitiesMoved.isEmpty()) {
            for (final Entry<Entity, Integer> entry : entitiesMoved.entrySet()) {
                if (entry.getKey() instanceof Player) {
                    Prism.messenger.sendMessage(entry.getKey(), Prism.messenger.playerSubduedHeaderMsg(
                            Il8n.formatMessage("prism-move-safety", entry.getValue())));
                }
            }
        }
        TextComponent.Builder builder = Component.text();

        // Send player success messages
        if (result.getProcessType().equals(PrismProcessType.ROLLBACK)) {

            // Build the results message
            if (!result.isPreview()) {
                builder.append(Il8n.formatMessage("applier-rollback-start", result.getChangesApplied()));
                if (result.getChangesSkipped() > 0) {
                    builder.append(Component.text(" "));
                    builder.append(Il8n.formatMessage("applier-changes-skipped", result.getChangesSkipped()));
                }
                if (result.getChangesApplied() > 0) {
                    builder.append(Component.text(" "));
                    builder.append(Il8n.getMessage("applier-rollback-done"));
                }
            } else {
                // Build the results message
                builder.append(Il8n.formatMessage("applier-rollback-preview-start", result.getChangesPlanned()));
                if (result.getChangesSkipped() > 0) {
                    builder.append(Component.text(" "));
                    builder.append(Il8n.formatMessage("applier-changes-skipped", result.getChangesSkipped()));
                }
                if (result.getChangesPlanned() > 0) {
                    builder.append(Component.text(" "));
                    builder.append(Il8n.formatMessage("applier-preview-done", result.getChangesSkipped()));
                }
                // Let me know there's no need to cancel/apply
                if (result.getChangesPlanned() == 0) {
                    builder.append(Il8n.getMessage("preview-no-actions"));
                }
            }
        }
        // Build the results message
        if (result.getProcessType().equals(PrismProcessType.RESTORE)) {
            if (!result.isPreview()) {

                // Build the results message
                builder.append(Il8n.formatMessage("applier-restore-start", result.getChangesApplied()));
                if (result.getChangesSkipped() > 0) {
                    builder.append(Component.text(" "));
                    builder.append(Il8n.formatMessage("applier-changes-skipped", result.getChangesSkipped()));
                }
                if (result.getChangesApplied() > 0) {
                    builder.append(Component.text(" "));
                    builder.append(Il8n.getMessage("applier-restore-done"));
                }

            } else {

                // Build the results message
                builder.append(Il8n.formatMessage("applier-restore-preview-start", result.getChangesPlanned()));
                if (result.getChangesSkipped() > 0) {
                    builder.append(Il8n.formatMessage("applier-changes-skipped", result.getChangesSkipped()));
                }
                if (result.getChangesPlanned() > 0) {
                    builder.append(Component.text(" "));
                    builder.append(Il8n.formatMessage("applier-preview-done", result.getChangesSkipped()));
                }
                // Let me know there's no need to cancel/apply
                if (result.getChangesPlanned() == 0) {
                    builder.append(Il8n.getMessage("preview-no-actions"));
                }
            }
        }

        // Build the results message
        if (result.getProcessType().equals(PrismProcessType.UNDO)) {
            builder.append(Il8n.formatMessage("applier-undo-start", result.getChangesApplied()));
            // Build the results message
            if (result.getChangesSkipped() > 0) {
                builder.append(Component.text(" "));

                builder.append(Il8n.formatMessage("applier-changes-skipped", result.getChangesSkipped()));
            }
            if (result.getChangesApplied() > 0) {
                builder.append(Component.text(" "));
                builder.append(Il8n.getMessage("applier-undo-done"));
            }
        }
        Prism.messenger.sendMessage(sender,builder.build());
        // Notify shared players of previews
        for (final CommandSender sharedPlayer : result.getParameters().getSharedPlayers()) {
            Prism.messenger.sendMessage(sharedPlayer, Prism.messenger.playerHeaderMsg(
                    Il8n.formatMessage("applier-preview-shared", result.getParameters().getOriginalCommand())));
        }
    }
}