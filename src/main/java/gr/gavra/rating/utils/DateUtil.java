package gr.gavra.rating.utils;

import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class DateUtil {

  public LocalDateTime now() {
    return LocalDateTime.now();
  }
}
