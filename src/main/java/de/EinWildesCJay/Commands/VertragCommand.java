package de.EinWildesCJay.Commands;

import de.EinWildesCJay.Utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class VertragCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender Sender, Command command, String s, String[] args) {

        if (Sender instanceof Player) {
            Player p = (Player) Sender;

            Inventory vertragsInventory = Bukkit.createInventory(null, 9 * 3, "§4§lVerträge §8⚊ §e§lÜbersicht");

            for (int i = 0; i < vertragsInventory.getSize(); i++) {
                vertragsInventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, (short) 7).setDisplayName(" ").build());
            }

            vertragsInventory.setItem(10, new ItemBuilder(Material.BOOK).setDisplayName("§a§lDeine Verträge").setLore("§7Übersicht über die Verträge, die du erstellt hast.").build());
            vertragsInventory.setItem(11, new ItemBuilder(Material.ENCHANTED_BOOK).setDisplayName("§c§lUnterzeichnete Verträge").setLore("§7Übersicht über die Verträge, die du unterzeichnet hast.").build());
            vertragsInventory.setItem(12, new ItemBuilder(Material.BOOK_AND_QUILL).setDisplayName("§e§lSignaturanfragen").setLore("§7Anfragen von Spielern, welche einen Vertrag mit dir abschließen wollen.").build());
            vertragsInventory.setItem(16, new ItemBuilder(Material.KNOWLEDGE_BOOK).setDisplayName("§a§lNeuen Vertrag erstellen").setLore("§7Erstelle aus einem signiertem Buch einen neuen Vertrag.").build());

            if (p.hasPermission("Vertrag.Team")) {
                vertragsInventory.setItem(14, new ItemBuilder(Material.EYE_OF_ENDER).setDisplayName("§6§lTeammenü").setLore("§7Menü zum Überwachen von abgeschlossenen Verträgen.").build());
            }

            p.openInventory(vertragsInventory);
            p.playSound(p.getLocation(), Sound.BLOCK_CHEST_OPEN, 1, 1);
        } else {
            Bukkit.getConsoleSender().sendMessage("§cDu kannst diesen Command nur als Spieler ausführen.");
        }

        return false;
    }
}
