package dev.crystall.playernpclib.manager;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityPose;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import com.comphenix.protocol.wrappers.Pair;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import dev.crystall.playernpclib.Constants;
import dev.crystall.playernpclib.PlayerNPCLib;
import dev.crystall.playernpclib.api.base.BasePlayerNPC;
import dev.crystall.playernpclib.nms_v1_20_R2.wrappers.*;
import dev.crystall.playernpclib.wrapper.*;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team.OptionStatus;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;

import static dev.crystall.playernpclib.wrapper.WrapperFactory.*;

/**
 * Created by CrystallDEV on 01/09/2020
 */
public class PacketManager {

  private PacketManager() {
  }

  public static void sendScoreBoardTeamPacket(Player player, BasePlayerNPC npc) {
    BaseWrapperPlayServerScoreboardTeam wrapperTeam = new WrapperGenerator<BaseWrapperPlayServerScoreboardTeam>().map(
      BASE_WRAPPER_PLAY_SERVER_SCOREBOARD_TEAM);
    wrapperTeam.setName(Constants.NPC_TEAM_NAME);
    wrapperTeam.setMode(TeamMode.PLAYERS_ADDED);
    wrapperTeam.setPlayers(Collections.singletonList(npc.getInternalName()));
    sendPacket(player, wrapperTeam.getHandle(), false);
  }

  public static void sendScoreBoardTeamCreatePacket(Player player) {
    BaseWrapperPlayServerScoreboardTeam wrapperTeam = new WrapperGenerator<BaseWrapperPlayServerScoreboardTeam>().map(BASE_WRAPPER_PLAY_SERVER_SCOREBOARD_TEAM);
    wrapperTeam.setName(Constants.NPC_TEAM_NAME);
    wrapperTeam.setMode(TeamMode.TEAM_CREATED);
    wrapperTeam.setNameTagVisibility(OptionStatus.ALWAYS.toString());
    wrapperTeam.setPlayers(Collections.emptyList());
    sendPacket(player, wrapperTeam.getHandle(), false);
  }

  /**
   * Sends packets to the given player to create a custom npc
   *
   * @param player
   * @param npc
   */
  public static void sendNPCCreatePackets(Player player, BasePlayerNPC npc) {
    // Add entity to player list

    // Spawn entity
    WrapperPlayServerNamedEntitySpawn spawnWrapper = new WrapperPlayServerNamedEntitySpawn();
    spawnWrapper.setEntityID(npc.getEntityId());
    spawnWrapper.setPlayerUUID(npc.getUuid());
    spawnWrapper.setPosition(npc.getLocation().toVector());
    spawnWrapper.setPitch(npc.getLocation().getPitch());
    spawnWrapper.setYaw(npc.getLocation().getYaw());
    sendPacket(player, spawnWrapper.getHandle(), false);

    sendHeadRotationPacket(player, npc);

  }

  /**
   * Sends position update packets to the given player
   *
   * @param player
   * @param npc
   */
  public static void sendMovePacket(Player player, BasePlayerNPC npc) {
    // Location update
    WrapperPlayServerEntityTeleport moveWrapper = new WrapperPlayServerEntityTeleport();
    moveWrapper.setEntityID(npc.getEntityId());
    moveWrapper.setX(npc.getLocation().getX());
    moveWrapper.setY(npc.getLocation().getY());
    moveWrapper.setZ(npc.getLocation().getZ());
    moveWrapper.setYaw(npc.getLocation().getYaw());
    moveWrapper.setPitch(npc.getLocation().getPitch());
    sendPacket(player, moveWrapper.getHandle(), false);

    sendHeadRotationPacket(player, npc);
  }

  /**
   * Sends packets to hide the npc from the player
   *
   * @param player
   * @param npc
   */
  public static void sendHidePackets(Player player, BasePlayerNPC npc) {
    // Remove entity
    WrapperPlayServerEntityDestroy spawnWrapper = new WrapperPlayServerEntityDestroy();
    spawnWrapper.setEntityIds(new int[]{npc.getEntityId()});
    sendPacket(player, spawnWrapper.getHandle(), false);

    // Remove player from tab list if its still on there
  }

  /**
   * Sends packets to rotate an entities head
   *
   * @param player
   * @param npc
   */
  public static void sendHeadRotationPacket(Player player, BasePlayerNPC npc) {
    // Head rotation
    if (npc.getEyeLocation() != null) {
      WrapperPlayServerEntityHeadRotation headWrapper = new WrapperPlayServerEntityHeadRotation();
      headWrapper.setEntityID(npc.getEntityId());
      headWrapper.setHeadYaw((byte) ((npc.getEyeLocation().getYaw() % 360.0F) * 256.0F / 360.0F));
      sendPacket(player, headWrapper.getHandle(), false);
    }
  }


  public static void sendEquipmentPackets(Player player, BasePlayerNPC npc) {
    WrapperPlayServerEntityEquipment wrapper = new WrapperPlayServerEntityEquipment();
    wrapper.setEntityID(npc.getEntityId());
    wrapper.SetSlotStackPairLists(Arrays.asList(
      new Pair<>(ItemSlot.MAINHAND, npc.getItemSlots().get(ItemSlot.MAINHAND)),
      new Pair<>(ItemSlot.OFFHAND, npc.getItemSlots().get(ItemSlot.OFFHAND)),
      new Pair<>(ItemSlot.FEET, npc.getItemSlots().get(ItemSlot.FEET)),
      new Pair<>(ItemSlot.LEGS, npc.getItemSlots().get(ItemSlot.LEGS)),
      new Pair<>(ItemSlot.CHEST, npc.getItemSlots().get(ItemSlot.CHEST)),
      new Pair<>(ItemSlot.HEAD, npc.getItemSlots().get(ItemSlot.HEAD))
    ));
    sendPacket(player, wrapper.getHandle(), false);
  }

  public static void sendAnimationPacket(Player player, BasePlayerNPC npc, int animationID) {
    WrapperPlayServerAnimation animationWrapper = new WrapperPlayServerAnimation();
    animationWrapper.setEntityID(npc.getEntityId());
    animationWrapper.setAnimation(animationID);
    sendPacket(player, animationWrapper.getHandle(), false);

  }

  public static void sendDeathMetaData(Player player, BasePlayerNPC npc) {
    WrapperPlayServerEntityMetadata wrapperEntityMeta = new WrapperPlayServerEntityMetadata();
    wrapperEntityMeta.setEntityID(npc.getEntityId());

    // Create the data watcher for this entity
    var watcher = WrappedDataWatcher.getEntityWatcher(player).deepClone();
    var obj = new WrappedDataWatcherObject(6, WrappedDataWatcher.Registry.get(EnumWrappers.getEntityPoseClass()));
    watcher.setObject(obj, EntityPose.DYING.toNms());

    wrapperEntityMeta.setMetadata(watcher.getWatchableObjects());
    sendPacket(player, wrapperEntityMeta.getHandle(), false);
  }

  /**
   * Sends the given packet to the given player
   *
   * @param player
   * @param packetContainer
   */
  private static void sendPacket(Player player, PacketContainer packetContainer, boolean debug) {
      ProtocolLibrary.getProtocolManager().sendServerPacket(player, packetContainer);
      if (debug) {
        PlayerNPCLib.getPlugin().getServer().getConsoleSender().sendMessage(
                "Sent packet " + packetContainer.getType().name() + " to " + player.name()
        );
      }

  }

}
