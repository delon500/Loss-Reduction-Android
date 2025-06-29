package com.example.lossreductionsystemandroid;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.progressindicator.CircularProgressIndicator;

public class SplashActivity extends AppCompatActivity {

    // Constants
    private static final long SPLASH_DISPLAY_LENGTH = 3500; // 3.5 seconds
    private static final long ANIMATION_DURATION = 1200;
    private static final String PREFS_NAME = "AppPrefs";
    private static final String FIRST_LAUNCH_KEY = "isFirstLaunch";
    private static final String LOGIN_KEY = "isLoggedIn";

    // UI Components
    private ImageView logoImageView;
    private TextView appNameTextView;
    private TextView taglineTextView;
    private TextView versionTextView;
    private TextView brandingTextView;
    private CircularProgressIndicator progressIndicator;

    // Animation and timing
    private Handler splashHandler;
    private Runnable splashRunnable;
    private boolean isDestroyed = false;
    private AnimatorSet mainAnimatorSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup status bar
        setupStatusBar();

        setContentView(R.layout.activity_splash);

        // Initialize views
        initViews();

        // Setup version info
        setupVersionInfo();

        // Check if first launch
        boolean isFirstLaunch = checkFirstLaunch();

        // Start animations
        startAnimations();

        // Setup splash timer
        setupSplashTimer(isFirstLaunch);
    }

    /**
     * Setup status bar appearance
     */
    private void setupStatusBar() {
        // Make status bar transparent but keep light content
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        // Set status bar text to dark (since background is light)
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        );
    }

    /**
     * Initialize all views
     */
    private void initViews() {
        logoImageView = findViewById(R.id.imageViewLogo);
        appNameTextView = findViewById(R.id.textViewAppName);
        taglineTextView = findViewById(R.id.textViewTagline);
        versionTextView = findViewById(R.id.textViewVersion);
        brandingTextView = findViewById(R.id.textViewBranding);
        progressIndicator = findViewById(R.id.progressIndicator);

        // Set initial states for animation
        setInitialAnimationStates();
    }

    /**
     * Set initial states for smooth animations
     */
    private void setInitialAnimationStates() {
        // Set initial alpha values
        logoImageView.setAlpha(0f);
        appNameTextView.setAlpha(0f);
        taglineTextView.setAlpha(0f);
        versionTextView.setAlpha(0f);
        brandingTextView.setAlpha(0f);
        progressIndicator.setAlpha(0f);

        // Set initial transformations
        logoImageView.setScaleX(0.3f);
        logoImageView.setScaleY(0.3f);

        // Set text views initial position (slide up effect)
        appNameTextView.setTranslationY(50f);
        taglineTextView.setTranslationY(30f);

        // Hide progress indicator initially
        progressIndicator.setVisibility(View.INVISIBLE);
    }

    /**
     * Setup version information
     */
    @SuppressLint("StringFormatInvalid")
    private void setupVersionInfo() {
        try {
            String versionName = getPackageManager()
                    .getPackageInfo(getPackageName(), 0).versionName;
            versionTextView.setText(String.format(getString(R.string.version_format), versionName));
        } catch (Exception e) {
            versionTextView.setText(getString(R.string.version_format));
        }
    }

    /**
     * Check if this is the first app launch
     */
    private boolean checkFirstLaunch() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isFirstLaunch = prefs.getBoolean(FIRST_LAUNCH_KEY, true);

        if (isFirstLaunch) {
            // Mark as not first launch anymore
            prefs.edit().putBoolean(FIRST_LAUNCH_KEY, false).apply();
        }

        return isFirstLaunch;
    }

    /**
     * Start entrance animations with sophisticated timing
     */
    private void startAnimations() {
        mainAnimatorSet = new AnimatorSet();

        // Create individual animations
        AnimatorSet logoAnimations = createLogoAnimations();
        AnimatorSet textAnimations = createTextAnimations();
        AnimatorSet progressAnimations = createProgressAnimations();

        // Sequence the animation groups
        mainAnimatorSet.play(logoAnimations);
        mainAnimatorSet.play(textAnimations).after(logoAnimations).after(300);
        mainAnimatorSet.play(progressAnimations).after(textAnimations).after(500);

        // Start the main animation sequence
        mainAnimatorSet.start();
    }

    /**
     * Create logo entrance animations
     */
    private AnimatorSet createLogoAnimations() {
        AnimatorSet logoSet = new AnimatorSet();

        // Logo animations
        ObjectAnimator logoFadeIn = ObjectAnimator.ofFloat(logoImageView, "alpha", 0f, 1f);
        ObjectAnimator logoScaleX = ObjectAnimator.ofFloat(logoImageView, "scaleX", 0.3f, 1.1f, 1f);
        ObjectAnimator logoScaleY = ObjectAnimator.ofFloat(logoImageView, "scaleY", 0.3f, 1.1f, 1f);

        // Branding fade in (happens early)
        ObjectAnimator brandingFadeIn = ObjectAnimator.ofFloat(brandingTextView, "alpha", 0f, 1f);

        // Set durations and interpolators
        logoFadeIn.setDuration(ANIMATION_DURATION);
        logoScaleX.setDuration(ANIMATION_DURATION);
        logoScaleY.setDuration(ANIMATION_DURATION);
        brandingFadeIn.setDuration(800);

        // Use bounce interpolator for logo scale
        BounceInterpolator bounceInterpolator = new BounceInterpolator();
        logoScaleX.setInterpolator(bounceInterpolator);
        logoScaleY.setInterpolator(bounceInterpolator);

        // Play logo animations together
        logoSet.playTogether(logoFadeIn, logoScaleX, logoScaleY);
        logoSet.play(brandingFadeIn).before(logoFadeIn);

        return logoSet;
    }

    /**
     * Create text entrance animations
     */
    private AnimatorSet createTextAnimations() {
        AnimatorSet textSet = new AnimatorSet();

        // App name animations
        ObjectAnimator appNameFadeIn = ObjectAnimator.ofFloat(appNameTextView, "alpha", 0f, 1f);
        ObjectAnimator appNameSlideUp = ObjectAnimator.ofFloat(appNameTextView, "translationY", 50f, 0f);

        // Tagline animations
        ObjectAnimator taglineFadeIn = ObjectAnimator.ofFloat(taglineTextView, "alpha", 0f, 1f);
        ObjectAnimator taglineSlideUp = ObjectAnimator.ofFloat(taglineTextView, "translationY", 30f, 0f);

        // Version animations
        ObjectAnimator versionFadeIn = ObjectAnimator.ofFloat(versionTextView, "alpha", 0f, 1f);

        // Set durations
        appNameFadeIn.setDuration(800);
        appNameSlideUp.setDuration(800);
        taglineFadeIn.setDuration(800);
        taglineSlideUp.setDuration(800);
        versionFadeIn.setDuration(600);

        // Set interpolator
        AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
        appNameFadeIn.setInterpolator(interpolator);
        appNameSlideUp.setInterpolator(interpolator);
        taglineFadeIn.setInterpolator(interpolator);
        taglineSlideUp.setInterpolator(interpolator);

        // Sequence text animations
        AnimatorSet appNameSet = new AnimatorSet();
        appNameSet.playTogether(appNameFadeIn, appNameSlideUp);

        AnimatorSet taglineSet = new AnimatorSet();
        taglineSet.playTogether(taglineFadeIn, taglineSlideUp);

        textSet.play(appNameSet);
        textSet.play(taglineSet).after(appNameSet).after(200);
        textSet.play(versionFadeIn).with(taglineSet);

        return textSet;
    }

    /**
     * Create progress indicator animations
     */
    private AnimatorSet createProgressAnimations() {
        AnimatorSet progressSet = new AnimatorSet();

        // Show progress indicator
        ObjectAnimator progressFadeIn = ObjectAnimator.ofFloat(progressIndicator, "alpha", 0f, 1f);
        progressFadeIn.setDuration(500);

        progressFadeIn.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                progressIndicator.setVisibility(View.VISIBLE);
            }
        });

        progressSet.play(progressFadeIn);
        return progressSet;
    }

    /**
     * Setup splash screen timer
     */
    private void setupSplashTimer(boolean isFirstLaunch) {
        splashHandler = new Handler(Looper.getMainLooper());

        // Extend splash time for first launch
        long displayLength = isFirstLaunch ? SPLASH_DISPLAY_LENGTH + 1000 : SPLASH_DISPLAY_LENGTH;

        splashRunnable = () -> {
            if (!isDestroyed) {
                navigateToNextActivity();
            }
        };

        splashHandler.postDelayed(splashRunnable, displayLength);
    }

    /**
     * Navigate to the next activity with exit animation
     */
    private void navigateToNextActivity() {
        createExitAnimation();
    }

    /**
     * Create smooth exit animation before navigation
     */
    private void createExitAnimation() {
        AnimatorSet exitSet = new AnimatorSet();

        // Create fade out animations
        ObjectAnimator logoFadeOut = ObjectAnimator.ofFloat(logoImageView, "alpha", 1f, 0f);
        ObjectAnimator appNameFadeOut = ObjectAnimator.ofFloat(appNameTextView, "alpha", 1f, 0f);
        ObjectAnimator taglineFadeOut = ObjectAnimator.ofFloat(taglineTextView, "alpha", 1f, 0f);
        ObjectAnimator progressFadeOut = ObjectAnimator.ofFloat(progressIndicator, "alpha", 1f, 0f);
        ObjectAnimator versionFadeOut = ObjectAnimator.ofFloat(versionTextView, "alpha", 1f, 0f);
        ObjectAnimator brandingFadeOut = ObjectAnimator.ofFloat(brandingTextView, "alpha", 1f, 0f);

        // Add slight scale down to logo
        ObjectAnimator logoScaleOut = ObjectAnimator.ofFloat(logoImageView, "scaleX", 1f, 0.8f);
        ObjectAnimator logoScaleOutY = ObjectAnimator.ofFloat(logoImageView, "scaleY", 1f, 0.8f);

        // Set duration
        long exitDuration = 600;
        logoFadeOut.setDuration(exitDuration);
        appNameFadeOut.setDuration(exitDuration);
        taglineFadeOut.setDuration(exitDuration);
        progressFadeOut.setDuration(exitDuration);
        versionFadeOut.setDuration(exitDuration);
        brandingFadeOut.setDuration(exitDuration);
        logoScaleOut.setDuration(exitDuration);
        logoScaleOutY.setDuration(exitDuration);

        // Play all exit animations together
        exitSet.playTogether(
                logoFadeOut, appNameFadeOut, taglineFadeOut,
                progressFadeOut, versionFadeOut, brandingFadeOut,
                logoScaleOut, logoScaleOutY
        );

        // Navigate when animation completes
        exitSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (!isDestroyed) {
                    proceedToMainActivity();
                }
            }
        });

        exitSet.start();
    }

    /**
     * Proceed to appropriate activity based on user state
     */
    private void proceedToMainActivity() {
        try {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            boolean isLoggedIn = prefs.getBoolean(LOGIN_KEY, false);

            Intent intent;
            if (isLoggedIn) {
                // User is logged in, go to main activity
                intent = new Intent(SplashActivity.this, MainActivity.class);
            } else {
                // User needs to login, go to login activity
                intent = new Intent(SplashActivity.this, MainActivity.class);
            }

            startActivity(intent);

            // Apply smooth transition
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();

        } catch (Exception e) {
            // Fallback navigation
            Intent fallbackIntent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(fallbackIntent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroyed = true;

        // Clean up animations
        if (mainAnimatorSet != null && mainAnimatorSet.isRunning()) {
            mainAnimatorSet.cancel();
        }

        // Clean up handler
        if (splashHandler != null && splashRunnable != null) {
            splashHandler.removeCallbacks(splashRunnable);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        // Pause animations if the activity goes to background
        if (mainAnimatorSet != null && mainAnimatorSet.isRunning()) {
            mainAnimatorSet.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Resume animations if they were paused
        if (mainAnimatorSet != null && mainAnimatorSet.isPaused()) {
            mainAnimatorSet.resume();
        }
    }
}