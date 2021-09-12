package gr.gavra.rating.utils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

class DateUtilTest {

  private DateUtil dateUtil;

  @Test
  void now_WhenInvoked_CorrectTimeIsReturned() {
    dateUtil = new DateUtil();
    LocalDateTime now = dateUtil.now();
    LocalDateTime date = LocalDateTime.now();
    Assert.isTrue(ChronoUnit.SECONDS.between(now, date) < 1, "Times should be similar");
  }

}