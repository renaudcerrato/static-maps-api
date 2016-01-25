package com.mypopsy.staticmaps.demo.ui;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.mypopsy.maps.StaticMap;
import com.mypopsy.maps.StaticMap.GeoPoint;
import com.mypopsy.maps.StaticMap.Marker;
import com.mypopsy.maps.StaticMap.Path;
import com.mypopsy.staticmaps.demo.R;
import com.mypopsy.staticmaps.demo.adapter.BindableViewHolder;
import com.mypopsy.staticmaps.demo.adapter.BindingArrayRecyclerAdapter;
import com.mypopsy.staticmaps.demo.app.RecyclerViewFragment;
import com.mypopsy.staticmaps.demo.utils.PaddingItemDecoration;

import butterknife.Bind;
import butterknife.BindDimen;
import butterknife.ButterKnife;

import static com.mypopsy.maps.StaticMap.Marker.Style.BLUE;
import static com.mypopsy.maps.StaticMap.Marker.Style.GREEN;
import static com.mypopsy.maps.StaticMap.Marker.Style.ORANGE;
import static com.mypopsy.maps.StaticMap.Marker.Style.PURPLE;
import static com.mypopsy.maps.StaticMap.Marker.Style.RED;
import static com.mypopsy.maps.StaticMap.Type.HYBRID;
import static com.mypopsy.maps.StaticMap.Type.ROADMAP;
import static com.mypopsy.maps.StaticMap.Type.SATELLITE;

public class DemoFragment extends RecyclerViewFragment {

    private static final String BASE_MARKER_URL = "http://raw.githubusercontent.com/renaudcerrato/static-maps-api/master/app/src/main/res/drawable/";
    private static final String ICON_GOOGLE = BASE_MARKER_URL + "google.png";
    private static final String ICON_NETFLIX = BASE_MARKER_URL + "netflix.png";
    private static final String ICON_FACEBOOK = BASE_MARKER_URL + "facebook.png";

    private final Model[] models = new Model[] {
            new Model("Paris", "5 most visited monuments",
                    new StaticMap()
                            .marker(BLUE, new GeoPoint("Tour Eiffel"))
                            .marker(RED, new GeoPoint("Cathédrale Notre Dame"))
                            .marker(GREEN, new GeoPoint("Sacré-Coeur, 75018"))
                            .marker(ORANGE, new GeoPoint("Musée du Louvre"))
                            .marker(PURPLE, new GeoPoint("Arc de Triomphe"))
            ),
            new Model("New-York City", "GhostBusters shooting locations",
                    new StaticMap()
                            .marker(BLUE.toBuilder().label('F').build(), new GeoPoint(40.7195532,-74.0067987, "Firehouse"))
                            .marker(RED.toBuilder().label('L').build(), new GeoPoint(40.7529234,-73.9827515, "Public Library"))
                            .marker(GREEN.toBuilder().label('C').build(), new GeoPoint("Columbia University, NYC"))
            ),
            new Model("Silicon Valley", "Popular Headquarters",
                    new StaticMap()
                            .marker(Marker.Style.builder().icon(ICON_FACEBOOK).build(), new GeoPoint("1 Hacker Way, Menlo Park, CA"))
                            .marker(Marker.Style.builder().icon(ICON_NETFLIX).build(), new GeoPoint("100 Winchester Cir, Los Gatos, CA"))
                            .marker(Marker.Style.builder().icon(ICON_GOOGLE).build(), new GeoPoint("1600 Amphitheatre Pkwy, Mountain View, CA"))
            ),
            new Model("Bermuda Triangle", "Brrrrr.",
                    new StaticMap()
                            .path(Path.Style.builder().color(Color.TRANSPARENT).fill(0x66FFFF00).build(),
                                    new GeoPoint("Miami, Florida"),
                                    new GeoPoint("San Juan, Puerto Rico"),
                                    new GeoPoint("Bermuda Island"))
            ),
    };

    @BindDimen(R.dimen.activity_vertical_margin)
    int mVerticalPadding;

    @BindDimen(R.dimen.activity_horizontal_margin)
    int mHorizonralPadding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_demo, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Adapter adapter = new Adapter();
        adapter.addAll(models);

        RecyclerView recyclerView = getRecyclerView();
        recyclerView.addItemDecoration(new PaddingItemDecoration(
                mHorizonralPadding/2, mVerticalPadding/2, mVerticalPadding/2, mHorizonralPadding/2));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    static class Model {
        final CharSequence title;
        final CharSequence description;
        final StaticMap map;

        Model(CharSequence title, CharSequence description, StaticMap map) {
            this.title = title;
            this.description = description;
            this.map = map;
        }
    }

    private class Adapter extends BindingArrayRecyclerAdapter<Model> {

        @Override
        public BindableViewHolder<Model> onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ModelViewHolder(parent);
        }
    }

    class ModelViewHolder extends BindableViewHolder<Model> implements Toolbar.OnMenuItemClickListener, RequestListener<StaticMap, GlideDrawable> {

        private final Drawable PLACEHOLDER = new ColorDrawable(0xffadcaff);

        @Bind(R.id.toolbar) Toolbar toolbar;
        @Bind(R.id.map)  ImageView image;
        @Bind(R.id.progressBar) View progress;

        private Model model;

        public ModelViewHolder(ViewGroup parent) {
            this(LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_demo, parent, false));
        }

        public ModelViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            toolbar.inflateMenu(R.menu.listitem);
            toolbar.setOnMenuItemClickListener(this);
        }

        @Override
        protected void bind(Model model) {
            this.model = model;
            toolbar.setTitle(model.title);
            toolbar.setSubtitle(model.description);
            progress.setVisibility(View.VISIBLE);

            Glide.with(DemoFragment.this).load(model.map)
                    .placeholder(PLACEHOLDER)
                    .error(R.drawable.frown_cloud)
                    .listener(this)
                    .into(image);

            switch(model.map.type()) {
                case ROADMAP: toolbar.getMenu().findItem(R.id.menu_roadmap).setChecked(true); break;
                case SATELLITE: toolbar.getMenu().findItem(R.id.menu_satellite).setChecked(true); break;
                case HYBRID: toolbar.getMenu().findItem(R.id.menu_hybrid).setChecked(true); break;
            }
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch(item.getItemId()) {
                case R.id.menu_roadmap: model.map.type(ROADMAP); break;
                case R.id.menu_satellite: model.map.type(SATELLITE); break;
                case R.id.menu_hybrid: model.map.type(HYBRID); break;
            }
            getRecyclerView().getAdapter().notifyItemChanged(getAdapterPosition());
            return true;
        }

        @Override
        public boolean onException(Exception e, StaticMap model, Target<GlideDrawable> target, boolean isFirstResource) {
            progress.setVisibility(View.GONE);
            return false;
        }

        @Override
        public boolean onResourceReady(GlideDrawable resource, StaticMap model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
            progress.setVisibility(View.GONE);
            return false;
        }
    }
}
