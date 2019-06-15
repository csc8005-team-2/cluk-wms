package org.team2.cluk.backend.tools;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;

/**
 * JsonTools Class to provide other classes, in particular web resources, with possibility to parse String into JSON
 *
 * @version 02/04/2019
 */

public class JsonTools {

    /**
     * Converts String object (e.g. http request body) into JsonObject
     * @param inputJson stringified JSON
     * @return parsed JSON
     */
    public static JsonObject parseObject(String inputJson) {
        // parsing incoming json using javax.json library from Java EE
        JsonReader jsonReader = Json.createReader(new StringReader(inputJson));
        JsonObject jsonObject = jsonReader.readObject();
        jsonReader.close();

        return jsonObject;
    }

    /**
     * Converts String object (e.g. http request body) into JsonArray
     * @param inputJsonArray    stringified JSON array
     * @return  parsed JSON array
     */
    public static JsonArray parseArray(String inputJsonArray) {
        // parsing incoming json array using javax.json library from Java EE
        JsonReader jsonReader = Json.createReader(new StringReader(inputJsonArray));
        JsonArray jsonArray = jsonReader.readArray();
        jsonReader.close();

        return jsonArray;
    }
}
