package com.map.polygon.helper;




/**
 * Created by zahi on 18/09/2017.
 */

public class FlickrUrl {

    public static String findPlaceId(String place,String key) {
      String url =  "https://api.flickr.com/services/rest/?method=flickr.places.find&api_key="
              + key +"&query="
              + place + "&format=json&nojsoncallback=1";
        return  url;
    }
    public static String findPolyline(String placeID,String key) {
        String url =  "https://api.flickr.com/services/rest/?method=flickr.places.getInfo&api_key="
                + key + "&woe_id="
                + placeID +"&format=json&nojsoncallback=1";
        return  url;
    }
}