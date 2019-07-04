package com.meibanlu.driver.broadcast;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.meibanlu.driver.R;
import com.meibanlu.driver.tool.SharePreData;

import java.io.File;



public class DownloadReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
	      if(DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())){
	    	  System.out.println("RECIVE DOWN LOAD FINISH");
			  long id=intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,-1 );
			  long downloadId= SharePreData.getInstance().getLong(R.string.download);
			  if(id==downloadId){
                  installApk( context,id );
			  }
	      }
	}
    private void installApk(Context context, Long id){
		DownloadManager manager= (DownloadManager) context.getSystemService( Context.DOWNLOAD_SERVICE );
		Uri uri=manager.getUriForDownloadedFile( id );
		if (uri != null) {
			Log.d("DownloadManager", uri.toString());
			Intent install = new Intent(Intent.ACTION_VIEW);
			File file=new File(getApkPath(manager,id));
			if(Build.VERSION.SDK_INT>=24) { //判读版本是否在7.0以上
				//参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
				Uri apkUri = FileProvider.getUriForFile(context, "tywj.busDriver.provider", file);
				//添加这一句表示对目标应用临时授权该Uri所代表的文件
				install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
				install.setDataAndType(apkUri, "application/vnd.android.package-archive");
			}else{
				install.setDataAndType(Uri.fromFile(file),
						"application/vnd.android.package-archive");
			}
			install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(install);
			android.os.Process.killProcess( android.os.Process.myPid() );
		} else {
			Log.e("DownloadManager", "下载失败");
		}
	}
	private String getApkPath(DownloadManager dm,Long downId){
		Cursor c = dm.query(new DownloadManager.Query().setFilterById(downId));
		if(c != null){
			c.moveToFirst();
			int fileUriIdx = c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
			String fileUri = c.getString(fileUriIdx);
			String fileName = null;
			if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
				if (fileUri != null) {
					fileName = Uri.parse(fileUri).getPath();
				}
			} else {
				//Android 7.0以上的方式：请求获取写入权限，这一步报错
				//过时的方式：DownloadManager.COLUMN_LOCAL_FILENAME
				int fileNameIdx = c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
				fileName = c.getString(fileNameIdx);
			}
			c.close();
			return fileName;
		}
		return "";
	}
}
