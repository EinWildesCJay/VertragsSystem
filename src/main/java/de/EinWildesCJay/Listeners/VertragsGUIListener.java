package de.EinWildesCJay.Listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.sun.media.sound.ModelByteBuffer;
import de.EinWildesCJay.Utils.ItemBuilder;
import de.EinWildesCJay.Vertrag;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.compression.Bzip2Decoder;
import org.apache.logging.log4j.util.ReflectionUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class VertragsGUIListener implements Listener {

    ArrayList<String> usedIds = new ArrayList<>();
    public static ArrayList<Vertrag> vertraege = new ArrayList<>();
    public static HashMap<Player, Inventory> vertragsInventory = new HashMap<>();
    static HashMap<Player, Vertrag> selectedVertrag = new HashMap<>();
    HashMap<Player, ItemStack> selectedBook = new HashMap<>();
    public static HashMap<Player, List<OfflinePlayer>> vertragsPartner = new HashMap<>();
    public static HashMap<Player, Inventory> lastInventory = new HashMap<>();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getView().getTitle().equals("§4§lVerträge §8⚊ §e§lÜbersicht")) {
            if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta() && e.getClickedInventory().contains(e.getCurrentItem())) {
                e.setCancelled(true);
                switch (e.getCurrentItem().getItemMeta().getDisplayName()) {
                    case "§a§lDeine Verträge":
                        p.closeInventory();
                        int currentSlot = 10;
                        Inventory vertrageInventory = Bukkit.createInventory(null, 9 * 5, "§4§lVerträge §8⚊ §e§lVerträge");
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

                        for (Vertrag v : vertraege) {
                            if (v.getErsteller().equals(p.getUniqueId())) {
                                if (vertrageInventory.getItem(currentSlot) != null) {
                                    currentSlot = currentSlot + 2;
                                }
                                vertrageInventory.setItem(currentSlot, new ItemBuilder(Material.BOOK).setDisplayName("§e§lID: " + v.getId()).setLore("§7Titel§8: §f" + v.getTitel(), "§7Ersteller§8: §f" + p.getName(), "§7Datum§8: §f" + v.getDatum(), "", "§7Linksklick, um den Vertrag zu öffnen").build());
                                currentSlot++;
                            }
                        }

                        p.openInventory(vertrageInventory);
                        p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1, 1);

                        break;
                    case "§c§lUnterzeichnete Verträge":
                        p.closeInventory();
                        int slot = 10;
                        Inventory signedInventory = Bukkit.createInventory(null, 9 * 5, "§4§lVerträge §8⚊ §e§lUnterzeichnet");
                        for (int i = 0; i < 8; i++) {
                            signedInventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, (short) 7).setDisplayName(" ").build());
                        }
                        for (int i = 45; i < signedInventory.getSize(); i++) {
                            signedInventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, (short) 7).setDisplayName(" ").build());
                        }
                        for (int i = 9; i < signedInventory.getSize(); i = i + 9) {
                            signedInventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, (short) 7).setDisplayName(" ").build());
                        }
                        for (int i = 8; i < signedInventory.getSize(); i = i + 9) {
                            signedInventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, (short) 7).setDisplayName(" ").build());
                        }
                        for (int i = signedInventory.getSize() - 9; i < signedInventory.getSize(); i++) {
                            signedInventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, (short) 7).setDisplayName(" ").build());
                        }
                        for (Vertrag v : vertraege) {
                            if (v.getVertragspartner().contains(p.getUniqueId()) && v.isSigniert(p.getUniqueId())) {
                                if (signedInventory.getItem(slot) != null) {
                                    slot = slot + 2;
                                }
                                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(v.getErsteller());
                                signedInventory.setItem(slot, new ItemBuilder(Material.BOOK).setDisplayName("§e§lID: " + v.getId()).setLore("§7Titel§8: §f" + v.getTitel(), "§7Ersteller§8: §f" + offlinePlayer.getName(), "§7Datum§8: §f" + v.getDatum(), "", "§7Linksklick, um den Vertrag zu öffnen").build());
                                slot++;
                            }
                        }

                        p.openInventory(signedInventory);
                        p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1, 1);

                        break;
                    case "§e§lSignaturanfragen":
                        p.closeInventory();
                        int cSlot = 10;
                        Inventory requestInventory = Bukkit.createInventory(null, 9 * 5, "§4§lVerträge §8⚊ §e§lAnfragen");
                        for (int i = 0; i < 8; i++) {
                            requestInventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, (short) 7).setDisplayName(" ").build());
                        }
                        for (int i = 45; i < requestInventory.getSize(); i++) {
                            requestInventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, (short) 7).setDisplayName(" ").build());
                        }
                        for (int i = 9; i < requestInventory.getSize(); i = i + 9) {
                            requestInventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, (short) 7).setDisplayName(" ").build());
                        }
                        for (int i = 8; i < requestInventory.getSize(); i = i + 9) {
                            requestInventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, (short) 7).setDisplayName(" ").build());
                        }
                        for (int i = requestInventory.getSize() - 9; i < requestInventory.getSize(); i++) {
                            requestInventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, (short) 7).setDisplayName(" ").build());
                        }
                        for (Vertrag v : vertraege) {
                            if (v.getVertragspartner().contains(p.getUniqueId()) && !v.isSigniert(p.getUniqueId())) {
                                if (requestInventory.getItem(cSlot) != null) {
                                    cSlot = cSlot + 2;
                                }
                                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(v.getErsteller());
                                requestInventory.setItem(cSlot, new ItemBuilder(Material.BOOK).setDisplayName("§e§lID: " + v.getId()).setLore("§7Titel§8: §f" + v.getTitel(), "§7Ersteller§8: §f" + offlinePlayer.getName(), "§7Datum§8: §f" + v.getDatum(), "", "§7Linksklick, um den Vertrag zu öffnen").build());
                                cSlot++;
                            }
                        }

                        p.openInventory(requestInventory);
                        p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1, 1);

                        break;
                    case "§a§lNeuen Vertrag erstellen":
                        if (p.getInventory().getItemInMainHand() != null) {
                            if (p.getInventory().getItemInMainHand().getType().equals(Material.WRITTEN_BOOK)) {
                                if (vertragsPartner.containsKey(p)) {
                                    vertragsPartner.remove(p);
                                }
                                BookMeta bookMeta = (BookMeta) p.getInventory().getItemInMainHand().getItemMeta();
                                if (!bookMeta.hasAuthor()) {
                                    p.sendMessage("§e§l➽ §c§l⚠ §r§eDu hälst kein signiertes Buch in der Hand.");
                                    p.playSound(p.getLocation(), "notification_warning", 1, 1);
                                    p.closeInventory();
                                    return;
                                }
                                if (bookMeta.getPageCount() == 50) {
                                    p.sendMessage("§e§l➽ §c§l⚠ §r§eDas Buch darf nicht mehr als 49 Seiten umfassen.");
                                    p.playSound(p.getLocation(), "notification_warning", 1, 1);
                                    p.closeInventory();
                                    return;
                                }

                                Inventory createVertragInventory = Bukkit.createInventory(null, 9 * 3, "§4§lVerträge §8⚊ §e§lErstellung");

                                for (int i = 0; i < createVertragInventory.getSize(); i++) {
                                    createVertragInventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, (short) 7).setDisplayName(" ").build());
                                }

                                ItemStack vertragsBuch = p.getInventory().getItemInMainHand().clone();
                                ItemMeta vertragsMeta = vertragsBuch.getItemMeta();

                                vertragsMeta.setDisplayName("§a§lVertrag");
                                vertragsBuch.setItemMeta(vertragsMeta);

                                createVertragInventory.setItem(10, p.getInventory().getItemInMainHand());
                                createVertragInventory.setItem(11, new ItemBuilder(Material.SKULL_ITEM, (short) 3).setDisplayName("§a§lVertragspartner").setLore("§7Gib hier den Spieler an, mit welchem du diesen Vertrag abschließen möchtest.").build());
                                createVertragInventory.setItem(16, new ItemBuilder(Material.BOOK_AND_QUILL).setDisplayName("§e§lAbschließen").setLore("§7Mit dem Abschließen des Vertrages, gibst du ihn zur Unterschrift an den Vertragspartner frei.").build());

                                p.openInventory(createVertragInventory);
                                p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1, 1);
                                selectedBook.put(p, vertragsBuch);
                            } else {
                                p.sendMessage("§e§l➽ §c§l⚠ §r§eDu hälst kein signiertes Buch in der Hand.");
                                p.playSound(p.getLocation(), "notification_warning", 1, 1);
                                p.closeInventory();
                                return;
                            }
                        }
                        break;
                    case "§6§lTeammenü":
                        Inventory teamInventory = Bukkit.createInventory(null, 9 * 3, "§4§lVerträge §8⚊ §e§lTeam");
                        for (int i = 0; i < teamInventory.getSize(); i++) {
                            teamInventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, (short) 7).setDisplayName(" ").build());
                        }
                        teamInventory.setItem(11, new ItemBuilder(Material.SKULL_ITEM, (short) 3).setDisplayName("§e§lSpielersuche").setLore("§7Suche nach Verträgen, welche entweder den Spieler als Ersteller oder Vertragspartner haben.").build());
                        teamInventory.setItem(15, new ItemBuilder(Material.ENCHANTED_BOOK).setDisplayName("§e§lVertragssuche").setLore("§7Suche nach einem Vertrag unter Angabe der Vertrags-ID.").build());
                        p.closeInventory();
                        p.openInventory(teamInventory);
                        p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1, 1);
                        break;
                }
            }
            return;
        }
        if (e.getView().getTitle().equals("§4§lVerträge §8⚊ §e§lErstellung")) {
            if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta() && e.getClickedInventory().contains(e.getCurrentItem())) {
                e.setCancelled(true);
                switch (e.getCurrentItem().getItemMeta().getDisplayName()) {
                    case "§a§lVertragspartner":
                        /*if (vertragsPartner.containsKey(p)) {
                            if (vertragsPartner.get(p).size() == 7) {
                                p.sendMessage("§c§l➽ §4§l✖ §r§cDu kannst nicht mehr als sieben Vertragspartner angeben.");
                                p.playSound(p.getLocation(), "notification_error", 1, 1);
                            }
                        }*/
                        p.closeInventory();
                        openPartnerInventory(p, e.getClickedInventory());
                        p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1, 1);
                        break;
                    case "§e§lAbschließen":
                        if (!vertragsPartner.containsKey(p) || vertragsPartner.get(p).isEmpty()) {
                            p.sendMessage("§c§l➽ §4§l✖ §r§cDu hast noch keinen Vertragspartner angegeben.");
                            p.playSound(p.getLocation(), "notification_error", 1, 1);
                            return;
                        }
                        BookMeta bookMeta = (BookMeta) selectedBook.get(p).getItemMeta();
                        String id = generateNewID();
                        LocalDateTime localDateTime = LocalDateTime.now();
                        String datumundzeit = localDateTime.format(DateTimeFormatter.ofPattern("dd. MM. uuuu - HH:mm"));
                        bookMeta.addPage("§c§lVERTRAGSSYSTEM\n\n§8Vertrags-ID: §c" + id + "\n§8Ersteller: §c" + p.getName() + "\n§8Erstellung: §c" + datumundzeit);
                        Vertrag vertrag = new Vertrag(id, bookMeta.getTitle(), datumundzeit, p.getUniqueId(), bookMeta.getPages());
                        for (OfflinePlayer op : vertragsPartner.get(p)) {
                            vertrag.addVertragspartner(op.getUniqueId());
                            if (op.isOnline()) {
                                Player t = op.getPlayer();
                                t.sendMessage("§b§l➽ §f§lℹ §r§b" + p.getName() + " hat dir den Vertrag mit der ID §l" + id + "§r§b zur Signatur freigegeben. Nutze /vertrag, um auf deine Signaturanfragen zu zugreifen.");
                                t.playSound(t.getLocation(), "notification_info", 1, 1);
                            }
                        }
                        vertraege.add(vertrag);
                        p.sendMessage("§a§l➽ §2§l✔ §r§aDu hast den Vertrag mit der ID §l" + id + "§r§a erstellt. Du musst nun auf die Signatur von deinen Vertragspartnern warten, damit er gültig ist.");
                        p.closeInventory();
                        p.playSound(p.getLocation(), "notification_success", 1, 1);
                        selectedBook.remove(p);
                        vertragsInventory.remove(p);
                        vertragsPartner.remove(p);
                        break;
                    default:
                        break;
                }
            }
            return;
        }
        if (e.getView().getTitle().equals("§4§lVerträge §8⚊ §e§lPartner")) {
            if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta() && e.getClickedInventory().contains(e.getCurrentItem())) {
                e.setCancelled(true);
                if (e.getCurrentItem().getType().equals(Material.SKULL_ITEM)) {
                    if (e.getSlot() >= 10) {
                        String playerName = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
                        if (lastInventory.get(p).getTitle().equals("§4§lVerträge §8⚊ §e§lErstellung")) {
                            p.closeInventory();
                            vertragsPartner.get(p).remove(Bukkit.getOfflinePlayer(playerName));
                            openPartnerInventory(p, null);
                            p.playSound(p.getLocation(), "notification_success", 1, 1);
                        } else {
                            Vertrag v = VertragsGUIListener.selectedVertrag.get(p);
                            OfflinePlayer op1 = Bukkit.getOfflinePlayer(Bukkit.getPlayerUniqueId(playerName));
                            if (op1.isOnline()) {
                                op1.getPlayer().sendMessage("§b§l➽ §f§lℹ §r§b§l" + p.getName() + "§r§b hat dich als Vertragspartner aus dem Vertrag mit der ID §l" + selectedVertrag.get(p).getId() + " §r§bentfernt.");
                                op1.getPlayer().playSound(op1.getPlayer().getLocation(), "notification_info", 1, 1);
                            }
                            if (v.isSigniert(Bukkit.getPlayerUniqueId(playerName))) {
                                v.removeSignatur(Bukkit.getPlayerUniqueId(playerName));
                            }

                            v.removeVertragspartner(Bukkit.getPlayerUniqueId(playerName));
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
                        }
                    }
                } else {
                    if (e.getSlot() == 0) {
                        p.openInventory(lastInventory.get(p));
                        p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1, 1);
                        return;
                    }
                    if (e.getCurrentItem().getType().equals(Material.BOOK_AND_QUILL)) {
                        p.sendMessage("§b§l➽ §f§lℹ §r§bGib im Chat den §lNamen des Spielers§r§b an, welchen du zum Vertrag hinzufügen möchtest. Schreibe \"stop\" in den Chat, um die Eingabe abzubrechen.");
                        p.playSound(p.getLocation(), "notification_info", 1, 1);
                        VertragsChatListener.eingabe.put(p, "ErstellungVertragspartner");
                        p.closeInventory();
                    }
                }
            }
            return;
        }
        if (e.getView().getTitle().equals("§4§lVerträge §8⚊ §e§lVerträge")) {
            e.setCancelled(true);
            if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta() && e.getClickedInventory().contains(e.getCurrentItem()) && e.getCurrentItem().getType() == Material.BOOK) {
                String id = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).replace("ID: ", "");
                for (Vertrag v : vertraege) {
                    if (v.getId().equals(id)) {
                        Inventory vertragsInventory = Bukkit.createInventory(null, 9 * 3, "§4§lVerträge §8⚊ §e§lVertrag");
                        for (int i = 0; i < vertragsInventory.getSize(); i++) {
                            vertragsInventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, (short) 7).setDisplayName(" ").build());
                        }
                        OfflinePlayer op = Bukkit.getOfflinePlayer(v.getErsteller());
                        vertragsInventory.setItem(10, new ItemBuilder(Material.PAPER).setDisplayName("§b§lInformationen zum Vertrag").setLore("§7Titel§8: §f" + v.getTitel(), "§7Ersteller§8: §f" + op.getName(), "§7Datum§8: §f" + v.getDatum()).build());
                        vertragsInventory.setItem(11, new ItemBuilder(Material.BOOK).setDisplayName("§e§lVertrag").setLore("§7Öffne den Vertrag.").build());
                        vertragsInventory.setItem(12, new ItemBuilder(Material.SKULL_ITEM, (short) 3).setDisplayName("§e§lVertragspartner").setLore("§7Verwalte die Vertragspartner.").build());
                        vertragsInventory.setItem(16, new ItemBuilder(Material.BARRIER).setDisplayName("§c§lAuflösen").setLore("§7Shift + Rechtsklick, um den Vertrag aufzulösen.").build());
                        p.openInventory(vertragsInventory);
                        p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1, 1);
                        selectedVertrag.put(p, v);
                    }
                }
            }
            return;
        }
        if (e.getView().getTitle().equals("§4§lVerträge §8⚊ §e§lAnfragen")) {
            e.setCancelled(true);
            if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta() && e.getClickedInventory().contains(e.getCurrentItem()) && e.getCurrentItem().getType() == Material.BOOK) {
                String id = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).replace("ID: ", "");
                for (Vertrag v : vertraege) {
                    if (v.getId().equals(id)) {
                        Inventory vertragsInventory = Bukkit.createInventory(null, 9 * 3, "§4§lVerträge §8⚊ §e§lSignatur");
                        for (int i = 0; i < vertragsInventory.getSize(); i++) {
                            vertragsInventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, (short) 7).setDisplayName(" ").build());
                        }
                        vertragsInventory.setItem(11, new ItemBuilder(Material.BOOK).setDisplayName("§e§lVertrag ansehen").setLore("§7Schaue dir den Vertrag an, bevor du ihn unterschreibst.").build());
                        vertragsInventory.setItem(15, new ItemBuilder(Material.BOOK_AND_QUILL).setDisplayName("§a§lVertrag unterschreiben").setLore("§7Mit dem Unterschreiben werden die Bedingungen des Vertrages gültig.").build());
                        p.closeInventory();
                        p.openInventory(vertragsInventory);
                        p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1, 1);
                        selectedVertrag.put(p, v);
                    }
                }
            }
            return;
        }
        if (e.getView().getTitle().equals("§4§lVerträge §8⚊ §e§lSignatur")) {
            e.setCancelled(true);
            if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta() && e.getClickedInventory().contains(e.getCurrentItem())) {
                switch (e.getCurrentItem().getItemMeta().getDisplayName()) {
                    case "§e§lVertrag ansehen":
                        Vertrag v = selectedVertrag.get(p);
                        ItemStack old = p.getInventory().getItemInMainHand();
                        ItemStack book = new ItemBuilder(Material.WRITTEN_BOOK).build();
                        BookMeta bookMeta = (BookMeta) book.getItemMeta();
                        bookMeta.setPages(v.getContent());
                        bookMeta.setDisplayName("§eVertragskopie: " + v.getId());
                        bookMeta.setAuthor(p.getName());
                        book.setItemMeta(bookMeta);
                        p.getInventory().setItem(p.getInventory().getHeldItemSlot(), book);
                        openBook(p, book);
                        p.getInventory().setItem(p.getInventory().getHeldItemSlot(), old);
                        break;
                    case "§a§lVertrag unterschreiben":
                        Vertrag vertrag = selectedVertrag.get(p);
                        vertrag.setSigniert(p.getUniqueId());
                        p.sendMessage("§a§l➽ §2§l✔ §r§aDu hast den Vertrag mit der ID §l" + vertrag.getId() + " §r§aunterschrieben.");
                        p.playSound(p.getLocation(), "notification_success", 1, 1);
                        p.closeInventory();
                        OfflinePlayer op = Bukkit.getOfflinePlayer(vertrag.getErsteller());
                        if (op.isOnline()) {
                            op.getPlayer().sendMessage("§b§l➽ §f§lℹ §r§b" + p.getName() + " hat den Vertrag mit dir mit der ID §l" + vertrag.getId() + " §r§bunterschrieben.");
                            op.getPlayer().playSound(op.getPlayer().getLocation(), "notification_info", 1, 1);
                        }
                        break;
                }
            }
            return;
        }
        if (e.getView().getTitle().equals("§4§lVerträge §8⚊ §e§lUnterzeichnet")) {
            e.setCancelled(true);
            if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta() && e.getClickedInventory().contains(e.getCurrentItem()) && e.getCurrentItem().getType().equals(Material.BOOK)) {
                String id = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).replace("ID: ", "");
                for (Vertrag v : vertraege) {
                    if (v.getId().equals(id)) {
                        ItemStack old = p.getInventory().getItemInMainHand();
                        ItemStack book = new ItemBuilder(Material.WRITTEN_BOOK).build();
                        BookMeta bookMeta = (BookMeta) book.getItemMeta();
                        bookMeta.setPages(v.getContent());
                        bookMeta.setDisplayName("§eVertragskopie: " + v.getId());
                        bookMeta.setAuthor(p.getName());
                        book.setItemMeta(bookMeta);
                        p.getInventory().setItem(p.getInventory().getHeldItemSlot(), book);
                        openBook(p, book);
                        p.getInventory().setItem(p.getInventory().getHeldItemSlot(), old);
                    }
                }
            }
            return;
        }
        if (e.getView().getTitle().equals("§4§lVerträge §8⚊ §e§lTeam")) {
            e.setCancelled(true);
            if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta() && e.getClickedInventory().contains(e.getCurrentItem())) {
                switch (e.getCurrentItem().getItemMeta().getDisplayName()) {
                    case "§e§lSpielersuche":
                        p.closeInventory();
                        p.sendMessage("§b§l➽ §f§lℹ §r§bGib im Chat den §lNamen des Spielers§r§b an, für welchen du die Suche durchführst. Schreibe \"stop\" in den Chat, um die Eingabe abzubrechen.");
                        p.playSound(p.getLocation(), "notification_info", 1, 1);
                        VertragsChatListener.eingabe.put(p, "SucheSpieler");
                        break;
                    case "§e§lVertragssuche":
                        p.closeInventory();
                        p.sendMessage("§b§l➽ §f§lℹ §r§bGib im Chat die §lID des Vertrags§r§b an, den du suchst. Schreibe \"stop\" in den Chat, um die Eingabe abzubrechen.");
                        p.playSound(p.getLocation(), "notification_info", 1, 1);
                        VertragsChatListener.eingabe.put(p, "SucheVertrag");
                        break;
                }
            }
            return;
        }
        if (e.getView().getTitle().equals("§4§lVerträge §8⚊ §e§lVertrag")) {
            e.setCancelled(true);
            if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta() && e.getClickedInventory().contains(e.getCurrentItem())) {
                Vertrag v = selectedVertrag.get(p);
                switch (e.getCurrentItem().getItemMeta().getDisplayName()) {
                    case "§e§lVertrag":
                        ItemStack old = p.getInventory().getItemInMainHand();
                        ItemStack book = new ItemBuilder(Material.WRITTEN_BOOK).build();
                        BookMeta bookMeta = (BookMeta) book.getItemMeta();
                        bookMeta.setPages(v.getContent());
                        bookMeta.setDisplayName("§eVertragskopie: " + v.getId());
                        bookMeta.setAuthor(p.getName());
                        book.setItemMeta(bookMeta);
                        p.getInventory().setItem(p.getInventory().getHeldItemSlot(), book);
                        openBook(p, book);
                        p.getInventory().setItem(p.getInventory().getHeldItemSlot(), old);
                        break;
                    case "§e§lVertragspartner":
                        lastInventory.put(p, e.getClickedInventory());
                        p.closeInventory();
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
                            System.out.println(v.getVertragspartner().toString());
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
                        p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1, 1);
                        break;
                    case "§c§lAuflösen":
                        if (e.getClick().isShiftClick() && e.getClick().isRightClick()) {
                            Vertrag vertrag1 = selectedVertrag.get(p);
                            p.sendMessage("§a§l➽ §2§l✔ §r§aDu hast den Vertrag mit der ID §l" + vertrag1.getId() + " §r§aaufgelöst.");
                            p.playSound(p.getLocation(), "notification_success", 1, 1);
                            for (int i = 0; i < vertrag1.getVertragspartner().size(); i++) {
                                OfflinePlayer op = Bukkit.getOfflinePlayer(vertrag1.getVertragspartner().get(i));
                                if (op.isOnline()) {
                                    op.getPlayer().sendMessage("§b§l➽ §f§lℹ §r§b" + p.getName() + " hat den Vertrag mit der ID §l" + vertrag1.getId() + ", in welchem du ein Vertragspartner bist, §r§baufgelöst.");
                                    op.getPlayer().playSound(op.getPlayer().getLocation(), "notification_info", 1, 1);
                                }
                            }
                            vertraege.remove(vertrag1);
                            p.closeInventory();
                        }
                        break;
                }
            }
        }
        if (e.getView().getTitle().equals("§4§lVerträge §8⚊ §e§lVerträge (Team)")) {
            e.setCancelled(true);
            if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta() && e.getClickedInventory().contains(e.getCurrentItem()) && e.getCurrentItem().getType() == Material.BOOK) {
                String id = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).replace("ID: ", "");
                for (Vertrag v : vertraege) {
                    if (v.getId().equals(id)) {
                        Inventory vertragsInventory = Bukkit.createInventory(null, 9 * 3, "§4§lVerträge §8⚊ §e§lVertrag (Team)");
                        for (int i = 0; i < vertragsInventory.getSize(); i++) {
                            vertragsInventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, (short) 7).setDisplayName(" ").build());
                        }
                        OfflinePlayer op = Bukkit.getOfflinePlayer(v.getErsteller());
                        vertragsInventory.setItem(10, new ItemBuilder(Material.PAPER).setDisplayName("§b§lInformationen zum Vertrag").setLore("§7Titel§8: §f" + v.getTitel(), "§7Ersteller§8: §f" + op.getName(), "§7Datum§8: §f" + v.getDatum()).build());
                        vertragsInventory.setItem(11, new ItemBuilder(Material.BOOK).setDisplayName("§e§lVertrag").setLore("§7Öffne den Vertrag.").build());
                        vertragsInventory.setItem(12, new ItemBuilder(Material.SKULL_ITEM, (short) 3).setDisplayName("§e§lVertragspartner").setLore("§7Verwalte die Vertragspartner.").build());
                        vertragsInventory.setItem(16, new ItemBuilder(Material.BARRIER).setDisplayName("§c§lAuflösen").setLore("§7Shift + Rechtsklick, um den Vertrag aufzulösen.").build());
                        p.openInventory(vertragsInventory);
                        p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1, 1);
                        selectedVertrag.put(p, v);
                    }
                }
            }
            return;
        }
        if (e.getView().getTitle().equals("§4§lVerträge §8⚊ §e§lVertrag (Team)")) {
            e.setCancelled(true);
            if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta() && e.getClickedInventory().contains(e.getCurrentItem())) {
                switch (e.getCurrentItem().getItemMeta().getDisplayName()) {
                    case "§e§lVertrag":
                        Vertrag v = selectedVertrag.get(p);
                        ItemStack old = p.getInventory().getItemInMainHand();
                        ItemStack book = new ItemBuilder(Material.WRITTEN_BOOK).build();
                        BookMeta bookMeta = (BookMeta) book.getItemMeta();
                        bookMeta.setPages(v.getContent());
                        bookMeta.setDisplayName("§eVertragskopie: " + v.getId());
                        bookMeta.setAuthor(p.getName());
                        book.setItemMeta(bookMeta);
                        p.getInventory().setItem(p.getInventory().getHeldItemSlot(), book);
                        openBook(p, book);
                        p.getInventory().setItem(p.getInventory().getHeldItemSlot(), old);
                        break;
                    case "§e§lVertragspartner":
                        lastInventory.put(p, e.getClickedInventory());
                        p.closeInventory();
                        Inventory partnerInventory = Bukkit.createInventory(null, 9 * 3, "§4§lVerträge §8⚊ §e§lPartner (Team)");
                        for (int i = 1; i < 10; i++) {
                            partnerInventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, (short) 7).setDisplayName(" ").build());
                        }
                        for (int i = 17; i < partnerInventory.getSize(); i++) {
                            partnerInventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, (short) 7).setDisplayName(" ").build());
                        }
                        partnerInventory.setItem(0, new ItemBuilder(Material.PAPER).setDisplayName("§7§l⬅  §c§lZurück").build());
                        Vertrag vertrag = selectedVertrag.get(p);
                        int currentSlot = 10;
                        for (int i = 0; i < vertrag.getVertragspartner().size(); i++) {
                            OfflinePlayer op = Bukkit.getOfflinePlayer(vertrag.getVertragspartner().get(i));
                            ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                            SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
                            skullMeta.setOwningPlayer(op);
                            skullMeta.setDisplayName("§e§l" + op.getName());
                            if (vertrag.isSigniert(vertrag.getVertragspartner().get(i))) {
                                skullMeta.setLore(Arrays.asList("§7Signiert§8: §a§lJA", "", "§7Shift + Rechtsklick, um den Spieler aus dem Vertrag zu lösen."));
                            } else {
                                skullMeta.setLore(Arrays.asList("§7Signiert§8: §c§lNEIN", "", "§7Shift + Rechtsklick, um den Spieler aus dem Vertrag zu lösen."));
                            }
                            skull.setItemMeta(skullMeta);
                            partnerInventory.setItem(currentSlot, skull);
                            currentSlot++;
                        }
                        p.openInventory(partnerInventory);
                        p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1, 1);
                        break;
                    case "§c§lAuflösen":
                        if (e.getClick().isShiftClick() && e.getClick().isRightClick()) {
                            Vertrag vertrag1 = selectedVertrag.get(p);
                            p.sendMessage("§a§l➽ §2§l✔ §r§aDu hast den Vertrag mit der ID §l" + vertrag1.getId() + " §r§aaufgelöst.");
                            p.playSound(p.getLocation(), "notification_success", 1, 1);
                            for (int i = 0; i < vertrag1.getVertragspartner().size(); i++) {
                                OfflinePlayer op = Bukkit.getOfflinePlayer(vertrag1.getVertragspartner().get(i));
                                if (op.isOnline()) {
                                    op.getPlayer().sendMessage("§b§l➽ §f§lℹ §r§bEin Teammitglied hat den Vertrag mit der ID §l" + vertrag1.getId() + ", in welchem du ein Vertragspartner bist, §r§baufgelöst.");
                                    op.getPlayer().playSound(op.getPlayer().getLocation(), "notification_info", 1, 1);
                                }
                            }
                            OfflinePlayer op = Bukkit.getOfflinePlayer(vertrag1.getErsteller());
                            if (op.isOnline()) {
                                op.getPlayer().sendMessage("§b§l➽ §f§lℹ §r§bEin Teammitglied hat den Vertrag mit der ID §l" + vertrag1.getId() + ", von welchem du Ersteller bist, §r§baufgelöst.");
                                op.getPlayer().playSound(op.getPlayer().getLocation(), "notification_info", 1, 1);
                            }
                            vertraege.remove(vertrag1);
                            p.closeInventory();
                        }
                        break;
                }
            }
        }
        if (e.getView().getTitle().equals("§4§lVerträge §8⚊ §e§lPartner (Team)")) {
            if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta() && e.getClickedInventory().contains(e.getCurrentItem())) {
                e.setCancelled(true);
                if (e.getCurrentItem().getType().equals(Material.SKULL_ITEM)) {
                    if (e.getSlot() >= 10 && e.getClick().isRightClick() && e.getClick().isShiftClick()) {
                        String playerName = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
                        OfflinePlayer op1 = Bukkit.getOfflinePlayer(playerName);
                        selectedVertrag.get(p).removeVertragspartner(op1.getUniqueId());
                        if (selectedVertrag.get(p).isSigniert(op1.getUniqueId())) {
                            selectedVertrag.get(p).removeSignatur(op1.getUniqueId());
                        }
                        if (op1.isOnline()) {
                            op1.getPlayer().sendMessage("§b§l➽ §f§lℹ §r§bEin Teammitglied hat dich als Vertragspartner aus dem Vertrag mit der ID §l" + selectedVertrag.get(p).getId() + " §r§bentfernt.");
                            op1.getPlayer().playSound(op1.getPlayer().getLocation(), "notification_info", 1, 1);
                        }

                        p.closeInventory();
                        Inventory partnerInventory = Bukkit.createInventory(null, 9 * 3, "§4§lVerträge §8⚊ §e§lPartner (Team)");
                        for (int i = 1; i < 10; i++) {
                            partnerInventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, (short) 7).setDisplayName(" ").build());
                        }
                        for (int i = 17; i < partnerInventory.getSize(); i++) {
                            partnerInventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, (short) 7).setDisplayName(" ").build());
                        }
                        partnerInventory.setItem(0, new ItemBuilder(Material.PAPER).setDisplayName("§7§l⬅  §c§lZurück").build());
                        Vertrag vertrag = selectedVertrag.get(p);
                        int currentSlot = 10;
                        for (int i = 0; i < vertrag.getVertragspartner().size(); i++) {
                            OfflinePlayer op = Bukkit.getOfflinePlayer(vertrag.getVertragspartner().get(i));
                            ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                            SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
                            skullMeta.setOwningPlayer(op);
                            skullMeta.setDisplayName("§e§l" + op.getName());
                            if (vertrag.isSigniert(vertrag.getVertragspartner().get(i))) {
                                skullMeta.setLore(Arrays.asList("§7Signiert§8: §a§lJA", "", "§7Shift + Rechtsklick, um den Spieler aus dem Vertrag zu lösen."));
                            } else {
                                skullMeta.setLore(Arrays.asList("§7Signiert§8: §c§lNEIN", "", "§7Shift + Rechtsklick, um den Spieler aus dem Vertrag zu lösen."));
                            }
                            skull.setItemMeta(skullMeta);
                            partnerInventory.setItem(currentSlot, skull);
                            currentSlot++;
                        }
                        p.openInventory(partnerInventory);
                        p.playSound(p.getLocation(), "notification_success", 1, 1);
                    }
                } else {
                    if (e.getSlot() == 0) {
                        p.openInventory(lastInventory.get(p));
                        p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1, 1);
                    }
                }
            }
            return;
        }
    }

    public String generateNewID() {
        String possibleChars = "1234567890abcdefghijklmnopqrstuvwxyz";
        String id = "";
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            id = id + "" + possibleChars.charAt(random.nextInt(possibleChars.length()));
        }
        if (usedIds.contains(id)) {
            return generateNewID();
        }
        return id;
    }

    public static void openPartnerInventory(Player p, @Nullable Inventory last) {
        Inventory partnerInventory = Bukkit.createInventory(null, 9 * 3, "§4§lVerträge §8⚊ §e§lPartner");
        for (int i = 1; i < 10; i++) {
            partnerInventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, (short) 7).setDisplayName(" ").build());
        }
        for (int i = 17; i < partnerInventory.getSize(); i++) {
            partnerInventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE, (short) 7).setDisplayName(" ").build());
        }
        int currentSlot = 10;
        partnerInventory.setItem(0, new ItemBuilder(Material.PAPER).setDisplayName("§7§l⬅  §c§lZurück").build());
        if (vertragsPartner.containsKey(p)) {
            for (OfflinePlayer op : vertragsPartner.get(p)) {
                ItemStack playerSkull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                SkullMeta skullMeta = (SkullMeta) playerSkull.getItemMeta();
                skullMeta.setOwningPlayer(op);
                skullMeta.setDisplayName("§e§l" + op.getName());
                skullMeta.setLore(Arrays.asList("§7Klicke auf den Kopf, um den Spieler aus dem Vertrag zu entfernen."));
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
        if (last != null) {
            lastInventory.put(p, last);
        }
        p.openInventory(partnerInventory);
    }

    public void openBook(Player p, ItemStack item) {
        PacketContainer pc = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.CUSTOM_PAYLOAD);
        pc.getModifier().writeDefaults();
        ByteBuf bf = Unpooled.buffer(256);
        bf.setByte(0, (byte) 0);
        bf.writerIndex(1);
        pc.getModifier().write(1, MinecraftReflection.getPacketDataSerializer(bf));
        pc.getStrings().write(0, "MC|BOpen");
        ProtocolLibrary.getProtocolManager().sendServerPacket(p, pc);
    }

}
