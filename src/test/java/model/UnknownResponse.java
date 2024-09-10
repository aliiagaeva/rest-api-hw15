package model;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UnknownResponse {
    private int page;
    @JsonProperty("per_page")
    private int perPage;
    private int total;
    @JsonProperty("total_pages")
    private int totalPages;
    private List<DataItem> data;
    private Support support;

    @Data
    public static class Support {
        private String url;
        private String text;
    }
}
