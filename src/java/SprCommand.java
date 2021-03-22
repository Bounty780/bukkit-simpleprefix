package com.flabaliki.simpleprefix;

import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SprCommand implements CommandExecutor
{

  static HashSet<String> prefixalias = new HashSet<String>();
  static HashSet<String> suffixalias = new HashSet<String>();
  static HashSet<String> groupalias = new HashSet<String>();
  static HashSet<String> useralias = new HashSet<String>();
  static HashSet<String> helpalias = new HashSet<String>();
  static HashSet<String> coloursalias = new HashSet<String>();
  static HashSet<String> worldalias = new HashSet<String>();
  static HashSet<String> updatealias = new HashSet<String>();
  static HashSet<String> reloadalias = new HashSet<String>();
  
  SimplePrefix plugin;

  public SprCommand(SimplePrefix p) {
	  plugin = p;
  }
  
  public void initialise() {
	prefixalias.add("prefix");
    prefixalias.add("p");
    suffixalias.add("suffix");
    suffixalias.add("s");
    groupalias.add("group");
    groupalias.add("g");
    useralias.add("user");
    useralias.add("u");
    helpalias.add("help");
    helpalias.add("?");
    helpalias.add("h");
    coloursalias.add("colours");
    coloursalias.add("colors");
    coloursalias.add("colour");
    coloursalias.add("color");
    coloursalias.add("c");
    worldalias.add("world");
    worldalias.add("w");
    updatealias.add("update");
    reloadalias.add("reload");

  }
  
  public boolean vaultMessage(CommandSender sender){
	  sender.sendMessage(ChatColor.AQUA + "[" + SimplePrefix.pluginName + "]" + ChatColor.RED + " You can not use that Simple Prefix command, because Simple Prefix currently gets its prefixes and suffixes from " + plugin.chat.getName());
	  return true;
  }

  public boolean onCommand(CommandSender sender, Command comannd, String label, String[] args){
	  boolean hasAll = sender.hasPermission("simpleprefix.command.*");
    if (args.length == 1)
    {
      if (helpalias.contains(args[0].toLowerCase())) {
        showHelp(sender);
        return true;
      }

      if ((sender.hasPermission("simpleprefix.command.reload") || hasAll) && reloadalias.contains(args[0].toLowerCase())) {
    	  plugin.config.reload();
    	  sender.sendMessage(ChatColor.AQUA + "[" + SimplePrefix.pluginName + "]" + ChatColor.WHITE + " Config reloaded.");
    	  return true;
      }

      if (((sender.hasPermission("simpleprefix.command.colors")) || (sender.hasPermission("simpleprefix.command.*"))) && (coloursalias.contains(args[0].toLowerCase()))) {
    	  showColours(sender);
    	  return true;
      }
      if ((sender instanceof Player))
      {
        if (((sender.hasPermission("simpleprefix.command.editOwn")) || (sender.hasPermission("simpleprefix.command.*"))) && (prefixalias.contains(args[0].toLowerCase()))) {
        	if (plugin.useVault) return vaultMessage(sender);
            changeMessage(sender, "Prefix", "remove", "user", sender.getName(), "");
        	setUserPrefix(sender, sender.getName(), "");
        	return true;
        }

        if (((sender.hasPermission("simpleprefix.command.editOwn")) || (sender.hasPermission("simpleprefix.command.*"))) && (suffixalias.contains(args[0].toLowerCase()))) {
        	if (plugin.useVault) return vaultMessage(sender);
            changeMessage(sender, "Suffix", "remove", "user", sender.getName(), "");
        	setUserSuffix(sender, sender.getName(), "");
        	return true;
        }
      }

    }

    if (args.length == 2) {
      if ((sender instanceof Player))
      {
        if (((sender.hasPermission("simpleprefix.command.editOwn")) || (sender.hasPermission("simpleprefix.command.*"))) && (prefixalias.contains(args[0].toLowerCase()))) {
        	if (plugin.useVault) return vaultMessage(sender);
        	changeMessage(sender, "Prefix", "set", "user", sender.getName(), args[1]);
        	setUserPrefix(sender, sender.getName(), args[1]);
        	return true;
        }

        if (((sender.hasPermission("simpleprefix.command.editOwn")) || (sender.hasPermission("simpleprefix.command.*"))) && (suffixalias.contains(args[0].toLowerCase()))) {
        	if (plugin.useVault) return vaultMessage(sender);
        	changeMessage(sender, "Suffix", "set", "user", sender.getName(), args[1]);
        	setUserSuffix(sender, sender.getName(), args[1]);
        	return true;
        }
      }

      if (((sender.hasPermission("simpleprefix.command.editWorld")) || (sender.hasPermission("simpleprefix.command.*"))) && (worldalias.contains(args[0].toLowerCase()))) {
    	  plugin.config.setWorld(args[1], "");
    	  changeMessage(sender, "Nickname", "remove", "world", args[1], "");
    	  return true;
      }
    }

    if (args.length == 3)
    {
      if (((sender.hasPermission("simpleprefix.command.editGroup")) || (sender.hasPermission("simpleprefix.command.*"))) && (prefixalias.contains(args[0].toLowerCase())) && (groupalias.contains(args[1].toLowerCase()))) {
    	  if (plugin.useVault) return vaultMessage(sender);
    	  plugin.config.setPrefix("group", args[2], "");
    	  changeMessage(sender, "Prefix", "remove", "group", args[2], "");
    	  return true;
      }

      if (((sender.hasPermission("simpleprefix.command.editGroup")) || (sender.hasPermission("simpleprefix.command.*"))) && (suffixalias.contains(args[0].toLowerCase())) && (groupalias.contains(args[1].toLowerCase()))) {
    	  if (plugin.useVault) return vaultMessage(sender);
    	  plugin.config.setSuffix("group", args[2], "");
    	  changeMessage(sender, "Suffix", "remove", "group", args[2], "");
    	  return true;
      }

      if (((sender.hasPermission("simpleprefix.command.editUser")) || (sender.hasPermission("simpleprefix.command.*"))) && (prefixalias.contains(args[0].toLowerCase())) && (useralias.contains(args[1].toLowerCase()))) {
    	  if (plugin.useVault) return vaultMessage(sender);
    	  changeMessage(sender, "Prefix", "remove", "user", args[2], "");
    	  setUserPrefix(sender, args[2], "");
    	  return true;
      }

      if (((sender.hasPermission("simpleprefix.command.editUser")) || (sender.hasPermission("simpleprefix.command.*"))) && (suffixalias.contains(args[0].toLowerCase())) && (useralias.contains(args[1].toLowerCase()))) {
    	  if (plugin.useVault) return vaultMessage(sender);
    	  changeMessage(sender, "Suffix", "remove", "user", args[2], "");
    	  setUserSuffix(sender, args[2], "");
    	  return true;
      }

      if (((sender.hasPermission("simpleprefix.command.editWorld")) || (sender.hasPermission("simpleprefix.command.*"))) && (worldalias.contains(args[0].toLowerCase()))) {
    	  plugin.config.setWorld(args[1], args[2]);
    	  changeMessage(sender, "Nickname", "set", "world", args[1], args[2]);
    	  return true;
      }
    }

    if (args.length >= 4)
    {
      if (((sender.hasPermission("simpleprefix.command.editGroup")) || (sender.hasPermission("simpleprefix.command.*"))) && (prefixalias.contains(args[0].toLowerCase())) && (groupalias.contains(args[1].toLowerCase()))) {
    	  if (plugin.useVault) return vaultMessage(sender);
    	  String prefix = args[3];
        if (args.length > 4) {
          for (int i = 4; i < args.length; i++) {
            prefix = prefix + " " + args[i];
          }
        }
        plugin.config.setPrefix("group", args[2], prefix);
        changeMessage(sender, "Prefix", "set", "group", args[2], prefix);
        return true;
      }

      if (((sender.hasPermission("simpleprefix.command.editGroup")) || (sender.hasPermission("simpleprefix.command.*"))) && (suffixalias.contains(args[0].toLowerCase())) && (groupalias.contains(args[1].toLowerCase()))) {
    	  if (plugin.useVault) return vaultMessage(sender);
    	  String suffix = args[3];
        if (args.length > 4) {
          for (int i = 4; i < args.length; i++) {
            suffix = suffix + " " + args[i];
          }
        }
        plugin.config.setSuffix("group", args[2], suffix);
        changeMessage(sender, "Suffix", "set", "group", args[2], suffix);
        return true;
      }

      if (((sender.hasPermission("simpleprefix.command.editUser")) || (sender.hasPermission("simpleprefix.command.*"))) && (prefixalias.contains(args[0].toLowerCase())) && (useralias.contains(args[1].toLowerCase()))) {
    	  if (plugin.useVault) return vaultMessage(sender);
    	  String prefix = args[3];
        if (args.length > 4) {
          for (int i = 4; i < args.length; i++) {
            prefix = prefix + " " + args[i];
          }
        }
        changeMessage(sender, "Prefix", "set", "user", args[2], prefix);
        setUserPrefix(sender, args[2], prefix);
        return true;
      }

      if (((sender.hasPermission("simpleprefix.command.editUser")) || (sender.hasPermission("simpleprefix.command.*"))) && (suffixalias.contains(args[0].toLowerCase())) && (useralias.contains(args[1].toLowerCase()))) {
    	  if (plugin.useVault) return vaultMessage(sender);
    	  String suffix = args[3];
        if (args.length > 4) {
          for (int i = 4; i < args.length; i++) {
            suffix = suffix + " " + args[i];
          }
        }
        changeMessage(sender, "Suffix", "set", "user", args[2], suffix);
        setUserSuffix(sender, args[2], suffix);
        return true;
      }
    }
    showHelp(sender);
    return true;
  }

  private void changeMessage(CommandSender sender, String item, String change, String entityType, String entity, String input) {
    String space = " ";
    sender.sendMessage(ChatColor.AQUA + "[" + SimplePrefix.pluginName + "]");
    if (change.equalsIgnoreCase("remove"))
      sender.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + " > " + ChatColor.WHITE + item + space + "for" + space + entityType + space + ChatColor.AQUA + entity + ChatColor.WHITE + space + ChatColor.RED + "removed.");
    else if (change.equalsIgnoreCase("set"))
      sender.sendMessage((ChatColor.AQUA + "" + ChatColor.BOLD + " > " + ChatColor.WHITE + item + space + "for" + space + entityType + space + ChatColor.AQUA + entity + ChatColor.WHITE + space + ChatColor.GREEN + "set to" + ChatColor.WHITE + ":" + space + input).replaceAll("(&([A-Fa-f0-9L-Ol-okKrR]))", "�$2"));
  }

  private void showColours(CommandSender sender)
  {
    sender.sendMessage(ChatColor.AQUA + "[" + SimplePrefix.pluginName + " Colours] ");
    if ((sender instanceof Player)) {
      sender.sendMessage("�0&0 �1&1 �2&2 �3&3 �4&4 �5&5 �6&6 �7&7 �8&8 �9&9 �A&A �B&B �C&C �D&D �E&E �F&F");
      sender.sendMessage("�r�L&L�r �M&M�r �N&N�r �O&O�r �k&K�R (�r&R�r Reset)");
    } else {
      sender.sendMessage("| &0 Black     | &1 Dark Blue | &2 Dark Green | &3 Dark Aqua |");
      sender.sendMessage("| &4 Dark Red  | &5 Purple    | &6 Gold       | &7 Gray      |");
      sender.sendMessage("| &8 Dark Gray | &9 Blue      | &a Green      | &b Aqua      |");
      sender.sendMessage("| &c Red       | &d Pink      | &e Yellow     | &f White     |");
      sender.sendMessage("| &l Bold      | &m Striked   | &n Underlined | &o Italic    |");
      sender.sendMessage("| &k Magic     | &r Reset     |");
    }
  }

  private void showHelp(CommandSender sender) {
    sender.sendMessage(ChatColor.AQUA + "[" + SimplePrefix.pluginName + " Commands] ");
    if ((sender.hasPermission("simpleprefix.command.colors")) || (sender.hasPermission("simpleprefix.command.*"))) sender.sendMessage(commandHelp("colors", "Shows a list of colors you can use"));
    if ((sender.hasPermission("simpleprefix.command.editGroup")) || (sender.hasPermission("simpleprefix.command.*"))) sender.sendMessage(commandHelp("prefix/suffix group <group> [prefix/suffix]", "Set a group's prefix or suffix"));
    if ((sender.hasPermission("simpleprefix.command.editUser")) || (sender.hasPermission("simpleprefix.command.*"))) sender.sendMessage(commandHelp("prefix/suffix user <user> [prefix/suffix]", "Set a user's prefix or suffix"));
    if ((sender.hasPermission("simpleprefix.command.editOwn")) || (sender.hasPermission("simpleprefix.command.*"))) sender.sendMessage(commandHelp("prefix/suffix [prefix/suffix]", "Set your prefix or suffix"));
    if ((sender.hasPermission("simpleprefix.command.editWorld")) || (sender.hasPermission("simpleprefix.command.*"))) sender.sendMessage(commandHelp("world <world> [nick]", "Set the nickname of a world"));
    if ((sender.hasPermission("simpleprefix.command.reload")) || (sender.hasPermission("simpleprefix.command.*"))) sender.sendMessage(commandHelp("reload", "Reloads the config or database"));
    sender.sendMessage(ChatColor.GRAY + "If you can't see the commands, check your permissions!");
    sender.sendMessage(ChatColor.GRAY + "More help: " + ChatColor.UNDERLINE + "http://dev.bukkit.org/server-mods/simple-prefix/");
  }

  private String commandHelp(String command, String function) {
    String message = ChatColor.AQUA + "" + ChatColor.BOLD + ">" + ChatColor.WHITE + "/spr " + command;
    return message;
  }
  
  @SuppressWarnings("deprecation")
  private void setUserPrefix(CommandSender sender, final String player, final String prefix){
	  if (!SimplePrefix.useUUID){
		  plugin.config.setPrefix("user", player, prefix);
	  } else {
		  if (Bukkit.getOfflinePlayer(player).isOnline()){
			  plugin.config.setPrefix("user", plugin.uuids.get(player), prefix);
		  } else {
		      sender.sendMessage(ChatColor.RED + "The player must be online!");
		  }
	  }
  }
  
  @SuppressWarnings("deprecation")
  private void setUserSuffix(CommandSender sender, final String player, final String suffix){
	  if (!SimplePrefix.useUUID){
		  plugin.config.setPrefix("user", player, suffix);
	  } else {
		  if (Bukkit.getOfflinePlayer(player).isOnline()){
			  plugin.config.setSuffix("user", plugin.uuids.get(player), suffix);
		  } else {
			  sender.sendMessage(ChatColor.RED + "The player must be online!");
		  }
	  }
  }
}