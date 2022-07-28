package net.tnemc.core.config;
/*
 * The New Economy
 * Copyright (C) 2022 Daniel "creatorfromhell" Vidmar
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

import net.tnemc.core.TNECore;
import net.tnemc.core.account.Account;
import net.tnemc.core.account.PlayerAccount;
import net.tnemc.core.io.message.translation.Language;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * MessageConfig
 *
 * @author creatorfromhell
 * @since 0.1.2.0
 */
public class MessageConfig extends Config {

  private final Map<String, Language> languages = new HashMap<>();

  public MessageConfig() {
    super(new File(TNECore.directory(), "messages.yml"), "resources/messages.yml", "Messages");
  }

  public String getString(final String node, final UUID player) {
    final Optional<Account> account = TNECore.eco().account().findAccount(player);

    if(account.isPresent() && account.get() instanceof PlayerAccount) {

      return getString(node, ((PlayerAccount)account.get()).getLanguage());
    }
    return yaml.getString(node);
  }

  public String getString(final String node, final String lang) {
    if(languages.containsKey(lang) && languages.get(lang).getConfig().contains(node)) {
      return languages.get(lang).getConfig().getString(node);
    }
    return yaml.getString(node);
  }

  @Override
  public boolean load() {

    loadLanguages();

    return super.load();
  }

  private void loadLanguages() {
   final File directory = new File(TNECore.directory(), "languages");
    if(!directory.exists()) {
      directory.mkdir();
      return;
    }

    final File[] langFiles = directory.listFiles((dir, name) -> name.endsWith(".yml"));

    if(langFiles != null) {
      for (File langFile : langFiles) {
        String name = langFile.getName().replace(".yml", "");
        YamlFile config = new YamlFile(langFile);
        try {
          config.loadWithComments();
        } catch (Exception ignore) {
          TNECore.log().debug("Failed to load language: " + name);
        }

        if(config.exists() && !config.isEmpty()) {
          final Language lang = new Language(name, config);

          languages.put(name, lang);

          TNECore.log().inform("Loaded language: " + name);
        }
      }
    }
  }
}
