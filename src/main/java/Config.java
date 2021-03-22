package SimplePrefixMod;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

public class Config
{
  public static FileConfiguration config;
  static SimplePrefix plugin;

  
  public Config(SimplePrefix p){
	  plugin = p;
  }
  
  public void reload(){
	  plugin.reloadConfig();
	  config = plugin.getConfig();
	  loadKeys();
  }

  public void firstRun()
  {
	  File file = new File(plugin.getDataFolder() + "/src/main/resources/config.yml");
	  if (!file.exists()){
		  plugin.saveDefaultConfig();
		  config = plugin.getConfig();
	  } else {
		  config = plugin.getConfig();
		  String configversion = config.getString("Version");
		  if (configversion == null){
			  config.set("Template.multiSuffix", false);
			  config.set("Template.multiPrefixSeparator", "&r, ");
			  config.set("Template.multiSuffixSeparator", "&r-");
			  config.set("Use-Vault", false);
			  config.set("Auto-Update", true);
			  config.set("debug-mode", false);
			  config.set("OPs-have-all", true);
			  config.set("Use-UUID", true);
			  
			  // I doubt this is necessary anymore. Removing it because fetchUUID function has been removed.
			  /*SimplePrefix.log.info("\n====================\nMigrating the SimplePrefix user data\nto the UUID system. This may freeze the\nserver until it is finished.\n====================");
			  for (String key : config.getConfigurationSection("User").getKeys(false)){
				  String uuid = plugin.fetchUUID(key).toString();
				  String prefix = config.getString("User." + key + ".prefix");
				  String suffix = config.getString("User." + key + ".suffix");
				  config.set("User." + key, null);
				  config.set("User." + uuid + ".prefix", prefix);
				  config.set("User." + uuid + ".suffix", suffix);
			  }*/
		  } else if (configversion.equals("2.3")){
			  config.set("Auto-Update", true);
			  config.set("Use-Vault", false);
			  config.set("debug-mode", false);
			  config.set("OPs-have-all", true);
			  config.set("Use-UUID", true);
		  } else if (configversion.equals("2.3.1") || configversion.equals("2.3.2") || configversion.equals("2.3.3") || configversion.equals("2.3.4")){
			  config.set("Use-Vault", false);
			  config.set("debug-mode", false);
			  config.set("OPs-have-all", true);
			  config.set("Use-UUID", true);
		  } else if (configversion.equals("2.3.5") || configversion.equals("2.4.0")){
			  config.set("debug-mode", false);
			  config.set("OPs-have-all", true);
			  config.set("Use-UUID", true);
		  } else if (configversion.equals("2.4.1") || configversion.equals("2.5.0")){
			  config.set("Use-UUID", true);
		  }
	  }
	  config.set("Version", "2.5.1");
	  plugin.saveConfig();
	  loadKeys();
  }

  private void loadKeys()
  {
    SimplePrefix.template = config.getString("Template.format");
    SimplePrefix.timeFormat = config.getString("Template.time");
    SimplePrefix.multiPrefix = Boolean.valueOf(config.getBoolean("Template.multiPrefix"));
    SimplePrefix.multiPrefixSeparator = config.getString("Template.multiPrefixSeparator");
    SimplePrefix.multiSuffix = Boolean.valueOf(config.getBoolean("Template.multiSuffix"));
    SimplePrefix.multiSuffixSeparator = config.getString("Template.multiSuffixSeparator");
    SimplePrefix.autoupdate = Boolean.valueOf(config.getBoolean("Auto-Update"));
    SimplePrefix.debug = Config.config.getBoolean("debug-mode");
    SimplePrefix.allowOps = Config.config.getBoolean("OPs-have-all");
    SimplePrefix.useUUID = Config.config.getBoolean("Use-UUID");
    
    if (!SimplePrefix.useUUID && config.getString("User.069a79f4-44e9-4726-a5be-fca90e38aaf5.prefix") != null){
    	config.set("User.069a79f4-44e9-4726-a5be-fca90e38aaf5", null);
    	config.set("User.Notch.prefix", "&c[Notch]&f");
    }
    
    plugin.saveConfig();
  }

