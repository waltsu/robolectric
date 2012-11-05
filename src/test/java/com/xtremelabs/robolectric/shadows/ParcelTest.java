package com.xtremelabs.robolectric.shadows;

import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Parcel;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.WithTestDefaultsRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(WithTestDefaultsRunner.class)
public class ParcelTest {

    private Parcel parcel;
    private ShadowParcel shadowParcel;

    @Before
    public void setup() {
        parcel = Parcel.obtain();
        shadowParcel = Robolectric.shadowOf(parcel);
    }

    @Test
    public void testObtain() {
        assertThat(parcel, notNullValue());
        assertThat(shadowParcel.getIndex(), equalTo(0));
        assertThat(shadowParcel.getParcelData().size(), equalTo(0));
    }

    @Test
    public void testReadIntWhenEmpty() {
        assertThat(parcel.readInt(), equalTo(0));
    }

    @Test
    public void testReadLongWhenEmpty() {
        assertThat(parcel.readLong(), equalTo(0l));
    }

    @Test
    public void testReadStringWhenEmpty() {
        assertThat(parcel.readString(), nullValue());
    }

    @Test
    public void testReadStrongBinderWhenEmpty() {
        assertThat(parcel.readStrongBinder(), nullValue());
    }

    @Test
    public void testReadWriteSingleString() {
        String val = "test";
        parcel.writeString(val);
        assertThat(parcel.readString(), equalTo(val));
    }

    @Test
    public void testWriteNullString() {
        parcel.writeString(null);
        assertThat(parcel.readString(), nullValue());
        assertThat(shadowParcel.getIndex(), equalTo(0));
        assertThat(shadowParcel.getParcelData().size(), equalTo(0));
    }

    @Test
    public void testReadWriteMultipleStrings() {
        for (int i = 0; i < 10; ++i) {
            parcel.writeString(Integer.toString(i));
        }
        for (int i = 0; i < 10; ++i) {
            assertThat(parcel.readString(), equalTo(Integer.toString(i)));
        }
        // now try to read past the number of items written and see what happens
        assertThat(parcel.readString(), nullValue());
    }

    @Test
    public void testReadWriteSingleInt() {
        int val = 5;
        parcel.writeInt(val);
        assertThat(parcel.readInt(), equalTo(val));
    }

    @Test
    public void testReadWriteIntArray() throws Exception {
        final int[] ints = {1, 2};
        parcel.writeIntArray(ints);
        final int[] ints2 = new int[ints.length];
        parcel.readIntArray(ints2);
        assertTrue(Arrays.equals(ints, ints2));
    }

    @Test
    public void testReadWriteLongArray() throws Exception {
        final long[] longs = {1, 2};
        parcel.writeLongArray(longs);
        final long[] longs2 = new long[longs.length];
        parcel.readLongArray(longs2);
        assertTrue(Arrays.equals(longs, longs2));
    }

    @Test
    public void testReadWriteSingleFloat() {
        float val = 5.2f;
        parcel.writeFloat(val);
        assertThat(parcel.readFloat(), equalTo(val));
    }

    @Test
    public void testReadWriteFloatArray() throws Exception {
        final float[] floats = {1.1f, 2.0f};
        parcel.writeFloatArray(floats);
        final float[] floats2 = new float[floats.length];
        parcel.readFloatArray(floats2);
        assertTrue(Arrays.equals(floats, floats2));
    }

    @Test
    public void testReadWriteDoubleArray() throws Exception {
        final double[] doubles = {1.1f, 2.0f};
        parcel.writeDoubleArray(doubles);
        final double[] doubles2 = new double[doubles.length];
        parcel.readDoubleArray(doubles2);
        assertTrue(Arrays.equals(doubles, doubles2));
    }

    @Test
    public void testReadWriteStringArray() throws Exception {
        final String[] strings = {"foo", "bar"};
        parcel.writeStringArray(strings);
        final String[] strings2 = new String[strings.length];
        parcel.readStringArray(strings2);
        assertTrue(Arrays.equals(strings, strings2));
    }

    @Test
    public void testReadWriteMultipleInts() {
        for (int i = 0; i < 10; ++i) {
            parcel.writeInt(i);
        }
        for (int i = 0; i < 10; ++i) {
            assertThat(parcel.readInt(), equalTo(i));
        }
        // now try to read past the number of items written and see what happens
        assertThat(parcel.readInt(), equalTo(0));
    }

    @Test
    public void testReadWriteSingleByte() {
        byte val = 1;
        parcel.writeByte(val);
        assertThat(parcel.readByte(), equalTo(val));
    }

    @Test
    public void testReadWriteMultipleBytes() {
        for (byte i = Byte.MIN_VALUE; i < Byte.MAX_VALUE; ++i) {
            parcel.writeByte(i);
        }
        for (byte i = Byte.MIN_VALUE; i < Byte.MAX_VALUE; ++i) {
            assertThat(parcel.readByte(), equalTo(i));
        }
        // now try to read past the number of items written and see what happens
        assertThat(parcel.readByte(), equalTo((byte) 0));
    }


