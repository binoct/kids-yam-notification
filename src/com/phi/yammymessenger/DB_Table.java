package com.phi.yammymessenger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
 
// 資料功能類別
public class DB_Table {
	// 表格名稱	
	public static final String TABLE_NAME_USER = "User";
	public static final String TABLE_NAME_MESSAGE = "Message";
	public static final String TABLE_NAME_REPLY = "Reply";
	public static final String TABLE_NAME_SETTING = "Setting";
 
	// User 這個表格
	public static final String ACCOUNT_COLUMN = "Account";
	public static final String IMAGE_COLUMN = "Image";
	public static final String NICK_NAME_COLUMN = "NickName";
	public static final String ON_NEW_MSG_COLUMN = "OnNewMsg";
	public static final String ON_REPLY_MSG_COLUMN = "OnReplyMsg";
	
	// Message 這個表格
	public static final String MSG_ACC_COLUMN = "Msg_acc";
	public static final String PID_COLUMN = "Pid";
	public static final String TIME_COLUMN = "Time";
	public static final String CONTENT_COLUMN = "Content";
	public static final String LINK_COLUMN = "Link";
	
	// Reply 這個表格
	public static final String RE_KEY_ID = "Re_key";
	public static final String RE_PID_COLUMN = "Re_pid";
	public static final String REPLIER_ACC_COLUMN = "ReplierAcc";
	public static final String RE_TIME_COLUMN = "ReTime";
	public static final String RE_CONTENT_COLUMN = "ReContent";
	public static final String REPLIER_IMG_COLUMN = "ReplierImg";
	public static final String REPLIER_NAME_COLUMN = "ReplierName";
	
	// Setting 這個表格
	public static final String SETTING_KEY_COLOMN = "SettingKey";
	public static final String ENABLE_COLUMN = "Enable";
	public static final String NOTI_ID_COLUMN = "NotiId";
	
	// 使用上面宣告的變數建立表格的SQL指令
	public static final String CREATE_TABLE_USER = 
			"CREATE TABLE " + TABLE_NAME_USER + " (" + 
			ACCOUNT_COLUMN + " TEXT PRIMARY KEY, " +
			IMAGE_COLUMN + " TEXT NOT NULL, " +
			NICK_NAME_COLUMN + " TEXT NOT NULL, " +
			ON_NEW_MSG_COLUMN + " INTEGER NOT NULL, " +
			ON_REPLY_MSG_COLUMN + " INTEGER NOT NULL)";
	
	public static final String CREATE_TABLE_MESSAGE = 
			"CREATE TABLE " + TABLE_NAME_MESSAGE + " (" + 
			MSG_ACC_COLUMN + " TEXT NOT NULL, " +
			PID_COLUMN + " INTEGER PRIMARY KEY, " +
			TIME_COLUMN + " INTEGER NOT NULL, " +
			CONTENT_COLUMN + " TEXT NOT NULL, " +
			LINK_COLUMN + " TEXT)";
	
	public static final String CREATE_TABLE_REPLY = 
			"CREATE TABLE " + TABLE_NAME_REPLY + " (" + 
			RE_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
			RE_PID_COLUMN + " INTEGER NOT NULL, " +
			REPLIER_ACC_COLUMN + " TEXT NOT NULL, " +
			RE_TIME_COLUMN + " INTEGER NOT NULL, " +
			RE_CONTENT_COLUMN + " TEXT NOT NULL, " +
			REPLIER_IMG_COLUMN + " TEXT NOT NULL, " +
			REPLIER_NAME_COLUMN + " TEXT NOT NULL)";
	
	public static final String CREATE_TABLE_SETTING =
			"CREATE TABLE " + TABLE_NAME_SETTING + " (" + 
			SETTING_KEY_COLOMN + " INTEGER PRIMARY KEY, " +
			ENABLE_COLUMN + " INTEGER NOT NULL, " + "" +
			NOTI_ID_COLUMN + " INTEGER NOT NULL)";
	
	public static final String INIT_TABLE_SETTING =
			"INSERT INTO " + TABLE_NAME_SETTING + " ("
			+ SETTING_KEY_COLOMN + ", "
			+ ENABLE_COLUMN + ", "
			+ NOTI_ID_COLUMN + ") Values (1, 1, 1)";
 
	// 資料庫物件	
	private SQLiteDatabase db;
 
	// 建構子，一般的應用都不需要修改
	public DB_Table(Context context) {
		db = DB_DBHelper.getDatabase(context);
	}
 
