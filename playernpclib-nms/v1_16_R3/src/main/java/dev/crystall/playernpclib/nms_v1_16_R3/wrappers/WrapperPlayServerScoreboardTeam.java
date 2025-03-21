package dev.crystall.playernpclib.nms_v1_16_R3.wrappers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import dev.crystall.playernpclib.wrapper.AbstractPacket;
import dev.crystall.playernpclib.wrapper.BaseWrapperPlayServerScoreboardTeam;
import java.util.Collection;
import java.util.List;
import org.bukkit.ChatColor;

public class WrapperPlayServerScoreboardTeam extends AbstractPacket implements BaseWrapperPlayServerScoreboardTeam {

  public WrapperPlayServerScoreboardTeam() {
    super(new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM), PacketType.Play.Server.SCOREBOARD_TEAM);
    handle.getModifier().writeDefaults();
  }

  public WrapperPlayServerScoreboardTeam(PacketContainer packet) {
    super(packet, PacketType.Play.Server.SCOREBOARD_TEAM);
  }

  @Override
  public String getName() {
    return handle.getStrings().read(0);
  }

  @Override
  public void setName(String value) {
    handle.getStrings().write(0, value);
  }

  @Override
  public WrappedChatComponent getDisplayName() {
    return handle.getChatComponents().read(0);
  }

  @Override
  public void setDisplayName(WrappedChatComponent value) {
    handle.getChatComponents().write(0, value);
  }

  @Override
  public WrappedChatComponent getPrefix() {
    return handle.getChatComponents().read(1);
  }

  @Override
  public void setPrefix(WrappedChatComponent value) {
    handle.getChatComponents().write(1, value);
  }

  @Override
  public WrappedChatComponent getSuffix() {
    return handle.getChatComponents().read(2);
  }

  @Override
  public void setSuffix(WrappedChatComponent value) {
    handle.getChatComponents().write(2, value);
  }

  @Override
  public String getNameTagVisibility() {
    return handle.getStrings().read(1);
  }

  @Override
  public void setNameTagVisibility(String value) {
    handle.getStrings().write(1, value);
  }

  @Override
  public ChatColor getColor() {
    return handle.getEnumModifier(ChatColor.class, MinecraftReflection.getMinecraftClass("EnumChatFormat")).read(0);
  }

  @Override
  public void setColor(ChatColor value) {
    handle.getEnumModifier(ChatColor.class, MinecraftReflection.getMinecraftClass("EnumChatFormat")).write(0, value);
  }

  @Override
  public String getCollisionRule() {
    return handle.getStrings().read(2);
  }

  @Override
  public void setCollisionRule(String value) {
    handle.getStrings().write(2, value);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<String> getPlayers() {
    return (List<String>) handle.getSpecificModifier(Collection.class)
      .read(0);
  }

  @Override
  public void setPlayers(List<String> value) {
    handle.getSpecificModifier(Collection.class).write(0, value);
  }

  @Override
  public int getMode() {
    return handle.getIntegers().read(0);
  }

  @Override
  public void setMode(int value) {
    handle.getIntegers().write(0, value);
  }

  @Override
  public int getPackOptionData() {
    return handle.getIntegers().read(1);
  }

  @Override
  public void setPackOptionData(int value) {
    handle.getIntegers().write(1, value);
  }

}
