package me.listenzz.navigation;

import android.os.Bundle;
import android.util.Log;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


/**
 * Created by Listen on 2018/1/11.
 */

public class FragmentHelper {

    private static final String TAG = "Navigation";

    @NonNull
    public static Bundle getArguments(@NonNull Fragment fragment) {
        Bundle args = fragment.getArguments();
        if (args == null) {
            args = new Bundle();
            fragment.setArguments(args);
        }
        return args;
    }

    public static void executePendingTransactionsSafe(@NonNull FragmentManager fragmentManager) {
        try {
            fragmentManager.executePendingTransactions();
        } catch (IllegalStateException e) {
            Log.wtf(TAG, e);
        }
    }

    public static void addFragmentToBackStack(@NonNull FragmentManager fragmentManager, int containerId, @NonNull AwesomeFragment fragment, @NonNull PresentAnimation animation) {
        if (fragmentManager.isDestroyed()) {
            return;
        }
        executePendingTransactionsSafe(fragmentManager);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setReorderingAllowed(true);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        AwesomeFragment topFragment = (AwesomeFragment) fragmentManager.findFragmentById(containerId);
        if (topFragment != null && topFragment.isAdded()) {
            topFragment.setAnimation(animation);
            transaction.hide(topFragment);
        }
        fragment.setAnimation(animation);
        transaction.add(containerId, fragment, fragment.getSceneId());
        transaction.addToBackStack(fragment.getSceneId());
        transaction.commit();
    }

    public static void addFragmentToAddedList(@NonNull FragmentManager fragmentManager, int containerId, @NonNull AwesomeFragment fragment) {
        addFragmentToAddedList(fragmentManager, containerId, fragment, true);
    }

    public static void addFragmentToAddedList(@NonNull FragmentManager fragmentManager, int containerId, @NonNull AwesomeFragment fragment, boolean primary) {
        if (fragmentManager.isDestroyed()) {
            return;
        }
        executePendingTransactionsSafe(fragmentManager);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(containerId, fragment, fragment.getSceneId());
        if (primary) {
            transaction.setPrimaryNavigationFragment(fragment); // primary
        }
        transaction.commit();
    }

    @Nullable
    public static AwesomeFragment getLatterFragment(@NonNull FragmentManager fragmentManager, @NonNull AwesomeFragment fragment) {
        int count = fragmentManager.getBackStackEntryCount();
        int index = findIndexAtBackStack(fragmentManager, fragment);
        if (index > -1 && index < count - 1) {
            FragmentManager.BackStackEntry backStackEntry = fragmentManager.getBackStackEntryAt(index + 1);
            AwesomeFragment latter = (AwesomeFragment) fragmentManager.findFragmentByTag(backStackEntry.getName());
            if (latter != null && latter.isAdded()) {
                return latter;
            }
        }
        return null;
    }

    @Nullable
    public static AwesomeFragment getAheadFragment(@NonNull FragmentManager fragmentManager, @NonNull AwesomeFragment fragment) {
        int count = fragmentManager.getBackStackEntryCount();
        int index = findIndexAtBackStack(fragmentManager, fragment);
        if (index > 0 && index < count) {
            FragmentManager.BackStackEntry backStackEntry = fragmentManager.getBackStackEntryAt(index - 1);
            AwesomeFragment ahead = (AwesomeFragment) fragmentManager.findFragmentByTag(backStackEntry.getName());
            if (ahead != null && ahead.isAdded()) {
                return ahead;
            }
        }
        return null;
    }

    public static int findIndexAtBackStack(@NonNull FragmentManager fragmentManager, @NonNull AwesomeFragment fragment) {
        int count = fragmentManager.getBackStackEntryCount();
        int index = -1;
        for (int i = 0; i < count; i++) {
            FragmentManager.BackStackEntry backStackEntry = fragmentManager.getBackStackEntryAt(i);
            String tag = fragment.getTag();
            if (tag != null && tag.equals(backStackEntry.getName())) {
                index = i;
            }
        }
        return index;
    }

    @Nullable
    public static Fragment findDescendantFragment(@NonNull FragmentManager fragmentManager, @NonNull String tag) {
        Fragment target = fragmentManager.findFragmentByTag(tag);
        if (target == null) {
            List<Fragment> fragments = fragmentManager.getFragments();
            int count = fragments.size();
            for (int i = count - 1; i > -1; i--) {
                Fragment f = fragments.get(i);
                if (f.isAdded()) {
                    if (f instanceof AwesomeFragment) {
                        AwesomeFragment af = (AwesomeFragment) f;
                        if (af.getSceneId().equals(tag)) {
                            target = af;
                        }
                    }

                    if (target == null) {
                        target = findDescendantFragment(f.getChildFragmentManager(), tag);
                    }

                    if (target != null) {
                        break;
                    }
                }
            }
        }
        return target;
    }

    @Nullable
    public static DialogFragment getDialogFragment(@NonNull FragmentManager fragmentManager) {
        Fragment fragment = fragmentManager.getPrimaryNavigationFragment();

        if (fragment != null && fragment.isAdded()) {
            if (fragment instanceof DialogFragment) {
                DialogFragment dialogFragment = (DialogFragment) fragment;
                if (dialogFragment.getShowsDialog()) {
                    return dialogFragment;
                }
            }
            return getDialogFragment(fragment.getChildFragmentManager());
        }

        List<Fragment> fragments = fragmentManager.getFragments();
        int count = fragments.size();

        for (int i = count - 1; i > -1; i--) {
            fragment = fragments.get(i);
            if (fragment.isAdded()) {
                if (fragment instanceof DialogFragment) {
                    DialogFragment dialogFragment = (DialogFragment) fragment;
                    if (dialogFragment.getShowsDialog()) {
                        return dialogFragment;
                    }
                }
                return getDialogFragment(fragment.getChildFragmentManager());
            }
        }

        return null;
    }

}