	// 關閉資料庫，一般的應用都不需要修改
	public void close() {
		db.close();
	}
	
	public void resetTable(){
		// 刪除原有的表格
		db.execSQL("DROP TABLE IF EXISTS "+DB_Table.TABLE_NAME_SETTING);
		db.execSQL("DROP TABLE IF EXISTS "+DB_Table.TABLE_NAME_USER);
		db.execSQL("DROP TABLE IF EXISTS "+DB_Table.TABLE_NAME_MESSAGE);
		db.execSQL("DROP TABLE IF EXISTS "+DB_Table.TABLE_NAME_REPLY);
		
		// 建立應用程式需要的表格
		db.execSQL(DB_Table.CREATE_TABLE_SETTING);
		db.execSQL(DB_Table.CREATE_TABLE_USER);
		db.execSQL(DB_Table.CREATE_TABLE_MESSAGE);
		db.execSQL(DB_Table.CREATE_TABLE_REPLY);
		db.execSQL(DB_Table.INIT_TABLE_SETTING);
	}
	
	/**
	 * 回傳目前的設定有沒有開啟追蹤功能
	 * @return
	 */
	public boolean isEnable(){
		// query database
		String where = SETTING_KEY_COLOMN + " = " + 1;
		Cursor cursor = db.query(TABLE_NAME_SETTING, new String[]{ENABLE_COLUMN}, where, null, null, null, null, null);
		cursor.moveToFirst();
		boolean result = cursor.getInt(0) == 1;
		cursor.close();
		
		return result;
	}
	
	/**
	 * 設定要不要開起追蹤功能
	 * @param enable
	 * @return
	 */
	public boolean setEnable(boolean enable){
		// 建立準備修改資料的ContentValues物件
		ContentValues cv = new ContentValues();
 
		// 加入ContentValues物件包裝的修改資料
		// 第一個參數是欄位名稱， 第二個參數是欄位的資料
		cv.put(ENABLE_COLUMN, (enable)?1:0);
 
		// 設定修改資料的條件為編號
		// 格式為「欄位名稱＝資料」
		String where = SETTING_KEY_COLOMN + " = " + 1;
 
		// 執行修改資料並回傳修改的資料數量是否成功
		return db.update(TABLE_NAME_SETTING, cv, where, null) > 0;
	}
	
	/**
	 * 取得 notification id 並加 1
	 * @return
	 */
	public int getNotiId(){
		// query database
		String where = SETTING_KEY_COLOMN + " = " + 1;
		Cursor cursor = db.query(TABLE_NAME_SETTING, new String[]{NOTI_ID_COLUMN}, where, null, null, null, null, null);
		cursor.moveToFirst();
		int id = cursor.getInt(0);
		cursor.close();
		
		// id++
		// 建立準備修改資料的ContentValues物件
		ContentValues cv = new ContentValues();
		// 加入ContentValues物件包裝的修改資料
		// 第一個參數是欄位名稱， 第二個參數是欄位的資料
		cv.put(NOTI_ID_COLUMN, id+1);
		// 設定修改資料的條件為編號
		// 格式為「欄位名稱＝資料」
		String update_where = SETTING_KEY_COLOMN + " = " + 1;
		// 執行修改資料並回傳修改的資料數量是否成功
		db.update(TABLE_NAME_SETTING, cv, update_where, null);
		
		return id;
	}
	
	/**
	 * 新增使用者到 database
	 * @param account 
	 * @param nickname
	 * @param image
	 */
	public void addUser(String account, String nickname, String image){
		// 建立準備新增資料的ContentValues物件
		ContentValues cv = new ContentValues();	 
 
		// 加入ContentValues物件包裝的新增資料
		// 第一個參數是欄位名稱， 第二個參數是欄位的資料
		cv.put(ACCOUNT_COLUMN, account);
		cv.put(NICK_NAME_COLUMN, nickname);
		cv.put(IMAGE_COLUMN, image);
		cv.put(ON_NEW_MSG_COLUMN, 1);
		cv.put(ON_REPLY_MSG_COLUMN, 1);
 
		// 新增一筆資料並取得編號
		// 第一個參數是表格名稱
		// 第二個參數是沒有指定欄位值的預設值
		// 第三個參數是包裝新增資料的ContentValues物件
		db.insert(TABLE_NAME_USER, null, cv);
	}
	
