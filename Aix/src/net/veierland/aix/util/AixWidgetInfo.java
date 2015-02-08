package net.veierland.aix.util;

import java.util.ArrayList;

import net.veierland.aix.AixProvider.AixViews;
import net.veierland.aix.AixProvider.AixWidgets;
import net.veierland.aix.AixProvider.AixWidgetsColumns;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

public class AixWidgetInfo {
	
	private Context mContext;
	
	private int mAppWidgetId;
	
	private Uri mWidgetUri = null;
	
	private Integer mSize = null;
	
	private String mViews = null;
	private ArrayList<AixViewInfo> mViewList = new ArrayList<AixViewInfo>();
	
	private AixWidgetSettings mAixWidgetSettings = null;
	
	public AixWidgetInfo() { }
	
	public AixWidgetInfo(int appWidgetId, int size) {
		mAppWidgetId = appWidgetId;
		mSize = size;
	}
	
	public static AixWidgetInfo build(Context context, Uri widgetUri) throws Exception {
		AixWidgetInfo widgetInfo = null;
		
		ContentResolver resolver = context.getContentResolver();
		Cursor widgetCursor = resolver.query(widgetUri, null, null, null, null);

		if (widgetCursor != null) {
			try {
				if (widgetCursor.moveToFirst()) {
					widgetInfo = buildFromCursor(widgetCursor);
				}
			} finally {
				widgetCursor.close();
			}
		}

		if (widgetInfo != null) {
			widgetInfo.setupViews(context);
		} else {
			throw new Exception("AixWidgetInfo.build() failed: Could not build AixWidgetInfo (uri=" + widgetUri + ")");
		}
		
		widgetInfo.mWidgetUri = widgetUri;
		widgetInfo.mContext = context;
		
		return widgetInfo;
	}
	
	public ContentValues buildContentValues() {
		mViews = setupViewsString();
		
		ContentValues values = new ContentValues();
		values.put(AixWidgetsColumns.APPWIDGET_ID, mAppWidgetId);
		
		if (mViews != null) {
			values.put(AixWidgetsColumns.VIEWS, mViews);
		} else {
			values.putNull(AixWidgetsColumns.VIEWS);
		}
		
		if (mSize != null) {
			values.put(AixWidgetsColumns.SIZE, mSize);
		} else {
			values.putNull(AixWidgetsColumns.SIZE);
		}
		
		return values;
	}
	
	public static AixWidgetInfo buildFromCursor(Cursor c) {
		AixWidgetInfo widgetInfo = new AixWidgetInfo();
		
		widgetInfo.mAppWidgetId = c.getInt(c.getColumnIndexOrThrow(AixWidgetsColumns.APPWIDGET_ID));
		widgetInfo.mSize = getWidgetSizeFromCursor(c);
		widgetInfo.mViews = getWidgetViewsFromCursor(c);
		
		return widgetInfo;
	}
	
	public Uri commit(Context context)
	{
		if (mViewList != null) {
			for (AixViewInfo view : mViewList) {
				view.commit(context);
			}
		}
		
		ContentResolver resolver = context.getContentResolver();
		ContentValues values = buildContentValues();
		
		mWidgetUri = resolver.insert(AixWidgets.CONTENT_URI, values);
		
		return mWidgetUri;
	}
	
	public int getAppWidgetId() {
		return mAppWidgetId;
	}
	
	public int getNumColumns() {
		return (mSize - 1) / 4 + 1;
	}
	
	public int getNumRows() {
		return (mSize - 1) % 4 + 1;
	}
	
	public ArrayList<AixViewInfo> getViewList() {
		return mViewList;
	}
	
	public long[] getViewsArray() {
		long[] viewsArray = null;
			
		if (!TextUtils.isEmpty(mViews)) {
			String[] views = mViews.split(":");
			viewsArray = new long[views.length];
			for (int i = 0; i < views.length; i++) {
				viewsArray[i] = Long.parseLong(views[i]);
			}
		}
		
		return viewsArray;
	}
	
	public AixWidgetSettings getWidgetSettings() {
		return mAixWidgetSettings;
	}
	
	private static int getWidgetSizeFromCursor(Cursor c) {
		int size = -1;
		
		int sizeIndex = c.getColumnIndex(AixWidgetsColumns.SIZE);
		
		if (sizeIndex != -1)
		{
			try {
				size = c.getInt(sizeIndex);
			} catch (Exception e) { }
		}
		
		// Size may be invalid due to old versions. If non-valid, default to 4x1
		if (!(size >= 1 && size <= 16)) {
			size = AixWidgetsColumns.SIZE_LARGE_TINY;
		}
		
		return size;
	}
	
	public Uri getWidgetUri() {
		if (mWidgetUri == null && mAppWidgetId != -1) {
			mWidgetUri = ContentUris.withAppendedId(AixWidgets.CONTENT_URI, mAppWidgetId);
		}
		return mWidgetUri;
	}
	
	private static String getWidgetViewsFromCursor(Cursor c) {
		int viewsIndex = c.getColumnIndex(AixWidgetsColumns.VIEWS);
		if (viewsIndex != -1) {
			String views = c.getString(viewsIndex);
			if (!TextUtils.isEmpty(views)) {
				return views;
			}
		}
		return null;
	}
	
	public void loadSettings() {
		mAixWidgetSettings = AixWidgetSettings.build(mContext, getWidgetUri());
	}
	
	private void setupViews(Context context) throws Exception {
		long[] viewsArray = getViewsArray();
		
		ArrayList<AixViewInfo> viewList = null;
		
		if (viewsArray != null && viewsArray.length > 0) {
			viewList = new ArrayList<AixViewInfo>();
			
			for (long viewID : viewsArray) {
				Uri viewUri = ContentUris.withAppendedId(AixViews.CONTENT_URI, viewID);
				viewList.add(AixViewInfo.build(context, viewUri));
			}
		}
		
		mViewList = viewList;
		mViews = setupViewsString();
	}
	
	public String setupViewsString() {
		String views = null;
		
		if (mViewList != null && mViewList.size() > 0) {
			StringBuilder stringBuilder = new StringBuilder();
			
			for (AixViewInfo viewInfo : mViewList) {
				if (stringBuilder.length() > 0)
					stringBuilder.append(":");
				stringBuilder.append(viewInfo.getId());
			}
			
			views = stringBuilder.toString();
		}
		
		return views;
	}
	
	public String toString() {
		return "AixWidgetInfo(" + mAppWidgetId + "," + mSize + "," + mViews + ")";
	}
	
}
