package com.edwardjones.portfolio.service;

import com.edwardjones.portfolio.dto.AssetAllocationResponse;
import com.edwardjones.portfolio.model.AssetAllocation;
import com.edwardjones.portfolio.model.Holding;
import com.edwardjones.portfolio.repository.AssetAllocationRepository;
import com.edwardjones.portfolio.repository.HoldingRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for computing asset allocation breakdowns with actual vs. target comparison.
 * Story Reference: MAP-15 — Asset Allocation Breakdown
 */
@Service
public class AssetAllocationService {

    private final HoldingRepository holdingRepository;
    private final AssetAllocationRepository allocationRepository;

    public AssetAllocationService(HoldingRepository holdingRepository,
                                  AssetAllocationRepository allocationRepository) {
        this.holdingRepository = holdingRepository;
        this.allocationRepository = allocationRepository;
    }

    /**
     * Computes the asset allocation breakdown for a client.
     * AC: Pie/donut chart percentage splits across asset classes are calculated.
     * AC: Specific holdings within each asset class are listed.
     * AC: Actual vs. target allocation is shown side by side if target is set.
     *
     * @param clientId the client's identifier
     * @return AssetAllocationResponse with segments and holdings
     */
    public AssetAllocationResponse getAllocation(String clientId) {
        if (clientId == null || clientId.isBlank()) {
            throw new IllegalArgumentException("Client ID must not be blank");
        }

        List<Holding> holdings = holdingRepository.findByClientId(clientId);
        Optional<AssetAllocation> targetOpt = allocationRepository.findByClientId(clientId);

        BigDecimal totalValue = holdings.stream()
                .map(Holding::getCurrentValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<Holding.AssetType, List<Holding>> byType = holdings.stream()
                .collect(Collectors.groupingBy(Holding::getAssetType));

        List<AssetAllocationResponse.AllocationSegment> segments = new ArrayList<>();
        for (Holding.AssetType type : Holding.AssetType.values()) {
            List<Holding> group = byType.getOrDefault(type, Collections.emptyList());
            BigDecimal typeValue = group.stream()
                    .map(Holding::getCurrentValue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal actualPercent = totalValue.compareTo(BigDecimal.ZERO) > 0
                    ? typeValue.divide(totalValue, 6, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                    : BigDecimal.ZERO;

            AssetAllocationResponse.AllocationSegment seg = new AssetAllocationResponse.AllocationSegment();
            seg.setAssetType(type.name());
            seg.setActualValue(typeValue);
            seg.setActualPercent(actualPercent.setScale(2, RoundingMode.HALF_UP));
            seg.setTargetPercent(targetOpt.map(t -> resolveTarget(t, type)).orElse(null));
            seg.setHoldings(group.stream().map(h -> toHoldingSummary(h, totalValue)).collect(Collectors.toList()));
            segments.add(seg);
        }

        AssetAllocationResponse response = new AssetAllocationResponse();
        response.setClientId(clientId);
        response.setSegments(segments);
        response.setTargetAllocationSet(targetOpt.isPresent());
        return response;
    }

    private BigDecimal resolveTarget(AssetAllocation allocation, Holding.AssetType type) {
        return switch (type) {
            case STOCK -> allocation.getTargetStockPercent();
            case BOND -> allocation.getTargetBondPercent();
            case MUTUAL_FUND -> allocation.getTargetMutualFundPercent();
            case ETF -> allocation.getTargetEtfPercent();
            case CASH -> allocation.getTargetCashPercent();
        };
    }

    private AssetAllocationResponse.AllocationSegment.HoldingSummary toHoldingSummary(
            Holding h, BigDecimal totalValue) {
        AssetAllocationResponse.AllocationSegment.HoldingSummary summary =
                new AssetAllocationResponse.AllocationSegment.HoldingSummary();
        summary.setTicker(h.getTicker());
        summary.setName(h.getName());
        summary.setValue(h.getCurrentValue());
        BigDecimal pct = totalValue.compareTo(BigDecimal.ZERO) > 0
                ? h.getCurrentValue().divide(totalValue, 6, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;
        summary.setPercent(pct.setScale(2, RoundingMode.HALF_UP));
        return summary;
    }
}
