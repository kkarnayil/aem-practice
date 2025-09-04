package com.aempactice.core.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@AllArgsConstructor
@Getter
@Setter
public class ListItem {
    private int position;
    private String name;
    private String item;

    @JsonProperty("@type")
    private final String type = "ListItem";

}
