package net.tnemc.bungee.message.backlog;
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

/**
 * BacklogEntry
 *
 * @author creatorfromhell
 * @since 0.1.2.0
 */
public class BacklogEntry {

  private final String channel;
  private final byte[] out;

  public BacklogEntry(String channel, byte[] out) {
    this.channel = channel;
    this.out = out;
  }

  public String channel() {
    return channel;
  }

  public byte[] out() {
    return out;
  }
}