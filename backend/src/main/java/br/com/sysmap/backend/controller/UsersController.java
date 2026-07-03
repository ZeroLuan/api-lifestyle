package br.com.sysmap.backend.controller;

import br.com.sysmap.backend.dto.common.SuccessResponseDTO;
import br.com.sysmap.backend.dto.user.*;
import br.com.sysmap.backend.openapi.UsersDoc;
import br.com.sysmap.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UsersController implements UsersDoc {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserDataDTO> getUser() {
        return ResponseEntity.ok(userService.getLoggedUserData());
    }

    @PutMapping("/update")
    public ResponseEntity<UserDataDTO> updateUserData(@Valid @RequestBody UpdateUserDTO dto) {
        return ResponseEntity.ok(userService.updateUser(dto));
    }

    @PutMapping(value = "/avatar", consumes = "multipart/form-data")
    public ResponseEntity<AvatarUrlDTO> updateUserAvatar(@Valid @ModelAttribute UpdateAvatarDTO dto) {
        return ResponseEntity.ok(userService.updateAvatar(dto.avatar()));
    }

    @DeleteMapping("/deactivate")
    public ResponseEntity<SuccessResponseDTO> deleteUser() {
        return ResponseEntity.ok(userService.deactivateUser());
    }

    @GetMapping("/preferences")
    public ResponseEntity<List<UserPreferencesDTO>> getUserPreferences() {
        return ResponseEntity.ok(userService.getPreferences());
    }

    @PostMapping("/preferences/define")
    public ResponseEntity<SuccessResponseDTO> updateUserPreferences(@RequestBody List<UUID> typeIds) {
        return ResponseEntity.ok(userService.definePreferences(typeIds));
    }
}