    @Test
    public void testReadWriteStringInt() {
        for (int i = 0; i < 10; ++i) {
            parcel.writeString(Integer.toString(i));
            parcel.writeInt(i);
        }
        for (int i = 0; i < 10; ++i) {
            assertThat(parcel.readString(), equalTo(Integer.toString(i)));
            assertThat(parcel.readInt(), equalTo(i));
        }
        // now try to read past the number of items written and see what happens
        assertThat(parcel.readString(), nullValue());
        assertThat(parcel.readInt(), equalTo(0));
    }

    @Test(expected = ClassCastException.class)
    public void testWriteStringReadInt() {
        String val = "test";
        parcel.writeString(val);
        parcel.readInt();
    }

    @Test(expected = ClassCastException.class)
    public void testWriteIntReadString() {
        int val = 9;
        parcel.writeInt(val);
        parcel.readString();
    }

    @Test
    public void testReadWriteSingleLong() {
        long val = 5;
        parcel.writeLong(val);
        assertThat(parcel.readLong(), equalTo(val));
    }

    @Test
    public void testReadWriteMultipleLongs() {
        for (long i = 0; i < 10; ++i) {
            parcel.writeLong(i);
        }
        for (long i = 0; i < 10; ++i) {
            assertThat(parcel.readLong(), equalTo(i));
        }
        // now try to read past the number of items written and see what happens
        assertThat(parcel.readLong(), equalTo(0l));
    }

    @Test
    public void testReadWriteStringLong() {
        for (long i = 0; i < 10; ++i) {
            parcel.writeString(Long.toString(i));
            parcel.writeLong(i);
        }
        for (long i = 0; i < 10; ++i) {
            assertThat(parcel.readString(), equalTo(Long.toString(i)));
            assertThat(parcel.readLong(), equalTo(i));
        }
        // now try to read past the number of items written and see what happens
        assertThat(parcel.readString(), nullValue());
        assertThat(parcel.readLong(), equalTo(0l));
    }

    @Test(expected = ClassCastException.class)
    public void testWriteStringReadLong() {
        String val = "test";
        parcel.writeString(val);
        parcel.readLong();
    }

    @Test(expected = ClassCastException.class)
    public void testWriteLongReadString() {
        long val = 9;
        parcel.writeLong(val);
        parcel.readString();
    }

    @Test
    public void testReadWriteParcelable() {
        Intent i1 = new Intent("anAction");
        parcel.writeParcelable(i1, 0);

        Intent i2 = parcel.readParcelable(Intent.class.getClassLoader());
        assertEquals(i1, i2);
    }

    @Test
    public void testReadWriteBundle() {
        Bundle b1 = new Bundle();
        b1.putString("hello", "world");
        parcel.writeBundle(b1);
        Bundle b2 = parcel.readBundle();

        assertEquals(b1, b2);
        assertEquals("world", b2.getString("hello"));

        parcel.writeBundle(b1);
        b2 = parcel.readBundle(null /* ClassLoader */);
        assertEquals(b1, b2);
        assertEquals("world", b2.getString("hello"));
    }

    @Test
    public void testCreateStringArrayList() throws Exception {
        parcel.writeInt(2);
        parcel.writeString("str1");
        parcel.writeString("str2");
        List<String> actual = parcel.createStringArrayList();
        assertEquals(2, actual.size());
        assertEquals("str1", actual.get(0));
        assertEquals("str2", actual.get(1));
    }

    @Test
    public void testWriteTypedListAndCreateTypedArrayList() throws Exception {
        TestParcelable normal = new TestParcelable(23);
        ArrayList<TestParcelable> normals = new ArrayList<TestParcelable>();
        normals.add(normal);

        parcel.writeTypedList(normals);
        ArrayList<TestParcelable> rehydrated = parcel.createTypedArrayList(TestParcelable.CREATOR);

        assertEquals(1, rehydrated.size());
        assertEquals(23, rehydrated.get(0).contents);
    }

    @Test
    public void testReadAndWriteStringList() throws Exception {
        ArrayList<String> original = new ArrayList<String>();
        ArrayList<String> rehydrated = new ArrayList<String>();
        original.add("str1");
        original.add("str2");
        parcel.writeStringList(original);
        parcel.readStringList(rehydrated);
        assertEquals(2, rehydrated.size());
        assertEquals("str1", rehydrated.get(0));
        assertEquals("str2", rehydrated.get(1));
    }

    @Test
    public void testReadWriteStrongBinder() throws Exception {
        Binder expected = new Binder();
        parcel.writeStrongBinder(expected);
        assertEquals(expected, parcel.readStrongBinder());
    }

    @Test
    public void testReadWriteMap() throws Exception {
        HashMap<String, String> original = new HashMap<String, String>();
        original.put("key", "value");
        parcel.writeMap(original);
        HashMap<String, String> rehydrated = parcel.readHashMap(null);

        assertEquals("value", rehydrated.get("key"));
    }
}
