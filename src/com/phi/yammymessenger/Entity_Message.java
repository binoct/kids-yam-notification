package com.phi.yammymessenger;

public class Entity_Message {
	
	private String Msg_acc;
	private int Pid;
	private int Time;
	private String Content;
	private String Link;
	private Entity_Reply[] AllReplies;
	
	/**
	 * Constructor Message Entity
	 * @param Msg_acc author account of this message
	 * @param Pid pid of this message, unique
	 * @param Time post time of this message
	 * @param Content content of this message
	 * @param Link link of this message
	 */
	public Entity_Message(String Msg_acc, int Pid, int Time, String Content, String Link){
		this.Msg_acc = Msg_acc;
		this.Pid = Pid;
		this.Time = Time;
		this.Content = Content;
		this.Link = Link;
		this.AllReplies = new Entity_Reply[0];
	}
	
	/**
	 * get author account of this message
	 * @return author account of this message
	 */
	public String getMsgAcc(){
		return Msg_acc;
	}
	
	/**
	 * get pid of this message
	 * @return pid of this message
	 */
	public int getPid(){
		return Pid;
	}
	
	/**
	 * get post time of this message
	 * @return post time of this message
	 */
	public int getTime(){
		return Time;
	}
	
	/**
	 * get content of this message
	 * @return content of this message
	 */
	public String getContent(){
		return Content;
	}
	
	/**
	 * get link of this message
	 * @return link of this message
	 */
	public String getLink(){
		return Link;
	}
	
	/**
	 * set all replies of this message
	 * @param AllReplies all replies of this message
	 */
	public void setAllReplies(Entity_Reply[] AllReplies){
		this.AllReplies = AllReplies;
	}
	
	/**
	 * get all replies of this message
	 * @return all replies of this message
	 */
	public Entity_Reply[] getAllReplies(){
		return AllReplies;
	}
	
	@Override
	public String toString(){
		String info = "";
		info += " ---\n";
		info += " +Pid: "+Pid+"\n";
		info += " +Time: "+Time+"\n";
		info += " +Content: "+Content+"\n";
		info += " +Link: "+Link+"\n";
		for(int x=0;x<AllReplies.length;++x){
			info += AllReplies[x].toString();
		}
		info += " ---\n";
		return info;
	}
}
