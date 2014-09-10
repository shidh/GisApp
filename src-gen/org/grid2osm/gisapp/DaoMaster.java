package org.grid2osm.gisapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import de.greenrobot.dao.AbstractDaoMaster;
import de.greenrobot.dao.identityscope.IdentityScopeType;

import org.grid2osm.gisapp.PoiEntityDao;
import org.grid2osm.gisapp.LocationTraceEntityDao;
import org.grid2osm.gisapp.LocationEntityDao;
import org.grid2osm.gisapp.PhotosEntityDao;
import org.grid2osm.gisapp.PhotoEntityDao;
import org.grid2osm.gisapp.PrimitiveAttributesEntityDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * Master of DAO (schema version 3): knows all DAOs.
*/
public class DaoMaster extends AbstractDaoMaster {
    public static final int SCHEMA_VERSION = 3;

    /** Creates underlying database table using DAOs. */
    public static void createAllTables(SQLiteDatabase db, boolean ifNotExists) {
        PoiEntityDao.createTable(db, ifNotExists);
        LocationTraceEntityDao.createTable(db, ifNotExists);
        LocationEntityDao.createTable(db, ifNotExists);
        PhotosEntityDao.createTable(db, ifNotExists);
        PhotoEntityDao.createTable(db, ifNotExists);
        PrimitiveAttributesEntityDao.createTable(db, ifNotExists);
    }
    
    /** Drops underlying database table using DAOs. */
    public static void dropAllTables(SQLiteDatabase db, boolean ifExists) {
        PoiEntityDao.dropTable(db, ifExists);
        LocationTraceEntityDao.dropTable(db, ifExists);
        LocationEntityDao.dropTable(db, ifExists);
        PhotosEntityDao.dropTable(db, ifExists);
        PhotoEntityDao.dropTable(db, ifExists);
        PrimitiveAttributesEntityDao.dropTable(db, ifExists);
    }
    
    public static abstract class OpenHelper extends SQLiteOpenHelper {

        public OpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory, SCHEMA_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i("greenDAO", "Creating tables for schema version " + SCHEMA_VERSION);
            createAllTables(db, false);
        }
    }
    
    /** WARNING: Drops all table on Upgrade! Use only during development. */
    public static class DevOpenHelper extends OpenHelper {
        public DevOpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
            dropAllTables(db, true);
            onCreate(db);
        }
    }

    public DaoMaster(SQLiteDatabase db) {
        super(db, SCHEMA_VERSION);
        registerDaoClass(PoiEntityDao.class);
        registerDaoClass(LocationTraceEntityDao.class);
        registerDaoClass(LocationEntityDao.class);
        registerDaoClass(PhotosEntityDao.class);
        registerDaoClass(PhotoEntityDao.class);
        registerDaoClass(PrimitiveAttributesEntityDao.class);
    }
    
    public DaoSession newSession() {
        return new DaoSession(db, IdentityScopeType.Session, daoConfigMap);
    }
    
    public DaoSession newSession(IdentityScopeType type) {
        return new DaoSession(db, type, daoConfigMap);
    }
    
}
