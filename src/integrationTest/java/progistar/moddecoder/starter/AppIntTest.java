package progistar.moddecoder.starter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.jupiter.api.Test;

class AppIntTest {
  private static final ByteArrayOutputStream stdoutContent = new ByteArrayOutputStream();
  private static final PrintStream originalStdout = System.out;

  @Test
  void main_ValidInput_ValidOutput() throws IOException {
    // given
    ClassLoader classLoader = ClassLoader.getSystemClassLoader();

    URL sam = classLoader.getResource("PDAC005-D.sort.dedup.realigned.recal.sam");
    URL bed = classLoader.getResource("SAAVs.bed");
    URL tsv = classLoader.getResource("PDAC_SAAV.tsv");
    URL expectedStdout = classLoader.getResource("expected_stdout.txt");

    assert sam != null;
    assert bed != null;
    assert tsv != null;
    assert expectedStdout != null;

    byte[] encoded = Files.readAllBytes(Paths.get(expectedStdout.getFile()));
    final String expected = new String(encoded, StandardCharsets.UTF_8);

    String[] args = {"/arbitrary/directory", sam.getFile(), bed.getFile(), tsv.getFile()};

    // when
    System.setOut(new PrintStream(stdoutContent));
    App.main(args);
    System.setOut(originalStdout);

    // then
    String actual = stdoutContent.toString();

    then(expected.length()).isEqualTo(1184816);
    then(actual.length()).isEqualTo(1184816);
    then(actual.equals(expected)).isTrue();
  }
}
