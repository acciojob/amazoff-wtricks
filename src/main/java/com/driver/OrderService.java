package com.driver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    OrderRepository orderRepository;

    public void addOrder(Order order) {
        orderRepository.addOrder(order);
    }

    public void addPartner(String partnerId) {
        DeliveryPartner partner = new DeliveryPartner(partnerId);
        orderRepository.addPartner(partner);
    }

    public void addOrderPartnerPair(String orderId, String partnerId) {
        Optional<Order> orderOpt = orderRepository.getOrderById(orderId);
        Optional<DeliveryPartner> partnerOpt = orderRepository.getPartnerById(partnerId);
        // assuming this is not for update
        if(orderOpt.isPresent() && partnerOpt.isPresent()) {
            DeliveryPartner p = partnerOpt.get();
            Integer initialOrders = p.getNumberOfOrders();
            initialOrders++;
            p.setNumberOfOrders(initialOrders);
            orderRepository.addPartner(p);
            orderRepository.addOrderPartnerPair(orderId, partnerId);
        }
    }

    public Order getOrderById(String orderId) throws RuntimeException{
        Optional<Order> orderOpt = orderRepository.getOrderById(orderId);
        if(orderOpt.isPresent()) {
            return orderOpt.get();
        }
        throw new RuntimeException("Order Not Found");
    }

    public Integer getOrderCountForPartner(String partnerId) {
        Optional<DeliveryPartner> p = orderRepository.getPartnerById(partnerId);
        if(p.isPresent()) {
            return p.get().getNumberOfOrders();
        }
        return 0;
    }


    public List<String> getOrdersByPartnerId(String partnerId) {
//        Map<String, String> orderPartnerMap = orderRepository.getAllOrderPartnerMappings();
//        List<String> orderIds = new ArrayList<>();
//        for(var entry: orderPartnerMap.entrySet()) {
//            if(entry.getValue().equals(partnerId)) {
//                orderIds.add(entry.getKey());
//            }
//        }
//        return orderIds;
        return orderRepository.getAllOrderForPartner(partnerId);
    }

    public List<String> getAllOrders() {
        return orderRepository.getAllOrders();
    }

    public Integer getUnassignedOrders() {
        return orderRepository.getAllOrders().size() - orderRepository.getAssignedOrders().size();
    }

    public Integer getOrdersLeftForPartnerAfterTime(String time, String partnerId) {
        List<String> orderIds = orderRepository.getAllOrderForPartner(partnerId);
        int currTime = TimeUtils.convertTime(time);
        int ordersLeft = 0;
        for(String orderId: orderIds) {
            int deliveryTime = orderRepository.getOrderById(orderId).get().getDeliveryTime();
            if(currTime < deliveryTime) {
                ordersLeft++;
            }
        }
        return ordersLeft;
    }

    public String getLastDeliveryTimeForPartner(String partnerId) {
        List<String> orderIds = orderRepository.getAllOrderForPartner(partnerId);
        int max = 0;
        for(String orderId: orderIds) {
            int deliveryTime = orderRepository.getOrderById(orderId).get().getDeliveryTime();
            if(deliveryTime>max) {
                max = deliveryTime;
            }
        }
        return TimeUtils.convertTime(max);
    }

    public void deletePartner(String partnerId) {
        List<String> orders = orderRepository.getAllOrderForPartner(partnerId);
        orderRepository.deletePartner(partnerId);
        for(String orderId:orders) {
            orderRepository.removeOrderPartnerMapping(orderId);
        }
    }

    public void deleteOrder(String orderId) {
        String partnerId = orderRepository.getPartnerForOrder(orderId);
        orderRepository.deleteOrder(orderId);
        if(Objects.nonNull(partnerId)) {
            DeliveryPartner p = orderRepository.getPartnerById(partnerId).get();
            Integer initialOrders = p.getNumberOfOrders();
            initialOrders--;
            p.setNumberOfOrders(initialOrders);
            orderRepository.addPartner(p);
            orderRepository.removeOrderForPartner(partnerId, orderId);
        }
    }
}
