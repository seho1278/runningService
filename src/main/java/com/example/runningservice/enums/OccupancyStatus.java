package com.example.runningservice.enums;

public enum OccupancyStatus {
    FULL {
        @Override
        public boolean validateFullOrAvailable(int capacity, int occupancy) {
            return capacity <= occupancy;
        }
    },
    AVAILABLE {
        @Override
        public boolean validateFullOrAvailable(int capacity, int occupancy) {
            return capacity > occupancy;
        }
    };

    public abstract boolean validateFullOrAvailable(int capacity, int occupancy);
}
