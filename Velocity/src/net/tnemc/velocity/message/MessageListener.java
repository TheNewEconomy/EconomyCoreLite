package net.tnemc.velocity.message;
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

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import net.tnemc.bungee.message.MessageManager;

/**
 * MessageManager
 *
 * @author creatorfromhell
 * @since 0.1.2.0
 */
public class MessageListener {

  @Subscribe
  public void handle(PluginMessageEvent event) {
    final String id = event.getIdentifier().toString();

    if(!id.startsWith("tne:")) {
      return;
    }
    MessageManager.instance().onMessage(id, event.getData());
  }
}