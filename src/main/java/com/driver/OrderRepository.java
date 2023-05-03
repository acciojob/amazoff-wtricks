package com.driver;

import io.swagger.models.auth.In;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class OrderRepository {
    HashMap<String, Order> orderDB = new HashMap<>();
    HashMap<String, DeliveryPartner> partnerDB = new HashMap<>();
    HashMap<String, String> orderPartnerDB = new HashMap<>();
    HashMap<String, HashSet<Order>> partnerOrdersDB = new HashMap<>();
    public void addOrder(Order order) {
        String orderId = order.getId();
        orderDB.put(orderId, order);
    }

    public void addPartner(String partnerId) {
        partnerDB.put(partnerId, new DeliveryPartner(partnerId));
        partnerOrdersDB.put(partnerId, new HashSet<>());
    }

    public void addOrderPartnerPair(String orderId, String partnerId) {
        partnerDB.get(partnerId).setNumberOfOrders(partnerDB.get(partnerId).getNumberOfOrders() + 1);
        orderPartnerDB.put(orderId, partnerId);
        partnerOrdersDB.get(partnerId).add(orderDB.get(orderId));
    }

    public Order getOrderById(String orderId) {
        return orderDB.get(orderId);
    }

    public DeliveryPartner getPartnerById(String partnerId) {
        return partnerDB.get(partnerId);
    }

    public Integer getOrderCountByPartnerId(String partnerId) {
        //partnerDB.get(partnerId).getNumberOfOrders();
        return partnerOrdersDB.get(partnerId).size();
    }

    public List<String> getOrdersByPartnerId(String partnerId) {
        List<String> orders = new ArrayList<>();

        for(Order order : partnerOrdersDB.get(partnerId)) {
            orders.add(order.getId());
        }
        return orders;
    }

    public List<String> getAllOrders() {
        return new ArrayList<>(orderDB.keySet());
    }

    public Integer getCountOfUnassignedOrders() {
        return orderDB.size() - orderPartnerDB.size();
    }

    public Integer getOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId) {
        int hours = Integer.parseInt(time.substring(0, 3));
        int minutes = Integer.parseInt(time.substring(3));
        int givenTime = hours * 60 + minutes;
        int remainingOrders = 0;
        for(Order order : partnerOrdersDB.get(partnerId)) {
            if(order.getDeliveryTime() > givenTime) {
                remainingOrders++;
            }
        }
        return remainingOrders;
    }

    public String getLastDeliveryTimeByPartnerId(String partnerId) {
        int max = 0;

        for(Order order : partnerOrdersDB.get(partnerId)) {
            if(order.getDeliveryTime() > max) {
                max = order.getDeliveryTime();
            }
        }
        return Time.convertIntToString(max);
    }

    public void deletePartnerById(String partnerId) {
        partnerDB.remove(partnerId);
        partnerOrdersDB.remove(partnerId);
        for(Map.Entry<String, String> entry : orderPartnerDB.entrySet()) {
            if(entry.getValue().equals(partnerId)) orderPartnerDB.remove(entry.getKey());
        }
    }

    public void deleteOrderById(String orderId) {
        orderDB.remove(orderId);
        orderPartnerDB.remove(orderId);
        for(HashSet<Order> set : partnerOrdersDB.values()) {
            for(Order order : set){
                if(orderId.equals(order.getId())){
                    set.remove(order);
                }
            }
        }
    }
}
