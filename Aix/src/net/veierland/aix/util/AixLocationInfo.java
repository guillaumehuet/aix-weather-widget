package net.veierland.aix.util;

import java.util.TimeZone;

import net.veierland.aix.AixProvider.AixLocations;
import net.veierland.aix.AixProvider.AixLocationsColumns;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

public class AixLocationInfo {

	private long id_ = -1;
	
	//private boolean _modified = false;
	
	private Double latitude_ = null;
	private Double longitude_ = null;
	
	private Integer type_ = null;
	
	private Long timeOfLastFix_ = null;
	private Long lastForecastUpdate_ = null;
	private Long forecastValidTo_ = null;
	private Long nextForecastUpdate_ = null;
	
	private String title_ = null;
	private String titleDetailed_ = null;
	private String timeZone_ = null;
	
	private TimeZone timeZoneObject_ = null;
	
	private Uri uri_ = null;
	
	public AixLocationInfo() { }
	
	public static AixLocationInfo build(Context context, Uri locationUri) throws Exception {
		if (context == null)
		{
			throw new IllegalArgumentException("AixLocationInfo.build() failed: context must be non-null");
		}
		if (locationUri == null)
		{
			throw new IllegalArgumentException("AixLocationInfo.build() failed: locationUri must be non-null");
		}
		
		AixLocationInfo locationInfo = null;
		
		ContentResolver resolver = context.getContentResolver();
		Cursor locationCursor = resolver.query(locationUri, null, null, null, null);
		
		if (locationCursor != null) {
			try {
				if (locationCursor.moveToFirst()) {
					locationInfo = AixLocationInfo.buildFromCursor(locationCursor);
				}
			} finally {
				locationCursor.close();
			}
		}
		
		if (locationInfo == null) {
			throw new Exception("AixLocationInfo.build(): Failed to build AixLocationInfo (locationUri=" + locationUri + ")");
		}
		
		return locationInfo;
	}
	
	private ContentValues buildContentValues()
	{
		ContentValues values = new ContentValues();
		values.put(AixLocationsColumns.TITLE, title_);
		values.put(AixLocationsColumns.TITLE_DETAILED, titleDetailed_);
		values.put(AixLocationsColumns.TIME_ZONE, timeZone_);
		values.put(AixLocationsColumns.TYPE, type_);
		values.put(AixLocationsColumns.TIME_OF_LAST_FIX, timeOfLastFix_);
		values.put(AixLocationsColumns.LATITUDE, latitude_);
		values.put(AixLocationsColumns.LONGITUDE, longitude_);
		values.put(AixLocationsColumns.LAST_FORECAST_UPDATE, lastForecastUpdate_);
		values.put(AixLocationsColumns.FORECAST_VALID_TO, forecastValidTo_);
		values.put(AixLocationsColumns.NEXT_FORECAST_UPDATE, nextForecastUpdate_);
		return values;
	}
	
	public static AixLocationInfo buildFromCursor(Cursor c) {
		AixLocationInfo locationInfo = new AixLocationInfo();
		
		locationInfo.id_ = c.getLong(c.getColumnIndexOrThrow(BaseColumns._ID));
		
		int titleColumn = c.getColumnIndex(AixLocationsColumns.TITLE);
		if (titleColumn != -1) locationInfo.title_ = c.getString(titleColumn);
		
		int titleDetailedColumn = c.getColumnIndex(AixLocationsColumns.TITLE_DETAILED);
		if (titleDetailedColumn != -1) locationInfo.titleDetailed_ = c.getString(titleDetailedColumn);
		
		int timeZoneColumn = c.getColumnIndex(AixLocationsColumns.TIME_ZONE);
		if (timeZoneColumn != -1) locationInfo.timeZone_ = c.getString(timeZoneColumn);
		
		int typeColumn = c.getColumnIndex(AixLocationsColumns.TYPE);
		if (typeColumn != -1) locationInfo.type_ = c.getInt(typeColumn);
		
		int timeOfLastFixColumn = c.getColumnIndex(AixLocationsColumns.TIME_OF_LAST_FIX);
		if (timeOfLastFixColumn != -1) locationInfo.timeOfLastFix_ = c.getLong(timeOfLastFixColumn);
		
		int latitudeColumn = c.getColumnIndex(AixLocationsColumns.LATITUDE);
		if (latitudeColumn != -1) locationInfo.latitude_ = c.getDouble(latitudeColumn);
		
		int longitudeColumn = c.getColumnIndex(AixLocationsColumns.LONGITUDE);
		if (longitudeColumn != -1) locationInfo.longitude_ = c.getDouble(longitudeColumn);
		
		int lastForecastUpdateColumn = c.getColumnIndex(AixLocationsColumns.LAST_FORECAST_UPDATE);
		if (lastForecastUpdateColumn != -1) locationInfo.lastForecastUpdate_ = c.getLong(lastForecastUpdateColumn);
		
		int forecastValidToColumn = c.getColumnIndex(AixLocationsColumns.FORECAST_VALID_TO);
		if (forecastValidToColumn != -1) locationInfo.forecastValidTo_ = c.getLong(forecastValidToColumn);
		
		int nextForecastUpdateColumn = c.getColumnIndex(AixLocationsColumns.NEXT_FORECAST_UPDATE);
		if (nextForecastUpdateColumn != -1) locationInfo.nextForecastUpdate_ = c.getLong(nextForecastUpdateColumn);
		
		return locationInfo;
	}
	
