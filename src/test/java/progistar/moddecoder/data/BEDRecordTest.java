package progistar.moddecoder.data;

import java.net.URL;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.jupiter.api.Test;

class BEDRecordTest {

  @Test
  void construct_ValidInput_ValidOutput() {
    BEDRecord bedRecord = new BEDRecord();

    then(bedRecord).isNotNull();
  }
}
