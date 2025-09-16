package ru.practicum.explorewithme.event.application;

import java.util.List;
import ru.practicum.explorewithme.api.client.event.dto.CompilationDto;
import ru.practicum.explorewithme.api.client.event.dto.NewCompilationDto;
import ru.practicum.explorewithme.api.client.event.dto.UpdateCompilationRequestDto;

public interface CompilationService {
    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilationById(Long compId);

    CompilationDto saveCompilation(NewCompilationDto request);

    CompilationDto updateCompilation(Long compId, UpdateCompilationRequestDto request);

    void deleteCompilation(Long compId);
}