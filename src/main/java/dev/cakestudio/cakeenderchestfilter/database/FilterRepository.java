package dev.cakestudio.cakeenderchestfilter.database;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import dev.cakestudio.cakeenderchestfilter.model.CustomItemFilterRule;
import dev.cakestudio.cakeenderchestfilter.model.FilterRule;
import dev.cakestudio.cakeenderchestfilter.model.MaterialFilterRule;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FilterRepository {

    private final DatabaseManager databaseManager;
    private final JavaPlugin plugin;
    private final Gson gson = new Gson();

    public FilterRepository(DatabaseManager databaseManager, JavaPlugin plugin) {
        this.databaseManager = databaseManager;
        this.plugin = plugin;
    }

    public List<FilterRule> loadFiltersFor(@NonNull InventoryType type) {
        List<FilterRule> rules = new ArrayList<>();
        String sql = "SELECT id, filter_type, criteria FROM item_filters WHERE storage_type = ?";
        try (Connection conn = databaseManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, type.name());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                long id = rs.getLong("id");
                String filterType = rs.getString("filter_type");
                String criteria = rs.getString("criteria");
                if ("MATERIAL".equals(filterType)) {
                    rules.add(new MaterialFilterRule(id, Material.valueOf(criteria)));
                } else if ("CUSTOM".equals(filterType)) {
                    JsonObject json = gson.fromJson(criteria, JsonObject.class);
                    String name = json.has("name") ? json.get("name").getAsString() : null;
                    Integer modelData = json.has("model") ? json.get("model").getAsInt() : null;
                    Type listType = new TypeToken<List<String>>() {}.getType();
                    List<String> lore = json.has("lore") ? gson.fromJson(json.get("lore"), listType) : null;

                    Type mapType = new TypeToken<Map<String, String>>() {}.getType();
                    Map<String, String> nbt = json.has("nbt") ? gson.fromJson(json.get("nbt"), mapType) : null;

                    rules.add(new CustomItemFilterRule(id, plugin, name, lore, modelData, nbt));
                }
            }
        } catch (SQLException | IllegalArgumentException e) {
            plugin.getLogger().severe("Error loading filter rules from database: " + e.getMessage());
            e.printStackTrace();
        }
        return rules;
    }

    public void addRule(@NonNull InventoryType type, @NonNull String filterType, @NonNull String criteria) {
        String sql = "INSERT INTO item_filters(storage_type, filter_type, criteria) VALUES(?, ?, ?)";
        try (Connection conn = databaseManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, type.name());
            pstmt.setString(2, filterType);
            pstmt.setString(3, criteria);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("Could not add rule to database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void removeRuleById(long id) {
        String sql = "DELETE FROM item_filters WHERE id = ?";
        try (Connection conn = databaseManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("Could not remove rule from database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
