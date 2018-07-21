package com.samapps.horachallenge.model;

public class Task
{
    private String timestamp;

    private String category;

    private String description;

    private String name;

    private double lat;

    private double lng;

    private String assignedTo;

    private String ruleId;

    private String createdBy;

    private boolean isCompleted;

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getTimestamp ()
    {
        return timestamp;
    }

    public void setTimestamp (String timestamp)
    {
        this.timestamp = timestamp;
    }

    public String getCategory ()
    {
        return category;
    }

    public void setCategory (String category)
    {
        this.category = category;
    }

    public String getDescription ()
    {
        return description;
    }

    public void setDescription (String description)
    {
        this.description = description;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getAssignedTo ()
    {
        return assignedTo;
    }

    public void setAssignedTo (String assignedTo)
    {
        this.assignedTo = assignedTo;
    }

    public boolean getIsCompleted ()
    {
        return isCompleted;
    }

    public void setIsCompleted (boolean isCompleted)
    {
        this.isCompleted = isCompleted;
    }


}
