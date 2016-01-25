package com.mypopsy.staticmaps.demo.ui;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;
import com.mypopsy.maps.StaticMap;
import com.mypopsy.maps.StaticMap.GeoPoint;
import com.mypopsy.maps.StaticMap.Marker;
import com.mypopsy.maps.StaticMap.MarkerGroup;
import com.mypopsy.maps.StaticMap.Path;
import com.mypopsy.staticmaps.demo.R;
import com.mypopsy.staticmaps.demo.adapter.BindableViewHolder;
import com.mypopsy.staticmaps.demo.adapter.BindingArrayRecyclerAdapter;
import com.mypopsy.staticmaps.demo.app.RecyclerViewFragment;
import com.mypopsy.staticmaps.demo.glide.CropCircleTransformation;
import com.mypopsy.staticmaps.demo.utils.PaddingItemDecoration;

import java.util.List;

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
    private static final String ICON_GHOSTBUSTER = BASE_MARKER_URL + "ghostbuster.png";

    private final Model[] models = new Model[] {
            new Model("Paris", "5 most visited monuments",
                    new GeoPoint("France"),
                    new StaticMap()
                            .marker(BLUE, new GeoPoint("Tour Eiffel"))
                            .marker(RED, new GeoPoint("Cathédrale Notre Dame"))
                            .marker(GREEN, new GeoPoint("Sacré-Coeur, 75018"))
                            .marker(ORANGE, new GeoPoint("Musée du Louvre"))
                            .marker(PURPLE, new GeoPoint("Arc de Triomphe"))
            ),
            new Model("New-York City", "Ghostbusters filming locations",
                    new GeoPoint("Manhattan, NY"),
                    new StaticMap()
                            .marker(Marker.Style.builder().icon(ICON_GHOSTBUSTER).build(), new GeoPoint(40.7195532,-74.0067987, "Firehouse"))
                            .marker(RED.toBuilder().label('A').build(), new GeoPoint(40.7529234,-73.9827515, "Public Library"))
                            .marker(GREEN.toBuilder().label('B').build(), new GeoPoint("Columbia University, NYC"))
            ),
            new Model("Silicon Valley", "Popular Headquarters",
                    new GeoPoint("California, USA"),
                    new StaticMap()
                            .marker(Marker.Style.builder().icon(ICON_GOOGLE).build(), new GeoPoint("1600 Amphitheatre Pkwy, Mountain View, CA"))
                            .marker(Marker.Style.builder().icon(ICON_FACEBOOK).build(), new GeoPoint("1 Hacker Way, Menlo Park, CA"))
                            .marker(Marker.Style.builder().icon(ICON_NETFLIX).build(), new GeoPoint("100 Winchester Cir, Los Gatos, CA"))
            ),
            new Model("Bermuda Triangle", "Brrrrr.",
                    null,
                    new StaticMap()
                            .path(Path.Style.builder().color(Color.TRANSPARENT).fill(0x66ff0000).build(),
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
        final GeoPoint icon;

        Model(CharSequence title, CharSequence description, GeoPoint icon, StaticMap map) {
            this.title = title;
            this.description = description;
            this.icon = icon;
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

        @Bind(R.id.toolbar)
        Toolbar toolbar;
        @Bind(R.id.map)
        ImageView image;
        @Bind(R.id.progressBar)
        View progress;
        @Bind(R.id.container_markers)
        ViewGroup markers;

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

            if(model.icon != null) {
                Glide.with(DemoFragment.this)
                        .load(new StaticMap().center(model.icon))
                        .asBitmap()
                        .transform(new CropCircleTransformation(itemView.getContext()))
                        .into(new ToolbarTarget(toolbar));
            }
            else
                toolbar.setNavigationIcon(null);

            Glide.with(DemoFragment.this).load(model.map)
                    .placeholder(PLACEHOLDER)
                    .error(R.drawable.frown_cloud)
                    .listener(this)
                    .into(image);

            switch (model.map.type()) {
                case ROADMAP:
                    toolbar.getMenu().findItem(R.id.menu_roadmap).setChecked(true);
                    break;
                case SATELLITE:
                    toolbar.getMenu().findItem(R.id.menu_satellite).setChecked(true);
                    break;
                case HYBRID:
                    toolbar.getMenu().findItem(R.id.menu_hybrid).setChecked(true);
                    break;
            }

            markers.setVisibility(View.VISIBLE);

            List<MarkerGroup> groups = model.map.markers();
            int position = 0;
            for(MarkerGroup group: groups) {
                for(GeoPoint point: group.points) {
                    TextView markerView = getMarkerView(position);
                    bindMarkerView(group.style, point, markerView);
                    position++;
                }
            }

            while(markers.getChildCount() > position)
                markers.removeViewAt(markers.getChildCount() - 1);

            if(markers.getChildCount() == 0) {
                markers.setVisibility(View.GONE);
            }
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_roadmap:
                    model.map.type(ROADMAP);
                    break;
                case R.id.menu_satellite:
                    model.map.type(SATELLITE);
                    break;
                case R.id.menu_hybrid:
                    model.map.type(HYBRID);
                    break;
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

        private TextView getMarkerView(int position) {
            if(position >= markers.getChildCount()) {
                LayoutInflater inflater = LayoutInflater.from(markers.getContext());
                TextView textView = (TextView) inflater.inflate(R.layout.layout_markers, markers, false);
                markers.addView(textView);
                return textView;
            }else
                return (TextView) markers.getChildAt(position);
        }

        private void bindMarkerView(Marker.Style style, GeoPoint point, TextView textView) {
            Character label = style.label();
            String text = point.address();

            if (text == null) {
                text = String.format("%.6f %.6f", point.latitude(), point.longitude());
            }

            if (label != null) {
                text = text + " (" + label + ")";
            }

            textView.setText(text);

            if (style.icon() != null) {
                Glide.with(DemoFragment.this).load(style.icon()).asBitmap().into(new TextViewTarget(textView));
            } else {
                Drawable d = DrawableCompat.wrap(getResources().getDrawable(R.drawable.ic_maps_marker));
                DrawableCompat.setTint(d, style.color());
                textView.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null);
            }
        }
    }

    static private class ToolbarTarget extends ViewTarget<Toolbar, Bitmap> {

        public static final int ICON_SIZE = 128; // dp

        public ToolbarTarget(Toolbar toolbar) {
            super(toolbar);
        }

        @Override
        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
            getView().setNavigationIcon(new BitmapDrawable(resource));
        }

        @Override
        public void getSize(SizeReadyCallback cb) {
            cb.onSizeReady(dpToPx(ICON_SIZE), dpToPx(ICON_SIZE));
        }
    }

    static private class TextViewTarget extends ViewTarget<TextView, Bitmap> {

        public static final int ICON_MARGIN = 4; //dp

        public TextViewTarget(TextView view) {
            super(view);
        }

        @Override
        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
            Drawable d = new BitmapDrawable(getView().getResources(), resource);
            d = new InsetDrawable(d, dpToPx(ICON_MARGIN));
            getView().setCompoundDrawablesWithIntrinsicBounds(d, null, null, null);
        }
    }

    static private int dpToPx(int dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return (int) (dp * metrics.density);
    }
}
