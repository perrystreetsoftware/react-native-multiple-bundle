package varunon9.me.reactnativemultiplebundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactRootView;
import com.facebook.react.bridge.CatalystInstance;
import com.facebook.react.bridge.CatalystInstanceImpl;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.soloader.SoLoader;

public class MultiBundleRnAppActivity extends AppCompatActivity implements DefaultHardwareBackBtnHandler {

    private ReactRootView mReactRootView;
    private ReactRootView mReactRootView2;

    // from SingletonReactInstanceManager
    private ReactInstanceManager mReactInstanceManager;
    private FrameLayout container1;
    private FrameLayout container2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_bundle);
        container1 = findViewById(R.id.container1);
        container2 = findViewById(R.id.container2);
        loadReactNativeApp();
    }

    @Override
    public void invokeDefaultOnBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mReactInstanceManager != null) {
            mReactInstanceManager.onHostPause(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mReactInstanceManager != null) {
            mReactInstanceManager.onHostResume(this, this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mReactInstanceManager != null) {
            mReactInstanceManager.onHostDestroy(this);
        }
        if (mReactRootView != null) {
            mReactRootView.unmountReactApplication();
        }
    }

    @Override
    public void onBackPressed() {
        if (mReactInstanceManager != null) {
            mReactInstanceManager.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    private void loadReactNativeApp() {
        SoLoader.init(this, false);

        System.out.println("loading Multi Bundle RN app");

        mReactRootView = new ReactRootView(this);
        mReactRootView2 = new ReactRootView(this);

        // Boot business Javascript bundle
        mReactInstanceManager = SingletonReactInstanceManager.getReactInstanceManager(this);
        if (mReactInstanceManager.hasStartedCreatingInitialContext()) {
            ReactContext reactContext = mReactInstanceManager.getCurrentReactContext();
            try {
                CatalystInstance catalyst = reactContext.getCatalystInstance();

                // The string here (e.g. "MultiBundleRnApp") has to match
                // the string in AppRegistry.registerComponent() in business.js

                // NOTE: This code displays two distinct react native apps at the same time
                catalyst.loadScriptFromAssets(reactContext.getAssets(), "assets://business.android.hermes.bundle", true);
                mReactRootView.startReactApplication(mReactInstanceManager, "MultiBundleRnApp", null);
                mReactRootView2.startReactApplication(mReactInstanceManager, "COMMON", null);

                // Why does this not work?
                // com.facebook.react.common.JavascriptException: Invariant Violation: "SingleBundleRnApp" has not been registered.
//                catalyst.loadScriptFromAssets(reactContext.getAssets(), "assets://index.android.bundle", true);
//                mReactRootView.startReactApplication(mReactInstanceManager, "SingleBundleRnApp", null);
//                mReactRootView2.startReactApplication(mReactInstanceManager, "COMMON", null);

                container1.addView(mReactRootView);
                container2.addView(mReactRootView2);
//                setContentView(mReactRootView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}