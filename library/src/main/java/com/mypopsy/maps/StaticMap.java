package com.mypopsy.maps;

import com.mypopsy.maps.internal.PolyLine;
import com.mypopsy.maps.internal.UrlBuilder;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Created by Cerrato Renaud
 * @see <a href="https://developers.google.com/maps/documentation/staticmaps">official documentation</a>
 */
public final class StaticMap {

    private static final String BASE_URL = "maps.googleapis.com/maps/api/staticmap";
    private static final String HTTP = "http://" + BASE_URL;
    private static final String HTTPS = "https://" + BASE_URL;

    public static final int DEFAULT_SCALE = 1;
    public static final Format DEFAULT_FORMAT = Format.PNG;
    public static final Type DEFAULT_TYPE = Type.ROADMAP;

    public static final int NO_WIDTH = -1;
    public static final int NO_HEIGHT = -1;
    public static final int NO_ZOOM = -1;


    /**
     * Map Types
     */
    public enum Type {
        /**
         * specifies a standard roadmap image, as is normally shown on the Google Maps website (default)
         */
        ROADMAP("roadmap"),
        /**
         * specifies a satellite image
         */
        SATELLITE("satellite"),
        /**
         * specifies a physical relief map image, showing terrain and vegetation.
         */
        TERRAIN("terrain"),
        /**
         * specifies a hybrid of the satellite and roadmap image,
         * showing a transparent layer of major streets and place names on the satellite image.
         */
        HYBRID("hybrid");

        private final String value;
        Type(String value) {
            this.value = value;
        }
    }

    /**
     * Image Format
     */
    public enum Format {
        /**
         * 8-bit PNG format (default)
         */
        PNG("png"),
        /**
         * 32-bit PNG format
         */
        PNG32("png32"),
        /**
         * GIF format
         */
        GIF("gif"),
        /**
         * JPEG format
         */
        JPG("jpg"),
        /**
         * non-progressive JPEG compression format
         */
        JPG_BASELINE("jpg-baseline");

        private final String value;
        Format(String value) {
            this.value = value;
        }
    }

    private List<MarkerGroup> markers = new ArrayList<>();
    private List<GeoPoint> visible = new ArrayList<>();
    private List<Path> paths = new ArrayList<>();

    private GeoPoint center;
    private Integer zoom;
    private Integer scale;
    private Integer width, height;
    private Format format;
    private Type type;
    private String apiKey;
    private boolean https = true;

    public StaticMap() { }

    public StaticMap(StaticMap other) {
        markers = new ArrayList<>(other.markers);
        visible = new ArrayList<>(other.visible);
        paths = new ArrayList<>(other.paths);
        center = other.center;
        zoom = other.zoom;
        scale = other.scale;
        width = other.width;
        height = other.height;
        format = other.format;
        type = other.type;
        apiKey = other.apiKey;
        https = other.https;
    }

    /**
     * Explicitely center the map on the given {@link GeoPoint geopoint}
     * @see #center(GeoPoint)
     * @param point of the center
     * @return this instance
     */
    public StaticMap center(GeoPoint point) {
        this.center = point;
        return this;
    }

    /**
     * Explicitely center the map on the given latitude/longitude
     * @see #center(GeoPoint)
     * @param latitude of the center
     * @param longitude of the center
     * @return this instance
     */
    public StaticMap center(double latitude, double longitude) {
        return center(new GeoPoint(latitude, longitude));
    }

    /**
     * @see #center(GeoPoint)
     * @param address of the center
     * @return this instance
     */
    public StaticMap center(@Nonnull String address) {
        return center(new GeoPoint(address));
    }

    /**
     * Query for the center of the map
     * @return the current center or null
     */
    @Nullable
    public GeoPoint center() {
        return center;
    }

    /**
     * @see #visible(GeoPoint...)
     * @param geoPoint to add
     * @return this instance
     */
    public StaticMap visible(GeoPoint geoPoint) {
        visible.add(geoPoint);
        return this;
    }

    /**
     * Images may specify a viewport by adding visible locations.
     * The visible parameter instructs the Google Static Maps API service to construct a map such
     * that the specified locations remain visible (this parameter may be combined with existing
     * markers or paths to define a visible region as well).
     * Defining a viewport in this manner obviates the need to specify an exact zoom level.
     * @param geoPoints to add
     * @return this instance
     */
    public StaticMap visible(GeoPoint ...geoPoints) {
        visible.addAll(Arrays.asList(geoPoints));
        return this;
    }

    /**
     * Query for any previously added visible locations
     * @see #visible(GeoPoint...)
     * @return the list of visible locations
     */
    public List<GeoPoint> visible() {
        return visible;
    }

