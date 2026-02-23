package com.procurement.procurement.service.procurement;

import com.procurement.procurement.entity.procurement.Requisition;
import com.procurement.procurement.entity.user.User;
import com.procurement.procurement.repository.procurement.RequisitionRepository;
import com.procurement.procurement.repository.user.UserRepository;
import com.procurement.procurement.service.audit.AuditService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RequisitionService {

    private final RequisitionRepository requisitionRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;

    public RequisitionService(RequisitionRepository requisitionRepository,
                              UserRepository userRepository,
                              AuditService auditService) {
        this.requisitionRepository = requisitionRepository;
        this.userRepository = userRepository;
        this.auditService = auditService;
    }

    // ===================== CREATE =====================
    @PreAuthorize("hasAuthority('CREATE_REQUISITION')")
    public Requisition createRequisition(Requisition requisition) {

        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        requisition.setRequestedBy(currentUser);
        requisition.setCreatedAt(LocalDateTime.now());
        requisition.setUpdatedAt(LocalDateTime.now());

        Requisition saved = requisitionRepository.save(requisition);

        // ✅ CORRECT AUDIT CALL
        auditService.log(
                "Requisition",
                saved.getId(),
                "CREATE",
                "Requisition created successfully"
        );

        return saved;
    }

    // ===================== UPDATE =====================
    @PreAuthorize("hasAuthority('UPDATE_REQUISITION')")
    public Requisition updateRequisition(Long id, Requisition updatedReq) {

        Requisition req = requisitionRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Requisition not found with id: " + id));

        req.setRequisitionNumber(updatedReq.getRequisitionNumber());
        req.setItems(updatedReq.getItems());
        req.setStatus(updatedReq.getStatus());
        req.setUpdatedAt(LocalDateTime.now());

        Requisition saved = requisitionRepository.save(req);

        // ✅ CORRECT AUDIT CALL
        auditService.log(
                "Requisition",
                saved.getId(),
                "UPDATE",
                "Requisition updated successfully"
        );

        return saved;
    }

    // ===================== GET ALL =====================
    @PreAuthorize("hasAnyRole('ADMIN','PROCUREMENT_MANAGER')")
    public List<Requisition> getAllRequisitions() {
        return requisitionRepository.findAll();
    }

    // ===================== GET MY =====================
    @PreAuthorize("hasRole('EMPLOYEE')")
    public List<Requisition> getMyRequisitions() {

        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return requisitionRepository.findByRequestedBy(currentUser);
    }

    // ===================== FILTER BY STATUS =====================
    @PreAuthorize("hasAuthority('VIEW_REQUISITION')")
    public List<Requisition> getRequisitionsByStatus(String status) {
        return requisitionRepository.findByStatus(status);
    }
}
