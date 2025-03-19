package pt.ulisboa.tecnico.socialsoftware.humanaethica.user.service

import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.repository.VolunteerProfileRepository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@DataJpaTest
class GetVolunteerProfileServiceTest extends SpockTest {
    @Autowired
    VolunteerProfileRepository volunteerProfileRepository

    def volunteerProfileDto

    def setup() {
        volunteerProfileDto = createVolunteerProfileDto()
        userService.createVolunteerProfile(volunteerProfileDto.getVolunteerId(), volunteerProfileDto)
    }

    def "get volunteer profile with valid arguments" () {
        when:
        def result = userService.getVolunteerProfile(volunteerProfileDto.getVolunteerId())

        then:
        result.getShortBio() == VOLUNTEERPROFILE_SHORT_BIO

        and:
        def storedProfile = volunteerProfileRepository.findAll().get(0)
        storedProfile.shortBio == VOLUNTEERPROFILE_SHORT_BIO
    }

    def "fail to get profile with invalid arguments" () {
        when:
        def result = userService.getVolunteerProfile(null)

        then:
        def exception = thrown(HEException)
        exception.errorMessage == ErrorMessage.USER_NOT_FOUND
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}