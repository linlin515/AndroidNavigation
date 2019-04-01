package me.listenzz.navigation;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.Window;

import java.util.ArrayList;
import java.util.List;

public abstract class AwesomeActivity extends AppCompatActivity implements PresentableActivity {

    public static final String TAG = "Navigation";

    private static final String SAVED_STATE_STATUS_BAR_TRANSLUCENT = "saved_state_status_bar_translucent";

    private LifecycleDelegate lifecycleDelegate = new LifecycleDelegate(this);

    private Style style;

    private boolean statusBarTranslucent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        style = new Style(this);
        onCustomStyle(style);

        if (savedInstanceState != null) {
            statusBarTranslucent = savedInstanceState.getBoolean(SAVED_STATE_STATUS_BAR_TRANSLUCENT);
            AppUtils.setStatusBarTranslucent(getWindow(), statusBarTranslucent);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_STATE_STATUS_BAR_TRANSLUCENT, statusBarTranslucent);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        int count = fragmentManager.getBackStackEntryCount();
        if (count > 0) {
            FragmentManager.BackStackEntry entry = fragmentManager.getBackStackEntryAt(count - 1);
            AwesomeFragment fragment = (AwesomeFragment) fragmentManager.findFragmentByTag(entry.getName());
            if (fragment != null && fragment.isAdded() && !fragment.dispatchBackPressed()) {
                if (count == 1) {
                    if (!handleBackPressed()) {
                        ActivityCompat.finishAfterTransition(this);
                    }
                } else {
                    dismissFragment(fragment);
                }
            }
        } else {
            super.onBackPressed();
        }
    }

    protected boolean handleBackPressed() {
        return false;
    }

    @Override
    public void presentFragment(@NonNull final AwesomeFragment fragment) {
        scheduleTaskAtStarted(new Runnable() {
            @Override
            public void run() {
                presentFragmentInternal(fragment);
            }
        }, true);
    }

    private void presentFragmentInternal(AwesomeFragment fragment) {
        FragmentHelper.addFragmentToBackStack(getSupportFragmentManager(), android.R.id.content, fragment, PresentAnimation.Modal);
    }

    @Override
    public void dismissFragment(@NonNull final AwesomeFragment fragment) {
        scheduleTaskAtStarted(new Runnable() {
            @Override
            public void run() {
                dismissFragmentInternal(fragment);
            }
        }, true);
    }

    private void dismissFragmentInternal(AwesomeFragment fragment) {
        if (!fragment.isAdded()) {
            return;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentHelper.executePendingTransactionsSafe(fragmentManager);

        AwesomeFragment top = (AwesomeFragment) fragmentManager.findFragmentById(android.R.id.content);
        if (top == null) {
            return;
        }
        top.setAnimation(PresentAnimation.Modal);
        AwesomeFragment presented = getPresentedFragment(fragment);
        if (presented != null) {
            fragment.setAnimation(PresentAnimation.Modal);
            top.setUserVisibleHint(false);
            getSupportFragmentManager().popBackStack(presented.getSceneId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            FragmentHelper.executePendingTransactionsSafe(getSupportFragmentManager());
            fragment.onFragmentResult(top.getRequestCode(), top.getResultCode(), top.getResultData());
        } else {
            AwesomeFragment presenting = getPresentingFragment(fragment);
            if (presenting != null) {
                presenting.setAnimation(PresentAnimation.Modal);
            }
            fragment.setUserVisibleHint(false);
            if (presenting == null) {
                ActivityCompat.finishAfterTransition(this);
            } else {
                fragmentManager.popBackStack(fragment.getSceneId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                FragmentHelper.executePendingTransactionsSafe(fragmentManager);
                presenting.onFragmentResult(fragment.getRequestCode(), fragment.getResultCode(), fragment.getResultData());
            }
        }
    }

    @Override
    public AwesomeFragment getPresentedFragment(@NonNull AwesomeFragment fragment) {
        return FragmentHelper.getLatterFragment(getSupportFragmentManager(), fragment);
    }

    @Override
    public AwesomeFragment getPresentingFragment(@NonNull AwesomeFragment fragment) {
        return FragmentHelper.getAheadFragment(getSupportFragmentManager(), fragment);
    }

    public void showDialog(@NonNull final AwesomeFragment dialog, final int requestCode) {
        scheduleTaskAtStarted(new Runnable() {
            @Override
            public void run() {
                showDialogInternal(dialog, requestCode);
            }
        });
    }

    private void showDialogInternal(AwesomeFragment dialog, int requestCode) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            Fragment fragment = fragmentManager.findFragmentById(android.R.id.content);
            if (fragment != null && fragment.isAdded()) {
                dialog.setTargetFragment(fragment, requestCode);
            }
        }
        dialog.show(fragmentManager, dialog.getSceneId());
    }

    @Override
    @NonNull
    public Style getStyle() {
        return style;
    }

    protected void onCustomStyle(@NonNull Style style) {

    }

    @Override
    public void setActivityRootFragment(@NonNull final AwesomeFragment rootFragment) {
        scheduleTaskAtStarted(new Runnable() {
            @Override
            public void run() {
                setRootFragmentInternal(rootFragment);
            }
        });
    }

    private void setRootFragmentInternal(AwesomeFragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        int count = fragmentManager.getBackStackEntryCount();
        if (count > 0) {
            String tag = fragmentManager.getBackStackEntryAt(0).getName();
            AwesomeFragment former = (AwesomeFragment) fragmentManager.findFragmentByTag(tag);
            if (former != null && former.isAdded()) {
                former.setAnimation(PresentAnimation.Fade);
                fragmentManager.popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                hasFormerRoot = true;
            }
        }

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setReorderingAllowed(true);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragment.setAnimation(PresentAnimation.None);
        transaction.add(android.R.id.content, fragment, fragment.getSceneId());
        transaction.addToBackStack(fragment.getSceneId());
        transaction.commit();
    }

    public void clearFragments() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        int count = fragmentManager.getBackStackEntryCount();
        if (count > 0) {
            getWindow().setBackgroundDrawable(new ColorDrawable(style.getScreenBackgroundColor()));

            String tag = fragmentManager.getBackStackEntryAt(0).getName();
            AwesomeFragment former = (AwesomeFragment) fragmentManager.findFragmentByTag(tag);
            if (former != null && former.isAdded()) {
                former.setAnimation(PresentAnimation.Fade);
                fragmentManager.popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        }
    }

    private boolean hasFormerRoot;

    @Override
    public boolean activityHasFormerRoot() {
        return hasFormerRoot;
    }

    @Override
    public void setStatusBarTranslucent(boolean translucent) {
        if (statusBarTranslucent != translucent) {
            statusBarTranslucent = translucent;
            AppUtils.setStatusBarTranslucent(getWindow(), translucent);
            onStatusBarTranslucentChanged(translucent);
        }
    }

    @Override
    public boolean isStatusBarTranslucent() {
        return statusBarTranslucent;
    }

    protected void onStatusBarTranslucentChanged(boolean translucent) {
        List<AwesomeFragment> children = getFragmentsAtAddedList();
        for (int i = 0, size = children.size(); i < size; i++) {
            AwesomeFragment child = children.get(i);
            child.onStatusBarTranslucentChanged(translucent);
        }
    }

    public List<AwesomeFragment> getFragmentsAtAddedList() {
        List<AwesomeFragment> children = new ArrayList<>();
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (int i = 0, size = fragments.size(); i < size; i++) {
            Fragment fragment = fragments.get(i);
            if (fragment instanceof AwesomeFragment) {
                children.add((AwesomeFragment) fragment);
            }
        }
        return children;
    }

    @Nullable
    public DialogFragment getDialogFragment() {
        return FragmentHelper.getDialogFragment(getSupportFragmentManager());
    }

    public Window getCurrentWindow() {
        DialogFragment dialogFragment = getDialogFragment();
        if (dialogFragment != null && dialogFragment.isAdded()) {
            return dialogFragment.getDialog().getWindow();
        } else {
            return getWindow();
        }
    }

    protected void scheduleTaskAtStarted(Runnable runnable) {
        scheduleTaskAtStarted(runnable, false);
    }

    protected void scheduleTaskAtStarted(Runnable runnable, boolean deferred) {
        lifecycleDelegate.scheduleTaskAtStarted(runnable, deferred);
    }

}
