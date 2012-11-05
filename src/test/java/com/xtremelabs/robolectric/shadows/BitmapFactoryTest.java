package com.xtremelabs.robolectric.shadows;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import com.xtremelabs.robolectric.R;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.WithTestDefaultsRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.InputStream;

import static com.xtremelabs.robolectric.Robolectric.shadowOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.notNullValue;

@RunWith(WithTestDefaultsRunner.class)
public class BitmapFactoryTest {
    @Test
    public void decodeResource_shouldSetDescription() throws Exception {
        Bitmap bitmap = BitmapFactory.decodeResource(Robolectric.application.getResources(), R.drawable.an_image);
        assertEquals("Bitmap for resource:drawable/an_image", shadowOf(bitmap).getDescription());
        assertEquals(100, bitmap.getWidth());
        assertEquals(100, bitmap.getHeight());
    }

    @Test
    public void decodeFile_shouldSetDescription() throws Exception {
        Bitmap bitmap = BitmapFactory.decodeFile("/some/file.jpg");
        assertEquals("Bitmap for file:/some/file.jpg", shadowOf(bitmap).getDescription());
        assertEquals(100, bitmap.getWidth());
        assertEquals(100, bitmap.getHeight());
    }

    @Test
    public void decodeStream_shouldSetDescription() throws Exception {
        InputStream inputStream = Robolectric.application.getContentResolver().openInputStream(Uri.parse("content:/path"));
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        assertEquals("Bitmap for content:/path", shadowOf(bitmap).getDescription());
        assertEquals(100, bitmap.getWidth());
        assertEquals(100, bitmap.getHeight());
    }

    @Test
    public void decodeStream_shouldSetDescriptionWithNullOptions() throws Exception {
        InputStream inputStream = Robolectric.application.getContentResolver().openInputStream(Uri.parse("content:/path"));
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, null);
        assertEquals("Bitmap for content:/path", shadowOf(bitmap).getDescription());
        assertEquals(100, bitmap.getWidth());
        assertEquals(100, bitmap.getHeight());
    }

    @Test
    public void decodeResource_shouldGetWidthAndHeightFromHints() throws Exception {
        ShadowBitmapFactory.provideWidthAndHeightHints(R.drawable.an_image, 123, 456);

        Bitmap bitmap = BitmapFactory.decodeResource(Robolectric.application.getResources(), R.drawable.an_image);
        assertEquals("Bitmap for resource:drawable/an_image", shadowOf(bitmap).getDescription());
        assertEquals(123, bitmap.getWidth());
        assertEquals(456, bitmap.getHeight());
    }

    @Test
    public void decodeResource_canTakeOptions() throws Exception {
    	BitmapFactory.Options options = new BitmapFactory.Options();
    	options.inSampleSize = 100;
        Bitmap bitmap = BitmapFactory.decodeResource(Robolectric.application.getResources(), R.drawable.an_image, options);
        assertEquals(true, shadowOf(bitmap).getDescription().contains("inSampleSize=100"));
    }

    @Test
    public void decodeFile_shouldGetWidthAndHeightFromHints() throws Exception {
        ShadowBitmapFactory.provideWidthAndHeightHints("/some/file.jpg", 123, 456);

        Bitmap bitmap = BitmapFactory.decodeFile("/some/file.jpg");
        assertEquals("Bitmap for file:/some/file.jpg", shadowOf(bitmap).getDescription());
        assertEquals(123, bitmap.getWidth());
        assertEquals(456, bitmap.getHeight());
    }

    @Test
    public void decodeFileEtc_shouldSetOptionsOutWidthAndOutHeightFromHints() throws Exception {
        ShadowBitmapFactory.provideWidthAndHeightHints("/some/file.jpg", 123, 456);

        BitmapFactory.Options options = new BitmapFactory.Options();
        BitmapFactory.decodeFile("/some/file.jpg", options);
        assertEquals(123, options.outWidth);
        assertEquals(456, options.outHeight);
    }

    @Test
    public void decodeUri_shouldGetWidthAndHeightFromHints() throws Exception {
        ShadowBitmapFactory.provideWidthAndHeightHints(Uri.parse("content:/path"), 123, 456);

        Bitmap bitmap = MediaStore.Images.Media.getBitmap(Robolectric.application.getContentResolver(), Uri.parse("content:/path"));
        assertEquals("Bitmap for content:/path", shadowOf(bitmap).getDescription());
        assertEquals(123, bitmap.getWidth());
        assertEquals(456, bitmap.getHeight());
    }

    @Test
    public void decodeStream_shouldGetWidthAndHeightFromHints() throws Exception {
        ShadowBitmapFactory.provideWidthAndHeightHints(Uri.parse("content:/path"), 123, 456);

        InputStream inputStream = Robolectric.application.getContentResolver().openInputStream(Uri.parse("content:/path"));
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        assertEquals("Bitmap for content:/path", shadowOf(bitmap).getDescription());
        assertEquals(123, bitmap.getWidth());
        assertEquals(456, bitmap.getHeight());
    }
    
    @Test
    public void decodeByteArray_shouldSetDataChecksum() throws Exception {
    	byte[] data = { 23, 100, 23, 52, 23, 18, 76, 43 };
    	 
    	Bitmap bitmap = ShadowBitmapFactory.decodeByteArray(data, 0, data.length);
    	assertThat( bitmap, notNullValue() );
    	assertThat( shadowOf(bitmap).getDescription(), equalTo( "Bitmap for byte array, checksum:80429753 offset: 0 length: 8" ) );
    	assertThat( bitmap.getWidth(), equalTo(100) );
    	assertThat( bitmap.getHeight(), equalTo(100) );

    }
    
    @Test
    public void decodeByteArray_withOptionsShouldSetDataChecksum() throws Exception {
    	byte[] data = { 23, 100, 23, 52, 23, 18, 76, 43 };

    	BitmapFactory.Options options = new BitmapFactory.Options();
    	options.inSampleSize = 4;
    	Bitmap bitmap = ShadowBitmapFactory.decodeByteArray(data, 0, data.length, options);
    	assertThat( shadowOf(bitmap).getDescription(), equalTo( "Bitmap for byte array, checksum:80429753 offset: 0 length: 8 with options inSampleSize=4" ) );
    	assertThat( bitmap.getWidth(), equalTo(100) );
    	assertThat( bitmap.getHeight(), equalTo(100) );
    }
}
