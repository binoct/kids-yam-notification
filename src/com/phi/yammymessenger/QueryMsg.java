package com.phi.yammymessenger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class QueryMsg extends BroadcastReceiver{

	@Override
	public void onReceive(final Context context, Intent intent){
		
		//Window window = MainActivity.activity.getWindow();
		//window.addFlags(WindowManager.LayoutParams.FL);
		//window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		
		// 休眠時開啟CPU
		//WakeLocker.acquire(context);
		PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "wakeup");
		
		try{
			// 休眠時開啟CPU
			wl.acquire();
			
			final DB_Table table = new DB_Table(context);
			
			Bundle data = intent.getExtras();
			
			// 爬一次網站的資料
			if (data != null && data.get("msg") != null && data.get("msg").equals("query_message")) {
				// 確認有開啟
				if (table.isEnable()){
					Thread thread = new Thread(){
						public void run() {
							try {
								queryAll(context);
							}
							catch (IOException e) {
								Log.e("phi",getErrorMsg(e));
							}
							catch (JSONException e) {
								Log.e("phi",getErrorMsg(e));
							}
						}
					};
					thread.start();
				}
			}
			// 註冊這個使用者
			else if(data != null && data.get("msg") != null && data.get("msg").equals("register")){
				final String account = data.get("account").toString();
				
				Thread thread = new Thread(){
					public void run(){
						boolean result = false;
						try {
							result = registerUser(context, account);
							MainActivity.updateView();		//刷新畫面
						}
						catch (IOException e) {
							Log.e("phi",getErrorMsg(e));
						}
						catch (JSONException e) {
							Log.e("phi",getErrorMsg(e));
						}
						
						if(result == true){
							String nickname = table.getUserNickname(account);
							MainActivity.alert("成功", "開始追蹤 "+nickname, context);
						}
						else{
							MainActivity.alert("失敗", "追蹤失敗", context);
						}
					}
				};
				thread.start();
			}
			
			// on turn on cell phone
			// reference: http://stackoverflow.com/questions/10420358/android-periodic-background-service-advice
			else if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
				// Register your reporting alarms here. 
				Calendar cal = Calendar.getInstance();
			 
				Intent new_intent = new Intent(context, QueryMsg.class);
				new_intent.putExtra("msg", "query_message");
				//sendBroadcast(intent);
			 
				PendingIntent pi = PendingIntent.getBroadcast(context, 0, new_intent, PendingIntent.FLAG_UPDATE_CURRENT);
					 
				AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
				//am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
				am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), MainActivity.queryPeriod, pi);
			}
		}
		catch(Exception e){
			Log.e("phi",getErrorMsg(e));
		}
		finally{
			// 繼續休眠
			wl.release();
		}
	}
	
	private boolean registerUser(Context context,String account) throws IOException, JSONException{
		try{
			DB_Table table = new DB_Table(context);
			
			// 這個人已經在資料庫裡面了
			if(table.isUserNew(account) == false){
				return false;
			}
			
			// 從網站上抓這個使用者最近的 1 張紙條
			JSONArray arr = readJsonArrFromUrl("http://myyamie.kids.yam.com/my/slim/plurk/"+account+"/mood/0/1");
			JSONObject authorObj = arr.getJSONObject(0).getJSONObject("author");
			Entity_User author = createUser(authorObj);
			queryMessage(context, table, account, false, false, false, true);
			
			table.addUser(account, author.getNickName(), author.getImage());
			
			return true;
		}
		finally{
			
		}
	}
	
	/**
	 * 爬一次網頁
	 * @param context 
	 */
	private void queryAll(Context context) throws IOException, JSONException{
		
		try{
			DB_Table table = new DB_Table(context);
			Entity_User[] allUser = table.getAllUsers();
			
			for(int x=0;x<allUser.length;++x){
				String account = allUser[x].getAccount();
				// 抓取使用者的紙條
				queryMessage(context, table, account, allUser[x].getOnNewMsg(), allUser[x].getOnReplyMsg(), true, false);
			}
		}
		finally{
			
		}
	}
	
	/**
	 * 從小番上抓取這個使用者的紙條資料
	 * @param context
	 * @param table
	 * @param account 帳號
	 * @param printNotiOnNewMsg 有新紙條時要不要送出通知
	 * @param printNotiOnReply 有新留言時要不要印出通知
	 * @param updateUser 要不要更新資料庫 user 的資料
	 * @param queryAllReply 要不要挖所有的留言
	 * @return
	 */
	private Entity_Message[] queryMessage(Context context, DB_Table table,String account, boolean printNotiOnNewMsg, boolean printNotiOnReply, boolean updateUser, boolean queryAllReply) throws IOException, JSONException{
		try{
			JSONArray arr = readJsonArrFromUrl("http://myyamie.kids.yam.com/my/slim/plurk/"+account+"/mood/0/10");		//從網站上抓這個使用者最近的 10 張紙條
			Entity_User tempuser = null;		//儲存這個使用者資訊
			Entity_Message[] allMessages = new Entity_Message[arr.length()];
			
			// 是不是最新的幾張紙條，不要因為新的紙條刪除而抓到舊的
			boolean isFirst = true;
			
			for(int y=0;y<arr.length();++y){
				JSONObject obj = arr.getJSONObject(y);
				
				int pid = obj.getInt("pid");
				int time = obj.getInt("posttime");
				String content = obj.getString("content");
				content = content.replace("<br />", "");
				
				String link = "";
				if(obj.has("plugin")){
					link = obj.getString("plugin");
				}
				
				// 如果是最新的紙條，順便讀取作者資訊，並更新作者資訊
				if(y==0){
					JSONObject author = obj.getJSONObject("author");
					tempuser = createUser(author);
					if(updateUser == true){
						table.updateUser(tempuser.getAccount(), tempuser.getNickName(), tempuser.getImage());
					}
				}
				
				// 如果是新的 印出通知
				if(isFirst == true && printNotiOnNewMsg == true && table.isMsgNew(pid) == true){
					sendNotification(context, tempuser.getNickName(), content, tempuser.getImage(), "http://myyamie.kids.yam.com/my/plurk.php?#plurkone/"+pid);
				}
				else{
					if( table.isMsgNew(pid) == false){
						isFirst = false;
					}
				}
				
				allMessages[y] = new Entity_Message(account, pid, time, content, link);
				//Log.i("phi", "pid: "+pid+"\ntime: "+time+"\ncontent: "+content+"\nlink: "+link);
				//Log.i("phi", "=========================");
				
				if(queryAllReply == true){
					allMessages[y].setAllReplies(queryReply(context, tempuser.getNickName(), pid, table, printNotiOnReply));
				}
				else if(obj.has("reply")){
					JSONArray replyArray = obj.getJSONObject("reply").getJSONArray("list");
					analysisReplyArray(replyArray, table, context, tempuser.getNickName(), pid, printNotiOnReply);
				}
				
				// 如果 database 沒有，存入 database
				if(table.isMsgNew(pid) == true){
					table.addMessage(account, pid, time, content, "");
				}
			}
			return allMessages;
		}
		finally{
			
		}
	}
	
	/**
	 * 取得單張紙條的回覆
	 * @param context
	 * @param authorNickname
	 * @param pid
	 * @param table
	 * @param sentNoti
	 * @return
	 */
	private Entity_Reply[] queryReply(Context context, String authorNickname, int pid, DB_Table table, boolean sentNoti) throws IOException, JSONException{
		try{
			// 取得這張紙條的所有回復
			JSONObject object = readJsonObjFromUrl("http://myyamie.kids.yam.com/my/slim/reply/"+pid+"/0/1");
			// 取得回復的數量
			int replyCount = object.getInt("total");
			
			// 如果有人回復
			if(replyCount > 0){
				JSONObject allReplyObject = readJsonObjFromUrl("http://myyamie.kids.yam.com/my/slim/reply/"+pid+"/0/"+replyCount);
				JSONArray replyArray = allReplyObject.getJSONArray("list");
				
				return analysisReplyArray(replyArray, table, context, authorNickname, pid, sentNoti);
			}
			return new Entity_Reply[0];
		}
		finally{
			
		}
	}
	
	private Entity_Reply[] analysisReplyArray(JSONArray replyArray, DB_Table table, Context context, String authorNickname, int pid, boolean sentNoti)  throws IOException, JSONException {
		try{
			Entity_Reply[] returnReplies = new Entity_Reply[replyArray.length()];
			
			// 取得每一個回復
			for(int x=0;x<replyArray.length();++x){
				JSONObject singleReply = replyArray.getJSONObject(x);
				String replierAcc = singleReply.getString("yid");
				int reTime = singleReply.getInt("posttime");
				String reContent = singleReply.getString("content");
				String replierImage = singleReply.getString("photo");
				String replierName = singleReply.getString("nickname");
				
				//Log.i("phi", "pid: "+pid+"\nre time: "+reTime+"\nre content: "+reContent+"\nreplierName: "+replierName);
				
				// 判斷回復是不是新的，是新的印出通知
				if(sentNoti == true && table.isReplyNew(pid, replierAcc, reTime) == true){
					sendNotification(context, replierName+"@"+authorNickname, reContent, replierImage, "http://myyamie.kids.yam.com/my/plurk.php?#plurkone/"+pid);
				}
				
				// 如果 database 沒有，存入 database
				if(table.isReplyNew(pid, replierAcc, reTime) == true){
					table.addReply(pid, replierAcc, reTime, reContent, "", replierName);
				}
				
				returnReplies[x] = new Entity_Reply(pid, replierAcc, reTime, reContent, replierImage, replierName);
			}
			return returnReplies;
		}
		finally{
			
		}
	}
	
	/**
	 * 從 json object 解碼出使用者資訊
	 * @param obj 
	 * @return
	 */
	private Entity_User createUser(JSONObject obj){
		try {
			String account = obj.getString("yid");
			String image = obj.getString("photo");
			String nickname = obj.getString("nickname");
			return new Entity_User(account, image, nickname, true, true);
		} catch (JSONException e) {
			Log.e("phi", e.getMessage());
		}
		return null;
	}
	
	/**
	 * 送出通知
	 * @param context
	 * @param title
	 * @param content
	 * @param imageURL
	 * @param link
	 */
	private static void sendNotification(Context context, String title, String content, String imageURL, String link){
		DB_Table table = new DB_Table(context);
		Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		
		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(context)
				.setSmallIcon(R.drawable.ic_launcher)
				.setLargeIcon(getBitmapFromURL(imageURL))
				.setSound(alarmSound)
				.setContentTitle(title)
				.setContentText(content);
		
		// Gets an instance of the NotificationManager service
		NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE );
		
		// pending implicit intent to view url
	    Intent resultIntent = new Intent(Intent.ACTION_VIEW);
	    resultIntent.setData(Uri.parse(link));

	    PendingIntent pending = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	    mBuilder.setContentIntent(pending);
		
		// Builds the notification and issues it.
		int notificationId = table.getNotiId();
		mNotifyMgr.notify(notificationId, mBuilder.build());
	}
	
	/**
	 * read all
	 * @param rd
	 * @return
	 * @throws IOException
	 */
	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}
	
	/**
	 * 從網站上抓 json object
	 * @param url
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 */
	private static JSONObject readJsonObjFromUrl(String url) throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONObject json = new JSONObject(jsonText);
			return json;
		} finally {
			is.close();
		}
	}
	
	private static JSONArray readJsonArrFromUrl(String url) throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONArray json = new JSONArray(jsonText);
			return json;
		} finally {
			is.close();
		}
	}
	
	/**
	 * 從網站上抓圖片
	 * http://stackoverflow.com/questions/16007401/android-use-external-profile-image-in-notification-bar-like-facebook
	 * @param strURL
	 * @return
	 */
	public static Bitmap getBitmapFromURL(String strURL) {
		try {
			URL url = new URL(strURL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);
			return myBitmap;
		} catch (IOException e) {
			Log.e("phi",e.getMessage());
			return null;
		}
	}
	
	/**
	 * 取得錯誤訊息的 String
	 * @return
	 */
	private String getErrorMsg(Exception e){
		StringWriter errors = new StringWriter();
		e.printStackTrace(new PrintWriter(errors));
		return errors.toString();
	}
}