	/**
	 * 這個使用者有沒有在 database 裡面
	 * @param account
	 * @return
	 */
	public boolean isUserNew(String account){
		// query database
		String where = ACCOUNT_COLUMN + " = \'" + account +"\'";
		Cursor cursor = db.query(TABLE_NAME_USER, null, where, null, null, null, null, null);
		boolean result = cursor.getCount() == 0;
		cursor.close();
		
		return result;
	}
	
	/**
	 * 輸入帳號回傳匿名
	 * @param account
	 * @return
	 */
	public String getUserNickname(String account){
		// query database
		String where = ACCOUNT_COLUMN + " = \'" + account +"\'";
		Cursor cursor = db.query(TABLE_NAME_USER, new String[]{NICK_NAME_COLUMN}, where, null, null, null, null, null);
		if(cursor.moveToFirst() == true){
			String result = cursor.getString(0);
			cursor.close();
			return result;
		}
		else{
			cursor.close();
			return "";
		}
	}
	
	/**
	 * 更新 database 中使用者的資訊
	 * @param account
	 * @param nickname
	 * @param image
	 * @return 有沒有成功
	 */
	public boolean updateUser(String account, String nickname, String image){
		// 建立準備修改資料的ContentValues物件
		ContentValues cv = new ContentValues();
 
		// 加入ContentValues物件包裝的修改資料
		// 第一個參數是欄位名稱， 第二個參數是欄位的資料
		cv.put(NICK_NAME_COLUMN, nickname);
		cv.put(IMAGE_COLUMN, image);
 
		// 設定修改資料的條件為編號
		// 格式為「欄位名稱＝資料」
		String where = ACCOUNT_COLUMN + " = \'" + account + "\'";
 
		// 執行修改資料並回傳修改的資料數量是否成功
		return db.update(TABLE_NAME_USER, cv, where, null) > 0;
	}
	
	/**
	 * 設定當這個使用者發出新紙條的時候，要不要跳出通知
	 * @param account
	 * @param enable
	 * @return
	 */
	public boolean setUserOnNewMsg(String account, boolean enable){
		// 建立準備修改資料的ContentValues物件
		ContentValues cv = new ContentValues();
		
		// 加入ContentValues物件包裝的修改資料
		// 第一個參數是欄位名稱， 第二個參數是欄位的資料
		cv.put(ON_NEW_MSG_COLUMN, (enable)?1:0);
		
		// 設定修改資料的條件為編號
		// 格式為「欄位名稱＝資料」
		String where = ACCOUNT_COLUMN + " = \'" + account + "\'";
		
		// 執行修改資料並回傳修改的資料數量是否成功
		return db.update(TABLE_NAME_USER, cv, where, null) > 0;
	}
	
	/**
	 * 設定當有人回復這個人紙條的時候，要不要跳出通知
	 * @param account
	 * @param enable
	 * @return
	 */
	public boolean setUserOnReplyMsg(String account, boolean enable){
		// 建立準備修改資料的ContentValues物件
		ContentValues cv = new ContentValues();
		
		// 加入ContentValues物件包裝的修改資料
		// 第一個參數是欄位名稱， 第二個參數是欄位的資料
		cv.put(ON_REPLY_MSG_COLUMN, (enable)?1:0);
		
		// 設定修改資料的條件為編號
		// 格式為「欄位名稱＝資料」
		String where = ACCOUNT_COLUMN + " = \'" + account + "\'";
		
		// 執行修改資料並回傳修改的資料數量是否成功
		return db.update(TABLE_NAME_USER, cv, where, null) > 0;
	}
	
	/**
	 * 刪除使用者
	 * @param account
	 * @return 有沒有成功
	 */
	public boolean deleteUser(String account){
		// 刪除這個作者的所有紙條
		deleteUserMessage(account);
		
		// 設定條件為編號，格式為「欄位名稱=資料」
		String where = ACCOUNT_COLUMN + " = \'" + account + "\'";
		// 刪除指定編號資料並回傳刪除是否成功
		return db.delete(TABLE_NAME_USER, where , null) > 0;
	}
	
