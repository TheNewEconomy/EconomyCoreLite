package net.tnemc.core.command;

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

import co.aikar.commands.BaseCommand;
import net.tnemc.core.TNECore;
import net.tnemc.core.account.Account;
import net.tnemc.core.account.PlayerAccount;
import net.tnemc.core.account.holdings.HoldingsEntry;
import net.tnemc.core.account.holdings.HoldingsType;
import net.tnemc.core.account.holdings.modify.HoldingsModifier;
import net.tnemc.core.account.holdings.modify.HoldingsOperation;
import net.tnemc.core.actions.source.PlayerSource;
import net.tnemc.core.compatibility.CmdSource;
import net.tnemc.core.compatibility.PlayerProvider;
import net.tnemc.core.currency.Currency;
import net.tnemc.core.io.message.MessageData;
import net.tnemc.core.transaction.Receipt;
import net.tnemc.core.transaction.Transaction;
import net.tnemc.core.transaction.TransactionResult;
import net.tnemc.core.transaction.processor.BaseTransactionProcessor;
import net.tnemc.core.utils.exceptions.InvalidTransactionException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * MoneyCommands
 *
 * @author creatorfromhell
 * @since 0.1.2.0
 */
public class MoneyCommand extends BaseCommand {

  //Arguments: [currency]
  public static void onMyBal(CmdSource sender, String[] args) {
    if(sender.player().isPresent()) {
      sender.player().get().inventory().openMenu(sender.player().get(), "my_bal");
    }
  }

  //Arguments: [currency] [world]
  public static void onBalance(CmdSource sender, String[] args) {
    final String currency = (args.length >= 1)? args[0] : "USD";

    //If our currency doesn't exist this is probably a username, so check for their balance instead.
    Optional<Currency> currencyOptional = TNECore.eco().currency().findCurrency(currency);
    if(currencyOptional.isEmpty()) {
      final String identifier = (args.length > 0)? args[0] : sender.identifier().toString();
      onOther(sender, identifier, ((args.length > 1)? Arrays.copyOfRange(args, 1, args.length) : new String[0]));
    }
  }

  //Arguments: <amount> <to currency> [from currency]
  public static void onConvert(CmdSource sender, String[] args) {

    long startTime = System.nanoTime();
    if(args.length < 2) {
      //TODO: Help
      return;
    }

    final String currency = args[1];
    final String fromCurrency = (args.length >= 3)? args[2] : "USD";
    //TODO: Default currency.

    Optional<Currency> currencyOBJ = TNECore.eco().currency().findCurrency(currency);
    Optional<Currency> currencyFromOBJ = TNECore.eco().currency().findCurrency(fromCurrency);

    if(currencyOBJ.isEmpty() || currencyFromOBJ.isEmpty()) {
      return;
    }

    if(currency.equalsIgnoreCase(fromCurrency)) {
      final MessageData data = new MessageData("Messages.General.NoPlayer");
      data.addReplacement("$player", sender.name());
      sender.message(data);
      return;
    }

    Optional<Account> account = sender.account();

    if(account.isEmpty()) {
      sender.message(new MessageData("Messages.Money.ConvertSame"));
      return;
    }

    final BigDecimal parsed = new BigDecimal(args[0]);
    final BigDecimal parsedTo = new BigDecimal(args[0]).multiply(BigDecimal.valueOf(currencyFromOBJ.get().getConversion().get(currency)));

    final HoldingsModifier modifier = new HoldingsModifier(sender.region(),
                                                           currencyOBJ.get().getUid(),
                                                           parsedTo);

    final HoldingsModifier modifierFrom = new HoldingsModifier(sender.region(),
                                                               currencyFromOBJ.get().getUid(),
                                                               parsed.negate());
    //TODO: Value args check

    final Transaction transaction = new Transaction("convert")
        .from(account.get(), modifierFrom)
        .to(account.get(), modifier)
        .processor(new BaseTransactionProcessor())
        .source(new PlayerSource(sender.identifier()));

    Optional<Receipt> receipt = processTransaction(sender, transaction);


    //TODO: Better conversion success message!
    final MessageData data = new MessageData("Messages.Money.Converted");
    data.addReplacement("$from_amount", parsed.toPlainString());
    data.addReplacement("$amount", parsedTo.toPlainString());
    sender.message(data);

    //TODO: Receipt logging and success checking
    long endTime = System.nanoTime();

    long duration = (endTime - startTime);

    sender.message(new MessageData("<red>Transaction took " + duration + "to execute!"));

    //TODO: Success message
  }

