package com.xtremelabs.robolectric.shadows;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.xtremelabs.robolectric.WithTestDefaultsRunner;

@RunWith(WithTestDefaultsRunner.class)
public class SQLiteOpenHelperTest {

    private TestOpenHelper helper;

    @Before
    public void setUp() throws Exception {
        helper = new TestOpenHelper(null, "path", null, 1);
    }

    @Test
    public void testInitialGetReadableDatabase() throws Exception {
        SQLiteDatabase database = helper.getReadableDatabase();
        assertInitialDB(database);
    }

    @Test
    public void testSubsequentGetReadableDatabase() throws Exception {
        SQLiteDatabase database = helper.getReadableDatabase();
        helper.reset();
        database = helper.getReadableDatabase();

        assertSubsequentDB(database);
    }

    @Test
    public void testSameDBInstanceSubsequentGetReadableDatabase() throws Exception {
        SQLiteDatabase db1 = helper.getReadableDatabase();
        SQLiteDatabase db2 = helper.getReadableDatabase();

        assertThat(db1, sameInstance(db2));
    }

    @Test
    public void testInitialGetWritableDatabase() throws Exception {
        SQLiteDatabase database = helper.getWritableDatabase();
        assertInitialDB(database);
    }

    @Test
    public void testSubsequentGetWritableDatabase() throws Exception {
        helper.getWritableDatabase();
        helper.reset();

        assertSubsequentDB(helper.getWritableDatabase());
    }

    @Test
    public void testSameDBInstanceSubsequentGetWritableDatabase() throws Exception {
        SQLiteDatabase db1 = helper.getWritableDatabase();
        SQLiteDatabase db2 = helper.getWritableDatabase();

        assertThat(db1, sameInstance(db2));
    }

    @Test
    public void testClose() throws Exception {
        SQLiteDatabase database = helper.getWritableDatabase();

        assertThat(database.isOpen(), equalTo(true));
        helper.close();
        assertThat(database.isOpen(), equalTo(false));
    }
    
    @Test
    public void testSameContextSameDatabase() throws Exception {
    	Context c = new Activity();
    	TestOpenHelper helper1 = new TestOpenHelper(c, "path", null, 1);
    	SQLiteDatabase db1 = helper1.getWritableDatabase();
    	
    	TestOpenHelper helper2 = new TestOpenHelper(c, "path", null, 1);
    	SQLiteDatabase db2 = helper2.getWritableDatabase();
    	
    	assertThat(db1, sameInstance(db2));
    }
    
    @Test
    public void testDifferentContextDifferentDatabase() throws Exception {
    	Context c1 = new Activity();
    	Context c2 = new Activity();
    	
    	TestOpenHelper helper1 = new TestOpenHelper(c1, "path", null, 1);
    	SQLiteDatabase db1 = helper1.getWritableDatabase();
    	
    	TestOpenHelper helper2 = new TestOpenHelper(c2, "path", null, 1);
    	SQLiteDatabase db2 = helper2.getWritableDatabase();
    	
    	assertNotSame(db1, db2);
    }
    
    @Test
    public void testSameContextSameDatabaseAfterClose() throws Exception {
    	Context c = new Activity();
    	TestOpenHelper helper1 = new TestOpenHelper(c, "path", null, 1);
    	SQLiteDatabase db1 = helper1.getWritableDatabase();
    	
    	helper1.close();
    	
    	TestOpenHelper helper2 = new TestOpenHelper(c, "path", null, 1);
    	SQLiteDatabase db2 = helper2.getWritableDatabase();
    	
    	assertThat(db1, sameInstance(db2));
    }

    private void assertInitialDB(SQLiteDatabase database) {
        assertDatabaseOpened(database);
        assertThat(helper.onCreateCalled, equalTo(true));
    }

    private void assertSubsequentDB(SQLiteDatabase database) {
        assertDatabaseOpened(database);
        assertThat(helper.onCreateCalled, equalTo(false));
    }

    private void assertDatabaseOpened(SQLiteDatabase database) {
        assertThat(database, notNullValue());
        assertThat(database.isOpen(), equalTo(true));
        assertThat(helper.onOpenCalled, equalTo(true));
        assertThat(helper.onUpgradeCalled, equalTo(false));
    }

    private class TestOpenHelper extends SQLiteOpenHelper {

        public boolean onCreateCalled;
        public boolean onUpgradeCalled;
        public boolean onOpenCalled;

        public TestOpenHelper(Context context, String name,
                              CursorFactory factory, int version) {
            super(context, name, factory, version);
            reset();
        }

        @Override
        public void onCreate(SQLiteDatabase database) {
            onCreateCalled = true;
        }

        @Override
        public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
            onUpgradeCalled = true;
        }

        @Override
        public void onOpen(SQLiteDatabase database) {
            onOpenCalled = true;
        }

        public void reset() {
            onCreateCalled = false;
            onUpgradeCalled = false;
            onOpenCalled = false;
        }
    }
}