    /**
     * Add a single {@link Marker marker} with default {@link Marker.Style style).
     * @see #marker(GeoPoint...)
     * @param latitude of the marker
     * @param longitude of the marker
     * @return this instance
     */
    public StaticMap marker(double latitude, double longitude) {
        return marker(new GeoPoint(latitude, longitude));
    }

    /**
     * Add a single {@link Marker marker} with default {@link Marker.Style style).
     * @see #marker(GeoPoint...)
     * @param address of the marker
     * @return this instance
     */
    public StaticMap marker(String address) {
        return marker(new GeoPoint(address));
    }

    /**
     * Add {@link Marker marker}(s) using default {@link Marker.Style style).
     * @see #marker(Marker.Style, GeoPoint...)
     * @param markers to add
     * @return this instance
     */
    public StaticMap marker(GeoPoint...markers) {
        return marker(null, markers);
    }

    /**
     * Add {@link Marker marker}(s) using the given {@link Marker.Style style)
     * @param style for marker(s)
     * @param markers to add
     * @return this instance
     */
    public StaticMap marker(Marker.Style style, GeoPoint...markers) {
        this.markers.add(new MarkerGroup(style, markers));
        return this;
    }

    /**
     * Query for the current markers.
     * @see #marker(Marker.Style, GeoPoint...)
     * @return the markers
     */
    public List<MarkerGroup> markers() {
        return markers;
    }

    /**
     * Add a {@link Path path} using default {@link Path.Style style)
     * @see #path(Path.Style, GeoPoint...)
     * @param path to add
     * @return this instance
     */
    public StaticMap path(Path path) {
        paths.add(path);
        return this;
    }

    /**
     * Add a {@link Path path} using default {@link Path.Style Style style)
     * @see #path(Path.Style, GeoPoint...)
     * @param points to add
     * @return this instance
     */
    public StaticMap path(GeoPoint...points) {
        path(null, points);
        return this;
    }

    /**
     * Add a {@link Path path} using the given {@link Path.Style style)
     * @param style of the path
     * @param points to add
     * @return this instance
     */
    public StaticMap path(Path.Style style, GeoPoint...points) {
        path(new Path(style, points));
        return this;
    }

    /**
     * @see #path(Path.Style, GeoPoint...)
     * @return the previously added paths
     */
    public List<Path> paths() {
        return paths;
    }

    /**
     * Set an explicit zoom level
     * @param zoom from 0 to 21+
     * @return this instance
     */
    public StaticMap zoom(int zoom) {
        this.zoom = zoom;
        return this;
    }

    /**
     * Query for the current zoom value
     * @see #zoom(int)
     * @return the current zoom or {@link #NO_ZOOM}
     */
    public int zoom() {
        if(zoom == null) return NO_ZOOM;
        return zoom;
    }

    /**
     * The scale value is multiplied with the size to determine the actual output size
     * of the image in pixels, without changing the coverage area of the map.
     * @param scale 1, 2 or 4 (the later is for Google Maps APIs Premium Plan customers only)
     * @return this instance
     */
    public StaticMap scale(int scale) {
        this.scale = scale;
        return this;
    }

    /**
     * Query for the current scale value.
     * @see #scale(int)
     * @return the current scale value
     */
    public int scale() {
        if(scale == null) return 1;
        return scale;
    }

