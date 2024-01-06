package com.cloudbees.grpcserver.service;

import com.cloudbees.grpcserver.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@GrpcService
public class TicketServiceImpl extends TrainTicketServiceGrpc.TrainTicketServiceImplBase {
    private Map<String, UserSeatInfo> userSeatMap = new HashMap<>();

    @Override
    public void submitPurchase(PurchaseRequest request, StreamObserver<PurchaseResponse> responseObserver) {
        // Assuming simple logic for seat allocation
        String section = getRandomSection();
        int seatNumber = getRandomSeatNumber(section);

        // Create UserSeatInfo
        UserSeatInfo userSeatInfo = UserSeatInfo.newBuilder()
                .setUser(request.getUser())
                .setSection(section)
                .setSeatNumber(seatNumber)
                .build();

        // Store user and seat information
        userSeatMap.put(request.getUser().getEmail(), userSeatInfo);

        // Generate receipt details
        String receiptDetails = generateReceiptDetails(request, userSeatInfo);

        // Send response
        PurchaseResponse response = PurchaseResponse.newBuilder()
                .setReceiptDetails(receiptDetails)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void viewReceipt(ViewReceiptRequest request, StreamObserver<ViewReceiptResponse> responseObserver) {
        // Retrieve user seat info based on email
        UserSeatInfo userSeatInfo = userSeatMap.get(request.getEmail());

        if (userSeatInfo != null) {
            // Generate receipt details
            String receiptDetails = generateReceiptDetails(userSeatInfo);

            // Send response
            ViewReceiptResponse response = ViewReceiptResponse.newBuilder()
                    .setReceiptDetails(receiptDetails)
                    .build();
            responseObserver.onNext(response);
        } else {
            // User not found
            responseObserver.onError(Status.NOT_FOUND.withDescription("User not found").asRuntimeException());
        }

        responseObserver.onCompleted();
    }

    @Override
    public void viewUsersBySection(ViewUsersBySectionRequest request, StreamObserver<ViewUsersBySectionResponse> responseObserver) {
        String requestedSection = request.getSection();
        List<UserSeatInfo> usersInSection = new ArrayList<>();

        // Collect users in the requested section
        for (UserSeatInfo userSeatInfo : userSeatMap.values()) {
            if (userSeatInfo.getSection().equals(requestedSection)) {
                usersInSection.add(userSeatInfo);
            }
        }

        // Send response
        ViewUsersBySectionResponse response = ViewUsersBySectionResponse.newBuilder()
                .addAllUserSeatInfo(usersInSection)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void removeUser(RemoveUserRequest request, StreamObserver<RemoveUserResponse> responseObserver) {
        String email = request.getEmail();
        UserSeatInfo removedUser = userSeatMap.remove(email);

        if (removedUser != null) {
            // Send success response
            RemoveUserResponse response = RemoveUserResponse.newBuilder().build();
            responseObserver.onNext(response);
        } else {
            // User not found
            responseObserver.onError(Status.NOT_FOUND.withDescription("User not found").asRuntimeException());
        }

        responseObserver.onCompleted();
    }

    @Override
    public void modifyUserSeat(ModifyUserSeatRequest request, StreamObserver<ModifyUserSeatResponse> responseObserver) {
        String email = request.getEmail();
        UserSeatInfo userSeatInfo = userSeatMap.get(email);

        if (userSeatInfo != null) {
            // Update user seat information
            userSeatInfo = userSeatInfo.toBuilder()
                    .setSection(request.getNewSection())
                    .setSeatNumber(request.getNewSeatNumber())
                    .build();

            // Store updated user seat information
            userSeatMap.put(email, userSeatInfo);

            // Send success response
            ModifyUserSeatResponse response = ModifyUserSeatResponse.newBuilder().build();
            responseObserver.onNext(response);
        } else {
            // User not found
            responseObserver.onError(Status.NOT_FOUND.withDescription("User not found").asRuntimeException());
        }

        responseObserver.onCompleted();
    }

    // Helper methods

    private String getRandomSection() {
        // Simple logic to randomly choose a section
        return Math.random() < 0.5 ? "Section A" : "Section B";
    }

    private int getRandomSeatNumber(String section) {
        // Simple logic to randomly choose a seat number based on the section
        return section.equals("Section A") ? ThreadLocalRandom.current().nextInt(1, 50 + 1) : ThreadLocalRandom.current().nextInt(51, 100 + 1);
    }

    private String generateReceiptDetails(PurchaseRequest request, UserSeatInfo userSeatInfo) {
        return String.format("Receipt Details:\nFrom: %s\nTo: %s\nUser: %s %s\nEmail: %s\nSection: %s\nSeat Number: %d\nPrice Paid: $%.2f",
                request.getFrom(), request.getTo(), request.getUser().getFirstName(), request.getUser().getLastName(),
                request.getUser().getEmail(), userSeatInfo.getSection(), userSeatInfo.getSeatNumber(), request.getPricePaid());
    }

    private String generateReceiptDetails(UserSeatInfo userSeatInfo) {
        return String.format("Receipt Details:\nUser: %s %s\nEmail: %s\nSection: %s\nSeat Number: %d",
                userSeatInfo.getUser().getFirstName(), userSeatInfo.getUser().getLastName(),
                userSeatInfo.getUser().getEmail(), userSeatInfo.getSection(), userSeatInfo.getSeatNumber());
    }
    @Override
    public void addUser(AddUserRequest request, StreamObserver<AddUserResponse> responseObserver) {
        // Implement your logic for adding a user
        UserSeatInfo newUserSeatInfo = UserSeatInfo.newBuilder()
                .setUser(request.getUser())
                .setSection("A") // You can set the section based on your logic
                .setSeatNumber(1) // You can set the seat number based on your logic
                .build();

        userSeatMap.put(request.getUser().getFirstName()+" "+request.getUser().getLastName(),newUserSeatInfo);

        AddUserResponse response = AddUserResponse.newBuilder()
                .setSuccess(true)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
