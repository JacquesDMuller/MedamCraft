package com.medamcraft.civilization.ai;

public class Decision {
    private final String title;
    private final String description;
    private final Runnable action;

    public Decision(String title, String description, Runnable action) {
        this.title = title;
        this.description = description;
        this.action = action;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void execute() {
        action.run();
    }
} 