package dev.selena.econ;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import dev.selena.core.HytaleCore;
import dev.selena.econ.command.EconomyCommands;
import dev.selena.econ.command.sub.BalanceCommand;
import dev.selena.econ.command.sub.BalanceTopCommand;
import dev.selena.econ.command.sub.DepositCommand;
import dev.selena.econ.component.CurrencyComponent;
import dev.selena.econ.config.Configs;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class EconTale extends JavaPlugin {

    @Getter
    private static EconTale instance;

    public EconTale(@NotNull JavaPluginInit init) {
        super(init);
        instance = this;
        HytaleCore.setupCore(this);
        HytaleCore.loadAllConfigs(Configs.class);
    }

    @Override
    protected void setup() {
        super.setup();
        if (CurrencyComponent.getComponentType() == null) {
            CurrencyComponent.setComponentType(getEntityStoreRegistry().registerComponent(CurrencyComponent.class, "Hyconomy", CurrencyComponent.CODEC));
        }
        getCommandRegistry().registerCommand(new EconomyCommands());
        getCommandRegistry().registerCommand(new BalanceCommand());
        getCommandRegistry().registerCommand(new DepositCommand());
        getCommandRegistry().registerCommand(new BalanceTopCommand());
    }

    public void runOnPluginThread(Runnable task) {
        task.run();
    }

    public static void log(String message) {
        instance.getLogger().at(Level.INFO).log(message);
    }

}
