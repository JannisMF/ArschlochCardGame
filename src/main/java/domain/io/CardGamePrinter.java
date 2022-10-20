package domain.io;

import lombok.extern.slf4j.Slf4j;

import java.io.PrintWriter;
import java.io.Writer;

@Slf4j
public class CardGamePrinter extends PrintWriter {

    public CardGamePrinter(Writer out) {
        super(out);
    }


    @Override
    public void println(String x) {
        log.debug("Es wurde geschrieben: " + x);
        super.println(x);
        super.flush();
    }
}
