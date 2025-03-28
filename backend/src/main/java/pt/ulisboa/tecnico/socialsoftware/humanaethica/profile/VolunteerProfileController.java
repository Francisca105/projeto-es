package pt.ulisboa.tecnico.socialsoftware.humanaethica.profile;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.humanaethica.profile.dto.VolunteerProfileDto;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController()
public class VolunteerProfileController {
    @Autowired
    VolunteerProfileService volunteerProfileService;

    @GetMapping("/profile/volunteer/{volunteerId}")
    public Optional<VolunteerProfileDto> getVolunteerProfile(@PathVariable Integer volunteerId) {
        return volunteerProfileService.getVolunteerProfile(volunteerId);
    }

    @PostMapping("/profile/volunteer")
    @PreAuthorize("(hasRole('ROLE_VOLUNTEER'))")
    public VolunteerProfileDto createVolunteerProfile(Principal principal, @Valid @RequestBody VolunteerProfileDto volunteerProfileDto) {
        int userId = ((AuthUser) ((Authentication) principal).getPrincipal()).getUser().getId();
        return volunteerProfileService.createVolunteerProfile(userId, volunteerProfileDto);
    }

    @GetMapping("/profile/volunteer")
    public ResponseEntity<List<VolunteerProfileDto>> getAllVolunteerProfiles() {
        List<VolunteerProfileDto> profiles = volunteerProfileService.getAllVolunteerProfiles();
        return ResponseEntity.ok(profiles); // TODO: check this
    }
}