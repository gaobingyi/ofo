package com.gby.ofo;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.R.id;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private Button butEquery;
	private Button butSubmit;
	private EditText edtId;
	private EditText edtSubmitId;
	private EditText edtSubmitPassword;
	
	private String equeryResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		butEquery = (Button) findViewById(R.id.button_equery);
		butSubmit = (Button) findViewById(R.id.button_submit);
		
		butEquery.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showQueryDialog();
			}
		});
		butSubmit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showSubmitDialog();
			}
		});
	}
	
	@SuppressLint("InflateParams")
	private void showQueryDialog() {
		AlertDialog.Builder equeryDialog = new AlertDialog.Builder(MainActivity.this);
		final View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.query_dialog, null);
		equeryDialog.setView(view);	
		equeryDialog.create();
		Button butEqueryDialog = (Button) view.findViewById(R.id.button_equery_dialog);
		butEqueryDialog.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (isNetworkConnected(MainActivity.this)) {//判断网络是否连接
					edtId = (EditText) view.findViewById(R.id.edittext_equery_id);
					String id = edtId.getText().toString();
					if (id.isEmpty()) {//判断输入是否为空
		        		Toast.makeText(MainActivity.this, "请填写车牌号！", 
								Toast.LENGTH_SHORT).show();	
		        	} else if ((id.length() != 6) || (!isNumeric(id))) {
		        		Toast.makeText(MainActivity.this, "车牌号为六位数字！", 
								Toast.LENGTH_SHORT).show();
		        	} else {
		        		equery(id);
		        	}
					
				} else {
					Toast.makeText(MainActivity.this, "当前网络未连接！", 
							Toast.LENGTH_SHORT).show();
				}
 			}
		});
		equeryDialog.show();
	}
	
	@SuppressLint("InflateParams")
	private void showSubmitDialog() {
		@SuppressWarnings("deprecation")
		AlertDialog.Builder submitDialog = new AlertDialog.Builder(MainActivity.this, AlertDialog.THEME_HOLO_DARK);
		final View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.submit_dialog, null);
		submitDialog.setView(view);		
		submitDialog.setTitle("提交车牌和密码");		
		submitDialog.setPositiveButton("提交", new DialogInterface.OnClickListener() {
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	        	if (isNetworkConnected(MainActivity.this)) {
	        		edtSubmitId = (EditText) view.findViewById(R.id.edittext_submit_id);
		        	edtSubmitPassword = (EditText) view.findViewById(R.id.edittext_submit_password);
		        	String id = edtSubmitId.getText().toString();
		        	String password = edtSubmitPassword.getText().toString();
		        	if ((id.equals("")) || (password.equals(""))) {
		        		Toast.makeText(MainActivity.this, "请填写车牌号和密码！", 
								Toast.LENGTH_SHORT).show();	
		        	} else if ((id.length() != 6) || (!isNumeric(id))) {
		        		Toast.makeText(MainActivity.this, "车牌号为六位数字！", 
								Toast.LENGTH_SHORT).show();
		        	} else if (password.length() !=4 || (!isNumeric(id))) {
		        		Toast.makeText(MainActivity.this, "密码为四位数字！", 
								Toast.LENGTH_SHORT).show();
		        	} else {
		        		submit(id, password);
		        	}
				} else {
					Toast.makeText(MainActivity.this, "当前网络未连接！", 
							Toast.LENGTH_SHORT).show();
				}
	        }
	    });
		/*submitDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
	    	@Override
	    	public void onClick(DialogInterface dialog, int which) {
	    	}
	    });*/
		submitDialog.show();		
	}
	

	private void equery(final String id) {
		new Thread(new Runnable() {		
			@Override
			public void run() {
		        HttpURLConnection conn = null;
				try {
					//URL url = new URL("http://free1.neiwangtong.com:8888/ServerServlet/login");
					URL url = new URL("http://free1.neiwangtong.com:8888/Bike/BikeServlet");
					Log.d("gaobingyi", "1");
					conn = (HttpURLConnection) url.openConnection();
					Log.d("gaobingyi", "2");
					conn.setRequestMethod("POST");
					conn.setConnectTimeout(8000);
					conn.setReadTimeout(8000);
					DataOutputStream out = new DataOutputStream(conn.getOutputStream());
					out.writeBytes("id=" + id);
					Log.d("gaobingyi", "2.1");
					InputStream in = conn.getInputStream();
					Log.d("gaobingyi", "3");
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					Log.d("gaobingyi", "4");
					StringBuilder responseMes = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null) {
						Log.d("gaobingyi", "5");
						responseMes.append(line);	
					}
					equeryResult = responseMes.toString();
					Message message = new Message();
		            if ("fail".equals(equeryResult)) {
		            	message.what = 0;
		                handler.sendMessage(message);
		            } else {
		            	message.what = 1;
		                handler.sendMessage(message);
		            }
					
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (conn != null) {
						conn.disconnect();	 
					}					
				}
			}
		}).start();
	}
	
	private void submit(final String id, final String password) {
		new Thread(new Runnable() {		
			@Override
			public void run() {
		        HttpURLConnection conn = null;
				try {
					Log.d("gaobingyi", "1");
					URL url = new URL("http://free1.neiwangtong.com:8888/Bike/BikeServlet");
					conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("POST");
					conn.setConnectTimeout(8000);
					conn.setReadTimeout(8000);
					Log.d("gaobingyi", "2");
					DataOutputStream out = new DataOutputStream(conn.getOutputStream());
					out.writeBytes("id=" + id + "&password=" + password);
					Log.d("gaobingyi", "3");
					InputStream in = conn.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					StringBuilder responseMes = new StringBuilder();
					Log.d("gaobingyi", "4");
					String line;
					while ((line = reader.readLine()) != null) {
						responseMes.append(line);	
					}
					equeryResult = responseMes.toString();
					Log.d("gaobingyi", equeryResult);
					Message message = new Message();
		            if ("submit fail".equals(equeryResult)) {
		            	message.what = 2;
		                handler.sendMessage(message);
		            } else {
		            	message.what = 3;
		                handler.sendMessage(message);
		            }
					
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (conn != null) {
						conn.disconnect();	 
					}					
				}
			}
		}).start();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case 0://查询失败
                    edtId.setText("");
                    Toast.makeText(MainActivity.this, "查询失败！", 
							Toast.LENGTH_SHORT).show();
                    break;
                case 1://查询成功
                	edtId.setText(equeryResult);
                	Toast.makeText(MainActivity.this, "查询成功！", 
							Toast.LENGTH_SHORT).show();
                    break;
                case 2://提交失败
                    Toast.makeText(MainActivity.this, "提交失败！", 
							Toast.LENGTH_SHORT).show();
                    break;
                case 3://提交成功
                    Toast.makeText(MainActivity.this, "提交成功！", 
							Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    
    private boolean isNetworkConnected(Context context) {    
        if (context != null) {    
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context    
                    .getSystemService(Context.CONNECTIVITY_SERVICE);    
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();    
            if (mNetworkInfo != null) {    
                return mNetworkInfo.isAvailable();    
            }    
        }    
        return false;   
    }  
    
    private boolean isNumeric(String str){ 
    	   Pattern pattern = Pattern.compile("[0-9]*"); 
    	   Matcher isNum = pattern.matcher(str);
    	   if( !isNum.matches() ){
    	       return false; 
    	   } 
    	   return true; 
    	}
}
