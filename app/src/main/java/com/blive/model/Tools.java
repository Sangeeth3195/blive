package com.blive.model;

public class Tools {

    private String is_this_tools_applied,toolsURL,name;

    public String getIs_this_tools_applied() {
        return is_this_tools_applied;
    }

    public void setIs_this_tools_applied(String is_this_tools_applied) {
        this.is_this_tools_applied = is_this_tools_applied;
    }

    public String getToolsURL() {
        return toolsURL;
    }

    public void setToolsURL(String toolsURL) {
        this.toolsURL = toolsURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Tools{" +
                "is_this_tools_applied='" + is_this_tools_applied + '\'' +
                ", toolsURL='" + toolsURL + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