  public String getPrefix(Player player) {
    if (!plugin.useVault) return ChatColor.translateAlternateColorCodes('&', getPrefixSuffix(player, "prefix"));
    else return ChatColor.translateAlternateColorCodes('&', getVaultPrefix(player));
  }

  public String getSuffix(Player player) {
    if (!plugin.useVault) return ChatColor.translateAlternateColorCodes('&', getPrefixSuffix(player, "suffix"));
    else return ChatColor.translateAlternateColorCodes('&', getVaultSuffix(player));
  }

  public String getWorld(Player player) {
    String world = "";
    if (config.getString("Worlds." + player.getWorld().getName() + ".nickname") != null) {
      world = config.getString("Worlds." + player.getWorld().getName() + ".nickname").replaceAll("(&([A-Fa-f0-9L-Ol-okKrR]))", "§$2");
    }
    return world;
  }
  
  public String getVaultPrefix(Player player){
	  if (SimplePrefix.debug) SimplePrefix.log.info("[" + SimplePrefix.pluginName + "] DEBUG: Attempting to get prefix for " + player.getName() + " from Vault");
	  
	  String groupPrefix = "";
	  String[] groups = plugin.chat.getPlayerGroups(player);
	  ArrayList<String> groupPrefixes = new ArrayList<>();
	  for (String group : groups){
		  if (group == null || group.equals("")) continue;
		  String spGroupPrefix = plugin.chat.getGroupPrefix(player.getWorld(), group);
		  if (spGroupPrefix == null || spGroupPrefix.equals("")) continue;
		  groupPrefixes.add(spGroupPrefix);
		  if (SimplePrefix.multiPrefix){
			  if (groupPrefix.equals("")) groupPrefix = spGroupPrefix;
			  else groupPrefix += SimplePrefix.multiPrefixSeparator + spGroupPrefix;
		  } else {
			  groupPrefix = spGroupPrefix;
			  break;
		  }
	  }
	  
	  String userPrefix = plugin.chat.getPlayerPrefix(player);
	  if (groupPrefixes.contains(userPrefix)) userPrefix = ""; // b/c sometimes Vault decides to return a group prefix as the user prefix
	  
	  if (userPrefix.equals("")) return groupPrefix;
	  if (groupPrefix.equals("")) return userPrefix;
	  if (!SimplePrefix.multiPrefix) return userPrefix;
	  return groupPrefix + SimplePrefix.multiPrefixSeparator + userPrefix;
  }
  
  public String getVaultSuffix(Player player){
	  if (SimplePrefix.debug) SimplePrefix.log.info("[" + SimplePrefix.pluginName + "] DEBUG: Attempting to get suffix for " + player.getName() + " from Vault");
	  
	  String groupSuffix = "";
	  String[] groups = plugin.chat.getPlayerGroups(player);
	  ArrayList<String> groupSuffixes = new ArrayList<>();
	  for (String group : groups){
		  if (group == null || group.equals("")) continue;
		  String spGroupSuffix = plugin.chat.getGroupSuffix(player.getWorld(), group);
		  if (spGroupSuffix == null || spGroupSuffix.equals("")) continue;
		  groupSuffixes.add(spGroupSuffix);
		  if (SimplePrefix.multiSuffix){
			  if (groupSuffix.equals("")) groupSuffix = spGroupSuffix;
			  else groupSuffix += SimplePrefix.multiSuffixSeparator + spGroupSuffix;
		  } else {
			  groupSuffix = spGroupSuffix;
			  break;
		  }
	  }
	  
	  String userSuffix = plugin.chat.getPlayerSuffix(player);
	  if (groupSuffixes.contains(userSuffix)) userSuffix = ""; // b/c sometimes Vault decides to return a group suffix as the user suffix
	  
	  if (userSuffix.equals("")) return groupSuffix;
	  if (groupSuffix.equals("")) return userSuffix;
	  if (!SimplePrefix.multiSuffix) return userSuffix;
	  return groupSuffix + SimplePrefix.multiSuffixSeparator + userSuffix;
  }

