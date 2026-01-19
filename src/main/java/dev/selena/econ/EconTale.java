package dev.selena.econ;

import com.hypixel.hytale.common.plugin.PluginIdentifier;
import com.hypixel.hytale.common.semver.SemverRange;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import dev.selena.core.HytaleCore;
import dev.selena.econ.api.VaultUnlockedEconomyProvider;
import dev.selena.econ.command.EconomyCommands;
import dev.selena.econ.command.sub.BalanceCommand;
import dev.selena.econ.command.sub.BalanceTopCommand;
import dev.selena.econ.command.sub.DepositCommand;
import dev.selena.econ.component.CurrencyComponent;
import dev.selena.econ.config.Configs;
import lombok.Getter;
import net.cfh.vault.VaultUnlockedServicesManager;
import net.milkbowl.vault2.economy.Economy;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
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
            CurrencyComponent.setComponentType(getEntityStoreRegistry().registerComponent(CurrencyComponent.class, "EconTale", CurrencyComponent.CODEC));
        }
        getCommandRegistry().registerCommand(new EconomyCommands());
        getCommandRegistry().registerCommand(new BalanceCommand());
        getCommandRegistry().registerCommand(new DepositCommand());
        getCommandRegistry().registerCommand(new BalanceTopCommand());
    }

    @Override
    protected void start() {
        if (HytaleServer.get().getPluginManager().hasPlugin(
                PluginIdentifier.fromString("TheNewEconomy:VaultUnlocked"),
                SemverRange.WILDCARD
        )) {
            log("VaultUnlocked is installed, enabling VaultUnlocked support.");

            try {
                Class<?> providerClass = Class.forName("dev.selena.econ.api.VaultUnlockedEconomyProvider");
                Object providerInstance = providerClass.getDeclaredConstructor().newInstance();

                VaultUnlockedServicesManager.get().economy((Economy) providerInstance);
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                     NoSuchMethodException | InvocationTargetException e) {
                log("Failed to enable VaultUnlocked support: " + e.getMessage());
            }

        } else {
            log("VaultUnlocked is not installed, disabling VaultUnlocked support.");
        }
    }

    public void runOnPluginThread(Runnable task) {
        task.run();
    }

    public static void log(String message) {
        instance.getLogger().at(Level.INFO).log(message);
    }

}
