package com.example.lab1.ui.video;


import android.content.Context;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class VideoMaterial {
    private String title;
    private String description;
    private String url;

    public VideoMaterial(String title, String description, String url) {
        this.title = title;
        this.description = description;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public List<VideoMaterial> loadVideoMaterialsFromJson(Context context) {
        List<VideoMaterial> videoMaterials = new ArrayList<>();
        try {
            InputStream inputStream = context.getAssets().open("video_materials.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder jsonString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
            Gson gson = new Gson();
            Type listType = new TypeToken<List<VideoMaterial>>() {}.getType();
            videoMaterials = gson.fromJson(jsonString.toString(), listType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  videoMaterials;
    }
}
