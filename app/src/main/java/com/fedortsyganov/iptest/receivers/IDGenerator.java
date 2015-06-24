package com.fedortsyganov.iptest.receivers;

/**
 * Created by fedortsyganov on 4/10/15.
 */
public class IDGenerator
{
    //generates unique id
    public static String generateID()
    {
        // the value of uuid will be something like '03c9a439-fba6-41e1-a18a-4c542c12e6a8'
        return java.util.UUID.randomUUID().toString();
    }
}