  //Arguments: <player> <amount> [world] [currency]
  public static void onGive(CmdSource sender, String[] args) {
    long startTime = System.nanoTime();
    if(args.length < 2) {
      //TODO: Help
      return;
    }

    final String region = (args.length >= 3)? args[2] : sender.region();
    final String currency = (args.length >= 4)? args[3] : "USD";
    //TODO: Default currency.
    //TODO: Check if currency exists.

    Optional<Currency> cur = TNECore.eco().currency().findCurrency(currency);

    Optional<Account> account = TNECore.eco().account().findAccount(args[0]);

    if(account.isEmpty()) {
      final MessageData data = new MessageData("Messages.General.NoPlayer");
      data.addReplacement("$player", args[0]);
      sender.message(data);
      return;
    }

    final HoldingsModifier modifier = new HoldingsModifier(region,
                                                           cur.get().getUid(),
                                                           new BigDecimal(args[1]));
    //TODO: Value args check

    final Transaction transaction = new Transaction("give")
        .to(account.get(), modifier)
        .processor(new BaseTransactionProcessor())
        .source(new PlayerSource(sender.identifier()));

    Optional<Receipt> receipt = processTransaction(sender, transaction);

    //TODO: Receipt logging and success checking
    long endTime = System.nanoTime();

    long duration = (endTime - startTime);

    sender.message(new MessageData("<red>Transaction took " + duration + "to execute!"));

    //TODO: Success message
  }

  //Arguments: <amount> [currency]
  public static void onNote(CmdSource sender, String[] args) {
    long startTime = System.nanoTime();
    if(args.length < 1) {
      //TODO: Help
      return;
    }

    final String currency = (args.length >= 2)? args[1] : "USD";
    //TODO: Default currency.

    Optional<Account> account = sender.account();

    if(account.isEmpty()) {
      final MessageData data = new MessageData("Messages.General.NoPlayer");
      data.addReplacement("$player", args[0]);
      sender.message(data);
      return;
    }

    Optional<Currency> cur = TNECore.eco().currency().findCurrency(currency);

    final HoldingsModifier modifier = new HoldingsModifier(sender.region(),
                                                           cur.get().getUid(),
                                                           new BigDecimal(args[0]));
    //TODO: Value args check

    final Transaction transaction = new Transaction("note")
        .from(account.get(), modifier.counter())
        .processor(new BaseTransactionProcessor())
        .source(new PlayerSource(sender.identifier()));


    Optional<Receipt> receipt = processTransaction(sender, transaction);

    //TODO: Receipt logging and success checking
    long endTime = System.nanoTime();

    long duration = (endTime - startTime);

    sender.message(new MessageData("<red>Transaction took " + duration + "to execute!"));
  }

  public static void onOther(CmdSource sender, String[] args) {
    if(args.length < 1) {
      //TODO: help
      return;
    }
    onOther(sender, args[0], ((args.length > 1)? Arrays.copyOfRange(args, 1, args.length) : new String[0]));
  }

