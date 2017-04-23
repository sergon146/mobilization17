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
        "syn",
        "mean",
        "ex"
})
public class Tr {

    @JsonProperty("text")
    private String text;
    @JsonProperty("pos")
    private String pos;
    @JsonProperty("gen")
    private String gen;
    @JsonProperty("syn")
    private List<Syn> syn = null;
    @JsonProperty("mean")
    private List<Mean> mean = null;
    @JsonProperty("ex")
    private List<Ex> ex = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<>();

    public Tr() {
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

    @JsonProperty("syn")
    public List<Syn> getSyn() {
        return syn;
    }

    @JsonProperty("syn")
    public void setSyn(List<Syn> syn) {
        this.syn = syn;
    }

    @JsonProperty("mean")
    public List<Mean> getMean() {
        return mean;
    }

    @JsonProperty("mean")
    public void setMean(List<Mean> mean) {
        this.mean = mean;
    }

    @JsonProperty("ex")
    public List<Ex> getEx() {
        return ex;
    }

    @JsonProperty("ex")
    public void setEx(List<Ex> ex) {
        this.ex = ex;
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