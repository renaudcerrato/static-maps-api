package com.mypopsy.maps;

import com.mypopsy.maps.internal.PolyLine;
import com.mypopsy.maps.internal.URLBuilder;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
         * @see {@link #PNG}
         */
        PNG8("png8"),
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

    private List<Markers> markers = new ArrayList<>();
    private List<Path> paths = new ArrayList<>();

    private GeoPoint center;
    private Integer zoom;
    private Integer scale;
    private Integer width, height;
    private Format format;
    private Type type;
    private String apiKey;
    private boolean https = true;


    /**
     * Explicitely center the map on the given {@link GeoPoint geopoint}
     * @param point of the center
     * @return this instance
     */
    public StaticMap center(GeoPoint point) {
        this.center = point;
        return this;
    }

    /**
     * Explicitely center the map on the given latitude/longitude
     * @param latitude of the center
     * @param longitude of the center
     * @return this instance
     */
    public StaticMap center(double latitude, double longitude) {
        return center(new GeoPoint(latitude, longitude));
    }

    /**
     * Add a single {@link Marker marker} with default {@link Marker.Style style).
     * @param latitude of the marker
     * @param longitude of the marker
     * @return this instance
     */
    public StaticMap addMarker(double latitude, double longitude) {
        return addMarkers(new GeoPoint(latitude, longitude));
    }

    /**
     * Add {@link Marker marker}(s) using default {@link Marker.Style style).
     * @param markers to add
     * @return this instance
     */
    public StaticMap addMarkers(GeoPoint...markers) {
        return addMarkers(null, markers);
    }

    /**
     * Add {@link Marker marker}(s) using the given {@link Marker.Style style)
     * @param style for marker(s)
     * @param markers to add
     * @return this instance
     */
    public StaticMap addMarkers(Marker.Style style, GeoPoint...markers) {
        this.markers.add(new Markers(style, markers));
        return this;
    }

    /**
     * Add a {@link Path path} using default {@link Path.Style style)
     * @param path to add
     * @return this instance
     */
    public StaticMap addPath(Path path) {
        paths.add(path);
        return this;
    }

    /**
     * Add a {@link Path path} using default {@link Path.Style Style style)
     * @param points to add
     * @return this instance
     */

    public StaticMap addPath(GeoPoint...points) {
        addPath(null, points);
        return this;
    }

    /**
     * Add a {@link Path path} using the given {@link Path.Style style)
     * @param style of the path
     * @param points to add
     * @return this instance
     */
    public StaticMap addPath(Path.Style style, GeoPoint...points) {
        addPath(new Path(style, points));
        return this;
    }

    /**
     * Clear any previously added {@link Marker marker}.
     * @return this instance
     */
    public StaticMap clearMarkers() {
        markers.clear();
        return this;
    }

    /**
     * Clear any previously added {@link Path path}.
     * @return this instance
     */
    public StaticMap clearPaths() {
        paths.clear();
        return this;
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
     * The scale value is multiplied with the size to determine the actual output size
     * of the image in pixels, without changing the coverage area of the map.
     * @param scale 1, 2 or 4 (the latter is for Google Maps APIs Premium Plan customers only)
     * @return this instance
     */
    public StaticMap scale(int scale) {
        this.scale = scale;
        return this;
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
     * Images may be returned in several common web graphics formats: GIF, JPEG and PNG.
     * @see Format
     * @param format
     * @return this instance
     */
    public StaticMap format(Format format) {
        this.format = format;
        return this;
    }

    /**
     * API Key
     * @param apiKey from your Google Developer Console
     * @return this instance
     */
    public StaticMap key(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    /**
     * Map {@link Type type}
     * @param type of the map
     * @return this instance
     */
    public StaticMap type(Type type) {
        this.type = type;
        return this;
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

    @Override
    public String toString() {

        URLBuilder builder = new URLBuilder(https ? HTTPS : HTTP);

        if(center != null) {
            builder.appendQuery("center", center);
        }

        if(width != null && height != null) {
            builder.appendQuery("size", width + "x" + height);
        }

        if(zoom != null) {
            builder.appendQuery("zoom", String.valueOf(zoom));
        }

        if(scale != null) {
            builder.appendQuery("scale", String.valueOf(scale));
        }

        if(format != null) {
            builder.appendQuery("format", format.value);
        }

        if(type != null) {
            builder.appendQuery("maptype", type.value);
        }

        if(apiKey != null) {
            builder.appendQuery("key", apiKey);
        }

        for(Markers markers: this.markers) {
            builder.appendQuery("markers", markers);
        }

        for(Path path: paths) {
            builder.appendQuery("path", path);
        }

        return builder.toString();
    }

    public URL toURL() throws MalformedURLException {
        return new URL(toString());
    }

    public static class GeoPoint {

        final Double latitude, longitude;
        final String address;

        public GeoPoint(String address) {
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
    }

    private static class Markers {

        private final Marker.Style style;
        private final GeoPoint[] points;

        Markers(Marker.Style style, GeoPoint... points) {
            this.style = style;
            this.points = points;
        }

        @Override
        public String toString() {
            return join('|', style, join('|', points));
        }
    }

    public static class Marker extends GeoPoint {

        public Marker(double latitude, double longitude) {
            super(latitude, longitude);
        }

        public Marker(String address) {
            super(address);
        }

        public static class Style {

            static public final Style BLACK  = new Builder().color(0x000000).build();
            static public final Style PURPLE = new Builder().color(0x800080).build();
            static public final Style RED    = new Builder().color(0xff0000).build();
            static public final Style GREY   = new Builder().color(0x808080).build();
            static public final Style GREEN  = new Builder().color(0x00ff00).build();
            static public final Style ORANGE = new Builder().color(0xffa500).build();
            static public final Style YELLOW = new Builder().color(0xffff00).build();
            static public final Style BLUE   = new Builder().color(0x0000ff).build();
            static public final Style WHITE  = new Builder().color(0xffffff).build();

            /**
             * Size of the {@link Marker marker}(s)
             */
            public enum Size {
                TINY("tiny"), MID("mid"), SMALL("small"), NORMAL(null);

                private final String value;
                Size(String value) { this.value = value; }
            }

            public final String icon;
            public final int color;
            public final String label;
            public final Size size;

            private Style(Builder builder) {
                this.icon = builder.icon;
                this.color = builder.color;
                this.size = builder.size;
                this.label = builder.label;
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
                        size != null && size.value != null ? "size:" + size.value : null,
                        color != 0 ? "color:" + rgb(color) : null,
                        label != null ? "label:" + label : null);
            }

            public static class Builder {

                String icon;
                int color;
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
                 * @see {@link #icon(String) icon(String)}
                 * @param icon url
                 * @return this instance
                 */
                public Builder icon(URL icon) {
                    return icon(icon.toString());
                }

                /**
                 * Specifies a URL to use as the marker's custom icon.
                 * Images may be in PNG, JPEG or GIF formats, though PNG is recommended.
                 * @param icon url
                 * @return this instance
                 */
                public Builder icon(String icon) {
                    this.icon = icon;
                    return this;
                }

                /**
                 * Marker color
                 * @param color RGB 24 bit
                 * @return this instance
                 */
                public Builder color(int color) {
                    this.color = color;
                    return this;
                }

                /**
                 * Specifies a single uppercase alphanumeric character from the set {A-Z, 0-9}.
                 * @param label {A-Z, 0-9}
                 * @return this instance
                 */
                public Builder label(String label) {
                    this.label = label;
                    return this;
                }

                public Style build() {
                    return new Style(this);
                }
            }
        }
    }

    static public class Path {

        private final GeoPoint[] points;
        private final Style style;

        public Path(Style style, GeoPoint...points) {
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

            return join('|', style, path);
        }

        static public class Style {

            public final Integer stroke;
            public final Integer color;
            public final Integer fillColor;
            public final boolean geodesic;

            private Style(Builder builder) {
                this.stroke = builder.stroke;
                this.color = builder.color;
                this.fillColor = builder.fillColor;
                this.geodesic = builder.geodesic;
            }

            public Builder toBuilder() {
                return new Builder(this);
            }

            @Override
            public String toString() {
                return join('|',
                        stroke != null ? "weight:" + stroke : null,
                        color != null ? "color:" + rgba(color) : null,
                        fillColor != null ? "fillcolor:" + rgba(fillColor) : null,
                        geodesic ? "geodesic:true" : null);
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

    static private String join(char separator, Object ...objects) {
        StringBuilder sb = new StringBuilder();
        for(Object object: objects) {
            if(object == null) continue;
            if(sb.length() > 0) sb.append(separator);
            sb.append(object);
        }
        return sb.toString();
    }
}
