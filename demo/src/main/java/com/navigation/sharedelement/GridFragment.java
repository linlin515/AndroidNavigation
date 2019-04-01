package com.navigation.sharedelement;

import android.os.Build;
import android.os.Bundle;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.navigation.BaseFragment;
import com.navigation.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Displays a grid of pictures
 *
 * @author bherbst
 */
public class GridFragment extends BaseFragment implements KittenClickListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_grid, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.setAdapter(new KittenGridAdapter(6, this));
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
    }

    @Override
    public void onKittenClicked(KittenViewHolder holder, int position) {
        int kittenNumber = (position % 6) + 1;

        DetailsFragment kittenDetails = DetailsFragment.newInstance(kittenNumber);

        // Note that we need the API version check here because the actual transition classes (e.g. Fade)
        // are not in the support library and are only available in API 21+. The methods we are calling on the Fragment
        // ARE available in the support library (though they don't do anything on API < 21)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            kittenDetails.setSharedElementEnterTransition(new DetailsTransition());
            kittenDetails.setEnterTransition(new Fade());
            setExitTransition(new Fade());
            kittenDetails.setSharedElementReturnTransition(new DetailsTransition());
        }

        requireFragmentManager()
                .beginTransaction()
                .setReorderingAllowed(true)
                .addSharedElement(holder.image, "kittenImage")
                .hide(this)
                .add(R.id.navigation_content, kittenDetails, kittenDetails.getSceneId())
                .addToBackStack(kittenDetails.getSceneId()/*important*/)
                .commit();
    }

}
