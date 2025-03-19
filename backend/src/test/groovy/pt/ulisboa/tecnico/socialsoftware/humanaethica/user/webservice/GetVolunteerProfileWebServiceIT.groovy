package pt.ulisboa.tecnico.socialsoftware.humanaethica.user.webservice

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.humanaethica.SpockTest
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.Volunteer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.RegisterUserDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.humanaethica.exceptions.HEException
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.humanaethica.utils.Mailer
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.domain.Assessment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.dto.VolunteerProfileDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.repository.VolunteerProfileRepository
import pt.ulisboa.tecnico.socialsoftware.humanaethica.user.domain.VolunteerProfile
import pt.ulisboa.tecnico.socialsoftware.humanaethica.participation.domain.Participation
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.domain.Enrollment
import pt.ulisboa.tecnico.socialsoftware.humanaethica.enrollment.dto.EnrollmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.assessment.dto.AssessmentDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.domain.Activity
import pt.ulisboa.tecnico.socialsoftware.humanaethica.institution.domain.Institution
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import java.time.LocalDateTime
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.http.HttpStatus
import pt.ulisboa.tecnico.socialsoftware.humanaethica.activity.dto.ActivityDto
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.domain.Theme
import pt.ulisboa.tecnico.socialsoftware.humanaethica.theme.dto.ThemeDto

import spock.mock.DetachedMockFactory

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetVolunteerProfileWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def volunteerProfileDto

    def setup() {
        deleteAll()

        webClient = WebClient.create("http://localhost:" + port)
        headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)

        volunteerProfileDto = createVolunteerProfileDto()
    }

    def "volunteer get VolunteerProfile"() {
        given:
        demoVolunteerLogin()

        webClient.post()
            .uri("/volunteers/${volunteerProfileDto.getVolunteerId()}/profile")
            .headers(httpHeaders -> httpHeaders.putAll(headers))
            .bodyValue(volunteerProfileDto)
            .retrieve()
            .bodyToMono(VolunteerProfileDto.class)
            .block()

        when:
        def response_get = webClient.get()
            .uri("/volunteers/${volunteerProfileDto.volunteerId}/profile")
            .headers(httpHeaders -> httpHeaders.putAll(headers))
            .retrieve()
            .bodyToMono(VolunteerProfileDto.class)
            .block()

        then:
        response_get.shortBio == VOLUNTEERPROFILE_SHORT_BIO
        response_get.volunteerId == volunteerProfileDto.getVolunteerId()

        cleanup:
        deleteAll()
    }

    def "get non-existent VolunteerProfile wihthout login"() {
        when:
        webClient.get()
            .uri("/volunteers/100/profile")
            .headers(httpHeaders -> httpHeaders.putAll(headers))
            .retrieve()
            .bodyToMono(VolunteerProfileDto.class)
            .block()

        then:
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.BAD_REQUEST

        cleanup:
        deleteAll()
    }

    def "volunteer get non-existent VolunteerProfile"() {
        given:
        demoVolunteerLogin()

        when:
        webClient.get()
            .uri("/volunteers/999/profile") // non-existent ID
            .headers(httpHeaders -> httpHeaders.putAll(headers))
            .retrieve()
            .bodyToMono(VolunteerProfileDto.class)
            .block()

        then:
        def error = thrown(WebClientResponseException)
        error.statusCode == HttpStatus.BAD_REQUEST

        cleanup:
        deleteAll()
    }
}