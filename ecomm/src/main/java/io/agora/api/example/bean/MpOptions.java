package io.agora.api.example.bean;

import android.util.Log;

import org.json.JSONObject;

import java.lang.reflect.Field;

public class MpOptions {
    public static final String TAG = "MpOptions";

    /**
     * key:
     * makeup_options
     * <p>
     * value:
     * bool enable_mu;          (true: makeup on; false: makeup off)
     * int browStyle;           (eyebrow type 0 - 2: off, type1, type2)
     * int browColor;           (eyebrow color 0 - 2: none, black, brown)
     * float browStrength;      (eyebrow strength 0.0 - 1.0)
     * int lashStyle;           (eyelash type 0 - 2: off, type1, type2)
     * int lashColor;           (eyelash color 0 - 2: none, black, brown)
     * float lashStrength;      (eyelash strength 0.0 - 1.0)
     * int shadowStyle;         (eye shadow type 0 - 2: off, type1, type2)
     * int shadowColor;         not available
     * float shadowStrength;    (eye shadow strength 0.0 - 1.0)
     * int pupilStyle;          (pupil type 0 - 2: off, type1, type2)
     * int pupilColor;          not available
     * float pupilStrength;     (pupil strength 0.0 - 1.0)
     * int blushStyle;          (blush type 0 - 2: off, type1, type2)
     * int blushColor;          (blush color 0 - 5: none, color1, color2, color3, color4, color5)
     * float blushStrength;     (blush strength 0.0 - 1.0)
     * int lipStyle;            (lipstick type 0 - 2: off, type1, type2)
     * int lipColor;            (lipstick color 0 - 5: none, color1, color2, color3, color4, color5)
     * float lipStrength;       (lipstick strength 0.0 - 1.0)
     **/

    public boolean enable_mu;

    public int browStyle;
    public int browColor;
    public float browStrength;

    public int lashStyle;
    public int lashColor;
    public float lashStrength;

    public int shadowStyle;
    public float shadowStrength;

    public int pupilStyle;
    public float pupilStrength;

    public int blushStyle;
    public int blushColor;
    public float blushStrength;

    public int lipStyle;
    public int lipColor;
    public float lipStrength;

    public MpOptions() {
        this.enable_mu = false;
        this.browStyle = 0;
        this.browColor = 0;
        this.browStrength = 0.5f;
        this.lashStyle = 0;
        this.lashColor = 0;
        this.lashStrength = 0.5f;
        this.shadowStyle = 0;
        this.shadowStrength = 0.5f;
        this.pupilStyle = 0;
        this.pupilStrength = 0.5f;
        this.blushStyle = 0;
        this.blushColor = 0;
        this.blushStrength = 0.5f;
        this.lipStyle = 0;
        this.lipColor = 0;
        this.lipStrength = 0.5f;
    }

    public String toJson() {
        String json = "{}";
        JSONObject jsonObject = new JSONObject();

        try {
            Field[] fields = MpOptions.class.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                String name = field.getName();
                Object value = field.get(this);
                jsonObject.put(name, value);
            }
        } catch (Exception e) {
            Log.e(TAG, "toJson: error:" + e.getMessage());
        }
        json = jsonObject.toString();
        Log.d(TAG, "toJson: " + json);
        return json;
    }
} 