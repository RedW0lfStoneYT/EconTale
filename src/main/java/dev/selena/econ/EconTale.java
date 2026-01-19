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
//        if (HytaleServer.get().getPluginManager().hasPlugin(
//                PluginIdentifier.fromString("TheNewEconomy:VaultUnlocked"),
//                SemverRange.WILDCARD
//        )) {
//            log("VaultUnlocked is installed, enabling VaultUnlocked support.");
//
//            VaultUnlockedServicesManager.get().economy(new VaultUnlockedEconomyProvider());
//        } else {
//            log("VaultUnlocked is not installed, disabling VaultUnlocked support.");
//        }

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
        VaultUnlockedServicesManager services = VaultUnlockedServicesManager.get();
        services.economy(new VaultUnlockedEconomyProvider());
    }

    public void runOnPluginThread(Runnable task) {
        task.run();
    }

    public static void log(String message) {
        instance.getLogger().at(Level.INFO).log(message);
    }

}