	public TimeZone buildTimeZone() {
		if (timeZoneObject_ == null && timeZone_ != null) {
			timeZoneObject_ = TimeZone.getTimeZone(timeZone_);
		}
		return timeZoneObject_;
	}
	
	public Uri commit(Context context)
	{
		Uri locationUri = null;
		//if (_modified)
		//{
			ContentResolver resolver = context.getContentResolver();
			ContentValues values = buildContentValues();
			
			if (id_ == -1)
			{
				locationUri = resolver.insert(AixLocations.CONTENT_URI, values);
				id_ = ContentUris.parseId(locationUri);
			}
			else
			{
				locationUri = ContentUris.withAppendedId(AixLocations.CONTENT_URI, id_);
				resolver.update(locationUri, values, null, null);
			}
		//}
		return locationUri;
	}
	
	public Long getForecastValidTo() {
		return forecastValidTo_;
	}
	
	public long getId() {
		return id_;
	}
	
	public Long getLastForecastUpdate() {
		return lastForecastUpdate_;
	}
	
	public Double getLatitude() {
		return latitude_;
	}
	
	public Uri getLocationUri() {
		if (uri_ == null && id_ != -1) {
			uri_ = ContentUris.withAppendedId(AixLocations.CONTENT_URI, id_);
		}
		return uri_;
	}
	
	public Double getLongitude() {
		return longitude_;
	}
	
	public Long getNextForecastUpdate() {
		return nextForecastUpdate_;
	}
	
	public Long getTimeOfLastFix() {
		return timeOfLastFix_;
	}
	
	public String getTimeZone() {
		return timeZone_;
	}
	
	public String getTitle() {
		return title_;
	}
	
	public String getTitleDetailed() {
		return titleDetailed_;
	}
	
	public Integer getType() {
		return type_;
	}
	
	//public boolean isModified() {
		//return _modified;
	//}
	
	public void setForecastValidTo(Long forecastValidTo) {
		forecastValidTo_ = forecastValidTo;
		//_modified = true;
	}
	
	public void setId(long id) {
		id_ = id;
		//_modified = true;
	}
	
	public void setLastForecastUpdate(Long lastForecastUpdate) {
		lastForecastUpdate_ = lastForecastUpdate;
		//_modified = true;
	}
	
	public void setLatitude(Double latitude) {
		latitude_ = latitude;
		//_modified = true;
	}
	
	public void setLongitude(Double longitude) {
		longitude_ = longitude;
		//_modified = true;
	}
	
	public void setNextForecastUpdate(Long nextForecastUpdate) {
		nextForecastUpdate_ = nextForecastUpdate;
		//_modified = true;
	}
	
	public void setTimeOfLastFix(Long timeOfLastFix) {
		timeOfLastFix_ = timeOfLastFix;
		//_modified = true;
	}
	
	public void setTimeZone(String timeZone) {
		timeZone_ = timeZone;
		timeZoneObject_ = null;
		//_modified = true;
	}
	
	public void setTitle(String title) {
		title_ = title;
		//_modified = true;
	}
	
	public void setTitleDetailed(String titleDetailed) {
		titleDetailed_ = titleDetailed;
		//_modified = true;
	}
	
	public void setType(Integer type) {
		type_ = type;
		//_modified = true;
	}

	@Override
	public String toString() {
		return "AixLocationInfo(" + id_ + "," + title_ + "," + "," + titleDetailed_ + ","
				+ timeZone_ + "," + type_ + "," + timeOfLastFix_ + ","
				+ latitude_ + "," + longitude_ + ","
				+ lastForecastUpdate_ + ","
				+ forecastValidTo_ + ","
				+ nextForecastUpdate_ + ")";
	}
	
}
