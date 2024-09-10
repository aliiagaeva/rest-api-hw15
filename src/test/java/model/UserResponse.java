package model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class UserResponse {
    private DataInfo data;
    private Support support;

    @Data
    public static class DataInfo {
        private int id;
        private String email;
       // @SerializedName("first_name")
        @JsonProperty("first_name")
        private String firstName;
       // @SerializedName("last_name")
       @JsonProperty("last_name")
        private String lastName;
        private String avatar;
    }

    @Data
    public static class Support {
        private String url;
        private String text;
    }
}