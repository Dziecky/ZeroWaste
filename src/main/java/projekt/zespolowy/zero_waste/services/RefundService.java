package projekt.zespolowy.zero_waste.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import projekt.zespolowy.zero_waste.entity.Refund;
import projekt.zespolowy.zero_waste.entity.User;
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
}
