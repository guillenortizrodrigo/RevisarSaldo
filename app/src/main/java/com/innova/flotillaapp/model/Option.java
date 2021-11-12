package com.innova.flotillaapp.model;

import java.io.Serializable;
import java.util.List;

public class Option implements Serializable {
    private String additionalCode;

    private String code;

    private String icon;

    private Integer id;

    private String label;

    private String printableLabel;

    private List<Option> related;

    private boolean selected;

    public String getAdditionalCode() {
        return this.additionalCode;
    }

    public String getCode() {
        return this.code;
    }

    public String getIcon() {
        return this.icon;
    }

    public Integer getId() {
        return this.id;
    }

    public String getLabel() {
        return this.label;
    }

    public String getPrintableLabel() {
        return this.printableLabel;
    }

    public List<Option> getRelated() {
        return this.related;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setAdditionalCode(String paramString) {
        this.additionalCode = paramString;
    }

    public void setCode(String paramString) {
        this.code = paramString;
    }

    public void setIcon(String paramString) {
        this.icon = paramString;
    }

    public void setId(Integer paramInteger) {
        this.id = paramInteger;
    }

    public void setLabel(String paramString) {
        this.label = paramString;
    }

    public void setPrintableLabel(String paramString) {
        this.printableLabel = paramString;
    }

    public void setRelated(List<Option> paramList) {
        this.related = paramList;
    }

    public void setSelected(boolean paramBoolean) {
        this.selected = paramBoolean;
    }
}

