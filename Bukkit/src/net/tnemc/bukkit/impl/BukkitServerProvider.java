package net.tnemc.bukkit.impl;

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

import net.tnemc.bukkit.TNE;
import net.tnemc.bukkit.impl.scheduler.BukkitScheduler;
import net.tnemc.core.compatibility.CmdSource;
import net.tnemc.core.compatibility.PlayerProvider;
import net.tnemc.core.compatibility.ProxyProvider;
import net.tnemc.core.compatibility.ServerConnector;
import net.tnemc.core.compatibility.scheduler.SchedulerProvider;
import net.tnemc.core.region.RegionMode;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.bukkit.BukkitCommandActor;
import revxrsal.commands.command.CommandActor;

import java.util.Optional;
import java.util.UUID;

/**
 * BukkitServerProvider
 *
 * @author creatorfromhell
 * @since 0.1.2.0
 */
public class BukkitServerProvider implements ServerConnector {

  protected final BukkitProxyProvider proxy = new BukkitProxyProvider();

  protected final BukkitScheduler scheduler;

  public BukkitServerProvider() {
    this.scheduler = new BukkitScheduler();
  }

  @Override
  public String name() {
    return "bukkit";
  }

  /**
   * The proxy provider to use for this implementation.
   *
   * @return The proxy provider to use for this implementation.
   */
  @Override
  public ProxyProvider proxy() {
    return proxy;
  }

  /**
   * Used to convert an {@link CommandActor} to a {@link CmdSource}.
   *
   * @param actor The command actor.
   *
   * @return The {@link CmdSource} for this actor.
   */
  @Override
  public CmdSource<?> source(@NotNull CommandActor actor) {
    return new BukkitCMDSource((BukkitCommandActor)actor);
  }

  /**
   * Used to get the amount of online players.
   *
   * @return The amount of online players.
   */
  @Override
  public int onlinePlayers() {
    return Bukkit.getOnlinePlayers().size();
  }

  /**
   * Attempts to find a {@link PlayerProvider player} based on an {@link UUID identifier}.
   *
   * @param identifier The identifier
   *
   * @return An Optional containing the located {@link PlayerProvider player}, or an empty
   * Optional if no player is located.
   */
  @Override
  public Optional<PlayerProvider> findPlayer(@NotNull UUID identifier) {

    return Optional.of(BukkitPlayerProvider.find(identifier.toString()));
  }

  /**
   * Used to determine if this player has played on this server before.
   *
   * @param uuid The {@link UUID} that is associated with the player.
   *
   * @return True if the player has played on the server before, otherwise false.
   */
  @Override
  public boolean playedBefore(UUID uuid) {
    return false;
  }

  /**
   * Used to determine if a player with the specified username has played
   * before.
   *
   * @param name The username to search for.
   *
   * @return True if someone with the specified username has played before,
   * otherwise false.
   */
  @Override
  public boolean playedBefore(String name) {
    return false;
  }

  /**
   * Used to determine if a player with the specified username is online.
   *
   * @param name The username to search for.
   *
   * @return True if someone with the specified username is online.
   */
  @Override
  public boolean online(String name) {
    try {

      final UUID id = UUID.fromString(name);
      return Bukkit.getPlayer(id) != null;
    } catch(Exception ignore) {
      return Bukkit.getPlayer(name) != null;
    }
  }

  @Override
  public Optional<UUID> fromName(String name) {
    for(OfflinePlayer player : Bukkit.getServer().getOfflinePlayers()) {
      if(player.getName() == null) continue;
      if(player.getName().equalsIgnoreCase(name)) {
        return Optional.of(player.getUniqueId());
      }
    }
    return Optional.empty();
  }

  /**
   * Used to locate a username for a specific name. This could be called from either a primary or
   * secondary thread, and should not call back to the Mojang API ever.
   *
   * @param id The {@link UUID} to use for the search.
   *
   * @return An optional containing the name if exists, otherwise false.
   */
  @Override
  public Optional<String> fromID(UUID id) {
    for(OfflinePlayer player : Bukkit.getServer().getOfflinePlayers()) {
      if(player.getUniqueId().equals(id)) {
        return Optional.ofNullable(player.getName());
      }
    }
    return Optional.empty();
  }

  /**
   * Returns the name of the default region.
   *
   * @param mode The {@link RegionMode} to use for this.
   *
   * @return The name of the default region. This could be different based on the current
   * {@link RegionMode}.
   */
  @Override
  public String defaultRegion(final RegionMode mode) {

    if(mode.name().equalsIgnoreCase("biome")) {
      final World world = Bukkit.getServer().getWorlds().get(0);

      return world.getSpawnLocation().getBlock().getBiome().getKey().getKey();
    }
    return Bukkit.getServer().getWorlds().get(0).getName();
  }

  /**
   * Determines if a plugin with the correlating name is currently installed.
   *
   * @param name The name to use for our check.
   *
   * @return True if a plugin with that name exists, otherwise false.
   */
  @Override
  public boolean pluginAvailable(String name) {
    return Bukkit.getPluginManager().isPluginEnabled(name);
  }

  /**
   * Used to replace colour codes in a string.
   *
   * @param string The string to format.
   *
   * @return The formatted string.
   */
  @Override
  public String replaceColours(String string) {
    return ChatColor.translateAlternateColorCodes('&', string);
  }

  @Override
  public void saveResource(String path, boolean replace) {
    TNE.instance().saveResource(path, replace);
  }

  /**
   * Provides this implementation's {@link SchedulerProvider scheduler}.
   *
   * @return The scheduler for this implementation.
   */
  @Override
  public BukkitScheduler scheduler() {
    return scheduler;
  }
}