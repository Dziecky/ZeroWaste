package projekt.zespolowy.zero_waste.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import projekt.zespolowy.zero_waste.entity.Refund;
import projekt.zespolowy.zero_waste.repository.RefundRepository;

@Service
public class RefundService {

    @Autowired
    private RefundRepository refundRepository;

    public Refund save(Refund refund) {
        return refundRepository.save(refund);
    }
}
