package com.meibanlu.driver.tool;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;


import com.meibanlu.driver.R;
import com.meibanlu.driver.bean.UpdateEntity;

import java.io.File;



public class UpdateManager {
	private static final String TAG="UpdateManager";
	/**
	 * 1。需要升级 2.强制升级
	 */
	private static final String UPDATE ="非强制更新";
	private final Activity activity;
    private UpdateEntity entity;

	public UpdateManager(Activity activity, UpdateEntity entity) {
		this.activity = activity;
		this.entity = entity;

	}

	public void showNoticeDialog() {
		// 构造对话框
		Builder builder = new Builder(activity);
		builder.setTitle(R.string.soft_update_title);
		builder.setPositiveButton(R.string.soft_update_updatebtn,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 显示下载对话框
						UpdateManager.this.downloadApk();
						dialog.dismiss();
					}
				});
		if (entity.getType().equals(UPDATE)) {
			builder.setMessage(R.string.soft_update_info_option);
			// 稍后更新
			builder.setNegativeButton(R.string.soft_update_later,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
		} else {
			builder.setMessage(R.string.soft_update_info_foced);
		}

		Dialog noticeDialog = builder.create();
		noticeDialog.setCancelable(false);
		noticeDialog.show();
	}
	/**
	 * 下载apk文件
	 */
	private void downloadApk() {
		DownloadManager manager= (DownloadManager) activity.getSystemService( Context.DOWNLOAD_SERVICE );
//		new downloadApkThread().start();
		Long downloadId= SharePreData.getInstance().getLong(R.string.download);
		if (downloadId != -1L) {
			int status=getDownloadStatus(downloadId,manager);
			if (status == DownloadManager.STATUS_SUCCESSFUL) {
				//启动更新界面
				Uri uri = manager.getUriForDownloadedFile(downloadId);
				if (uri != null) {
					if (compare(getApkInfo(activity, getApkPath(manager,downloadId)), activity)) {
						startInstall(activity, getApkPath(manager,downloadId));
						return;
					} else {
						manager.remove(downloadId);
					}
				}
				startDownload( manager );
			} else if (status == DownloadManager.STATUS_FAILED) {
				startDownload( manager );
			} else {
				Log.d(TAG, "apk is already downloading");
			}
		} else {
			startDownload( manager );
		}

	}
    private void startDownload(DownloadManager manager){
		//以下两行代码可以让下载的apk文件被直接安装而不用使用Fileprovider,系统7.0或者以上才启动。
		if(TextUtils.isEmpty(Constants.Update_URL )) return;
		File file = new File(Environment.getExternalStorageDirectory()
				+ File.separator + "tywj" );
		if (!file.exists()||!file.isDirectory()){
			if(!file.mkdir()){
				T.showShort("文件夹创建失败");
				return;
			}
		}
		Uri uri = Uri.parse(Constants.Update_URL);
		DownloadManager.Request request=new DownloadManager.Request( uri );
		request.setDestinationInExternalPublicDir("tywj" ,"panda.apk" );
		request.setDescription( "胖哒司机端更新" );
//		request.setAllowedNetworkTypes( DownloadManager.Request.NETWORK_MOBILE );
		request.setTitle( "胖哒_司机端" );
		//7.0以上的系统适配
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//			request.setRequiresDeviceIdle(false);
//			request.setRequiresCharging(false);
//		}
		// 下载过程和下载完成后通知栏有通知消息。
		request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE | DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
		request.setMimeType("application/vnd.android.package-archive");
		// 设置为可被媒体扫描器找到
		request.allowScanningByMediaScanner();
		// 设置为可见和可管理
		request.setVisibleInDownloadsUi(true);
//		if (activity != null) {
//			activity.startActivity(new android.content.Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));//启动系统下载界面
//		}
		Long downloadId=manager.enqueue( request );
		SharePreData.getInstance().putLong(R.string.download,downloadId);
	}

	/**
	 * 安装APK文件
	 */
	private static void startInstall(Context context, String path) {
		Intent install = new Intent(Intent.ACTION_VIEW);
		File file = new File(path);
		if (Build.VERSION.SDK_INT >= 24) { //判读版本是否在7.0以上
			//参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
			Uri apkUri = FileProvider.getUriForFile(context, "tywj.busDriver.provider", file);
			//添加这一句表示对目标应用临时授权该Uri所代表的文件
			install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			install.setDataAndType(apkUri, "application/vnd.android.package-archive");
		} else {
			install.setDataAndType(Uri.fromFile(file),
					"application/vnd.android.package-archive");
		}
		install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(install);
		android.os.Process.killProcess(android.os.Process.myPid());
	}


	private int getDownloadStatus(long downloadId,DownloadManager dm) {
		DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
		Cursor c = dm.query(query);
		if (c != null) {
			try {
				if (c.moveToFirst()) {
					return c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));

				}
			} finally {
				c.close();
			}
		}
		return -1;
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
	/**
	 * 获取apk程序信息[packageName,versionName...]
	 *
	 * @param context Context
	 * @param path    apk path
	 */
	private static PackageInfo getApkInfo(Context context, String path) {
		Log.d(TAG,"path"+path);
		PackageManager pm = context.getPackageManager();
		PackageInfo info = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
		if (info != null) {
			Log.i( TAG,info.toString() );
			String packageName = info.packageName;
			String version = info.versionName;
			Log.d(TAG, "packageName:" + packageName + ";version:" + version);
			return info;
		}
		return null;
	}

	/**
	 * 下载的apk和当前程序版本比较
	 *
	 * @param apkInfo apk file's packageInfo
	 * @param context Context
	 * @return 如果当前应用版本小于apk的版本则返回true
	 */
	private static boolean compare(PackageInfo apkInfo, Context context) {
		if (apkInfo == null) {
			return false;
		}
		Log.d(TAG, "compare packageName:" + apkInfo.packageName + ";version:" + apkInfo.versionName);
		String localPackage = context.getPackageName();
		if (apkInfo.packageName.equals(localPackage)) {
			try {
				PackageInfo packageInfo = context.getPackageManager().getPackageInfo(localPackage, 0);
				if (apkInfo.versionCode > packageInfo.versionCode) {
					return true;
				}
			} catch (PackageManager.NameNotFoundException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

}
