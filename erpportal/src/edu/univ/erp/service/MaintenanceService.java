package edu.univ.erp.service;

import edu.univ.erp.data.SettingsData;

public class MaintenanceService {

    private final SettingsData settingsData = new SettingsData();

    public boolean isMaintenanceEnabled() {
        return settingsData.isMaintenanceModeOn();
    }

    public boolean toggleMaintenanceMode() {
        boolean currentState = settingsData.isMaintenanceModeOn();
        boolean newState = !currentState; // Flip it

        settingsData.setMaintenanceMode(newState);
        return newState;
    }
}