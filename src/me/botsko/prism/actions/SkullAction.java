package me.botsko.prism.actions;

import java.text.SimpleDateFormat;

import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;

public class SkullAction extends GenericAction {
	
	/**
	 * 
	 */
	protected SkullActionData actionData;
	
	
	/**
	 * 
	 * @param action_type
	 * @param block
	 * @param player
	 */
	public SkullAction( ActionType action_type, Block block, String player ){
		
		// Build an object for the specific details of this action
		actionData = new SkullActionData();
		
		// Store information for the action
		if(action_type != null){
			this.type = action_type;
		}
		if(block != null){
			Skull s = (Skull)block.getState();
			actionData.rotation = s.getRotation().name().toLowerCase();
			actionData.owner = s.getOwner();
			actionData.skull_type = s.getSkullType().name().toLowerCase();
			actionData.block_id = block.getTypeId();
			actionData.block_subid = block.getData();
			this.world_name = block.getWorld().getName();
			this.x = block.getLocation().getX();
			this.y = block.getLocation().getY();
			this.z = block.getLocation().getZ();
		}
		if(player != null){
			this.player_name = player;
		}
		if(action_time == null){
			java.util.Date date= new java.util.Date();
			action_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date.getTime());
		}
		
		// Set data from current block
		setDataFromObject();
		setObjectFromData();
		
	}
	
	
	/**
	 * 
	 */
	public void setData( String data ){
		this.data = data;
		setObjectFromData();
	}
	
	
	/**
	 * 
	 */
	protected void setDataFromObject(){
		data = gson.toJson(actionData);
	}
	
	
	/**
	 * 
	 */
	protected void setObjectFromData(){
		if(data != null){
			actionData = gson.fromJson(data, SkullActionData.class);
		}
	}
	
	
	/**
	 * 
	 */
	public int getBlock_id(){
		return actionData.block_id;
	}
	
	
	/**
	 * 
	 * @param id
	 */
	public void setBlockId( int id ){
		actionData.block_id = id;
		setDataFromObject();
	}
	
	
	/**
	 * 
	 */
	public byte getBlock_subid(){
		return actionData.block_subid;
	}
	
	
	/**
	 * 
	 * @param id
	 */
	public void setBlockSubId( byte id ){
		actionData.block_subid = id;
		setDataFromObject();
	}
	
	
	/**
	 * 
	 * @return
	 */
	public SkullType getSkullType(){
		if(actionData.skull_type != null){
			return SkullType.valueOf(actionData.skull_type.toUpperCase());
		}
		return null;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public BlockFace getRotation(){
		if(actionData.rotation != null){
			return BlockFace.valueOf(actionData.rotation.toUpperCase());
		}
		return null;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public String getNiceName(){
		return actionData.skull_type + " skull";
	}
}