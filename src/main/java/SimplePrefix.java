package com.flabaliki.simpleprefix;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.SimplePrefixExpansion;

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
  /**
 * This class will automatically register as a placeholder expansion 
 * when a jar including this class is added to the directory 
 * {@code /plugins/PlaceholderAPI/expansions} on your server.
 * <br>
 * <br>If you create such a class inside your own plugin, you have to
 * register it manually in your plugins {@code onEnable()} by using 
 * {@code new YourExpansionClass().register();}
 */
  public class PrefixExpansion extends SimplePrefixExpansion {

    /**
     * This method should always return true unless we
     * have a dependency we need to make sure is on the server
     * for our placeholders to work!
     *
     * @return always true since we do not have any dependencies.
     */
    @Override
    public boolean canRegister(){
        return true;
    }

    /**
     * The name of the person who created this expansion should go here.
     * 
     * @return The name of the author as a String.
     */
    @Override
    public String getAuthor(){
        return "Bounty";
    }

    /**
     * The placeholder identifier should go here.
     * <br>This is what tells PlaceholderAPI to call our onRequest 
     * method to obtain a value if a placeholder starts with our 
     * identifier.
     * <br>The identifier has to be lowercase and can't contain _ or %
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @Override
    public String getIdentifier(){
        return "simpleprefix";
    }

    /**
     * This is the version of this expansion.
     * <br>You don't have to use numbers, since it is set as a String.
     *
     * @return The version as a String.
     */
    @Override
    public String getVersion(){
        return "1.0.0";
    }
  
    /**
     * This is the method called when a placeholder with our identifier 
     * is found and needs a value.
     * <br>We specify the value identifier in this method.
     * <br>Since version 2.9.1 can you use OfflinePlayers in your requests.
     *
     * @param  player
     *         A {@link org.bukkit.OfflinePlayer OfflinePlayer}.
     * @param  identifier
     *         A String containing the identifier/value.
     *
     * @return Possibly-null String of the requested identifier.
     */
    @Override
    public String onRequest(OfflinePlayer player, String identifier){
  
        // %example_placeholder1%
        if(identifier.equals("prefix")){
            return prefix;
        }

        // %example_placeholder2%
        if(identifier.equals("suffix")){
            return suffix;
        }

        // We return null if an invalid placeholder (f.e. %example_placeholder3%) 
        // was provided
        return null;
    }
  }
}
