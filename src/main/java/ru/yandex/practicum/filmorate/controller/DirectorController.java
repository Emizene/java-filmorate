package ru.yandex.practicum.filmorate.controller;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.service.DirectorService;
import java.util.List;

@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
@Valid
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping
    public ResponseEntity<List<DirectorDto>> getAllDirectors() {
        return directorService.getAllDirectors();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DirectorDto> getDirectorById(@PathVariable Long id) {
        return directorService.getDirectorById(id);
    }

    @PostMapping
    public ResponseEntity<DirectorDto> addDirector(@Valid @RequestBody DirectorDto director) {
        return directorService.addDirector(director);
    }

    @PutMapping
    public ResponseEntity<DirectorDto> updateDirector(@Valid @RequestBody DirectorDto director) {
        return directorService.updateDirector(director);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteDirector(@PathVariable Long id) {
        return directorService.deleteDirector(id);
    }
}