	/**
	 * 增加紙條的紀錄
	 * @param Msg_acc
	 * @param pid
	 * @param time
	 * @param content
	 * @param link
	 */
	public void addMessage(String Msg_acc, int pid, int time, String content, String link){
		// 建立準備新增資料的ContentValues物件
		ContentValues cv = new ContentValues();	 
	
		// 加入ContentValues物件包裝的新增資料
		// 第一個參數是欄位名稱， 第二個參數是欄位的資料
		cv.put(MSG_ACC_COLUMN, Msg_acc);
		cv.put(PID_COLUMN, pid);
		cv.put(TIME_COLUMN, time);
		cv.put(CONTENT_COLUMN, content);
		cv.put(LINK_COLUMN, link);
	
		// 新增一筆資料並取得編號
		// 第一個參數是表格名稱
		// 第二個參數是沒有指定欄位值的預設值
		// 第三個參數是包裝新增資料的ContentValues物件
		db.insert(TABLE_NAME_MESSAGE, null, cv);
	}
	
	/**
	 * 檢查資料庫中有沒有這張紙條
	 * @param pid
	 * @return 紙條存不存在
	 */
	public boolean isMsgNew(int pid){
		// query database
		String where = PID_COLUMN + " = " + pid;
		Cursor cursor = db.query(TABLE_NAME_MESSAGE, null, where, null, null, null, null, null);
		boolean result = cursor.getCount() == 0;
		cursor.close();
		
		return result;
	}

	/**
	 * 刪除這個使用者的所有紙條
	 * @param account
	 * @return 有沒有成功
	 */
	public boolean deleteUserMessage(String account){
		// 刪除這個紙條的所有回復
		Entity_Message[] allMsg = getAllMessages(account);
		for(int x=0;x<allMsg.length;++x){
			deleteMessageReply(allMsg[x].getPid());
		}
		
		// 設定條件為編號，格式為「欄位名稱=資料」
		String where = MSG_ACC_COLUMN + " = \'" + account + "\'";
		// 刪除指定編號資料並回傳刪除是否成功
		return db.delete(TABLE_NAME_MESSAGE, where , null) > 0;
	}
	
	/**
	 * 刪除一張紙條
	 * @param account
	 * @return 有沒有成功
	 */
	public boolean deleteMessage(int pid){
		// 刪除這張紙條的所有回復
		deleteMessageReply(pid);
		
		// 設定條件為編號，格式為「欄位名稱=資料」
		String where = PID_COLUMN + " = " + pid;
		// 刪除指定編號資料並回傳刪除是否成功
		return db.delete(TABLE_NAME_MESSAGE, where , null) > 0;
	}
	
	/**
	 * 增加某一張紙條的回覆
	 * @param Re_pid
	 * @param ReplierAcc
	 * @param ReTime
	 * @param ReContent
	 * @param ReplierImg
	 * @param ReplierName
	 */
	public void addReply(int Re_pid, String ReplierAcc, int ReTime, String ReContent, String ReplierImg, String ReplierName){
		// 建立準備新增資料的ContentValues物件
		ContentValues cv = new ContentValues();	 
 
		// 加入ContentValues物件包裝的新增資料
		// 第一個參數是欄位名稱， 第二個參數是欄位的資料
		cv.put(RE_PID_COLUMN, Re_pid);
		cv.put(REPLIER_ACC_COLUMN, ReplierAcc);
		cv.put(RE_TIME_COLUMN, ReTime);
		cv.put(RE_CONTENT_COLUMN, ReContent);
		cv.put(REPLIER_IMG_COLUMN, ReplierImg);
		cv.put(REPLIER_NAME_COLUMN, ReplierName);
 
		// 新增一筆資料並取得編號
		// 第一個參數是表格名稱
		// 第二個參數是沒有指定欄位值的預設值
		// 第三個參數是包裝新增資料的ContentValues物件
		db.insert(TABLE_NAME_REPLY, null, cv);
	}
	
	public boolean isReplyNew(int pid, String replierAcc, int replyTime){
		// query database
		String where = RE_PID_COLUMN + " = " + pid +
						" AND " + REPLIER_ACC_COLUMN + " = \'" + replierAcc + "\'" +
						" AND " + RE_TIME_COLUMN + " = " + replyTime;
		Cursor cursor = db.query(TABLE_NAME_REPLY, null, where, null, null, null, null, null);
		boolean result = cursor.getCount() == 0;
		cursor.close();
		
		return result;
	}
	
	/**
	 * 刪除一張紙條的所有回復
	 * @param pid
	 * @return 有沒有成功
	 */
	public boolean deleteMessageReply(int pid){
		// 設定條件為編號，格式為「欄位名稱=資料」
		String where = RE_PID_COLUMN + " = " + pid;
		// 刪除指定編號資料並回傳刪除是否成功
		return db.delete(TABLE_NAME_REPLY, where , null) > 0;
	}
	
