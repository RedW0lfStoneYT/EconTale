package dev.selena.econ.command.sub;

import com.hypixel.hytale.server.core.auth.ProfileServiceClient;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.modules.entity.component.DisplayNameComponent;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import dev.selena.core.util.PlaceholderUtil;
import dev.selena.core.util.PlayerUtil;
import dev.selena.econ.config.Lang;
import dev.selena.econ.consts.Placeholders;
import dev.selena.econ.util.CurrencyUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class BalanceCommand extends AbstractAsyncCommand {

    private final OptionalArg<ProfileServiceClient.PublicGameProfile> playerArg;

    public BalanceCommand() {
        super("balance", "Check a players balance");
        this.addAliases("bal", "money");
        playerArg = this.withOptionalArg("player", "The player to check the balance of", ArgTypes.GAME_PROFILE_LOOKUP);
    }

    @NotNull
    @Override
    protected CompletableFuture<Void> executeAsync(@NotNull CommandContext commandContext) {
        if (playerArg.get(commandContext) == null && !commandContext.isPlayer()) {
            commandContext.sender().sendMessage(PlaceholderUtil.parsePlaceholdersToMessage(Lang.get().getPlayerOnlyCommandConsole()));
            return CompletableFuture.runAsync(()->{});
        }
        ProfileServiceClient.PublicGameProfile targetProfile = playerArg.get(commandContext);
        UUID targetUuid = commandContext.sender().getUuid();
        boolean isSelf;
        if (targetProfile != null) {
            targetUuid = targetProfile.getUuid();
            isSelf = false;
        } else {
            isSelf = true;
        }
        if (PlayerUtil.isOnline(targetUuid)) {
            World world = Universe.get().getWorld(Objects.requireNonNull(Objects.requireNonNull(Universe.get().getPlayer(targetUuid)).getWorldUuid()));
            UUID finalTargetUuid = targetUuid;
            assert world != null;
            return CompletableFuture.runAsync(() -> {
                command(commandContext, finalTargetUuid, isSelf);
            }, world);
        }
        UUID finalTargetUuid1 = targetUuid;
        return CompletableFuture.runAsync(() -> {
            command(commandContext, finalTargetUuid1, isSelf);
        });
    }

    private void command(CommandContext commandContext, UUID uuid, boolean isSelf) {
        double balance = CurrencyUtil.getOrAddCurrencyComponent(uuid).getBalance();
        String formattedBalance = CurrencyUtil.formatCurrency(balance);
        DisplayNameComponent playerRef = PlayerUtil.getComponentOffline(uuid, DisplayNameComponent.getComponentType());
        if (!isSelf) {
            commandContext.sender().sendMessage(PlaceholderUtil.parsePlaceholdersToMessage(
                    Lang.get().getBalanceOther(), Placeholders.TARGET_PLAYER, playerRef.getDisplayName().getRawText(), Placeholders.BALANCE, formattedBalance));
            return;
        }
        commandContext.sender().sendMessage(PlaceholderUtil.parsePlaceholdersToMessage(
                Lang.get().getBalanceSelf(), Placeholders.BALANCE, formattedBalance));
    }


}
