package de.EinWildesCJay.Listeners;

import de.EinWildesCJay.Utils.ItemBuilder;
import de.EinWildesCJay.Vertrag;
import io.netty.handler.codec.compression.Bzip2Decoder;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class VertragsChatListener implements Listener {

    public static HashMap<Player, String> eingabe = new HashMap<>();

    @EventHandler
    public void onAsyncChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if (eingabe.containsKey(p)) {
            e.setCancelled(true);
            switch (eingabe.get(p)) {
                case "ErstellungVertragspartner":
                    if (VertragsGUIListener.lastInventory.get(p).getTitle().equals("§4§lVerträge §8⚊ §e§lErstellung")) {
                        if (e.getMessage().equalsIgnoreCase("stop")) {
                            eingabe.remove(p);
                            p.openInventory(VertragsGUIListener.vertragsInventory.get(p));
                            return;
                        }
                        try {
                            OfflinePlayer op = Bukkit.getOfflinePlayer(e.getMessage());
                            if (!op.hasPlayedBefore()) {
                                p.sendMessage("§c§l➽ §4§l✖ §r§c§l" + e.getMessage() + "§r§c hat noch nie auf dem Server gespielt.");
                                p.playSound(p.getLocation(), "notification_error", 1, 1);
                                return;
                            }
                            if (!VertragsGUIListener.vertragsPartner.containsKey(p)) {
                                List<OfflinePlayer> list = new ArrayList<>();
                                list.add(op);
                                VertragsGUIListener.vertragsPartner.put(p, list);
                            } else {
                                if (VertragsGUIListener.vertragsPartner.get(p).contains(op)) {
                                    p.sendMessage("§c§l➽ §4§l✖ §r§c§l" + e.getMessage() + "§r§c ist bereits als Vertragspartner angegeben.");
                                    p.playSound(p.getLocation(), "notification_error", 1, 1);
                                    return;
                                } else {
                                    VertragsGUIListener.vertragsPartner.get(p).add(op);
                                }
                            }

                            VertragsGUIListener.openPartnerInventory(p, null);
                            p.playSound(p.getLocation(), "notification_success", 1, 1);

                            eingabe.remove(p);
                        } catch (NullPointerException exe) {
                            p.sendMessage("§c§l➽ §4§l✖ §r§c§l" + e.getMessage() + "§r§c ist kein existierender Spieler.");
                            p.playSound(p.getLocation(), "notification_error", 1, 1);
                        }
                    } else {
                        if (e.getMessage().equalsIgnoreCase("stop")) {
                            eingabe.remove(p);
                            p.sendMessage("§a§l➽ §2§l✔ §r§aEingabe abgebrochen.");
                            p.playSound(p.getLocation(), "notification_success", 1, 1);
                            return;
                        }
                        try {
                            OfflinePlayer op1 = Bukkit.getOfflinePlayer(e.getMessage());
                            if (!op1.hasPlayedBefore()) {
                                p.sendMessage("§c§l➽ §4§l✖ §r§c§l" + e.getMessage() + "§r§c hat noch nie auf dem Server gespielt.");
                                p.playSound(p.getLocation(), "notification_error", 1, 1);
                                return;
                            }
                            Vertrag v = VertragsGUIListener.selectedVertrag.get(p);
                            if (v.getVertragspartner().contains(op1.getUniqueId())) {
                                p.sendMessage("§c§l➽ §4§l✖ §r§c§l" + op1.getName() + "§r§c ist bereits ein Vertragspartner.");
                                p.playSound(p.getLocation(), "notification_error", 1, 1);
                                return;
                            }
                            v.getVertragspartner().add(op1.getUniqueId());
                            if (op1.isOnline()) {
                                op1.getPlayer().sendMessage("§b§l➽ §f§lℹ §r§b" + p.getName() + " hat dir den Vertrag mit der ID §l" + v.getId() + "§r§b zur Signatur freigegeben. Nutze /vertrag, um auf deine Signaturanfragen zu zugreifen.");
                                op1.getPlayer().playSound(op1.getPlayer().getLocation(), "notification_info", 1, 1);
                            }
                            Inventory partnerInventory = Bukkit.createInventory(null, 9 * 3, "§4§lVerträge §8⚊ §e§lPartner");
                            for (int i = 1; i < 10; i++) {
                                partnerInventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, (short) 7).setDisplayName(" ").build());
                            }
                            for (int i = 17; i < partnerInventory.getSize(); i++) {
                                partnerInventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, (short) 7).setDisplayName(" ").build());
                            }
                            int currentSlot = 10;
                            partnerInventory.setItem(0, new ItemBuilder(Material.PAPER).setDisplayName("§7§l⬅  §c§lZurück").build());
                            if (!v.getVertragspartner().isEmpty()) {
                                for (int i = 0; i < v.getVertragspartner().size(); i++) {
                                    OfflinePlayer op = Bukkit.getOfflinePlayer(v.getVertragspartner().get(i));
                                    ItemStack playerSkull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                                    SkullMeta skullMeta = (SkullMeta) playerSkull.getItemMeta();
                                    skullMeta.setOwningPlayer(op);
                                    skullMeta.setDisplayName("§e§l" + op.getName());
                                    if (v.isSigniert(op.getUniqueId())) {
                                        skullMeta.setLore(Arrays.asList("§7Signiert§8: §a§lJA", "", "§7Klicke auf den Kopf, um den Spieler aus dem Vertrag zu entfernen."));
                                    } else {
                                        skullMeta.setLore(Arrays.asList("§7Signiert§8: §c§lNEIN", "", "§7Klicke auf den Kopf, um den Spieler aus dem Vertrag zu entfernen."));
                                    }
                                    playerSkull.setItemMeta(skullMeta);
                                    partnerInventory.setItem(currentSlot, playerSkull);
                                    currentSlot++;
                                }
                                if (currentSlot <= 16) {
                                    partnerInventory.setItem(currentSlot, new ItemBuilder(Material.BOOK_AND_QUILL).setDisplayName("§a§lVertragspartner hinzufügen").setLore("§7Gib einen Spieler an, den du als Vertragspartner hinzufügen willst.").build());
                                }
                            } else {
                                partnerInventory.setItem(currentSlot, new ItemBuilder(Material.BOOK_AND_QUILL).setDisplayName("§a§lVertragspartner hinzufügen").setLore("§7Gib einen Spieler an, den du als Vertragspartner hinzufügen willst.").build());
                            }
                            p.openInventory(partnerInventory);
                            p.playSound(p.getLocation(), "notification_success", 1, 1);

                            eingabe.remove(p);
                        } catch (NullPointerException exe) {
                            p.sendMessage("§c§l➽ §4§l✖ §r§c§l" + e.getMessage() + "§r§c ist kein existierender Spieler.");
                            p.playSound(p.getLocation(), "notification_error", 1, 1);
                        }
                    }
                    break;
                case "SucheSpieler":
                    if (e.getMessage().equalsIgnoreCase("stop")) {
                        eingabe.remove(p);
                        p.sendMessage("§a§l➽ §2§l✔ §r§aSuche abgebrochen.");
                        p.playSound(p.getLocation(), "notification_success", 1, 1);
                        return;
                    }

                    try {
                        OfflinePlayer op = Bukkit.getOfflinePlayer(e.getMessage());

                        p.closeInventory();
                        int currentSlot = 10;
                        Inventory vertrageInventory = Bukkit.createInventory(null, 9 * 5, "§4§lVerträge §8⚊ §e§lVerträge (Team)");
                        for (int i = 0; i < 8; i++) {
                            vertrageInventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, (short) 7).setDisplayName(" ").build());
                        }
                        for (int i = 45; i < vertrageInventory.getSize(); i++) {
                            vertrageInventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, (short) 7).setDisplayName(" ").build());
                        }
                        for (int i = 9; i < vertrageInventory.getSize(); i = i + 9) {
                            vertrageInventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, (short) 7).setDisplayName(" ").build());
                        }
                        for (int i = 8; i < vertrageInventory.getSize(); i = i + 9) {
                            vertrageInventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, (short) 7).setDisplayName(" ").build());
                        }
                        for (int i = vertrageInventory.getSize() - 9; i < vertrageInventory.getSize(); i++) {
                            vertrageInventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, (short) 7).setDisplayName(" ").build());
                        }

                        for (Vertrag v : VertragsGUIListener.vertraege) {
                            if (v.getErsteller().equals(op.getUniqueId()) || v.getVertragspartner().contains(op.getUniqueId())) {
                                if (vertrageInventory.getItem(currentSlot) != null) {
                                    currentSlot = currentSlot + 2;
                                }
                                OfflinePlayer ersteller = Bukkit.getOfflinePlayer(v.getErsteller());
                                String status = "";
                                vertrageInventory.setItem(currentSlot, new ItemBuilder(Material.BOOK).setDisplayName("§e§lID: " + v.getId()).setLore("§7Ersteller§8: §f" + ersteller.getName(), "§7Datum§8: §f" + v.getDatum(), "", "§7Linksklick, um den Vertrag zu öffnen").build());
                                currentSlot++;
                            }
                        }

                        p.openInventory(vertrageInventory);
                        p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1, 1);

                        eingabe.remove(p);
                    } catch (NullPointerException exe) {
                        p.sendMessage("§c§l➽ §4§l✖ §r§c§l" + e.getMessage() + "§r§c ist kein existierender Spieler.");
                        p.playSound(p.getLocation(), "notification_error", 1, 1);
                    }
                    break;
                case "SucheVertrag":
                    if (e.getMessage().equalsIgnoreCase("stop")) {
                        eingabe.remove(p);
                        p.sendMessage("§a§l➽ §2§l✔ §r§aSuche abgebrochen.");
                        p.playSound(p.getLocation(), "notification_success", 1, 1);
                        return;
                    }
                    Vertrag vertrag = null;
                    for (Vertrag v : VertragsGUIListener.vertraege) {
                        if (v.getId().equalsIgnoreCase(e.getMessage())) {
                            vertrag = v;
                        }
                    }
                    if (vertrag == null) {
                        p.sendMessage("§c§l➽ §4§l✖ §r§c§l" + e.getMessage() + "§r§c ist keine exisitierende Vertrags-ID.");
                        p.playSound(p.getLocation(), "notification_error", 1, 1);
                        return;
                    }
                    Inventory vertragsInventory = Bukkit.createInventory(null, 9 * 3, "§4§lVerträge §8⚊ §e§lVertrag (Team)");
                    for (int i = 0; i < vertragsInventory.getSize(); i++) {
                        vertragsInventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, (short) 7).setDisplayName(" ").build());
                    }
                    OfflinePlayer op = Bukkit.getOfflinePlayer(vertrag.getErsteller());
                    vertragsInventory.setItem(10, new ItemBuilder(Material.PAPER).setDisplayName("§b§lInformationen zum Vertrag").setLore("§7Titel§8: §f" + vertrag.getTitel(), "§7Ersteller§8: §f" + op.getName(), "§7Datum§8: §f" + vertrag.getDatum()).build());
                    vertragsInventory.setItem(11, new ItemBuilder(Material.BOOK).setDisplayName("§e§lVertrag").setLore("§7Öffne den Vertrag.").build());
                    vertragsInventory.setItem(12, new ItemBuilder(Material.SKULL_ITEM, (short) 3).setDisplayName("§e§lVertragspartner").setLore("§7Lass dir die Vertragspartner des Vertrages anzeigen.").build());
                    vertragsInventory.setItem(16, new ItemBuilder(Material.BARRIER).setDisplayName("§c§lAuflösen").setLore("§7Shift + Rechtsklick, um den Vertrag aufzulösen.").build());
                    p.openInventory(vertragsInventory);
                    p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1, 1);
                    VertragsGUIListener.selectedVertrag.put(p, vertrag);
                    p.closeInventory();
                    p.openInventory(vertragsInventory);
                    p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1, 1);
                    eingabe.remove(p);
                    break;
                default:

            }
        }
    }

    public void openPartnerInventory(Player p, Vertrag v) {

    }

}