  //Arguments: <player> [world] [currency]
  public static void onOther(CmdSource sender, String identifier, String[] args) {

    final String currency = (args.length >= 1)? args[0] : "USD";
    final String region = (args.length >= 2)? args[1] : sender.region();

    //TODO: Default currency.

    Optional<Currency> cur = TNECore.eco().currency().findCurrency(currency);

    Optional<Account> account = TNECore.eco().account().findAccount(identifier);

    if(account.isEmpty()) {
      final MessageData data = new MessageData("Messages.General.NoPlayer");
      data.addReplacement("$player", identifier);
      sender.message(data);
      return;
    }

    final List<HoldingsEntry> holdings = new ArrayList<>();

    if(args.length >= 2) {
      BigDecimal amount = BigDecimal.ZERO;
      for(HoldingsEntry entry : account.get().getHoldings(region, cur.get().getUid())) {
        amount = amount.add(entry.getAmount());
      }
      holdings.add(new HoldingsEntry(region, cur.get().getUid(), amount, HoldingsType.NORMAL_HOLDINGS));
    } else {
      holdings.addAll(account.get().getAllHoldings(region, HoldingsType.NORMAL_HOLDINGS));
    }

    if(holdings.isEmpty()) {

      //Shouldn't happen, but if it does handle it.
      final MessageData msg = new MessageData("Messages.Account.NoHoldings");
      msg.addReplacement("$currency", currency);
      sender.message(msg);
      return;
    }

    final MessageData msg = new MessageData("Messages.Money.HoldingsMulti");
    msg.addReplacement("$world", region);
    sender.message(msg);

    for(HoldingsEntry entry : holdings) {

      Optional<Currency> entryCur = TNECore.eco().currency().findCurrency(entry.getCurrency());

      final MessageData entryMSG = new MessageData("Messages.Money.HoldingsMultiSingle");
      entryMSG.addReplacement("$currency", entryCur.get().getIdentifier());
      entryMSG.addReplacement("$amount", entry.getAmount().toPlainString());
      sender.message(entryMSG);
    }
    //TODO: currency formatting.
  }

  //Arguments: <player> <amount> [currency] [from:account]
  public static void onPay(CmdSource sender, String[] args) {
    long startTime = System.nanoTime();
    if(args.length < 2) {
      //TODO: Help
      return;
    }

    final String currency = (args.length >= 3)? args[2] : "USD";

    Optional<Currency> cur = TNECore.eco().currency().findCurrency(currency);
    //TODO: Default currency.

    Optional<Account> account = TNECore.eco().account().findAccount(args[0]);
    Optional<Account> senderAccount = sender.account();

    if(account.isEmpty() || senderAccount.isEmpty()) {
      final MessageData data = new MessageData("Messages.General.NoPlayer");
      data.addReplacement("$player", args[0]);
      sender.message(data);
      return;
    }

    final Optional<PlayerProvider> provider = TNECore.server().findPlayer(((PlayerAccount)account.get()).getUUID());
    if(provider.isEmpty()) {
      final MessageData data = new MessageData("Messages.General.NoPlayer");
      data.addReplacement("$player", args[0]);
      sender.message(data);
      return;
    }

    final HoldingsModifier modifier = new HoldingsModifier(sender.region(),
                                                           cur.get().getUid(),
                                                           new BigDecimal(args[1]));
    //TODO: Value args check

    final Transaction transaction = new Transaction("pay")
        .to(account.get(), modifier)
        .from(senderAccount.get(), modifier.counter())
        .processor(new BaseTransactionProcessor())
        .source(new PlayerSource(sender.identifier()));

    final Optional<Receipt> receipt = processTransaction(sender, transaction);

    //TODO: Receipt logging and success checking
    long endTime = System.nanoTime();

    long duration = (endTime - startTime);

    sender.message(new MessageData("<red>Transaction took " + duration + "to execute!"));

    //TODO: Success message
  }

  //Arguments: <player> <amount> [currency]
  public static void onRequest(CmdSource sender, String[] args) {
    long startTime = System.nanoTime();
    if(args.length < 2) {
      //TODO: Help
      return;
    }

    final String currency = (args.length >= 3)? args[2] : "USD";
    //TODO: Default currency.

    Optional<Account> account = TNECore.eco().account().findAccount(args[0]);

    if(account.isEmpty() || !TNECore.server().online(args[0])) {
      final MessageData data = new MessageData("Messages.General.NoPlayer");
      data.addReplacement("$player", args[0]);
      sender.message(data);
      return;
    }

    Optional<PlayerProvider> provider = TNECore.server().findPlayer(((PlayerAccount)account.get()).getUUID());
    if(provider.isEmpty()) {
      final MessageData data = new MessageData("Messages.General.NoPlayer");
      data.addReplacement("$player", args[0]);
      sender.message(data);
      return;
    }

    final MessageData msg = new MessageData("Messages.Money.RequestSender");
    msg.addReplacement("$player", args[0]);
    msg.addReplacement("$amount", args[1]);
    sender.message(msg);

    final MessageData request = new MessageData("Messages.Money.Request");
    request.addReplacement("$player", sender.name());
    request.addReplacement("$amount", args[1]);
    request.addReplacement("$currency", currency);
    provider.get().message(request);

    long endTime = System.nanoTime();

    long duration = (endTime - startTime);

    sender.message(new MessageData("<red>Transaction took " + duration + "to execute!"));
  }

