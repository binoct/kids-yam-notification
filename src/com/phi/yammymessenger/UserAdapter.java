package com.phi.yammymessenger;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

public class UserAdapter extends ArrayAdapter<Entity_User>{
	
	//畫面資源編號
	private int resource;
	//包裝的使用者資料
	private List<Entity_User> users;
	
	public UserAdapter(Context context, int resource, List<Entity_User> users){
		super(context, resource, users);
		this.resource = resource;
		this.users = users;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent){
		LinearLayout itemView;
		
		DB_Table table = new DB_Table(getContext());
		final Entity_User[] users = table.getAllUsers();

		if (convertView == null) {
			// 建立項目畫面元件
			itemView = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater li = (LayoutInflater)getContext().getSystemService(inflater);
			li.inflate(resource, itemView, true);
		}
		else {
			itemView = (LinearLayout) convertView;
		}

		// 讀取記事顏色、已選擇、標題與日期時間元件
		TextView titleView = (TextView) itemView.findViewById(R.id.title_text);
		TextView onNewTextView = (TextView) itemView.findViewById(R.id.text_onNew);
		TextView onReplyTextView = (TextView) itemView.findViewById(R.id.text_onReply);
		final ImageView selectedItem = (ImageView) itemView.findViewById(R.id.selected_item);
		Switch onNewSwitch = (Switch) itemView.findViewById(R.id.switch_onNew);
		Switch onReplySwitch = (Switch) itemView.findViewById(R.id.switch_onReply);
		Button deleteButton = (Button) itemView.findViewById(R.id.button_delete_user);

		// 設定內容
		titleView.setText(users[position].getNickName());
		onNewTextView.setText("新紙條");
		onReplyTextView.setText("新留言");
		onNewSwitch.setChecked(users[position].getOnNewMsg());
		onReplySwitch.setChecked(users[position].getOnReplyMsg());
		
		//紀錄帳號
		final String account = users[position].getAccount();
		
		//拜訪使用者
		itemView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity.confirmVisit(account);
			}
		});
		
		//刪除使用者
		deleteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 跳出確認視窗
				MainActivity.confirmDeleteUser(account);
			}
		});
		
		//設定開關動作
		onNewSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				DB_Table table = new DB_Table(getContext());
				table.setUserOnNewMsg(account, isChecked);
			}
		});
		
		//設定開關動作
		onReplySwitch.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				DB_Table table = new DB_Table(getContext());
				table.setUserOnReplyMsg(account, isChecked);
			}
		});

		// 設定頭像
		Thread thread = new Thread(){
			public void run(){
				Bitmap bitmap = QueryMsg.getBitmapFromURL(users[position].getImage());
				MainActivity.setBitmap(selectedItem, bitmap);
			}
		};
		thread.start();

		return itemView;
	}
	
	// 設定指定編號的記事資料
	public void set(int index, Entity_User user) {
		if (index >= 0 && index < users.size()) {
			users.set(index, user);
			notifyDataSetChanged();
		}
	}
	
	// 讀取指定編號的記事資料
	public Entity_User get(int index) {
		return users.get(index);
	}
}
