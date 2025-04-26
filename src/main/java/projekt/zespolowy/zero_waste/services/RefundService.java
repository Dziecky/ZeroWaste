package projekt.zespolowy.zero_waste.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import projekt.zespolowy.zero_waste.controller.exception.RefundNotFoundException;
import projekt.zespolowy.zero_waste.entity.Refund;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.entity.enums.RefundStatus;
import projekt.zespolowy.zero_waste.repository.OrderRepository;
import projekt.zespolowy.zero_waste.repository.RefundRepository;

import java.util.List;

@Service
public class RefundService {

    @Autowired
    private RefundRepository refundRepository;

    @Autowired
    private OrderRepository orderRepository;

    public Refund save(Refund refund) {
        return refundRepository.save(refund);
    }

    public Page<Refund> getRefundsByUser(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "requestDate"));
        return refundRepository.findAllByOrder_User(user, pageable);
    }

    public Page<Refund> getAllRefunds(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "requestDate"));
        return refundRepository.findAllWithOrderAndUser(pageable);
    }

    public Refund findRefundById(Long id) {
        return refundRepository.findById(id).orElseThrow(() -> new RefundNotFoundException(id));
    }

    public void updateRefundStatus(Long refundId, RefundStatus newStatus) {
        Refund refund = findRefundById(refundId);
        refund.setStatus(newStatus);
        refundRepository.save(refund);
    }
}
