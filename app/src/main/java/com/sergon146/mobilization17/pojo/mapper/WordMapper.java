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
        "head",
        "def"
})
public class WordMapper {

    @JsonProperty("head")
    private Head head;
    @JsonProperty("def")
    private List<Def> def = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     */
    public WordMapper() {
    }

    /**
     * @param def
     * @param head
     */
    public WordMapper(Head head, List<Def> def) {
        super();
        this.head = head;
        this.def = def;
    }

    @JsonProperty("head")
    public Head getHead() {
        return head;
    }

    @JsonProperty("head")
    public void setHead(Head head) {
        this.head = head;
    }

    public WordMapper withHead(Head head) {
        this.head = head;
        return this;
    }

    @JsonProperty("def")
    public List<Def> getDef() {
        return def;
    }

    @JsonProperty("def")
    public void setDef(List<Def> def) {
        this.def = def;
    }

    public WordMapper withDef(List<Def> def) {
        this.def = def;
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

    public WordMapper withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

}