package com.flabaliki.simpleprefix;

import me.clip.placeholderapi.PlaceholderAPI;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import me.clip.placeholderapi.PlaceholderAPI;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import net.milkbowl.vault.chat.Chat;

public class SimplePrefix extends JavaPlugin implements Listener
{
  static String pluginName;
  static File pluginFolder;
  static String pluginVersion;
  protected static final Logger log = Bukkit.getLogger();
  static String template;
  static String timeFormat;
  static Boolean multiPrefix;
  static String multiPrefixSeparator;
  static Boolean multiSuffix;
  static String multiSuffixSeparator;
  Config config = new Config(this);
  SprCommand commands = new SprCommand(this);
  BukkitScheduler scheduler = null;
  ConcurrentHashMap<String, String> uuids = new ConcurrentHashMap<String, String>();
  static Boolean autoupdate;
  public Chat chat = null;
  boolean useVault = false;
  static Boolean debug;
  static Boolean allowOps;
  static Boolean useUUID = true;

  public void onEnable()
  {
    pluginName = getDescription().getName();
    pluginFolder = getDataFolder();
    pluginVersion = getDescription().getVersion();
    config.firstRun();
    commands.initialise();
    getServer().getPluginManager().registerEvents(this, this);
    getCommand("spr").setExecutor(commands);
    scheduler = Bukkit.getScheduler();
    
    if (useUUID && Bukkit.getOnlinePlayers().size() > 0){
		for (Player p : Bukkit.getOnlinePlayers()){
 		   uuids.put(p.getName(), p.getUniqueId().toString());
    	}
    }
    
    if (Config.config.getBoolean("Use-Vault")) setupChat();
  }
  
  private void setupChat() {
	  try {
	      RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
	      if (rsp == null) return;
	      chat = rsp.getProvider();
	      if (chat != null){
	    	  useVault = true;
	    	  log.info("[" + pluginName + "] " + chat.getName() + " has been detected, through Vault. Simple Prefix will get prefixes and suffixes from it.");
	      }
	  } finally {
		  if (!useVault){
			  log.warning("[" + pluginName + "] Use-Vault was enabled in the Simple Prefix config, but there was a problem accessing it.");
			  log.warning("[" + pluginName + "] Make sure you have Vault, and a Vault compatible permissions/chat plugin installed.");
		  }
		  
	  }
  }

  @EventHandler
  public void onPlayerChat(AsyncPlayerChatEvent event)
  {
    Player player = event.getPlayer();
    String prefix = config.getPrefix(player);
    String suffix = config.getSuffix(player);
    String world = config.getWorld(player);
    String message = event.getMessage().replaceAll("%", "%%");
    if (template == null) template = "<[time] [world] [prefix][name][suffix]> ";
    if (timeFormat == null) timeFormat = "[h:mm aa]";
 String formattedName = template;
formattedName = formattedName.replaceAll("\\[world\\]", world);
formattedName = formattedName.replaceAll("\\[prefix\\]", prefix);
formattedName = formattedName.replaceAll("\\[name\\]", player.getDisplayName());
formattedName = formattedName.replaceAll("\\[suffix\\]", suffix);
formattedName = formattedName.replaceAll("(&([A-Fa-f0-9L-Ol-okKrR]))", "�$2"); 
formattedName = PlaceholderAPI.setPlaceholders(event.getPlayer(), formattedName);
    if ((timeFormat != null) && (!timeFormat.equalsIgnoreCase("")) && (formattedName.contains("[time]"))) {
      DateFormat dateFormat = new SimpleDateFormat(timeFormat);
      Date date = new Date();
      formattedName = formattedName.replaceAll("\\[time\\]", String.valueOf(dateFormat.format(date)));
    }
    formattedName = formattedName.replaceAll("\\s+", " ");
    event.setFormat(formattedName + message);
  }
  
  @EventHandler
  public void onPreLogin(AsyncPlayerPreLoginEvent event){
	  if (useUUID) uuids.put(event.getName(), event.getUniqueId().toString());
  }

  public static void message(String message, CommandSender sender) {
	  sender.sendMessage(ChatColor.AQUA + "[" + pluginName + "] " + ChatColor.WHITE + message);
  }
}
