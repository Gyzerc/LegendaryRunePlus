package com.gyzer.Data;

import java.util.List;

public class AttributeWriterData {
    private String page;
    private List<String> attrs;

    public AttributeWriterData(String page, List<String> attrs) {
        this.page = page;
        this.attrs = attrs;
    }

    public String getPage() {
        return page;
    }

    public List<String> getAttrs() {
        return attrs;
    }
}
