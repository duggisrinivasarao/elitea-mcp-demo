package com.edwardjones.portfolio.service;

import com.edwardjones.portfolio.dto.AssetAllocationResponse;
import com.edwardjones.portfolio.model.AssetAllocation;
import com.edwardjones.portfolio.model.Holding;
import com.edwardjones.portfolio.repository.AssetAllocationRepository;
import com.edwardjones.portfolio.repository.HoldingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AssetAllocationService.
 * Story Reference: MAP-15 — Asset Allocation Breakdown
 */
class AssetAllocationServiceTest {

    @Mock
    private HoldingRepository holdingRepository;

    @Mock
    private AssetAllocationRepository allocationRepository;

    @InjectMocks
    private AssetAllocationService service;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    // AC: Pie/donut chart percentage splits are calculated across asset classes
    @Test
    void getAllocation_shouldCalculatePercentagePerAssetType() {
        Holding stock = makeHolding("AAPL", Holding.AssetType.STOCK, new BigDecimal("60000"));
        Holding bond  = makeHolding("BND",  Holding.AssetType.BOND,  new BigDecimal("40000"));

        when(holdingRepository.findByClientId("C1")).thenReturn(List.of(stock, bond));
        when(allocationRepository.findByClientId("C1")).thenReturn(Optional.empty());

        AssetAllocationResponse response = service.getAllocation("C1");

        AssetAllocationResponse.AllocationSegment stockSeg = segmentFor(response, "STOCK");
        AssetAllocationResponse.AllocationSegment bondSeg  = segmentFor(response, "BOND");

        assertThat(stockSeg.getActualPercent()).isEqualByComparingTo(new BigDecimal("60.00"));
        assertThat(bondSeg.getActualPercent()).isEqualByComparingTo(new BigDecimal("40.00"));
    }

    // AC: Specific holdings within each asset class are listed
    @Test
    void getAllocation_shouldListHoldingsWithinEachSegment() {
        Holding s1 = makeHolding("AAPL", Holding.AssetType.STOCK, new BigDecimal("30000"));
        Holding s2 = makeHolding("MSFT", Holding.AssetType.STOCK, new BigDecimal("20000"));

        when(holdingRepository.findByClientId("C2")).thenReturn(List.of(s1, s2));
        when(allocationRepository.findByClientId("C2")).thenReturn(Optional.empty());

        AssetAllocationResponse response = service.getAllocation("C2");

        AssetAllocationResponse.AllocationSegment stockSeg = segmentFor(response, "STOCK");
        assertThat(stockSeg.getHoldings()).extracting("ticker").containsExactlyInAnyOrder("AAPL", "MSFT");
    }

    // AC: Actual vs. target allocation shown side by side when target is set
    @Test
    void getAllocation_shouldIncludeTargetPercentWhenAllocationIsSet() {
        Holding stock = makeHolding("AAPL", Holding.AssetType.STOCK, new BigDecimal("70000"));
        Holding bond  = makeHolding("BND",  Holding.AssetType.BOND,  new BigDecimal("30000"));

        AssetAllocation target = new AssetAllocation();
        target.setTargetStockPercent(new BigDecimal("60.00"));
        target.setTargetBondPercent(new BigDecimal("40.00"));

        when(holdingRepository.findByClientId("C3")).thenReturn(List.of(stock, bond));
        when(allocationRepository.findByClientId("C3")).thenReturn(Optional.of(target));

        AssetAllocationResponse response = service.getAllocation("C3");

        assertThat(response.isTargetAllocationSet()).isTrue();
        assertThat(segmentFor(response, "STOCK").getTargetPercent())
                .isEqualByComparingTo(new BigDecimal("60.00"));
        assertThat(segmentFor(response, "BOND").getTargetPercent())
                .isEqualByComparingTo(new BigDecimal("40.00"));
    }

    // AC: No target set → targetAllocationSet is false
    @Test
    void getAllocation_shouldMarkTargetNotSetWhenMissing() {
        when(holdingRepository.findByClientId("C4")).thenReturn(List.of());
        when(allocationRepository.findByClientId("C4")).thenReturn(Optional.empty());

        AssetAllocationResponse response = service.getAllocation("C4");

        assertThat(response.isTargetAllocationSet()).isFalse();
    }

    // Helpers
    private Holding makeHolding(String ticker, Holding.AssetType type, BigDecimal value) {
        Holding h = new Holding();
        h.setClientId("CX");
        h.setTicker(ticker);
        h.setName(ticker);
        h.setAssetType(type);
        h.setCurrentValue(value);
        h.setQuantity(BigDecimal.ONE);
        h.setCurrentPrice(value);
        return h;
    }

    private AssetAllocationResponse.AllocationSegment segmentFor(
            AssetAllocationResponse response, String type) {
        return response.getSegments().stream()
                .filter(s -> s.getAssetType().equals(type))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Segment not found: " + type));
    }
}
