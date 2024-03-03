package de.EinWildesCJay;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import de.EinWildesCJay.Commands.VertragCommand;
import de.EinWildesCJay.Listeners.VertragsChatListener;
import de.EinWildesCJay.Listeners.VertragsGUIListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main instance;

    @Override
    public void onEnable() {
        ProtocolManager pm = ProtocolLibrary.getProtocolManager();
        Bukkit.getConsoleSender().sendMessage("§aDas Vertragssystem wurde gestartet.");
        getCommand("vertrag").setExecutor(new VertragCommand());
        Bukkit.getPluginManager().registerEvents(new VertragsGUIListener(), this);
        Bukkit.getPluginManager().registerEvents(new VertragsChatListener(), this);
    }

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("§aDas Vertragssystem wurde gestoppt.");
    }

    public static Main getInstance() {
        return instance;
    }


}
