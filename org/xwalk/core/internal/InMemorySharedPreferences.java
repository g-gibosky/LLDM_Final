package org.xwalk.core.internal;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

class InMemorySharedPreferences implements SharedPreferences {
    private final Map<String, Object> mData;

    private class InMemoryEditor implements Editor {
        private volatile boolean mApplyCalled;
        private final Map<String, Object> mChanges;
        private boolean mClearCalled;

        private InMemoryEditor() {
            this.mChanges = new HashMap();
        }

        public Editor putString(String key, String value) {
            synchronized (this.mChanges) {
                if (this.mApplyCalled) {
                    throw new IllegalStateException();
                }
                this.mChanges.put(key, value);
            }
            return this;
        }

        public Editor putStringSet(String key, Set<String> values) {
            synchronized (this.mChanges) {
                if (this.mApplyCalled) {
                    throw new IllegalStateException();
                }
                this.mChanges.put(key, values);
            }
            return this;
        }

        public Editor putInt(String key, int value) {
            synchronized (this.mChanges) {
                if (this.mApplyCalled) {
                    throw new IllegalStateException();
                }
                this.mChanges.put(key, Integer.valueOf(value));
            }
            return this;
        }

        public Editor putLong(String key, long value) {
            synchronized (this.mChanges) {
                if (this.mApplyCalled) {
                    throw new IllegalStateException();
                }
                this.mChanges.put(key, Long.valueOf(value));
            }
            return this;
        }

        public Editor putFloat(String key, float value) {
            synchronized (this.mChanges) {
                if (this.mApplyCalled) {
                    throw new IllegalStateException();
                }
                this.mChanges.put(key, Float.valueOf(value));
            }
            return this;
        }

        public Editor putBoolean(String key, boolean value) {
            synchronized (this.mChanges) {
                if (this.mApplyCalled) {
                    throw new IllegalStateException();
                }
                this.mChanges.put(key, Boolean.valueOf(value));
            }
            return this;
        }

        public Editor remove(String key) {
            synchronized (this.mChanges) {
                if (this.mApplyCalled) {
                    throw new IllegalStateException();
                }
                this.mChanges.put(key, this);
            }
            return this;
        }

        public Editor clear() {
            synchronized (this.mChanges) {
                if (this.mApplyCalled) {
                    throw new IllegalStateException();
                }
                this.mClearCalled = true;
            }
            return this;
        }

        public boolean commit() {
            apply();
            return true;
        }

        public void apply() {
            synchronized (InMemorySharedPreferences.this.mData) {
                synchronized (this.mChanges) {
                    if (this.mApplyCalled) {
                        throw new IllegalStateException();
                    }
                    if (this.mClearCalled) {
                        InMemorySharedPreferences.this.mData.clear();
                    }
                    for (Entry<String, Object> entry : this.mChanges.entrySet()) {
                        String key = (String) entry.getKey();
                        InMemoryEditor value = entry.getValue();
                        if (value == this) {
                            InMemorySharedPreferences.this.mData.remove(key);
                        } else {
                            InMemorySharedPreferences.this.mData.put(key, value);
                        }
                    }
                    this.mApplyCalled = true;
                }
            }
        }
    }

    public InMemorySharedPreferences() {
        this.mData = new HashMap();
    }

    public InMemorySharedPreferences(Map<String, Object> data) {
        this.mData = data;
    }

    public Map<String, ?> getAll() {
        Map<String, ?> unmodifiableMap;
        synchronized (this.mData) {
            unmodifiableMap = Collections.unmodifiableMap(this.mData);
        }
        return unmodifiableMap;
    }

    public String getString(String key, String defValue) {
        synchronized (this.mData) {
            if (this.mData.containsKey(key)) {
                String str = (String) this.mData.get(key);
                return str;
            }
            return defValue;
        }
    }

    public Set<String> getStringSet(String key, Set<String> set) {
        synchronized (this.mData) {
            if (this.mData.containsKey(key)) {
                set = Collections.unmodifiableSet((Set) this.mData.get(key));
            }
        }
        return set;
    }

    public int getInt(String key, int defValue) {
        synchronized (this.mData) {
            if (this.mData.containsKey(key)) {
                defValue = ((Integer) this.mData.get(key)).intValue();
            }
        }
        return defValue;
    }

    public long getLong(String key, long defValue) {
        synchronized (this.mData) {
            if (this.mData.containsKey(key)) {
                defValue = ((Long) this.mData.get(key)).longValue();
            }
        }
        return defValue;
    }

    public float getFloat(String key, float defValue) {
        synchronized (this.mData) {
            if (this.mData.containsKey(key)) {
                defValue = ((Float) this.mData.get(key)).floatValue();
            }
        }
        return defValue;
    }

    public boolean getBoolean(String key, boolean defValue) {
        synchronized (this.mData) {
            if (this.mData.containsKey(key)) {
                defValue = ((Boolean) this.mData.get(key)).booleanValue();
            }
        }
        return defValue;
    }

    public boolean contains(String key) {
        boolean containsKey;
        synchronized (this.mData) {
            containsKey = this.mData.containsKey(key);
        }
        return containsKey;
    }

    public Editor edit() {
        return new InMemoryEditor();
    }

    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        throw new UnsupportedOperationException();
    }

    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        throw new UnsupportedOperationException();
    }
}
