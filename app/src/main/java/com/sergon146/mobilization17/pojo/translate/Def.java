package com.sergon146.mobilization17.pojo.translate;

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
        "pos",
        "gen",
        "anm",
        "ts",
        "tr"
})
public class Def {

    @JsonProperty("text")
    private String text;
    @JsonProperty("pos")
    private String pos;
    @JsonProperty("gen")
    private String gen;
    @JsonProperty("anm")
    private String anm;
    @JsonProperty("ts")
    private String ts;
    @JsonProperty("tr")
    private List<Tr> tr = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Def() {
    }

    @JsonProperty("text")
    public String getText() {
        return text;
    }

    @JsonProperty("text")
    public void setText(String text) {
        this.text = text;
    }

    @JsonProperty("pos")
    public String getPos() {
        return pos;
    }

    @JsonProperty("pos")
    public void setPos(String pos) {
        this.pos = pos;
    }

    @JsonProperty("gen")
    public String getGen() {
        return gen;
    }

    @JsonProperty("gen")
    public void setGen(String gen) {
        this.gen = gen;
    }

    @JsonProperty("anm")
    public String getAnm() {
        return anm;
    }

    @JsonProperty("anm")
    public void setAnm(String anm) {
        this.anm = anm;
    }

    @JsonProperty("ts")
    public String getTs() {
        return ts;
    }

    @JsonProperty("ts")
    public void setTs(String ts) {
        this.ts = ts;
    }
    @JsonProperty("tr")
    public List<Tr> getTr() {
        return tr;
    }

    @JsonProperty("tr")
    public void setTr(List<Tr> tr) {
        this.tr = tr;
    }


    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}