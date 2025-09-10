package com.aempactice.core.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Getter
public class ListItem {
    private int position;
    private String name;
    private String item;

    @JsonProperty("@type")
    private String type = "ListItem";

    public ListItem(int position, String name, String item) {
        this.position = position;
        this.name = name;
        this.item = item;
    }

}
