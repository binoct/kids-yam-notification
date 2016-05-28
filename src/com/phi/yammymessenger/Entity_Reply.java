package com.phi.yammymessenger;

public class Entity_Reply {
	
	private int Re_pid;
	private String ReplierAcc;
	private int ReTime;
	private String ReContent;
	private String ReplierImg;
	private String ReplierName;
	
	/**
	 * Constructor of reply entity
	 * @param Re_pid pid of message
	 * @param ReplierAcc account of replier
	 * @param ReTime post time of this reply
	 * @param ReContent content of this reply
	 * @param ReplierImg image URL of replier of this replt
	 * @param ReplierName nickname of this replier
	 */
	public Entity_Reply(int Re_pid, String ReplierAcc, int ReTime, String ReContent, String ReplierImg, String ReplierName){
		this.Re_pid = Re_pid;
		this.ReplierAcc = ReplierAcc;
		this.ReTime = ReTime;
		this.ReContent = ReContent;
		this.ReplierImg = ReplierImg;
		this.ReplierName = ReplierName;
	}
	
	/**
	 * get message pid
	 * @return pid of this message
	 */
	public int getRePid(){
		return Re_pid;
	}
	
	/**
	 * get account of the replier
	 * @return account of the replier
	 */
	public String getReplierAcc(){
		return ReplierAcc;
	}
	
	/**
	 * get reply time of this reply
	 * @return post time of this reply
	 */
	public int getReTime(){
		return ReTime;
	}
	
	/**
	 * get content of this reply
	 * @return content of this reply
	 */
	public String getReContent(){
		return ReContent;
	}
	
	/**
	 * get image of the replier
	 * @return image of the replier
	 */
	public String getReplierImg(){
		return ReplierImg;
	}
	
	/**
	 * get the name of the replier
	 * @return the name of the replier
	 */
	public String getReplierName(){
		return ReplierName;
	}
	
	@Override
	public String toString(){
		String info = "";
		info += "  .re_pid: "+Re_pid+"\n";
		info += "  .replierAcc: "+ReplierAcc+"\n";
		info += "  .reTime: "+ReTime+"\n";
		info += "  .replierName: "+ReplierName+"\n";
		info += "  .reContent: "+ReContent+"\n";
		info += "  .replierImg: "+ReplierImg+"\n";
		return info;
	}
}
