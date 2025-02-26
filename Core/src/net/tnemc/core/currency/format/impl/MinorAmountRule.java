package net.tnemc.core.currency.format.impl;

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

import net.tnemc.core.account.Account;
import net.tnemc.core.account.holdings.HoldingsEntry;
import net.tnemc.core.currency.format.FormatRule;
import org.jetbrains.annotations.Nullable;

public class MinorAmountRule implements FormatRule {
  @Override
  public String name() {
    return "minor_amount";
  }

  @Override
  public String format(@Nullable Account account, HoldingsEntry entry, String format) {
    if(entry.asMonetary().scale() == 0) {
      return format.replace("<minor.amount>", "");
    }
    return format.replace("<minor.amount>", entry.asMonetary().minor());
  }
}