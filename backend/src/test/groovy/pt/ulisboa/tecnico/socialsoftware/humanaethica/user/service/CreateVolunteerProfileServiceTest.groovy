package pt.ulisboa.tecnico.socialsoftware.humanaethica.user.service;

import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.repository.VolunteerProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.VolunteerProfileDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;
import java.time.LocalDateTime;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.ParticipationRepository;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.dto.ParticipationDto;

@DataJpaTest
class CreateVolunteerProfileServiceTest extends SpockTest {
    @Autowired
    VolunteerProfileRepository volunteerProfileRepository

    @Autowired
    ParticipationRepository participationRepository

    def volunteer
    def volunteerProfileDto
    def activity1
    def institution1
    def participation1, participation2
    def enrollment1
    def selectedParticipations

    def setup() {
        deleteAll()

        volunteer = new Volunteer("Volunteer Name", "volunteer", "volunteer@example.com", AuthUser.Type.NORMAL, User.State.ACTIVE)
        userRepository.save(volunteer)
    }

    def "create volunteer profile with valid arguments"() {
        given:
        volunteer = createVolunteer(USER_1_NAME, USER_1_PASSWORD, USER_1_EMAIL, AuthUser.Type.NORMAL, User.State.APPROVED)

        institution1 = createInstitution(INSTITUTION_1_NAME, INSTITUTION_1_EMAIL, INSTITUTION_1_NIF)

        activity1 = new Activity()
        activity1.setInstitution(institution1)
        activity1.setApplicationDeadline(LocalDateTime.now().plusDays(1))
        activity1.setEndingDate(LocalDateTime.now().minusDays(5))
        institution1.addActivity(activity1)
        activityRepository.save(activity1)

        enrollment1 = new Enrollment(activity1, volunteer, new EnrollmentDto(motivation: "Valid motivation with more than 10 caracters."))
        activity1.addEnrollment(enrollment1)
        enrollmentRepository.save(enrollment1)

        participation1 = new Participation(volunteer: volunteer, activity: activity1)
        participation1.setVolunteerRating(5)
        participationRepository.save(participation1)

        participation2 = new Participation(volunteer: volunteer, activity: activity1)
        participation2.setVolunteerRating(4)
        participationRepository.save(participation2)

        userRepository.saveAndFlush(volunteer)

        volunteerProfileDto = new VolunteerProfileDto()
        volunteerProfileDto.setVolunteerId(volunteer.getId())
        volunteerProfileDto.setShortBio(VOLUNTEERPROFILE_SHORT_BIO)
        volunteerProfileDto.setSelectedParticipationsIds([participation1.id, participation2.id])

        when:
        def result = userService.createVolunteerProfile(volunteer.getId(), volunteerProfileDto)

        then:
        result.getShortBio() == VOLUNTEERPROFILE_SHORT_BIO
        result.getVolunteer() == volunteer
        result.getNumTotalEnrollments() == 1
        result.getNumTotalParticipations() == 2
        result.getNumTotalAssessments() == 0
        result.getAverageRating() == 4.5

        and:
        volunteerProfileRepository.findAll().size() == 1
        def storedProfile = volunteerProfileRepository.findAll().get(0)
        storedProfile.shortBio == VOLUNTEERPROFILE_SHORT_BIO
        storedProfile.getVolunteer() == volunteer
        storedProfile.getNumTotalEnrollments() == 1
        storedProfile.getNumTotalParticipations() == 2
        storedProfile.getNumTotalAssessments() == 0
        storedProfile.getAverageRating() == 4.5
    }

    def "create volunteer profile with no participations"() {
        given:
        def volunteerProfileDto = new VolunteerProfileDto()
        volunteerProfileDto.setShortBio(VOLUNTEERPROFILE_SHORT_BIO)
        volunteerProfileDto.setVolunteerId(volunteer.getId())
        volunteerProfileDto.setSelectedParticipationsIds([])

        when:
        def result = userService.createVolunteerProfile(volunteer.getId(), volunteerProfileDto)

        then:
        result.getShortBio() == VOLUNTEERPROFILE_SHORT_BIO
        volunteerProfileRepository.findAll().size() == 1
    }

    def "fail to create profile with invalid arguments"() {
        when:
        userService.createVolunteerProfile(volunteer.getId(), null)

        then:
        volunteerProfileRepository.findAll().size() == 0
        def exception = thrown(HEException)
        exception.errorMessage == ErrorMessage.VOLUNTEERPROFILE_INVALID_ARGUMENT
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}