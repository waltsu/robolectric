package com.xtremelabs.robolectric.shadows;

import android.animation.ObjectAnimator;
import android.os.Handler;
import android.os.Looper;
import com.xtremelabs.robolectric.RobolectricShadowOfLevel16;
import com.xtremelabs.robolectric.internal.Implementation;
import com.xtremelabs.robolectric.internal.Implements;
import com.xtremelabs.robolectric.internal.RealObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SuppressWarnings({"UnusedDeclaration"})
@Implements(ObjectAnimator.class)
public class ShadowObjectAnimator extends ShadowValueAnimator {
    private static boolean pausingEndNotifications;
    private static List<ShadowObjectAnimator> pausedEndNotifications = new ArrayList<ShadowObjectAnimator>();

    @RealObject
    private ObjectAnimator realObject;
    private Object target;
    private String propertyName;
    private float[] floatValues;
    private int[] intValues;
    private Class<?> animationType;
    private static final Map<Object, Map<String, ObjectAnimator>> mapsForAnimationTargets = new HashMap<Object, Map<String, ObjectAnimator>>();
    private boolean isRunning;
    private boolean cancelWasCalled;

    @Implementation
    public static ObjectAnimator ofFloat(Object target, String propertyName, float... values) {
        ObjectAnimator result = new ObjectAnimator();

        result.setTarget(target);
        result.setPropertyName(propertyName);
        result.setFloatValues(values);
        RobolectricShadowOfLevel16.shadowOf(result).setAnimationType(float.class);

        getAnimatorMapFor(target).put(propertyName, result);
        return result;
    }

    @Implementation
    public static ObjectAnimator ofInt(Object target, String propertyName, int... values) {
        ObjectAnimator result = new ObjectAnimator();

        result.setTarget(target);
        result.setPropertyName(propertyName);
        result.setIntValues(values);
        RobolectricShadowOfLevel16.shadowOf(result).setAnimationType(int.class);

        getAnimatorMapFor(target).put(propertyName, result);
        return result;
    }

    private static Map<String, ObjectAnimator> getAnimatorMapFor(Object target) {
        Map<String, ObjectAnimator> result = mapsForAnimationTargets.get(target);
        if (result == null) {
            result = new HashMap<String, ObjectAnimator>();
            mapsForAnimationTargets.put(target, result);
        }
        return result;
    }

    private void setAnimationType(Class<?> type) {
        animationType = type;
    }

    @Implementation
    public void setTarget(Object target) {
        this.target = target;
    }

    @Implementation
    public Object getTarget() {
        return target;
    }

    @Implementation
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    @Implementation
    public String getPropertyName() {
        return propertyName;
    }

    @Implementation
    public void setFloatValues(float... values) {
        this.floatValues = values;
    }

    @Implementation
    public void setIntValues(int... values) {
        this.intValues = values;
    }

    @Implementation
    public ObjectAnimator setDuration(long duration) {
        this.duration = duration;
        return realObject;
    }

    @Implementation
    public void start() {
        isRunning = true;
        String methodName = "set" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
        final Method setter;
        notifyStart();
        try {
            setter = target.getClass().getMethod(methodName, animationType);
            if (animationType == float.class) {
                setter.invoke(target, floatValues[0]);
            } else if (animationType == int.class) {
                setter.invoke(target, intValues[0]);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                isRunning = false;
                try {
                    if (animationType == float.class) {
                        setter.invoke(target, floatValues[floatValues.length - 1]);
                    } else if (animationType == int.class) {
                        setter.invoke(target, intValues[intValues.length - 1]);
                    }
                    if (pausingEndNotifications) {
                        pausedEndNotifications.add(ShadowObjectAnimator.this);
                    } else {
                        notifyEnd();
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }, duration);
    }

    @Implementation
    public boolean isRunning() {
        return isRunning;
    }

    @Implementation
    public void cancel() {
        cancelWasCalled = true;
    }

    public boolean cancelWasCalled() {
        return cancelWasCalled;
    }

    public void resetCancelWasCalled() {
        cancelWasCalled = false;
    }

    public static Map<String, ObjectAnimator> getAnimatorsFor(Object target) {
        return getAnimatorMapFor(target);
    }

    public static void pauseEndNotifications() {
        pausingEndNotifications = true;
    }

    public static void unpauseEndNotifications() {
        while (pausedEndNotifications.size() > 0) {
            pausedEndNotifications.remove(0).notifyEnd();
        }
        pausingEndNotifications = false;
    }
}
