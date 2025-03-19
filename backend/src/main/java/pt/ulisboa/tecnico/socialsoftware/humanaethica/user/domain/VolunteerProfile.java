package pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain;

import jakarta.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.VolunteerProfileDto;

import java.util.ArrayList;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage.*;

@Entity
public class VolunteerProfile {
    @Id
    private Integer id;
    private String shortBio;
    private Integer numTotalEnrollments;
    private Integer numTotalParticipations;
    private Integer numTotalAssessments;
    private Double averageRating;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private Volunteer volunteer;

    @OneToMany(mappedBy = "volunteerProfile", fetch = FetchType.EAGER)
    private List<Participation> selectedParticipations = new ArrayList<>();


    public VolunteerProfile() {}

    public VolunteerProfile(Volunteer volunteer, VolunteerProfileDto volunteerProfileDto) {
        setVolunteer(volunteer);
        setShortBio(volunteerProfileDto.getShortBio());
        setNumTotalEnrollments(volunteer.getEnrollments().size());
        setNumTotalParticipations(volunteer.getParticipations().size());
        setNumTotalAssessments(volunteer.getAssessments().size());
        setAverageRating(calculateAverageRating());
        setSelectedParticipations(volunteerProfileDto.getSelectedParticipations());

        verifyInvariants();
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

    public void setNumTotalEnrollments(Integer numTotalEnrollments) {
        this.numTotalEnrollments = numTotalEnrollments;
    }

    public Integer getNumTotalParticipations() {
        return this.numTotalParticipations;
    }

    public void setNumTotalParticipations(Integer numTotalParticipations) {
        this.numTotalParticipations = numTotalParticipations;
    }

    public Integer getNumTotalAssessments() {
        return this.numTotalAssessments;
    }

    public void setNumTotalAssessments(Integer numTotalAssessments) {
        this.numTotalAssessments = numTotalAssessments;
    }

    public Double getAverageRating() {
        return this.averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Volunteer getVolunteer() {
        return this.volunteer;
    }

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
    }

    public void setSelectedParticipations(List<Participation> selectedParticipations) {
        this.selectedParticipations = selectedParticipations;
    }

    public List<Participation> getSelectedParticipations() {
        return this.selectedParticipations;
    }

    private void verifyInvariants() {
        shortBioIsRequired();
        allSelectedParticipationsMustBeAssessed();
        numberOfSelectedParticipationsMustBeValid();
    }

    // Invariant 1
    private void shortBioIsRequired() {
        if (this.shortBio == null || this.shortBio.trim().length() < 10) {
            throw new HEException(VOLUNTEERPROFILE_REQUIRES_SHORTBIO);
        }
    }

    // Invariant 2
    private void allSelectedParticipationsMustBeAssessed() {
        if (this.selectedParticipations.stream().anyMatch(participation -> !isParticipationAssessed(participation))) {
            throw new HEException(VOLUNTEERPROFILE_REQUIRES_ALL_SELECTED_PARTICIPATIONS_MUST_BE_ASSESSED);
        }
    }

    private boolean isParticipationAssessed(Participation participation) {
        return participation.getVolunteerRating() != null;
    }

    // Invariant 3
    private void numberOfSelectedParticipationsMustBeValid(){
        int numSelectedParticipations = this.selectedParticipations.size();
        int totalParticipations = getNumTotalParticipations();
        int totalAssessments = getAssessedParticipations().size();

        int minValidParticipations = Math.min(totalParticipations / 2, totalAssessments);

        if (numSelectedParticipations < minValidParticipations) {
            throw new HEException(VOLUNTEERPROFILE_REQUIRES_VALID_NUMBER_OF_SELECTED_PARTICIPATIONS);
        }
    }

    private List<Participation> getAssessedParticipations() {
        List<Participation> assessedParticipations = new ArrayList<>();
        getTotalParticipations().stream()
            .filter(this::isParticipationAssessed)
            .forEach(assessedParticipations::add);
        return assessedParticipations;
    }

    private Double calculateAverageRating() {
        int totalRates = 0;
        int totalRatedParticipations = 0;
        totalRates = getTotalParticipations().stream()
            .filter(participation -> participation.getVolunteerRating() != null)
            .mapToInt(Participation::getVolunteerRating)
            .sum();

        totalRatedParticipations = (int) getTotalParticipations().stream()
            .filter(participation -> participation.getVolunteerRating() != null)
            .count();

        if (totalRatedParticipations == 0) {
            return 0.0;
        }

        return (double) totalRates / totalRatedParticipations;
    }

    private List<Participation> getTotalParticipations() {
        return this.volunteer.getParticipations();
    }
}