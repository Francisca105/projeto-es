package pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.VolunteerProfileDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser

import java.time.LocalDateTime

@DataJpaTest
class VolunteerProfileTest extends SpockTest {
    def volunteer
    def volunteerProfileDto
    def activity1
    def institution1
    def participation1, participation2, participation3
    def enrollment1
    def selectedParticipations

    def setup() {
        volunteer = createVolunteer(USER_1_NAME, USER_1_PASSWORD, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED)

        volunteerProfileDto = new VolunteerProfileDto()

        institution1 = createInstitution(INSTITUTION_1_NAME, INSTITUTION_1_EMAIL, INSTITUTION_1_NIF)

        activity1 = new Activity()
        activity1.setInstitution(institution1)
        activity1.setApplicationDeadline(LocalDateTime.now().plusDays(1))

        activity1.setEndingDate(LocalDateTime.now().minusDays(5))

        institution1.addActivity(activity1)

        enrollment1 = new Enrollment(activity1, volunteer, new EnrollmentDto(motivation: "Valid motivation with more than 10 caracters."))

        activity1.addEnrollment(enrollment1)

        participation1 = new Participation(volunteer: volunteer, activity: activity1)
        participation2 = new Participation(volunteer: volunteer, activity: activity1)
        participation3 = new Participation(volunteer: volunteer, activity: activity1)

        participation1.setVolunteerRating(5)
        participation2.setVolunteerRating(4)

        selectedParticipations = [participation1, participation2]

        volunteerProfileDto.setVolunteer(volunteer)
        volunteerProfileDto.setShortBio(VOLUNTEERPROFILE_SHORT_BIO)
        volunteerProfileDto.setNumTotalParticipations(volunteer.getParticipations().size())
        volunteerProfileDto.setNumTotalEnrollments(volunteer.getEnrollments().size())
        volunteerProfileDto.setNumTotalAssessments(volunteer.getAssessments().size())
        volunteerProfileDto.setSelectedParticipations(selectedParticipations)
    }

    def "create volunteer profile with valid parameters"() {
        when:
        def profile = new VolunteerProfile(volunteer, volunteerProfileDto)

        then:
        profile.getShortBio() == VOLUNTEERPROFILE_SHORT_BIO
        profile.getVolunteer() == volunteer
        profile.getNumTotalEnrollments() == 1
        profile.getNumTotalParticipations() == 3
        profile.getNumTotalAssessments() == 0
        profile.getAverageRating() == 4.5
    }

    // Test invariant 1
    def "fail to create profile with invalid short bio"() {
        given:
        def invalidShortBio = "Short"
        def dto = new VolunteerProfileDto()
        dto.setVolunteer(volunteer)
        dto.setShortBio(invalidShortBio)

        when:
        new VolunteerProfile(volunteer, dto)

        then:
        def exception = thrown(HEException)
        exception.errorMessage == ErrorMessage.VOLUNTEERPROFILE_REQUIRES_SHORTBIO
    }

    // Test invariant 2
    def "fail if selected participations contain unassessed participation"() {
        when:
        selectedParticipations = [participation3]
        volunteerProfileDto.setSelectedParticipations(selectedParticipations)
        new VolunteerProfile(volunteer, volunteerProfileDto)

        then:
        def exception = thrown(HEException)
        exception.errorMessage == ErrorMessage.VOLUNTEERPROFILE_REQUIRES_ALL_SELECTED_PARTICIPATIONS_MUST_BE_ASSESSED
    }

    // Test invariant 3
    def "fail if selected participations are fewer than min between tp/2 and ta)"() {
        when:
        selectedParticipations = []
        volunteerProfileDto.setSelectedParticipations(selectedParticipations)
        def v_profile = new VolunteerProfile(volunteer, volunteerProfileDto)

        then:
        def exception = thrown(HEException)
        exception.errorMessage == ErrorMessage.VOLUNTEERPROFILE_REQUIRES_VALID_NUMBER_OF_SELECTED_PARTICIPATIONS
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}