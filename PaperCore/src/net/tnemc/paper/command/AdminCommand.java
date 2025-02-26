package net.tnemc.paper.command;

/*
 * The New Economy
 * Copyright (C) 2022 - 2023 Daniel "creatorfromhell" Vidmar
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import net.tnemc.core.EconomyManager;
import net.tnemc.core.TNECore;
import net.tnemc.core.account.Account;
import net.tnemc.core.account.AccountStatus;
import net.tnemc.core.account.holdings.HoldingsEntry;
import net.tnemc.core.api.response.AccountAPIResponse;
import net.tnemc.core.command.BaseCommand;
import net.tnemc.core.compatibility.log.DebugLevel;
import net.tnemc.core.compatibility.scheduler.ChoreExecution;
import net.tnemc.core.compatibility.scheduler.ChoreTime;
import net.tnemc.core.currency.Currency;
import net.tnemc.core.io.message.MessageData;
import net.tnemc.core.utils.Identifier;
import net.tnemc.paper.impl.PaperCMDSource;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;
import org.simpleyaml.configuration.file.YamlFile;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Default;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.annotation.Usage;
import revxrsal.commands.bukkit.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import revxrsal.commands.help.CommandHelp;

import java.io.File;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * AdminCommand
 *
 * @author creatorfromhell
 * @since 0.1.2.0
 */
@Command({"tne", "ecomin", "ecoadmin", "ecomanage", "theneweconomy"})
@Description("Admin.Main.Description")
public class AdminCommand {

  /*@Subcommand({"ecomenu", "menu", "myeco", "ecomenu"})
  @Usage("Admin.MyEco.Arguments")
  @Description("Admin.MyEco.Description")
  @CommandPermission("tne.money.myeco")
  public void onMyEco(BukkitCommandActor sender) {
    net.tnemc.core.command.AdminCommand.onMyEco(new PaperCMDSource(sender));
  }*/

  @Subcommand({"help", "?"})
  @DefaultFor({"tne", "ecomin", "ecoadmin", "ecomanage", "theneweconomy"})
  @Usage("Help.Arguments")
  @Description("Help.Description")
  public void help(BukkitCommandActor actor, CommandHelp<String> helpEntries, @Default("1") int page) {
    BaseCommand.help(new PaperCMDSource(actor), helpEntries, page);
  }

  @Subcommand({"backup", "archive"})
  @Usage("Admin.Backup.Arguments")
  @Description("Admin.Backup.Description")
  @CommandPermission("tne.admin.backup")
  public void backup(BukkitCommandActor sender) {
    net.tnemc.core.command.AdminCommand.onBackup(new PaperCMDSource(sender));
  }

  @Subcommand({"create", "add", "new", "make", "+"})
  @Usage("Admin.Create.Arguments")
  @Description("Admin.Create.Description")
  @CommandPermission("tne.admin.create")
  public void create(BukkitCommandActor sender, String name) {
    net.tnemc.core.command.AdminCommand.onCreate(new PaperCMDSource(sender), name);
  }

  @Subcommand({"debug"})
  @Usage("Admin.Debug.Arguments")
  @Description("Admin.Debug.Description")
  @CommandPermission("tne.admin.debug")
  public void debug(BukkitCommandActor sender, DebugLevel level) {
    net.tnemc.core.command.AdminCommand.onDebug(new PaperCMDSource(sender), level);
  }

  @Subcommand({"delete", "destroy", "del", "remove", "-"})
  @Usage("Admin.Delete.Arguments")
  @Description("Admin.Delete.Description")
  @CommandPermission("tne.admin.delete")
  public void delete(BukkitCommandActor sender, String name) {
    net.tnemc.core.command.AdminCommand.onDelete(new PaperCMDSource(sender), name);
  }

  @Subcommand({"extract"})
  @Usage("Admin.Extract.Arguments")
  @Description("Admin.Extract.Description")
  @CommandPermission("tne.admin.extract")
  public void extract(BukkitCommandActor sender) {
    net.tnemc.core.command.AdminCommand.onExtract(new PaperCMDSource(sender));
  }

  @Subcommand({"purge"})
  @Usage("Admin.Purge.Arguments")
  @Description("Admin.Purge.Description")
  @CommandPermission("tne.admin.purge")
  public void purge(BukkitCommandActor sender) {
    net.tnemc.core.command.AdminCommand.onPurge(new PaperCMDSource(sender));
  }

  @Subcommand({"reload"})
  @Usage("Admin.Reload.Arguments")
  @Description("Admin.Reload.Description")
  @CommandPermission("tne.admin.reload")
  public void reload(BukkitCommandActor sender, @Default("all") String type) {
    net.tnemc.core.command.AdminCommand.onReload(new PaperCMDSource(sender), type);
  }

  @Subcommand({"reset", "nuke"})
  @Usage("Admin.Reset.Arguments")
  @Description("Admin.Reset.Description")
  @CommandPermission("tne.admin.reset")
  public void reset(BukkitCommandActor sender) {
    net.tnemc.core.command.AdminCommand.onReset(new PaperCMDSource(sender));
  }

  @Subcommand({"restore"})
  @Usage("Admin.Restore.Arguments")
  @Description("Admin.Restore.Description")
  @CommandPermission("tne.admin.restore")
  public void restore(BukkitCommandActor sender, @Default("0") int extraction) {
    net.tnemc.core.command.AdminCommand.onRestore(new PaperCMDSource(sender), extraction);
  }

