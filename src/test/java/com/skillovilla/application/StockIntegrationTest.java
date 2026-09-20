package com.skillovilla.application;

import com.skillovilla.application.dto.IssueCostDto;
import com.skillovilla.application.entity.Item;
import com.skillovilla.application.entity.StockMovement;
import com.skillovilla.application.entity.Warehouse;
import com.skillovilla.application.exception.ResourceNotFoundException;
import com.skillovilla.application.service.ItemService;
import com.skillovilla.application.service.StockMovementService;
import com.skillovilla.application.service.StockReportService;
import com.skillovilla.application.service.WarehouseService;
import com.skillovilla.application.utility.SecurityConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class StockIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private WarehouseService warehouseService;

    @Autowired
    private StockMovementService stockMovementService;

    @Autowired
    private StockReportService stockReportService;

    private Item testItem;
    private Warehouse testWarehouse;

    @BeforeEach
    public void setup() {
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 5);
        
        Item item = new Item();
        item.setCode("PEN-" + uniqueSuffix);
        item.setName("Blue Pen");
        item.setUnit("pieces");
        testItem = itemService.create(item);

        Warehouse warehouse = new Warehouse();
        warehouse.setCode("WH-" + uniqueSuffix);
        warehouse.setName("Main Warehouse");
        testWarehouse = warehouseService.create(warehouse);
    }

    @Test
    public void testFifoCostAndCancellation() {
        // 1 Mar: IN 100 units at Rs 10
        StockMovement in1 = createMovement(SecurityConstant.IN_MOVEMENT, 100, new BigDecimal("10.00"), LocalDateTime.now().minusDays(10));
        stockMovementService.create(in1);

        // 5 Mar: IN 100 units at Rs 12
        StockMovement in2 = createMovement(SecurityConstant.IN_MOVEMENT, 100, new BigDecimal("12.00"), LocalDateTime.now().minusDays(5));
        stockMovementService.create(in2);

        // 8 Mar: OUT 150 units
        StockMovement out = createMovement(SecurityConstant.OUT_MOVEMENT, 150, null, LocalDateTime.now().minusDays(2));
        StockMovement savedOut = stockMovementService.create(out);

        // 1. Check Issue Cost
        IssueCostDto costDto = stockReportService.getIssueCost(savedOut.getId());
        assertEquals(150, costDto.getTotalQuantity());
        // Cost should be (100 * 10) + (50 * 12) = 1000 + 600 = 1600
        assertEquals(0, new BigDecimal("1600.00").compareTo(costDto.getTotalCost()));

        // 2. Cancel the issue
        stockMovementService.cancelMovement(savedOut.getId(), "Test Cancellation", "Tester");

        // 3. Verify stock returned to original places (Current Stock should be 200)
        var currentStock = stockReportService.getCurrentStock(testItem.getId(), testWarehouse.getId());
        assertEquals(200, currentStock.getTotalQuantity());
        // Total value should be (100 * 10) + (100 * 12) = 2200
        assertEquals(0, new BigDecimal("2200.00").compareTo(currentStock.getTotalValue()));
    }

    @Test
    public void testHalfFailedDocumentRollback() {
        StockMovement validIn = createMovement(SecurityConstant.IN_MOVEMENT, 50, new BigDecimal("10.00"), LocalDateTime.now());
        // Trying to take 100 when only 50 is being added
        StockMovement invalidOut = createMovement(SecurityConstant.OUT_MOVEMENT, 100, null, LocalDateTime.now());

        assertThrows(Exception.class, () -> {
            stockMovementService.createInBulk(Arrays.asList(validIn, invalidOut));
        });

        // Verify NOTHING was saved (the 50 IN should have rolled back)
        var currentStock = stockReportService.getCurrentStock(testItem.getId(), testWarehouse.getId());
        assertEquals(0, currentStock.getTotalQuantity());
    }

    @Test
    public void testTwoPeopleGoingForLastUnit() throws InterruptedException {
        // Add exactly 1 unit to stock
        StockMovement in = createMovement(SecurityConstant.IN_MOVEMENT, 1, new BigDecimal("10.00"), LocalDateTime.now());
        stockMovementService.create(in);

        int numberOfThreads = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(numberOfThreads);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        Runnable task = () -> {
            try {
                latch.await(); // Wait until both threads are ready
                StockMovement out = createMovement(SecurityConstant.OUT_MOVEMENT, 1, null, LocalDateTime.now());
                stockMovementService.create(out);
                successCount.incrementAndGet();
            } catch (Exception e) {
                failCount.incrementAndGet();
            } finally {
                doneLatch.countDown();
            }
        };

        executorService.submit(task);
        executorService.submit(task);

        // Let them both run at the exact same millisecond
        latch.countDown();
        doneLatch.await();

        // Exactly one should succeed, exactly one should fail due to our Pessimistic Lock!
        assertEquals(1, successCount.get());
        assertEquals(1, failCount.get());
    }

    private StockMovement createMovement(String type, int quantity, BigDecimal price, LocalDateTime date) {
        return StockMovement.builder()
                .item(testItem)
                .warehouse(testWarehouse)
                .movementType(type)
                .quantity(quantity)
                .unitPrice(price)
                .movementDate(date)
                .recordedBy("TestRunner")
                .build();
    }
}
