package edu.univ.erp.domain;

public class Section {

    private int sectionId;
    private int courseId;
    private int instructorId; // Can be 0 if not assigned
    private String dayTime;
    private String room;
    private int capacity;

    // We can add more fields like semester/year if needed

    public Section(int sectionId, int courseId, int instructorId, String dayTime, String room, int capacity) {
        this.sectionId = sectionId;
        this.courseId = courseId;
        this.instructorId = instructorId;
        this.dayTime = dayTime;
        this.room = room;
        this.capacity = capacity;
    }

    // Getters

    public int getSectionId() {
        return sectionId;
    }

    public int getCourseId() {
        return courseId;
    }

    public int getInstructorId() {
        return instructorId;
    }

    public String getDayTime() {
        return dayTime;
    }

    public String getRoom() {
        return room;
    }

    public int getCapacity() {
        return capacity;
    }
}