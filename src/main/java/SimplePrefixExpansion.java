// 
// Decompiled. Apparently.
// 

package SimplePrefixMod;

import org.bukkit.metadata.MetadataValue;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class SimplePrefixExpansion extends PlaceholderExpansion
{
    public boolean canRegister() {
        return Bukkit.getPluginManager().getPlugin(this.getPlugin()) != null;
    }
    
    public String getAuthor() {
        return "clip";
    }
    
    public String getIdentifier() {
        return "simpleprefix";
    }
    
    public String getPlugin() {
        return "SimplePrefix";
    }
    
    public String getVersion() {
        return "1.0.0";
    }
    
    public String onPlaceholderRequest(final Player p, final String identifier) {
        if (p == null) {
            return "";
        }
        if (identifier.equals("prefix")) {
            return this.getPrefixSuffix(p, "prefix");
        }
        if (identifier.equals("suffix")) {
            return this.getPrefixSuffix(p, "suffix");
        }
        return null;
    }
    
    public String getPrefixSuffix(final Player player, final String type) {
        if (player.hasMetadata(type)) {
            return player.getMetadata(type).get(0).asString();
        }
        return "";
    }
}
