package dev.selena.econ.command.sub;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import dev.selena.core.util.PlaceholderUtil;
import dev.selena.core.util.PlayerUtil;
import dev.selena.econ.EconTale;
import dev.selena.econ.config.Config;
import dev.selena.econ.config.Lang;
import dev.selena.econ.consts.Placeholders;
import dev.selena.econ.util.CachedMessage;
import dev.selena.econ.util.CurrencyUtil;
import net.celestialpvp.utils.ColorUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class BalanceTopCommand extends AbstractAsyncCommand {
    public BalanceTopCommand() {
        super("balancetop", "Check the top players by balance", false);
        this.addAliases("baltop");
        requirePermission("econ.player");
    }

    private CachedMessage cacheBaltop = null;

    @NotNull
    @Override
    protected CompletableFuture<Void> executeAsync(@NotNull CommandContext commandContext) {
        if (commandContext.isPlayer()) {

            return CompletableFuture.runAsync(() -> {
                command(commandContext);
            }, commandContext.senderAsPlayerRef().getStore().getExternalData().getWorld());
        }
        return CompletableFuture.runAsync(() -> {
            command(commandContext);
        });
    }

    private void command(CommandContext commandContext) {
        File playerDirectory = new File("universe/players");
        if (!playerDirectory.exists() || !playerDirectory.isDirectory()) {
            EconTale.getInstance().getLogger().at(Level.SEVERE).log("Player directory not found.");
            return;
        }

        if (cacheBaltop != null && !cacheBaltop.isExpired()) {
            commandContext.sender().sendMessage(cacheBaltop.value());
            return;
        }

        Map<String, Double> balances = Arrays.stream(Objects.requireNonNull(playerDirectory.listFiles()))
                .filter(file -> file.getName().endsWith(".json") && !file.getName().endsWith(".json.bak"))
                .map(file -> {
                    try {
                        UUID uuid = UUID.fromString(file.getName().replace(".json", ""));
                        String playerName = PlayerUtil.getOfflineDisplayNameRaw(uuid);
                        double balance = CurrencyUtil.getOrAddCurrencyComponent(uuid).getBalance();
                        return new AbstractMap.SimpleEntry<>(playerName, balance);
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        List<Map.Entry<String, Double>> sortedBalances = balances.entrySet().stream()
                .sorted((entry1, entry2) -> Double.compare(entry2.getValue(), entry1.getValue()))
                .collect(Collectors.toList());

        StringBuilder message = new StringBuilder(Lang.get().getBalTopHeader()).append("\n");
        int leaderboardSize = Config.get().getLeaderboardSize();
        for (int i = 0; i < Math.min(sortedBalances.size(), leaderboardSize); i++) {
            Map.Entry<String, Double> entry = sortedBalances.get(i);
            String formattedEntry = PlaceholderUtil.parsePlaceholders(
                    Lang.get().getBalTopEntry(),
                    Placeholders.BALTOP_POSITION, String.valueOf(i + 1),
                    Placeholders.PLAYER, entry.getKey(),
                    Placeholders.BALANCE, CurrencyUtil.formatCurrency(entry.getValue())
            );
            message.append(formattedEntry).append("\n");
        }
        message.append(Lang.get().getBalTopFooter());
        cacheBaltop = new CachedMessage(System.currentTimeMillis() + Config.get().getBalTopCacheDurationMins() * 60000L, ColorUtils.parse(message.toString()));
        commandContext.sender().sendMessage(ColorUtils.parse(message.toString()));
    }

}
