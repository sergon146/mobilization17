package com.sergon146.mobilization17.pojo.mapper;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "text",
        "tr"
})
public class Ex {

    @JsonProperty("text")
    private String text;
    @JsonProperty("tr")
    private List<Tr> tr = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     */
    public Ex() {
    }

    /**
     * @param text
     * @param tr
     */
    public Ex(String text, List<Tr> tr) {
        super();
        this.text = text;
        this.tr = tr;
    }

    @JsonProperty("text")
    public String getText() {
        return text;
    }

    @JsonProperty("text")
    public void setText(String text) {
        this.text = text;
    }

    public Ex withText(String text) {
        this.text = text;
        return this;
    }

    @JsonProperty("tr")
    public List<Tr> getTr() {
        return tr;
    }

    @JsonProperty("tr")
    public void setTr(List<Tr> tr) {
        this.tr = tr;
    }

    public Ex withTr(List<Tr> tr) {
        this.tr = tr;
        return this;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public Ex withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}