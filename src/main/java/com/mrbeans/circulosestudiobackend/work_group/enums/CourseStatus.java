package com.mrbeans.circulosestudiobackend.work_group.enums;

public enum CourseStatus {
    ACTIVE("Activo"),
    PAUSED("Pausado"),
    FINISHED("Finalizado");

    private final String displayName;

    CourseStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
