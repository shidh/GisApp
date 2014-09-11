package org.grid2osm.gisapp;

import java.util.List;
import java.util.ArrayList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.SqlUtils;
import de.greenrobot.dao.internal.DaoConfig;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

import org.grid2osm.gisapp.PoiEntity;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table POI_ENTITY.
*/
public class PoiEntityDao extends AbstractDao<PoiEntity, Long> {

    public static final String TABLENAME = "POI_ENTITY";

    /**
     * Properties of entity PoiEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Done = new Property(1, Boolean.class, "done", false, "DONE");
        public final static Property PoiEntitiesId = new Property(2, long.class, "poiEntitiesId", false, "POI_ENTITIES_ID");
        public final static Property LocationEntitiesId = new Property(3, long.class, "locationEntitiesId", false, "LOCATION_ENTITIES_ID");
        public final static Property PhotoEntitiesId = new Property(4, long.class, "photoEntitiesId", false, "PHOTO_ENTITIES_ID");
        public final static Property PrimitiveAttributesEntityId = new Property(5, long.class, "primitiveAttributesEntityId", false, "PRIMITIVE_ATTRIBUTES_ENTITY_ID");
    };

    private DaoSession daoSession;

    private Query<PoiEntity> poiEntities_PoiEntityListQuery;

    public PoiEntityDao(DaoConfig config) {
        super(config);
    }
    
    public PoiEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'POI_ENTITY' (" + //
                "'_id' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'DONE' INTEGER," + // 1: done
                "'POI_ENTITIES_ID' INTEGER NOT NULL ," + // 2: poiEntitiesId
                "'LOCATION_ENTITIES_ID' INTEGER NOT NULL ," + // 3: locationEntitiesId
                "'PHOTO_ENTITIES_ID' INTEGER NOT NULL ," + // 4: photoEntitiesId
                "'PRIMITIVE_ATTRIBUTES_ENTITY_ID' INTEGER NOT NULL );"); // 5: primitiveAttributesEntityId
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'POI_ENTITY'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, PoiEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Boolean done = entity.getDone();
        if (done != null) {
            stmt.bindLong(2, done ? 1l: 0l);
        }
        stmt.bindLong(3, entity.getPoiEntitiesId());
        stmt.bindLong(4, entity.getLocationEntitiesId());
        stmt.bindLong(5, entity.getPhotoEntitiesId());
        stmt.bindLong(6, entity.getPrimitiveAttributesEntityId());
    }

    @Override
    protected void attachEntity(PoiEntity entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public PoiEntity readEntity(Cursor cursor, int offset) {
        PoiEntity entity = new PoiEntity( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getShort(offset + 1) != 0, // done
            cursor.getLong(offset + 2), // poiEntitiesId
            cursor.getLong(offset + 3), // locationEntitiesId
            cursor.getLong(offset + 4), // photoEntitiesId
            cursor.getLong(offset + 5) // primitiveAttributesEntityId
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, PoiEntity entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setDone(cursor.isNull(offset + 1) ? null : cursor.getShort(offset + 1) != 0);
        entity.setPoiEntitiesId(cursor.getLong(offset + 2));
        entity.setLocationEntitiesId(cursor.getLong(offset + 3));
        entity.setPhotoEntitiesId(cursor.getLong(offset + 4));
        entity.setPrimitiveAttributesEntityId(cursor.getLong(offset + 5));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(PoiEntity entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(PoiEntity entity) {
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
    
    /** Internal query to resolve the "poiEntityList" to-many relationship of PoiEntities. */
    public List<PoiEntity> _queryPoiEntities_PoiEntityList(long poiEntitiesId) {
        synchronized (this) {
            if (poiEntities_PoiEntityListQuery == null) {
                QueryBuilder<PoiEntity> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.PoiEntitiesId.eq(null));
                poiEntities_PoiEntityListQuery = queryBuilder.build();
            }
        }
        Query<PoiEntity> query = poiEntities_PoiEntityListQuery.forCurrentThread();
        query.setParameter(0, poiEntitiesId);
        return query.list();
    }

    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getLocationEntitiesDao().getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T1", daoSession.getPhotoEntitiesDao().getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T2", daoSession.getPrimitiveAttributesEntityDao().getAllColumns());
            builder.append(" FROM POI_ENTITY T");
            builder.append(" LEFT JOIN LOCATION_ENTITIES T0 ON T.'LOCATION_ENTITIES_ID'=T0.'_id'");
            builder.append(" LEFT JOIN PHOTO_ENTITIES T1 ON T.'PHOTO_ENTITIES_ID'=T1.'_id'");
            builder.append(" LEFT JOIN PRIMITIVE_ATTRIBUTES_ENTITY T2 ON T.'PRIMITIVE_ATTRIBUTES_ENTITY_ID'=T2.'_id'");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected PoiEntity loadCurrentDeep(Cursor cursor, boolean lock) {
        PoiEntity entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        LocationEntities locationEntities = loadCurrentOther(daoSession.getLocationEntitiesDao(), cursor, offset);
         if(locationEntities != null) {
            entity.setLocationEntities(locationEntities);
        }
        offset += daoSession.getLocationEntitiesDao().getAllColumns().length;

        PhotoEntities photoEntities = loadCurrentOther(daoSession.getPhotoEntitiesDao(), cursor, offset);
         if(photoEntities != null) {
            entity.setPhotoEntities(photoEntities);
        }
        offset += daoSession.getPhotoEntitiesDao().getAllColumns().length;

        PrimitiveAttributesEntity primitiveAttributesEntity = loadCurrentOther(daoSession.getPrimitiveAttributesEntityDao(), cursor, offset);
         if(primitiveAttributesEntity != null) {
            entity.setPrimitiveAttributesEntity(primitiveAttributesEntity);
        }

        return entity;    
    }

    public PoiEntity loadDeep(Long key) {
        assertSinglePk();
        if (key == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(getSelectDeep());
        builder.append("WHERE ");
        SqlUtils.appendColumnsEqValue(builder, "T", getPkColumns());
        String sql = builder.toString();
        
        String[] keyArray = new String[] { key.toString() };
        Cursor cursor = db.rawQuery(sql, keyArray);
        
        try {
            boolean available = cursor.moveToFirst();
            if (!available) {
                return null;
            } else if (!cursor.isLast()) {
                throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
            }
            return loadCurrentDeep(cursor, true);
        } finally {
            cursor.close();
        }
    }
    
    /** Reads all available rows from the given cursor and returns a list of new ImageTO objects. */
    public List<PoiEntity> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<PoiEntity> list = new ArrayList<PoiEntity>(count);
        
        if (cursor.moveToFirst()) {
            if (identityScope != null) {
                identityScope.lock();
                identityScope.reserveRoom(count);
            }
            try {
                do {
                    list.add(loadCurrentDeep(cursor, false));
                } while (cursor.moveToNext());
            } finally {
                if (identityScope != null) {
                    identityScope.unlock();
                }
            }
        }
        return list;
    }
    
    protected List<PoiEntity> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<PoiEntity> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}