    /**
     * The size parameter, in conjunction with center, defines the coverage area of a map.
     * It also defines the output size of the map in pixels, when multiplied
     * with the scale value (which is 1 by default).
     * @param width in pixels
     * @param height in pixels
     * @return this instance
     */
    public StaticMap size(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    /**
     * Query for the current width value
     * @see #size(int, int)
     * @return the current width or {@link #NO_WIDTH}
     */
    public int width() {
        if(width == null) return NO_WIDTH;
        return width;
    }

    /**
     * Query for the current height value
     * @see #size(int, int)
     * @return the current height or {@link #NO_HEIGHT}
     */
    public int height() {
        if(height == null) return NO_HEIGHT;
        return height;
    }

    /**
     * Images may be returned in several common web graphics formats: GIF, JPEG and PNG.
     * @see Format
     * @param format of the image
     * @return this instance
     */
    public StaticMap format(Format format) {
        this.format = format;
        return this;
    }

    /**
     * Query for the current {@link Format format}
     * @see #format(Format)
     * @return the current format
     */
    public Format format() {
        if(format == null) return Format.PNG;
        return format;
    }

    /**
     * Query for the current API Key
     * @param apiKey from your Google Developer Console
     * @return this instance
     */
    public StaticMap key(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    /**
     * Specifies the map {@link Type type}
     * @param type of the map
     * @return this instance
     */
    public StaticMap type(Type type) {
        this.type = type;
        return this;
    }

    /**
     * Query for the current map {@link Type type}
     * @see #type(Type)
     * @return the {@link Type type} of the map
     */
    public Type type() {
        if(type == null) return Type.ROADMAP;
        return type;
    }

    /**
     * Force http scheme
     * @return this instance
     */
    public StaticMap http() {
        this.https = false;
        return this;
    }

    /**
     * Force https scheme (default)
     * @return this instance
     */
    public StaticMap https() {
        this.https = true;
        return this;
    }

    /**
     * @see #https()
     * @return true if the current scheme is https
     */
    public boolean isHttps() {
        return https;
    }

    @Override
    public String toString() {

        UrlBuilder builder = new UrlBuilder(https ? HTTPS : HTTP);

        if(center != null) {
            builder.appendQuery("center", center);
        }

        if(width != null && height != null) {
            builder.appendQuery("size", width + "x" + height);
        }

        if(zoom != null) {
            builder.appendQuery("zoom", String.valueOf(zoom));
        }

        if(scale != null && scale != DEFAULT_SCALE) {
            builder.appendQuery("scale", String.valueOf(scale));
        }

        if(format != null && format != DEFAULT_FORMAT) {
            builder.appendQuery("format", format.value);
        }

        if(type != null && type != DEFAULT_TYPE) {
            builder.appendQuery("maptype", type.value);
        }

        if(apiKey != null) {
            builder.appendQuery("key", apiKey);
        }

        for(MarkerGroup markers: this.markers) {
            builder.appendQuery("markers", markers);
        }

        for(Path path: paths) {
            builder.appendQuery("path", path);
        }

        for(GeoPoint point: visible) {
            builder.appendQuery("visible", point);
        }

        return builder.toString();
    }

    public URL toURL() throws MalformedURLException {
        return new URL(toString());
    }

    public static class GeoPoint {

        final Double latitude, longitude;
        final String address;

        public GeoPoint(@Nonnull String address) {
            //noinspection ConstantConditions
            if(address == null) throw new IllegalArgumentException("address can't be null");
            this.address = address;
            this.latitude = null;
            this.longitude = null;
        }

        public GeoPoint(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.address = null;
        }

        public boolean hasCoordinates() {
            return latitude != null && longitude != null;
        }

        public double latitude() {
            return latitude;
        }

        public double longitude() {
            return longitude;
        }

        @Nullable
        public String address() {
            return address;
        }

        @Override
        public String toString() {
            if(hasCoordinates())
                return String.format("%.6f,%.6f", latitude, longitude);
            else
                return address;
        }

        @Override
        public boolean equals(Object obj) {
            if(!(obj instanceof GeoPoint)) return false;
            return hashCode() == obj.hashCode();
        }

        @Override
        public int hashCode() {
            return hash(latitude, longitude, address);
        }
    }

    public static class MarkerGroup {

        @Nullable public final Marker.Style style;
        public final GeoPoint[] points;

        MarkerGroup(@Nullable Marker.Style style, GeoPoint... points) {
            if(points == null || points.length == 0) throw new IllegalArgumentException("markers can't be empty");
            this.style = style;
            this.points = points;
        }

        @Override
        public String toString() {
            if(style == null || Marker.Style.DEFAULT.equals(style))
                return join('|', points);
            else
                return join('|', style, join('|', points));
        }

        @Override
        public boolean equals(Object obj) {
            if(!(obj instanceof MarkerGroup)) return false;
            return hashCode() == obj.hashCode();
        }

        @Override
        public int hashCode() {
            return hash(style == null ? Marker.Style.DEFAULT : style, hash(points));
        }
    }

    public static class Marker extends GeoPoint {

        public Marker(double latitude, double longitude) {
            super(latitude, longitude);
        }

        public Marker(@Nonnull String address) {
            super(address);
        }

        public static class Style {

            static public final int DEFAULT_COLOR = 0xff0000;
            static public final Size DEFAULT_SIZE = Size.NORMAL;

            static public final Style BLACK  = builder().color(0x000000).build();
            static public final Style PURPLE = builder().color(0x800080).build();
            static public final Style RED    = builder().color(0xff0000).build();
            static public final Style GREY   = builder().color(0x808080).build();
            static public final Style GREEN  = builder().color(0x00ff00).build();
            static public final Style ORANGE = builder().color(0xffa500).build();
            static public final Style YELLOW = builder().color(0xffff00).build();
            static public final Style BLUE   = builder().color(0x0000ff).build();
            static public final Style WHITE  = builder().color(0xffffff).build();

            static public final Style DEFAULT = builder().build();

            /**
             * Size of the {@link Marker marker}(s)
             */
            public enum Size {
                TINY("tiny"), MID("mid"), SMALL("small"), NORMAL(null);

                private final String value;
                Size(String value) { this.value = value; }
            }

            final String icon;
            final Integer color;
            final String label;
            final Size size;

            Style(Builder builder) {
                this.icon = builder.icon;
                this.color = builder.color;
                this.size = builder.size;
                this.label = builder.label;
            }

            @Nullable
            public String icon() {
                return icon;
            }

            public int color() {
                if(color == null) return DEFAULT_COLOR;
                return color;
            }

            @Nullable
            public String label() {
                return label;
            }

            public Size size() {
                if(size == null) return DEFAULT_SIZE;
                return size;
            }

            public Builder toBuilder() {
                return new Builder(this);
            }

            static public Builder builder() {
                return new Builder();
            }

            @Override
            public String toString() {
                return join('|',
                        icon != null ? "icon:" + icon : null,
                        size() != DEFAULT_SIZE ? "size:" + size.value : null,
                        color != null ? "color:" + rgb(color) : null,
                        label != null ? "label:" + label : null);
            }

            @Override
            public boolean equals(Object obj) {
                if(!(obj instanceof Style)) return false;
                return hashCode() == obj.hashCode();
            }

            @Override
            public int hashCode() {
                return hash(icon(), size(), color(), label());
            }

            public static class Builder {

                String icon;
                Integer color;
                String label;
                Size size;

                Builder(Style style) {
                    icon = style.icon;
                    color = style.color;
                    label = style.label;
                    size = style.size;
                }

                public Builder() {

                }

                /**
                 * @see {@link #icon(String)}
                 * @param icon url
                 * @return this instance
                 */
                public Builder icon(URL icon) {
                    return icon(icon.toString());
                }

                /**
                 * Specifies an URL to use as the marker's custom icon.
                 * Images may be in PNG, JPEG or GIF formats, though PNG is recommended.
                 * Note that the Google Static Maps API does not support custom icon URLs
                 * that use HTTPS; the default icon will be displayed.
                 * @param icon url
                 * @return this instance
                 */
                public Builder icon(String icon) {
                    this.icon = icon;
                    return this;
                }

                /**
                 * Specifies the 24-bit {@link Marker marker} color
                 * @param color RGB 24 bit
                 * @return this instance
                 */
                public Builder color(int color) {
                    this.color = color;
                    return this;
                }

                /**
                 * Specifies a single uppercase alphanumeric character from the set {A-Z, 0-9}.
                 * @param label containing exclusively the character set {A-Z, 0-9}
                 * @return this instance
                 */
                public Builder label(String label) {
                    this.label = label;
                    return this;
                }

                /**
                 * Specifies the size of marker.
                 * If no size parameter is set, the marker will appear in its default (normal) size.
                 * @see Size
                 * @param size of the marker
                 * @return this instance
                 */
                public Builder size(Size size) {
                    this.size = size;
                    return this;
                }

                public Style build() {
                    return new Style(this);
                }
            }
        }
    }

    static public class Path {

        @Nullable public final Style style;
        public final GeoPoint[] points;

        public Path(@Nullable Style style, GeoPoint...points) {
            if(points == null || points.length == 0) throw new IllegalArgumentException("you must specify geopoints");
            this.style = style;
            this.points = points;
        }

        @Override
        public String toString() {
            String path = PolyLine.encode(points);

            if(path != null) {
                path = "enc:" + path;
            }else{
                path = join('|', points);
            }

            if(style == null || Style.DEFAULT.equals(style))
                return path;
            else
                return join('|', style, path);
        }

        @Override
        public boolean equals(Object obj) {
            if(!(obj instanceof Path)) return false;
            return hashCode() == obj.hashCode();
        }

        @Override
        public int hashCode() {
            return hash(style == null ? Style.DEFAULT : style, hash(points));
        }

        static public class Style {

            public static Style DEFAULT = builder().build();

            public static final int DEFAULT_STROKE = 5;
            public static final int DEFAULT_COLOR = 0xff;
            public static final int NO_FILL_COLOR = 0;

            final Integer stroke;
            final Integer color;
            final Integer fillColor;
            final boolean geodesic;

            private Style(Builder builder) {
                this.stroke = builder.stroke;
                this.color = builder.color;
                this.fillColor = builder.fillColor;
                this.geodesic = builder.geodesic;
            }

            public int stroke() {
                if(stroke == null) return DEFAULT_STROKE;
                return stroke;
            }

            public int color() {
                if(color == null) return DEFAULT_COLOR;
                return color;
            }

            public int fillColor() {
                if(fillColor == null) return NO_FILL_COLOR;
                return fillColor;
            }

            public boolean isGeodesic() {
                return geodesic;
            }

            public Builder toBuilder() {
                return new Builder(this);
            }

            @Override
            public String toString() {
                return join('|',
                        stroke() != DEFAULT_STROKE ? "weight:" + stroke : null,
                        color() != DEFAULT_COLOR ? "color:" + rgba(color) : null,
                        fillColor() != NO_FILL_COLOR ? "fillcolor:" + rgba(fillColor) : null,
                        geodesic ? "geodesic:true" : null);
            }

            @Override
            public boolean equals(Object obj) {
                if(!(obj instanceof Style)) return false;
                return hashCode() == obj.hashCode();
            }

            @Override
            public int hashCode() {
                return hash(stroke(), color(), fillColor(), isGeodesic());
            }

            public static Builder builder() {
                return new Builder();
            }

            static public class Builder {
                Integer stroke;
                Integer color;
                Integer fillColor;
                boolean geodesic;

                Builder(Style style) {
                    stroke = style.stroke;
                    color = style.color;
                    fillColor = style.fillColor;
                    geodesic = style.geodesic;
                }

                public Builder() {

                }

                /**
                 * Specifies the thickness of the path in pixels.
                 * If no weight parameter is set, the path will appear
                 * in its default thickness (5 pixels).
                 * @param width in pixel
                 * @return this instance
                 */
                public Builder stroke(int width) {
                    this.stroke = width;
                    return this;
                }

                /**
                 * Specifies a 24-bit or 32-bit color
                 * @param color ARGB
                 * @return this instance
                 */
                public Builder color(int color) {
                    this.color = color;
                    return this;
                }

                /**
                 * Indicates both that the path marks off a polygonal area
                 * and specifies the fill color to use as an overlay within that area.
                 * @param color ARGB
                 * @return this instance
                 */
                public Builder fill(int color) {
                    this.fillColor = color;
                    return this;
                }

                /**
                 * Indicates that the requested path should be interpreted
                 * as a geodesic line that follows the curvature of the Earth.
                 * @param geodesic (default to false)
                 * @return this instance
                 */
                public Builder geodesic(boolean geodesic) {
                    this.geodesic = geodesic;
                    return this;
                }

                /**
                 * @see {@link #geodesic(boolean) geodesic(boolean)}
                 * @return this instance
                 */
                public Builder geodesic() {
                    this.geodesic = true;
                    return this;
                }

                public Style build() {
                    return new Style(this);
                }
            }

            /**
             * Construct a circle {@link Path path} using the given style, center and radius.
             * @param style for path
             * @param latitude center
             * @param longitude center
             * @param radius in meters
             * @param count polygon count
             * @return a circle shaped {@link Path path}
             */
            public static Path circle(Style style, double latitude, double longitude, int radius, int count) {
                final GeoPoint[] points = new GeoPoint[count];
                final double pi = Math.PI;
                final double d = radius/6371e3;

                latitude = latitude*pi/180f;
                longitude= longitude*pi/180f;

                for(int i = 0; i < count; i++) {
                    double angle = (i*2*pi)/count;
                    double lat = asin(sin(latitude) * cos(d) + cos(latitude) * sin(d) * cos(angle));
                    double lon = ((longitude + atan2(sin(angle)*sin(d)*cos(latitude), cos(d)-sin(latitude)*sin(lat))) * 180) / pi;
                    lat*=180/pi;
                    points[i] = new GeoPoint(lat, lon);
                }

                return new Path(style, points);
            }
        }
    }

    private static int hash(Object ...objects) {
        return Arrays.hashCode(objects);
    }

    private static String rgb(int argb) {
        return String.format("0x%06X", argb & 0xffffff);
    }

    private static String rgba(int argb) {
        int alpha = alpha(argb);
        if(alpha == 0) return rgb(argb);
        return String.format("0x%08X", ((argb << 8) | alpha));
    }

    private static int alpha(int color) {
        return (color >> 24) & 0xff;
    }

    private static String join(char separator, Object ...objects) {
        StringBuilder sb = new StringBuilder();
        for(Object object: objects) {
            if(object == null) continue;
            if(sb.length() > 0) sb.append(separator);
            sb.append(object);
        }
        return sb.toString();
    }
}
