package com.xtremelabs.robolectric.matchers;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import com.xtremelabs.robolectric.ProxyDelegatingHandler;
import com.xtremelabs.robolectric.fakes.ShadowActivity;
import com.xtremelabs.robolectric.fakes.ShadowApplication;
import com.xtremelabs.robolectric.fakes.ShadowContextWrapper;
import com.xtremelabs.robolectric.fakes.ShadowIntent;
import org.hamcrest.Description;
import org.junit.internal.matchers.TypeSafeMatcher;

public class StartedMatcher extends TypeSafeMatcher<Context> {
    private final Intent expectedIntent;

    private String message;

    public StartedMatcher(Intent expectedIntent) {
        this.expectedIntent = expectedIntent;
    }

    public StartedMatcher(String packageName, Class<? extends Activity> expectedActivityClass) {
        this(createIntent(packageName, expectedActivityClass));
    }

    public StartedMatcher(Class<? extends Activity> expectedActivityClass) {
        this(createIntent(expectedActivityClass));
    }

    public StartedMatcher(Class<? extends Activity> expectedActivityClass, String expectedAction) {
        this(createIntent(expectedActivityClass));

        expectedIntent.setAction(expectedAction);
    }

    @Override
    public boolean matchesSafely(Context actualContext) {
        if (expectedIntent == null) {
            message = "null intent (did you mean to expect null?)";
            return false;
        }

        String expected = expectedIntent.toString();
        message = "to start " + expected + ", but ";

        Intent actualStartedIntent = shadowFor((ContextWrapper) actualContext).getNextStartedActivity();

        if (actualStartedIntent == null) {
            message += "didn't start anything";
            return false;
        }

        ShadowIntent proxyIntent = shadowFor(actualStartedIntent);

        boolean intentsMatch = shadowFor(expectedIntent).realIntentEquals(proxyIntent);
        if (!intentsMatch) {
            message += "started " + actualStartedIntent;
        }
        return intentsMatch;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(message);
    }

    private ShadowActivity shadowFor(Activity real) {
        return (ShadowActivity) ProxyDelegatingHandler.getInstance().shadowFor(real);
    }

    private ShadowContextWrapper shadowFor(ContextWrapper real) {
        return (ShadowContextWrapper) ProxyDelegatingHandler.getInstance().shadowFor(real);
    }

    private ShadowApplication shadowFor(Application real) {
        return (ShadowApplication) ProxyDelegatingHandler.getInstance().shadowFor(real);
    }

    private ShadowIntent shadowFor(Intent real) {
        return (ShadowIntent) ProxyDelegatingHandler.getInstance().shadowFor(real);
    }

    public static Intent createIntent(Class<? extends Activity> activityClass, String extraKey, String extraValue) {
        Intent intent = createIntent(activityClass);
        intent.putExtra(extraKey, extraValue);
        return intent;
    }

    public static Intent createIntent(Class<? extends Activity> activityClass, String action) {
        Intent intent = createIntent(activityClass);
        intent.setAction(action);
        return intent;
    }

    public static Intent createIntent(Class<? extends Activity> activityClass) {
        String packageName = activityClass.getPackage().getName();
        return createIntent(packageName, activityClass);
    }

    public static Intent createIntent(String packageName, Class<? extends Activity> activityClass) {
        Intent intent = new Intent();
        intent.setClassName(packageName, activityClass.getName());
        return intent;
    }
}