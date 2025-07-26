package dev.cakestudio.cakeenderchestfilter.model;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

@Getter
public class Messages {

    private final String noPermission;
    private final String reload;
    private final String usage;
    private final String mustBePlayer;
    private final String unknownCommand;
    private final String mustHoldItem;
    private final String unknownStorage;
    private final String itemAddedToFilter;
    private final String itemRemovedFromFilter;
    private final String playerDeny;


    public Messages(ConfigurationSection section) {
        this.noPermission = section != null ? section.getString("no-permission") : "#EE4B2B[✘] &fУ вас нет прав.";
        this.reload = section != null ? section.getString("reload") : "#7CFC00[✔] &fКонфиг перезагружен!";
        this.usage = section != null ? section.getString("usage") : "#FFFF00[!] &fИспользование: /ecf <...>";
        this.mustBePlayer = section != null ? section.getString("must-be-player") : "&cТолько для игроков.";
        this.unknownCommand = section != null ? section.getString("unknown-command") : "#EE4B2B[✘] &fНеизвестная команда.";
        this.mustHoldItem = section != null ? section.getString("must-hold-item") : "#EE4B2B[✘] &fВозьмите предмет в руку.";
        this.unknownStorage = section != null ? section.getString("unknown-storage") : "#EE4B2B[✘] &fНеизвестное хранилище.";
        this.itemAddedToFilter = section != null ? section.getString("item-added-to-filter") : "#7CFC00[✔] &fПредмет &a%item% &cзапрещён &fв &e%storage%.";
        this.itemRemovedFromFilter = section != null ? section.getString("item-removed-from-filter") : "#7CFC00[✔] &fПредмет &a%item% &aразрешён &fв &e%storage%.";
        this.playerDeny = section != null ? section.getString("player-deny") : "#EE4B2B[✘] &fВы не можете это сделать.";
    }
}