  //Arguments: <player> <amount> [world] [currency]
  public static void onSet(CmdSource sender, String[] args) {
    long startTime = System.nanoTime();
    if(args.length < 2) {
      //TODO: Help
      return;
    }

    final String region = (args.length >= 3)? args[2] : sender.region();
    final String currency = (args.length >= 4)? args[3] : "USD";

    Optional<Currency> cur = TNECore.eco().currency().findCurrency(currency);
    //TODO: Default currency.

    Optional<Account> account = TNECore.eco().account().findAccount(args[0]);

    if(account.isEmpty()) {
      final MessageData data = new MessageData("Messages.General.NoPlayer");
      data.addReplacement("$player", args[0]);
      sender.message(data);
      return;
    }

    final HoldingsModifier modifier = new HoldingsModifier(region,
                                                           cur.get().getUid(),
                                                           new BigDecimal(args[1]),
                                                           HoldingsOperation.SET);
    //TODO: Value args check

    final Transaction transaction = new Transaction("set")
        .to(account.get(), modifier)
        .processor(new BaseTransactionProcessor())
        .source(new PlayerSource(sender.identifier()));

    Optional<Receipt> receipt = processTransaction(sender, transaction);

    final MessageData msg = new MessageData("Messages.Money.Set");
    msg.addReplacement("$player", args[0]);
    msg.addReplacement("$amount", args[1]);
    sender.message(msg);

    //TODO: Receipt logging and success checking
    long endTime = System.nanoTime();

    long duration = (endTime - startTime);

    sender.message(new MessageData("<red>Transaction took " + duration + "to execute!"));

    //TODO: Success message
  }

  //Arguments: <amount> [world]
  public static void onSetAll(CmdSource sender, String[] args) {

  }

  //Arguments: <player> <amount> [world] [currency]
  public static void onTake(CmdSource sender, String[] args) {

    long startTime = System.nanoTime();
    if(args.length < 2) {
      //TODO: Help
      return;
    }

    final String region = (args.length >= 3)? args[2] : sender.region();
    final String currency = (args.length >= 4)? args[3] : "USD";

    Optional<Currency> cur = TNECore.eco().currency().findCurrency(currency);
    //TODO: Default currency.

    Optional<Account> account = TNECore.eco().account().findAccount(args[0]);

    if(account.isEmpty()) {
      final MessageData data = new MessageData("Messages.General.NoPlayer");
      data.addReplacement("$player", args[0]);
      sender.message(data);
      return;
    }

    final HoldingsModifier modifier = new HoldingsModifier(region,
                                                           cur.get().getUid(),
                                                           new BigDecimal(args[1]));
    //TODO: Value args check

    final Transaction transaction = new Transaction("take")
        .to(account.get(), modifier.counter())
        .processor(new BaseTransactionProcessor())
        .source(new PlayerSource(sender.identifier()));

    Optional<Receipt> receipt = processTransaction(sender, transaction);

    //TODO: Receipt logging and success checking
    long endTime = System.nanoTime();

    long duration = (endTime - startTime);

    sender.message(new MessageData("<red>Transaction took " + duration + "to execute!"));

    //TODO: Success message
  }

  //Arguments: [page] [currency:name] [world:world] [limit:#]
  public static void onTop(CmdSource sender, String[] args) {

  }

  private static Optional<Receipt> processTransaction(CmdSource sender, Transaction transaction) {
    try {
      final TransactionResult result = transaction.process();

      if(!result.isSuccessful()) {
        sender.message(new MessageData(result.getMessage()));
        return Optional.empty();
      }

      if(result.getReceipt().isPresent()) {
        if(transaction.getTo() != null) {
          transaction.getTo().asAccount().ifPresent((account->account.log(result.getReceipt().get())));
        }

        if(transaction.getFrom() != null) {
          transaction.getFrom().asAccount().ifPresent((account->account.log(result.getReceipt().get())));
        }
      }

      System.out.println(result.getMessage());
      return result.getReceipt();
    } catch(InvalidTransactionException e) {
      e.printStackTrace();
    }
    return Optional.empty();
  }
}