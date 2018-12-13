package pw.vasilis.navigator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonParser {

    private String estimated;

    private String scheduled;

    private String rtrip;

    private String rdistance;

    private String pspeed;

    private String cspeed;

    private String deviation;

    private String distancemetric;

    private String opcode;

    private JSONArray points;

    private String speedmetric;

    private String timemetric;

    public JsonParser() {
    }


    public JsonParser(String jsonstr) {
        try {
            JSONObject jsonObj = new JSONObject(jsonstr);

            estimated = jsonObj.getString("estimated");

            scheduled = jsonObj.getString("scheduled");

            rtrip = jsonObj.getString("rtrip");

            rdistance = jsonObj.getString("rdistance");

            pspeed = jsonObj.getString("pspeed");

            cspeed = jsonObj.getString("cspeed");

            deviation = jsonObj.getString("deviation");

            distancemetric = jsonObj.getString("distancemetric");

            opcode = jsonObj.getString("opcode");

            points = jsonObj.getJSONArray("points");

            speedmetric = jsonObj.getString("speedmetric");

            timemetric = jsonObj.getString("timemetric");


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String getEstimated() {
        return estimated.split(" ")[1];
    }

    public String getScheduled() {
        return scheduled.split(" ")[1];
    }

    public String getRtrip() {
        return rtrip;
    }

    public String getRdistance() {
        return rdistance + "" + distancemetric;
    }

    public String getPspeed() {
        return pspeed;
    }

    public String getCspeed() {
        return cspeed;
    }

    public String getDeviation() {
        return deviation;
    }

    public String getDistancemetric() {
        return distancemetric;
    }

    public String getOpcode() {
        return opcode;
    }

    public String getSpeedmetric() {
        return speedmetric;
    }

    public String getTimemetric() {
        return timemetric;
    }
}