  @Subcommand({"old"})
  @Usage("Admin.Restore.Arguments")
  @Description("Admin.Restore.Description")
  @CommandPermission("tne.admin.old")
  public void old(BukkitCommandActor sender, @Default("0") int extraction) {
    TNECore.server().scheduler().createDelayedTask(()->restoreOld(extraction), new ChoreTime(0), ChoreExecution.SECONDARY);
    new PaperCMDSource(sender).message(new MessageData("Messages.Admin.Restoration"));
  }

  @Subcommand({"save"})
  @Usage("Admin.Save.Arguments")
  @Description("Admin.Save.Description")
  @CommandPermission("tne.admin.save")
  public void save(BukkitCommandActor sender) {
    net.tnemc.core.command.AdminCommand.onSave(new PaperCMDSource(sender));
  }

  @Subcommand({"status"})
  @Usage("Admin.Status.Arguments")
  @Description("Admin.Status.Description")
  @CommandPermission("tne.admin.status")
  public void status(BukkitCommandActor sender, Account account, @Default("normal") AccountStatus status) {
    net.tnemc.core.command.AdminCommand.onStatus(new PaperCMDSource(sender), account, status);
  }

  @Subcommand({"version", "ver", "build"})
  @Usage("Admin.Version.Arguments")
  @Description("Admin.Version.Description")
  @CommandPermission("tne.admin.version")
  public void version(BukkitCommandActor sender) {
    net.tnemc.core.command.AdminCommand.onVersion(new PaperCMDSource(sender));
  }

  public static boolean restoreOld(@Nullable final Integer extraction) {
    final File file = new File(TNECore.directory(), "extracted.yml");

    if(!file.exists()) {

      TNECore.log().inform("The extraction file doesn't exist.", DebugLevel.OFF);
      return false;
    }
    YamlFile extracted = null;
    try {
      extracted = new YamlFile(file.getPath());
    } catch(Exception e) {
      TNECore.log().error("Failed load extraction file for writing.", e, DebugLevel.OFF);
    }

    if(extracted == null) {
      TNECore.log().inform("The extraction file doesn't exist.", DebugLevel.OFF);
      return false;
    }

    try {
      extracted.loadWithComments();
    } catch(Exception e) {
      TNECore.log().error("Failed load extraction file for writing.", e, DebugLevel.OFF);
      return false;
    }

    if(extracted.contains("Accounts")) {
      final Set<String> accounts = extracted.getConfigurationSection("Accounts").getKeys(false);

      final int frequency = (int)(accounts.size() * 0.10);
      int number = 1;

      final boolean recode = extracted.contains("Version");

      for(String name : accounts) {

        final String username = name.replaceAll("\\!", "\\.").replaceAll("\\@", "-").replaceAll("\\%", "_");
        boolean nonPlayer = false;

        UUID id = get(username);
        if(id == null) {
          nonPlayer = true;
          id = UUID.randomUUID();
        }

        final AccountAPIResponse response = TNECore.eco().account().createAccount(id.toString(), username, nonPlayer);
        if(!response.getResponse().success() || response.getAccount().isEmpty()) {
          TNECore.log().inform("Couldn't create account for " + username + ". Skipping.", DebugLevel.OFF);
          continue;
        }

        final Set<String> regions = extracted.getConfigurationSection("Accounts." + name + ".Balances").getKeys(false);
        for(String region : regions) {

          final Set<String> currencies = extracted.getConfigurationSection("Accounts." + name + ".Balances." + region).getKeys(false);
          for(String currency : currencies) {

            if(!recode) {
              final String finalCurrency = (currency.equalsIgnoreCase("default")) ? TNECore.eco().currency().getDefaultCurrency().getIdentifier() : currency;
              final Optional<Currency> cur = TNECore.eco().currency().findCurrency(finalCurrency);

              final Currency currencyObj = cur.orElseGet(() -> TNECore.eco().currency().getDefaultCurrency(TNECore.eco().region().resolve(region)));

              final BigDecimal amount = new BigDecimal(extracted.getString("Accounts." + name + ".Balances." + region + "." + currency));

              response.getAccount().get().setHoldings(new HoldingsEntry(TNECore.eco().region().resolve(region), currencyObj.getUid(),
                      amount, EconomyManager.NORMAL), TNECore.eco().getFor(currencyObj.type()).get(0).identifier());
            } else {

              final Set<String> types = extracted.getConfigurationSection("Accounts." + name + ".Balances." + region + "." + currency).getKeys(false);
              for(String type : types) {

                final BigDecimal amount = new BigDecimal(extracted.getString("Accounts." + name
                        + ".Balances." + region
                        + "." + currency + "."
                        + type));

                response.getAccount().get().setHoldings(new HoldingsEntry(region, UUID.fromString(currency),
                        amount, Identifier.fromID(type)));
              }
            }
            number++;
            try {
              final boolean message = (number % frequency == 0);

              if (message) {
                final int progress = (number * 100) / accounts.size();
                TNECore.log().inform("Restoration Progress: " + progress, DebugLevel.OFF);
              }
            } catch(Exception ignore) {}
          }
        }
      }
      TNECore.log().inform("Restoration has completed!", DebugLevel.OFF);
    }

    return true;
  }

  protected static UUID get(final String name) {
    for(OfflinePlayer player : Bukkit.getServer().getOfflinePlayers()) {
      if(player.getName() == null) continue;
      if(player.getName().equalsIgnoreCase(name)) {
        return player.getUniqueId();
      }
    }
    return null;
  }
}