  private String getPrefixSuffix(Player player, String type) {
	  if (SimplePrefix.debug) SimplePrefix.log.info("[" + SimplePrefix.pluginName + "] DEBUG: Attempting to get " + type + " for " + player.getName() + " from SimplePrefix config");
	  
    String prefixSuffix = "";
    Set<String> groups = config.getConfigurationSection("Group").getKeys(false);
    List<String> usedTiers = new LinkedList<String>();
    for (String group : groups) {
      if (player.hasPermission("simpleprefix." + group) && (config.getString("Group." + group + "." + type) != null)) {
    	if (SimplePrefix.allowOps == false && !player.isPermissionSet("simpleprefix." + group)) continue;
        if (SimplePrefix.debug) SimplePrefix.log.info("[" + SimplePrefix.pluginName + "] DEBUG: Player has permission for the '" + group + "' prefix/suffix group");
        if ((type.equals("prefix") && SimplePrefix.multiPrefix.booleanValue()) || (type.equals("suffix") && SimplePrefix.multiSuffix.booleanValue())) {
        	String tier = config.getString("Group." + group + ".tier");
        	if (tier == null || tier.equals("") || !usedTiers.contains(tier)){
	        	if (prefixSuffix.equals("")) prefixSuffix = config.getString("Group." + group + "." + type);
	        	else {
	        		if (type.equals("prefix")) prefixSuffix = prefixSuffix + SimplePrefix.multiPrefixSeparator + config.getString("Group." + group + "." + type);
	        		else if (type.equals("suffix")) prefixSuffix = prefixSuffix + SimplePrefix.multiSuffixSeparator + config.getString("Group." + group + "." + type);
	        	}
	        	usedTiers.add(tier);
        	}
        } else {
          prefixSuffix = config.getString("Group." + group + "." + type);
          if (SimplePrefix.debug) SimplePrefix.log.info("[" + SimplePrefix.pluginName + "] DEBUG: Multi" + type + " is not enabled, so " + type + " retrieved is '" + prefixSuffix + "'");
          break;
        }
      }
    }

    player.setMetadata(type, new FixedMetadataValue(plugin, prefixSuffix));

    if (config.get("User") != null) {
      String data;
      if (plugin.uuids.containsKey(player.getName()) && config.getConfigurationSection("User").contains(plugin.uuids.get(player.getName()))){
    	  data = config.getString("User." + plugin.uuids.get(player.getName()) + "." + type);
      } else {
    	  data = config.getString("User." + player.getName() + "." + type);
      }  	  
      if ((data != null) && (!data.equalsIgnoreCase(""))) {
    	if ((type.equals("prefix") && SimplePrefix.multiPrefix.booleanValue()) || (type.equals("suffix") && SimplePrefix.multiSuffix.booleanValue())) {
    		if (prefixSuffix.equals("")) player.setMetadata(type, new FixedMetadataValue(plugin, data));
    		else {
    			if (type.equals("prefix")) player.setMetadata(type, new FixedMetadataValue(plugin, prefixSuffix + SimplePrefix.multiPrefixSeparator + data));
    			else if (type.equals("suffix")) player.setMetadata(type, new FixedMetadataValue(plugin, prefixSuffix + SimplePrefix.multiSuffixSeparator + data));
    		}
    	} else {
    		player.setMetadata(type, new FixedMetadataValue(plugin, data));
    		if (SimplePrefix.debug) SimplePrefix.log.info("[" + SimplePrefix.pluginName + "] DEBUG: Multi" + type + " is not enabled, so user " + type + " retrieved is '" + data + "'");
        }
      }

    }

    if (player.hasMetadata(type)) {
      return ((MetadataValue)player.getMetadata(type).get(0)).asString();
    }

    return "";
  }

  public void setPrefix(String type, String body, String prefix) {
    setPrefixSuffix("prefix", type, body, prefix);
  }

  public void setSuffix(String type, String body, String suffix) {
    setPrefixSuffix("suffix", type, body, suffix);
  }

  public void setWorld(String world, String input) {
    config.set("Worlds." + world + ".nickname", input);
    plugin.saveConfig();
  }

  private void setPrefixSuffix(String type, String bodyType, String body, String input) {
    if (bodyType.equalsIgnoreCase("group"))
      config.set("Group." + body + "." + type, input);
    else if (bodyType.equalsIgnoreCase("user")) {
      config.set("User." + body + "." + type, input);
    }
    plugin.saveConfig();
  }
}
