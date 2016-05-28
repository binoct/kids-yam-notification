package com.phi.yammymessenger;

public class Entity_User {
	
	private String Account;
	private String Image;
	private String Nickname;
	private boolean OnNewMsg;
	private boolean OnReplyMsg;
	private Entity_Message[] AllMessages;
	
	/**
	 * Constructor to construct User
	 * @param Account user account, unique
	 * @param Image user image URL
	 * @param NickName user nickname
	 */
	public Entity_User(String Account, String Image, String NickName, boolean OnNewMsg, boolean OnReplyMsg){
		this.Account = Account;
		this.Image = Image;
		this.Nickname = NickName;
		this.OnNewMsg = OnNewMsg;
		this.OnReplyMsg = OnReplyMsg;
		this.AllMessages = new Entity_Message[0];
	}
	
	/**
	 * get account of this user
	 * @return account String
	 */
	public String getAccount(){
		return Account;
	}
	
	/**
	 * get image of this user
	 * @return image URL
	 */
	public String getImage(){
		return Image;
	}
	
	/**
	 * get nickname of this user
	 * @return nickname of this user
	 */
	public String getNickName(){
		return Nickname;
	}
	
	/**
	 * get whether notify if new message post
	 * @return whether notify on new message post
	 */
	public boolean getOnNewMsg(){
		return OnNewMsg;
	}
	
	/**
	 * get whether notify if new reply post
	 * @return whether notify on new reply post
	 */
	public boolean getOnReplyMsg(){
		return OnReplyMsg;
	}
	
	/**
	 * set messages of this user
	 * @param AllMessage all messages from this user
	 */
	public void setAllMessage(Entity_Message[] AllMessage){
		this.AllMessages = AllMessage;
	}
	
	/**
	 * return all messages that were posted by this user
	 * @return all messages that were posted by this user
	 */
	public Entity_Message[] getAllMessage(){
		return AllMessages;
	}
	
	/**
	 * print user information
	 */
	@Override
	public String toString(){
		String info = "";
		info += "================================\n";
		info += "*Account: "+Account+"\n";
		info += "*Image: "+Image+"\n";
		info += "*Nickname: "+Nickname+"\n";
		info += "*OnNewMsg: "+OnNewMsg+"\n";
		info += "*OnReplyMsg: "+OnReplyMsg+"\n";
		for(int x=0;x<AllMessages.length;++x){
			info += AllMessages[x].toString();
		}
		info += "================================\n";
		return info;
	}
}
