package org.grid2osm.gisapp;

import java.util.List;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

import org.grid2osm.gisapp.PhotoEntity;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table PHOTO_ENTITY.
*/
public class PhotoEntityDao extends AbstractDao<PhotoEntity, Long> {

    public static final String TABLENAME = "PHOTO_ENTITY";

    /**
     * Properties of entity PhotoEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Accuracy = new Property(1, Float.class, "accuracy", false, "ACCURACY");
        public final static Property Altitude = new Property(2, Double.class, "altitude", false, "ALTITUDE");
        public final static Property Bearing = new Property(3, Float.class, "bearing", false, "BEARING");
        public final static Property Latitude = new Property(4, Double.class, "latitude", false, "LATITUDE");
        public final static Property Longitude = new Property(5, Double.class, "longitude", false, "LONGITUDE");
        public final static Property Provider = new Property(6, String.class, "provider", false, "PROVIDER");
        public final static Property Time = new Property(7, Long.class, "time", false, "TIME");
        public final static Property FilePath = new Property(8, String.class, "filePath", false, "FILE_PATH");
        public final static Property PhotosEntityId = new Property(9, long.class, "photosEntityId", false, "PHOTOS_ENTITY_ID");
    };

    private Query<PhotoEntity> photosEntity_PhotoEntityListQuery;

    public PhotoEntityDao(DaoConfig config) {
        super(config);
    }
    
    public PhotoEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'PHOTO_ENTITY' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'ACCURACY' REAL," + // 1: accuracy
                "'ALTITUDE' REAL," + // 2: altitude
                "'BEARING' REAL," + // 3: bearing
                "'LATITUDE' REAL," + // 4: latitude
                "'LONGITUDE' REAL," + // 5: longitude
                "'PROVIDER' TEXT," + // 6: provider
                "'TIME' INTEGER," + // 7: time
                "'FILE_PATH' TEXT," + // 8: filePath
                "'PHOTOS_ENTITY_ID' INTEGER NOT NULL );"); // 9: photosEntityId
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'PHOTO_ENTITY'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, PhotoEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Float accuracy = entity.getAccuracy();
        if (accuracy != null) {
            stmt.bindDouble(2, accuracy);
        }
 
        Double altitude = entity.getAltitude();
        if (altitude != null) {
            stmt.bindDouble(3, altitude);
        }
 
        Float bearing = entity.getBearing();
        if (bearing != null) {
            stmt.bindDouble(4, bearing);
        }
 
        Double latitude = entity.getLatitude();
        if (latitude != null) {
            stmt.bindDouble(5, latitude);
        }
 
        Double longitude = entity.getLongitude();
        if (longitude != null) {
            stmt.bindDouble(6, longitude);
        }
 
        String provider = entity.getProvider();
        if (provider != null) {
            stmt.bindString(7, provider);
        }
 
        Long time = entity.getTime();
        if (time != null) {
            stmt.bindLong(8, time);
        }
 
        String filePath = entity.getFilePath();
        if (filePath != null) {
            stmt.bindString(9, filePath);
        }
        stmt.bindLong(10, entity.getPhotosEntityId());
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public PhotoEntity readEntity(Cursor cursor, int offset) {
        PhotoEntity entity = new PhotoEntity( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getFloat(offset + 1), // accuracy
            cursor.isNull(offset + 2) ? null : cursor.getDouble(offset + 2), // altitude
            cursor.isNull(offset + 3) ? null : cursor.getFloat(offset + 3), // bearing
            cursor.isNull(offset + 4) ? null : cursor.getDouble(offset + 4), // latitude
            cursor.isNull(offset + 5) ? null : cursor.getDouble(offset + 5), // longitude
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // provider
            cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7), // time
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // filePath
            cursor.getLong(offset + 9) // photosEntityId
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, PhotoEntity entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setAccuracy(cursor.isNull(offset + 1) ? null : cursor.getFloat(offset + 1));
        entity.setAltitude(cursor.isNull(offset + 2) ? null : cursor.getDouble(offset + 2));
        entity.setBearing(cursor.isNull(offset + 3) ? null : cursor.getFloat(offset + 3));
        entity.setLatitude(cursor.isNull(offset + 4) ? null : cursor.getDouble(offset + 4));
        entity.setLongitude(cursor.isNull(offset + 5) ? null : cursor.getDouble(offset + 5));
        entity.setProvider(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setTime(cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7));
        entity.setFilePath(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setPhotosEntityId(cursor.getLong(offset + 9));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(PhotoEntity entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(PhotoEntity entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
    /** Internal query to resolve the "photoEntityList" to-many relationship of PhotosEntity. */
    public List<PhotoEntity> _queryPhotosEntity_PhotoEntityList(long photosEntityId) {
        synchronized (this) {
            if (photosEntity_PhotoEntityListQuery == null) {
                QueryBuilder<PhotoEntity> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.PhotosEntityId.eq(null));
                photosEntity_PhotoEntityListQuery = queryBuilder.build();
            }
        }
        Query<PhotoEntity> query = photosEntity_PhotoEntityListQuery.forCurrentThread();
        query.setParameter(0, photosEntityId);
        return query.list();
    }

}
