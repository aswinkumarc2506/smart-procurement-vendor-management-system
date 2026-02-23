package com.procurement.procurement.controller.vendor;

import com.procurement.procurement.entity.vendor.Vendor;
import com.procurement.procurement.repository.vendor.VendorRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/vendor")
public class VendorController {

    private final VendorRepository vendorRepository;

    public VendorController(VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    // ===================== Create Vendor =====================
    @PostMapping("/create")
    public ResponseEntity<?> createVendor(@RequestBody Vendor vendor) {

        vendor.setStatus("ACTIVE");
        Vendor savedVendor = vendorRepository.save(vendor);

        return ResponseEntity.ok(savedVendor);
    }

    // ===================== Get All Vendors =====================
    @GetMapping("/all")
    public ResponseEntity<List<Vendor>> getAllVendors() {

        List<Vendor> vendors = vendorRepository.findAll();
        return ResponseEntity.ok(vendors);
    }

    // ===================== Get Vendor By ID =====================
    @GetMapping("/{id}")
    public ResponseEntity<?> getVendorById(@PathVariable Long id) {

        Optional<Vendor> vendorOpt = vendorRepository.findById(id);

        if (vendorOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Vendor not found");
        }

        return ResponseEntity.ok(vendorOpt.get());
    }

    // ===================== Update Vendor =====================
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateVendor(@PathVariable Long id,
                                          @RequestBody Vendor vendor) {

        Optional<Vendor> vendorOpt = vendorRepository.findById(id);

        if (vendorOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Vendor not found");
        }

        Vendor existingVendor = vendorOpt.get();

        existingVendor.setName(vendor.getName());
        existingVendor.setEmail(vendor.getEmail());
        existingVendor.setContactNumber(vendor.getContactNumber());
        existingVendor.setAddress(vendor.getAddress());
        existingVendor.setStatus(vendor.getStatus());

        vendorRepository.save(existingVendor);

        return ResponseEntity.ok(existingVendor);
    }

    // ===================== Delete Vendor =====================
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteVendor(@PathVariable Long id) {

        if (!vendorRepository.existsById(id)) {
            return ResponseEntity.status(404).body("Vendor not found");
        }

        vendorRepository.deleteById(id);

        return ResponseEntity.ok("Vendor deleted successfully");
    }
}