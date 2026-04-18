package com.edwardjones.portfolio.service;

import com.edwardjones.portfolio.dto.PortfolioAlertRequest;
import com.edwardjones.portfolio.model.PortfolioAlert;
import com.edwardjones.portfolio.repository.PortfolioAlertRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PortfolioAlertService.
 * Story Reference: MAP-17 — Portfolio Alert Notifications
 */
class PortfolioAlertServiceTest {

    @Mock
    private PortfolioAlertRepository alertRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private PortfolioAlertService service;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    // AC: Client configures threshold → alert is saved with correct channel
    @Test
    void configureAlert_shouldSaveAlertWithCorrectThresholdAndChannel() {
        PortfolioAlertRequest req = new PortfolioAlertRequest();
        req.setThresholdPercent(new BigDecimal("5.0"));
        req.setChannel("EMAIL");

        PortfolioAlert saved = new PortfolioAlert();
        saved.setId(1L);
        saved.setThresholdPercent(new BigDecimal("5.0"));
        saved.setChannel(PortfolioAlert.NotificationChannel.EMAIL);
        saved.setEnabled(true);

        when(alertRepository.save(any())).thenReturn(saved);

        PortfolioAlert result = service.configureAlert("CLIENT1", req);

        assertThat(result.getThresholdPercent()).isEqualByComparingTo(new BigDecimal("5.0"));
        assertThat(result.getChannel()).isEqualTo(PortfolioAlert.NotificationChannel.EMAIL);
        assertThat(result.isEnabled()).isTrue();
    }

    // AC: When portfolio value changes by threshold %, notification is sent
    @Test
    void evaluateAndNotify_shouldSendNotificationWhenThresholdBreached() {
        PortfolioAlert alert = new PortfolioAlert();
        alert.setClientId("CLIENT1");
        alert.setThresholdPercent(new BigDecimal("5.0"));
        alert.setChannel(PortfolioAlert.NotificationChannel.EMAIL);
        alert.setEnabled(true);

        when(alertRepository.findByClientIdAndEnabledTrue("CLIENT1")).thenReturn(List.of(alert));
        when(alertRepository.save(any())).thenReturn(alert);

        service.evaluateAndNotify("CLIENT1", new BigDecimal("6.5")); // 6.5% > 5.0% threshold

        verify(notificationService, times(1))
                .sendPortfolioAlert(eq("CLIENT1"), eq(PortfolioAlert.NotificationChannel.EMAIL), any());
    }

    // AC: Notification NOT sent when change is below threshold
    @Test
    void evaluateAndNotify_shouldNotSendNotificationBelowThreshold() {
        PortfolioAlert alert = new PortfolioAlert();
        alert.setThresholdPercent(new BigDecimal("5.0"));
        alert.setChannel(PortfolioAlert.NotificationChannel.PUSH);
        alert.setEnabled(true);

        when(alertRepository.findByClientIdAndEnabledTrue("CLIENT1")).thenReturn(List.of(alert));

        service.evaluateAndNotify("CLIENT1", new BigDecimal("3.0")); // 3% < 5% threshold

        verify(notificationService, never()).sendPortfolioAlert(any(), any(), any());
    }

    // AC: Client disables alerts → no further alerts sent
    @Test
    void disableAlerts_shouldDisableAllClientAlerts() {
        PortfolioAlert a1 = new PortfolioAlert(); a1.setEnabled(true);
        PortfolioAlert a2 = new PortfolioAlert(); a2.setEnabled(true);

        when(alertRepository.findByClientId("CLIENT1")).thenReturn(List.of(a1, a2));

        service.disableAlerts("CLIENT1");

        assertThat(a1.isEnabled()).isFalse();
        assertThat(a2.isEnabled()).isFalse();
        verify(alertRepository).saveAll(List.of(a1, a2));
    }

    // AC: Client re-enables alerts → alerts are active again
    @Test
    void enableAlerts_shouldEnableAllClientAlerts() {
        PortfolioAlert a1 = new PortfolioAlert(); a1.setEnabled(false);

        when(alertRepository.findByClientId("CLIENT1")).thenReturn(List.of(a1));

        service.enableAlerts("CLIENT1");

        assertThat(a1.isEnabled()).isTrue();
    }

    // AC: Blank clientId throws on configureAlert
    @Test
    void configureAlert_shouldThrowForBlankClientId() {
        PortfolioAlertRequest req = new PortfolioAlertRequest();
        req.setThresholdPercent(new BigDecimal("5.0"));
        req.setChannel("EMAIL");

        assertThatThrownBy(() -> service.configureAlert("", req))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
