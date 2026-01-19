package dev.selena.econ.command;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.command.system.pages.CommandListPage;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.selena.econ.command.sub.*;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;


public class EconomyCommands extends CommandBase {

    public EconomyCommands() {
        super("economy", "Economy related commands", false);
        this.addSubCommand(new BalanceCommand());
        this.addSubCommand(new DepositCommand());
        this.addSubCommand(new AdminAddCommand());
        this.addSubCommand(new AdminRemoveCommand());
        this.addSubCommand(new AdminSetCommand());
        this.addSubCommand(new BalanceTopCommand());
        this.addSubCommand(new WithdrawCommand());
        this.addAliases("econ", "eco");
    }

    @Override
    protected void executeSync(@NotNull CommandContext commandContext) {
        Ref<EntityStore> playerRefStore = commandContext.senderAsPlayerRef();
        World world = playerRefStore.getStore().getExternalData().getWorld();
        CompletableFuture.runAsync(() -> {
            PlayerRef playerRef = playerRefStore.getStore().getComponent(playerRefStore, PlayerRef.getComponentType());
            Player player = playerRefStore.getStore().getComponent(playerRefStore, Player.getComponentType());
            player.getPageManager().openCustomPage(playerRefStore, playerRefStore.getStore(), new CommandListPage(playerRef, this.getName()));
        }, world
        );
    }
}
