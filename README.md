# Google Static Maps API

Fluent and clean Java interface for [Google Static Maps API](https://developers.google.com/maps/documentation/static-maps/). 

> [DEMO APK](https://github.com/renaudcerrato/static-maps-api/raw/master/app/app-debug.apk)

<p align="center"><img src="https://github.com/renaudcerrato/static-maps-api/raw/master/assets/screenshot.png"></p>


# How To

Most of the official API is available under a fluent java interface. You'll find below some example usage, for more details, see the [official documentation](https://developers.google.com/maps/documentation/static-maps/intro) or look at the [demo source](https://github.com/renaudcerrato/static-maps-api/blob/master/app/src/main/java/com/mypopsy/staticmaps/demo/ui/DemoFragment.java).


## Basic usage

```java
StaticMap map = new StaticMap().center("NYC").size(320, 240);
load(map.toURL()); // load into your image view
```
<p align="center"><img src="https://maps.googleapis.com/maps/api/staticmap?center=NYC&size=320x240"/></p>

## Map Type

```java
new StaticMap().center("NYC").type(HYBRID).size(320, 240);
```
<p align="center"><img src="https://maps.googleapis.com/maps/api/staticmap?center=NYC&size=320x240&maptype=hybrid"/></p>

## Markers

### Custom colors

```java
 new StaticMap()
    .size(320, 240)
    .marker(Style.BLUE, new GeoPoint("Tour Eiffel"))
    .marker(Style.RED, new GeoPoint("Cathédrale Notre Dame"))
    .marker(Style.GREEN, new GeoPoint("Sacré-Coeur, 75018"))
    .marker(Style.ORANGE, new GeoPoint("Musée du Louvre"))
    .marker(Style.PURPLE, new GeoPoint("Arc de Triomphe"));
```

<td>
<p align="center"><img src="https://maps.googleapis.com/maps/api/staticmap?size=320x240&markers=color%3A0x0000FF%7CTour+Eiffel&markers=color%3A0xFF0000%7CCath%C3%A9drale+Notre+Dame&markers=color%3A0x00FF00%7CSacr%C3%A9-Coeur%2C+75018&markers=color%3A0xFFA500%7CMus%C3%A9e+du+Louvre&markers=color%3A0x800080%7CArc+de+Triomphe"/></p>
</td>
</tr>
</table>

### Custom icons
```java
new StaticMap()
    .size(320, 240)
    .marker(Style.builder().icon(ICON_URL_GHOSTBUSTER).build(), new GeoPoint(40.7195532,-74.0067987))
    .marker(Style.RED.toBuilder().label('A').build(), new GeoPoint(40.7529234,-73.9827515))
    .marker(Style.GREEN.toBuilder().label('B').build(), new GeoPoint("Columbia University, NYC"));
```

<p align="center"><img src="https://maps.googleapis.com/maps/api/staticmap?size=320x240&markers=icon%3Ahttp%3A%2F%2Fraw.githubusercontent.com%2Frenaudcerrato%2Fstatic-maps-api%2Fmaster%2Fapp%2Fsrc%2Fmain%2Fres%2Fdrawable%2Fghostbuster.png%7C40%2C719553%2C-74%2C006799&markers=color%3A0xFF0000%7Clabel%3AA%7C40%2C752923%2C-73%2C982752&markers=color%3A0x00FF00%7Clabel%3AB%7CColumbia+University%2C+NYC"/></p>

## Path

### Line

```java
new StaticMap()
    .size(320, 240)
    .path(Path.Style.builder().color(BLUE).build(),
          new GeoPoint(40.737102,-73.990318),
          new GeoPoint(40.749825,-73.987963),
          new GeoPoint(40.752946,-73.987384),
          new GeoPoint(40.755823,-73.986397));
```

<p align="center"><img src="https://maps.googleapis.com/maps/api/staticmap?path=color:0x0000ff%7C40.737102,-73.990318%7C40.749825,-73.987963%7C40.752946,-73.987384%7C40.755823,-73.986397&size=320x240"/></p>
### Filled

```java
new StaticMap()
    .size(320, 240)
    .path(Path.Style.builder().color(Color.TRANSPARENT).fill(0x66ff0000).build(),
          new GeoPoint("Miami, Florida"),
          new GeoPoint("San Juan, Puerto Rico"),
          new GeoPoint("Bermuda Island"));
```

<p align="center"><img src="https://maps.googleapis.com/maps/api/staticmap?size=320x240&path=color%3A0x00000000%7Cfillcolor%3A0xFF000066%7CMiami%2C+Florida%7CSan+Juan%2C+Puerto+Rico%7CBermuda+Island"/></p>

