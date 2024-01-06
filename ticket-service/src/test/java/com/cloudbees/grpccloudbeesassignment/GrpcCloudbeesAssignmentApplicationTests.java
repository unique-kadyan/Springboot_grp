//package com.cloudbees.grpccloudbeesassignment;
//
//import com.cloudbees.grpcserver.*;
//import io.grpc.ManagedChannel;
//import io.grpc.ManagedChannelBuilder;
//import io.grpc.StatusRuntimeException;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//@SpringBootTest(classes = SpringBootGrpcServer.class)
//public class GrpcCloudbeesAssignmentApplicationTests {
//
//	private static final int PORT = 8080;
//	private static final String HOST = "localhost";
//
//	private static ManagedChannel channel;
//	private static TrainTicketServiceGrpc.TrainTicketServiceBlockingStub blockingStub;
//
//	@BeforeAll
//	static void setup() {
//		// Create a channel and a blocking stub for testing
//		channel = ManagedChannelBuilder.forAddress(HOST, PORT)
//				.usePlaintext()
//				.build();
//
//		blockingStub = TrainTicketServiceGrpc.newBlockingStub(channel);
//	}
//
//	@AfterAll
//	static void tearDown() {
//		// Shutdown the channel after tests
//		channel.shutdown();
//	}
//
//	@Test
//	void testSubmitPurchase() {
//		// Create a PurchaseRequest for testing
//		PurchaseRequest request = PurchaseRequest.newBuilder()
//				.setFrom("London")
//				.setTo("France")
//				.setUser(User.newBuilder()
//						.setFirstName("John")
//						.setLastName("Doe")
//						.setEmail("john.doe@example.com")
//						.build())
//				.setPricePaid(20.0F)
//				.build();
//
//		// Test the SubmitPurchase method
//		PurchaseResponse response = blockingStub.submitPurchase(request);
//		assertNotNull(response);
//		assertNotNull(response.getReceiptDetails());
//	}
//
//	@Test
//	void testViewReceipt() {
//		// Create a ViewReceiptRequest for testing
//		ViewReceiptRequest request = ViewReceiptRequest.newBuilder()
//				.setEmail("john.doe@example.com")
//				.build();
//
//		// Test the ViewReceipt method
//		ViewReceiptResponse response = blockingStub.viewReceipt(request);
//		assertNotNull(response);
//		assertNotNull(response.getReceiptDetails());
//	}
//
//	@Test
//	void testViewReceiptNotFound() {
//		// Create a ViewReceiptRequest for testing a non-existing user
//		ViewReceiptRequest request = ViewReceiptRequest.newBuilder()
//				.setEmail("nonexistent.user@example.com")
//				.build();
//
//		// Test the ViewReceipt method for a user that does not exist
//		assertThrows(StatusRuntimeException.class, () -> blockingStub.viewReceipt(request));
//	}
//
//}
