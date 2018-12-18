package opengdc.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author fabio
 * @source http://stackoverflow.com/questions/21720759/convert-a-json-string-to-a-hashmap
 */
public class JSONUtils {

    public static Map<String, Object> jsonToMap(JSONObject json) throws JSONException {
        Map<String, Object> retMap = new HashMap<>();

        if(json != JSONObject.NULL) {
            retMap = toMap(json);
        }
        return retMap;
    }

    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<>();

        Iterator<String> keysItr = object.keys();
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<>();
        for(int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }


    public static HashMap<String, ArrayList<Object>> searchFor(JsonObject node, String prefix, HashMap<String, ArrayList<Object>> intermediateRes, HashSet<String> attributes) {
        ArrayList<Object> values = new ArrayList<>();
        HashMap<String, Object> hm_values = new HashMap<>();

        for (Entry<String, JsonValue> entry: node.entrySet()) {
            String attribute = entry.getKey();
            JsonValue value = entry.getValue();
            if (value.getValueType() == ValueType.STRING || 
                    value.getValueType() == ValueType.FALSE || 
                    value.getValueType() == ValueType.NULL ||
                    value.getValueType() == ValueType.NUMBER ||
                    value.getValueType() == ValueType.TRUE) {
                if (attributes.contains(prefix + attribute))
                    hm_values.put(prefix + attribute, value.toString());
            }
            else {
                if (value.getValueType() == ValueType.ARRAY) {
                    JsonArray array = (JsonArray) value;
                    for (int array_index=0; array_index<array.size(); array_index++) {
                        if (array.get(array_index).getValueType() == ValueType.STRING || 
                                array.get(array_index).getValueType() == ValueType.FALSE || 
                                array.get(array_index).getValueType() == ValueType.NULL ||
                                array.get(array_index).getValueType() == ValueType.NUMBER ||
                                array.get(array_index).getValueType() == ValueType.TRUE) {
                            if (attributes.contains(prefix + attribute))
                                hm_values.put(prefix + attribute, array.get(array_index).toString()); // store the first value (e.g.: "acl")
                        }
                        else
                            intermediateRes = searchFor(array.getJsonObject(array_index), prefix + attribute + ".", intermediateRes, attributes);
                    }
                }
                else if (value.getValueType() == ValueType.OBJECT) {
                    intermediateRes = searchFor((JsonObject) value, prefix + attribute + ".", intermediateRes, attributes);
                }
            }
        }

        values.add(hm_values);
        for (String jattr: hm_values.keySet())
            intermediateRes.put(jattr, values);
        return intermediateRes;
    }

    public static HashMap<String, ArrayList<Object>> searchFor(JsonObject hit, HashSet<String> attributes, String aliquot_id, HashSet<String> dataTypes) {

        boolean aliquot_found = false;

        HashMap<String, ArrayList<Object>> result = new HashMap<>();


        String hitDataType = hit.getString("data_type");
        if (dataTypes.contains(hitDataType)) {

            JsonArray cases = hit.getJsonArray("cases");
            if(cases != null){
                for (int cases_index=0; cases_index<cases.size(); cases_index++) {
                    String case_prefix = "cases.";
                    JsonObject caseobj = cases.getJsonObject(cases_index);

                    JsonArray samples = caseobj.getJsonArray("samples");
                    if(samples != null){
                        for (int samples_index=0; samples_index<samples.size(); samples_index++) {
                            String sample_prefix = case_prefix + "samples.";
                            JsonObject sample = samples.getJsonObject(samples_index);

                            JsonArray portions = sample.getJsonArray("portions");
                            if(portions != null){
                                for (int portions_index=0; portions_index<portions.size(); portions_index++) {
                                    String portion_prefix = sample_prefix + "portions.";
                                    JsonObject portion = portions.getJsonObject(portions_index);

                                    JsonArray analytes = portion.getJsonArray("analytes");
                                    if(analytes != null){
                                        for (int analytes_index=0; analytes_index<analytes.size(); analytes_index++) {
                                            String analyte_prefix = portion_prefix + "analytes.";
                                            JsonObject analyte = analytes.getJsonObject(analytes_index);

                                            JsonArray aliquots = analyte.getJsonArray("aliquots");
                                            if(aliquots != null){
                                                for (int aliquots_index=0; aliquots_index<aliquots.size(); aliquots_index++) {
                                                    String aliquot_prefix = analyte_prefix + "aliquots.";
                                                    JsonObject aliquot = aliquots.getJsonObject(aliquots_index);

                                                    String current_aliquot = aliquot.getString("aliquot_id").toLowerCase();
                                                    // aliquot
                                                    if (current_aliquot.trim().equals(aliquot_id)) {
                                                        aliquot_found = true;
                                                        result = searchFor(aliquot, aliquot_prefix, result, attributes);
                                                        break;
                                                    }  
                                                }
                                            }
                                            // analyte
                                            if (aliquot_found) {
                                                JsonObject analyte_copy = cloneObj(analyte, "aliquots");
                                                result = searchFor(analyte_copy, analyte_prefix, result, attributes);
                                                break;
                                            }
                                        }
                                    }
                                    // portion
                                    if (aliquot_found) {
                                        JsonObject portion_copy = cloneObj(portion, "analytes");
                                        result = searchFor(portion_copy, portion_prefix, result, attributes);
                                        break;
                                    }
                                }
                            }
                            // sample
                            if (aliquot_found) {
                                JsonObject sample_copy = cloneObj(sample, "portions");
                                result = searchFor(sample_copy, sample_prefix, result, attributes);
                                break;
                            }
                        }
                    }
                    // case
                    if (aliquot_found) {
                        JsonObject caseobj_copy = cloneObj(caseobj, "samples");
                        result = searchFor(caseobj_copy, case_prefix, result, attributes);
                        break;
                    }
                }
            }
            // hit
            if (aliquot_found) {
                JsonObject hit_copy = cloneObj(hit, "cases");
                result = searchFor(hit_copy, "", result, attributes);
            }

        }

        return result;
    }

