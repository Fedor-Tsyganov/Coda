package com.fedortsyganov.iptest.amr;

/**
 * Created by fedortsyganov on 5/20/15.
 */
import java.util.List;

public interface OnNewMetadataListener
{
    void onNewHeaders(String stringUri, List<String> name, List<String> desc, List<String> br,
                      List<String> genre, List<String> info);
    void onNewStreamTitle(String stringUri, String streamTitle);
}
