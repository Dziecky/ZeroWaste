package projekt.zespolowy.zero_waste.services.EducationalServices.Advice;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import projekt.zespolowy.zero_waste.dto.AdviceDTO;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Advice.Advice;
import projekt.zespolowy.zero_waste.entity.EducationalEntities.Advice.AdviceCategory;
import projekt.zespolowy.zero_waste.entity.Task;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.entity.UserTask;
import projekt.zespolowy.zero_waste.mapper.AdviceMapper;
import projekt.zespolowy.zero_waste.repository.AdviceRepository;
import projekt.zespolowy.zero_waste.repository.TaskRepository;
import projekt.zespolowy.zero_waste.repository.UserRepository;
import projekt.zespolowy.zero_waste.repository.UserTaskRepository;
import projekt.zespolowy.zero_waste.services.TagService;
import projekt.zespolowy.zero_waste.services.UserService;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class AdviceServiceImpl implements AdviceService {
    private final AdviceRepository adviceRepository;
    private final TagService tagService;
    private final AdviceMapper adviceMapper;
    private final UserService userService;

    private final UserTaskRepository userTaskRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Autowired
    public AdviceServiceImpl(AdviceRepository adviceRepository,
                             TagService tagService,
                             AdviceMapper adviceMapper,
                             UserService userService, UserTaskRepository userTaskRepository, TaskRepository taskRepository, UserRepository userRepository) {
        this.adviceRepository = adviceRepository;
        this.tagService = tagService;
        this.adviceMapper = adviceMapper;
        this.userService = userService;

        this.userTaskRepository = userTaskRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }
    @Override
    public Advice createAdvice(AdviceDTO adviceDTO) {
        Advice advice = adviceMapper.toEntity(adviceDTO, tagService);
        advice.setAuthor(userService.getUser());

        Task createAdviceTask = taskRepository.findByTaskName("Dodaj pierwszą poradę");

        if (createAdviceTask != null) {
            // Pobierz zadanie użytkownika
            UserTask userTask = userTaskRepository.findByUserAndTask(userService.getUser(), createAdviceTask);
            // Zwiększ postęp zadania
            userTask.setProgress(userTask.getProgress() + 1);

            // Sprawdź, czy zadanie zostało ukończone
            if (userTask.getProgress() >= createAdviceTask.getRequiredActions()) {
                userTask.setCompleted(true);
                userTask.setCompletionDate(LocalDate.now());

                userService.getUser().setTotalPoints(userService.getUser().getTotalPoints() + createAdviceTask.getPointsAwarded());
                userRepository.save(userService.getUser()); // Zapisz zmiany w użytkowniku
            }

            // Zapisz zmiany w UserTask
            userTaskRepository.save(userTask);
        }


        return adviceRepository.save(advice);
    }
    @Override
    @Transactional
    public Advice updateAdvice(Long id, AdviceDTO adviceDTO) {
        return adviceRepository.findById(id).map(existingAdvice -> {
            adviceMapper.updateAdviceFromDTO(adviceDTO, existingAdvice, tagService);
            return adviceRepository.save(existingAdvice);
        }).orElseThrow(() -> new RuntimeException("Advice not found with id " + id));
    }

    @Override
    public Optional<Advice> getAdviceById(Long id) {
        return adviceRepository.findById(id);
    }
    @Override
    public Page<Advice> getAllAdvices(Pageable pageable) {
        return adviceRepository.findAll(pageable);
    }
    @Override
    public Page<Advice> getAdvicesByTitle(String title, Pageable pageable) {
        if(title != null && !title.trim().isEmpty()){
            return adviceRepository.findByTitleContainingIgnoreCase(title, pageable);
        }
        else {
            return adviceRepository.findAll(pageable);
        }
    }

    @Override
    public void deleteAdvice(Long id) {
        adviceRepository.deleteById(id);
    }

    @Override
    public Page<Advice> getAdvicesByCategory(AdviceCategory category, Pageable pageable) {
        if(category!=null){
            return adviceRepository.findByAdviceCategory(category, pageable);
        }else{
            return adviceRepository.findAll(pageable);
        }
    }

    @Override
    public Page<Advice> findByTags_NameIgnoreCase(String tagName, Pageable pageable) {
        if(tagName != null && !tagName.trim().isEmpty()){
            return adviceRepository.findByTags_NameIgnoreCase(tagName, pageable);
        }
        else {
            return adviceRepository.findAll(pageable);
        }
    }
    @Override
    public Page<Advice> findAdvices(AdviceCategory category, String title, String tagName, Pageable pageable) {
        Specification<Advice> spec = Specification.where(null);
        if(category != null){
            spec = spec.and((root, query, cb) -> cb.equal(root.get("adviceCategory"), category));
        }
        if(title != null && !title.trim().isEmpty()){
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
        }
        if(tagName != null && !tagName.trim().isEmpty()){
            spec = spec.and((root, query, cb) -> cb.equal(root.join("tags").get("name"), tagName));
        }
        return adviceRepository.findAll(spec, pageable);
    }
    @Override
    public Page<AdviceDTO> findAdvicesWithLikesAndReads(AdviceCategory category, String title, String tagName, Pageable pageable, User currentUser) {
        Page<Advice> advices = findAdvices(category, title, tagName, pageable);
        return advices.map(advice -> {
            AdviceDTO dto = adviceMapper.toDTO(advice);
            dto.setLikedByCurrentUser(advice.getLikedByUsers().contains(currentUser));
            dto.setLikesCount(advice.getLikedByUsers().size());
            dto.setReadByCurrentUser(advice.getReadByUsers().contains(currentUser));
            dto.setReadsCount(advice.getReadByUsers().size());
            return dto;
        });
    }

    @Override
    @Transactional
    public void toggleReadAdvice(Long id) {
        Advice advice = adviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Advice not found with id " + id));

        User currentUser = userService.getUser();

        if(advice.getReadByUsers().contains(currentUser)){
            return;
        }else{
            advice.getReadByUsers().add(currentUser);
            currentUser.getReadAdvices().add(advice);
        }

        adviceRepository.save(advice);
        userService.save(currentUser);
    }

    @Override
    public Page<AdviceDTO> findAdvicesWithReads(AdviceCategory category, String title, String tagName, Pageable pageable, User currentUser) {
        Page<Advice> advices = findAdvices(category, title, tagName, pageable);
        return advices.map(advice -> {
            AdviceDTO dto = adviceMapper.toDTO(advice);
            dto.setReadByCurrentUser(advice.getReadByUsers().contains(currentUser));
            dto.setReadsCount(advice.getReadByUsers().size());
            return dto;
        });
    }

    @Override
    @Transactional
    public void toggleLikeAdvice(Long id) {
        Advice advice = adviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Advice not found with id " + id));

        User currentUser = userService.getUser();

        if(advice.getLikedByUsers().contains(currentUser)){
            advice.getLikedByUsers().remove(currentUser);
            currentUser.getLikedAdvices().remove(advice);
        }else{
            advice.getLikedByUsers().add(currentUser);
            currentUser.getLikedAdvices().add(advice);
        }

        adviceRepository.save(advice);
        userService.save(currentUser);
    }
}
