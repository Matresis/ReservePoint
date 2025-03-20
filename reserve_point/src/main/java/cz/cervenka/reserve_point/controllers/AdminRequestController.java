package cz.cervenka.reserve_point.controllers;

import cz.cervenka.reserve_point.database.entities.ReservationCancellationRequestEntity;
import cz.cervenka.reserve_point.database.entities.ReservationConfirmationRequestEntity;
import cz.cervenka.reserve_point.database.entities.ReservationEntity;
import cz.cervenka.reserve_point.database.entities.ReservationModificationRequestEntity;
import cz.cervenka.reserve_point.database.repositories.ReservationCancellationRequestRepository;
import cz.cervenka.reserve_point.database.repositories.ReservationConfirmationRequestRepository;
import cz.cervenka.reserve_point.database.repositories.ReservationModificationRequestRepository;
import cz.cervenka.reserve_point.database.repositories.ReservationRepository;
import cz.cervenka.reserve_point.services.AdminRequestService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/admin/requests")
public class AdminRequestController {

    private final AdminRequestService requestService;
    private final ReservationModificationRequestRepository modificationRequestRepository;
    private final ReservationCancellationRequestRepository cancellationRequestRepository;
    private final ReservationConfirmationRequestRepository confirmationRequestRepository;

    public AdminRequestController(AdminRequestService requestService, ReservationModificationRequestRepository modificationRequestRepository, ReservationCancellationRequestRepository cancellationRequestRepository, ReservationConfirmationRequestRepository confirmationRequestRepository) {
        this.requestService = requestService;
        this.modificationRequestRepository = modificationRequestRepository;
        this.cancellationRequestRepository = cancellationRequestRepository;
        this.confirmationRequestRepository = confirmationRequestRepository;
    }

    @GetMapping
    public String viewRequests(Model model) {
        List<ReservationModificationRequestEntity> modificationRequests = modificationRequestRepository.findAll();
        List<ReservationCancellationRequestEntity> cancellationRequests = cancellationRequestRepository.findAll();
        List<ReservationConfirmationRequestEntity> confirmationRequests = confirmationRequestRepository.findAll();

        modificationRequests.forEach(ReservationModificationRequestEntity::formatRequestedOrderTime);

        model.addAttribute("modificationRequests", modificationRequests);
        model.addAttribute("cancellationRequests", cancellationRequests);
        model.addAttribute("confirmationRequests", confirmationRequests);

        return "admin/requests";
    }

    @PostMapping("/{id}/approve-confirmation")
    public String approveConfirmation(@PathVariable Long id) {
        requestService.approveConfirmationRequest(id);
        return "redirect:/admin/requests";
    }


    @PostMapping("/{id}/approve-modification")
    public String approveModification(@PathVariable Long id) {
        requestService.approveModificationRequest(id);
        return "redirect:/admin/requests";
    }

    @PostMapping("/{id}/reject-modification")
    public String rejectModification(@PathVariable Long id) {
        requestService.rejectModificationRequest(id);
        return "redirect:/admin/requests";
    }

    @PostMapping("/{id}/approve-cancellation")
    public String approveCancellation(@PathVariable Long id) {
        requestService.approveCancellation(id);
        return "redirect:/admin/requests";
    }
}