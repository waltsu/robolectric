package com.xtremelabs.robolectric.shadows;

import android.graphics.Typeface;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.WithTestDefaultsRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileWriter;

import static com.xtremelabs.robolectric.Robolectric.shadowOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

@RunWith(WithTestDefaultsRunner.class)
public class TypefaceTest {
    private File assetsBase;
    private File fontFile;

    @Before
    public void setup() throws Exception {
        assetsBase = shadowOf(Robolectric.application).getResourceLoader().getAssetsBase();
        fontFile = new File(assetsBase, "myFont.ttf");
        FileWriter fileWriter = new FileWriter(fontFile);
        fileWriter.write("fontdata");
        fileWriter.close();
    }

    @After
    public void teardown() throws Exception {
        fontFile.delete();
    }

    @Test
    public void canAnswerAssetUsedDuringCreation() throws Exception {
        Typeface typeface = Typeface.createFromAsset(Robolectric.application.getAssets(), "myFont.ttf");
        assertThat(shadowOf(typeface).getAssetPath(), equalTo("myFont.ttf"));
    }

    @Test(expected = RuntimeException.class)
    public void createFromAsset_throwsExceptionWhenFontNotFound() throws Exception {
        Typeface.createFromAsset(Robolectric.application.getAssets(), "nonexistent.ttf");
    }

    @Test(expected = RuntimeException.class)
    public void createFromFile_shouldRaiseErrorFromInvalidFilePath() throws Exception {
        //set allowed font paths:
        ShadowTypeface.addAllowedTypefacePath("/system/fonts/Robolectric.ttf");

        //raise error if given font path is not a member of allowed font paths
        Typeface.createFromFile("/system/fonts/Roboto.ttf");
    }

    @Test
    public void createFromFile_shouldReturnNewTypeface() throws Exception {
        //set allowed font paths:
        String path = "/system/fonts/Robolectric.ttf";
        ShadowTypeface.addAllowedTypefacePath(path);

        // if given font path is ok, return a new Typeface
        Typeface typeface = Typeface.createFromFile(path);
        assertThat(typeface, instanceOf(Typeface.class));
        assertThat(shadowOf(typeface).getAssetPath(), equalTo(path));
    }

    @Test
    public void canGetDefaultTypeface() throws Exception {
        assertThat(shadowOf(Typeface.DEFAULT).getAssetPath(), equalTo("/default/font"));
    }
}
