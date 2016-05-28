package com.phi.yammymessenger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;


public class MainActivity extends Activity {
	
	private DB_Table table;
	private static Activity activity;
	public static final int queryPeriod = 180000;
	
	private static UserAdapter userAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		activity = this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		table = new DB_Table(getApplicationContext());
		
		if(isAlarmRunning() == false){
			setAlarm();
		}
		
		//設定上面的總開關
		TextView enaleTextView = (TextView) findViewById(R.id.text_enable);
		Switch enableSwitch = (Switch) findViewById(R.id.switch_enable);
		enaleTextView.setText("追蹤功能");
		enableSwitch.setChecked(table.isEnable());
		
		//設定開關動作
		enableSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				table.setEnable(isChecked);
			}
		});
		
		updateView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// http://stackoverflow.com/questions/4134117/edittext-on-a-popup-window
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_register) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);

			alert.setTitle("新增");
			alert.setMessage("輸入帳號");

			// Set an EditText view to get user input 
			final EditText input = new EditText(this);
			alert.setView(input);
			
			//註冊使用者
			alert.setPositiveButton("確認", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					Intent intent = new Intent(activity, QueryMsg.class);
					Bundle extras = new Bundle();
					extras.putString("msg", "register");
					extras.putString("account",input.getText().toString());
					intent.putExtras(extras);
					sendBroadcast(intent);
				}
			});
			
			alert.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
				}
			});

			alert.show();
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * 取得 alarmmanager 是不是在執行中
	 * @return
	 */
	private boolean isAlarmRunning(){
		return (PendingIntent.getBroadcast(this, 0, new Intent(this, QueryMsg.class), PendingIntent.FLAG_NO_CREATE) != null);
	}
	
	/**
	 * 設定 alarmmanager
	 * http://oldgrayduck.blogspot.tw/2012/10/androidalarmmanager.html
	 */
	private void setAlarm(){
		Calendar cal = Calendar.getInstance();
		// 設定於 10 秒後執行
		//cal.add(Calendar.SECOND, 10);
		
		Intent intent = new Intent(this, QueryMsg.class);
		intent.putExtra("msg", "query_message");
		//sendBroadcast(intent);
		
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		//am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
		am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), queryPeriod, pi);
	}
	
	/**
	 * 跳出 alert 視窗
	 * @param title
	 * @param content
	 * @param context
	 */
	public static void alert(final String title, final String content, final Context context) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				Builder MyAlertDialog = new AlertDialog.Builder(activity);
				MyAlertDialog.setTitle(title);
				MyAlertDialog.setMessage(content);
				DialogInterface.OnClickListener OkClick = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				};
				MyAlertDialog.setPositiveButton("OK", OkClick);
				MyAlertDialog.show();
			}
		});
	}
	
	/**
	 * 刷新頁面
	 */
	public static void updateView(){
		activity.runOnUiThread(new Runnable() {
			public void run() {
				DB_Table table = new DB_Table(activity);
				Entity_User[] usersArr = table.getAllUsers();

				ArrayList<Entity_User> users = new ArrayList<Entity_User>(Arrays.asList(usersArr));
				
				// 建立自定Adapter物件
				userAdapter = new UserAdapter(activity, R.layout.singleitem, users);
				ListView item_list = (ListView) activity.findViewById(R.id.user_list);
				item_list.setAdapter(userAdapter);
			}
		});
	}
	
	/**
	 * 設定頭像
	 * @param item
	 * @param bitmap
	 */
	public static void setBitmap(final ImageView item, final Bitmap bitmap){
		activity.runOnUiThread(new Runnable() {
			public void run() {
				item.setImageBitmap(bitmap);
			}
		});
	}
	
	/**
	 * 確認取消追蹤
	 * @param account
	 */
	public static void confirmDeleteUser(final String account){
		activity.runOnUiThread(new Runnable() {
			public void run() {
				final DB_Table table = new DB_Table(activity);
				
				Builder MyAlertDialog = new AlertDialog.Builder(activity);
				MyAlertDialog.setTitle("取消追蹤");
				MyAlertDialog.setMessage("確定取消追蹤 "+table.getUserNickname(account)+" ?");
				
				//按下確定
				DialogInterface.OnClickListener OkClick = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						table.deleteUser(account);
						updateView();
					}
				};
				
				//按下取消
				DialogInterface.OnClickListener CancleClick = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				};
				
				MyAlertDialog.setPositiveButton("確定", OkClick);
				MyAlertDialog.setNegativeButton("取消", CancleClick);
				MyAlertDialog.show();
			}
		});
	}
	
	/**
	 * 確定去拜訪某人的家
	 * @param account
	 */
	public static void confirmVisit(final String account){
		activity.runOnUiThread(new Runnable() {
			public void run() {
				final DB_Table table = new DB_Table(activity);
				
				Builder MyAlertDialog = new AlertDialog.Builder(activity);
				MyAlertDialog.setTitle("拜訪");
				MyAlertDialog.setMessage("確定拜訪 "+table.getUserNickname(account)+" ?");
				
				//按下確定
				DialogInterface.OnClickListener OkClick = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent ie = new Intent(Intent.ACTION_VIEW, Uri.parse("http://myyamie.kids.yam.com/my/plurk.php?visit_yamie="+account+"&act=mood"));
						activity.startActivity(ie);
					}
				};
				
				//按下取消
				DialogInterface.OnClickListener CancleClick = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				};
				
				MyAlertDialog.setPositiveButton("確定", OkClick);
				MyAlertDialog.setNegativeButton("取消", CancleClick);
				MyAlertDialog.show();
			}
		});
	}
}