	/**
	 * 刪除某一個回復
	 * @param pid
	 * @param replierAcc
	 * @param reTime
	 * @return
	 */
	public boolean deleteReply(int pid, String replierAcc, int reTime){
		// 設定條件為編號，格式為「欄位名稱=資料」
		String where = RE_PID_COLUMN + "=" + pid +
				" AND " + REPLIER_ACC_COLUMN + " = \'" + replierAcc + "\'" +
				" AND " + RE_TIME_COLUMN + " = " + reTime;
		// 刪除指定編號資料並回傳刪除是否成功
		return db.delete(TABLE_NAME_REPLY, where , null) > 0;
	}
	
	/**
	 * 取得 database 中所有使用者
	 * @return
	 */
	public Entity_User[] getAllUsers(){
		Entity_User[] allUsers;
		
		// query database
		Cursor cursor = db.query(TABLE_NAME_USER, null, null, null, null, null, null, null);
		allUsers = new Entity_User[cursor.getCount()];
		
		// decode cursor
		int count = 0;
		while (cursor.moveToNext()) {
			allUsers[count] = creatEntity_User(cursor);
			allUsers[count].setAllMessage(getAllMessages(allUsers[count].getAccount()));
			count++;
		}
		cursor.close();
		
		return allUsers;
	}
	
	/**
	 * 取得 database 中這個使用者的所有紙條
	 * @return
	 */
	public Entity_Message[] getAllMessages(String account){
		Entity_Message[] allMessages;
		
		// query database
		String where = MSG_ACC_COLUMN + " = \'" + account + "\'";
		Cursor cursor = db.query(TABLE_NAME_MESSAGE, null, where, null, null, null, null, null);
		allMessages = new Entity_Message[cursor.getCount()];
		
		// decode cursor
		int count = 0;
		while (cursor.moveToNext()) {
			allMessages[count] = creatEntity_Message(cursor);
			allMessages[count].setAllReplies(getAllReplies(allMessages[count].getPid()));
			count++;
		}
		cursor.close();
		
		return allMessages;
	}
	
	/**
	 * 取得 database 中這個使用者的所有紙條
	 * @param pid
	 * @return
	 */
	public Entity_Reply[] getAllReplies(int pid){
		Entity_Reply[] allReplies;
		
		// query database
		String where = RE_PID_COLUMN + " = " + pid;
		Cursor cursor = db.query(TABLE_NAME_REPLY, null, where, null, null, null, null, null);
		allReplies = new Entity_Reply[cursor.getCount()];
		
		// decode cursor
		int count = 0;
		while (cursor.moveToNext()) {
			allReplies[count++] = creatEntity_Reply(cursor);
		}
		cursor.close();
		
		return allReplies;
	}
	
	
	/**
	 * 從 query 回來的資料中，創造出 user 的 object
	 * @param cursor
	 * @return
	 */
	private Entity_User creatEntity_User(Cursor cursor){
		String account = cursor.getString(0);
		String image = cursor.getString(1);
		String nickname = cursor.getString(2);
		boolean OnNewMsg = cursor.getInt(3)==1;
		boolean OnReplyMsg = cursor.getInt(4)==1;
		
		Entity_User user = new Entity_User(account, image, nickname, OnNewMsg, OnReplyMsg);
		return user;
	}
	
	/**
	 * 從 query 回來的資料中，創造出 message 的 object
	 * @param cursor
	 * @return
	 */
	private Entity_Message creatEntity_Message(Cursor cursor){
		String Msg_acc = cursor.getString(0);
		int pid = cursor.getInt(1);
		int time = cursor.getInt(2);
		String content = cursor.getString(3);
		String link = cursor.getString(4);
		
		Entity_Message message = new Entity_Message(Msg_acc, pid, time, content, link);
		return message;
	}
	
	/**
	 * 從 query 回來的資料中，創造出 reply 的 object
	 * @param cursor
	 * @return
	 */
	private Entity_Reply creatEntity_Reply(Cursor cursor){
		int Re_pid = cursor.getInt(1);
		String replierAcc = cursor.getString(2);
		int reTime = cursor.getInt(3);
		String reContent = cursor.getString(4);
		String replierImg = cursor.getString(5);
		String replierName = cursor.getString(6);
		
		Entity_Reply reply = new Entity_Reply(Re_pid, replierAcc, reTime, reContent, replierImg, replierName);
		return reply;
	}
}
