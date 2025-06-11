package uk.gov.justice.laa.dstew.access;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// `feature.security=false` prevents Entra auto-configuration failing context initialization
// (temporary until figure out a better solution).
@SpringBootTest(properties = "feature.security=false")
class AccessApplicationTests {

  @Test
  void contextLoads() {
    // empty due to only testing context load
  }
}
