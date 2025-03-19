package pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.VolunteerProfile;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation;

public class VolunteerProfileDto {
    private String shortBio;
    private Integer numTotalEnrollments;
    private Integer numTotalParticipations;
    private Integer numTotalAssessments;
    private Double averageRating;
    private Integer volunteerId;
    private List<Integer> selectedParticipationsIds;

    @JsonIgnore
    private Volunteer volunteer;

    @JsonIgnore
    private List<Participation> selectedParticipations;

    public VolunteerProfileDto() {}

    public VolunteerProfileDto(VolunteerProfile volunteerProfile) {
        this.volunteer = volunteerProfile.getVolunteer();
        this.volunteerId = volunteerProfile.getVolunteer().getId();
        this.shortBio = volunteerProfile.getShortBio();
        this.numTotalEnrollments = volunteerProfile.getVolunteer().getEnrollments().size();
        this.numTotalParticipations = volunteerProfile.getVolunteer().getParticipations().size();
        this.numTotalAssessments = volunteerProfile.getVolunteer().getAssessments().size();
        this.averageRating = volunteerProfile.getAverageRating();
        setSelectedParticipations(volunteerProfile.getSelectedParticipations());
        this.selectedParticipationsIds = volunteerProfile.getSelectedParticipations().stream().map(Participation::getId).toList();
    }

    public String getShortBio() {
        return this.shortBio;
    }

    public void setShortBio(String shortBio) {
        this.shortBio = shortBio;
    }

    public Integer getNumTotalEnrollments() {
        return this.numTotalEnrollments;
    }

    public void setNumTotalEnrollments(Integer totalEnrollments) {
        this.numTotalEnrollments = totalEnrollments;
    }

    public Integer getNumTotalParticipations() {
        return this.numTotalParticipations;
    }

    public void setNumTotalParticipations(Integer totalParticipations) {
        this.numTotalParticipations = totalParticipations;
    }

    public Integer getNumTotalAssessments() {
        return this.numTotalAssessments;
    }

    public void setNumTotalAssessments(Integer totalAssessments) {
        this.numTotalAssessments = totalAssessments;
    }

    public Double getAverageRating() {
        return this.averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public List<Participation> getSelectedParticipations() {
        return this.selectedParticipations;
    }

    public void setSelectedParticipations(List<Participation> selectedParticipations) {
        this.selectedParticipations = selectedParticipations;
    }

    public Volunteer getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
    }

    public Integer getVolunteerId() {
        return volunteerId;
    }

    public void setVolunteerId(Integer volunteerId) {
        this.volunteerId = volunteerId;
    }

    public List<Integer> getSelectedParticipationsIds() {
        return selectedParticipationsIds;
    }

    public void setSelectedParticipationsIds(List<Integer> selectedParticipationsIds) {
        this.selectedParticipationsIds = selectedParticipationsIds;
    }

    @Override
    public String toString() {
        return "VolunteerProfileDto{" +
                "shortBio='" + shortBio + '\'' +
                ", numTotalEnrollments=" + numTotalEnrollments +
                ", numTotalParticipations=" + numTotalParticipations +
                ", numTotalAssessments=" + numTotalAssessments +
                ", averageRating=" + averageRating +
                ", volunteer=" + volunteer +
                ", volunteerId=" + volunteerId +
                ", selectedParticipations=" + selectedParticipations +
                ", selectedParticipationsIds=" + selectedParticipationsIds +
                '}';
    }
}