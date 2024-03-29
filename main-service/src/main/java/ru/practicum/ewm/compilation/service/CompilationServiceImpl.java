package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.CompilationMapper;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.model.CompilationEvents;
import ru.practicum.ewm.compilation.repository.CompilationEventsRepository;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.NotFoundException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final CompilationEventsRepository compilationEventsRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CompilationDto create(NewCompilationDto newCompilationDto) {
        Compilation compNew = CompilationMapper.toCompilation(newCompilationDto);
        final Compilation compSaved = compilationRepository.save(compNew);
        List<CompilationEvents> compilationEvents = newCompilationDto.getEvents().stream()
                .map(eventId -> new CompilationEvents(null, compSaved.getId(),
                        getEventById(eventId)))
                .collect(Collectors.toList());
        compilationEvents.forEach(o -> compilationEventsRepository.save(o));
        compSaved.setCompilationEvents(compilationEvents);
        return CompilationMapper.toCompilationDto(compSaved);
    }

    @Override
    public CompilationDto findById(long compilationId) {
        Compilation compilation = getAndCheck(compilationId);
        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    public void deleteById(long compilationId) {
        getAndCheck(compilationId);
        compilationRepository.deleteById(compilationId);
    }

    @Override
    public void deleteEvent(long compilationId, long eventId) {
        CompilationEvents compilationEvents =
                compilationEventsRepository.findByCompIdAndEventId(compilationId, eventId)
                        .orElseThrow(() -> new NotFoundException("Указанное событие в " +
                                "поборке не найдено"));
        compilationEventsRepository.deleteById(compilationEvents.getId());
    }

    @Override
    public void addEvent(long compilationId, long eventId) {
        CompilationEvents ce = new CompilationEvents(null, compilationId, getEventById(eventId));
        compilationEventsRepository.save(ce);
    }

    @Override
    public void disablePin(long compilationId) {
        Compilation compilation = getAndCheck(compilationId);
        compilation.setPinned(false);
        compilationRepository.save(compilation);
    }

    @Override
    public void enablePin(long compilationId) {
        Compilation compilation = getAndCheck(compilationId);
        compilation.setPinned(true);
        compilationRepository.save(compilation);
    }

    @Override
    public List<CompilationDto> find(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        if (pinned == null) {
            return compilationRepository.findAll(pageable).stream()
                    .map(CompilationMapper::toCompilationDto)
                    .collect(Collectors.toList());
        } else {
            return compilationRepository.findByPinned(pinned, pageable).stream()
                    .map(CompilationMapper::toCompilationDto)
                    .collect(Collectors.toList());
        }
    }

    private Compilation getAndCheck(Long compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Указанная подборка не найдена"));
    }

    private Event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие в подборке не найдено"));
    }
}