    public static HashMap<String, ArrayList<Object>> searchForExperiment(JsonObject hit, HashSet<String> attributes, String value_aliquot, HashSet<String> dataTypes) {
        boolean aliquot_found = false;

        HashMap<String, ArrayList<Object>> result = new HashMap<>();

        JsonArray cases = hit.getJsonArray("cases");
        if(cases != null){
            for (int cases_index=0; cases_index<cases.size(); cases_index++) {
                String case_prefix = "cases.";
                JsonObject caseobj = cases.getJsonObject(cases_index);

                JsonArray samples = caseobj.getJsonArray("samples");
                if(samples != null){
                    for (int samples_index=0; samples_index<samples.size(); samples_index++) {
                        String sample_prefix = case_prefix + "samples.";
                        JsonObject sample = samples.getJsonObject(samples_index);

                        JsonArray portions = sample.getJsonArray("portions");
                        if(portions != null){
                            for (int portions_index=0; portions_index<portions.size(); portions_index++) {
                                String portion_prefix = sample_prefix + "portions.";
                                JsonObject portion = portions.getJsonObject(portions_index);

                                JsonArray analytes = portion.getJsonArray("analytes");
                                if(analytes != null){
                                    for (int analytes_index=0; analytes_index<analytes.size(); analytes_index++) {
                                        String analyte_prefix = portion_prefix + "analytes.";
                                        JsonObject analyte = analytes.getJsonObject(analytes_index);

                                        JsonArray aliquots = analyte.getJsonArray("aliquots");
                                        if(aliquots != null){
                                            for (int aliquots_index=0; aliquots_index<aliquots.size(); aliquots_index++) {
                                                String aliquot_prefix = analyte_prefix + "aliquots.";
                                                JsonObject aliquot = aliquots.getJsonObject(aliquots_index);

                                                // aliquot
                                                aliquot_found = true;
                                                result = searchFor(aliquot, aliquot_prefix, result, attributes);
                                                break;                                                   
                                            }
                                        }
                                        // analyte
                                        if (aliquot_found) {
                                            JsonObject analyte_copy = cloneObj(analyte, "aliquots");
                                            result = searchFor(analyte_copy, analyte_prefix, result, attributes);
                                            break;
                                        }
                                    }
                                }
                                // portion
                                if (aliquot_found) {
                                    JsonObject portion_copy = cloneObj(portion, "analytes");
                                    result = searchFor(portion_copy, portion_prefix, result, attributes);
                                    break;
                                }
                            }
                        }
                        // sample
                        if (aliquot_found) {
                            JsonObject sample_copy = cloneObj(sample, "portions");
                            result = searchFor(sample_copy, sample_prefix, result, attributes);
                            break;
                        }
                    }
                }
                // case
                if (aliquot_found) {
                    JsonObject caseobj_copy = cloneObj(caseobj, "samples");
                    result = searchFor(caseobj_copy, case_prefix, result, attributes);
                    break;
                }
            }
        }
        // hit
        if (aliquot_found) {
            JsonObject hit_copy = cloneObj(hit, "cases");
            result = searchFor(hit_copy, "", result, attributes);
        }

        return result;
    }

    public static JsonObject cloneObj(JsonObject obj, String exclude) {       
        JsonObjectBuilder builder = Json.createObjectBuilder();
        for (Entry<String, JsonValue> entry: obj.entrySet()) {
            if (!entry.getKey().equals(exclude))
                builder.add(entry.getKey(), entry.getValue());
        }
        return builder.build();
    }


}